# Checklist to use in exam questions

When confronted with a *code snippet* in exam questions, we typically need to check:
1. whether it *compiles*
2. if so, whether it *runs successfully* (without throwing an exception, without running forever, etc.)

That's the main topic of this checklist.

### Quickly checking code snippets

Many exam questions start with a code snippet before or after the main question. It makes sense to first read the question
well, and then get an idea of the *solution space* in the potential answers, in order to determine how to best go about
quickly answering the question. For example:
* If possible answers to a question asking what a code snippet does include normal output, exception at runtime and compilation error, we know the *solution space* is quite large
* If such a question only has possible answers that show output, then the solution space is much smaller and does not include exceptions or compilation errors

When inspecting the code snippet and the potential answers, first of all *read carefully*. Don't mix up `throw` with `throws`,
`=` with `==` etc.

How to best quickly check code snippets (in question and/or possible answers) depends on the code snippet.

If a code snippet contains *1 or more interfaces and/or classes*, in any case we need to quickly *understand the global
structure* of the classes/interfaces, and *static versus instance scope* for their members. That is:
* What are the top-level (and nested) classes and interfaces?
  * Remember: classes *extend* a superclass, interfaces *extend* other interfaces, but classes *implement* interfaces
  * If applicable, what are the *static and instance initializer blocks*?
* What are the *constructors*, in the case of classes (since interfaces do not have them)?
  * Be careful: it is to be expected that at least one question makes a *method look like a constructor*
  * What is the *constructor call chain* and does it work? E.g., if the superclass has one constructor only, and it has a parameter, the subclass constructor must invoke this superclass constructor (with argument)
* What are the *static and instance fields*?
  * Note that the only fields in interfaces are `public static final` constants, whether these modifiers are inferred or explicit
* What are the *static and instance methods*?
  * Note that in interfaces non-private instance methods without body are always `public abstract`, whether these modifiers are inferred or explicit
* Note that *nested interfaces/enums/records* are implicitly *static* (so they cannot and do not share any state with any "instance of the outer class")

If needed for answering the question, we may need to figure out *class initialization order* and *instance initialization order*.

When the same method name is used once or more in a class/interface and in subtypes, we probably need to check:
* Proper use of *non-private instance method overriding* (if applicable), where *equally named methods have the same method signature*
* Proper use of *method overloading*, where *equally named methods have different method signatures*

Depending on the question (and possible answers) we may need to dive deeper into class/interface members, in particular the methods:
* *Types* of fields, method/constructor results and method/constructor parameters
* Should a method declare any *thrown exceptions*?
* *Scope* and *type* of *local variables*
  * Pitfall: *local variables cannot shadow each other* (so in this context, also check method parameter names, lambda parameter names etc.)
* *Initialization* of *local variables*
  * If a local variable is `final`, is it indeed initialized exactly once?
  * Is there a requirement for a local variable to be *effectively final*?

Specific *control structures* (in particular `switch` statements and expressions) and *type declarations*, such as `record`,
`enum` etc. come with their own *syntactic and typing/scoping* rules. This should be checked whenever encountering these
control structures and custom kinds of classes. This is also true for *sealed types*. Of course, it is important to know
these rules well, but they only need to be checked where applicable.

Simple *typing* rules that are easy to overlook include:
* A loop/if-statement condition must be a `boolean` expression
* A resource in a resource specification of a try-resources statement must implement the `AutoCloseable` interface

Simple *scoping* rules that are easy to overlook include:
* A variable declared inside a block is scoped to that block, so:
  * In a while-do statement, any variable declared in the loop body is out of scope in the loop condition

#### Streams, functional interfaces, lambdas and method references

*Lambdas and method references* have a *functional interface* as type, and they get this type from "immediate context",
i.e., an *assignment*, *cast* or *method parameter*. The latter is the most usual case, as "functional" method arguments
in *stream pipelines*. So lambdas and method references must get their type from an assignment, cast or method parameter.

For *lambdas*, mind their syntactic requirements as well. If they are given in a context where other local variables exist,
mind the remarks about local variables not shadowing each other and about "effectively final".

For *method references*, we can immediately ditch method references that "get parameters" or that act as "lambda bodies",
when asked for valid expressions that happen to contain method references. This can save time on several "stream-related"
questions.

Of course, besides checking the correct use of methods in *stream pipelines* (e.g. `peek` gets a *Consumer* parameter),
we always need to check the *correct use of intermediate and terminal operations* in stream pipelines. E.g., without terminal
operation at the end, the stream pipeline is just lazy code waiting to be triggered but so far doing nothing.

### Standard APIs

Some standard APIs are quite "functional" in nature, and some are not. For example, the `java.time` API is "functional",
in that the methods take/return *immutable* objects. So is `String`. But `StringBuilder` is not, and its methods rely
on side effects (returning the same but altered object, instead of a fresh new immutable object).

Especially for "functional" APIs, check if *method results are used*. If not, those calls predominantly do nothing.

For specific APIs (e.g. NIO.2), check *which checked exceptions* are likely to occur in their methods. So check whether
we need to declare them in *throws clauses*.

### Other tips

If a question comes with multiple answers, each being snippets of code, it may help to start with the simplest one and
see what it "teaches us" about how to check the other answers.

In such questions it may also help to weed out incorrect answers by first checking the *syntax*. Depending on the variation
among the answers it may or may not be handy to weed out incorrect answers simply by comparing all answers.

## Old content

TODO Remove, but take from it what's useful.

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
* Syntax of *interfaces*
  * Allowed *members* of interfaces (like instance methods without body, `default` instance methods etc.)
  * *Inferred modifiers* (like `public`, `abstract` for instance methods without body, etc.)
* When making `super` calls explicit in constructors (as first statement), where appropriate, is there always a superclass constructor that's called?
* Are *control structures* syntactically correct?
  * Mind syntax rules for if-statements, loops, switch statements/expressions etc.

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
* Resources in try-resources statements must be `AutoCloseable`
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
  * For example: code directly after a `break`, `continue` or `return` statement that can never be reached will be rejected by the compiler
  * Same for code after `while (true) {}`, for example
  * Summarized: if the compiler can detect that all code paths make a subsequent statement unreachable, it will reject that code
  * See for example [Java unreachable statements](https://www.baeldung.com/java-unreachable-statements)
* Is a method *correctly overridden*? Method overriding only applies to *instance methods* having the *same signature* as *non-private instance methods* in a supertype
* Is a method *correctly overloaded*? Method overloading only applies to methods having the *same name* but different signature
* ...

### Checks for specific language constructs and APIs

It makes sense to do specific checks for certain language constructs and certain APIs, such as for:
* do-while-loops
* switch statements/expressions
* Java pattern matching and flow scope
* try-statements and try-resources statements
* lambdas and functional interfaces
* immutable `String` and mutable `StringBuilder` APIs
* immutable `java.time` API
* NIO.2 API (e.g. checked exceptions inside lambdas typed as functional interfaces not expecting any checked exceptions)
* JDBC

TODO Make such a list of language constructs and APIs on the one hand, with specific checks for them on the other hand.

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
