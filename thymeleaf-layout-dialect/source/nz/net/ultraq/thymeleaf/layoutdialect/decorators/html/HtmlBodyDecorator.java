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
import nz.net.ultraq.thymeleaf.layoutdialect.models.AttributeMerger;
import nz.net.ultraq.thymeleaf.layoutdialect.models.extensions.IModelExtensions;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IModel;

/**
 * A decorator specific to processing an HTML {@code <body>} element.
 *
 * @author zhanhb
 * @author Emanuel Rabina
 */
public class HtmlBodyDecorator implements Decorator {

	private final ITemplateContext context;

	/**
	 * Constructor, sets up the element decorator context.
	 *
	 * @param context
	 */
	public HtmlBodyDecorator(ITemplateContext context) {
		this.context = context;
	}

	/**
	 * Decorate the {@code <body>} part.
	 *
	 * @param targetBodyModel
	 * @param sourceBodyModel
	 * @return Result of the decoration.
	 */
	@Override
	public IModel decorate(IModel targetBodyModel, IModel sourceBodyModel) {
		// If one of the parameters is missing return a copy of the other, or
		// nothing if both parameters are missing.
		if (!IModelExtensions.asBoolean(targetBodyModel) || !IModelExtensions.asBoolean(sourceBodyModel)) {
			IModel result = IModelExtensions.asBoolean(targetBodyModel) ? targetBodyModel.cloneModel() : null;
			return IModelExtensions.asBoolean(result) ? result : IModelExtensions.asBoolean(sourceBodyModel) ? sourceBodyModel.cloneModel() : null;
		}
		return new AttributeMerger(context).merge(targetBodyModel, sourceBodyModel);
	}

	public final ITemplateContext getContext() {
		return context;
	}

}
