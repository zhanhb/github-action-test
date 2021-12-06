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
package nz.net.ultraq.thymeleaf.layoutdialect.models.extensions;

import nz.net.ultraq.thymeleaf.layoutdialect.internal.IContextDelegate;
import org.thymeleaf.context.IContext;
import org.thymeleaf.model.IElementTag;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.standard.StandardDialect;

import java.util.Map;
import java.util.Objects;

/**
 * Meta-programming extensions to the {@link IProcessableElementTag} class.
 *
 * @author zhanhb
 * @author Emanuel Rabina
 */
public class IProcessableElementTagExtensions {

	/**
	 * Compare processable elements for equality.
	 * 
	 * @param self
	 * @param other
	 * @return {@code true} if this tag has the same name and attributes as the
	 * other element.
	 */
	public static boolean equals(IProcessableElementTag self, Object other) {
		return other instanceof IProcessableElementTag
			&& Objects.equals(self.getElementCompleteName(), ((IElementTag) other).getElementCompleteName())
			&& Objects.equals(self.getAttributeMap(), ((IProcessableElementTag) other).getAttributeMap());
	}

	/**
	 * Compare elements, ignoring XML namespace declarations and Thymeleaf's
	 * {@code th:with} processor.
	 * 
	 * @param self
	 * @param other
	 * @param context
	 * @return {@code true} if the elements share the same name and all attributes,
	 *         with exceptions for of XML namespace declarations and Thymeleaf's
	 *         {@code th:with} attribute processor.
	 */
	public static boolean equalsIgnoreXmlnsAndWith(IProcessableElementTag self, IProcessableElementTag other, IContext context) {

		if (Objects.equals(self.getElementDefinition(), other.getElementDefinition())) {
			String standardDialectPrefix = IContextDelegate.getPrefixForDialect(context, StandardDialect.class);
			Map<String, String> attributeMap = other.getAttributeMap();
			for (Map.Entry<String, String> entry : self.getAttributeMap().entrySet()) {
				String key = entry.getKey();
				String value = entry.getValue();
				if (key.startsWith("xmlns:") || key.equals(standardDialectPrefix + ":with") || key.equals("data-" + "-with"))
					continue;
				if (!attributeMap.containsKey(key)) {
					return false;
				}
				if (!Objects.equals(value, attributeMap.get(key))) {
					return false;
				}
			}
			return true;
		}
		return false;
	}
}
