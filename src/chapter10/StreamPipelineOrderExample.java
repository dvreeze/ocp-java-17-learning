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
 * Program showing the order of processing in a Stream pipeline.
 *
 * @author Chris de Vreeze
 */
public class StreamPipelineOrderExample {

    public static void main(String[] args) {
        System.out.println("Going to create a Stream (without using it in a stream pipeline yet) ...");
        Stream<String> stringStream = Stream.of(
                makeString("First string"),
                makeString("Ignore"),
                makeString("Second string"),
                makeString("Third string"),
                makeString("Ignore"),
                makeString("Fourth string")
        );
        System.out.println("Just created a stream (without it being used in a stream pipeline yet)");

        System.out.println("Going to create the intermediary operations ...");
        Stream<String> adaptedStream = stringStream
                .flatMap(s -> repeat(s, 3))
                .filter(s -> {
                    System.out.printf("\tFiltering away 'ignore' (from '%s')%n", s);
                    return !s.equalsIgnoreCase("ignore");
                })
                .filter(s -> {
                    System.out.printf("\tFiltering away empty strings (from '%s')%n", s);
                    return !s.isEmpty();
                })
                .map(s -> {
                    System.out.printf("\tMake first letter lowercase (from '%s')%n", s);
                    return s.substring(0, 1).toLowerCase() + s.substring(1);
                });
        System.out.println("Just created intermediary operations (but those steps have not run yet)");

        System.out.println("Going to call terminal operation of stream pipeline (triggering the intermediary operations as well) ...");
        List<String> resultStrings = adaptedStream.toList();
        System.out.println("Just called terminal operation of stream pipeline");

        System.out.println();
        System.out.println("Result:");

        resultStrings.forEach(System.out::println);
    }

    private static String makeString(String s) {
        System.out.printf("\tString: '%s'%n", s);
        return s;
    }

    private static Stream<String> repeat(String s, int times) {
        System.out.printf("\tRepeating string '%s' %d times%n", s, times);
        return Stream.generate(() -> s).limit(times);
    }
}
