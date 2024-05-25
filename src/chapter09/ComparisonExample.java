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

package chapter09;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

/**
 * Example of comparisons, sorting and searching.
 *
 * @author Chris de Vreeze
 */
public class ComparisonExample {

    private ComparisonExample() {
    }

    public record Category(String level1Category, String level2Category,
                           Optional<String> maybeLevel3Category) implements Comparable<Category> {

        public int compareTo(Category other) {
            return comparator.compare(this, other);
        }

        public static Comparator<Category> comparator =
                Comparator.comparing(Category::level1Category)
                        .thenComparing(Category::level2Category)
                        .thenComparing(v -> v.maybeLevel3Category.orElse(""));

        public static Category category(String level1Category, String level2Category, Optional<String> maybeLevel3Category) {
            return new Category(level1Category, level2Category, maybeLevel3Category);
        }
    }

    // Unmodifiable List
    private static final List<Category> categories = List.of(
            Category.category("car", "Honda", Optional.empty()),
            Category.category("car", "BMW", Optional.of("320")),
            Category.category("car", "Suzuki", Optional.empty()),
            Category.category("car", "Mazda", Optional.empty()),
            Category.category("car", "Audi", Optional.of("A6")),
            Category.category("car", "Volkswagen", Optional.of("Golf GTI")),
            Category.category("car", "Peugeot", Optional.empty()),
            Category.category("motorcycle", "Honda", Optional.empty()),
            Category.category("motorcycle", "Kawasaki", Optional.of("ZZR 1400")),
            Category.category("motorcycle", "Suzuki", Optional.of("Hayabusa")),
            Category.category("motorcycle", "BMW", Optional.empty()),
            Category.category("motorcycle", "Yamaha", Optional.of("R1")),
            Category.category("motorcycle", "Ducati", Optional.empty()),
            Category.category("motorcycle", "Triumph", Optional.empty()),
            Category.category("car", "Audi", Optional.of("RS6")),
            Category.category("car", "Volkswagen", Optional.of("Passat")),
            Category.category("car", "Audi", Optional.of("A5")),
            Category.category("motorcycle", "Kawasaki", Optional.of("H2")),
            Category.category("car", "BMW", Optional.of("740")),
            Category.category("car", "Mercedes Benz", Optional.of("S 500")),
            Category.category("car", "BMW", Optional.of("530"))
    );

    public static void main(String[] args) {
        List<List<Category>> sortedCategoryLists =
                List.of(
                        showSortedInPlace(new ArrayList<>(categories), Collections::sort),
                        showSortedInPlace(new ArrayList<>(categories), categories -> Collections.sort(categories, Category.comparator)),
                        showSortedInPlace(new ArrayList<>(categories), categories -> categories.sort(Category.comparator)),
                        showSortedFunctionally(new ArrayList<>(categories), categories -> categories.stream().sorted().toList()),
                        showSortedFunctionally(new ArrayList<>(categories), categories -> categories.stream().sorted(Category.comparator).toList()),
                        showSortedSet(() -> new TreeSet<>(categories)),
                        showSortedSet(() -> {
                            var s = new TreeSet<>(Category.comparator);
                            s.addAll(categories);
                            return s;
                        })
                );

        List<List<Category>> distinctSortedCategoryLists = sortedCategoryLists.stream().distinct().toList();

        if (distinctSortedCategoryLists.size() >= 2) {
            throw new RuntimeException("Not all sorted lists are the same");
        }

        final var category1 = Category.category("motorcycle", "Kawasaki", Optional.of("ZZR 1400"));
        final var category2 = Category.category("car", "Audi", Optional.of("RS8"));

        List<Integer> kawasakiIndices = sortedCategoryLists
                .stream()
                .map(categoryList -> Collections.binarySearch(categoryList, category1))
                .distinct()
                .toList();

        var kawasakiIndex = 17;
        if (!kawasakiIndices.equals(List.of(kawasakiIndex))) {
            throw new RuntimeException(String.format("Expected Kawasaki ZZR 1400 at list index %d, but did not find it there", kawasakiIndex));
        }

        List<Integer> audiIndices = sortedCategoryLists
                .stream()
                .map(categoryList -> Collections.binarySearch(categoryList, category2, Category.comparator))
                .distinct()
                .toList();

        var audiIndex = -4;
        if (!audiIndices.equals(List.of(audiIndex))) {
            throw new RuntimeException(String.format("Expected Audi RS8 at list index %d, but did not find it there", audiIndex));
        }
    }

    private static List<Category> showSortedInPlace(List<Category> categories, Consumer<List<Category>> sortingCode) {
        sortingCode.accept(categories);

        System.out.println();
        categories.forEach(System.out::println);

        return categories.stream().toList();
    }

    private static List<Category> showSortedFunctionally(List<Category> categories, UnaryOperator<List<Category>> sortingCode) {
        List<Category> sortingResult = sortingCode.apply(categories);

        System.out.println();
        sortingResult.forEach(System.out::println);

        return sortingResult;
    }

    private static List<Category> showSortedSet(Supplier<SortedSet<Category>> categorySupplier) {
        SortedSet<Category> categories = categorySupplier.get();

        System.out.println();
        categories.forEach(System.out::println);

        return categories.stream().toList();
    }
}
