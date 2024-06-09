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
import java.util.function.BooleanSupplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * This program "plays" with some Stream properties.
 *
 * @author Chris de Vreeze
 */
public class StreamPropertiesExample {

    public static void main(String[] args) {
        List<Integer> intList = IntStream.rangeClosed(1, 1_000).boxed().toList();

        requireFalse(() -> intList.stream().isParallel());
        requireTrue(() -> intList.parallelStream().isParallel());
        requireTrue(() -> intList.stream().parallel().isParallel());
        requireFalse(() -> makeParallel(makeParallel(makeSequential(intList.parallelStream()))).sequential().isParallel());
        requireFalse(() -> makeParallel(intList.stream()).sequential().filter(i -> i % 2 == 0).map(i -> i * 2).isParallel());
        requireFalse(() -> makeParallel(intList.stream()).filter(i -> i % 2 == 0).map(i -> i * 2).sequential().isParallel());

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
}
