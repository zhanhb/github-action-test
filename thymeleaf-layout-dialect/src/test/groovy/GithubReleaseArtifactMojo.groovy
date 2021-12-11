import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes
import java.nio.file.spi.FileSystemProvider
import java.util.concurrent.atomic.AtomicLong
import java.util.stream.Stream

static def copy(Path src, Path dest) {
	def parent = dest.parent
	parent && Files.createDirectories(parent)
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
	Closure<? super AtomicLong> callback) {
	def copied = new AtomicLong()
	closure().forEach {
		copy it, dest.resolve(it.fileName.toString())
		++copied
	}
	callback(copied)
}

def
	log = log,
	project = project,
	session = session

try {
	String sourceDirectory = project.build.sourceDirectory
	String directory = project.build.directory
	String finalName = project.build.finalName
	File root = session.request.multiModuleProjectDirectory

	def copyContent = { Path base ->
		def distName = "$finalName.$project.packaging"

		copy Paths.get(directory, distName), base.resolve(distName)
		deepCopy Paths.get(sourceDirectory), base.resolve('source')
		deepCopy Paths.get(directory, 'site/apidocs'), base.resolve('javadoc')

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

	FileSystemProvider.installedProviders().grep { it.scheme == 'jar' }[0].newFileSystem(
		Paths.get(directory, "${finalName}.zip"),
		Collections.singletonMap('create', 'true')
	).withCloseable {
		copyContent it.rootDirectories[0].resolve(finalName)
	}
} catch (Throwable t) {
	log.error t
	throw t
}
