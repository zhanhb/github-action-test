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
package nz.net.ultraq.thymeleaf.models;

import nz.net.ultraq.thymeleaf.internal.Extensions;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.AttributeValueQuotes;
import org.thymeleaf.model.IElementTag;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IModelFactory;
import org.thymeleaf.model.IOpenElementTag;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.model.IStandaloneElementTag;
import org.thymeleaf.model.ITemplateEvent;

/**
 * Merges an element and all its children into an existing element.
 *
 * @author zhanhb
 * @author Emanuel Rabina
 */
public class ElementMerger implements ModelMerger {

    private final ITemplateContext context;

    /**
     * Constructor, sets up the element merger context.
     *
     * @param context
     */
    public ElementMerger(ITemplateContext context) {
        this.context = context;
    }

    /**
     * Replace the content of the target element, with the content of the source
     * element.
     *
     * @param targetModel
     * @param sourceModel
     * @return Model that is the result of the merge.
     */
    @Override
    public IModel merge(IModel targetModel, IModel sourceModel) {
        // If one of the parameters is missing return a copy of the other, or
        // nothing if both parameters are missing.
        if (!Extensions.asBoolean(targetModel) || !Extensions.asBoolean(sourceModel)) {
            IModel result = Extensions.asBoolean(targetModel) ? targetModel.cloneModel() : null;
            return Extensions.asBoolean(result) ? result : Extensions.asBoolean(sourceModel) ? sourceModel.cloneModel() : null;
        }

        IModelFactory modelFactory = context.getModelFactory();

        // The result we want is the source model, but merged into the target root element attributes
        ITemplateEvent sourceRootEvent = Extensions.first(sourceModel);
        IModel sourceRootElement = modelFactory.createModel(sourceRootEvent);
        IProcessableElementTag targetRootEvent = (IProcessableElementTag) Extensions.first(targetModel);
        IModel targetRootElement = modelFactory.createModel(
                sourceRootEvent instanceof IOpenElementTag
                        ? modelFactory.createOpenElementTag(((IElementTag) sourceRootEvent).getElementCompleteName(),
                                targetRootEvent.getAttributeMap(), AttributeValueQuotes.DOUBLE, false)
                        : sourceRootEvent instanceof IStandaloneElementTag
                                ? modelFactory.createStandaloneElementTag(((IElementTag) sourceRootEvent).getElementCompleteName(),
                                        targetRootEvent.getAttributeMap(), AttributeValueQuotes.DOUBLE, false, ((IStandaloneElementTag) sourceRootEvent).isMinimized())
                                : null);
        IModel mergedRootElement = new AttributeMerger(context).merge(targetRootElement, sourceRootElement);
        IModel mergedModel = sourceModel.cloneModel();
        mergedModel.replace(0, Extensions.first(mergedRootElement));
        return mergedModel;
    }

}
