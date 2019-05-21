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
import com.fineio.v3.cache.core.MpscGrowableArrayQueue;
import com.fineio.v3.cache.core.Node;
import com.fineio.v3.cache.core.Ticker;
import com.fineio.v3.cache.core.WriteOrderDeque;
import java.lang.Runnable;
import java.lang.SuppressWarnings;

/**
 * <em>WARNING: GENERATED CODE</em>
 *
 * A cache that provides the following features:
 * <ul>
 *   <li>ExpireWrite
 *   <li>WeakKeys (inherited)
 *   <li>StrongValues (inherited)
 * </ul>
 *
 * @author ben.manes@gmail.com (Ben Manes)
 */
@SuppressWarnings({"unchecked", "MissingOverride", "NullAway"})
class WSW<K, V> extends WS<K, V> {
  final Ticker ticker;

  final WriteOrderDeque<Node<K, V>> writeOrderDeque;

  volatile long expiresAfterWriteNanos;

  final MpscGrowableArrayQueue<Runnable> writeBuffer;

  WSW(Caffeine<K, V> builder, CacheLoader<? super K, V> cacheLoader, boolean async) {
    super(builder, cacheLoader, async);
    this.ticker = builder.getTicker();
    this.writeOrderDeque = new WriteOrderDeque<Node<K, V>>();
    this.expiresAfterWriteNanos = builder.getExpiresAfterWriteNanos();
    this.writeBuffer = new MpscGrowableArrayQueue<>(WRITE_BUFFER_MIN, WRITE_BUFFER_MAX);
  }

  public final Ticker expirationTicker() {
    return ticker;
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

  protected final MpscGrowableArrayQueue<Runnable> writeBuffer() {
    return writeBuffer;
  }

  protected final boolean buffersWrites() {
    return true;
  }
}
