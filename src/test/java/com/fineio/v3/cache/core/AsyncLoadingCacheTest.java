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

import com.fineio.v3.cache.core.testing.CacheContext;
import com.fineio.v3.cache.core.testing.CacheProvider;
import com.fineio.v3.cache.core.testing.CacheSpec;
import com.fineio.v3.cache.core.testing.CacheSpec.CacheExecutor;
import com.fineio.v3.cache.core.testing.CacheSpec.Compute;
import com.fineio.v3.cache.core.testing.CacheSpec.ExecutorFailure;
import com.fineio.v3.cache.core.testing.CacheSpec.Implementation;
import com.fineio.v3.cache.core.testing.CacheSpec.Listener;
import com.fineio.v3.cache.core.testing.CacheSpec.Loader;
import com.fineio.v3.cache.core.testing.CacheSpec.Population;
import com.fineio.v3.cache.core.testing.CacheSpec.ReferenceType;
import com.fineio.v3.cache.core.testing.CacheValidationListener;
import com.fineio.v3.cache.core.testing.CheckNoStats;
import com.fineio.v3.cache.core.testing.CheckNoWriter;
import com.fineio.v3.cache.testing.Awaits;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.primitives.Ints;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.fineio.v3.cache.core.testing.HasRemovalNotifications.hasRemovalNotifications;
import static com.fineio.v3.cache.core.testing.HasStats.hasHitCount;
import static com.fineio.v3.cache.core.testing.HasStats.hasLoadFailureCount;
import static com.fineio.v3.cache.core.testing.HasStats.hasLoadSuccessCount;
import static com.fineio.v3.cache.core.testing.HasStats.hasMissCount;
import static com.fineio.v3.cache.testing.Awaits.await;
import static com.fineio.v3.cache.testing.IsFutureValue.futureOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.both;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;

/**
 * The test cases for the {@link AsyncLoadingCache} interface that simulate the most generic usages.
 *
 * @author ben.manes@gmail.com (Ben Manes)
 */
@Listeners(CacheValidationListener.class)
@Test(dataProviderClass = CacheProvider.class)
@SuppressWarnings("FutureReturnValueIgnored")
public final class AsyncLoadingCacheTest {

  /* --------------- get --------------- */

  @CacheSpec
  @CheckNoWriter
  @Test(dataProvider = "caches", expectedExceptions = NullPointerException.class)
  public void get_null(AsyncLoadingCache<Integer, Integer> cache, CacheContext context) {
    cache.get(null);
  }

  @CacheSpec
  @CheckNoWriter
  @Test(dataProvider = "caches")
  public void get_absent(AsyncLoadingCache<Integer, Integer> cache, CacheContext context) {
    assertThat(cache.get(context.absentKey()), is(futureOf(context.absentValue())));
  }

  @CheckNoWriter
  @Test(dataProvider = "caches")
  @CacheSpec(loader = Loader.EXCEPTIONAL)
  public void get_absent_failure(AsyncLoadingCache<Integer, Integer> cache, CacheContext context) {
    CompletableFuture<Integer> future = cache.get(context.absentKey());
    assertThat(future.isCompletedExceptionally(), is(true));
    assertThat(cache.getIfPresent(context.absentKey()), is(nullValue()));
  }

  @CheckNoWriter
  @Test(dataProvider = "caches")
  @CacheSpec(loader = Loader.EXCEPTIONAL, executor = CacheExecutor.THREADED,
      executorFailure = ExecutorFailure.IGNORED)
  public void get_absent_failure_async(AsyncLoadingCache<Integer, Integer> cache,
      CacheContext context) throws InterruptedException {
    AtomicBoolean done = new AtomicBoolean();
    Integer key = context.absentKey();
    CompletableFuture<Integer> valueFuture = cache.get(key);
    valueFuture.whenComplete((r, e) -> done.set(true));

    Awaits.await().untilTrue(done);
    Awaits.await().until(() -> !cache.synchronous().asMap().containsKey(context.absentKey()));
    Awaits.await().until(() -> context, both(hasMissCount(1)).and(hasHitCount(0)));
    Awaits.await().until(() -> context, both(hasLoadSuccessCount(0)).and(hasLoadFailureCount(1)));

    assertThat(valueFuture.isCompletedExceptionally(), is(true));
    assertThat(cache.getIfPresent(key), is(nullValue()));
  }

  @CheckNoWriter
  @Test(dataProvider = "caches")
  @CacheSpec(population = { Population.SINGLETON, Population.PARTIAL, Population.FULL })
  public void get_present(AsyncLoadingCache<Integer, Integer> cache, CacheContext context) {
    assertThat(cache.get(context.firstKey()), futureOf(-context.firstKey()));
    assertThat(cache.get(context.middleKey()), futureOf(-context.middleKey()));
    assertThat(cache.get(context.lastKey()), futureOf(-context.lastKey()));
    assertThat(context, both(hasMissCount(0)).and(hasHitCount(3)));
    assertThat(context, both(hasLoadSuccessCount(0)).and(hasLoadFailureCount(0)));
  }

  /* --------------- getAll --------------- */

  @CheckNoWriter @CheckNoStats
  @Test(dataProvider = "caches", expectedExceptions = NullPointerException.class)
  @CacheSpec(removalListener = { Listener.DEFAULT, Listener.REJECTING })
  public void getAll_iterable_null(AsyncLoadingCache<Integer, Integer> cache,
      CacheContext context) {
    cache.getAll(null);
  }

  @CheckNoWriter @CheckNoStats
  @Test(dataProvider = "caches", expectedExceptions = NullPointerException.class)
  @CacheSpec(loader = { Loader.NEGATIVE, Loader.BULK_NEGATIVE },
      removalListener = { Listener.DEFAULT, Listener.REJECTING })
  public void getAll_iterable_nullKey(AsyncLoadingCache<Integer, Integer> cache,
      CacheContext context) {
    cache.getAll(Collections.singletonList(null));
  }

  @CheckNoWriter @CheckNoStats
  @Test(dataProvider = "caches")
  @CacheSpec(loader = { Loader.NEGATIVE, Loader.BULK_NEGATIVE },
      removalListener = { Listener.DEFAULT, Listener.REJECTING })
  public void getAll_iterable_empty(AsyncLoadingCache<Integer, Integer> cache,
      CacheContext context) throws Exception {
    CompletableFuture<Map<Integer, Integer>> result = cache.getAll(ImmutableList.of());
    assertThat(result.get().size(), is(0));
  }

  @CacheSpec
  @CheckNoWriter
  @Test(dataProvider = "caches", expectedExceptions = UnsupportedOperationException.class)
  public void getAll_immutable(AsyncLoadingCache<Integer, Integer> cache, CacheContext context)
      throws Exception {
    cache.getAll(context.absentKeys()).get().clear();
  }

  @CheckNoWriter
  @CacheSpec(loader = Loader.BULK_NULL)
  @Test(dataProvider = "caches", expectedExceptions = ExecutionException.class)
  public void getAll_absent_bulkNull(AsyncLoadingCache<Integer, Integer> cache,
      CacheContext context) throws Exception {
    try {
      cache.getAll(context.absentKeys()).get();
    } finally {
      int misses = context.absentKeys().size();
      int loadFailures = context.loader().isBulk()
          ? 1
          : (context.isAsync() ? misses : 1);
      assertThat(context, both(hasMissCount(misses)).and(hasHitCount(0)));
      assertThat(context, both(hasLoadSuccessCount(0)).and(hasLoadFailureCount(loadFailures)));
    }
  }

  @CheckNoWriter
  @CacheSpec(loader = { Loader.EXCEPTIONAL, Loader.BULK_EXCEPTIONAL })
  @Test(dataProvider = "caches", expectedExceptions = ExecutionException.class)
  public void getAll_absent_failure(AsyncLoadingCache<Integer, Integer> cache,
      CacheContext context) throws Exception {
    try {
      cache.getAll(context.absentKeys()).get();
    } finally {
      int misses = context.absentKeys().size();
      int loadFailures = context.loader().isBulk()
          ? 1
          : (context.isAsync() ? misses : 1);
      assertThat(context, both(hasMissCount(misses)).and(hasHitCount(0)));
      assertThat(context, both(hasLoadSuccessCount(0)).and(hasLoadFailureCount(loadFailures)));
    }
  }

  @CheckNoWriter
  @Test(dataProvider = "caches")
  @CacheSpec(loader = { Loader.NEGATIVE, Loader.BULK_NEGATIVE },
      removalListener = { Listener.DEFAULT, Listener.REJECTING })
  public void getAll_absent(AsyncLoadingCache<Integer, Integer> cache, CacheContext context)
      throws Exception {
    Map<Integer, Integer> result = cache.getAll(context.absentKeys()).get();

    int count = context.absentKeys().size();
    int loads = context.loader().isBulk() ? 1 : count;
    assertThat(result.size(), is(count));
    assertThat(context, both(hasMissCount(count)).and(hasHitCount(0)));
    assertThat(context, both(hasLoadSuccessCount(loads)).and(hasLoadFailureCount(0)));
  }

  @CheckNoWriter
  @Test(dataProvider = "caches")
  @CacheSpec(loader = { Loader.NEGATIVE, Loader.BULK_NEGATIVE },
      population = { Population.SINGLETON, Population.PARTIAL, Population.FULL },
      removalListener = { Listener.DEFAULT, Listener.REJECTING })
  public void getAll_present_partial(AsyncLoadingCache<Integer, Integer> cache,
      CacheContext context) throws Exception {
    Map<Integer, Integer> expect = new HashMap<>();
    expect.put(context.firstKey(), -context.firstKey());
    expect.put(context.middleKey(), -context.middleKey());
    expect.put(context.lastKey(), -context.lastKey());
    Map<Integer, Integer> result = cache.getAll(expect.keySet()).get();

    assertThat(result, is(equalTo(expect)));
    assertThat(context, both(hasMissCount(0)).and(hasHitCount(expect.size())));
    assertThat(context, both(hasLoadSuccessCount(0)).and(hasLoadFailureCount(0)));
  }

  @CheckNoWriter
  @Test(dataProvider = "caches")
  @CacheSpec(loader = { Loader.BULK_NEGATIVE_EXCEEDS },
      removalListener = { Listener.DEFAULT, Listener.REJECTING })
  public void getAll_exceeds(AsyncLoadingCache<Integer, Integer> cache, CacheContext context) {
    Map<Integer, Integer> result = cache.getAll(context.absentKeys()).join();

    assertThat(result.keySet(), equalTo(context.absentKeys()));
    assertThat(cache.synchronous().estimatedSize(),
        is(greaterThan(context.initialSize() + context.absentKeys().size())));

    assertThat(context, both(hasMissCount(result.size())).and(hasHitCount(0)));
    assertThat(context, both(hasLoadSuccessCount(1)).and(hasLoadFailureCount(0)));
  }

  @CheckNoWriter
  @Test(dataProvider = "caches")
  @CacheSpec(loader = { Loader.NEGATIVE, Loader.BULK_NEGATIVE },
      population = { Population.SINGLETON, Population.PARTIAL, Population.FULL },
      removalListener = { Listener.DEFAULT, Listener.REJECTING })
  public void getAll_duplicates(AsyncLoadingCache<Integer, Integer> cache, CacheContext context) {
    Set<Integer> absentKeys = ImmutableSet.copyOf(Iterables.limit(context.absentKeys(),
        Ints.saturatedCast(context.maximum().max() - context.initialSize())));
    Iterable<Integer> keys = Iterables.concat(absentKeys, absentKeys,
        context.original().keySet(), context.original().keySet());
    Map<Integer, Integer> result = cache.getAll(keys).join();

    assertThat(context, hasMissCount(absentKeys.size()));
    assertThat(context, hasHitCount(context.initialSize()));
    assertThat(result.keySet(), is(equalTo(ImmutableSet.copyOf(keys))));

    int loads = context.loader().isBulk() ? 1 : absentKeys.size();
    assertThat(context, both(hasLoadSuccessCount(loads)).and(hasLoadFailureCount(0)));
  }

  @CheckNoWriter
  @Test(dataProvider = "caches")
  @CacheSpec(loader = { Loader.NEGATIVE, Loader.BULK_NEGATIVE },
      population = { Population.SINGLETON, Population.PARTIAL, Population.FULL },
      removalListener = { Listener.DEFAULT, Listener.REJECTING })
  public void getAllPresent_ordered_absent(
      AsyncLoadingCache<Integer, Integer> cache, CacheContext context) {
    List<Integer> keys = new ArrayList<>(context.absentKeys());
    Collections.shuffle(keys);

    List<Integer> result = new ArrayList<>(cache.getAll(keys).join().keySet());
    assertThat(result, is(equalTo(keys)));
  }

  @CheckNoWriter
  @Test(dataProvider = "caches")
  @CacheSpec(loader = { Loader.NEGATIVE, Loader.BULK_NEGATIVE },
      population = { Population.SINGLETON, Population.PARTIAL },
      removalListener = { Listener.DEFAULT, Listener.REJECTING })
  public void getAllPresent_ordered_partial(
      AsyncLoadingCache<Integer, Integer> cache, CacheContext context) {
    List<Integer> keys = new ArrayList<>(context.original().keySet());
    keys.addAll(context.absentKeys());
    Collections.shuffle(keys);

    List<Integer> result = new ArrayList<>(cache.getAll(keys).join().keySet());
    assertThat(result, is(equalTo(keys)));
  }

  @CheckNoWriter
  @Test(dataProvider = "caches")
  @CacheSpec(loader = { Loader.NEGATIVE, Loader.BULK_NEGATIVE },
      population = { Population.SINGLETON, Population.PARTIAL, Population.FULL },
      removalListener = { Listener.DEFAULT, Listener.REJECTING })
  public void getAllPresent_ordered_present(
      AsyncLoadingCache<Integer, Integer> cache, CacheContext context) {
    List<Integer> keys = new ArrayList<>(context.original().keySet());
    Collections.shuffle(keys);

    List<Integer> result = new ArrayList<>(cache.getAll(keys).join().keySet());
    assertThat(result, is(equalTo(keys)));
  }

  @CheckNoWriter
  @Test(dataProvider = "caches")
  @CacheSpec(loader = Loader.BULK_NEGATIVE_EXCEEDS,
      removalListener = { Listener.DEFAULT, Listener.REJECTING })
  public void getAllPresent_ordered_exceeds(
      AsyncLoadingCache<Integer, Integer> cache, CacheContext context) {
    List<Integer> keys = new ArrayList<>(context.original().keySet());
    keys.addAll(context.absentKeys());
    Collections.shuffle(keys);

    List<Integer> result = new ArrayList<>(cache.getAll(keys).join().keySet());
    assertThat(result.subList(0, keys.size()), is(equalTo(keys)));
  }

  @CheckNoWriter
  @Test(dataProvider = "caches")
  @CacheSpec(implementation = Implementation.Caffeine, compute = Compute.ASYNC,
      removalListener = { Listener.DEFAULT, Listener.REJECTING })
  public void getAll_badLoader(CacheContext context) {
    @SuppressWarnings("serial")
    final class LoadAllException extends RuntimeException {};

    AsyncCacheLoader<Integer, Integer> loader = new AsyncCacheLoader<Integer, Integer>() {
      @Override public CompletableFuture<Integer> asyncLoad(Integer key, Executor executor) {
        throw new IllegalStateException();
      }
      @Override public CompletableFuture<Map<Integer, Integer>> asyncLoadAll(
          Iterable<? extends Integer> keys, Executor executor) {
        throw new LoadAllException();
      }
    };
    AsyncLoadingCache<Integer, Integer> cache = context.buildAsync(loader);

    try {
      cache.getAll(context.absentKeys());
      Assert.fail();
    } catch (LoadAllException e) {
      assertThat(cache.synchronous().estimatedSize(), is(0L));
    }
  }

  /* --------------- put --------------- */

  @Test(dataProvider = "caches")
  @CacheSpec(population = { Population.SINGLETON, Population.PARTIAL, Population.FULL })
  public void put_replace(AsyncLoadingCache<Integer, Integer> cache, CacheContext context) {
    CompletableFuture<Integer> value = CompletableFuture.completedFuture(context.absentValue());
    for (Integer key : context.firstMiddleLastKeys()) {
      cache.put(key, value);
      assertThat(cache.get(key), is(futureOf(context.absentValue())));
    }
    assertThat(cache.synchronous().estimatedSize(), is(context.initialSize()));

    int count = context.firstMiddleLastKeys().size();
    assertThat(cache, hasRemovalNotifications(context, count, RemovalCause.REPLACED));
  }

  /* --------------- refresh --------------- */

  @Test(dataProvider = "caches")
  @CacheSpec(implementation = Implementation.Caffeine, population = Population.EMPTY,
      executor = CacheExecutor.THREADED, compute = Compute.ASYNC, values = ReferenceType.STRONG)
  public void refresh(CacheContext context) {
    AtomicBoolean done = new AtomicBoolean();
    AsyncLoadingCache<Integer, Integer> cache = context.buildAsync(key -> {
      await().untilTrue(done);
      return -key;
    });

    Integer key = 1;
    cache.synchronous().put(key, key);
    CompletableFuture<Integer> original = cache.get(key);
    for (int i = 0; i < 10; i++) {
      context.ticker().advance(1, TimeUnit.SECONDS);
      cache.synchronous().refresh(key);

      CompletableFuture<Integer> next = cache.get(key);
      assertThat(next, is(sameInstance(original)));
    }
    done.set(true);
    await().until(() -> cache.synchronous().getIfPresent(key), is(-key));
  }

  @Test(dataProvider = "caches", timeOut = 5000) // Issue #69
  @CacheSpec(implementation = Implementation.Caffeine, population = Population.EMPTY,
      executor = CacheExecutor.THREADED, compute = Compute.ASYNC, values = ReferenceType.STRONG)
  public void refresh_deadlock(CacheContext context) {
    CompletableFuture<Integer> future = new CompletableFuture<>();
    AsyncLoadingCache<Integer, Integer> cache = context.buildAsync((k, e) -> future);

    cache.synchronous().refresh(context.absentKey());
    CompletableFuture<Integer> get = cache.get(context.absentKey());

    future.complete(context.absentValue());
    assertThat(get, futureOf(context.absentValue()));
  }

  /* --------------- AsyncCacheLoader --------------- */

  @Test(expectedExceptions = UnsupportedOperationException.class)
  public void asyncLoadAll() throws Throwable {
    AsyncCacheLoader<Integer, Integer> loader =
        (key, executor) -> CompletableFuture.completedFuture(-key);
    loader.asyncLoadAll(Collections.emptyList(), Runnable::run).get();
  }

  @Test
  public void asyncReload() throws Exception {
    AsyncCacheLoader<Integer, Integer> loader =
        (key, executor) -> CompletableFuture.completedFuture(-key);
    CompletableFuture<?> future = loader.asyncReload(1, 2, Runnable::run);
    assertThat(future.get(), is(-1));
  }
}