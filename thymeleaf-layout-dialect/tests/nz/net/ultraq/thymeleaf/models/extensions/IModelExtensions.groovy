/*
 * Copyright 2016 zhanhb.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nz.net.ultraq.thymeleaf.models.extensions;

import nz.net.ultraq.thymeleaf.internal.Extensions as Z;
import nz.net.ultraq.thymeleaf.internal.ITemplateEventConsumer;
import nz.net.ultraq.thymeleaf.internal.ITemplateEventIntPredicate;
import nz.net.ultraq.thymeleaf.internal.ITemplateEventPredicate;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IModelFactory;
import org.thymeleaf.model.ITemplateEvent;

/**
 *
 * @author zhanhb
 */
class IModelExtensions {

    static void apply() {
        IModel.metaClass {
            asBoolean << {
                Z.asBoolean(delegate)
            }
            childModelIterator << {
                Z.childModelIterator(delegate)
            }
            each << { Closure closure ->
                Z.each(delegate, closure as ITemplateEventConsumer)
            }
            equals << { Object other ->
                Z.equals(delegate, other)
            }
            equalsIgnoreWhitespace << { IModel other ->
                Z.equalsIgnoreWhitespace(delegate, other)
            }
            everyWithIndex << { Closure closure ->
                Z.everyWithIndex(delegate, closure as ITemplateEventIntPredicate)
            }
            find << { Closure closure ->
                Z.find(delegate, closure as ITemplateEventIntPredicate)
            }
            findAll << { Closure closure ->
                Z.findAll(delegate, closure as ITemplateEventIntPredicate)
            }
            findIndexOf << { Closure closure ->
                Z.findIndexOf(delegate, closure as ITemplateEventPredicate)
            }
            findIndexOfModel << { Closure closure ->
                Z.findIndexOf(delegate, closure as ITemplateEventPredicate)
            }
            findModel << { Closure closure ->
                Z.findModel(delegate, closure as ITemplateEventPredicate)
            }
            first << {
                Z.first(delegate)
            }
            getModel << { int pos ->
                Z.getModel(delegate, pos)
            }
            insertModelWithWhitespace << { int pos, IModel model ->
                Z.insertModelWithWhitespace(delegate, pos, model)
            }
            insertWithWhitespace << { int pos, ITemplateEvent event, IModelFactory modelFactory ->
                Z.insertWithWhitespace(delegate, pos, event, modelFactory)
            }
            isElement << {
                Z.isElement(delegate)
            }
            isElementOf << {tagName ->
                Z.isElementOf(delegate, tagName)
            }
            isWhitespace << {
                Z.isWhitespace(delegate)
            }
            iterator << {
                Z.iterator(delegate)
            }
            last << {
                Z.last(delegate)
            }
            removeChildren << {
                Z.removeChildren(delegate)
            }
            removeFirst << {
                Z.removeFirst(delegate)
            }
            removeLast << {
                Z.removeLast(delegate)
            }
            removeModel << { int pos ->
                Z.removeModel(delegate, pos)
            }
            replaceModel << { int pos, IModel model ->
                Z.replaceModel(delegate, pos, model)
            }
            sizeOfModelAt << { int pos ->
                Z.sizeOfModelAt(delegate, pos)
            }
            trim << {
                Z.trim(delegate)
            }
        }
    }

}
