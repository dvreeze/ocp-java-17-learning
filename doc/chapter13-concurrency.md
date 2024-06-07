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
although technically that is not correct.

System and user-defined threads can be created as *daemon threads*. These are threads that will not prevent the JVM from
exiting when the program finishes. In other words, a Java program ends when the only remaining threads are daemon threads.

By default, user-defined threads are not daemon threads, so the program will wait for them to finish.

#### Managing a thread's life cycle

A created `Thread` can be in one of 6 states. The 3 "non-pausing" thread states are:
* *New*
  * This is the state of a thread that has been created but not yet started
* *Runnable*, which means that the thread either runs or is able to run
  * The first time the thread is in this state is after calling `start()` on a freshly created thread
  * A thread can go from "runnable" to one of the 3 "pausing" states mentioned below, and back from those states to "runnable"
* *Terminated*
  * This is the end state of the thread, when the `run()` method completes or an uncaught exception is thrown

The 3 "pausing" thread states are:
* *Blocked*, so waiting to enter a *synchronized block*
  * When a thread is runnable and starts waiting for a "monitor lock", it enters this state
  * When subsequently access to the "monitor lock" has been granted, the thread becomes runnable again
* *Waiting*, so waiting indefinitely to be *notified*
  * When a thread is runnable and calls instance method `Object.wait()`, it enters this state
  * When subsequently instance method `Object.notify()` is called, the thread becomes runnable again
* *Timed_waiting*, so waiting a specified time
  * When a thread is runnable and calls static method `Thread.sleep()` (among other calls), it enters this state
  * When the sleep time has passed, the thread becomes runnable again

Not all possible thread state changes are mentioned above. For example, an interrupted thread in state *Timed_waiting*
will go straight back to state *Runnable*.

Thread methods `wait()`, `notify()` and `join()` are beyond the scope of the exam. They should not be used anyway.
The concurrency API should be used instead.

The thread state can be queried with method `getState()`, returning an enumeration value from enum `Thread.State`.

#### Polling with sleep

TODO

#### Interrupting a thread

TODO

### Creating threads with the concurrency API

TODO
