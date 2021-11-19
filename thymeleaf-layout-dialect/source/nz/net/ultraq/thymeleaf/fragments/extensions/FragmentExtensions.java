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
package nz.net.ultraq.thymeleaf.fragments.extensions;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IModel;
import org.thymeleaf.processor.element.IElementModelStructureHandler;

import java.util.*;

/**
 * Holds the layout fragments encountered across layout/decorator and content
 * templates for use later.
 *
 * @author zhanhb
 * @author Emanuel Rabina
 */
public class FragmentExtensions {

	public static final String FRAGMENT_COLLECTION_KEY = "LayoutDialect::FragmentCollection";

	/**
	 * Retrieves the fragment collection for the current context.
	 *
	 * @param self
	 * @param fromDecorator
	 * @return A new or existing fragment collection.
	 */
	@SuppressWarnings("CollectionWithoutInitialCapacity")
	public static Map<String, List<IModel>> getFragmentCollection(ITemplateContext self, boolean fromDecorator) {

		// If the template stack contains only 1 template and we've been called from
		// the decorator, then always return a new fragment collection.  This seems
		// to be one way to know if Thymeleaf is processing a new file and as such
		// should have a fresh collection to work with, otherwise we may be using an
		// older collection from an already-used context.
		// See: https://github.com/ultraq/thymeleaf-layout-dialect/issues/189
		if (self.getTemplateStack().size() == 1 && fromDecorator) {
			return new LinkedHashMap<>();
		}

		@SuppressWarnings("unchecked")
		Map<String, List<IModel>> fragmentCollection = (Map<String, List<IModel>>) self.getVariable(FRAGMENT_COLLECTION_KEY);
		return fragmentCollection != null ? fragmentCollection : new LinkedHashMap<>();
	}

	/**
	 * Set a fragment cache to contain any existing fragments, plus the given
	 * new fragments, with the same scope as setting a local variable.
	 *
	 * @param self
	 * @param context
	 * @param fragments     The new fragments to add to the cache.
	 * @param fromDecorator Whether the call was from {@code DecorateProcessor},
	 *                      used for determining if a new fragment collection should be used and the
	 *                      order of collected fragments.
	 */
	public static void setLocalFragmentCollection(IElementModelStructureHandler self, ITemplateContext context,
																								Map<String, List<IModel>> fragments, boolean fromDecorator) {
		Map<String, List<IModel>> old = getFragmentCollection(context, fromDecorator);
		for (Map.Entry<String, List<IModel>> entry : old.entrySet()) {
			String fragmentName = entry.getKey();
			List<IModel> fragmentList = entry.getValue();

			List<IModel> oldList = fragments.get(fragmentName);
			if (oldList == null || oldList.isEmpty()) {
				fragments.put(fragmentName, fragmentList);
			} else {
				List<IModel> list = new ArrayList<>(oldList.size() + fragmentList.size());
				list.addAll(oldList);
				list.addAll(fragmentList);
				if (!fromDecorator) {
					Collections.reverse(list);
				}
				fragments.put(fragmentName, list);
			}
		}
		self.setLocalVariable(FRAGMENT_COLLECTION_KEY, fragments);
	}

	public static Map<String, List<IModel>> getFragmentCollection(ITemplateContext self) {
		return getFragmentCollection(self, false);
	}

	public static void setLocalFragmentCollection(IElementModelStructureHandler self, ITemplateContext context,
																								Map<String, List<IModel>> fragments) {
		setLocalFragmentCollection(self, context, fragments, false);
	}

}
