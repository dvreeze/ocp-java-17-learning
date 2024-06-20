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
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * FizzBuzz using Java Streams.
 *
 * @author Chris de Vreeze
 */
public class StreamingFizzBuzz {

    private static String convertNumber(int i) {
        if (i % 15 == 0) {
            return "FizzBuzz";
        } else if (i % 3 == 0) {
            return "Fizz";
        } else if (i % 5 == 0) {
            return "Buzz";
        } else {
            return String.valueOf(i);
        }
    }

    private static OptionalInt getOptionalNumber(String s) {
        try {
            return OptionalInt.of(Integer.parseInt(s));
        } catch (RuntimeException e) {
            return OptionalInt.empty();
        }
    }

    public static List<String> fizzBuzz1() {
        return IntStream.rangeClosed(1, 100)
                .parallel() // Just for fun
                .mapToObj(StreamingFizzBuzz::convertNumber)
                .toList();
    }

    public static List<String> fizzBuzz2() {
        return IntStream.rangeClosed(1, 100)
                .parallel() // Just for fun
                .mapToObj(Objects::toString)
                .map(s -> getOptionalNumber(s).stream().filter(i -> i % 15 == 0).mapToObj(i -> "FizzBuzz").findFirst().orElse(s))
                .map(s -> getOptionalNumber(s).stream().filter(i -> i % 3 == 0).mapToObj(i -> "Fizz").findFirst().orElse(s))
                .map(s -> getOptionalNumber(s).stream().filter(i -> i % 5 == 0).mapToObj(i -> "Buzz").findFirst().orElse(s))
                .toList();
    }

    public static List<String> fizzBuzz3() {
        return IntStream.rangeClosed(1, 100)
                .parallel() // Just for fun
                .boxed()
                .collect(Collectors.toMap(Function.identity(), StreamingFizzBuzz::convertNumber))
                .entrySet()
                .stream()
                .parallel() // Just for fun
                .sorted(Comparator.comparingInt(Map.Entry::getKey))
                .map(Map.Entry::getValue)
                .toList();
    }

    public static List<String> fizzBuzz4() {
        Function<Integer, String> f = StreamingFizzBuzz::convertNumber;
        Collector<Integer, ?, List<String>> collector = Collectors.mapping(f, Collectors.toList());

        return IntStream.rangeClosed(1, 100)
                .parallel() // Just for fun
                .boxed()
                .collect(collector); // A bit over the top, but still equivalent
    }

    public static void main(String[] args) {
        fizzBuzz1().forEach(System.out::println);

        if (Stream.of(fizzBuzz1(), fizzBuzz2(), fizzBuzz3(), fizzBuzz4()).distinct().toList().size() != 1) {
            throw new RuntimeException("Not all FizzBuzz solutions are the same, which is an error");
        }
    }
}
