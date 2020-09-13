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
package nz.net.ultraq.thymeleaf.tests;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import nz.net.ultraq.thymeleaf.decorators.SortingStrategy;

/**
 *
 * @author zhanhb
 */
public class LayoutDialect extends nz.net.ultraq.thymeleaf.LayoutDialect {

    /**
     * Apply model extensions.
     */
    static {
        [
            // Context extensions
            nz.net.ultraq.thymeleaf.context.extensions.IContextExtensions,
            // Model extensions
            nz.net.ultraq.thymeleaf.models.extensions.IAttributeExtensions,
            nz.net.ultraq.thymeleaf.models.extensions.ICloseElementTagExtensions,
            nz.net.ultraq.thymeleaf.models.extensions.IModelExtensions,
            nz.net.ultraq.thymeleaf.models.extensions.IProcessableElementTagExtensions,
            nz.net.ultraq.thymeleaf.models.extensions.IStandaloneElementTagExtensions,
            nz.net.ultraq.thymeleaf.models.extensions.ITemplateEventExtensions,
            nz.net.ultraq.thymeleaf.models.extensions.ITextExtensions
        ]*.apply()
    }

    public LayoutDialect() {
    }

    public LayoutDialect(SortingStrategy sortingStrategy) {
        super(sortingStrategy);
    }

    public LayoutDialect(SortingStrategy sortingStrategy, boolean autoHeadMerging){
        super(sortingStrategy, autoHeadMerging);
    }

}
