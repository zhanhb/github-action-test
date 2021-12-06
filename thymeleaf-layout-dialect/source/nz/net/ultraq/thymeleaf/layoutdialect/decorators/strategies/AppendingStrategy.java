/*
 * Copyright 2019, Emanuel Rabina (http://www.ultraq.net.nz/)
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
package nz.net.ultraq.thymeleaf.layoutdialect.decorators.strategies;

import nz.net.ultraq.thymeleaf.layoutdialect.decorators.SortingStrategy;
import nz.net.ultraq.thymeleaf.layoutdialect.models.extensions.IModelExtensions;
import org.thymeleaf.model.IModel;

/**
 * The standard {@code <head>} merging strategy, which simply appends the
 * content elements to the layout ones.
 *
 * @author zhanhb
 * @author Emanuel Rabina
 * @since 2.4.0
 */
public class AppendingStrategy implements SortingStrategy {

	/**
	 * Returns the position at the end of the {@code <head>} section.
	 *
	 * @param headModel
	 * @return The end of the head model.
	 */
	public int findPositionForModel(IModel headModel, IModel childModel) {

		// Discard text/whitespace nodes
		if (IModelExtensions.isWhitespace(childModel)) {
			return -1;
		}

		int positions = headModel.size();
		return positions - (positions > 2 ? 2 : 1);
	}

}
