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

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Example using the Map "merge" method.
 *
 * @author Chris de Vreeze
 */
public class MergeExample {

    private MergeExample() {
    }

    public record AccountNumber(String number) {
    }

    public record AmountInEuro(BigDecimal amount) {

        public AmountInEuro plus(AmountInEuro other) {
            return new AmountInEuro(this.amount().add(other.amount()));
        }

        public static final AmountInEuro ZERO = new AmountInEuro(new BigDecimal(0));
    }

    // Unmodifiable Map
    private static final Map<AccountNumber, AmountInEuro> accountBalances = Map.ofEntries(
            Map.entry(new AccountNumber("34455468"), new AmountInEuro(new BigDecimal(120))),
            Map.entry(new AccountNumber("12645437"), new AmountInEuro(new BigDecimal(9405))),
            Map.entry(new AccountNumber("87674777"), new AmountInEuro(new BigDecimal(54))),
            Map.entry(new AccountNumber("35667547"), new AmountInEuro(new BigDecimal(250950))),
            Map.entry(new AccountNumber("74767658"), new AmountInEuro(new BigDecimal(375250))),
            Map.entry(new AccountNumber("75863664"), new AmountInEuro(new BigDecimal(25))),
            Map.entry(new AccountNumber("98765654"), new AmountInEuro(new BigDecimal(0))),
            Map.entry(new AccountNumber("24355455"), new AmountInEuro(new BigDecimal(245))),
            Map.entry(new AccountNumber("84566446"), new AmountInEuro(new BigDecimal(6500))),
            Map.entry(new AccountNumber("97665646"), new AmountInEuro(new BigDecimal(35))),
            Map.entry(new AccountNumber("34435497"), new AmountInEuro(new BigDecimal(9050))),
            Map.entry(new AccountNumber("54677356"), new AmountInEuro(new BigDecimal(2450000))),
            Map.entry(new AccountNumber("85756464"), new AmountInEuro(new BigDecimal(145))),
            Map.entry(new AccountNumber("86576646"), new AmountInEuro(new BigDecimal(85))),
            Map.entry(new AccountNumber("96765677"), new AmountInEuro(new BigDecimal(125)))
    );

    // Unmodifiable Map
    private static final Map<AccountNumber, AmountInEuro> transactions = Map.ofEntries(
            Map.entry(new AccountNumber("74767658"), new AmountInEuro(new BigDecimal(150))),
            Map.entry(new AccountNumber("97665646"), new AmountInEuro(new BigDecimal(120000))),
            Map.entry(new AccountNumber("85756464"), new AmountInEuro(new BigDecimal(-15))),
            Map.entry(new AccountNumber("35667547"), new AmountInEuro(new BigDecimal(-3500))),
            Map.entry(new AccountNumber("98765654"), new AmountInEuro(new BigDecimal(1000))),
            Map.entry(new AccountNumber("21456987"), new AmountInEuro(new BigDecimal(1500))), // new data
            Map.entry(new AccountNumber("16738954"), new AmountInEuro(new BigDecimal(1600))) // new data
    );

    public static void main(String[] args) {
        // Making (sorted) copy of unmodifiable Map before updating in-place (which would otherwise fail)
        SortedMap<AccountNumber, AmountInEuro> sortedAccountBalances =
                new TreeMap<>(Comparator.comparing(AccountNumber::number));
        sortedAccountBalances.putAll(accountBalances);

        // The iteration over "merge" calls (these are rather straightforward merges, only updating or adding data)
        transactions.forEach((account, amount) -> sortedAccountBalances.merge(account, amount, AmountInEuro::plus));

        sortedAccountBalances
                .forEach((account, amount) -> System.out.printf(
                        "Account: %s. Balance in euro: %11.2f%n",
                        account.number(),
                        amount.amount()));

        // Creating expected account balances through "replaceAll" calls on modifiable copy
        Map<AccountNumber, AmountInEuro> expectedAccountBalances = new HashMap<>(accountBalances);

        expectedAccountBalances
                .replaceAll((account, amount) -> amount.plus(transactions.getOrDefault(account, AmountInEuro.ZERO)));
        transactions.forEach(expectedAccountBalances::putIfAbsent);

        if (!sortedAccountBalances.equals(expectedAccountBalances)) {
            throw new RuntimeException("Account balances not equal to expected account balances (1)");
        }

        // Creating expected account balances through Stream API calls
        var knownAccounts = accountBalances.keySet();
        Map<AccountNumber, AmountInEuro> expectedAccountBalances2 =
                Stream.concat(
                                accountBalances
                                        .entrySet()
                                        .stream()
                                        .map(e -> Map.entry(
                                                e.getKey(),
                                                e.getValue().plus(transactions.getOrDefault(e.getKey(), AmountInEuro.ZERO))
                                        )),
                                transactions.entrySet().stream().filter(e -> !knownAccounts.contains(e.getKey())))
                        .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));

        if (!sortedAccountBalances.equals(expectedAccountBalances2)) {
            throw new RuntimeException("Account balances not equal to expected account balances (2)");
        }
    }
}
