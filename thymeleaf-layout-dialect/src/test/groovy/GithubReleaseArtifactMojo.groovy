import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes
import java.nio.file.spi.FileSystemProvider
import java.util.concurrent.atomic.AtomicLong
import java.util.stream.Stream

static def copy(Path src, Path dest) {
	def parent = dest.parent
	parent && parent.nameCount && Files.createDirectories(parent)
	Files.copy src, dest, StandardCopyOption.COPY_ATTRIBUTES, StandardCopyOption.REPLACE_EXISTING
}

static def deepCopy(Path src, Path dest) {
	Files.walkFileTree(src, new SimpleFileVisitor<Path>() {

		def transform(Path path) {
			dest.resolve src.relativize(path).join('/')
		}

		@Override
		FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
			copy file, transform(file)
			return FileVisitResult.CONTINUE
		}
	})
	dest
}

static def copy(
	Path dest, Closure<? extends Stream<? extends Path>> closure,
	Closure callback) {
	def copied = new AtomicLong()
	closure().forEach {
		copy it, dest.resolve(it.fileName.toString())
		++copied
	}
	callback(copied)
}

static <T> T onlyElement(Iterable<T> iterable, String msg = null) {
	def it = iterable.iterator()
	if (!it.hasNext()) throw new NoSuchElementException(msg)
	def element = it.next()
	if (it.hasNext()) {
		def sb = new StringBuilder('expected one element but was: <').append(element)
		for (def cnt = 0; ;) {
			sb.append(',' as char).append(it.next())
			if (!it.hasNext()) break
			if (++cnt >= 4) {
				sb.append(", ...")
				break
			}
		}
		sb.append('>' as char)
		throw new IllegalStateException("$sb")
	}
	element
}

static def extract(FileSystemProvider zipFsp, Path zip, Path dest) {
	zipFsp.newFileSystem(zip, Collections.emptyMap()).withCloseable {
		deepCopy onlyElement(it.rootDirectories), dest
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
	def attachedArtifacts = project.attachedArtifacts

	def notAssembled =
		'The project artifact has not been assembled yet. ' +
			'Please do not invoke this goal before the lifecycle phase "package".'

	if (!artifactFile) throw new NoSuchElementException(notAssembled)

	File sources = onlyElement attachedArtifacts.grep { it.type == 'java-source' }.collect { it.file }, notAssembled
	File javadoc = onlyElement attachedArtifacts.grep { it.type == 'javadoc' }.collect { it.file }, notAssembled

	def zipFsp = onlyElement FileSystemProvider.installedProviders().grep { it.scheme == 'jar' }, "Jar FileSystem not available"

	def copyContent = { Path base ->
		copy artifactFile.toPath(), base.resolve(artifactFile.name)
		extract zipFsp, sources.toPath(), base.resolve('source')
		extract zipFsp, javadoc.toPath(), base.resolve('javadoc')

		copy(base.resolve('libraries')) {
			project.resolvedArtifacts.stream().filter {
				!it.optional && it.type == 'jar' && it.scope in ['compile', 'runtime']
			}.map { it.file.toPath() }
		} {
			log.info "Archived $it libraries."
		}

		copy(base) {
			Files.list(root.toPath()).filter { it.fileName ==~ /(?i)^(CHANGELOG|LICENSE|README).*/ }
		} {
			log.info "Archived $it files from $root."
		}
	}

	zipFsp.newFileSystem(
		Paths.get(directory, "${finalName}.zip"),
		Collections.singletonMap('create', 'true')
	).withCloseable {
		copyContent onlyElement(it.rootDirectories).resolve(finalName)
	}
} catch (Throwable t) {
	log.error t
	throw t
}
