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
 * Example showing object initialization order, without inheritance.
 * <p>
 * This shows that instance field declarations and instance initializers are run in order of occurrence in the class,
 * as opposed to first the instance field declarations and after that the instance initializers. The book is wrong here.
 * It is true, though, that constructors themselves run after the instance field declarations and instance initializers.
 *
 * @author Chris de Vreeze
 */
public class ObjectInitializationOrderExample {

    private int intField = initializeIntField(1, true, false);

    {
        charField = initializeCharField('b', false, false);
    }

    public ObjectInitializationOrderExample() {
        this.charField = initializeCharField('c', false, true);
        this.doubleField = initializeDoubleField(3.0, false, true);
        this.stringField = initializeStringField("C", false, true);
        this.intField = initializeIntField(3, false, true);
    }

    private String stringField = initializeStringField("A", true, false);

    {
        intField = initializeIntField(2, false, false);
    }

    private double doubleField = initializeDoubleField(1.0, true, false);

    {
        stringField = initializeStringField("B", false, false);
    }

    {
        doubleField = initializeDoubleField(2.0, false, false);
    }

    // Way after the initializer that also initializes this field; this is apparently allowed
    private char charField = initializeCharField('a', true, false);

    public static void main(String[] args) {
        var obj = new ObjectInitializationOrderExample();
        System.out.println("Object initialized");
        System.out.printf("Character field value: %s%n", obj.charField);
        System.out.printf("Integer field value: %d%n", obj.intField);
        System.out.printf("Double field value: %.2f%n", obj.doubleField);
        System.out.printf("String field value: %s%n", obj.stringField);
    }

    private static int initializeIntField(int value, boolean inDeclaration, boolean inConstructor) {
        System.out.printf("Initializing integer field to %d (in declaration: %s; in constructor: %s)%n", value, inDeclaration, inConstructor);
        return value;
    }

    private static char initializeCharField(char value, boolean inDeclaration, boolean inConstructor) {
        System.out.printf("Initializing character field to %s (in declaration: %s; in constructor: %s)%n", value, inDeclaration, inConstructor);
        return value;
    }

    private static String initializeStringField(String value, boolean inDeclaration, boolean inConstructor) {
        System.out.printf("Initializing string field to %s (in declaration: %s; in constructor: %s)%n", value, inDeclaration, inConstructor);
        return value;
    }

    private static double initializeDoubleField(double value, boolean inDeclaration, boolean inConstructor) {
        System.out.printf("Initializing double field to %.2f (in declaration: %s; in constructor: %s)%n", value, inDeclaration, inConstructor);
        return value;
    }
}
