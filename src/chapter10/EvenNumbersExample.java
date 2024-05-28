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
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Example program showing different ways to create streams/collections of even numbers, using method "Stream.iterate".
 *
 * @author Chris de Vreeze
 */
public class EvenNumbersExample {

    public static void main(String[] args) {
        System.out.printf("Even numbers <= 10: %s%n", getEvenNumbers());

        List<List<Integer>> distinctResults = Stream.of(
                getEvenNumbers(),
                getEvenNumbersUsingTakeWhile(),
                getEvenNumbersUsingFilterAndTakeWhile(),
                getEvenNumbersUsingFilteringInCollectors()
        ).distinct().toList();

        if (distinctResults.size() != 1) {
            throw new IllegalStateException("Not all even number computations led to the same results");
        }
    }

    private static List<Integer> getEvenNumbers() {
        return Stream.iterate(0, i -> i <= 10, i -> i + 2).collect(Collectors.toList());
    }

    private static List<Integer> getEvenNumbersUsingTakeWhile() {
        return Stream.iterate(0, i -> i + 2).takeWhile(i -> i <= 10).collect(Collectors.toList());
    }

    private static List<Integer> getEvenNumbersUsingFilterAndTakeWhile() {
        return Stream.iterate(0, i -> i + 1).filter(i -> i % 2 == 0).takeWhile(i -> i <= 10).collect(Collectors.toList());
    }

    private static List<Integer> getEvenNumbersUsingFilteringInCollectors() {
        return Stream.iterate(0, i -> i <= 10, i -> i + 1)
                .collect(Collectors.filtering(i -> i % 2 == 0, Collectors.toList()));
    }
}
