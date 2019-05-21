/*
 * Copyright 2018 Ben Manes. All Rights Reserved.
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

import com.fineio.v3.cache.core.stats.CacheStats;


import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.Objects.requireNonNull;

/**
 * This class provides a skeletal implementation of the {@link AsyncCache} interface to minimize the
 * effort required to implement a {@link LocalCache}.
 *
 * @author ben.manes@gmail.com (Ben Manes)
 */
interface LocalAsyncCache<K, V> extends AsyncCache<K, V> {
    Logger logger = Logger.getLogger(LocalAsyncCache.class.getName());

    /**
     * Returns the backing {@link LocalCache} data store.
     */
    LocalCache<K, CompletableFuture<V>> cache();

    /**
     * Returns the policy supported by this implementation and its configuration.
     */
    Policy<K, V> policy();

    @Override
    default CompletableFuture<V> getIfPresent(Object key) {
        return cache().getIfPresent(key, /* recordStats */ true);
    }

    @Override
    default CompletableFuture<V> get(K key,
                                     Function<? super K, ? extends V> mappingFunction) {
        requireNonNull(mappingFunction);
        return get(key, (k1, executor) -> CompletableFuture.supplyAsync(
                () -> mappingFunction.apply(key), executor));
    }

    @Override
    default CompletableFuture<V> get(K key,
                                     BiFunction<? super K, Executor, CompletableFuture<V>> mappingFunction) {
        return get(key, mappingFunction, /* recordStats */ true);
    }

    @SuppressWarnings({"FutureReturnValueIgnored", "NullAway"})
    default CompletableFuture<V> get(K key,
                                     BiFunction<? super K, Executor, CompletableFuture<V>> mappingFunction, boolean recordStats) {
        long startTime = cache().statsTicker().read();
        @SuppressWarnings({"unchecked", "rawtypes"})
        CompletableFuture<V>[] result = new CompletableFuture[1];
        CompletableFuture<V> future = cache().computeIfAbsent(key, k -> {
            result[0] = mappingFunction.apply(key, cache().executor());
            return requireNonNull(result[0]);
        }, recordStats, /* recordLoad */ false);
        if (result[0] != null) {
            handleCompletion(key, result[0], startTime, /* recordMiss */ false);
        }
        return future;
    }

    @Override
    @SuppressWarnings("FutureReturnValueIgnored")
    default void put(K key, CompletableFuture<V> valueFuture) {
        if (valueFuture.isCompletedExceptionally()
                || (valueFuture.isDone() && (valueFuture.join() == null))) {
            cache().statsCounter().recordLoadFailure(0L);
            cache().remove(key);
            return;
        }
        long startTime = cache().statsTicker().read();
        cache().put(key, valueFuture);
        handleCompletion(key, valueFuture, startTime, /* recordMiss */ false);
    }

    @SuppressWarnings("FutureReturnValueIgnored")
    default void handleCompletion(K key, CompletableFuture<V> valueFuture,
                                  long startTime, boolean recordMiss) {
        AtomicBoolean completed = new AtomicBoolean();
        valueFuture.whenComplete((value, error) -> {
            if (!completed.compareAndSet(false, true)) {
                // Ignore multiple invocations due to ForkJoinPool retrying on delays
                return;
            }
            long loadTime = cache().statsTicker().read() - startTime;
            if (value == null) {
                if (error != null) {
                    logger.log(Level.WARNING, "Exception thrown during asynchronous load", error);
                }
                cache().remove(key, valueFuture);
                cache().statsCounter().recordLoadFailure(loadTime);
                if (recordMiss) {
                    cache().statsCounter().recordMisses(1);
                }
            } else {
                // update the weight and expiration timestamps
                cache().replace(key, valueFuture, valueFuture);
                cache().statsCounter().recordLoadSuccess(loadTime);
                if (recordMiss) {
                    cache().statsCounter().recordMisses(1);
                }
            }
        });
    }

    /* --------------- Asynchronous view --------------- */
    final class AsyncAsMapView<K, V> implements ConcurrentMap<K, CompletableFuture<V>> {
        final LocalAsyncCache<K, V> asyncCache;

        AsyncAsMapView(LocalAsyncCache<K, V> asyncCache) {
            this.asyncCache = requireNonNull(asyncCache);
        }

        @Override
        public boolean isEmpty() {
            return asyncCache.cache().isEmpty();
        }

        @Override
        public int size() {
            return asyncCache.cache().size();
        }

        @Override
        public void clear() {
            asyncCache.cache().clear();
        }

        @Override
        public boolean containsKey(Object key) {
            return asyncCache.cache().containsKey(key);
        }

        @Override
        public boolean containsValue(Object value) {
            return asyncCache.cache().containsValue(value);
        }

        @Override
        public CompletableFuture<V> get(Object key) {
            return asyncCache.cache().get(key);
        }

        @Override
        public CompletableFuture<V> putIfAbsent(K key, CompletableFuture<V> value) {
            CompletableFuture<V> prior = asyncCache.cache().putIfAbsent(key, value);
            long startTime = asyncCache.cache().statsTicker().read();
            if (prior == null) {
                asyncCache.handleCompletion(key, value, startTime, /* recordMiss */ false);
            }
            return prior;
        }

        @Override
        public CompletableFuture<V> put(K key, CompletableFuture<V> value) {
            CompletableFuture<V> prior = asyncCache.cache().put(key, value);
            long startTime = asyncCache.cache().statsTicker().read();
            asyncCache.handleCompletion(key, value, startTime, /* recordMiss */ false);
            return prior;
        }

        @SuppressWarnings("FutureReturnValueIgnored")
        @Override
        public void putAll(Map<? extends K, ? extends CompletableFuture<V>> map) {
            map.forEach(this::put);
        }

        @Override
        public CompletableFuture<V> replace(K key, CompletableFuture<V> value) {
            CompletableFuture<V> prior = asyncCache.cache().replace(key, value);
            long startTime = asyncCache.cache().statsTicker().read();
            if (prior != null) {
                asyncCache.handleCompletion(key, value, startTime, /* recordMiss */ false);
            }
            return prior;
        }

        @Override
        public boolean replace(K key, CompletableFuture<V> oldValue, CompletableFuture<V> newValue) {
            boolean replaced = asyncCache.cache().replace(key, oldValue, newValue);
            long startTime = asyncCache.cache().statsTicker().read();
            if (replaced) {
                asyncCache.handleCompletion(key, newValue, startTime, /* recordMiss */ false);
            }
            return replaced;
        }

        @Override
        public CompletableFuture<V> remove(Object key) {
            return asyncCache.cache().remove(key);
        }

        @Override
        public boolean remove(Object key, Object value) {
            return asyncCache.cache().remove(key, value);
        }

        @SuppressWarnings("FutureReturnValueIgnored")
        @Override
        public CompletableFuture<V> computeIfAbsent(K key,
                                                    Function<? super K, ? extends CompletableFuture<V>> mappingFunction) {
            requireNonNull(mappingFunction);
            @SuppressWarnings({"rawtypes", "unchecked"})
            CompletableFuture<V>[] result = new CompletableFuture[1];
            long startTime = asyncCache.cache().statsTicker().read();
            CompletableFuture<V> future = asyncCache.cache().computeIfAbsent(key, k -> {
                result[0] = mappingFunction.apply(k);
                return result[0];
            }, /* recordStats */ false, /* recordLoad */ false);

            if (result[0] == null) {
                if ((future != null) && asyncCache.cache().isRecordingStats()) {
                    future.whenComplete((r, e) -> {
                        if ((r != null) || (e == null)) {
                            asyncCache.cache().statsCounter().recordHits(1);
                        }
                    });
                }
            } else {
                asyncCache.handleCompletion(key, result[0], startTime, /* recordMiss */ true);
            }
            return future;
        }

        @Override
        public CompletableFuture<V> computeIfPresent(K key, BiFunction<? super K,
                ? super CompletableFuture<V>, ? extends CompletableFuture<V>> remappingFunction) {
            requireNonNull(remappingFunction);

            @SuppressWarnings({"rawtypes", "unchecked"})
            CompletableFuture<V>[] result = new CompletableFuture[1];
            long startTime = asyncCache.cache().statsTicker().read();
            asyncCache.cache().compute(key, (k, oldValue) -> {
                result[0] = (oldValue == null) ? null : remappingFunction.apply(k, oldValue);
                return result[0];
            }, /* recordMiss */ false, /* recordLoad */ false, /* recordLoadFailure */ false);

            if (result[0] != null) {
                asyncCache.handleCompletion(key, result[0], startTime, /* recordMiss */ false);
            }
            return result[0];
        }

        @Override
        public CompletableFuture<V> compute(K key, BiFunction<? super K,
                ? super CompletableFuture<V>, ? extends CompletableFuture<V>> remappingFunction) {
            requireNonNull(remappingFunction);

            @SuppressWarnings({"rawtypes", "unchecked"})
            CompletableFuture<V>[] result = new CompletableFuture[1];
            long startTime = asyncCache.cache().statsTicker().read();
            asyncCache.cache().compute(key, (k, oldValue) -> {
                result[0] = remappingFunction.apply(k, oldValue);
                return result[0];
            }, /* recordMiss */ false, /* recordLoad */ false, /* recordLoadFailure */ false);

            if (result[0] != null) {
                asyncCache.handleCompletion(key, result[0], startTime, /* recordMiss */ false);
            }
            return result[0];
        }

        @Override
        public CompletableFuture<V> merge(K key, CompletableFuture<V> value,
                                          BiFunction<? super CompletableFuture<V>, ? super CompletableFuture<V>,
                                                  ? extends CompletableFuture<V>> remappingFunction) {
            requireNonNull(value);
            requireNonNull(remappingFunction);

            @SuppressWarnings({"rawtypes", "unchecked"})
            CompletableFuture<V>[] result = new CompletableFuture[1];
            long startTime = asyncCache.cache().statsTicker().read();
            asyncCache.cache().compute(key, (k, oldValue) -> {
                result[0] = (oldValue == null) ? value : remappingFunction.apply(oldValue, value);
                return result[0];
            }, /* recordMiss */ false, /* recordLoad */ false, /* recordLoadFailure */ false);

            if (result[0] != null) {
                asyncCache.handleCompletion(key, result[0], startTime, /* recordMiss */ false);
            }
            return result[0];
        }

        @Override
        public Set<K> keySet() {
            return asyncCache.cache().keySet();
        }

        @Override
        public Collection<CompletableFuture<V>> values() {
            return asyncCache.cache().values();
        }

        @Override
        public Set<Entry<K, CompletableFuture<V>>> entrySet() {
            return asyncCache.cache().entrySet();
        }

        @Override
        public boolean equals(Object o) {
            return asyncCache.cache().equals(o);
        }

        @Override
        public int hashCode() {
            return asyncCache.cache().hashCode();
        }

        @Override
        public String toString() {
            return asyncCache.cache().toString();
        }
    }

    /* --------------- Synchronous view --------------- */
    final class CacheView<K, V> extends AbstractCacheView<K, V> {
        private static final long serialVersionUID = 1L;

        final LocalAsyncCache<K, V> asyncCache;

        CacheView(LocalAsyncCache<K, V> asyncCache) {
            this.asyncCache = requireNonNull(asyncCache);
        }

        @Override
        LocalAsyncCache<K, V> asyncCache() {
            return asyncCache;
        }
    }

    @SuppressWarnings("serial")
    abstract class AbstractCacheView<K, V> implements Cache<K, V>, Serializable {
        transient AsMapView<K, V> asMapView;

        abstract LocalAsyncCache<K, V> asyncCache();

        @Override
        public V getIfPresent(Object key) {
            CompletableFuture<V> future = asyncCache().cache().getIfPresent(key, /* recordStats */ true);
            return Async.getIfReady(future);
        }

        @Override
        public Map<K, V> getAllPresent(Iterable<?> keys) {
            Set<Object> uniqueKeys = new LinkedHashSet<>();
            for (Object key : keys) {
                uniqueKeys.add(key);
            }

            int misses = 0;
            Map<Object, Object> result = new LinkedHashMap<>();
            for (Object key : uniqueKeys) {
                CompletableFuture<V> future = asyncCache().cache().get(key);
                Object value = Async.getIfReady(future);
                if (value == null) {
                    misses++;
                } else {
                    result.put(key, value);
                }
            }
            asyncCache().cache().statsCounter().recordMisses(misses);
            asyncCache().cache().statsCounter().recordHits(result.size());

            @SuppressWarnings("unchecked")
            Map<K, V> castedResult = (Map<K, V>) result;
            return Collections.unmodifiableMap(castedResult);
        }

        @Override
        @SuppressWarnings("PMD.PreserveStackTrace")
        public V get(K key, Function<? super K, ? extends V> mappingFunction) {
            requireNonNull(mappingFunction);
            CompletableFuture<V> future = asyncCache().get(key, (k, executor) ->
                    CompletableFuture.supplyAsync(() -> mappingFunction.apply(key), executor));
            try {
                return future.get();
            } catch (ExecutionException e) {
                if (e.getCause() instanceof RuntimeException) {
                    throw (RuntimeException) e.getCause();
                } else if (e.getCause() instanceof Error) {
                    throw (Error) e.getCause();
                }
                throw new CompletionException(e.getCause());
            } catch (InterruptedException e) {
                throw new CompletionException(e);
            }
        }

        @Override
        public void put(K key, V value) {
            requireNonNull(value);
            asyncCache().cache().put(key, CompletableFuture.completedFuture(value));
        }

        @Override
        public void putAll(Map<? extends K, ? extends V> map) {
            map.forEach(this::put);
        }

        @Override
        public void invalidate(Object key) {
            asyncCache().cache().remove(key);
        }

        @Override
        public void invalidateAll(Iterable<?> keys) {
            asyncCache().cache().invalidateAll(keys);
        }

        @Override
        public void invalidateAll() {
            asyncCache().cache().clear();
        }

        @Override
        public long estimatedSize() {
            return asyncCache().cache().size();
        }

        @Override
        public CacheStats stats() {
            return asyncCache().cache().statsCounter().snapshot();
        }

        @Override
        public void cleanUp() {
            asyncCache().cache().cleanUp();
        }

        @Override
        public Policy<K, V> policy() {
            return asyncCache().policy();
        }

        @Override
        public ConcurrentMap<K, V> asMap() {
            return (asMapView == null) ? (asMapView = new AsMapView<>(asyncCache().cache())) : asMapView;
        }
    }

    final class AsMapView<K, V> extends AbstractMap<K, V> implements ConcurrentMap<K, V> {
        final LocalCache<K, CompletableFuture<V>> delegate;

        Collection<V> values;
        Set<Entry<K, V>> entries;

        AsMapView(LocalCache<K, CompletableFuture<V>> delegate) {
            this.delegate = delegate;
        }

        @Override
        public boolean isEmpty() {
            return delegate.isEmpty();
        }

        @Override
        public int size() {
            return delegate.size();
        }

        @Override
        public void clear() {
            delegate.clear();
        }

        @Override
        public boolean containsKey(Object key) {
            return delegate.containsKey(key);
        }

        @Override
        public boolean containsValue(Object value) {
            requireNonNull(value);

            for (CompletableFuture<V> valueFuture : delegate.values()) {
                if (value.equals(Async.getIfReady(valueFuture))) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public V get(Object key) {
            return Async.getIfReady(delegate.get(key));
        }

        @Override
        public V putIfAbsent(K key, V value) {
            requireNonNull(value);
            CompletableFuture<V> valueFuture =
                    delegate.putIfAbsent(key, CompletableFuture.completedFuture(value));
            return Async.getWhenSuccessful(valueFuture);
        }

        @Override
        public V put(K key, V value) {
            requireNonNull(value);
            CompletableFuture<V> oldValueFuture =
                    delegate.put(key, CompletableFuture.completedFuture(value));
            return Async.getWhenSuccessful(oldValueFuture);
        }

        @Override
        public V remove(Object key) {
            CompletableFuture<V> oldValueFuture = delegate.remove(key);
            return Async.getWhenSuccessful(oldValueFuture);
        }

        @Override
        public boolean remove(Object key, Object value) {
            requireNonNull(key);
            if (value == null) {
                return false;
            }

            @SuppressWarnings("unchecked")
            K castedKey = (K) key;
            boolean[] removed = {false};
            boolean[] done = {false};
            for (; ; ) {
                CompletableFuture<V> future = delegate.get(key);
                V oldValue = Async.getWhenSuccessful(future);
                if ((future != null) && !value.equals(oldValue)) {
                    // Optimistically check if the current value is equal, but don't skip if it may be loading
                    return false;
                }

                delegate.compute(castedKey, (k, oldValueFuture) -> {
                    if (future != oldValueFuture) {
                        return oldValueFuture;
                    }
                    done[0] = true;
                    removed[0] = value.equals(oldValue);
                    return removed[0] ? null : oldValueFuture;
                }, /* recordStats */ false, /* recordLoad */ false, /* recordLoadFailure */ true);
                if (done[0]) {
                    return removed[0];
                }
            }
        }

        @Override
        public V replace(K key, V value) {
            requireNonNull(value);
            CompletableFuture<V> oldValueFuture =
                    delegate.replace(key, CompletableFuture.completedFuture(value));
            return Async.getWhenSuccessful(oldValueFuture);
        }

        @Override
        public boolean replace(K key, V oldValue, V newValue) {
            requireNonNull(oldValue);
            requireNonNull(newValue);
            CompletableFuture<V> oldValueFuture = delegate.get(key);
            if ((oldValueFuture != null) && !oldValue.equals(Async.getWhenSuccessful(oldValueFuture))) {
                // Optimistically check if the current value is equal, but don't skip if it may be loading
                return false;
            }

            @SuppressWarnings("unchecked")
            K castedKey = key;
            boolean[] replaced = {false};
            delegate.compute(castedKey, (k, value) -> {
                replaced[0] = oldValue.equals(Async.getWhenSuccessful(value));
                return replaced[0] ? CompletableFuture.completedFuture(newValue) : value;
            }, /* recordStats */ false, /* recordLoad */ false, /* recordLoadFailure */ true);
            return replaced[0];
        }

        @Override
        public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
            requireNonNull(mappingFunction);
            CompletableFuture<V> valueFuture = delegate.computeIfAbsent(key, k -> {
                V newValue = mappingFunction.apply(key);
                return (newValue == null) ? null : CompletableFuture.completedFuture(newValue);
            });
            return Async.getWhenSuccessful(valueFuture);
        }

        @Override
        public V computeIfPresent(K key,
                                  BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
            requireNonNull(remappingFunction);
            boolean[] computed = {false};
            for (; ; ) {
                CompletableFuture<V> future = delegate.get(key);
                V oldValue = Async.getWhenSuccessful(future);
                if (oldValue == null) {
                    return null;
                }
                CompletableFuture<V> valueFuture = delegate.computeIfPresent(key, (k, oldValueFuture) -> {
                    if (future != oldValueFuture) {
                        return oldValueFuture;
                    }
                    computed[0] = true;
                    V newValue = remappingFunction.apply(key, oldValue);
                    return (newValue == null) ? null : CompletableFuture.completedFuture(newValue);
                });
                if (computed[0] || (valueFuture == null)) {
                    return Async.getWhenSuccessful(valueFuture);
                }
            }
        }

        @Override
        public V compute(K key,
                         BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
            requireNonNull(remappingFunction);
            boolean[] computed = {false};
            for (; ; ) {
                CompletableFuture<V> future = delegate.get(key);
                V oldValue = Async.getWhenSuccessful(future);
                CompletableFuture<V> valueFuture = delegate.compute(key, (k, oldValueFuture) -> {
                    if (future != oldValueFuture) {
                        return oldValueFuture;
                    }
                    computed[0] = true;
                    long startTime = delegate.statsTicker().read();
                    V newValue = remappingFunction.apply(key, oldValue);
                    long loadTime = delegate.statsTicker().read() - startTime;
                    if (newValue == null) {
                        delegate.statsCounter().recordLoadFailure(loadTime);
                        return null;
                    }
                    delegate.statsCounter().recordLoadSuccess(loadTime);
                    return CompletableFuture.completedFuture(newValue);
                }, /* recordMiss */ false, /* recordLoad */ false, /* recordLoadFailure */ true);
                if (computed[0]) {
                    return Async.getWhenSuccessful(valueFuture);
                }
            }
        }

        @Override
        public V merge(K key, V value,
                       BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
            requireNonNull(value);
            requireNonNull(remappingFunction);
            CompletableFuture<V> newValueFuture = CompletableFuture.completedFuture(value);
            boolean[] merged = {false};
            for (; ; ) {
                CompletableFuture<V> future = delegate.get(key);
                V oldValue = Async.getWhenSuccessful(future);
                CompletableFuture<V> mergedValueFuture = delegate.merge(
                        key, newValueFuture, (oldValueFuture, valueFuture) -> {
                            if (future != oldValueFuture) {
                                return oldValueFuture;
                            }
                            merged[0] = true;
                            if (oldValue == null) {
                                return valueFuture;
                            }
                            V mergedValue = remappingFunction.apply(oldValue, value);
                            if (mergedValue == null) {
                                return null;
                            } else if (mergedValue == oldValue) {
                                return oldValueFuture;
                            } else if (mergedValue == value) {
                                return valueFuture;
                            }
                            return CompletableFuture.completedFuture(mergedValue);
                        });
                if (merged[0] || (mergedValueFuture == newValueFuture)) {
                    return Async.getWhenSuccessful(mergedValueFuture);
                }
            }
        }

        @Override
        public Set<K> keySet() {
            return delegate.keySet();
        }

        @Override
        public Collection<V> values() {
            return (values == null) ? (values = new Values()) : values;
        }

        @Override
        public Set<Entry<K, V>> entrySet() {
            return (entries == null) ? (entries = new EntrySet()) : entries;
        }

        private final class Values extends AbstractCollection<V> {

            @Override
            public boolean isEmpty() {
                return AsMapView.this.isEmpty();
            }

            @Override
            public int size() {
                return AsMapView.this.size();
            }

            @Override
            public boolean contains(Object o) {
                return AsMapView.this.containsValue(o);
            }

            @Override
            public void clear() {
                AsMapView.this.clear();
            }

            @Override
            public Iterator<V> iterator() {
                return new Iterator<V>() {
                    Iterator<Entry<K, V>> iterator = entrySet().iterator();

                    @Override
                    public boolean hasNext() {
                        return iterator.hasNext();
                    }

                    @Override
                    public V next() {
                        return iterator.next().getValue();
                    }

                    @Override
                    public void remove() {
                        iterator.remove();
                    }
                };
            }
        }

        private final class EntrySet extends AbstractSet<Entry<K, V>> {

            @Override
            public boolean isEmpty() {
                return AsMapView.this.isEmpty();
            }

            @Override
            public int size() {
                return AsMapView.this.size();
            }

            @Override
            public boolean contains(Object o) {
                if (!(o instanceof Entry<?, ?>)) {
                    return false;
                }
                Entry<?, ?> entry = (Entry<?, ?>) o;
                V value = AsMapView.this.get(entry.getKey());
                return (value != null) && value.equals(entry.getValue());
            }

            @Override
            public boolean remove(Object obj) {
                if (!(obj instanceof Entry<?, ?>)) {
                    return false;
                }
                Entry<?, ?> entry = (Entry<?, ?>) obj;
                return AsMapView.this.remove(entry.getKey(), entry.getValue());
            }

            @Override
            public void clear() {
                AsMapView.this.clear();
            }

            @Override
            public Iterator<Entry<K, V>> iterator() {
                return new Iterator<Entry<K, V>>() {
                    Iterator<Entry<K, CompletableFuture<V>>> iterator = delegate.entrySet().iterator();
                    Entry<K, V> cursor;
                    K removalKey;

                    @Override
                    public boolean hasNext() {
                        while ((cursor == null) && iterator.hasNext()) {
                            Entry<K, CompletableFuture<V>> entry = iterator.next();
                            V value = Async.getIfReady(entry.getValue());
                            if (value != null) {
                                cursor = new WriteThroughEntry<>(AsMapView.this, entry.getKey(), value);
                            }
                        }
                        return (cursor != null);
                    }

                    @Override
                    public Entry<K, V> next() {
                        if (!hasNext()) {
                            throw new NoSuchElementException();
                        }
                        @SuppressWarnings("NullAway")
                        K key = cursor.getKey();
                        Entry<K, V> entry = cursor;
                        removalKey = key;
                        cursor = null;
                        return entry;
                    }

                    @Override
                    public void remove() {
                        Caffeine.requireState(removalKey != null);
                        delegate.remove(removalKey);
                        removalKey = null;
                    }
                };
            }
        }
    }
}
