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
import java.util.Optional;
import java.util.stream.IntStream;

/**
 * Example showing type "Optional".
 *
 * @author Chris de Vreeze
 */
public class OptionalExample {

    public static Optional<Double> getAverage(int... numbers) {
        return IntStream.of(numbers).average().stream().boxed().findFirst();
    }

    public static void averageExample() {
        List.<List<Integer>>of(
                List.of(1),
                List.of(1, 2, 3, 4),
                List.of(),
                List.of(2, 4, 6),
                List.of(-4, 78, 23, 67, 1344, 47, -233, -3542, 4565)
        ).forEach(list -> {
            var optAverage = getAverage(list.stream().mapToInt(i -> i).toArray());
            System.out.printf("Numbers: %s. Optional average: %s%n", list, optAverage.map(i -> String.format("%.2f", i)));
        });
    }

    public static void main(String[] args) {
        averageExample();
    }
}
