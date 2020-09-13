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
package nz.net.ultraq.thymeleaf.internal;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.ElementDefinitions;
import org.thymeleaf.engine.HTMLElementDefinition;
import org.thymeleaf.engine.HTMLElementType;
import org.thymeleaf.model.AttributeValueQuotes;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IModelFactory;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.StringUtils;

/**
 * Create Thymeleaf 3 models using the Groovy builder syntax.
 *
 * @author zhanhb
 * @author Emanuel Rabina
 */
public class ModelBuilder {

    private static final Logger logger = LoggerFactory.getLogger(ModelBuilder.class);

    private static final Set<String> encounteredVoidTags = Collections.newSetFromMap(new ConcurrentHashMap<>());

    private final ElementDefinitions elementDefinitions;
    private final IModelFactory modelFactory;
    private final TemplateMode templateMode;

    /**
     * Constructor, create a new model builder.
     *
     * @param context
     */
    public ModelBuilder(ITemplateContext context) {
        this(context.getModelFactory(), context.getConfiguration().getElementDefinitions(), context.getTemplateMode());
    }

    /**
     * Constructor, create a new model builder.
     *
     * @param modelFactory
     * @param elementDefinitions
     * @param templateMode
     */
    public ModelBuilder(IModelFactory modelFactory, ElementDefinitions elementDefinitions, TemplateMode templateMode) {
        this.modelFactory = modelFactory;
        this.elementDefinitions = elementDefinitions;
        this.templateMode = templateMode;
    }

    /**
     * Create a model for the given element.
     *
     * @param name Element name.
     * @return New model representing an element with the given name.
     */
    public IModel createNode(String name) {
        return createNode(name, null, null);
    }

    /**
     * Create a model for the given element and inner text content.
     *
     * @param name Element name.
     * @param value Text content.
     * @return New model representing an element with the given name and
     * content.
     */
    public IModel createNode(String name, String value) {
        return createNode(name, null, value);
    }

    /**
     * Create a model for the given element and attributes.
     *
     * @param name Element name.
     * @param attributes Element attributes.
     * @return New model representing an element with the given name and
     * attributes.
     */
    public IModel createNode(String name, Map<String, String> attributes) {
        return createNode(name, attributes, null);
    }

    /**
     * Create a model for the given element, attributes, and inner text content.
     *
     * @param name Element name.
     * @param attributes Element attributes.
     * @param value Text content.
     * @return New model representing an element with the given name,
     * attributes, and content.
     */
    public IModel createNode(String name, Map<String, String> attributes, String value) {
        Objects.requireNonNull(name);

        IModel model = modelFactory.createModel();
        HTMLElementDefinition elementDefinition = (HTMLElementDefinition) elementDefinitions.forName(templateMode, name);

        // Standalone element
        if (elementDefinition.getType() == HTMLElementType.VOID) {
            if (attributes != null && attributes.containsKey("standalone")) {
                attributes.remove("standalone");
                model.add(modelFactory.createStandaloneElementTag(name, attributes, AttributeValueQuotes.DOUBLE, false, true));
            } else if (attributes != null && attributes.containsKey("void")) {
                attributes.remove("void");
                model.add(modelFactory.createStandaloneElementTag(name, attributes, AttributeValueQuotes.DOUBLE, false, false));
            } else {
                if (encounteredVoidTags.add(name)) {
                    logger.warn(
                            "Instructed to write a closing tag {} for an HTML void element.  "
                            + "This might cause processing errors further down the track.  "
                            + "To avoid this, either self close the opening element, remove the closing tag, or process this template using the XML processing mode.  "
                            + "See https://html.spec.whatwg.org/multipage/syntax.html#void-elements for more information on HTML void elements.",
                            name);
                }
                model.add(modelFactory.createStandaloneElementTag(name, attributes, AttributeValueQuotes.DOUBLE, false, false));
                model.add(modelFactory.createCloseElementTag(name, false, true));
            }
        } else if (attributes != null && attributes.containsKey("standalone")) { // Other standalone element
            attributes.remove("standalone");
            model.add(modelFactory.createStandaloneElementTag(name, attributes, AttributeValueQuotes.DOUBLE, false, true));
        } else { // Open/close element and potential text content
            model.add(modelFactory.createOpenElementTag(name, attributes, AttributeValueQuotes.DOUBLE, false));
            if (!StringUtils.isEmpty(value)) {
                model.add(modelFactory.createText(value));
            }
            model.add(modelFactory.createCloseElementTag(name));
        }

        return model;
    }

    /**
     * Link a parent and child node. A child node is appended to a parent by
     * being the last sub-model before the parent close tag.
     *
     * @param parent
     * @param child
     */
    @SuppressWarnings("null")
    public void nodeCompleted(@Nullable IModel parent, @Nullable IModel child) {
        if (Extensions.asBoolean(parent)) {
            parent.insertModel(parent.size() - 1, child);
        }
    }

    /**
     * Does nothing. Because models only copy events when added to one another,
     * we can't just add child events at this point - we need to wait until that
     * child has had it's children added, and so on. So the parent/child link is
     * made in the {@link ModelBuilder#nodeCompleted} method instead.
     *
     * @param parent
     * @param child
     */
    public void setParent(IModel parent, IModel child) {
    }

}
