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

import java.util.ArrayList;
import java.util.List;

/**
 * Example showing the order in which "initialisation" is run when an object is constructed.
 * <p>
 * Note that the constructor code is run after the fields and instance initializers, the latter 2 in
 * order of occurrence.
 *
 * @author Chris de Vreeze
 */
public class ConstructionLifecycleExample {

    public static void main(String[] args) {
        System.out.println("About to construct an instance of the class ...");

        // Create a new instance of the class, and store the reference to it in a local variable
        var classInstance = new ConstructionLifecycleExample();

        classInstance.instanceField.forEach(System.out::println);
    }

    // Constructor
    public ConstructionLifecycleExample() {
        instanceField.add("3. Constructor code only runs after field initializers and instance initializers");
    }

    // Instance field
    private final List<String> instanceField = createField("1. Instance field directly initialized");

    // Instance initializer (cannot appear before corresponding field(s))
    {
        instanceField.add("2. Instance initializer");
    }

    // Static members can obviously be used by (shorter living) instance members, but not the other way around
    private static List<String> createField(String text) {
        var result = new ArrayList<String>();
        result.add(text);
        return result;
    }
}
