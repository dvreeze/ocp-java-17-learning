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
import java.util.stream.Stream;

/**
 * Showing "Stream.count()" in terms of "Stream.reduce".
 *
 * @author Chris de Vreeze
 */
public class CountAsReduceExample {

    public static <T> long count(Stream<T> stream) {
        return stream.reduce(
                0L,
                (acc, nextElem) -> acc + 1L,
                Long::sum
        );
    }

    public static <T> long count2(Stream<T> stream) {
        return stream.mapToLong(ignored -> 1L).sum();
    }

    public static <T> long count3(Stream<T> stream) {
        return stream.mapToLong(ignored -> 1L).reduce(0L, Long::sum);
    }

    private static void checkCountComputations() {
        List<List<Double>> lists = List.of(
                Stream.generate(Math::random).limit(120000).toList(),
                Stream.generate(Math::random).limit(15).toList(),
                Stream.generate(Math::random).limit(1).toList(),
                Stream.generate(Math::random).limit(3).toList(),
                Stream.generate(Math::random).limit(0).toList(),
                Stream.generate(Math::random).limit(240000).toList(),
                Stream.generate(Math::random).limit(125).toList()
        );
        lists.forEach(list -> {
            if (count(list.parallelStream()) != list.stream().count()) {
                throw new IllegalStateException("Mismatch between 2 count computations (1)");
            }
            if (list.parallelStream().count() != list.size()) {
                throw new IllegalStateException("Mismatch between count and collection size computations");
            }
            if (count2(list.parallelStream()) != list.stream().count()) {
                throw new IllegalStateException("Mismatch between 2 count computations (2)");
            }
            if (count3(list.parallelStream()) != list.stream().count()) {
                throw new IllegalStateException("Mismatch between 2 count computations (3)");
            }
        });
    }

    public static void main(String[] args) {
        checkCountComputations();
    }
}
