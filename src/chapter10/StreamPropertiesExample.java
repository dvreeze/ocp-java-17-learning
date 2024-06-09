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

package chapter10;

import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.BooleanSupplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * This program "plays" with some Stream properties.
 *
 * @author Chris de Vreeze
 */
public class StreamPropertiesExample {

    public static final List<Integer> intList = List.copyOf(IntStream.rangeClosed(1, 144_000).boxed().toList());
    public static final List<Integer> intListDoubled = List.copyOf(intList.stream().map(i -> i * 2).toList());

    private static void showParallelOrNot() {
        requireFalse(() -> intList.stream().isParallel());
        requireTrue(() -> intList.parallelStream().isParallel());
        requireTrue(() -> intList.stream().parallel().isParallel());
        requireFalse(() -> makeParallel(makeParallel(makeSequential(intList.parallelStream()))).sequential().isParallel());
        requireFalse(() -> makeParallel(intList.stream()).sequential().filter(i -> i % 2 == 0).map(i -> i * 2).isParallel());
        requireFalse(() -> makeParallel(intList.stream()).filter(i -> i % 2 == 0).map(i -> i * 2).sequential().isParallel());
    }

    private static void showParallelStreamSemantics() {
        // The result is the same as for sequential processing of the stream
        requireTrue(() ->
                Arrays.equals(
                        intList.parallelStream().mapToInt(x -> {
                            expensiveCall(x);

                            return x * 2;
                        }).toArray(),
                        intListDoubled.stream().mapToInt(x -> x).toArray()
                )
        );

        final Set<Thread> usedThreads = new CopyOnWriteArraySet<>();

        requireTrue(() -> {
            var list = List.copyOf(intList.parallelStream().map(x -> {
                expensiveCall(x);

                // It's ok. We are "interfering with" a concurrent collection.
                usedThreads.add(Thread.currentThread());

                return x * 2;
            }).toList());

            return list.equals(intListDoubled);
        });

        System.out.printf("Used thread count: %d%n", usedThreads.size());

        usedThreads.clear();

        requireTrue(() -> {
            var list = List.copyOf(intList.parallelStream().unordered().map(x -> {
                expensiveCall(x);

                usedThreads.add(Thread.currentThread());

                return x * 2;
            }).toList());

            return Set.copyOf(list).equals(Set.copyOf(intListDoubled));
        });

        System.out.printf("Used thread count: %d%n", usedThreads.size());
    }

    private static void showUnordered() {
        requireFalse(
                () -> intListDoubled.parallelStream().unordered().spliterator().hasCharacteristics(Spliterator.ORDERED)
        );
        requireTrue(
                () -> intListDoubled.parallelStream().spliterator().hasCharacteristics(Spliterator.ORDERED)
        );

        requireFalse(
                () -> intListDoubled.parallelStream().unordered().findFirst()
                        .equals(intListDoubled.stream().findFirst())
        );
        requireTrue(
                () -> intListDoubled.parallelStream().findFirst()
                        .equals(intListDoubled.stream().findFirst())
        );
    }

    private static void showCharacteristics() {
        requireFalse(() -> Collectors.toList().characteristics().contains(Collector.Characteristics.UNORDERED));
        requireFalse(() -> Collectors.toList().characteristics().contains(Collector.Characteristics.CONCURRENT));

        Collector<Integer, ?, Map<Integer, Integer>> toMapCollector =
                Collectors.toMap(k -> k, v -> v);

        requireFalse(() -> toMapCollector.characteristics().contains(Collector.Characteristics.UNORDERED));
        requireFalse(() -> toMapCollector.characteristics().contains(Collector.Characteristics.CONCURRENT));

        Collector<Integer, ?, ConcurrentMap<Integer, Integer>> toConcurrentMapCollector =
                Collectors.toConcurrentMap(k -> k, v -> v);

        requireTrue(() -> toConcurrentMapCollector.characteristics().contains(Collector.Characteristics.UNORDERED));
        requireTrue(() -> toConcurrentMapCollector.characteristics().contains(Collector.Characteristics.CONCURRENT));
    }

    public static void main(String[] args) {
        showParallelOrNot();
        showParallelStreamSemantics();
        showUnordered();
        showCharacteristics();

        System.out.printf("Stream: %s%n", intList.stream());
        System.out.printf("Stream: %s%n", intList.parallelStream());
        System.out.printf("Stream: %s%n", makeParallel(intList.stream()).filter(i -> i % 2 == 0).map(i -> i * 2));
    }

    private static void requireTrue(BooleanSupplier predicate) {
        if (!predicate.getAsBoolean()) {
            throw new RuntimeException("The predicate does not hold");
        }
    }

    private static void requireFalse(BooleanSupplier predicate) {
        requireTrue(() -> !predicate.getAsBoolean());
    }

    private static <T> Stream<T> makeSequential(Stream<T> stream) {
        return stream.sequential();
    }

    private static <T> Stream<T> makeParallel(Stream<T> stream) {
        return stream.parallel();
    }

    private static void expensiveCall(int x) {
        requireTrue(() ->
                (x % 100 != 0) ||
                        Thread.getAllStackTraces().values().stream().allMatch(Objects::nonNull));
    }
}
