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
package nz.net.ultraq.thymeleaf.models.extensions;

import org.thymeleaf.model.IText;

import java.util.Objects;

/**
 * Meta-programming extensions to the {@link IText} class.
 *
 * @author zhanhb
 * @author Emanuel Rabina
 */
public class ITextExtensions {

	/**
	 * Compares this text with another.
	 *
	 * @param self
	 * @param other
	 * @return {@code true} if the text content matches.
	 */
	public static boolean equals(IText self, Object other) {
		return other instanceof IText && Objects.equals(self.getText(), ((IText) other).getText());
	}

	/**
	 * Returns whether or not this text event is collapsible whitespace.
	 *
	 * @param self
	 * @return {@code true} if, when trimmed, the text content is empty.
	 */
	public static boolean isWhitespace(IText self) {
		return self.getText().trim().isEmpty();
	}

}
