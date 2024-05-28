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
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * "String joining" example, using Collectors, and showing (naive) equivalence with another Collector.
 *
 * @author Chris de Vreeze
 */
public class JoiningExample {

    public static String joining(Stream<String> stream, String delimiter, String prefix, String suffix) {
        return stream.collect(Collectors.joining(delimiter, prefix, suffix));
    }

    public static String joining2(Stream<String> stream, String delimiter, String prefix, String suffix) {
        Collector<String, StringBuilder, String> collector = Collector.of(
                () -> new StringBuilder(prefix),
                (acc, nextElem) -> {
                    append(acc, nextElem, delimiter, prefix);
                },
                (acc1, acc2) -> {
                    append(acc1, acc2.toString(), delimiter, prefix);
                    return acc1;
                },
                sb -> {
                    sb.append(suffix);
                    return sb.toString();
                }
        );
        return stream.collect(collector);
    }

    public static String joining3(Stream<String> stream, String delimiter, String prefix, String suffix) {
        Collector<String, StringBuilder, StringBuilder> collector = Collector.of(
                () -> new StringBuilder(prefix),
                (acc, nextElem) -> {
                    append(acc, nextElem, delimiter, prefix);
                },
                (acc1, acc2) -> {
                    append(acc1, acc2.toString(), delimiter, prefix);
                    return acc1;
                }
        );
        return stream.collect(Collectors.collectingAndThen(collector, sb -> {
            sb.append(suffix);
            return sb.toString();
        }));
    }

    private static void append(StringBuilder sb, String toAppend, String delimiter, String prefix) {
        if (!sb.toString().equals(prefix)) {
            sb.append(delimiter);
        }
        sb.append(toAppend);
    }

    private static void showResults(Supplier<Stream<String>> streamSupplier) {
        var delimiter = ", ";
        var prefix = "[ ";
        var suffix = " ]";

        String result1 = joining(streamSupplier.get(), delimiter, prefix, suffix);
        String result2 = joining2(streamSupplier.get(), delimiter, prefix, suffix);
        String result3 = joining3(streamSupplier.get(), delimiter, prefix, suffix);

        System.out.println();
        System.out.printf("Result 1: %s%n", result1);
        System.out.printf("Result 2: %s%n", result2);
        System.out.printf("Result 3: %s%n", result3);

        if (Stream.of(result1, result2, result3).distinct().count() != 1) {
            throw new IllegalStateException("Mismatch between different ways of joining strings");
        }
    }

    public static void main(String[] args) {
        List<List<String>> stringLists = List.of(
                List.of("abc"),
                List.of(),
                List.of("abc", "def", "ghi", "jkl", "mno", "pqr", "stu", "vw", "xyz"),
                List.of("1", "3", "5"),
                Stream.generate(() -> "test").limit(5).toList()
        );
        stringLists.forEach(stringList -> showResults(stringList::stream));
    }
}
