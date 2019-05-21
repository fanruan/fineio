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

import com.fineio.v3.cache.core.factory.Feature;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;
import java.util.Set;

import static java.util.Objects.requireNonNull;

/**
 * @author ben.manes@gmail.com (Ben Manes)
 */
public final class NodeContext {
  public final boolean isFinal;
  public final String className;
  public final TypeName superClass;
  public final Set<Feature> parentFeatures;
  public final Set<Feature> generateFeatures;

  public TypeSpec.Builder nodeSubtype;
  public MethodSpec.Builder constructorByKey;
  public MethodSpec.Builder constructorByKeyRef;
  public MethodSpec. Builder constructorDefault;

  @SuppressWarnings("NullAway.Init")
  public NodeContext(TypeName superClass, String className, boolean isFinal,
      Set<Feature> parentFeatures, Set<Feature> generateFeatures) {
    this.isFinal = isFinal;
    this.className = requireNonNull(className);
    this.superClass = requireNonNull(superClass);
    this.parentFeatures = requireNonNull(parentFeatures);
    this.generateFeatures = requireNonNull(generateFeatures);
  }

  public Modifier[] publicFinalModifiers() {
    return isFinal
        ? new Modifier[] { Modifier.PUBLIC }
        : new Modifier[] { Modifier.PUBLIC, Modifier.FINAL };
  }
}
