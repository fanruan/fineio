// Copyright 2019 Ben Manes. All Rights Reserved.
// 
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// 
//     http://www.apache.org/licenses/LICENSE-2.0
// 
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.fineio.v3.cache.core.factory;

import com.fineio.v3.cache.base.UnsafeAccess;
import com.fineio.v3.cache.core.Node;
import com.fineio.v3.cache.core.References.SoftValueReference;
import java.lang.Object;
import java.lang.SuppressWarnings;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;

/**
 * <em>WARNING: GENERATED CODE</em>
 *
 * A cache entry that provides the following features:
 * <ul>
 *   <li>StrongKeys
 *   <li>SoftValues
 * </ul>
 *
 * @author ben.manes@gmail.com (Ben Manes)
 */
@SuppressWarnings({"unchecked", "PMD.UnusedFormalParameter", "MissingOverride", "NullAway"})
class PD<K, V> extends Node<K, V> implements NodeFactory<K, V> {
  protected static final long KEY_OFFSET = UnsafeAccess.objectFieldOffset(PD.class, LocalCacheFactory.KEY);

  protected static final long VALUE_OFFSET = UnsafeAccess.objectFieldOffset(PD.class, LocalCacheFactory.VALUE);

  volatile K key;

  volatile SoftValueReference<V> value;

  PD() {
  }

  PD(K key, ReferenceQueue<K> keyReferenceQueue, V value, ReferenceQueue<V> valueReferenceQueue, int weight, long now) {
    this(key, value, valueReferenceQueue, weight, now);
  }

  PD(Object keyReference, V value, ReferenceQueue<V> valueReferenceQueue, int weight, long now) {
    UnsafeAccess.UNSAFE.putObject(this, KEY_OFFSET, keyReference);
    UnsafeAccess.UNSAFE.putObject(this, VALUE_OFFSET, new SoftValueReference<V>(keyReference, value, valueReferenceQueue));
  }

  public final K getKey() {
    return (K) UnsafeAccess.UNSAFE.getObject(this, KEY_OFFSET);
  }

  public final Object getKeyReference() {
    return UnsafeAccess.UNSAFE.getObject(this, KEY_OFFSET);
  }

  public final V getValue() {
    return ((Reference<V>) UnsafeAccess.UNSAFE.getObject(this, VALUE_OFFSET)).get();
  }

  public final Object getValueReference() {
    return UnsafeAccess.UNSAFE.getObject(this, VALUE_OFFSET);
  }

  public final void setValue(V value, ReferenceQueue<V> referenceQueue) {
    ((Reference<V>) getValueReference()).clear();
    UnsafeAccess.UNSAFE.putObject(this, VALUE_OFFSET, new SoftValueReference<V>(getKeyReference(), value, referenceQueue));
  }

  public final boolean containsValue(Object value) {
    return getValue() == value;
  }

  public Node<K, V> newNode(K key, ReferenceQueue<K> keyReferenceQueue, V value, ReferenceQueue<V> valueReferenceQueue, int weight, long now) {
    return new PD<>(key, keyReferenceQueue, value, valueReferenceQueue, weight, now);
  }

  public Node<K, V> newNode(Object keyReference, V value, ReferenceQueue<V> valueReferenceQueue, int weight, long now) {
    return new PD<>(keyReference, value, valueReferenceQueue, weight, now);
  }

  public boolean softValues() {
    return true;
  }

  public final boolean isAlive() {
    Object key = getKeyReference();
    return (key != RETIRED_STRONG_KEY) && (key != DEAD_STRONG_KEY);
  }

  public final boolean isRetired() {
    return (getKeyReference() == RETIRED_STRONG_KEY);
  }

  public final void retire() {
    ((Reference<V>) getValueReference()).clear();
    UnsafeAccess.UNSAFE.putObject(this, KEY_OFFSET, RETIRED_STRONG_KEY);
  }

  public final boolean isDead() {
    return (getKeyReference() == DEAD_STRONG_KEY);
  }

  public final void die() {
    ((Reference<V>) getValueReference()).clear();
    UnsafeAccess.UNSAFE.putObject(this, KEY_OFFSET, DEAD_STRONG_KEY);
  }
}
