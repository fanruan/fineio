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
package com.fineio.v3.cache.core.factory.node;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;

import javax.lang.model.element.Modifier;
import java.lang.ref.Reference;
import java.util.Objects;

import static com.fineio.v3.cache.core.factory.Specifications.UNSAFE_ACCESS;
import static com.fineio.v3.cache.core.factory.Specifications.newFieldOffset;
import static com.fineio.v3.cache.core.factory.Specifications.offsetName;
import static com.fineio.v3.cache.core.factory.Specifications.vRefQueueType;
import static com.fineio.v3.cache.core.factory.Specifications.vTypeVar;

/**
 * Adds the value to the node.
 *
 * @author ben.manes@gmail.com (Ben Manes)
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public final class AddValue extends NodeRule {

  @Override
  protected boolean applies() {
    return isBaseClass();
  }

  @Override
  protected void execute() {
    context.nodeSubtype
        .addField(newFieldOffset(context.className, "value"))
        .addField(newValueField())
        .addMethod(newGetter(valueStrength(), vTypeVar, "value", Visibility.LAZY))
        .addMethod(newGetRef("value"))
        .addMethod(makeSetValue())
        .addMethod(makeContainsValue());
  }

  private FieldSpec newValueField() {
    FieldSpec.Builder fieldSpec = isStrongValues()
        ? FieldSpec.builder(vTypeVar, "value", Modifier.VOLATILE)
        : FieldSpec.builder(valueReferenceType(), "value", Modifier.VOLATILE);
    return fieldSpec.build();
  }

  /** Creates the setValue method. */
  private MethodSpec makeSetValue() {
    MethodSpec.Builder setter = MethodSpec.methodBuilder("setValue")
        .addModifiers(context.publicFinalModifiers())
        .addParameter(vTypeVar, "value")
        .addParameter(vRefQueueType, "referenceQueue");

    if (isStrongValues()) {
      setter.addStatement("$T.UNSAFE.putObject(this, $N, $N)",
          UNSAFE_ACCESS, offsetName("value"), "value");
    } else {
      setter.addStatement("(($T<V>) getValueReference()).clear()", Reference.class);
      setter.addStatement("$T.UNSAFE.putObject(this, $N, new $T($L, $N, referenceQueue))",
          UNSAFE_ACCESS, offsetName("value"), valueReferenceType(), "getKeyReference()", "value");
    }

    return setter.build();
  }

  private MethodSpec makeContainsValue() {
    MethodSpec.Builder containsValue = MethodSpec.methodBuilder("containsValue")
        .addModifiers(context.publicFinalModifiers())
        .addParameter(Object.class, "value")
        .returns(boolean.class);
    if (isStrongValues()) {
      containsValue.addStatement("return $T.equals(value, getValue())", Objects.class);
    } else {
      containsValue.addStatement("return getValue() == value");
    }
    return containsValue.build();
  }
}
