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

package chapter03;

import java.util.Optional;

/**
 * Switch expression example.
 *
 * @author Chris de Vreeze
 */
public class SwitchExpressionExample {

    private static boolean isEven(int k) {
        return k % 2 == 0;
    }

    private static Optional<Boolean> isSmallEvenNumber(int n) {
        // Note that we are not even allowed to use the complex "case syntax" that switch statements permit.
        // But foremost we get rid of the break statements that are basically always needed in switch statements.
        // Both are big improvements, as far as I am concerned.
        return
            switch (n) {
                case 2, 4, 6, 8, 10, 12, 14, 16, 18 -> Optional.of(true);
                case 1, 3, 5, 7, 9, 11, 13, 15, 17 -> {
                    // If we use a block, we need to "yield" a value (in all code paths, except where exceptions are thrown)
                    if (isEven(n)) {
                        // Not the case, obviously
                        yield Optional.of(true);
                    } else {
                        yield Optional.of(false);
                    }
                }
                default -> Optional.empty();
        };
    }

    public static void main(String[] args) {
        var i = 11;
        // Must return false
        System.out.println(i + " is evenAndSmall: " + isSmallEvenNumber(i));
        i = 12;
        // Must return true
        System.out.println(i + " is evenAndSmall: " + isSmallEvenNumber(i));
        i = 124;
        // Must return false
        System.out.println(i + " is evenAndSmall: " + isSmallEvenNumber(i));
    }
}
