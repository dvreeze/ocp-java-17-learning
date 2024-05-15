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

package chapter05.square;

import chapter05.rectangle.Rectangle;

/**
 * Subclass Square of Rectangle (in a different package).
 *
 * @author Chris de Vreeze
 */
public class Square extends Rectangle {

    public Square(double width) {
        super(width, width);
    }

    public void printArea() {
        // Accessing protected member of superclass in this subclass
        System.out.println(area());
    }

    public static void printArea(double width) {
        // Within this subclass Square, create a Square object and use protected members of the superclass via this variable
        Square square = new Square(width);
        System.out.println(square.area());
    }
}
