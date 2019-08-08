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

/**
 * This package contains caching utilities.
 * <p>
 * The core interface used to represent caches is {@link com.fineio.v3.cache.core.Cache}.
 * A cache may be specialized as either a {@link com.fineio.v3.cache.core.LoadingCache}
 * or {@link com.fineio.v3.cache.core.AsyncLoadingCache}.
 * <p>
 * In-memory caches can be configured and created using
 * {@link com.fineio.v3.cache.core.Caffeine}. The cache entries may be loaded by
 * {@link com.fineio.v3.cache.core.CacheLoader}, weighed by
 * {@link com.fineio.v3.cache.core.Weigher}, and on removal forwarded to
 * {@link com.fineio.v3.cache.core.RemovalListener}. Statistics about cache performance
 * are exposed using {@link com.fineio.v3.cache.core.stats.CacheStats}.
 *
 * @author ben.manes@gmail.com (Ben Manes)
 */
package com.fineio.v3.cache.core;
