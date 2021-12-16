import groovy.transform.CompileStatic
import net.bytebuddy.jar.asm.ClassWriter

import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes

import static net.bytebuddy.jar.asm.Opcodes.*

@CompileStatic
static void createModuleInfo(String moduleName, String version, Path outputPath) {
	def id = /\p{javaJavaIdentifierStart}\p{javaJavaIdentifierPart}*/
	if (!(moduleName ==~ $/$id(?:\.$id)*/$))
		throw new IllegalArgumentException("Invalid module name: '$moduleName'")

	def cw = new ClassWriter(0)
	cw.visit V9, ACC_MODULE, 'module-info', null, null, null

	def mv = cw.visitModule moduleName, ACC_OPEN, version

	def pkgs = new TreeSet<String>()

	Files.walkFileTree(Files.createDirectories(outputPath), new SimpleFileVisitor<Path>() {

		@Override
		FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
			if (dir.fileName.toString() in ['META-INF', 'OSGI-OPT']) {
				return FileVisitResult.SKIP_SUBTREE
			}
			return super.preVisitDirectory(dir, attrs)
		}

		@Override
		FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
			if (file.fileName.toString().endsWith('.class')) {
				pkgs.add outputPath.relativize(file.parent).join('/')
			}
			super.visitFile(file, attrs)
		}
	})

	for (def pkg in pkgs) pkg && mv.visitPackage(pkg)

	mv.visitRequire 'java.base', ACC_MANDATED, null
	mv.visitRequire 'java.annotation', ACC_STATIC_PHASE, null
	mv.visitRequire 'org.codehaus.groovy', ACC_STATIC_PHASE, null
	mv.visitRequire 'org.slf4j', 0, null
	mv.visitRequire 'thymeleaf', 0, null

	for (def pkg in pkgs) pkg && !pkg.endsWith('internal') && mv.visitExport(pkg, 0)

	mv.visitEnd()

	cw.visitEnd()

	Files.write outputPath.resolve('module-info.class'), cw.toByteArray()
}

def
	log = log,
	project = project

try {
	String moduleName = project.properties['module.name']
	String version = project.properties['moduleVersion'] ?: project.version
	String outputDirectory = project.build.outputDirectory

	createModuleInfo moduleName, version, Paths.get(outputDirectory)
} catch (Throwable t) {
	log.error(t)
	throw t
}
