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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Showing "Stream.toList()" in terms of "Stream.reduce".
 *
 * @author Chris de Vreeze
 */
public class ToListAsReduceExample {

    public static <T> List<T> toList(Stream<T> stream) {
        return stream.collect(Collectors.toList());
    }

    public static <T> List<T> toList2(Stream<T> stream) {
        return stream.collect(Collectors.toCollection(ArrayList::new));
    }

    public static <T> List<T> toList3(Stream<T> stream) {
        Supplier<List<T>> makeList = ArrayList::new;

        return stream.collect(Collector.of(
                makeList,
                List::add,
                (acc1, acc2) -> {
                    acc1.addAll(acc2);
                    return acc1;
                }
        ));
    }

    public static <T> List<T> toList4(Stream<T> stream) {
        Supplier<List<T>> makeList = ArrayList::new;

        return stream.reduce(
                makeList.get(),
                (acc, elem) -> {
                    acc.add(elem);
                    return acc;
                },
                (acc1, acc2) -> {
                    acc1.addAll(acc2);
                    return acc1;
                }
        );
    }

    private static void checkToListComputations() {
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
            if (areNotEqual(toList(list.stream()), list.stream().toList())) {
                throw new IllegalStateException("Mismatch between 2 toList computations (1)");
            }
            if (areNotEqual(list.stream().toList(), list)) {
                throw new IllegalStateException("Mismatch between toList computation and List itself");
            }
            if (areNotEqual(toList2(list.stream()), list.stream().toList())) {
                throw new IllegalStateException("Mismatch between 2 toList computations (2)");
            }
            if (areNotEqual(toList3(list.stream()), list.stream().toList())) {
                throw new IllegalStateException("Mismatch between 2 toList computations (3)");
            }
            if (areNotEqual(toList4(list.stream()), list.stream().toList())) {
                throw new IllegalStateException("Mismatch between 2 toList computations (4)");
            }
        });
    }

    private static <T> boolean areEqual(List<T> list1, List<T> list2) {
        return list1.equals(list2);
    }

    private static <T> boolean areNotEqual(List<T> list1, List<T> list2) {
        return !areEqual(list1, list2);
    }

    public static void main(String[] args) {
        checkToListComputations();
    }
}
