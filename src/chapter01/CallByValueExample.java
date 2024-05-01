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

package chapter01;

import java.util.Date;

/**
 * Showing call-by-value, even if the value is a reference to a mutable object.
 *
 * @author Chris de Vreeze
 */
public class CallByValueExample {

    // Of course, this is the exact opposite of good FP practices, but that's not the point here

    private static void addSecond(long dateInEpochMs) {
        dateInEpochMs += 1000L;
    }

    private static void addSecond(Date date) {
        date.setTime(date.getTime() + 1000L);
    }

    public static void main(String[] args) {
        var now = new Date();
        System.out.println("1. Date: " + now);

        // Call-by-value passing primitive data; nothing is updated in the caller
        addSecond(now.getTime());

        System.out.println("2. Date (after 'fake update'): " + now);

        // Call-by-value passing a reference to a mutable Date; it is updated in the caller as a result
        addSecond(now);

        System.out.println("3. Date (after update, a second later): " + now);
    }
}
