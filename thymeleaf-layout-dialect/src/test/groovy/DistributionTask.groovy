import groovy.transform.CompileStatic
import groovy.transform.stc.ClosureParams
import groovy.transform.stc.SimpleType

import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes
import java.nio.file.spi.FileSystemProvider
import java.util.function.Predicate

@CompileStatic
static Path copy(Path src, Path dest) {
	def parent = dest.parent
	parent && parent.nameCount && Files.createDirectories(parent)
	Files.copy src, dest, StandardCopyOption.COPY_ATTRIBUTES, StandardCopyOption.REPLACE_EXISTING
}

@CompileStatic
static Path deepCopy(Path src, Path dest) {
	Files.walkFileTree(src, new SimpleFileVisitor<Path>() {

		private Path transform(Path path) {
			dest.resolve src.relativize(path).join('/')
		}

		@Override
		FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
			copy file, transform(file)
			FileVisitResult.CONTINUE
		}
	})
	dest
}

@CompileStatic
static Path[] copyAll(Path[] sources, Path dest) {
	sources.each { copy it, dest.resolve(it.fileName.toString()) }
}

@CompileStatic
static <T> T onlyElement(Iterable<T> iterable, String msg = null) {
	def it = iterable.iterator()
	if (!it.hasNext()) throw new NoSuchElementException(msg)
	def element = it.next()
	if (!it.hasNext()) return element
	def sb = new StringBuilder('expected one element but was: <').append element
	for (def cnt = 0; ;) {
		sb.append(',').append it.next()
		if (!it.hasNext()) break
		if (++cnt >= 4) {
			sb.append ', ...'
			break
		}
	}
	throw new IllegalStateException(sb.append('>').toString())
}

@CompileStatic
static Path extract(FileSystemProvider zipFsp, Path zip, Path dest) {
	zipFsp.newFileSystem(zip, Collections.emptyMap()).withCloseable {
		deepCopy onlyElement(it.rootDirectories), dest
	}
}

@CompileStatic
static def doArchive(
	Closure info, String directory, String finalName,
	File root, File artifactFile, Path[] dependencies,
	@ClosureParams(value = SimpleType, options = 'String') Closure<File> findArtifact) {

	def source = findArtifact 'java-source'
	def javadoc = findArtifact 'javadoc'

	def zipFsp = onlyElement FileSystemProvider.installedProviders()
		.grep { FileSystemProvider it -> it.scheme == 'jar' },
		'Jar FileSystem not available'

	def copyContent = { Path base ->
		copy artifactFile.toPath(), base.resolve(artifactFile.name)
		extract zipFsp, source.toPath(), base.resolve('source')
		extract zipFsp, javadoc.toPath(), base.resolve('javadoc')

		copyAll dependencies, base.resolve('libraries')
		info "Archived $dependencies.length dependencies."

		Predicate<Path> filter = { Path it -> it.fileName ==~ /(?i)^(CHANGELOG|LICENSE|README).*/ }
		def extras = copyAll Files.list(root.toPath()).filter(filter).toArray { new Path[it] }, base
		info "Archived $extras.length files from $root."
	}

	zipFsp.newFileSystem(
		Paths.get(directory, "${finalName}.zip"),
		Collections.singletonMap('create', 'true')
	).withCloseable {
		copyContent onlyElement(it.rootDirectories).resolve(finalName)
	}
}

def
	log = log,
	project = project,
	session = session

try {
	String directory = project.build.directory
	String finalName = project.build.finalName
	File root = session.request.multiModuleProjectDirectory
	File artifactFile = project.artifact.file
	def artifacts = project.artifacts
	def attachedArtifacts = project.attachedArtifacts

	def msg =
		'The project artifact has not been assembled yet. ' +
			'Please do not invoke this goal before the lifecycle phase "package".'

	if (!artifactFile) throw new NoSuchElementException(msg)

	def info = { log.info it }
	def dependencies = artifacts
		.grep { !it.optional && it.type == 'jar' && it.scope in ['compile', 'runtime'] }
		.collect { it.file.toPath() } as Path[]
	def findArtifact = { type ->
		onlyElement(attachedArtifacts.grep { it.type == type }.collect { it.file }, msg) as File
	}

	doArchive info, directory, finalName, root, artifactFile, dependencies, findArtifact
} catch (Throwable t) {
	log.error t
	throw t
}
