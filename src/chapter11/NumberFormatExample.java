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

package chapter11;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * NumberFormat example.
 *
 * @author Chris de Vreeze
 */
public class NumberFormatExample {

    private static final int MAX_FRACTION_DIGITS = 10;

    private static NumberFormat settingMaxFractionDigits(NumberFormat formatter) {
        formatter.setMaximumFractionDigits(MAX_FRACTION_DIGITS);
        return formatter;
    }

    private static Map<Optional<Locale>, String> formatNumberInDifferentLocales(double number, List<Locale> locales) {
        List<Optional<Locale>> optionalLocales = new ArrayList<>(locales.stream().map(Optional::of).distinct().toList());
        optionalLocales.add(Optional.empty());

        return optionalLocales.stream()
                .map(optLocale -> Map.entry(
                        optLocale,
                        settingMaxFractionDigits(
                                optLocale.map(NumberFormat::getInstance)
                                        .orElse(NumberFormat.getInstance())
                        ).format(number)
                ))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private static Map<String, String> formatNumberUsingDifferentPatterns(double number, List<String> patterns) {
        return patterns.stream()
                .map(pattern -> Map.entry(pattern, settingMaxFractionDigits(new DecimalFormat(pattern)).format(number)))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private static void showFormattedNumberAndParseBack(double number, Optional<Locale> optLocale, String formattedNumber) {
        System.out.printf(
                "\tNumber: %s. Locale: %s. Formatted string: %s (language: %s)%n",
                number,
                optLocale,
                formattedNumber,
                optLocale.map(Locale::getDisplayLanguage).orElse("<none>"));

        NumberFormat formatter =
                settingMaxFractionDigits(
                        optLocale.map(NumberFormat::getInstance).orElse(NumberFormat.getInstance())
                );

        try {
            double parsedNumber = formatter.parse(formattedNumber).doubleValue();
            System.out.printf("\t\tNumber parsed back: %s%n", parsedNumber);

            if (parsedNumber != number) {
                System.out.println("\t\tParsed number not equal to the original number");
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private static void showFormattedNumberUsingPatternAndParseBack(double number, String pattern, String formattedNumber) {
        System.out.printf(
                "\tNumber: %s. Pattern: \"%s\". Formatted string: %s%n",
                number,
                pattern,
                formattedNumber);

        NumberFormat formatter = settingMaxFractionDigits(new DecimalFormat(pattern));

        try {
            double parsedNumber = formatter.parse(formattedNumber).doubleValue();
            System.out.printf("\t\tNumber parsed back: %s%n", parsedNumber);

            if (parsedNumber != number) {
                System.out.println("\t\tParsed number not equal to the original number");
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private static void showFormattedNumbers(List<Double> numbers, List<Locale> availableLocales) {
        numbers.forEach(n -> {
            System.out.println();
            System.out.printf("Number: %s%n", n);

            formatNumberInDifferentLocales(n, availableLocales).entrySet()
                    .stream()
                    .sorted(Comparator.comparing(entry -> entry.getKey().toString()))
                    .forEach(entry -> showFormattedNumberAndParseBack(n, entry.getKey(), entry.getValue()));
        });
    }

    private static void showFormattedNumbersUsingPatterns(List<Double> numbers, List<String> patterns) {
        numbers.forEach(n -> {
            System.out.println();
            System.out.printf("Number: %s%n", n);

            formatNumberUsingDifferentPatterns(n, patterns).entrySet()
                    .stream()
                    .sorted(Comparator.comparing(Map.Entry::getKey))
                    .forEach(entry -> showFormattedNumberUsingPatternAndParseBack(n, entry.getKey(), entry.getValue()));
        });
    }

    public static void main(String[] args) {
        List<String> languages = Arrays.stream(Locale.getISOLanguages()).sorted().toList();
        List<Locale> locales = languages.stream().map(Locale::new).toList();

        List<Double> numbers = List.of(3141592.6535, 3.1415926535, (double) 0, -12.10, 123.789);

        // Using Locales and NumberFormat static factory methods (preferred)
        showFormattedNumbers(numbers, locales);

        List<String> patterns = List.of(
                "###,###,###.0",
                "000,000,000.00000",
                "#,###,###.##"
        );

        // Using hard-coded patterns and DecimalFormat constructor (not preferred)
        showFormattedNumbersUsingPatterns(numbers, patterns);
    }
}
