/*
 * Copyright 2014 Ben Manes. All Rights Reserved.
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
package com.fineio.v3.cache.core;

import com.fineio.v3.cache.core.testing.CacheContext;
import com.fineio.v3.cache.core.testing.CacheProvider;
import com.fineio.v3.cache.core.testing.CacheSpec;
import com.fineio.v3.cache.core.testing.CacheSpec.CacheWeigher;
import com.fineio.v3.cache.core.testing.CacheSpec.Expire;
import com.fineio.v3.cache.core.testing.CacheSpec.Implementation;
import com.fineio.v3.cache.core.testing.CacheSpec.Maximum;
import com.fineio.v3.cache.core.testing.CacheSpec.Population;
import com.fineio.v3.cache.core.testing.CacheSpec.ReferenceType;
import com.fineio.v3.cache.core.testing.CacheValidationListener;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * The test cases for the implementation details of {@link UnboundedLocalCache}.
 *
 * @author ben.manes@gmail.com (Ben Manes)
 */
@Listeners(CacheValidationListener.class)
@Test(dataProviderClass = CacheProvider.class)
public final class UnboundedLocalCacheTest {

    @CacheSpec(implementation = Implementation.Caffeine, population = Population.EMPTY,
            maximumSize = Maximum.DISABLED, weigher = CacheWeigher.DEFAULT,
            expireAfterAccess = Expire.DISABLED, expireAfterWrite = Expire.DISABLED,
            refreshAfterWrite = Expire.DISABLED, keys = ReferenceType.STRONG,
            values = ReferenceType.STRONG)
    @Test(dataProvider = "caches")
    public void noPolicy(Cache<Integer, Integer> cache, CacheContext context) {
        assertThat(cache.policy().eviction(), is(Optional.empty()));
        assertThat(cache.policy().expireAfterWrite(), is(Optional.empty()));
        assertThat(cache.policy().expireAfterAccess(), is(Optional.empty()));
        assertThat(cache.policy().refreshAfterWrite(), is(Optional.empty()));
    }

    @CacheSpec(implementation = Implementation.Caffeine, population = Population.EMPTY,
            maximumSize = Maximum.DISABLED, weigher = CacheWeigher.DEFAULT,
            expireAfterAccess = Expire.DISABLED, expireAfterWrite = Expire.DISABLED,
            refreshAfterWrite = Expire.DISABLED, keys = ReferenceType.STRONG,
            values = ReferenceType.STRONG)
    @Test(dataProvider = "caches")
    public void noPolicy_async(AsyncLoadingCache<Integer, Integer> cache, CacheContext context) {
        assertThat(cache.synchronous().policy().eviction(), is(Optional.empty()));
        assertThat(cache.synchronous().policy().expireAfterWrite(), is(Optional.empty()));
        assertThat(cache.synchronous().policy().expireAfterAccess(), is(Optional.empty()));
        assertThat(cache.synchronous().policy().refreshAfterWrite(), is(Optional.empty()));
    }
}
