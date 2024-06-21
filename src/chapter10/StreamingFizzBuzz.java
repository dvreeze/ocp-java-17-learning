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

    private static final int MAX_NUMBER = 1_000_000;

    // Far more efficient than computing remainder

    private static boolean isDivisibleBy3(int i) {
        var sumOfDigits =
                String.valueOf(i).chars().filter(Character::isDigit).map(Character::getNumericValue).sum();

        if (sumOfDigits >= 0 && sumOfDigits < 10) {
            return sumOfDigits == 3 || sumOfDigits == 6 || sumOfDigits == 9;
        } else {
            // Recursive call
            return isDivisibleBy3(sumOfDigits);
        }
    }

    private static boolean isDivisibleBy5(int i) {
        var s = String.valueOf(i);
        return s.endsWith("5") || s.endsWith("0");
    }

    private static boolean isDivisibleBy15(int i) {
        return isDivisibleBy5(i) && isDivisibleBy3(i);
    }

    private static String convertNumber(int i) {
        if (isDivisibleBy15(i)) {
            return "FizzBuzz";
        } else if (isDivisibleBy3(i)) {
            return "Fizz";
        } else if (isDivisibleBy5(i)) {
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

    private static List<String> limitSolution(List<String> solution, int limit) {
        return solution.stream().limit(limit).toList();
    }

    public static List<String> fizzBuzz1() {
        return IntStream.rangeClosed(1, MAX_NUMBER)
                .parallel() // Just for fun
                .mapToObj(StreamingFizzBuzz::convertNumber)
                .toList();
    }

    public static List<String> fizzBuzz2() {
        return IntStream.rangeClosed(1, MAX_NUMBER)
                .parallel() // Just for fun
                .mapToObj(Objects::toString)
                .map(s -> getOptionalNumber(s).stream().filter(StreamingFizzBuzz::isDivisibleBy15).mapToObj(i -> "FizzBuzz").findFirst().orElse(s))
                .map(s -> getOptionalNumber(s).stream().filter(StreamingFizzBuzz::isDivisibleBy3).mapToObj(i -> "Fizz").findFirst().orElse(s))
                .map(s -> getOptionalNumber(s).stream().filter(StreamingFizzBuzz::isDivisibleBy5).mapToObj(i -> "Buzz").findFirst().orElse(s))
                .toList();
    }

    public static List<String> fizzBuzz3() {
        return IntStream.rangeClosed(1, MAX_NUMBER)
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

        return IntStream.rangeClosed(1, MAX_NUMBER)
                .parallel() // Just for fun
                .boxed()
                .collect(collector); // A bit over the top, but still equivalent
    }

    public static List<String> fizzBuzz5() {
        Collector<Integer, ?, List<String>> collector = Collector.of(
                ArrayList::new,
                (accList, nextElem) -> accList.add(convertNumber(nextElem)),
                (accList1, accList2) -> {
                    accList1.addAll(accList2);
                    return accList1;
                }
        );

        return IntStream.rangeClosed(1, MAX_NUMBER)
                .parallel() // Just for fun
                .boxed()
                .collect(collector); // A bit over the top, but still equivalent
    }

    public static List<String> fizzBuzz6() {
        return IntStream.rangeClosed(1, MAX_NUMBER)
                .parallel() // Just for fun
                .boxed()
                .collect(
                        ArrayList::new,
                        (accList, nextElem) -> accList.add(convertNumber(nextElem)),
                        ArrayList::addAll
                ); // A bit over the top, but still equivalent
    }

    public static void main(String[] args) {
        fizzBuzz1().forEach(System.out::println);

        var limit = 1000;
        Set<List<String>> partialSolutions = Stream.of(
                limitSolution(fizzBuzz1(), limit),
                limitSolution(fizzBuzz2(), limit),
                limitSolution(fizzBuzz3(), limit),
                limitSolution(fizzBuzz4(), limit),
                limitSolution(fizzBuzz5(), limit),
                limitSolution(fizzBuzz6(), limit)
        ).collect(Collectors.toSet());

        if (partialSolutions.size() != 1) {
            throw new RuntimeException("Not all FizzBuzz solutions are the same, which is an error");
        }
    }
}
