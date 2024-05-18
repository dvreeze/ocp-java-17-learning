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

package chapter07;

/**
 * Enum for (zoo) seasons. Taken from the OCP preparation book.
 *
 * @author Chris de Vreeze
 */
public enum ZooSeason {

    // No "new" keyword, again illustrating that the compiler creates the enum values and creates them only once
    WINTER("Low") {
        public String getHours() {
            return "10am-3pm";
        }
    },
    SPRING("Medium"),
    SUMMER("High") {
        public String getHours() {
            return "9am-7pm";
        }
    },
    FALL("Medium");

    private final String expectedVisitors; // Immutable field, as should be the case within enum values

    // The constructor is implicitly private, and cannot be changed to protected or public
    ZooSeason(String expectedVisitors) {
        this.expectedVisitors = expectedVisitors;
    }

    public String getHours() {
        return "9am-5pm";
    }

    public void printExpectedVisitors() {
        System.out.println(expectedVisitors);
    }
}
