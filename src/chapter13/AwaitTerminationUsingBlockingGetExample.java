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

import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Example showing the effect of method "Future.get" after (orderly) shutdown.
 * <p>
 * In this case the effect is only to keep the main thread alive as long as the "executor thread", without
 * affecting the program result.
 *
 * @author Chris de Vreeze
 */
public class AwaitTerminationUsingBlockingGetExample {

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

        final Future<Map<Thread, StackTraceElement[]>> future1;
        final Future<Map<Thread, StackTraceElement[]>> future2;
        final Future<Map<Thread, StackTraceElement[]>> future3;

        try {
            future1 = taskRunner.submit(task1);
            future2 = taskRunner.submit(task2);
            future3 = taskRunner.submit(task3);

            System.out.printf(addThreadToMessage("Task 1 is finished: %b%n"), future1.isDone());
            System.out.printf(addThreadToMessage("Task 2 is finished: %b%n"), future2.isDone());
            System.out.printf(addThreadToMessage("Task 3 is finished: %b%n"), future3.isDone());
        } finally {
            System.out.println(addThreadToMessage("Calling \"shutDown\""));
            taskRunner.shutdown();
            System.out.println(addThreadToMessage("Just called \"shutDown\""));
        }
        System.out.printf(addThreadToMessage("Task runner is terminated (directly after shutdown): %b%n"), taskRunner.isTerminated());

        // Without the "Future.get" calls below, the main thread would end before the tasks complete on the other thread.
        // Still, the other thread is not a daemon thread, so it would finish the submitted tasks before the program ends.
        // In that sense, "Future.get" is not needed here, and "fire-and-forget" is good enough.
        // Note that "Future.get" is similar in this sense to "ExecutorService.awaitTermination" (if we disregard the results).

        List.of(future1, future2, future3).forEach(f -> {
            try {
                f.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        });
        System.out.printf(addThreadToMessage("Task runner is terminated (directly after \"Future.get\"): %b%n"), taskRunner.isTerminated());
    }
}
