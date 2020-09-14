/* 
 * Copyright 2017, Emanuel Rabina (http://www.ultraq.net.nz/)
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

package nz.net.ultraq.thymeleaf.tests

import nz.net.ultraq.thymeleaf.LayoutDialect
import nz.net.ultraq.thymeleaf.decorators.strategies.GroupingStrategy
import nz.net.ultraq.thymeleaf.testing.JUnitTestExecutor

import org.junit.runners.Parameterized
import org.reflections.Reflections
import org.reflections.scanners.ResourcesScanner
import org.thymeleaf.dialect.AbstractProcessorDialect
import org.thymeleaf.dialect.IDialect
import org.thymeleaf.processor.IProcessor
import org.thymeleaf.standard.StandardDialect
import org.thymeleaf.standard.processor.StandardXmlNsTagProcessor
import org.thymeleaf.templatemode.TemplateMode

/**
 * Special test executor for testing interaction of the layout dialect with
 * other dialects.
 * 
 * @author Emanuel Rabina
 */
class LayoutDialectInteractionTestExecutor extends JUnitTestExecutor {

	final List<? extends IDialect> testDialects = [
		new StandardDialect(),
		new LayoutDialect(new GroupingStrategy()),
		new HighPriorityDialect()
	]

	/**
	 * Return only Thymeleaf testing files involved in the testing of dialect
	 * interaction.
	 *
	 * @return List of all the Thymeleaf testing files for dialect interaction.
	 */
	@Parameterized.Parameters(name = '{0}')
	static List<String> listInteractionLayoutDialectTests() {

		return new Reflections('', new ResourcesScanner())
			.getResources(~/Interaction.*\.thtest/) as List
	}


	/**
	 * A do-nothing high-priority dialect.
	 */
	private class HighPriorityDialect extends AbstractProcessorDialect {

		private HighPriorityDialect() {
			super('High', 'high', 2)
		}

		@Override
		Set<IProcessor> getProcessors(String dialectPrefix) {
			return [
				new StandardXmlNsTagProcessor(TemplateMode.HTML, dialectPrefix)
			]
		}
	}
}
