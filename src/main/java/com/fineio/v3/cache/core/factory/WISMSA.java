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
import com.fineio.v3.cache.core.Expiry;
import com.fineio.v3.cache.core.Ticker;
import com.fineio.v3.cache.core.TimerWheel;
import java.lang.SuppressWarnings;

/**
 * <em>WARNING: GENERATED CODE</em>
 *
 * A cache that provides the following features:
 * <ul>
 *   <li>ExpireAccess
 *   <li>WeakKeys (inherited)
 *   <li>InfirmValues (inherited)
 *   <li>Stats (inherited)
 *   <li>MaximumSize (inherited)
 * </ul>
 *
 * @author ben.manes@gmail.com (Ben Manes)
 */
@SuppressWarnings({"unchecked", "MissingOverride", "NullAway"})
class WISMSA<K, V> extends WISMS<K, V> {
  final Ticker ticker;

  final Expiry<K, V> expiry;

  final TimerWheel<K, V> timerWheel;

  volatile long expiresAfterAccessNanos;

  WISMSA(Caffeine<K, V> builder, CacheLoader<? super K, V> cacheLoader, boolean async) {
    super(builder, cacheLoader, async);
    this.ticker = builder.getTicker();
    this.expiry = builder.getExpiry(isAsync);
    this.timerWheel = builder.expiresVariable() ? new TimerWheel<K, V>(this) : null;
    this.expiresAfterAccessNanos = builder.getExpiresAfterAccessNanos();
  }

  public final Ticker expirationTicker() {
    return ticker;
  }

  protected final boolean expiresVariable() {
    return (timerWheel != null);
  }

  protected final Expiry<K, V> expiry() {
    return expiry;
  }

  protected final TimerWheel<K, V> timerWheel() {
    return timerWheel;
  }

  protected final boolean expiresAfterAccess() {
    return (timerWheel == null);
  }

  protected final long expiresAfterAccessNanos() {
    return expiresAfterAccessNanos;
  }

  protected final void setExpiresAfterAccessNanos(long expiresAfterAccessNanos) {
    this.expiresAfterAccessNanos = expiresAfterAccessNanos;
  }
}
