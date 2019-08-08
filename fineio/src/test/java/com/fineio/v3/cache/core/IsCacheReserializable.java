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
package com.fineio.v3.cache.core;

import com.fineio.v3.cache.core.Async.AsyncExpiry;
import com.fineio.v3.cache.core.Async.AsyncWeigher;
import com.fineio.v3.cache.core.BoundedLocalCache.BoundedLocalAsyncLoadingCache;
import com.fineio.v3.cache.core.BoundedLocalCache.BoundedLocalLoadingCache;
import com.fineio.v3.cache.core.BoundedLocalCache.BoundedLocalManualCache;
import com.fineio.v3.cache.core.LocalAsyncLoadingCache.LoadingCacheView;
import com.fineio.v3.cache.core.UnboundedLocalCache.UnboundedLocalAsyncLoadingCache;
import com.fineio.v3.cache.core.UnboundedLocalCache.UnboundedLocalLoadingCache;
import com.fineio.v3.cache.core.UnboundedLocalCache.UnboundedLocalManualCache;
import com.fineio.v3.cache.testing.DescriptionBuilder;
import com.google.common.testing.SerializableTester;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

/**
 * A matcher that evaluates a cache by creating a serialized copy and checking its equality.
 *
 * @author ben.manes@gmail.com (Ben Manes)
 */
public final class IsCacheReserializable<T> extends TypeSafeDiagnosingMatcher<T> {
    DescriptionBuilder desc;

    private IsCacheReserializable() {
    }

    private static <K, V> void checkAsynchronousCache(AsyncLoadingCache<K, V> original,
                                                      AsyncLoadingCache<K, V> copy, DescriptionBuilder desc) {
        if (!IsValidAsyncCache.<K, V>validAsyncCache().matchesSafely(copy, desc.getDescription())) {
            desc.expected("valid async cache");
        } else if (original instanceof UnboundedLocalAsyncLoadingCache<?, ?>) {
            checkUnboundedAsyncLocalLoadingCache(
                    (UnboundedLocalAsyncLoadingCache<K, V>) original,
                    (UnboundedLocalAsyncLoadingCache<K, V>) copy, desc);
        } else if (original instanceof BoundedLocalAsyncLoadingCache<?, ?>) {
            checkBoundedAsyncLocalLoadingCache(
                    (BoundedLocalAsyncLoadingCache<K, V>) original,
                    (BoundedLocalAsyncLoadingCache<K, V>) copy, desc);
        }
    }

    private static <K, V> void checkSynchronousCache(Cache<K, V> original, Cache<K, V> copy,
                                                     DescriptionBuilder desc) {
        if (!IsValidCache.<K, V>validCache().matchesSafely(copy, desc.getDescription())) {
            desc.expected("valid cache");
            return;
        }

        checkIfUnbounded(original, copy, desc);
        checkIfBounded(original, copy, desc);
    }

    @SuppressWarnings("unchecked")
    private static <K, V> void checkIfUnbounded(
            Cache<K, V> original, Cache<K, V> copy, DescriptionBuilder desc) {
        if (original instanceof UnboundedLocalManualCache<?, ?>) {
            checkUnboundedLocalManualCache(
                    (UnboundedLocalManualCache<K, V>) original,
                    (UnboundedLocalManualCache<K, V>) copy, desc);
        }
        if (original instanceof UnboundedLocalLoadingCache<?, ?>) {
            checkUnboundedLocalLoadingCache(
                    (UnboundedLocalLoadingCache<K, V>) original,
                    (UnboundedLocalLoadingCache<K, V>) copy, desc);
        }
        if (original instanceof LoadingCacheView<?, ?>) {
            LocalAsyncLoadingCache<?, ?> originalAsync = ((LoadingCacheView<?, ?>) original).asyncCache();
            LocalAsyncLoadingCache<?, ?> copyAsync = ((LoadingCacheView<?, ?>) copy).asyncCache();
            if (originalAsync instanceof UnboundedLocalAsyncLoadingCache<?, ?>) {
                checkUnboundedAsyncLocalLoadingCache(
                        (UnboundedLocalAsyncLoadingCache<K, V>) originalAsync,
                        (UnboundedLocalAsyncLoadingCache<K, V>) copyAsync, desc);
            }
        }
    }

    private static <K, V> void checkUnboundedLocalManualCache(UnboundedLocalManualCache<K, V> original,
                                                              UnboundedLocalManualCache<K, V> copy, DescriptionBuilder desc) {
        checkUnboundedLocalCache(original.cache, copy.cache, desc);
    }

    /* --------------- Unbounded --------------- */

    private static <K, V> void checkUnboundedLocalLoadingCache(
            UnboundedLocalLoadingCache<K, V> original,
            UnboundedLocalLoadingCache<K, V> copy, DescriptionBuilder desc) {
        desc.expectThat("same cacheLoader", copy.loader, is(original.loader));
    }

    private static <K, V> void checkUnboundedAsyncLocalLoadingCache(
            UnboundedLocalAsyncLoadingCache<K, V> original,
            UnboundedLocalAsyncLoadingCache<K, V> copy, DescriptionBuilder desc) {
        checkUnboundedLocalCache(original.cache, copy.cache, desc);
        desc.expectThat("same cacheLoader", copy.loader, is(original.loader));
    }

    private static <K, V> void checkUnboundedLocalCache(UnboundedLocalCache<K, V> original,
                                                        UnboundedLocalCache<K, V> copy, DescriptionBuilder desc) {
        desc.expectThat("estimated empty", copy.estimatedSize(), is(0L));
        desc.expectThat("same ticker", copy.ticker, is(original.ticker));
        desc.expectThat("same writer", copy.writer, is(original.writer));
        desc.expectThat("same isRecordingStats",
                copy.isRecordingStats, is(original.isRecordingStats));

        if (original.removalListener == null) {
            desc.expectThat("same removalListener", copy.removalListener, is(nullValue()));
        } else if (copy.removalListener == null) {
            desc.expected("non-null removalListener");
        } else if (copy.removalListener.getClass() != original.removalListener.getClass()) {
            desc.expected("same removalListener but was " + copy.removalListener.getClass());
        }
    }

    @SuppressWarnings("unchecked")
    private static <K, V> void checkIfBounded(
            Cache<K, V> original, Cache<K, V> copy, DescriptionBuilder desc) {
        if (original instanceof BoundedLocalManualCache<?, ?>) {
            checkBoundedLocalManualCache(
                    (BoundedLocalManualCache<K, V>) original,
                    (BoundedLocalManualCache<K, V>) copy, desc);
        }
        if (original instanceof BoundedLocalLoadingCache<?, ?>) {
            checkBoundedLocalLoadingCache(
                    (BoundedLocalLoadingCache<K, V>) original,
                    (BoundedLocalLoadingCache<K, V>) copy, desc);
        }
        if (original instanceof LoadingCacheView) {
            LocalAsyncLoadingCache<?, ?> originalAsync = ((LoadingCacheView<K, V>) original).asyncCache();
            LocalAsyncLoadingCache<?, ?> copyAsync = ((LoadingCacheView<K, V>) copy).asyncCache();
            if (originalAsync instanceof BoundedLocalAsyncLoadingCache<?, ?>) {
                checkBoundedAsyncLocalLoadingCache(
                        (BoundedLocalAsyncLoadingCache<K, V>) originalAsync,
                        (BoundedLocalAsyncLoadingCache<K, V>) copyAsync, desc);
            }
        }
    }

    private static <K, V> void checkBoundedLocalManualCache(BoundedLocalManualCache<K, V> original,
                                                            BoundedLocalManualCache<K, V> copy, DescriptionBuilder desc) {
        checkBoundedLocalCache(original.cache, copy.cache, desc);
    }

    /* --------------- Bounded --------------- */

    private static <K, V> void checkBoundedLocalLoadingCache(BoundedLocalLoadingCache<K, V> original,
                                                             BoundedLocalLoadingCache<K, V> copy, DescriptionBuilder desc) {
        desc.expectThat("same cacheLoader", copy.cache.cacheLoader, is(original.cache.cacheLoader));
    }

    private static <K, V> void checkBoundedAsyncLocalLoadingCache(
            BoundedLocalAsyncLoadingCache<K, V> original,
            BoundedLocalAsyncLoadingCache<K, V> copy, DescriptionBuilder desc) {
        checkBoundedLocalCache(original.cache, copy.cache, desc);
        desc.expectThat("same cacheLoader", copy.loader, is(original.loader));
    }

    private static <K, V> void checkBoundedLocalCache(BoundedLocalCache<K, V> original,
                                                      BoundedLocalCache<K, V> copy, DescriptionBuilder desc) {
        desc.expectThat("empty", copy.estimatedSize(), is(0L));
        desc.expectThat("same weigher", unwrapWeigher(copy.weigher).getClass(),
                is(equalTo(unwrapWeigher(original.weigher).getClass())));
        desc.expectThat("same nodeFactory",
                copy.nodeFactory, instanceOf(original.nodeFactory.getClass()));
        if (original.evicts()) {
            desc.expectThat("same maximumWeight", copy.maximum(), is(original.maximum()));
            desc.expectThat("same maximumwindowWeight",
                    copy.windowMaximum(), is(original.windowMaximum()));
        }

        if (original.expiresVariable()) {
            desc.expectThat("same expiry", unwrapExpiry(copy.expiry()).getClass(),
                    is(equalTo(unwrapExpiry(original.expiry()).getClass())));
        } else {
            desc.expectThat("", copy.expiresVariable(), is(false));
        }

        if (original.expiresAfterAccess()) {
            desc.expectThat("same expiresAfterAccessNanos",
                    copy.expiresAfterAccessNanos(), is(original.expiresAfterAccessNanos()));
        } else {
            desc.expectThat("", copy.expiresAfterAccess(), is(false));
        }

        if (original.expiresAfterWrite()) {
            desc.expectThat("same expireAfterWriteNanos",
                    copy.expiresAfterWriteNanos(), is(original.expiresAfterWriteNanos()));
        } else {
            desc.expectThat("", copy.expiresAfterWrite(), is(false));
        }

        if (original.refreshAfterWrite()) {
            desc.expectThat("same refreshAfterWriteNanos",
                    copy.refreshAfterWriteNanos(), is(original.refreshAfterWriteNanos()));
        } else {
            desc.expectThat("", copy.refreshAfterWrite(), is(false));
        }

        if (original.removalListener() == null) {
            desc.expectThat("same removalListener", copy.removalListener(), is(nullValue()));
        } else if (copy.removalListener() == null) {
            desc.expected("non-null removalListener");
        } else if (copy.removalListener().getClass() != original.removalListener().getClass()) {
            desc.expected("same removalListener but was " + copy.removalListener().getClass());
        }
    }

    @SuppressWarnings("unchecked")
    private static <K, V> Weigher<K, V> unwrapWeigher(Weigher<K, V> weigher) {
        for (; ; ) {
            if (weigher instanceof BoundedWeigher<?, ?>) {
                weigher = (Weigher<K, V>) ((BoundedWeigher<?, ?>) weigher).delegate;
            } else if (weigher instanceof AsyncWeigher<?, ?>) {
                weigher = (Weigher<K, V>) ((AsyncWeigher<?, ?>) weigher).delegate;
            } else {
                return weigher;
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static <K, V> Expiry<K, V> unwrapExpiry(Expiry<K, V> expiry) {
        for (; ; ) {
            if (expiry instanceof AsyncExpiry<?, ?>) {
                expiry = (Expiry<K, V>) ((AsyncExpiry<?, ?>) expiry).delegate;
            } else {
                return expiry;
            }
        }
    }

    public static <T> Matcher<T> reserializable() {
        return new IsCacheReserializable<T>();
    }

    @Override
    public void describeTo(Description description) {
        description.appendValue("serialized copy");
        if (desc.getDescription() != description) {
            description.appendText(desc.getDescription().toString());
        }
    }

    @Override
    public boolean matchesSafely(T original, Description description) {
        desc = new DescriptionBuilder(description);

        T copy = SerializableTester.reserialize(original);

        if (original instanceof AsyncLoadingCache<?, ?>) {
            @SuppressWarnings("unchecked")
            AsyncLoadingCache<Object, Object> asyncCache = (AsyncLoadingCache<Object, Object>) original;
            @SuppressWarnings("unchecked")
            AsyncLoadingCache<Object, Object> asyncCopy = (AsyncLoadingCache<Object, Object>) copy;
            checkAsynchronousCache(asyncCache, asyncCopy, desc);
        } else if (original instanceof Cache<?, ?>) {
            @SuppressWarnings("unchecked")
            Cache<Object, Object> syncCache = (Cache<Object, Object>) original;
            @SuppressWarnings("unchecked")
            Cache<Object, Object> syncCopy = (Cache<Object, Object>) copy;
            checkSynchronousCache(syncCache, syncCopy, desc);
        } else {
            throw new UnsupportedOperationException();
        }

        return desc.matches();
    }
}
