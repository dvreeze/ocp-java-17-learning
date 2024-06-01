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

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

/**
 * NumberFormat example using Locales.
 *
 * @author Chris de Vreeze
 */
public class NumberFormatExampleUsingLocales {

    public static void showFormatting(BigDecimal number, Locale locale) {
        System.out.println();
        System.out.println("Locale: " + locale);
        System.out.println();
        System.out.println(
                "Using NumberFormat.getNumberInstance:                " +
                        format(number, NumberFormat.getNumberInstance(locale)));
        System.out.println(
                "Using NumberFormat.getIntegerInstance:               " +
                        format(number, NumberFormat.getIntegerInstance(locale)));
        System.out.println(
                "Using NumberFormat.getCurrencyInstance:              " +
                        format(number, NumberFormat.getCurrencyInstance(locale)));
        System.out.println(
                "Using NumberFormat.getPercentInstance:               " +
                        format(number, NumberFormat.getPercentInstance(locale)));
        System.out.println(
                "Using NumberFormat.getCompactNumberInstance (SHORT): " +
                        format(number, NumberFormat.getCompactNumberInstance(locale, NumberFormat.Style.SHORT)));
        System.out.println(
                "Using NumberFormat.getCompactNumberInstance (LONG):  " +
                        format(number, NumberFormat.getCompactNumberInstance(locale, NumberFormat.Style.LONG)));
    }

    private static String format(Number number, NumberFormat formatter) {
        return formatter.format(number);
    }

    public static void main(String[] args) {
        var localeRu = new Locale("ru", "RU");
        var localeNl = new Locale("nl", "NL");
        var locales = List.of(Locale.US, localeRu, Locale.UK, Locale.CHINESE, Locale.FRANCE, Locale.GERMANY, localeNl);
        var numbers =
                List.of(new BigDecimal("21"), new BigDecimal("145.534589"), new BigDecimal("0.85"), new BigDecimal("7123456"));

        for (BigDecimal number : numbers) {
            for (Locale locale : locales) {
                showFormatting(number, locale);
            }
        }
    }
}
