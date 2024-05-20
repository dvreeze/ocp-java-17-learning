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

import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Objects;

/**
 * Nested class example, showing that nested interfaces/records/enums are implicitly static.
 *
 * @author Chris de Vreeze
 */
public class NestedClassExample {

    public interface NestedTypeMember {
    }

    public class InnerClass implements NestedTypeMember {
        {
            var outerInstance = NestedClassExample.this;
            Objects.requireNonNull(outerInstance);
        }
    }

    public static class StaticMemberClass implements NestedTypeMember {
    }

    public record RecordClass(String stringComponent) implements NestedTypeMember {
    }

    public enum EnumClass implements NestedTypeMember {ENUM_VALUE}

    public static boolean isStaticTypeMember(Class<?> clazz) {
        if (!clazz.isMemberClass()) {
            throw new IllegalArgumentException();
        }
        return Modifier.isStatic(clazz.getModifiers());
    }

    public static void main(String... args) {
        List.of(NestedTypeMember.class, InnerClass.class, StaticMemberClass.class, RecordClass.class, EnumClass.class)
                .forEach(cls -> System.out.printf("Class %s is static: %b%n", cls, isStaticTypeMember(cls)));
    }
}
