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
import nz.net.ultraq.thymeleaf.layoutdialect.models.extensions.ChildModelIterator;
import nz.net.ultraq.thymeleaf.layoutdialect.models.extensions.IModelExtensions;
import nz.net.ultraq.thymeleaf.layoutdialect.models.extensions.ITemplateEventExtensions;
import org.thymeleaf.model.*;

import java.util.ArrayList;
import java.util.ListIterator;

/**
 * A special version of the {@link GroupingStrategy} sorter that respects the
 * position of the {@code <title>} element within the layout page.
 * <p>
 * The default behaviour of the layout dialect has historically been to place
 * the {@code <title>} element at the beginning of the {@code <head>} element
 * during the decoration process; an arbitrary design decision which made
 * development of this library easier. However, this runs against the
 * expectations of developers who wished to control the order of elements, most
 * notably the position of a {@code <meta charset...>} element. This sorting
 * strategy instead keep {@code <title>}s wherever they exist within the
 * target/layout template being decorated, and then appending everything else as
 * normal.
 * <p>
 * This will become the default behaviour of the layout dialect from version 3.x
 * onwards, but was introduced in 2.4.0 to be a non-breaking change.
 *
 * @author zhanhb
 * @author Emanuel Rabina
 * @since 2.4.0
 */
public class GroupingRespectLayoutTitleStrategy implements SortingStrategy {

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
		final int OTHER = 6;

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
			return OTHER;
		}
		return 0;
	}

	/**
	 * For {@code <title>} elements, returns the position of the matching
	 * {@code <title>} in the {@code headModel} argument, otherwise returns the
	 * index of the last set of elements that are of the same 'type' as the
	 * content node. eg: groups scripts with scripts, stylesheets with
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

		// Locate any matching <title> element
		if (IModelExtensions.isElementOf(childModel, "title")) {
			int existingTitleIndex = IModelExtensions.findIndexOf(headModel, event -> ITemplateEventExtensions.isOpeningElementOf(event, "title"));
			if (existingTitleIndex != -1) {
				return existingTitleIndex;
			}
		}

		int type = findMatchingType(childModel);
		ArrayList<IModel> list = new ArrayList<>(20);

		ChildModelIterator it = IModelExtensions.childModelIterator(headModel);
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
		return 1;
	}

}
