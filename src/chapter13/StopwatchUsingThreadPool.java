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

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Just for fun, a stopwatch unnecessarily using a (large) thread pool.
 *
 * @author Chris de Vreeze
 */
public class StopwatchUsingThreadPool {

    private static final int DEFAULT_SECONDS = 30;

    private final int startSeconds;
    private final AtomicInteger remainingSeconds;
    private final int threadPoolSize;

    public StopwatchUsingThreadPool(int numberOfSeconds, int threadPoolSize) {
        this.startSeconds = numberOfSeconds;
        this.remainingSeconds = new AtomicInteger(startSeconds);
        this.threadPoolSize = threadPoolSize;
    }

    public void run() {
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(threadPoolSize);

        try {
            ScheduledFuture<?> future = executorService.scheduleAtFixedRate(
                    () -> {
                        System.out.printf(
                                "Remaining seconds: %d (current Thread: %s)%n",
                                remainingSeconds.getAndDecrement(),
                                Thread.currentThread());
                        System.out.flush();
                    },
                    0,
                    1,
                    TimeUnit.SECONDS
            );

            Runnable canceller = () -> {
                System.out.println("Beep!"); // Not really. The BEEP does not work.
                future.cancel(false);
            };
            executorService.schedule(canceller, startSeconds, TimeUnit.SECONDS);

            // Blocking wait, or else the program flow does not proceed
            // Even an "executorService.awaitTermination" after "executorService.shutdown" does not change that
            future.get();
        } catch (CancellationException e) {
            System.out.println("Cancelled, as expected");
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            executorService.shutdown();
        }
    }

    public static void main(String[] args) {
        int seconds = (args.length == 0) ? DEFAULT_SECONDS : Integer.parseInt(args[0]);

        var availableProcessors = Runtime.getRuntime().availableProcessors();
        System.out.printf("Available processors: %d%n", availableProcessors);
        System.out.flush();

        var stopwatch = new StopwatchUsingThreadPool(seconds, availableProcessors);
        stopwatch.run();
    }
}
