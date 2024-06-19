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

package chapter09;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Showing 2-way updates through Arrays.asList.
 *
 * @author Chris de Vreeze
 */
public class ArraysAsListExample {

    public static void main(String[] args) {
        Integer[] arr = IntStream.range(0, 10).boxed().toArray(Integer[]::new);
        System.out.printf("Array: %s%n", Arrays.toString(arr));

        List<Integer> list = Arrays.asList(arr);

        // Update through List view
        list.set(2, 24);

        // Update of underlying array
        arr[2] *= 2;

        System.out.printf("Element arr[2] is now: %d%n", arr[2]);
        System.out.printf("Element arr[2] is now (via List view): %d%n", list.get(2));

        System.out.printf("Entire updated array:     %s%n", Arrays.toString(arr));
        System.out.printf("Entire updated List view: %s%n", list);

        var areEqual = Arrays.stream(arr).toList().equals(list);

        System.out.printf("They are equal: %b%n", areEqual);

        // In-place updates
        list.replaceAll(i -> i * 2);

        // In-place sorting
        list.sort(Comparator.naturalOrder());

        System.out.printf("Entire updated array (after doubling and sorting):     %s%n", Arrays.toString(arr));
        System.out.printf("Entire updated List view (after doubling and sorting): %s%n", list);

        var areStillEqual = Arrays.stream(arr).toList().equals(list);

        System.out.printf("They are still equal: %b%n", areStillEqual);
    }
}
