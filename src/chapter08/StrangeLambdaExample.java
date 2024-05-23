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

package chapter08;

import java.util.concurrent.atomic.AtomicLong;
import java.util.function.LongSupplier;
import java.util.stream.LongStream;

/**
 * Example of a "strange" lambda, taking an effectively final local variable from its context that is nevertheless mutable.
 *
 * @author Chris de Vreeze
 */
public class StrangeLambdaExample {

    public static void main(String[] args) {
        // Effectively final local variable that is used in the lambda below, but mutable all the same
        var counter = new AtomicLong(0L);

        LongSupplier supplier = () -> {
            var x = counter.incrementAndGet();
            return x * x;
        };

        long result = LongStream.generate(supplier)
                .takeWhile(ignored -> counter.get() <= 1000L)
                .reduce(Math::max)
                .orElse(0L);

        System.out.println(result);
    }
}
