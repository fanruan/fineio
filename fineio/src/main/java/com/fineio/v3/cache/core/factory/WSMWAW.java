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

import com.fineio.v3.cache.core.CacheLoader;
import com.fineio.v3.cache.core.Caffeine;
import com.fineio.v3.cache.core.Node;
import com.fineio.v3.cache.core.WriteOrderDeque;

/**
 * <em>WARNING: GENERATED CODE</em>
 * <p>
 * A cache that provides the following features:
 * <ul>
 *   <li>ExpireWrite
 *   <li>WeakKeys (inherited)
 *   <li>StrongValues (inherited)
 *   <li>MaximumWeight (inherited)
 *   <li>ExpireAccess (inherited)
 * </ul>
 *
 * @author ben.manes@gmail.com (Ben Manes)
 */
@SuppressWarnings({"unchecked", "MissingOverride", "NullAway"})
class WSMWAW<K, V> extends WSMWA<K, V> {
    final WriteOrderDeque<Node<K, V>> writeOrderDeque;

    volatile long expiresAfterWriteNanos;

    WSMWAW(Caffeine<K, V> builder, CacheLoader<? super K, V> cacheLoader, boolean async) {
        super(builder, cacheLoader, async);
        this.writeOrderDeque = new WriteOrderDeque<Node<K, V>>();
        this.expiresAfterWriteNanos = builder.getExpiresAfterWriteNanos();
    }

    protected final WriteOrderDeque<Node<K, V>> writeOrderDeque() {
        return writeOrderDeque;
    }

    protected final boolean expiresAfterWrite() {
        return true;
    }

    protected final long expiresAfterWriteNanos() {
        return expiresAfterWriteNanos;
    }

    protected final void setExpiresAfterWriteNanos(long expiresAfterWriteNanos) {
        this.expiresAfterWriteNanos = expiresAfterWriteNanos;
    }
}