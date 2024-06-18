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

package chapter02;

/**
 * Example, or better, examples, to show the use of operators.
 *
 * @author Chris de Vreeze
 */
public class OperatorExample {

    public static void binaryArithmeticOperatorExamples() {
        int intVar;
        long longVar;
        short shortVar;
        byte byteVar;
        float floatVar;
        double doubleVar;

        byte twoAsByte = 2;
        short threeAsShort = 3;

        System.out.println();

        // Straightforward integer addition and assignment
        showExample(
                "intVar = 2 + 3",
                intVar = 2 + 3,
                5);
        // RHS int value promoted to larger long type
        showExample(
                "longVar = 2 + 3L",
                longVar = 2 + 3L,
                5L);
        // RHS operands promoted to int
        showExample(
                "intVar = twoAsByte + threeAsShort",
                intVar = twoAsByte + threeAsShort,
                5);
        // RHS operands promoted to int, int addition, and needed down-cast to byte for the assignment
        showExample(
                "byteVar = (byte) (twoAsByte + threeAsShort)",
                byteVar = (byte) (twoAsByte + threeAsShort),
                (byte) 5);
        // RHS operand promoted to floating-point before addition
        showExample(
                "floatVar = 2L + 3f",
                floatVar = 2L + 3f,
                5f);
        // Multiple promotion rules used (from byte to int, from int to long, and from long to double)
        showExample(
                "doubleVar = twoAsByte + 3L + 4.0",
                doubleVar = twoAsByte + 3L + 4.0,
                9.0);
        // Like before, but down-casting to assignment result type
        showExample(
                "byteVar = (byte) (twoAsByte + 3L + 4.0)",
                byteVar = (byte) (twoAsByte + 3L + 4.0),
                (byte) 9);

        byte byteVarOriginallyOne = 1;
        // Much like before, but no more need for the down-cast, although it would be clearer to add the cast
        showExample(
                "byteVarOriginallyOne *= twoAsByte + 3L + 4.0",
                byteVarOriginallyOne *= twoAsByte + 3L + 4.0,
                (byte) 9);
        byteVarOriginallyOne = 1;
    }

    public static void stringConcatenationExamples() {
        System.out.println();

        var abcString = "abc";

        showExample("abcString + null", abcString + null, "abcnull");
        showExample("null + abcString", null + abcString, "nullabc");
    }

    private static <T> void showExample(String expressionAsString, T expressionResult, T expectedResult) {
        System.out.println("Expression \"" + expressionAsString + "\" returns " + expressionResult);
        if (!expressionResult.equals(expectedResult)) {
            throw new RuntimeException("No match between expression result and expected result");
        }
    }

    public static void main(String[] args) {
        binaryArithmeticOperatorExamples();
        stringConcatenationExamples();
    }
}
