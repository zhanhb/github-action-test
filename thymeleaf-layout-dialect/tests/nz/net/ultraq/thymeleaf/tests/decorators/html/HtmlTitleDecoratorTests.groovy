/* 
 * Copyright 2016, Emanuel Rabina (http://www.ultraq.net.nz/)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package nz.net.ultraq.thymeleaf.tests.decorators.html

import nz.net.ultraq.thymeleaf.tests.LayoutDialect
import nz.net.ultraq.thymeleaf.decorators.html.HtmlTitleDecorator
import nz.net.ultraq.thymeleaf.models.ModelBuilder

import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.ITemplateContext
import org.thymeleaf.dialect.IProcessorDialect
import org.thymeleaf.standard.StandardDialect
import org.thymeleaf.templatemode.TemplateMode

/**
 * Unit tests for the HTML head decorator.
 * 
 * @author Emanuel Rabina
 */
class HtmlTitleDecoratorTests {

	private static ITemplateContext mockContext
	private static ModelBuilder modelBuilder

	private HtmlTitleDecorator htmlTitleDecorator

	/**
	 * Set up, create a template engine.
	 */
	@BeforeClass
	static void setupThymeleafEngine() {

		def templateEngine = new TemplateEngine(
			additionalDialects: [
				new LayoutDialect()
			]
		)
		def modelFactory = templateEngine.configuration.getModelFactory(TemplateMode.HTML)

		modelBuilder = new ModelBuilder(modelFactory, templateEngine.configuration.elementDefinitions, TemplateMode.HTML)
		mockContext = [
			getConfiguration: { ->
				return templateEngine.configuration
			},
			getModelFactory: { ->
				return modelFactory
			},
			getTemplateMode: { ->
				return TemplateMode.HTML
			}
		] as ITemplateContext
		mockContext.metaClass {
			getPrefixForDialect = { Class<IProcessorDialect> dialectClass ->
				return dialectClass == StandardDialect ? 'th' :
				       dialectClass == LayoutDialect ? 'layout' :
				       'mock-prefix'
			}
		}
	}

	/**
	 * Set up, create a new HTML title decorator.
	 */
	@Before
	void setupHtmlDocumentDecorator() {

		htmlTitleDecorator = new HtmlTitleDecorator(mockContext)
	}

	/**
	 * Test that the HTML title decorator doesn't modify the source parameters.
	 */
	@Test
	void immutability() {

		def content = modelBuilder.build {
			title('Content page')
		}

		def layout = modelBuilder.build {
			title('Layout page')
		}

		def contentOrig = content.cloneModel()
		def layoutOrig = layout.cloneModel()

		htmlTitleDecorator.decorate(layout, content)

		def contentAfter = content.cloneModel()
		def layoutAfter = layout.cloneModel()

		assert contentOrig == contentAfter
		assert layoutOrig == layoutAfter
	}
}
