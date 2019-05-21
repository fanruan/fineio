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

import static com.fineio.v3.cache.core.factory.Specifications.TICKER;

/**
 * @author ben.manes@gmail.com (Ben Manes)
 */
public final class AddExpirationTicker extends LocalCacheRule {

  @Override
  protected boolean applies() {
    return !(Feature.usesExpirationTicker(context.parentFeatures)
        || !Feature.usesExpirationTicker(context.generateFeatures));
  }

  @Override
  protected void execute() {
    context.constructor.addStatement("this.ticker = builder.getTicker()");
    context.cache.addField(FieldSpec.builder(TICKER, "ticker", Modifier.FINAL).build());
    context.cache.addMethod(MethodSpec.methodBuilder("expirationTicker")
        .addModifiers(context.publicFinalModifiers())
        .addStatement("return ticker")
        .returns(TICKER)
        .build());
  }
}
