/*
 * Copyright 2015 the original author or authors.
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

package org.gradle.platform.base.internal.rules;

import com.google.common.collect.Maps;
import org.gradle.api.GradleException;
import org.gradle.api.NamedDomainObjectFactory;
import org.gradle.api.internal.DynamicTypesNamedEntityInstantiator;
import org.gradle.model.internal.core.rule.describe.ModelRuleDescriptor;

import java.util.Map;

public class DefaultRuleAwareDynamicTypesNamedEntityInstantiator<T> implements RuleAwareDynamicTypesNamedEntityInstantiator<T> {

    private final Map<Class<? extends T>, ModelRuleDescriptor> creators = Maps.newHashMap();
    private final DynamicTypesNamedEntityInstantiator<T> instantiator;

    public DefaultRuleAwareDynamicTypesNamedEntityInstantiator(DynamicTypesNamedEntityInstantiator<T> instantiator) {
        this.instantiator = instantiator;
    }

    @Override
    public <S extends T> S create(String name, Class<S> type) {
        return instantiator.create(name, type);
    }

    @Override
    public <U extends T> void registerFactory(Class<U> type, NamedDomainObjectFactory<? extends U> factory, ModelRuleDescriptor descriptor) {
        checkCanRegister(type, descriptor);
        instantiator.registerFactory(type, factory);
    }

    private void checkCanRegister(Class<? extends T> type, ModelRuleDescriptor descriptor) {
        ModelRuleDescriptor creator = creators.get(type);
        if (creator != null) {
            StringBuilder builder = new StringBuilder("Cannot register a factory for type ")
                    .append(type.getSimpleName())
                    .append(" because a factory for this type was already registered by ");
            creator.describeTo(builder);
            builder.append(".");
            throw new GradleException(builder.toString());
        }
        creators.put(type, descriptor);
    }
}
