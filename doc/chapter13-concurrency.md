# Chapter 13. Concurrency

See [OCP Java SE 17 Developer Study Guide](https://www.amazon.com/Oracle-Certified-Professional-Developer-Study/dp/1119864585/ref=sr_1_1?crid=1GIZNHYFXHAK4&dib=eyJ2IjoiMSJ9.Mz5O0lUSaZhUZ-O1Mi__dRPfXHL9GM_CfZ3JDTz910a2d8XI7Vsfj7zwcywJAfMcubfCglH02m8PwlAk_DORk8SS5460zaDP1fskFDX4sUiFVR4pxE1Ln0VIY-g5awTQaOJKp4t0Y1HchXkrw0HtOeVSHg3dHG8Jql9TibGCj-WeXYyNdMp4zWtgM4EimHpl4wvlJZufvGpNjNEmXIObAd2B1mp1skt5k7v_B-k_Ip4.bRERgxl7gsekO5AihUKuOeT_yoO6Bsg7jHigb4sjHEM&dib_tag=se&keywords=ocp+java+se17&qid=1714573695&sprefix=ocp+java+%2Caps%2C192&sr=8-1).

This chapter is about *concurrency* in Java. It is about *threads*, *thread-safety*, the Java *concurrency API*,
*concurrent collections*, *concurrency in Stream pipelines*, etc.

All operating systems support *multithreaded processing*. That is, within an *OS process* (such as a running Java program)
there are *multiple threads of execution* that are running.

### Introducing threads

A *thread* is the *smallest* unit of execution that can be *scheduled* by the operating system. A *process* is a group of
associated threads executing in the *same shared environment*. This shared environment means that threads in the same
process share the same *memory space* and can communicate directly with one another.

A *single-threaded process* is a process containing only 1 thread, whereas a *multithreaded process* supports more than
1 thread.

A *task* is a single *unit of work* performed by a thread. Tasks are often implemented in Java as lambda expressions.
A thread can complete multiple tasks, but only one task at a time.

At each time a running thread is mapped to one CPU. This mapping is done by the OS thread scheduler.

In an OS process running a Java program *shared memory* often means *static fields*. It could also be *instance fields*
of an object shared by multiple threads, or even shared objects pointed to by local variables. The local variables
themselves are not shared, because they are on the thread-specific *call stack*, but the objects referred to by those
local variables may be shared.

#### Understanding thread concurrency

*Concurrency* is the property of multiple processes and threads in those processes executing at the same time.

Often there are more threads than CPUs. To deal with that, the OS uses a *thread scheduler* to determine which threads
should currently be executing. For example, the scheduler may use a *round-robin* schedule in which each available
thread gets an equal number of CPU cycles with which to execute, while visiting the threads in circular order.

When time is up for a running thread that has not finished executing yet, a *context switch* occurs. The thread's current
state is stored and later restored to continue executing, when it is this thread's turn again. Context switches are
relatively expensive at runtime. Also, it is too simplistic to talk about storing and restoring thread state. The details
are far more complex. See for example [Java memory model](https://jenkov.com/tutorials/java-concurrency/java-memory-model.html).

A thread can *interrupt* (or supersede) another thread if it has a higher priority than the other thread. *Thread priorities*
in Java are integer numbers.

#### Creating a thread

Creating a `java.lang.Thread` is quite easy. The constructor takes a `java.lang.Runnable`, which is a *functional interface*:

```java
@FunctionalInterface
public interface Runnable {
    void run();
}
```

So a Thread is easy to create using a *lambda expression*. Creating and *starting* the thread can easily be coded as a oneliner,
if we don't need to store the thread reference in a variable. For example:

```java
new Thread(() -> System.out.println("Hello world")).start();
```

Doing this in a Java program creates and starts another thread than the one currently running, such as the "main thread".
There is *no guarantee w.r.t. the execution order* of the newly created and started thread and the "current thread".
That depends on the thread scheduler. Think of each created and started thread as an *asynchronous task*. It is
*fire-and-forget*.

The order within a thread is still linear. So one thread in isolation is a *synchronous task*, waiting for each code line
to finish executing before moving on to the next code line. This is called *blocking*.

If we call `Thread` method `run()` instead of `start()`, no different thread is started, and the code of the `Runnable`
executes in the current thread. Watch out for this in exam questions!

Instead of creating a thread by passing a lambda expression as `Runnable` to the `Thread` constructor (before calling
method `start()`), we could create a subclass of `Thread` and override the `run()` method. Typically, this is not needed.

#### Distinguishing thread types

A *system thread* is created by the JVM and runs in the background. The *garbage collection* thread is an example.

A *user-defined thread* is created by the application developer. Maybe most programs do not create any user thread,
leaving only one user thread, namely the one calling the `main(String[])` method. We call such programs *single-threaded*,
although technically that is not entirely correct.

System and user-defined threads can be created as *daemon threads*. These are threads that will not prevent the JVM from
exiting when the program finishes. In other words, a Java program ends when the only remaining threads are daemon threads.

By default, user-defined threads are not daemon threads, so the program will wait for them to finish.

#### Managing a thread's life cycle

A created `Thread` can be in one of 6 states. The 3 "non-pausing" thread states are:
* *NEW*
  * This is the state of a thread that has been created but not yet started
* *RUNNABLE*, which means that the thread either runs or is able to run
  * The first time the thread is in this state is after calling `start()` on a freshly created thread
  * A thread can go from RUNNABLE to one of the 3 "pausing" states mentioned below, and back from those states to RUNNABLE
* *TERMINATED*
  * This is the end state of the thread, when the `run()` method completes or an uncaught exception is thrown

The 3 "pausing" thread states are:
* *BLOCKED*, so waiting to enter a *synchronized block*
  * When a thread is RUNNABLE and starts waiting for a "monitor lock", it enters this state
  * When subsequently access to the "monitor lock" has been granted, the thread becomes RUNNABLE again
* *WAITING*, so waiting indefinitely to be *notified*
  * When a thread is RUNNABLE and calls instance method `Object.wait()`, it enters this state
  * When subsequently in another thread instance method `Object.notify()` is called on the same object, the thread becomes RUNNABLE again
* *TIMED_WAITING*, so waiting a specified time
  * For example, when a thread is RUNNABLE and calls static method `Thread.sleep(long)`, passing a sleep time in milliseconds, it enters this state
  * When the sleep time has passed, the thread becomes RUNNABLE again

Not all possible thread state changes are mentioned above. For example, an interrupted thread in state *TIMED_WAITING*
will go straight back to state *RUNNABLE*.

Methods `Object.wait()`, `Object.notify()` and `Thread.join()` are beyond the scope of the exam. They should not be used anyway.
The concurrency API should be used instead.

The thread state can be queried with method `Thread.getState()`, returning an enumeration value from enum `Thread.State`.

#### Polling with sleep

Let's start with the following example of a counter in another thread (copied from the book), and stopping when the count
has been reached:

```java
public class CheckResults {
    // Not thread-safe
    private static int counter = 0;

    public static void main(String[] args) {
        new Thread(() -> {
                for (int i = 0; i < 1_000_000; i++) counter++;
        }).start();
        while (counter < 1_000_000) {
            System.out.println("Not reached yet");
        }
        System.out.println("Reached: " + counter);
    }
}
```

This program unnecessarily uses a lot of CPU resources. Let's improve on that, by *polling*, which is the process of
intermittently checking data at some interval:

```java
public class CheckResultsWithSleep {
    // Not thread-safe
    private static int counter = 0;

    public static void main(String[] args) {
        new Thread(() -> {
                for (int i = 0; i < 1_000_000; i++) counter++;
        }).start();
        while (counter < 1_000_000) {
            System.out.println("Not reached yet");
            try {
                Thread.sleep(1_000); // 1 second
            } catch (InterruptedException e) {
                System.out.println("Interrupted!");
            }
        }
        System.out.println("Reached: " + counter);
    }
}
```

At least the "main" thread alternates a lot between state RUNNABLE and TIMED_WAITING, spending most time in the latter state.
But the program is still not thread-safe, in that the static `counter` variable can have an invalid or unexpected value,
because 2 threads may access that variable at the same time.

#### Interrupting a thread

The preceding program still may take almost a second too long. Why not *interrupt* the main thread once the program is
done counting? So we get:

```java
public class CheckResultsWithSleepAndInterrupt {
    // Not thread-safe
    private static int counter = 0;

    public static void main(String[] args) {
        final var mainThread = Thread.currentThread();
        new Thread(() -> {
                for (int i = 0; i < 1_000_000; i++) counter++;
                mainThread.interrupt();
        }).start();
        while (counter < 1_000_000) {
            System.out.println("Not reached yet");
            try {
                Thread.sleep(1_000); // 1 second
            } catch (InterruptedException e) {
                System.out.println("Interrupted!");
            }
        }
        System.out.println("Reached: " + counter);
    }
}
```

Calling instance method `Thread.interrupt()` on a thread in the TIMED_WAITING or WAITING state causes that thread to become
RUNNABLE again, triggering a checked `java.lang.InterruptedException`. The thread may also go to the BLOCKED state if it
needs to acquire a "monitor lock".

If the thread on which method `interrupt()` is called is (already) RUNNABLE, its state is not affected. On the other hand,
a RUNNABLE thread can still "be cooperative" by periodically checking the `Thread.isInterrupted()` boolean value.
A thread in state NEW is also not affected by an `interrupt()` call.

### Creating threads with the concurrency API

Java contains the *Java Concurrency API*, which is offered by package `java.util.concurrent` and sub-packages.
It should almost always be used instead of creating threads ourselves.

The main interface is `java.util.concurrent.ExecutorService` (which extends interface `java.util.concurrent.Executor`).
An `ExecutorService` can be used to submit work to. Typically, but not always, an `ExecutorService` is backed by
a *thread pool*.

An `ExecutorService` can be obtained from *factory class* `java.util.concurrent.Executors`, through one of its static
methods.

#### Introducing the single-thread executor

We can get a single-thread executor with static method `Executors.newSingleThreadExecutor()`. After this call,
there are 2 user-defined threads, namely the main thread and the "executor" thread, both running simultaneously.
Because of the single "executor" thread, all tasks submitted to the executor will run sequentially in order of submission.
Yet the order between work on the main thread and work in the "executor" thread is undefined and up to the thread scheduler.

Consider the following example:

```java
ExecutorService executor = Executors.newSingleThreadExecutor();
try {
    executor.execute(runnable1);
    executor.execute(runnable2);
    executor.execute(runnable3);
} finally {
    executor.shutdown();
}
```

The following is true about this example:
* The 3 runnable tasks will be run sequentially in order of submission to the single-thread executor
* It is quite likely that shutdown is called before the 3 tasks have completed, but they will not be cancelled in this "orderly shutdown"
* So it is likely that the main thread terminates before the single executor thread finishes
* That is ok, though, because the single executor thread is a non-daemon thread, so it keeps running after the main thread terminates

#### Shutting down a thread executor

The *life cycle* of an `ExecutorService` is as follows:
1. On creation, the `ExecutorService` is immediately *active*:
   * the `ExecutorService` accepts new tasks, and starts executing already submitted tasks
   * methods `isShutdown()` and `isTerminated()` both return `false`
   * when calling `shutdown()`, the `ExecutorService` transfers to state "shutting down"
2. After a `shutdown()` call, the `ExecutorService` is *shutting down*:
   * new tasks are rejected, throwing an unchecked `RejectedExecutionException`
   * but already submitted tasks are still executing (on a thread pool non-daemon thread, regardless of whether the main thread still runs)
   * method `isShutdown()` returns `true`, but `isTerminated` may initially still return `false`
   * when after shutting down all tasks have finished, the `ExecutorService` transfers to state "terminated"
3. After *all tasks have finished* following a call to `shutdown()`, the `ExecutorService` has *terminated*:
   * new tasks are rejected and no tasks are running
   * methods `isShutdown()` and `isTerminated()` both return `true`
   * the `ExecutorService` cannot be used anymore

A `ThreadExecutor` creates a non-daemon thread on the first executed task, so if we fail to call `shutdown()`, the application
will *never terminate*. Method `shutdownNow()` does try to stop all running tasks (and discard any tasks that have not yet
started).

An `ExecutorService` is not an `AutoCloseable`, so it cannot be used as resource in a try-with-resources statement.
Still, it must be used in a try-finally statement, as shown above, in order to prevent memory leaks.

#### Submitting tasks

Tasks can be submitted to an `ExecutorService` as `Runnable` or `Callable`, using one of the following instance methods:
* `public void execute(Runnable task)`, inherited from supertype `Executor`
* `public Future<?> submit(Runnable task)`
* `public <T> Future<T> submit(Callable<T> task)`
* `public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks)`
* `public <T> T invokeAny(Collection<? extends Callable<T>> tasks)`

Method `execute` is really "fire-and-forget". It returns `void`, so gives no handle to checks the task's progress, etc.
It is usually better to use a `submit` call instead.

Both `submit` methods (one taking a `Runnable` and the other one taking a `Callable<T>`) return a `Future<T>` (or `Future<?>`)
which can be used to track the result. With a `Future` we can do a blocking wait until the result is available, for example.

Method `invokeAll` returns a collection of `Future` instances in the same order as the original collection of `Callable`
instances.

Method `invokeAny` executes the given tasks but waits for at least one to complete.

#### Waiting for results

*Interface* `Future<T>` has the following methods we need to know about:
* `public boolean isDone()`
  * it returns `true` if the task was completed, threw an exception or was cancelled
* `public boolean isCancelled()`
  * it returns `true` if the task was cancelled before completing normally
* `public boolean cancel(boolean mayInterruptIfRunning)`
  * attempts to cancel execution of the task, returning `true` if it was successfully cancelled or `false` if it could not be cancelled or is complete
* `public T get()`
  * retrieves result of the task, *waiting endlessly* if the result is not yet available
* `public T get(long timeout, TimeUnit unit)`
  * retrieves result of the task, *waiting the specified amount of time*; if the result is not yet available after the timeout period, a checked `TimeoutException` is thrown

Our earlier "CheckResults" program could become as follows, using the Java Concurrency API:

```java
public class CheckResults {
    // Not thread-safe
    private static int counter = 0;

    public static void main(String[] args) {
        ExecutorService service = Executors.newSingleThreadExecutor();
        try {
            Future<?> result = service.submit(() -> {
                for (int i = 0; i < 1_000_000; i++) counter++;
            });
            result.get(10, TimeUnit.SECONDS); // returns null for Runnable
            System.out.println("Reached!");
        } catch (TimeoutException e) {
            System.out.println("Not reached in time");
        } finally {
            service.shutdown();
        }
    }
}
```

For `TimeUnit`, we can choose NANOSECONDS, MICROSECONDS, MILLISECONDS, SECONDS, MINUTES, HOURS and DAYS.

*Functional interface* `Callable<T>` is like `Runnable`, but it has a non-`void` generic return type, and may throw
a checked exception:

```java
@FunctionalInterface
public interface Callable<T> {
    T call() throws Exception;
}
```

If after calling `shutdown()` we would like to wait for the results, without needing the results themselves (as `Future`
instance method `get()` would give us), consider using `ExecutorService` method `awaitTermination(long, TimeUnit)`.

Method `awaitTermination(long, TimeUnit)` returns `true` if the executor terminated and `false` if the timeout elapsed
before termination. Not surprisingly, it can throw a checked `InterruptedException` if interrupted while waiting.
Again, make sure that `awaitTermination` is called after `shutdown`.

#### Scheduling tasks

With static factory method `Executors.newSingleThreadScheduledExecutor()` we get a `ScheduledExecutorService`.
*Interface* `java.util.concurrent.ScheduledExecutorService` extends interface `ExecutorService`, adding the following methods:
* `schedule(Callable<T>, long, TimeUnit)`, taking a task and delay, and returning a `ScheduledFuture<T>`
* `schedule(Runnable, long, TimeUnit)`, taking a task and delay, and returning a `ScheduledFuture<?>`
* `scheduleAtFixedRate(Runnable, long, long, TimeUnit)`, taking a task, initial delay and period, and returning a `ScheduledFuture<?>`
* `scheduleWithFixedDelay(Runnable, long, long, TimeUnit)`, taking a task, initial delay and period, and returning a `ScheduledFuture<?>`

Some remarks about methods `scheduleAtFixedRate` and `scheduleWithFixedDelay`:
* These 2 methods *create a new task repeatedly* (at the given rate or after the given delay, respectively)
* This also means that if the main thread quickly calls `shutdown` and then terminates, any "new tasks" (after 1st delay, 2nd delay etc.) will not be started!
* These 2 methods return a `ScheduledFuture`, representing pending completion of the series of repeated tasks
* The "repeated task loop" will run forever, unless cancelled
* Cancellation can be implemented as a scheduled task that calls `ScheduledFuture.cancel` on the returned `ScheduledFuture`, leading to an unchecked `CancellationException`
* The `ScheduledFuture.get` calls will never return normally
* Method `scheduleAtFixedRate` is problematic when each task consistently takes longer than the execution interval

Note that for all 4 methods mentioned above the following holds:
* If the main thread quickly calls `shutdown` and then terminates, any (one-time or repeated) tasks not yet created will be discarded!

Interface `ScheduledFuture<T>` adds one method to `Future<T>`, namely `getDelay(TimeUnit)`, which returns the remaining delay.

#### Increasing concurrency with thread pools

So far we have seen *static factory methods* in class `Executors` that create single-thread executors. For more concurrency
it makes sense to create executors that use *thread pools* of reusable pre-instantiated threads. The static factory methods in
class `Executors` that we need to know about are:
* `newSingleThreadExecutor()`, returning an `ExecutorService`
  * the returned executor service uses a single worker thread that processes tasks in the order that they were submitted
* `newSingleThreadScheduledExecutor()`, returning a `ScheduledExecutorService`
  * the returned executor service uses a single worker thread that is capable of running scheduled one-time or repeated tasks
* `newCachedThreadPool()`, returning an `ExecutorService`
  * the executor service uses a thread pool that creates new threads as needed and then reuses them
* `newFixedThreadPool(int)`, returning an `ExecutorService`
  * the returned executor service uses a thread pool with a fixed number of threads
* `newScheduledThreadPool(int)`, returning a `ScheduledExecutorService`
  * the returned executor service uses a thread pool that is capable of running scheduled one-time or repeated tasks

All these methods have overloads that take an extra parameter of *interface* type `java.util.concurrent.ThreadFactory`.

### Writing thread-safe code

*Thread-safety* is a property of an *object* that it can safely be used in a multithreaded environment *without its state
being corrupted* as a consequence of interfering threads.

To make classes thread-safe, we need to explicitly use *techniques* to protect data in a multithreaded environment.

My personal take on *strategies* for achieving *thread-safety*, which is foremost about protecting *shared mutable state*:
* *Don't share* the data among threads (e.g. in a servlet application data is typically created in the context of handling 1 HTTP request in 1 request handling thread, without sharing the data)
* *Don't mutate* the data; this is in many cases the most scalable (and maybe underrated) approach: make data *deeply immutable*, and thread-safety concerns disappear right away
* *Don't mutate shared data at the same time*; many of the "synchronisation mechanisms" below fall in this category

Unfortunately, the Java platform does not really have the features to prevent thread-safety concerns early on in application design.
Again, disciplined use of *deep immutability* goes a long way in preventing thread-safety issues.

#### Understanding thread-safety

Even a `++` (pre-/post-)increment operation is *not atomic* when the data can be accessed simultaneously by multiple
threads. For example, the following program (from the book) can have quite different outputs in different runs, with
duplicated numbers and/or mixed up order of the numbers. Clearly this program is not thread-safe, foremost because it
suffers from *lost updates*.

```java
public class SheepManager {
    // Not thread-safe
    private int sheepCount = 0;
    private void incrementAndReport() {
        System.out.println((++sheepCount) + "");
    }
    public static void main(String[] args) {
        ExecutorService service = Executors.newFixedThreadPool(20);
        try {
            SheepManager manager = new SheepManager();
            for (int i = 0; i < 10; i++) {
                service.submit(() -> manager.incrementAndReport());
            }
        } finally {
            service.shutdown();
        }
    }
}
```

Indeed, even a small operation like `++` is *not atomic*, and increments may get lost, caused by `++` being 2 operations,
namely a read and a write operation. For example, the following order of events is possible when 2 threads try to increment
the same variable at the same time:
1. Thread A reads variable `sheepCount` as 1
2. Thread B reads variable `sheepCount` as 1
3. Thread A writes variable `sheepCount` as 2
4. Thread B writes variable `sheepCount` as 2

This unexpected result of 2 tasks running at the same time is called a *race condition*.

#### Accessing data with volatile

Keyword `volatile` in a variable declaration ensures that only 1 thread at a time updates the variable at one time and that
data read among multiple threads is consistent. This does not solve the thread-safety problem above, however, because we
still have the problem that `++` is not atomic but 2 operations instead. With keyword `volatile` we do not fix that:

```java
// Not good enough
private volatile int sheepCount = 0;
```

#### Protecting data with atomic classes

An operation is called *atomic* when it can be carried out as a single unit of execution without any interference from
another thread, instead making the other thread wait for this operation to end.

The Concurrency API offers support for *atomic operations*, in the `java.util.concurrent.atomic` package. In that package
we find classes like `AtomicBoolean`, `AtomicInteger`, `AtomicLong` and `AtomicReference<V>`, offering atomic updates.

Let's list some of the methods of class `AtomicInteger`. Some `AtomicInteger` *instance methods* are:
* `get()`, returning the current (int) value
* `set(int)`, atomically setting a new value, equivalent to assignment (`=`)
* `getAndSet(int)`, like `set()` but also returning the old value
* `incrementAndGet()` and `getAndIncrement()`, atomically incrementing and returning the new value and old value, respectively
* `decrementAndGet()` and `getAndDecrement()`, atomically decrementing and returning the new value and old value, respectively

Class `AtomicLong` is analogous, but for primitive type `long` instead of `int`. Class `AtomicBoolean` is analogous, but
for `boolean` and without the increment/decrement operations.

Class `AtomicReference<V>` is analogous to `AtomicBoolean`, but for reference types instead of `boolean`. It also has methods:
* `getAndUpdate(UnaryOperator<V>)`, to perform the given "update operation" atomically and returning the old value
* `updateAndGet(UnaryOperator<V>)`, to perform the given "update operation" atomically and returning the new value

Classes `AtomicInteger` and `AtomicLong` have similar methods.

Using `AtomicInteger` we could adapt the problematic program above by declaring variable `sheepCount` as follows:

```java
private AtomicInteger sheepCount = new AtomicInteger(0);
```

The statement with the "increment" would then become:

```java
System.out.println(sheepCount.incrementAndGet() + "");
```

The result would be that duplicate numbers would no longer occur, but the order of numbers in the output is still unknown.

#### Improving access with synchronized blocks

What if we need to safely update 2 atomic variables at the same time? Then we might use a *monitor*, also known as *lock*.
This *monitor* supports *mutual exclusion*, which means that only one thread can execute a certain code segment at the
same time.

Any Java `Object` can be used as monitor. For example (taken from the book):

```java
var manager = new SheepManager();
synchronized(manager) {
    // Code segment to be executed by one thread at a time
}
```

Only 1 thread can obtain the monitor at each moment in time. If one thread has obtained the lock and is in the process
of running the code segment, another thread trying to acquire the lock transitions to the BLOCKED state, until the lock
becomes available again.

Note that synchronisation only works when synchronizing on *the same Java Object*.

We can use keyword `synchronized` as modifier of *instance methods*. The semantics are the same as synchronizing on the
`this` reference, scoped to the entire method body.

We can even use keyword `synchronized` as modifier of *static methods*. There the lock is on Object `MyClass.class`.

#### Understanding the Lock framework

A more powerful monitor/lock is offered by *interface* `java.util.concurrent.locks.Lock` and its `ReentrantLock` implementation.

The following code snippets are equivalent:

```java
Object object = new Object();
synchronized (object) {
    // Protected code
}
```

and

```java
Lock lock = new ReentrantLock();
try {
    lock.lock();
    // Protected code
} finally {
    lock.unlock();
}
```

This try-finally pattern with `ReentrantLock` is a best practice, because we must make sure that locks are *obtained and
released exactly the same number of times*, in the right order. Only *release a lock that you have*, or else an unchecked
`java.lang.IllegalMonitorStateException` is thrown.

The following `Lock` interface *instance methods* are important to know:
* `public void lock()`, requesting a lock and blocking until a lock is acquired
* `public void unlock()`, releasing a lock
* `public boolean tryLock()`, requesting a lock but returning immediately without blocking; returns true if the lock was successfully acquired
* `public boolean tryLock(long timeout, TimeUnit unit)`, requesting a lock and blocking for the specified time or until the lock is acquired; returns true if the lock was successfully acquired

A safe pattern to use with `tryLock` is the following pattern:

```java
Lock lock = new ReentrantLock();
if (lock.tryLock()) {
    try {
        // Code executed if we have acquired the lock
    } finally {
        lock.unlock(); // Only releasing a lock we have acquired
    }
} else {
    // Code executed if we have not acquired the lock
}
```

Like the use of keyword `synchronized`, a `ReentrantLock` can obtain an *exclusive lock*, doing a *blocking wait* if the
lock is not yet available.

In addition, a `ReentrantLock` can do the following:
* request a lock without blocking (see above)
* request a lock while blocking for a specified amount of time (see above)
* create a `ReentrantLock` with a "fairness" property, granting the lock to threads in the order of lock requests

There is also a `ReentrantReadWriteLock`, which is quite useful but not necessary to know about for the exam.

#### Orchestrating tasks with a CyclicBarrier

We can use a `java.util.concurrent.CyclicBarrier` if we want a given number of "parties" (i.e. threads) to all reach a
certain "common barrier point", before they can all proceed past that barrier point. The barrier is created with a
constructor that takes a number of parties, e.g. `public CyclicBarrier(int parties)`. Then the code executed for all
parties includes an `await()` call, which returns the "arrival index" (ranging from `parties - 1` to `0`, inclusive).
Each `await()` call decrements the "arrival index". Once "arrival index" zero is reached, the barrier is released.

That's the happy flow. The details are more complex if a participating thread is interrupted or the barrier is reset.

The `CyclicBarrier` is *cyclic* because it can be reused after the waiting threads are released. Method `reset()` resets
the barrier.

Note that using a `CyclicBarrier` along with a thread pool as an `ExecutorService` allows us to avoid sequential processing
in a single thread without adding too much complexity.

### Using concurrent collections

The Java Concurrency API not only offers support for thread management, but it also offers *Java collections* that can
be shared by multiple threads. These *concurrent collections* fit naturally in the Java Collections API, implementing
familiar interfaces like `List`, `Set`, `Queue` and `Map`.

A personal note on thread-safe collections: the *safest collections* to use in a multithreaded environment are *immutable
collections*. The Java platform does not deliver them, but *Guava* does, e.g. *ImmutableList* and *ImmutableSet*.

#### Understanding memory consistency errors

*Concurrent collections* are designed to prevent *memory consistency errors*, so to prevent 2 threads from having inconsistent
views of what should be the same data. We want writes on one thread to *happen before* and therefore be available to
another thread reading the data after the write operation has occurred. For an explanation of *memory visibility* issues,
see for example [Java memory model](https://jenkov.com/tutorials/java-concurrency/java-memory-model.html).

When 2 threads try to modify the same non-concurrent collection, an unchecked `java.util.ConcurrentModificationException`
may be thrown. Even with a single thread this exception can be thrown. Consider the following example (from the book):

```java
Map<String, Integer> foodData = new HashMap<>();
foodData.put("penguin", 1);
foodData.put("flamingo", 2;
for (String key : foodData.keySet()) {
    foodData.remove(key);
}
```

A `ConcurrentModificationException` will be thrown during the second loop iteration, because after removing the first
element the iterator on `foodData.keySet()` should be properly updated but that is not the case.

This is solved if we replace the creation of the `Map` with the following code, using a concurrent `Map`:

```java
Map<String, Integer> foodData = new ConcurrentHashMap<>();
```

Now the iterator on `foodData.keySet()` is properly updated after removing an element.

#### Working with concurrent classes

Whenever multiple threads modify a collection outside a synchronized block or method, a *concurrent collection* should
be used (or a Guava immutable collection, I would like to add).

Before mentioning some important Java concurrent collection implementation types, let's consider some *collection interfaces*,
in the loose sense (because `Map` does not extend `Collection`), leaving out the type parameters:
* `List`
  * There are no interface subtypes to mention here
* `Set`
  * Interface subtype `SortedSet`, promising a *total ordering* on the elements in the set
  * Interface type `NavigableSet` extending `SortedSet`, adding methods for finding "closest matches"
* `Queue`
  * Interface subtype `BlockingQueue`, adding methods to do *blocking waits*
* `Map`
  * Interface subtype `SortedMap`, promising a *total ordering* on the keys
  * Interface type `NavigableMap` extending `SortedMap`, adding methods for finding "closest matches"
  * Interface subtype `ConcurrentMap`, providing thread-safety and atomicity guarantees
  * Interface type `ConcurrentNavigableMap` extending `ConcurrentMap` and `NavigableMap`

Interface type `BlockingQueue` promises support for *blocking waits*. All collection types (concurrent or not) that do
not express "blocking behaviour" in the interface/class name do not offer any blocking behaviour.

Moreover, a `Set` implementation that does not implement `SortedSet` is not sorted. The same is true for a `Map`
implementation that does not implement `SortedMap`.

The *concurrent collection classes* to be familiar with (except for the extra methods) are (again leaving out type parameters):
* `java.util.concurrent.CopyOnWriteArrayList`
  * It implements `List`
  * Under the hood it is a thread-safe `ArrayList` where all mutator methods make a fresh copy of the underlying array
  * So this implementation makes sense as concurrent `List` if there are far more reads than writes
  * Iterators do not see modifications to the collection
* `java.util.concurrent.CopyOnWriteArraySet`
  * It implements `Set`
  * It is backed by a `CopyOnWriteArrayList`
  * Iterators do not see modifications to the collection
* `java.util.concurrent.ConcurrentSkipListSet`
  * It implements `Set`, `SortedSet` and `NavigableSet`
  * Under the hood it uses a `ConcurrentSkipListMap`
  * Strings "concurrent" and "skip" in the name suggest "sorted concurrent"
* `java.util.concurrent.ConcurrentLinkedQueue`
  * It implements `Queue`
  * It is a thread-safe FIFO queue based on linked nodes
* `java.util.concurrent.LinkedBlockingQueue`
  * It implements `Queue` and `BlockingQueue`, and is the only "blocking" collection in this list of collection implementations
  * It is a thread-safe FIFO queue based on linked nodes and offering blocking behaviour
  * It offers overloaded blocking `offer` and `poll` methods taking a timeout
* `java.util.concurrent.ConcurrentHashMap`
  * It implements `Map` and `ConcurrentMap`
  * Under the hood it uses a hash table
  * Like legacy class `Hashtable` it is thread-safe, but it offers far more concurrency and never locks on retrieval
* `java.util.concurrent.ConcurrentSkipListMap`
  * It implements `Map`, `SortedMap`, `NavigableMap`, `ConcurrentMap` and `ConcurrentNavigableMap`
  * Under the hood it uses a concurrent variant of so-called *skip lists* (see [skip list](https://en.wikipedia.org/wiki/Skip_list))
  * Strings "concurrent" and "skip" in the name suggest "sorted concurrent"

Above we ignored thread-safe `Deque` implementations.

This is an example (in the book) of using a `CopyOnWriteArrayList`, where the iterator does not see modifications to the
collection:

```java
List<Integer> favNumbers = new CopyOnWriteArrayList<>(List.of(4, 3, 42));
for (var n : favNumbers) {
    System.out.print(n + " ");
    favNumbers.add(n + 1);
}
// Afterwards, favNumbers.size() returns 6, because the iterator is not modified
// With a regular ArrayList, we would get a ConcurrentModificationException
```

#### Obtaining synchronized collections

If we have non-concurrent collection objects, and we need thread-safety, consider using *static methods* of class
`java.util.Collections`, such as:
* `synchronizedCollection(Collection<E>)`
* `synchronizedList(List<E>)`
* `synchronizedSet(Set<E>)`
* `synchronizedSortedSet(SortedSet<E>)`
* `synchronizedNavigableSet(NavigableSet<E>)`
* `synchronizedMap(Map<K, V>)`
* `synchronizedSortedMap(SortedMap<K, V>)`
* `synchronizedNavigableMap(NavigableMap<K, V>)`

When possible, prefer the concurrent collection implementations mentioned above.

### Identifying threading problems

We have learned how to solve potential thread-safety issues with "synchronisation mechanisms" requiring some form of
locking (if we have to deal with *shared mutable state*), but the other side of the coin is the potential for "threading
problems", in particular *liveness problems*. These are problems in which an application becomes unresponsive or appear
to be "stuck", due to threads being "stuck", waiting for resources that require a lock.

The following *liveness problems* are important to know about:
* *deadlock*
* *starvation*
* *livelock*

Even the use of the Java Concurrency API cannot completely prevent these issues.

*Deadlock* occurs when 2 threads block each other. It is often the consequence of threads trying to get locks in the opposite
order. For example, see this pattern where deadlock may occur:

```java
// One thread running this code
synchronized(resource1) {
    // Assume the thread is at this point
    synchronized(resource2) {
        // do something ...
    }
}

// And the other thread running this code,
// trying to acquire locks in the opposite order
synchronized(resource2) {
    // Assume the thread is at this point
    synchronized(resource1) {
        // do something ...
    }
}
```

*Starvation* happens when a thread hardly gets a chance to acquire a lock that is claimed continuously by other threads.

*Livelock* occurs when 2 or more threads are *conceptually blocked,* although they are still active. *Failed deadlock
recovery* is a good example of livelock.

*Race conditions* occur when 2 tasks that should run sequentially run at the same time and interfere with each other.
For example, 2 users are trying to create an account with the same username at the same time.

What we certainly do not want to happen in that case is that both users get the same username. One or both users getting
an error message is better, and does not affect the (data) integrity of the system.

### Working with parallel streams

The `Stream` instances in chapter 10 were *sequential* ones, using only 1 thread when processing the stream.
We can also use *parallel streams*, exploiting multiple threads during processing of the stream.

If the work done by a stream pipeline is easy to divide over multiple threads, the performance gains of parallel streams
(processing many elements) can be dramatic.

The stream result should ideally be "equivalent" for sequential and parallel processing, but this is only the case if certain
requirements are met.

#### Creating parallel streams

A parallel stream can be created in one of 2 ways:
* Using a `parallelStream()` call on the source `Collection`
* Turning a `Stream` into a parallel one by calling `Stream` instance method `parallel()`

Since streams are processed in a lazy manner, we can repeatedly turn sequential streams into parallel ones and vice versa.
The latest such call "wins". Stream method `isParallel()` can be used to test whether a stream is parallel or not.

#### Performing a parallel decomposition

*Parallel decomposition* is the process of taking a task, dividing it into smaller pieces that can be processed concurrently,
and then reassembling the results.

A stream pipeline where each element of a collection is independently mapped to an element of the result collection is
certainly a good example of stream processing that can safely be done in parallel. But the same is true for well-designed
*reductions*, as shown below.

Note that terminal operation `forEach` does not respect encounter order, so especially in parallel streams, if the encounter
order of elements must be respected, use terminal operation `forEachOrdered` instead of `forEach`. This may come with some
performance penalty, of course.

#### Processing parallel reductions

Like terminal operation `forEach`, terminal operation `findAny` is also explicitly nondeterministic. For sequential streams
it may tend to return the first element, but that's not even guaranteed. For parallel streams this is even unlikely.

Stream operations `skip`, `limit` and `findFirst` do respect encounter order, if there is any, but for parallel streams
this may come with a substantial performance penalty. So these methods have exactly the same semantics for parallel streams
as for sequential streams, but at a runtime cost.

Intermediate operation `unordered` tells the JVM that the encounter order can be ignored for order-based stream operations.
This can potentially significantly speed up parallel stream pipelines involving methods like `skip`, `limit` and `findFirst`.

Recall `Stream` terminal operation `reduce`, taking an *identity*, *accumulator* function and *combiner* function:

```java
public <U> U reduce(U identity, BiFunction<U, ? super T, U> accumulator, BinaryOperator<U> combiner);
```

Note that the *combiner* function is needed in parallel stream processing. In sequential stream processing the
*identity* and *accumulator* function taken together perform a simple *recursive* algorithm to compute the result,
with the accumulator on each call taking the accumulated result so far along with the next element of the stream.
In parallel stream processing, all 3 `reduce` parameters are used.

In parallel streams, we must be concerned about the order. Yet following some simple rules, the order does not affect
the stream pipeline result. The idea is that accumulator and combiner are such that they can be called in any order. If
that is the case, parallel stream processing returns the same result as sequential processing.

Let's make this a little bit more formal, even if the book does not do that. In order for the `reduce` call to work exactly
as expected, whether the stream is sequential or parallel, first of all the *accumulator* and *combiner* functions must be
*stateless*, *non-interfering* and *side-effect-free*. Moreover:
* The *identity* value must be an *identity for the combiner* function
  * I.e. `combiner.apply(identity, u).equals(u)`
* The *combiner* function must be *associative*
  * I.e. `combiner.apply(u, combiner.apply(v, w)).equals(combiner.apply(combiner.apply(u, v), w))`
* The *combiner* function must be *compatible* with the *accumulator* function
  * I.e. `combiner.apply(u, accumulator.apply(identity, t)).equals(accumulator.apply(u, t))`

Put differently, the *combiner* function with its parameter type make up what is called a *monoid* in *category theory*,
and the *accumulator* must be compatible with it. Take for example type `String` and the string concatenation operation.
Clearly string concatenation is *associative* (so the order of evaluating a chain of concatenations is irrelevant).
This makes type `String` with the concatenation operation a so-called *semigroup*. Add to that the fact that the empty
string is the *identity value* for string concatenation, and we can conclude that type `String` with the string concatenation
operation make up a *monoid*. Type `int` with integer addition also make up a monoid, with zero as the identity value.
String concatenation is *not communicative* (unlike integer addition), but still a monoid. This is what we see in 2 of
the mentioned 3 requirements above, namely "identity" and "associativity".

See also [package java.util.Stream Javadoc](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/stream/package-summary.html).

Note that unlike integer addition, integer subtraction is not a monoid, and unfit to use in a Stream `reduce` call.
After all, subtraction is not associative. For example: `(1 - 1) - 1 != 1 - (1 - 1)`.

Also string concatenation with a non-empty string as "identity" is not a monoid, and unfit to use in a Stream `reduce` call.

Now consider *mutable reduction*, using method `collect`:

```java
public <U> U collect(Supplier<U> supplier, BiConsumer<U, ? super T> accumulator, BinaryConsumer<U, U> combiner);
```

The *supplier*, *accumulator* and *combiner* are tightly coupled, and can be combined into a `Collector`, as we have seen.
An overloaded Stream `collect` method directly takes a `Collector` (which may have been created by class `Collectors`).

"In spirit" the requirements for *mutable reductions* are similar to those for "regular" reductions.

For parallel streams, if the *supplier* in the `collect` call creates a collection, it should create a *concurrent collection*.

A *concurrent reduction* is a mutable reduction where the result container is a concurrent collection, and where there is
no need for the combiner to merge result containers. This efficient *concurrent reduction* is only possible if:
* The stream is *parallel*
* The `Collector` has characteristic `Collector.Characteristics.CONCURRENT`
* Either the stream is *unordered*, or the `Collector` has characteristic `Collector.Characteristics.UNORDERED`

To ensure that a stream is unordered, use stream method `unordered`.

Class `Collectors` has several static methods to create concurrent reduction `Collector` instances, such as:
* `toConcurrentMap`
* `groupingByConcurrent`

Finally, *avoid stateful behavioral parameters* anywhere in a stream pipeline, to prevent data corruption and unexpected
results. The morale of the story is to *play by the rules of stream processing*, and get clear semantics, performance
and no data corruption as a result.
