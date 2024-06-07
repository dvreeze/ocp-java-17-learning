/*
 * Copyright 2024-2024 Chris de Vreeze
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package chapter13;

import java.util.List;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Example showing thread-safe counters.
 *
 * @author Chris de Vreeze
 */
public class MultiThreadedCountingExample {

    private static final int NUMBER_OF_THREADS = Math.min(Runtime.getRuntime().availableProcessors() * 2, 12);

    private record MyCounter(int count, String bogusDescriptionField) {

        MyCounter increment() {
            return new MyCounter(this.count() + 1, this.bogusDescriptionField());
        }
    }

    private final Set<String> threads = new ConcurrentSkipListSet<>();

    private final Lock lock = new ReentrantLock();

    private int veryUnsafeCounter = 0;

    private int unsafeCounter = 0;

    private int counter1 = 0;

    private final AtomicInteger counter2 = new AtomicInteger(0);

    private final AtomicReference<MyCounter> counter3 = new AtomicReference<>(new MyCounter(0, ""));

    private final Callable<Integer> incrementVeryUnsafeCounter = () -> {
        var currentValue = veryUnsafeCounter;
        threads.add(Thread.currentThread().getName());

        if (currentValue % 1000 == 0) {
            var allStackTraces = Thread.getAllStackTraces();
            if (allStackTraces.isEmpty()) {
                throw new RuntimeException("Strange. No current thread stack traces.");
            }
        } else {
            var stackTrace = Thread.currentThread().getStackTrace();
            if (stackTrace.length <= 2) {
                throw new RuntimeException("Strange. Current stack trace has <= 2 elements.");
            }
        }

        // This update of veryUnsafeCounter is everything but an atomic operation, so lost updates are quite likely
        veryUnsafeCounter = currentValue + 1;
        return veryUnsafeCounter;
    };

    private final Callable<Integer> incrementUnsafeCounter = () -> {
        threads.add(Thread.currentThread().getName());
        // This assignment is not atomic, so lost updates can occur
        unsafeCounter += 1;
        return unsafeCounter;
    };

    private final Callable<Integer> incrementIntCounter = () -> {
        threads.add(Thread.currentThread().getName());
        try {
            lock.lock();
            counter1 += 1;
            return counter1;
        } finally {
            lock.unlock();
        }
    };

    private final Callable<Integer> incrementAtomicIntCounter = () -> {
        threads.add(Thread.currentThread().getName());
        return counter2.incrementAndGet();
    };

    private final Callable<Integer> incrementAtomicReferenceCounter = () -> {
        threads.add(Thread.currentThread().getName());
        return counter3.updateAndGet(MyCounter::increment).count();
    };

    public void run(int numberOfTimes) {
        ExecutorService executorService = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

        List<Integer> range = IntStream.range(0, numberOfTimes).boxed().toList();

        try {
            List<Callable<Integer>> callables =
                    Stream.of(
                            incrementVeryUnsafeCounter,
                            incrementUnsafeCounter,
                            incrementIntCounter,
                            incrementAtomicIntCounter,
                            incrementAtomicReferenceCounter
                    ).parallel().flatMap(callable -> range.stream().map(i -> callable)).toList();

            List<Future<Integer>> futures = executorService.invokeAll(callables);

            for (Future<Integer> f : futures) {
                f.get();
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        } finally {
            executorService.shutdown();
        }

        System.out.printf("Value of very unsafe counter: %d%n", veryUnsafeCounter);
        System.out.printf("Value of unsafe counter: %d%n", unsafeCounter);
        System.out.println();
        System.out.printf("Value of integer counter: %d%n", counter1);
        System.out.printf("Value of atomic integer counter: %d%n", counter2.get());
        System.out.printf("Value of atomic reference counter: %d%n", counter3.get().count());

        System.out.println();
        System.out.printf("Number of used threads: %d%n", threads.size());
        System.out.printf("Threads used: %s%n", threads.stream().sorted().toList());
    }

    public static void main(String[] args) {
        int howManyTimes = (args.length == 0) ? 1_000_000 : Integer.parseInt(args[0]);
        var countingExample = new MultiThreadedCountingExample();
        countingExample.run(howManyTimes);
    }
}
