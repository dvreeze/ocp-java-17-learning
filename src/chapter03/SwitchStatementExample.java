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
 * Switch statement example.
 *
 * @author Chris de Vreeze
 */
public class SwitchStatementExample {

    private static Optional<Boolean> isSmallEvenNumber(int n) {
        var evenAndSmall = Optional.<Boolean>empty();
        switch (n) {
            case 2:
            case 4:
            case 6, 8, 10: case 12, 14:
            case 16, 18:
                evenAndSmall = Optional.of(true);
                break;
            case 1, 3, 5: case 7, 9:
            case 11, 13, 15: case 17:
                evenAndSmall = Optional.of(false);
                break;
            default:
                evenAndSmall = Optional.of(false);
                break;
        }
        return evenAndSmall;
    }

    public static void main(String[] args) {
        var i = 11;
        // Must return false
        System.out.println(i + " is evenAndSmall: " + isSmallEvenNumber(i));
        i = 12;
        // Must return true
        System.out.println(i + " is evenAndSmall: " + isSmallEvenNumber(i));
    }
}
