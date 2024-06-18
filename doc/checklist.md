# Checklist

When confronted with a *code snippet*, we typically need to check:
1. whether it *compiles*
2. if so, whether it *runs successfully* (without throwing an exception, without running forever, etc.)

That's the main topic of this checklist.

### Quickly checking whether a code snippet compiles successfully

First of all, *read very carefully*! For example:
* Do we mix up *throw* and *throws*?
* Do we miss any *semicolons*, or conversely, do we see them where they should not occur?
* Do we mix up *commas* with *semicolons*, or *colons* with *semicolons*?
* Do we mix up `=` with `==`, for example in a loop/if-statement condition?
* Do we miss any *braces* where a block is required?
* Do we miss any *parentheses*, e.g. in a do-while statement?
* Do we incorrectly interpret *nested* if-statements?
* Are constructor parameters assigned to (possibly equally named) instance fields and *not to themselves*?

Does a code snippet *compile successfully*? It might help to quickly follow these steps:

1. Check the *syntactic structure* of *classes/interfaces and their members*
2. Check *scope, types and initialization of variables*
3. Check *additional rules* checked by the compiler

#### Checking syntax of classes/interfaces and their members

This is about very quickly recognizing the *overall structure* of a piece of code, recognizing interfaces and classes,
and their members like constructors, static/non-static fields, static/non-static methods, nested classes etc. This is
also about quickly recognizing *subtype relationships* and constructor call chains.

Syntax of *control structures* (such as loop statements, switch statements/expressions etc.) is also checked in this step.

Examples of checking the "overall" syntax:
* Distinguish *methods* from *constructors* (sometimes methods can be made to look suspiciously like constructors)
* Distinguish *instance members* from *static members*
* Note that classes *implement* interfaces (keyword `implements`), classes *extend* another class, and interfaces *extend* other interfaces (keyword `extends`)
* When making `super` calls explicit in constructors (as first statement), where appropriate, is there always a superclass constructor that's called?
* Are *control structures* syntactically correct?

#### Checking scope, types and initialization of variables

Examples of checking scope, types and initialization of variables:
* Does a *static member* try to access an *instance member*?
* Static/instance fields are automatically initialized. Local variables are not. Are *uninitialized local variables used*?
* *Final static fields* must be explicitly initialized *precisely once*, *during class initialization* (so all static fields are always initialized during class initialization)
* *Final instance fields* must be explicitly initialized *precisely once*, *during object construction* (so all instance fields are always initialized during object construction)
* *Local variables* (in the broad sense of the word) *cannot shadow each other* (that includes lambda parameters)
* Mind *flow scope* (in particular in combination with "local variable shadowing")
* `switch` statements/expressions can not take `long`, reference types other than `String` and primitive wrappers, etc.
* Loop conditions etc. must be `boolean` expressions
* Enhanced for-loops loop over arrays or `Iterable`
* ...

#### Additional rules checked by the compiler

Examples of additional rules checked by the compiler, other than syntax/scope/type checks:
* Do we miss any needed *checked exceptions in a throws clause*? Some APIs that "invite" checked exceptions are:
  * `Thread.sleep(long)` etc., potentially throwing an `InterruptedException`
  * NIO.2, which is quick to throw `java.io.IOException`
  * JDBC, which is quick to throw `java.sql.SQLException`
* Related: Do lambda expressions typed as familiar *functional interfaces* like `Function`, `Predicate` etc. fail to catch *checked exceptions* (and maybe rethrow them as unchecked exceptions)?
* Is it tried to update a `final` variable?
* Does a variable have to be `final` or *effectively final*? This is a requirement in the following cases:
  * When local variables (in the broad sense) are used in a *lambda expression*, they must be *effectively final*
  * When local variables (in the broad sense) are used in a *local or anonymous class*, they must be *effectively final*
  * When local variables (in the broad sense) that have already been declared are used in the *resource specification* of a *try-resources statement*, they must be *effectively final*
* Does the compiler detect *unreachable code*?
* Is a method *correctly overridden*? Method overriding only applies to *instance methods* having the *same signature* as *non-private instance methods* in a supertype
* Is a method *correctly overloaded*? Method overloading only applies to methods having the *same name* but different signature
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

### Standard APIs to know

The standard APIs below are in module `java.base`, unless mentioned otherwise. Package `java.sql` is in module `java.sql`.

| API description             | Java packages          | Characteristics                             |
|-----------------------------|------------------------|---------------------------------------------|
| Core `java.lang` APIs       | `java.lang`            |                                             |
| Collections Framework       | `java.util`            |                                             |
| Concurrent Collections      | `java.util.concurrent` |                                             |
| Functional interfaces       | `java.util.function`   |                                             |
| Stream API                  | `java.util.stream`     |                                             |
| Time API                    | `java.time`            |                                             |
| "Old" Java I/O API          | `java.io`              |                                             |
| NIO.2                       | `java.nio.file`        |                                             |
| Formatting API              | `java.text`            |                                             |
| Math                        | `java.math`            |                                             |
| JDBC                        | `java.sql`             |                                             |

Other types to know about include:
* `java.net.URI`

### Language constructs to know

TODO
