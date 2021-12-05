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
package nz.net.ultraq.thymeleaf.expressionprocessor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.standard.expression.FragmentExpression;
import org.thymeleaf.standard.expression.IStandardExpression;
import org.thymeleaf.standard.expression.StandardExpressions;
import org.thymeleaf.util.StringUtils;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A simplified API for working with Thymeleaf expressions.
 *
 * @author zhanhb
 * @author Emanuel Rabina
 */
public class ExpressionProcessor {

	private static final Logger logger = LoggerFactory.getLogger(ExpressionProcessor.class);
	@SuppressWarnings("CollectionWithoutInitialCapacity")
	private static final Set<String> oldFragmentExpressions = Collections.newSetFromMap(new ConcurrentHashMap<>());

	private final IExpressionContext context;

	/**
	 * Constructor, sets the execution context.
	 *
	 * @param context
	 */
	public ExpressionProcessor(IExpressionContext context) {
		this.context = context;
	}

	/**
	 * Parses an expression, returning the matching expression type.
	 *
	 * @param expression
	 * @return Matching expression type.
	 */
	public IStandardExpression parse(String expression) {
		return StandardExpressions.getExpressionParser(context.getConfiguration())
			.parseExpression(context, expression);
	}

	/**
	 * Parses an expression under the assumption it is a fragment expression.
	 * This method will wrap fragment expressions written in Thymeleaf 2 syntax
	 * as a backwards compatibility measure for those migrating their web apps
	 * to Thymeleaf 3. (This is because Thymeleaf 3 currently does the same, but
	 * expect this method to go away when Thymeleaf starts enforcing the new
	 * fragment expression syntax itself.)
	 *
	 * @param expression
	 * @return A fragment expression.
	 */
	public FragmentExpression parseFragmentExpression(String expression) {
		if (!StringUtils.isEmpty(expression) && !expression.matches("(?s)^~\\{.+\\}$")) {
			if (oldFragmentExpressions.add(expression)) {
				logger.warn(
					"Fragment expression \"{}\" is being wrapped as a Thymeleaf 3 fragment expression (~{...}) for backwards compatibility purposes.  "
						+ "This wrapping will be dropped in the next major version of the expression processor, so please rewrite as a Thymeleaf 3 fragment expression to future-proof your code.  "
						+ "See https://github.com/thymeleaf/thymeleaf/issues/451 for more information.",
					expression);
			}
			return (FragmentExpression) parse("~{" + expression + "}");
		}

		return (FragmentExpression) parse(expression);
	}

	/**
	 * Parse and executes an expression, returning whatever the type of the
	 * expression result is.
	 *
	 * @param expression
	 * @return The result of the expression being executed.
	 */
	public Object process(String expression) {
		return parse(expression).execute(context);
	}

	/**
	 * Parse and execute an expression, returning the result as a string. Useful
	 * for expressions that expect a simple result.
	 *
	 * @param expression
	 * @return The expression as a string.
	 */
	public String processAsString(String expression) {
		return String.valueOf(process(expression));
	}

	public final IExpressionContext getContext() {
		return this.context;
	}

}
