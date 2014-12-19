/*
 * Copyright 2014 the original author or authors.
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

package org.gradle.platform.base.internal.util;

import org.apache.commons.lang.StringUtils;
import org.gradle.model.internal.type.ModelType;
import org.gradle.platform.base.InvalidModelException;
import org.gradle.platform.base.internal.builder.TypeBuilderInternal;

public class ImplementationTypeDetermer<T, U extends T> {


    private final ModelType<U> baseImplementation;
    private String modelName;

    public ImplementationTypeDetermer(String modelName, Class<U> baseImplementation) {
        this.modelName = modelName;
        this.baseImplementation = ModelType.of(baseImplementation);
    }

    public ModelType<? extends U> determineImplementationType(ModelType<? extends T> type, TypeBuilderInternal<? extends T> builder) {
        Class<? extends T> implementation = builder.getDefaultImplementation();
        if (implementation == null) {
            return null;
        }

        ModelType<? extends T> implementationType = ModelType.of(implementation);
        ModelType<? extends U> asSubclass = baseImplementation.asSubclass(implementationType);

        if (asSubclass == null) {
            throw new InvalidModelException(String.format("%s implementation '%s' must extend '%s'.", StringUtils.capitalize(modelName), implementationType, baseImplementation));
        }

        if (!type.isAssignableFrom(asSubclass)) {
            throw new InvalidModelException(String.format("%s implementation '%s' must implement '%s'.", StringUtils.capitalize(modelName), asSubclass, type));
        }

        try {
            asSubclass.getRawClass().getConstructor();
        } catch (NoSuchMethodException nsmException) {
            throw new InvalidModelException(String.format("%s implementation '%s' must have public default constructor.", StringUtils.capitalize(modelName), asSubclass));
        }

        return asSubclass;
    }
//
//    protected <R> void assertIsVoidMethod(MethodRuleDefinition<R> ruleDefinition) {
//        if (!ModelType.of(Void.TYPE).equals(ruleDefinition.getReturnType())) {
//            throw new InvalidModelException(String.format("Method %s must not have a return value.", getDescription()));
//        }
//    }

//    protected ModelType<? extends T> readType(MethodRuleDefinition<?> ruleDefinition, ) {
//        assertIsVoidMethod(ruleDefinition);
//        if (ruleDefinition.getReferences().size() != 1) {
//            throw new InvalidModelException(String.format("Method %s must have a single parameter of type '%s'.", getDescription(), builderInterface.toString()));
//        }
//        ModelType<?> builder = ruleDefinition.getReferences().get(0).getType();
//        if (!builderInterface.isAssignableFrom(builder)) {
//            throw new InvalidModelException(String.format("Method %s must have a single parameter of type '%s'.", getDescription(), builderInterface.toString()));
//        }
//        if (builder.getTypeVariables().size() != 1) {
//            throw new InvalidModelException(String.format("Parameter of type '%s' must declare a type parameter.", builderInterface.toString()));
//        }
//        ModelType<?> subType = builder.getTypeVariables().get(0);
//
//        if (subType.isWildcard()) {
//            throw new InvalidModelException(String.format("%s type '%s' cannot be a wildcard type (i.e. cannot use ? super, ? extends etc.).", StringUtils.capitalize(modelName), subType.toString()));
//        }
//
//        ModelType<? extends T> asSubclass = baseInterface.asSubclass(subType);
//        if (asSubclass == null) {
//            throw new InvalidModelException(String.format("%s type '%s' is not a subtype of '%s'.", StringUtils.capitalize(modelName), subType.toString(), baseInterface.toString()));
//        }
//
//        return asSubclass;
//    }
}
