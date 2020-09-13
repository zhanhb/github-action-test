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
package nz.net.ultraq.thymeleaf.decorators.html;

import java.util.Collections;
import java.util.Iterator;
import nz.net.ultraq.thymeleaf.LayoutDialect;
import nz.net.ultraq.thymeleaf.decorators.Decorator;
import nz.net.ultraq.thymeleaf.decorators.TitlePatternProcessor;
import nz.net.ultraq.thymeleaf.internal.Extensions;
import nz.net.ultraq.thymeleaf.internal.ModelBuilder;
import nz.net.ultraq.thymeleaf.models.ElementMerger;
import org.thymeleaf.context.IEngineContext;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IAttribute;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.model.ITemplateEvent;
import org.thymeleaf.standard.StandardDialect;
import org.thymeleaf.standard.processor.StandardTextTagProcessor;
import org.thymeleaf.standard.processor.StandardUtextTagProcessor;

/**
 * Decorator for the {@code <title>} part of the template to handle the special
 * processing required for the {@code layout:title-pattern} processor.
 *
 * @author zhanhb
 * @author Emanuel Rabina
 */
public class HtmlTitleDecorator implements Decorator {

    // Get the title pattern to use
    private static IAttribute titlePatternProcessorRetriever(IModel titleModel, String layoutDialectPrefix) {
        if (Extensions.asBoolean(titleModel)) {
            ITemplateEvent event = Extensions.first(titleModel);
            if (event != null) {
                return ((IProcessableElementTag) event).getAttribute(layoutDialectPrefix, TitlePatternProcessor.PROCESSOR_NAME);
            }
        }
        return null;
    }

    private static IModel build(ModelBuilder modelBuilder, String key, String value) {
        return modelBuilder.createNode("th:block", Collections.singletonMap(key, value));
    }

    private static void extractTitle(IModel titleModel, String contextKey, ITemplateContext context, String standardDialectPrefix, ModelBuilder modelBuilder) {

        // This title part already exists from a previous run, so do nothing
        if (context.containsVariable(contextKey)) {
            return;
        }

        if (Extensions.asBoolean(titleModel)) {
            IProcessableElementTag titleTag = (IProcessableElementTag) Extensions.first(titleModel);

            // Escapable title from a th:text attribute on the title tag
            if (titleTag.hasAttribute(standardDialectPrefix, StandardTextTagProcessor.ATTR_NAME)) {
                ((IEngineContext) context).setVariable(contextKey, build(modelBuilder,
                        "th:text", titleTag.getAttributeValue(standardDialectPrefix, StandardTextTagProcessor.ATTR_NAME)));
            } // Unescaped title from a th:utext attribute on the title tag, or
            // whatever happens to be within the title tag
            else if (titleTag.hasAttribute(standardDialectPrefix, StandardUtextTagProcessor.ATTR_NAME)) {
                ((IEngineContext) context).setVariable(contextKey, build(modelBuilder,
                        "th:utext", titleTag.getAttributeValue(standardDialectPrefix, StandardUtextTagProcessor.ATTR_NAME)));

            } else {
                IModel titleChildrenModel = context.getModelFactory().createModel();
                for (Iterator<IModel> it = Extensions.childModelIterator(titleModel); it.hasNext();) {
                    IModel model = it.next();
                    titleChildrenModel.addModel(model);
                }
                ((IEngineContext) context).setVariable(contextKey, titleChildrenModel);
            }
        }
    }

    private final ITemplateContext context;

    /**
     * Constructor, sets up the decorator context.
     *
     * @param context
     */
    public HtmlTitleDecorator(ITemplateContext context) {
        this.context = context;
    }

    /**
     * Special decorator for the {@code <title>} part, accumulates the important
     * processing parts for the {@code layout:title-pattern} processor.
     *
     * @param targetTitleModel
     * @param sourceTitleModel
     * @return A new {@code <title>} model that is the result of decorating the
     * {@code <title>}.
     */
    @Override
    public IModel decorate(IModel targetTitleModel, IModel sourceTitleModel) {
        ModelBuilder modelBuilder = new ModelBuilder(context);
        String layoutDialectPrefix = Extensions.getPrefixForDialect(context, LayoutDialect.class);
        String standardDialectPrefix = Extensions.getPrefixForDialect(context, StandardDialect.class);

        IAttribute titlePatternProcessor = titlePatternProcessorRetriever(sourceTitleModel, layoutDialectPrefix);
        if (titlePatternProcessor == null) {
            titlePatternProcessor = titlePatternProcessorRetriever(targetTitleModel, layoutDialectPrefix);
        }

        IModel resultTitle;

        // Set the title pattern to use on a new model, as well as the important
        // title result parts that we want to use on the pattern.
        if (titlePatternProcessor != null) {
            extractTitle(sourceTitleModel, TitlePatternProcessor.CONTENT_TITLE_KEY, context, standardDialectPrefix, modelBuilder);
            extractTitle(targetTitleModel, TitlePatternProcessor.LAYOUT_TITLE_KEY, context, standardDialectPrefix, modelBuilder);

            resultTitle = new ModelBuilder(context).createNode("title",
                    Collections.singletonMap(titlePatternProcessor.getAttributeCompleteName(), titlePatternProcessor.getValue()));
        } else {
            resultTitle = new ElementMerger(context).merge(targetTitleModel, sourceTitleModel);
        }
        return resultTitle;
    }

}
