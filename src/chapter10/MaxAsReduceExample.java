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

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Showing "Stream.max(Comparator&lt;? super T&gt;)" in terms of "Stream.reduce".
 *
 * @author Chris de Vreeze
 */
public class MaxAsReduceExample {

    public static <T> Optional<T> max(Stream<T> stream, Comparator<? super T> comparator) {
        return stream.reduce(
                Optional.empty(),
                (acc, nextElem) -> max(acc, Optional.of(nextElem), comparator),
                (acc1, acc2) -> max(acc1, acc2, comparator)
        );
    }

    private static <T> Optional<T> max(Optional<T> optI, Optional<T> optJ, Comparator<? super T> comparator) {
        return (optI.isEmpty() && optJ.isEmpty()) ? Optional.empty() :
                (optI.isEmpty()) ? optJ :
                        (optJ.isEmpty()) ? optI :
                                (comparator.compare(optI.orElseThrow(), optJ.orElseThrow()) < 0) ? optJ :
                                        optI;
    }

    private static void checkMaxComputations() {
        List<List<Double>> lists = List.of(
                Stream.generate(Math::random).limit(120000).toList(),
                Stream.generate(Math::random).limit(15).toList(),
                Stream.generate(Math::random).limit(1).toList(),
                Stream.generate(Math::random).limit(3).toList(),
                Stream.generate(Math::random).limit(0).toList(),
                Stream.generate(Math::random).limit(240000).toList(),
                Stream.generate(Math::random).limit(125).toList()
        );

        Comparator<Double> comparator = Comparator.naturalOrder();

        lists.forEach(list -> {
            if (!max(list.parallelStream(), comparator).equals(list.stream().max(comparator))) {
                throw new IllegalStateException("Mismatch between 2 max computations (1)");
            }
            if (!list.isEmpty() && !list.parallelStream().max(comparator).orElseThrow().equals(Collections.max(list, comparator))) {
                throw new IllegalStateException("Mismatch between count and collection size computations");
            }
        });
    }

    public static void main(String[] args) {
        checkMaxComputations();
    }
}
