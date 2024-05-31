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

import java.util.Arrays;

/**
 * Example showing suppressed exceptions.
 *
 * @author Chris de Vreeze
 */
public class SuppressedExceptionsExample {

    @FunctionalInterface
    public interface FallibleRunnable {

        void run() throws Exception;
    }

    public static class MyCloseable implements AutoCloseable {

        private final int number;

        public MyCloseable(int number) {
            this.number = number;
            System.out.println("Creating MyCloseable " + this.number);
        }

        public void run() {
            System.out.println("Running MyCloseable " + number);
        }

        @Override
        public void close() throws Exception {
            System.out.println("Throwing exception from closing MyCloseable " + number);
            throw new Exception("Exception from closing MyCloseable " + number);
        }
    }

    public static class MyCloseable2 implements AutoCloseable {

        private final int number;

        public MyCloseable2(int number) {
            this.number = number;
            System.out.println("Creating MyCloseable2 " + this.number);
        }

        public void run() {
            System.out.println("Running MyCloseable2 " + number);
        }

        public void runUnsuccessfully() throws Exception {
            System.out.println("Running (unsuccessfully) MyCloseable2 " + number);
            throw new Exception("Running (unsuccessfully) MyCloseable2 " + number);
        }

        @Override
        public void close() {
            System.out.println("Throwing runtime exception from closing MyCloseable2 " + number);
            throw new RuntimeException("RuntimeException from closing MyCloseable2 " + number);
        }
    }

    public static void throwTwice1() throws Exception {
        try {
            throw new Exception("Exception from try-block");
        } finally {
            throw new Exception("Exception from finally block");
        }
    }

    public static void throwTwice2() throws Exception {
        try {
            throw new Exception("Exception from try-block");
        } catch (Exception e) {
            throw new Exception("Exception from catch-block");
        } finally {
            throw new Exception("Exception from finally block");
        }
    }

    public static void throwTwice3() throws Exception {
        try (var myCloseable = new MyCloseable(1)) {
            myCloseable.run();
        }
    }

    public static void throwTwice4() throws Exception {
        try (var myCloseable = new MyCloseable(1)) {
            myCloseable.run();
            throw new Exception("Exception from try-block");
        }
    }

    public static void throwTwice5() throws Exception {
        try (var myCloseable = new MyCloseable(1)) {
            myCloseable.run();
            throw new Exception("Exception from try-block");
        } finally {
            throw new Exception("Exception from finally block");
        }
    }

    public static void throwTwice6() throws Exception {
        try (var myCloseable1 = new MyCloseable(1);
             var myCloseable2 = new MyCloseable(2)) {
            myCloseable1.run();
            myCloseable2.run();
            throw new Exception("Exception from try-block");
        }
    }

    public static void throwTwice7() throws Exception {
        try (var myCloseable1 = new MyCloseable2(1);
             var myCloseable2 = new MyCloseable2(2)) {
            myCloseable1.run();
            myCloseable2.run();
            throw new Exception("Exception from try-block");
        } catch (RuntimeException e) {
            System.out.println("Rethrowing ...");
            throw e;
        }
    }

    public static void throwTwice8() throws Exception {
        try (var myCloseable1 = new MyCloseable2(1);
             var myCloseable2 = new MyCloseable2(2)) {
            myCloseable1.runUnsuccessfully();
            myCloseable2.runUnsuccessfully();
        }
    }

    public static void throwTwice9() throws Exception {
        try (var myCloseable1 = new MyCloseable2(1);
             var myCloseable2 = new MyCloseable2(2)) {
            myCloseable1.run();
            myCloseable2.run();
        }
    }

    public static void handleException(FallibleRunnable throwingCode, String title) {
        System.out.println();
        System.out.println();
        System.out.println(title);
        System.out.println();

        try {
            throwingCode.run();
        } catch (Exception e) {
            System.out.println("Exception e: " + e.getMessage());
            Arrays.stream(e.getSuppressed()).forEach(t -> {
                System.out.println("\tSuppressed exception: " + t.getMessage());

                var suppressedInSuppressed = Arrays.stream(t.getSuppressed()).findAny().isPresent();
                System.out.printf("\t\tThe suppressed exception has itself a suppressed exception: %b%n", suppressedInSuppressed);
            });
        }
    }

    public static void main(String[] args) {
        handleException(SuppressedExceptionsExample::throwTwice1, "Running throwTwice1:");
        handleException(SuppressedExceptionsExample::throwTwice2, "Running throwTwice2:");
        handleException(SuppressedExceptionsExample::throwTwice3, "Running throwTwice3:");
        handleException(SuppressedExceptionsExample::throwTwice4, "Running throwTwice4:");
        handleException(SuppressedExceptionsExample::throwTwice5, "Running throwTwice5:");
        handleException(SuppressedExceptionsExample::throwTwice6, "Running throwTwice6:");
        handleException(SuppressedExceptionsExample::throwTwice7, "Running throwTwice7:");
        handleException(SuppressedExceptionsExample::throwTwice8, "Running throwTwice8:");
        handleException(SuppressedExceptionsExample::throwTwice9, "Running throwTwice9:");
    }
}
