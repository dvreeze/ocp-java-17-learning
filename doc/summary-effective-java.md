# Summary of Effective Java, 3rd Edition

Besides learning the OCP material, it makes a lot of sense to be familiar with the content of the book
Effective Java (3rd Edition), authored by Joshua Bloch. Whereas the OCP material teaches us about Java itself,
the book Effective Java teaches us about how to use java *idiomatically*. More precisely, the book is about
writing *clear, correct, usable, robust, flexible and maintainable programs*, as the book says in the introduction.
Performance will typically follow relatively easily once writing such programs.

This document summarizes the book Effective Java, following the exact structure of the book in terms of chapters
and items per chapter.

**Of course this summary in no way replaces the book itself. It is absolutely worth the money to buy the book itself.**

Another summary/discussion of the items in the book Effective Java can be found [here](https://dev.to/kylec32/).

## Chapter 2. Creating and Destroying Objects

### Item 1: Consider static factory methods instead of constructors

Public *static factory methods* have some advantages compared to constructors:
* they have names (which may increase readability)
* they are not required to create a new object on each invocation
* they can return an object of any subtype of the return type
* the class of the returned object can ever vary from call to call based on the input parameters
* the class of the returned object need not exist when writing the class containing the static factory method (thus supporting *service provider frameworks*, e.g. JDBC)

In other words, static factory methods offer *flexibility*. That is, they have *expressive power*.

Some limitations of static factory methods are:
* classes without public or protected constructors cannot be subclassed (which may be desirable, encouraging the use of composition instead of inheritance)
* they are harder to find in the API than constructors (although there are some common names for static factory methods)

### Item 2: Consider a builder when faced with many constructor parameters

Suppose object construction (through a constructor or static factory method) requires a lot of parameters. Consider using
the *Builder pattern* (especially if many construction parameters are optional). The Builder pattern can even be used for creating objects
of different types in a class hierarchy.

The Builder pattern is more clear than the telescoping constructor pattern. It is certainly more safe than Java Beans, because it can
make sure that objects are created in a *consistent state*. Note that Java Beans (with getters and setters) cannot be used
for immutable classes.

In other words, the Builder pattern can help achieve *clarity* and *correctness*.

### Item 3: Enforce the singleton property with a private constructor or an enum type

A singleton class has *precisely 1 instance*. This can be enforced by a private constructor, in combination with a public
static field or factory method returning the single instance. Yet mind potential attacks through reflection, and vulnerabilities
using Java (de)serialization (consider using a readResolve method, while making all instance fields transient).

A *single-element enum* also has uses these techniques, while protecting against reflection and serialization attacks
that lead to multiple element instantiations. A single-element enum is also concise. It cannot extend another superclass, however.

So a single-element enum helps achieve *clarity* and *correctness* in enforcing the singleton property.

Singleton classes (including single-element enums) can make its clients difficult to test, however (since the singleton
is typically hard to mock).

### Item 4: Enforce noninstantiability with a private constructor

Some classes ("utility classes") contain only static methods (and maybe static fields), and are not meant to be instantiated.
Non-instantiation can be enforced by a private constructor (that is not called), accompanied by a clarifying comment.
Note that the private constructor also prevents the creation of subclasses.

Again, this is about *clarity* (and *correctness*).

### Item 5: Prefer dependency injection to hardwiring resources

Many classes have *underlying resources as dependencies*. Implementing them as utility classes or singleton classes that
create the underlying resource themselves is quite inflexible and untestable. It is much better to *pass the resource
in the constructor* when creating a new instance of the class. In other words, use a form of *dependency injection*,
where the underlying resource is a *dependency* that is *injected* on creating an instance of the class.

This form of dependency injection also helps preserve *immutability*, which helps enforce *correctness* and *clarity*.

It enhances *flexibility*, *reusability* and *testability* of a class (the latter through easy mocking of dependencies).

Dependency injection "clutter" can be reduced by the use of the dependency injection framework.

### Item 6: Avoid creating unnecessary objects

Object creation on the JVM is *cheap in general*. Still, *some objects are expensive to create*, and this object creation
is not always visible. For example, consider matching strings against regular expressions. A reused `Pattern` (holding
a compiled regular expression) may dramatically improve execution speed compared to repeated implicit `Pattern` creation
under the hood.

Immutable objects can always be reused. For example, using a `String` constructor passing a `String` literal as constructor
argument makes no sense at all. *Immutable classes offering static factory methods* may also help avoid unnecessary
creation of different equal instances.

Mind the runtime costs of *unintentional autoboxing* of primitives.

On the other hand, creating own object pools is typically a bad idea. So is lazy initialisation of fields.
Program clarity should not suffer, and creating additional objects for clarity, simplicity or flexibility is generally
a good thing. Correctness should never be sacrificed, so when *defensive copies* are needed, we should never avoid them
in order to increase performance at the expense of correctness.

### Item 7: Eliminate obsolete object references

Java's *garbage collector* may leave the impression that *thinking about memory management* is not needed. This is not
entirely true.

For example, if we create some `Stack` class that *manages its own memory*, we may invite *memory leaks* (aka *unintentional
object retentions*). This can be solved by *nulling out references* once they become obsolete. This technique should
be an exception rather than the norm, but in this case it may be needed. Simply letting *reference variables fall out of scope*
should be the norm otherwise.

*Caches* may also invite memory leaks. So do *listeners and other callbacks*, if they are not deregistered explicitly.

*Weak references* (such as keys in a `WeakHashMap`) may help prevent memory leaks.

### Item 8: Avoid finalizers and cleaners

In the C++ language, destructors are normally used to reclaim resources associated with an object. There destructors are
the necessary counterparts to constructors. In Java, which uses garbage collection, there is no such thing as destructors.
Java offers *finalizers* instead (i.e., method `Object.finalize`). Java 9 deprecated them and offered *cleaners* as an
alternative. See [Cleaner](https://docs.oracle.com/javase/9/docs/api/?java/lang/ref/Cleaner.html). Unfortunately, finalizers
and cleaners are quite unpredictable, and therefore they should normally be avoided.

Finalizers are unpredictable, often dangerous, and mostly unnecessary. Cleaners, while less dangerous than finalizers,
are still unpredictable and mostly unnecessary.

First of all, after an object becomes unreachable (so a candidate for garbage collection) it *cannot be predicted when
its finalizer or cleaner runs, if ever*. This may also vary significantly between JVMs.

Uncaught exceptions during finalization are ignored, potentially leaving objects in a corrupt state. Cleaners do not have
this specific problem.

Finalizers inhibit efficient garbage collection, making them a slow way to destroy objects. Cleaners are generally slow as well.

Finalizers are also open to so-called *finalizer attacks*. Normally, throwing an exception from a constructor prevents
the object from being created, but in the presence of finalizers this is not the case!

A much safer alternative to finalizers/cleaners is implementing the `AutoCloseable` interface, and requiring users of the
class to call the `close` method, typically by using a *try-with-resources* statement.

Cleaners may be useful only as safety net (when users of the class might forget to call the `close` method) or to terminate
noncritical native resources. Otherwise, they should not be used at all. Starting with Java 9, finalizers should never be
used at all.

### Item 9: Prefer try-with-resources to try-finally

Closing resources was done using a *try-finally* statement before the advent of *try-with-resources* statements. The latter
leads to more *concise* code, especially with more than 1 resource that must be closed. But it also *keeps track of
suppressed exceptions*, leading to better diagnostics when exceptions are thrown.

Obviously, try-with-resources statements require the resources to implement interface `AutoCloseable`. This is the case
for all kinds of resource-representing classes in the JDK, but can also be implemented by application code.

In short, the try-with-resources statement makes it practically possible to write correct code using resources that must
be closed. This was not the case with try-finally statements. So try-with-resources statements offer *correctness*
and *clarity* when dealing with resources that must be closed.

## Chapter 3. Methods Common to All Objects

### Item 10: Obey the general contract when overriding equals

Every class ultimately extends class `java.lang.Object`, and therefore inherits its `equals` method, which must obey
a certain *contract*.

By default, `equals` behaves as *object identity*. By all means, do not override `equals` if:
* each instance of the class is inherently unique
* a "logical equality" test is not needed
* a superclass has overridden `equals`, and the superclass behaviour is appropriate for this class
* the class is private or package-private and the `equals` method will never be invoked

Never write an `equals` method depending on unreliable resources.

Overriding `equals` is appropriate for *value classes*, where *logical equality* differs from object identity.

The contract of method `equals` states that it must be an *equivalence relationship*, so it must be:
* *reflexive* (for non-null references)
* *symmetric* (for non-null references)
* *transitive* (for non-null references)

Moreover, the `equals` contract states that:
* this method must be *consistent* (for non-null references), so consistently return `true` or `false` if no data (that `equals` depends on) is changed
* for any non-null references, `equals(null)` must return `false`

Symmetry and transitivity are *very easy to break*, potentially causing serious *bugs*, for example when using these
classes having broken equality in collections. E.g., what does `Collection.contains` return if equality is broken for the element type?

Joshua Bloch illustrates broken transitivity of `equals` with imaginary class `Point` (having a position) and subclass
`ColorPoint` (adding a color). He says that class `java.sql.Timestamp` (which extends `java.util.Date`) has the same issue.
The problem is very *fundamental* (and not Java-specific), he says. Namely, in his words:

**There is no way to extend an instantiable class and add a value component while preserving the equals contract**, unless
we sacrifice the *Liskov substitution principle* (which says that a subclass must adhere to the API contract of the superclass).

The *recipe* for writing an overridden `equals` method follows rather easily from the contract. Yet keep the following in mind:
* always override `hashCode` when overriding `equals` (see next item)
* do not write any equality method *overloading* `java.lang.Object.equals`
* do not try to be too clever when overriding `equals`

A *personal note*, applying to relatively recent Java LTS versions such as Java 17 and 21:

These Java versions ship with *Java record classes*. Combined with *Guava immutable collections* it is easier than ever
to create *deeply immutable thread-safe data classes* with automatically created well-behaved `equals` and `hashCode` methods.
The idea is to create such classes by *recursively* following the *construction rules* below:

1. Start with "atomic" *non-record non-collection* immutable classes
2. Each *record* class whose components are clearly immutable data classes is itself clearly an immutable data class with well-behaved equality
3. Each *immutable Guava collection* whose element types (or key/value types) are clearly immutable data classes is itself clearly an immutable data class with well-behaved equality

The effort to create these classes is minimal, while it is very *easy to reason about* them w.r.t. equality, immutability, thread-safety etc.

### Item 11: Always override hashCode when you override equals

When overriding method `equals`, method `hashCode` must be overridden too. The `hashCode` contract states that *equal objects*
(according to method `equals`) must have the *same hash code* (returned by method `hashCode`).

When failing to do so, instances of such classes may not be found in a `HashSet` collection or as keys in a `HashMap`.
After all, based on the `hashCode` value, the element is first searched for in the *hash bucket* for its hash code,
and only then the equality check using method `equals` is done. Yet if the "same" element is found in another hash bucket,
the equality check will never be done, so for example method `HashSet.contains` would not work.

### Item 12: Always override toString

Overriding method `toString` makes a class more pleasant to use and aids in debugging.

There are pros and cons to specifying the format returned by the `toString` method. Note that the `toString` method is
not a substitute for programmatic access to the information contained in the `toString` result.

### Item 13: Override clone judiciously

### Item 14: Consider implementing Comparable

## Chapter 4. Classes and Interfaces

### Item 15: Minimize the accessibility of classes and members

### Item 16: In public classes, use accessor methods, not public fields

### Item 17: Minimize mutability

### Item 18: Favor composition over inheritance

### Item 19: Design and document for inheritance or else prohibit it

### Item 20: Prefer interfaces to abstract classes

### Item 21: Design interfaces for posterity

### Item 22: Use interfaces only to define types

### Item 23: Prefer class hierarchies to tagged classes

### Item 24: Favor static member classes over nonstatic

### Item 25: Limit source files to a single top-level class

## Chapter 5. Generics

### Item 26: Don’t use raw types

### Item 27: Eliminate unchecked warnings

### Item 28: Prefer lists to arrays

### Item 29: Favor generic types

### Item 30: Favor generic methods

### Item 31: Use bounded wildcards to increase API flexibility

### Item 32: Combine generics and varargs judiciously

### Item 33: Consider typesafe heterogeneous containers

## Chapter 6. Enums and Annotations

### Item 34: Use enums instead of int constants

### Item 35: Use instance fields instead of ordinals

### Item 36: Use EnumSet instead of bit fields

### Item 37: Use EnumMap instead of ordinal indexing

### Item 38: Emulate extensible enums with interfaces

### Item 39: Prefer annotations to naming patterns

### Item 40: Consistently use the Override annotation

### Item 41: Use marker interfaces to define types

## Chapter 7. Lambdas and Streams

### Item 42: Prefer lambdas to anonymous classes

### Item 43: Prefer method references to lambdas

### Item 44: Favor the use of standard functional interfaces

### Item 45: Use streams judiciously

### Item 46: Prefer side-effect-free functions in streams

### Item 47: Prefer Collection to Stream as a return type

### Item 48: Use caution when making streams parallel

## Chapter 8. Methods

### Item 49: Check parameters for validity

### Item 50: Make defensive copies when needed

### Item 51: Design method signatures carefully

### Item 52: Use overloading judiciously

### Item 53: Use varargs judiciously

### Item 54: Return empty collections or arrays, not nulls

### Item 55: Return optionals judiciously

### Item 56: Write doc comments for all exposed API elements

## Chapter 9. General Programming

### Item 57: Minimize the scope of local variables

### Item 58: Prefer for-each loops to traditional for loops

### Item 59: Know and use the libraries

### Item 60: Avoid float and double if exact answers are required

### Item 61: Prefer primitive types to boxed primitives

### Item 62: Avoid strings where other types are more appropriate

### Item 63: Beware the performance of string concatenation

### Item 64: Refer to objects by their interfaces

### Item 65: Prefer interfaces to reflection

### Item 66: Use native methods judiciously

### Item 67: Optimize judiciously

### Item 68: Adhere to generally accepted naming conventions

## Chapter 10. Exceptions

### Item 69: Use exceptions only for exceptional conditions

### Item 70: Use checked exceptions for recoverable conditions and runtime exceptions for programming errors

### Item 71: Avoid unnecessary use of checked exceptions

### Item 72: Favor the use of standard exceptions

### Item 73: Throw exceptions appropriate to the abstraction

### Item 74: Document all exceptions thrown by each method

### Item 75: Include failure-capture information in detail messages

### Item 76: Strive for failure atomicity

### Item 77: Don’t ignore exceptions

## Chapter 11. Concurrency

### Item 78: Synchronize access to shared mutable data

### Item 79: Avoid excessive synchronization

### Item 80: Prefer executors, tasks, and streams to threads

### Item 81: Prefer concurrency utilities to wait and notify

### Item 82: Document thread safety

### Item 83: Use lazy initialization judiciously

### Item 84: Don’t depend on the thread scheduler

## Chapter 12. Serialization

### Item 85: Prefer alternatives to Java serialization

### Item 86: Implement Serializable with great caution

### Item 87: Consider using a custom serialized form

### Item 88: Write readObject methods defensively

### Item 89: For instance control, prefer enum types to readResolve

### Item 90: Consider serialization proxies instead of serialized instances
