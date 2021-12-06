/*
 * Copyright 2015, Emanuel Rabina (http://www.ultraq.net.nz/)
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
import org.thymeleaf.model.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;

/**
 * The {@code <head>} merging strategy which groups like elements together.
 *
 * @author zhanhb
 * @author Emanuel Rabina
 * @since 2.4.0
 */
public class GroupingStrategy implements SortingStrategy {

	/**
	 * Figure out the enum for the given model.
	 *
	 * @param model
	 * @return Matching enum to describe the model.
	 */
	private static int findMatchingType(IModel model) {
		final int COMMENT = 1;
		final int META = 2;
		final int SCRIPT = 3;
		final int STYLE = 4;
		final int STYLESHEET = 5;
		final int TITLE = 6;
		final int OTHER = 7;

		ITemplateEvent event = IModelExtensions.first(model);

		if (event instanceof IComment) {
			return COMMENT;
		}
		if (event instanceof IElementTag) {
			String elementCompleteName = ((IElementTag) event).getElementCompleteName();
			if (event instanceof IProcessableElementTag && "meta".equals(elementCompleteName)) {
				return META;
			}
			if (event instanceof IOpenElementTag && "script".equals(elementCompleteName)) {
				return SCRIPT;
			}
			if (event instanceof IOpenElementTag && "style".equals(elementCompleteName)) {
				return STYLE;
			}
			if (event instanceof IProcessableElementTag && "link".equals(elementCompleteName)
				&& "stylesheet".equals(((IProcessableElementTag) event).getAttributeValue("rel"))) {
				return STYLESHEET;
			}
			if (event instanceof IOpenElementTag && "title".equals(elementCompleteName)) {
				return TITLE;
			}
			return OTHER;
		}
		return 0;
	}

	/**
	 * Returns the index of the last set of elements that are of the same 'type'
	 * as the content node. eg: groups scripts with scripts, stylesheets with
	 * stylesheets, and so on.
	 *
	 * @param headModel
	 * @param childModel
	 * @return Position of the end of the matching element group.
	 */
	@Override
	public int findPositionForModel(IModel headModel, IModel childModel) {
		// Discard text/whitespace nodes
		if (IModelExtensions.isWhitespace(childModel)) {
			return -1;
		}

		// Find the last element of the same type, and return the point after that
		int type = findMatchingType(childModel);
		ArrayList<IModel> list = new ArrayList<>(20);

		Iterator<IModel> it = IModelExtensions.childModelIterator(headModel);
		if (it != null) {
			while (it.hasNext()) {
				list.add(it.next());
			}
		}

		ListIterator<IModel> listIterator = list.listIterator(list.size());
		while (listIterator.hasPrevious()) {
			IModel headSubModel = listIterator.previous();
			if (type == findMatchingType(headSubModel)) {
				if (IModelExtensions.asBoolean(headModel)) {
					return IModelExtensions.findIndexOfModel(headModel, headSubModel) + headSubModel.size();
				}
				break;
			}
		}

		// Otherwise, do what the AppendingStrategy does
		int positions = headModel.size();
		return positions - (positions > 2 ? 2 : 1);
	}

}
