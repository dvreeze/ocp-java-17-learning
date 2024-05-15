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

package chapter06;

/**
 * Example showing class initialization order, without inheritance.
 * <p>
 * This shows that static field declarations and static initializers are run in order of occurrence in the class,
 * as opposed to first the static field declarations and after that the static initializers. The book is wrong here.
 *
 * @author Chris de Vreeze
 */
public class ClassInitializationOrderExample {

    private static int intField = initializeIntField(1, true);

    private static char charField = initializeCharField('a', true);

    static {
        charField = initializeCharField('b', false);
    }

    private static String stringField = initializeStringField("A", true);

    static {
        intField = initializeIntField(2, false);
    }

    private static double doubleField = initializeDoubleField(1.0, true);

    static {
        stringField = initializeStringField("B", false);
    }

    static {
        doubleField = initializeDoubleField(2.0, false);
    }

    public static void main(String[] args) {
        System.out.println("Class initialized");
    }

    private static int initializeIntField(int value, boolean inDeclaration) {
        System.out.printf("Initializing integer field to %d (in declaration: %s)%n", value, inDeclaration);
        return value;
    }

    private static char initializeCharField(char value, boolean inDeclaration) {
        System.out.printf("Initializing character field to %s (in declaration: %s)%n", value, inDeclaration);
        return value;
    }

    private static String initializeStringField(String value, boolean inDeclaration) {
        System.out.printf("Initializing string field to %s (in declaration: %s)%n", value, inDeclaration);
        return value;
    }

    private static double initializeDoubleField(double value, boolean inDeclaration) {
        System.out.printf("Initializing double field to %.2f (in declaration: %s)%n", value, inDeclaration);
        return value;
    }
}
