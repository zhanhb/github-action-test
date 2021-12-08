import org.objectweb.asm.ClassWriter

import java.nio.file.Files
import java.nio.file.Paths

import static org.objectweb.asm.Opcodes.*

try {
	def project = project

	String moduleName = project.properties['module.name']
	String version = project.version
	String sourceDirectory = project.build.sourceDirectory
	String outputDirectory = project.build.outputDirectory

	def id = /\p{javaJavaIdentifierStart}\p{javaJavaIdentifierPart}*/
	if (!(moduleName ==~ /$id(?:\.$id)*/))
		throw new IllegalArgumentException("Invalid module name: '$moduleName'")

	def cw = new ClassWriter(0)
	cw.visit V9, ACC_MODULE, 'module-info', null, null, null

	def mv = cw.visitModule moduleName, ACC_OPEN, version
	mv.visitRequire 'java.base', 0, null
	mv.visitRequire 'java.annotation', ACC_STATIC_PHASE, null
	mv.visitRequire 'org.codehaus.groovy', ACC_STATIC_PHASE, null
	mv.visitRequire 'org.slf4j', 0, null
	mv.visitRequire 'thymeleaf', 0, null

	def sourcePath = Paths.get sourceDirectory
	Files.walk(sourcePath).filter {
		String fileName = it.fileName
		fileName.endsWith('.java') || fileName.endsWith('.groovy')
	}.map {
		sourcePath.relativize(it.parent).join('/')
	}.sorted().distinct().forEach {
		it && !it.endsWith('internal') && mv.visitExport(it, 0)
	}
	mv.visitEnd()

	cw.visitEnd()

	def file = Files
		.createDirectories(Paths.get(outputDirectory))
		.resolve 'module-info.class'
	Files.write(file, cw.toByteArray())
} catch (Throwable t) {
	log.error(t); throw t
}
