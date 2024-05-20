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

import java.util.List;
import java.util.Objects;

/**
 * Local class example, showing that regular local classes can access the outer class instance if defined
 * within an instance method.
 *
 * @author Chris de Vreeze
 */
public class LocalClassExample {

    public static boolean isLocalClass(Class<?> clazz) {
        return clazz.isLocalClass();
    }

    public static void useLocalClassesInStaticContext() {
        interface NestedTypeMember {
        }

        class RegularClass implements NestedTypeMember {
        }

        record RecordClass(String stringComponent) implements NestedTypeMember {
        }

        enum EnumClass implements NestedTypeMember {ENUM_VALUE}

        List.of(NestedTypeMember.class, RegularClass.class, RecordClass.class, EnumClass.class)
                .forEach(cls -> System.out.printf("Method 'useLocalClassesInStaticContext'. Class %s is a local class: %b%n", cls, isLocalClass(cls)));
    }

    public void useLocalClassesInInstanceContext() {
        interface NestedTypeMember {
        }

        class RegularClass implements NestedTypeMember {
            {
                var outerInstance = LocalClassExample.this;
                Objects.requireNonNull(outerInstance);
            }

            public void validateExistenceOfOuterInstance() {
                var outerInstance = LocalClassExample.this;
                Objects.requireNonNull(outerInstance);
            }
        }

        record RecordClass(String stringComponent) implements NestedTypeMember {
        }

        enum EnumClass implements NestedTypeMember {ENUM_VALUE}

        List.of(NestedTypeMember.class, RegularClass.class, RecordClass.class, EnumClass.class)
                .forEach(cls -> System.out.printf("Method 'useLocalClassesInInstanceContext'. Class %s is a local class: %b%n", cls, isLocalClass(cls)));

        RegularClass obj = new RegularClass();
        obj.validateExistenceOfOuterInstance();

    }

    public static void main(String... args) {
        useLocalClassesInStaticContext();

        var outerObject = new LocalClassExample();
        outerObject.useLocalClassesInInstanceContext();
    }
}
