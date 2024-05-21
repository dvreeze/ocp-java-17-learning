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

package chapter08;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * Predicate example.
 *
 * @author Chris de Vreeze
 */
public class PredicateExample {

    private PredicateExample() {
    }

    public static boolean containsWord(String s, String word) {
        Objects.checkIndex(0, word.length());
        var i = s.indexOf(word);
        if (i < 0) {
            return false;
        } else {
            var notPrecededByLetterOrDigit = s.substring(Math.max(i - 1, 0), i).chars().noneMatch(Character::isLetterOrDigit);
            var notSucceededByLetterOrDigit =
                    s.substring(i + word.length()).chars().limit(1).noneMatch(Character::isLetterOrDigit);
            return notPrecededByLetterOrDigit && notSucceededByLetterOrDigit;
        }
    }

    public static Predicate<String> containsWord(String word) {
        return s -> containsWord(s, word);
    }

    public static Predicate<String> couldWellBeWimHofQuote() {
        Predicate<String> hasExpectedQuoteWords =
                containsWord("mind")
                        .or(containsWord("possible"))
                        .or(containsWord("not").and(containsWord("afraid")))
                        .or(containsWord("lived"))
                        .or(containsWord("succeed"))
                        .or(containsWord("cold"))
                        .or(containsWord("Cold"))
                        .or(containsWord("stressor"));

        return containsWord("Wim")
                .and(containsWord("Hof"))
                .and(hasExpectedQuoteWords);
    }

    public static void main(String[] args) {
        var quotes = List.of(
                "Wim Hof: If you can learn how to use your mind, anything is possible.",
                "I'm not afraid of dying. I'm afraid not to have lived. - Wim Hof",
                """
                        I've come to understand that if you want to learn something badly enough,
                        you'll find a way to make it happen.
                        Having the will to search and succeed is very important (Wim Hof).""",
                """
                        Cold is a stressor, so if you are able to get into the cold and control your body's response to it,
                        you will be able to control stress. (Wim Hof)""",
                "Legitimate use of violence can only be that which is required in self-defense (Ron Paul).",
                "Real patriotism is a willingness to challenge the government when it's wrong (Ron Paul).",
                "War is never economically beneficial except for those in position to profit from war expenditures (Ron Paul)."
        );
        System.out.println();
        System.out.println("Probable Wim Hof quotes:");
        quotes.stream().filter(couldWellBeWimHofQuote()).forEach(quote -> {
            System.out.println();
            System.out.println(quote);
        });
    }
}
