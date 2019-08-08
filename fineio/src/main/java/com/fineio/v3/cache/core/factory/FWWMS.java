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

import com.fineio.v3.cache.core.Node;

import java.lang.ref.ReferenceQueue;

/**
 * <em>WARNING: GENERATED CODE</em>
 * <p>
 * A cache entry that provides the following features:
 * <ul>
 *   <li>MaximumSize
 *   <li>WeakKeys (inherited)
 *   <li>WeakValues (inherited)
 *   <li>ExpireWrite (inherited)
 * </ul>
 *
 * @author ben.manes@gmail.com (Ben Manes)
 */
@SuppressWarnings({"unchecked", "PMD.UnusedFormalParameter", "MissingOverride", "NullAway"})
final class FWWMS<K, V> extends FWW<K, V> {
    int queueType;

    Node<K, V> previousInAccessOrder;

    Node<K, V> nextInAccessOrder;

    FWWMS() {
    }

    FWWMS(K key, ReferenceQueue<K> keyReferenceQueue, V value, ReferenceQueue<V> valueReferenceQueue, int weight, long now) {
        super(key, keyReferenceQueue, value, valueReferenceQueue, weight, now);
    }

    FWWMS(Object keyReference, V value, ReferenceQueue<V> valueReferenceQueue, int weight, long now) {
        super(keyReference, value, valueReferenceQueue, weight, now);
    }

    public int getQueueType() {
        return queueType;
    }

    public void setQueueType(int queueType) {
        this.queueType = queueType;
    }

    public Node<K, V> getPreviousInAccessOrder() {
        return previousInAccessOrder;
    }

    public void setPreviousInAccessOrder(Node<K, V> previousInAccessOrder) {
        this.previousInAccessOrder = previousInAccessOrder;
    }

    public Node<K, V> getNextInAccessOrder() {
        return nextInAccessOrder;
    }

    public void setNextInAccessOrder(Node<K, V> nextInAccessOrder) {
        this.nextInAccessOrder = nextInAccessOrder;
    }

    public Node<K, V> newNode(K key, ReferenceQueue<K> keyReferenceQueue, V value, ReferenceQueue<V> valueReferenceQueue, int weight, long now) {
        return new FWWMS<>(key, keyReferenceQueue, value, valueReferenceQueue, weight, now);
    }

    public Node<K, V> newNode(Object keyReference, V value, ReferenceQueue<V> valueReferenceQueue, int weight, long now) {
        return new FWWMS<>(keyReference, value, valueReferenceQueue, weight, now);
    }
}
