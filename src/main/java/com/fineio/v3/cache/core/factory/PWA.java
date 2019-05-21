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
import java.lang.Object;
import java.lang.SuppressWarnings;
import java.lang.ref.ReferenceQueue;

/**
 * <em>WARNING: GENERATED CODE</em>
 *
 * A cache entry that provides the following features:
 * <ul>
 *   <li>ExpireAccess
 *   <li>StrongKeys (inherited)
 *   <li>WeakValues (inherited)
 * </ul>
 *
 * @author ben.manes@gmail.com (Ben Manes)
 */
@SuppressWarnings({"unchecked", "PMD.UnusedFormalParameter", "MissingOverride", "NullAway"})
class PWA<K, V> extends PW<K, V> {
  protected static final long ACCESS_TIME_OFFSET = UnsafeAccess.objectFieldOffset(PWA.class, LocalCacheFactory.ACCESS_TIME);

  volatile long accessTime;

  Node<K, V> previousInAccessOrder;

  Node<K, V> nextInAccessOrder;

  PWA() {
  }

  PWA(K key, ReferenceQueue<K> keyReferenceQueue, V value, ReferenceQueue<V> valueReferenceQueue, int weight, long now) {
    super(key, keyReferenceQueue, value, valueReferenceQueue, weight, now);
    UnsafeAccess.UNSAFE.putLong(this, ACCESS_TIME_OFFSET, now);
  }

  PWA(Object keyReference, V value, ReferenceQueue<V> valueReferenceQueue, int weight, long now) {
    super(keyReference, value, valueReferenceQueue, weight, now);
    UnsafeAccess.UNSAFE.putLong(this, ACCESS_TIME_OFFSET, now);
  }

  public Node<K, V> getPreviousInVariableOrder() {
    return previousInAccessOrder;
  }

  public void setPreviousInVariableOrder(Node<K, V> previousInAccessOrder) {
    this.previousInAccessOrder = previousInAccessOrder;
  }

  public Node<K, V> getNextInVariableOrder() {
    return nextInAccessOrder;
  }

  public void setNextInVariableOrder(Node<K, V> nextInAccessOrder) {
    this.nextInAccessOrder = nextInAccessOrder;
  }

  public long getVariableTime() {
    return UnsafeAccess.UNSAFE.getLong(this, ACCESS_TIME_OFFSET);
  }

  public void setVariableTime(long accessTime) {
    UnsafeAccess.UNSAFE.putLong(this, ACCESS_TIME_OFFSET, accessTime);
  }

  public boolean casVariableTime(long expect, long update) {
    return (accessTime == expect)
        && UnsafeAccess.UNSAFE.compareAndSwapLong(this, ACCESS_TIME_OFFSET, expect, update);
  }

  public final long getAccessTime() {
    return UnsafeAccess.UNSAFE.getLong(this, ACCESS_TIME_OFFSET);
  }

  public final void setAccessTime(long accessTime) {
    UnsafeAccess.UNSAFE.putLong(this, ACCESS_TIME_OFFSET, accessTime);
  }

  public final Node<K, V> getPreviousInAccessOrder() {
    return previousInAccessOrder;
  }

  public final void setPreviousInAccessOrder(Node<K, V> previousInAccessOrder) {
    this.previousInAccessOrder = previousInAccessOrder;
  }

  public final Node<K, V> getNextInAccessOrder() {
    return nextInAccessOrder;
  }

  public final void setNextInAccessOrder(Node<K, V> nextInAccessOrder) {
    this.nextInAccessOrder = nextInAccessOrder;
  }

  public Node<K, V> newNode(K key, ReferenceQueue<K> keyReferenceQueue, V value, ReferenceQueue<V> valueReferenceQueue, int weight, long now) {
    return new PWA<>(key, keyReferenceQueue, value, valueReferenceQueue, weight, now);
  }

  public Node<K, V> newNode(Object keyReference, V value, ReferenceQueue<V> valueReferenceQueue, int weight, long now) {
    return new PWA<>(keyReference, value, valueReferenceQueue, weight, now);
  }
}
