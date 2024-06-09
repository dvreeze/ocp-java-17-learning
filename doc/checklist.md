# Checklist

When confronted with a *code snippet*, we typically need to check:
1. whether it *compiles*
2. if so, whether it *runs successfully* (without throwing an exception, without running forever, etc.)

That's the main topic of this checklist.

### Quickly checking whether a code snippet compiles successfully

Does a code snippet *compile successfully*? It might help to quickly follow these steps:

1. Check the *syntactic structure*
2. Check *scope and types of variables*
3. Check *additional rules* checked by the compiler

#### Checking syntax

TODO Add content...

Examples of checking the syntax:
* Distinguish *methods* from *constructors*
* Distinguish *instance members* from *static members*
* ...

#### Checking scope and types of variables

Examples of checking scope and types of variables:
* *Local variables* (in the broad sense of the word) *cannot shadow each other*
* Mind *flow scope*
* `switch` statements/expressions can not take `long`, reference types other than `String` and primitive wrappers, etc.
* ...

#### Additional rules checked by the compiler

Examples of additional rules checked by the compiler, other than syntax/scope/type checks:
* Is it tried to update a `final` variable?
* Does a variable have to be `final` or *effectively final*?
* Does a static member try to access an instance member?
* Does the compiler detect *unreachable code*?
* Is a method *correctly overridden*?
* ...

### Quickly checking whether a code snippet runs successfully

Below, assume that the code snippet to check compiles successfully. Does it run successfully, or does it throw an exception,
or does it run forever?

TODO Add content...

There are endless possibilities w.r.t. *hidden runtime behavior* not visible in the code:
* *Mutable* data (e.g. `ArrayList` constructor) and *unmodifiable* data (e.g. `List.of` result) versus *immutable* data (e.g. `String`)
* *Side-effecting* calls (e.g. `StringBuilder.append`) versus *functions returning new (immutable) objects* (e.g. `LocalDate.plus`)
* *Collections* versus *streams and iterators* that are *processed only once* (e.g. `Stream`, `java.io.Reader`)
* *Eager* versus *lazy* evaluation
* *Synchronous* versus *asynchronous* code
* *Single-threaded* versus *multithreaded* code
* *Autocloseable* resources versus resources/objects that are not autocloseable (e.g. `ExecutorService`)
* *Nullable* versus *non-nullable* variables
* *Thread-safe* classes versus *non-thread-safe* classes
* Is an object *backed by some resource* or not? E.g. `java.sql.Connection` is certainly backed by a JDBC resource
* ...

Another perspective to look at code is to use an "FP perspective" (e.g. see ZIO library documentation).
Is a *function* or *operator*:
* *Deterministic*? (counterexample: `Thread.getStackTrace`)
* *Total*? (counterexample: division, where division by zero throws an exception)
* *Pure* or *free from side effects*? (counterexample: `StringBuilder.append`)

I remember this as "DTP" (deterministic, total, pure).
