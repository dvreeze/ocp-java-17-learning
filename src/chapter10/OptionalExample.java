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

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Example showing type "Optional".
 *
 * @author Chris de Vreeze
 */
public class OptionalExample {

    public static Optional<Double> getAverage(int... numbers) {
        return IntStream.of(numbers).average().stream().boxed().findFirst();
    }

    public static Optional<Double> getAverageUsingAveragingCollector(int... numbers) {
        return IntStream.of(numbers).boxed()
                .collect(
                        Collectors.collectingAndThen(
                                Collectors.averagingInt(i -> i),
                                v -> (numbers.length == 0) ? Optional.empty() : Optional.of(v))
                );
    }

    public static Optional<Double> getAverageUsingSum(int... numbers) {
        double sum = IntStream.of(numbers).asDoubleStream().sum();

        return OptionalInt.of(numbers.length).stream().filter(cnt -> cnt > 0).boxed().map(cnt -> sum / cnt).findFirst();
    }

    public record NumbersSumAndCount(double sum, int count) {
    }

    public static Optional<Double> getAverageUsingReduce(int... numbers) {
        return IntStream.of(numbers).asDoubleStream().mapToObj(i -> new NumbersSumAndCount(i, 1))
                .reduce((x, y) -> new NumbersSumAndCount(x.sum() + y.sum(), x.count() + y.count()))
                .map(v -> v.sum() / v.count());
    }

    public static Optional<Double> getAverageUsingTeeing(int... numbers) {
        return IntStream.of(numbers).boxed().collect(
                Collectors.teeing(
                        Collectors.summingDouble(i -> (double) i),
                        Collectors.counting(),
                        (sum, cnt) -> (cnt == 0) ? Optional.empty() : Optional.of(sum / cnt)
                )
        );
    }

    public static Optional<Double> getAverageUsingSideEffects(int... numbers) {
        final var sum = new AtomicLong(0); // Mutable, but still effectively final as reference

        // Looping using Stream.iterate
        Stream.iterate(0, i -> i < numbers.length, i -> i + 1).forEach(i -> {
            sum.addAndGet(numbers[i]); // side-effect
        });

        return Optional.of((double) sum.get()).filter(ignored -> numbers.length > 0).map(s -> s / (numbers.length));
    }

    public static Optional<Double> getAverageUsingLimitAndSideEffects(int... numbers) {
        final var sum = new AtomicLong(0); // Mutable, but still effectively final as reference

        // Looping using Stream.iterate
        Stream.iterate(0, i -> i + 1).limit(numbers.length).forEach(i -> {
            sum.addAndGet(numbers[i]); // side-effect
        });

        return Optional.of((double) sum.get()).filter(ignored -> numbers.length > 0).map(s -> s / (numbers.length));
    }

    public static void averageExample() {
        List.<List<Integer>>of(
                List.of(1),
                List.of(1, 2, 3, 4),
                List.of(),
                List.of(0),
                List.of(2, 4, 6),
                List.of(-4, 78, 23, 67, 1344, 47, -233, -3542, 4565)
        ).forEach(list -> {
            var optAverage = getAverage(list.stream().mapToInt(i -> i).toArray());
            System.out.printf("Numbers: %s. Optional average: %s%n", list, optAverage.map(i -> String.format("%.2f", i)));

            if (!optAverage.equals(getAverageUsingAveragingCollector(list.stream().mapToInt(i -> i).toArray()))) {
                throw new RuntimeException("Some inconsistency between 2 methods computing average values (1)");
            }

            if (!optAverage.equals(getAverageUsingSum(list.stream().mapToInt(i -> i).toArray()))) {
                throw new RuntimeException("Some inconsistency between 2 methods computing average values (2)");
            }

            if (!optAverage.equals(getAverageUsingReduce(list.stream().mapToInt(i -> i).toArray()))) {
                throw new RuntimeException("Some inconsistency between 2 methods computing average values (3)");
            }

            if (!optAverage.equals(getAverageUsingTeeing(list.stream().mapToInt(i -> i).toArray()))) {
                throw new RuntimeException("Some inconsistency between 2 methods computing average values (4)");
            }

            if (!optAverage.equals(getAverageUsingSideEffects(list.stream().mapToInt(i -> i).toArray()))) {
                throw new RuntimeException("Some inconsistency between 2 methods computing average values (5)");
            }

            if (!optAverage.equals(getAverageUsingLimitAndSideEffects(list.stream().mapToInt(i -> i).toArray()))) {
                throw new RuntimeException("Some inconsistency between 2 methods computing average values (6)");
            }
        });
    }

    public static void main(String[] args) {
        averageExample();
    }
}
