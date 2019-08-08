/*
 * Copyright 2015 Ben Manes. All Rights Reserved.
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
package com.fineio.v3.cache.core.factory.local;

import com.fineio.v3.cache.core.factory.Feature;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;

import javax.lang.model.element.Modifier;

import static com.fineio.v3.cache.core.factory.Specifications.REMOVAL_LISTENER;

/**
 * @author ben.manes@gmail.com (Ben Manes)
 */
public final class AddRemovalListener extends LocalCacheRule {

    @Override
    protected boolean applies() {
        return context.generateFeatures.contains(Feature.LISTENING);
    }

    @Override
    protected void execute() {
        context.cache.addField(
                FieldSpec.builder(REMOVAL_LISTENER, "removalListener", Modifier.FINAL).build());
        context.constructor.addStatement("this.removalListener = builder.getRemovalListener(async)");
        context.cache.addMethod(MethodSpec.methodBuilder("removalListener")
                .addModifiers(context.publicFinalModifiers())
                .addStatement("return removalListener")
                .returns(REMOVAL_LISTENER)
                .build());
        context.cache.addMethod(MethodSpec.methodBuilder("hasRemovalListener")
                .addModifiers(context.publicFinalModifiers())
                .addStatement("return true")
                .returns(boolean.class)
                .build());
    }
}
