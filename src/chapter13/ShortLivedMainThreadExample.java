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

package chapter13;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Example showing that without calling "ExecutorService.awaitTermination" this program ends successfully.
 * This is a case where it is ok for the main thread to be short-lived. The "task runner" thread is not a daemon
 * thread, and it proceeds until it is ready, even if the main thread has terminated.
 *
 * @author Chris de Vreeze
 */
public class ShortLivedMainThreadExample {

    private static Map<Thread, StackTraceElement[]> getStackTraces(int taskIndex) {
        var result = Thread.getAllStackTraces();
        try {
            Thread.sleep(1_000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println(addThreadToMessage("Retrieved all stack traces of task " + taskIndex));
        return result;
    }

    private static String addThreadToMessage(String message) {
        return String.format("[%s] %s", Thread.currentThread().getName(), message);
    }

    public static void main(String[] args) {
        // Create one other thread to submit tasks to. So we have that thread, and this main thread, running at the same time.
        ExecutorService taskRunner = Executors.newSingleThreadExecutor();

        Callable<Map<Thread, StackTraceElement[]>> task1 = () -> getStackTraces(1);
        Callable<Map<Thread, StackTraceElement[]>> task2 = () -> getStackTraces(2);
        Callable<Map<Thread, StackTraceElement[]>> task3 = () -> getStackTraces(3);

        try {
            Future<Map<Thread, StackTraceElement[]>> future1 = taskRunner.submit(task1);
            Future<Map<Thread, StackTraceElement[]>> future2 = taskRunner.submit(task2);
            Future<Map<Thread, StackTraceElement[]>> future3 = taskRunner.submit(task3);

            System.out.printf(addThreadToMessage("Task 1 is finished: %b%n"), future1.isDone());
            System.out.printf(addThreadToMessage("Task 2 is finished: %b%n"), future2.isDone());
            System.out.printf(addThreadToMessage("Task 3 is finished: %b%n"), future3.isDone());
        } finally {
            System.out.println(addThreadToMessage("Calling \"shutDown\""));
            taskRunner.shutdown();
            System.out.println(addThreadToMessage("Just called \"shutDown\""));
        }
        System.out.printf(addThreadToMessage("Task runner is terminated (directly after shutdown): %b%n"), taskRunner.isTerminated());
        System.out.println(addThreadToMessage("Main thread has finished"));

        // The main thread terminates here, but that is ok. The "task runner" thread will still finish its work.
        // After all, that thread is a non-daemon thread.
    }
}
