/*
 * Copyright 2013, Emanuel Rabina (http://www.ultraq.net.nz/)
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
package nz.net.ultraq.thymeleaf.layoutdialect.decorators.html;

import nz.net.ultraq.thymeleaf.layoutdialect.decorators.Decorator;
import nz.net.ultraq.thymeleaf.layoutdialect.decorators.SortingStrategy;
import nz.net.ultraq.thymeleaf.layoutdialect.models.AttributeMerger;
import nz.net.ultraq.thymeleaf.layoutdialect.models.extensions.IModelExtensions;
import nz.net.ultraq.thymeleaf.layoutdialect.models.extensions.ITemplateEventExtensions;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IModelFactory;
import org.thymeleaf.model.ITemplateEvent;

import java.util.Iterator;
import java.util.function.Predicate;

/**
 * A decorator specific to processing an HTML {@code <head>} element.
 *
 * @author zhanhb
 * @author Emanuel Rabina
 */
public class HtmlHeadDecorator implements Decorator {

	private final ITemplateContext context;
	private final SortingStrategy sortingStrategy;

	/**
	 * Constructor, sets up the decorator context.
	 *
	 * @param context
	 * @param sortingStrategy
	 */
	public HtmlHeadDecorator(ITemplateContext context, SortingStrategy sortingStrategy) {
		this.context = context;
		this.sortingStrategy = sortingStrategy;
	}

	/**
	 * Decorate the {@code <head>} part.
	 *
	 * @param targetHeadModel
	 * @param sourceHeadModel
	 * @return Result of the decoration.
	 */
	@Override
	public IModel decorate(IModel targetHeadModel, IModel sourceHeadModel) {
		// If none of the parameters are present, return nothing
		if (!IModelExtensions.asBoolean(targetHeadModel) && !IModelExtensions.asBoolean(sourceHeadModel)) {
			return null;
		}

		IModelFactory modelFactory = context.getModelFactory();

		// New head model based off the target being decorated
		IModel resultHeadModel = new AttributeMerger(context).merge(targetHeadModel, sourceHeadModel);
		if (IModelExtensions.asBoolean(sourceHeadModel) && IModelExtensions.asBoolean(targetHeadModel)) {
			Iterator<IModel> it = IModelExtensions.childModelIterator(sourceHeadModel);
			if (it != null) {
				while (it.hasNext()) {
					IModel model = it.next();
					IModelExtensions.insertModelWithWhitespace(resultHeadModel,
						sortingStrategy.findPositionForModel(resultHeadModel, model),
						model, modelFactory);
				}
			}
		}

		// Replace <title>s in the result with a proper merge of the source and target <title> elements
		Predicate<ITemplateEvent> titleFinder = event -> ITemplateEventExtensions.isOpeningElementOf(event, "title");

		int indexOfTitle = IModelExtensions.findIndexOf(resultHeadModel, titleFinder);
		if (indexOfTitle != -1) {
			IModelExtensions.removeAllModels(resultHeadModel, titleFinder);
			IModel resultTitle = new HtmlTitleDecorator(context).decorate(
				IModelExtensions.asBoolean(targetHeadModel) ? IModelExtensions.findModel(targetHeadModel, titleFinder) : null,
				IModelExtensions.asBoolean(sourceHeadModel) ? IModelExtensions.findModel(sourceHeadModel, titleFinder) : null
			);
			IModelExtensions.insertModelWithWhitespace(resultHeadModel, indexOfTitle, resultTitle, modelFactory);
		}
		return resultHeadModel;
	}

	public final ITemplateContext getContext() {
		return this.context;
	}

	public final SortingStrategy getSortingStrategy() {
		return this.sortingStrategy;
	}

}
