# Summary of Effective Java, 3rd Edition

Besides learning the OCP material, it makes a lot of sense to be familiar with the content of the book
*Effective Java (3rd Edition)*, authored by *Joshua Bloch*. Whereas the OCP material teaches us about Java itself,
the book Effective Java teaches us about how to use java *idiomatically*. More precisely, the book is about
writing *clear, correct, usable, robust, flexible and maintainable programs*, as the book says in the introduction.
Performance will typically follow relatively easily once writing such programs.

This document summarizes the book Effective Java, following the exact structure of the book in terms of chapters
and items per chapter.

**Of course this summary in no way replaces the book itself. It is absolutely worth the money to buy the book itself.**
For example, the example code in the book is carefully chosen to prove the points made by the author. Even examples
in the Java platform itself that go against the advice in the book are included. The author clearly knows what he is
talking about, and this book also shows many lessons learned by maintainers of the Java platform, including the author
himself (who wrote the Java 1.2 Collections Framework, among other APIs).

Another summary/discussion of the items in the book Effective Java can be found [here](https://dev.to/kylec32/).

The advice offered in this book is sometimes a bit at odds with common practices in "Java enterprise" projects, for example
due to limitations imposed on application programmers by byte-code manipulation libraries, and due to "legacy APIs".
Still, it is good to be aware of *competing design forces* and to try to find a reasonable balance. It can be said
that this book is about *coding discipline*, which is a good thing. Experimenting a lot while coding is great, balancing
that with discipline is even better. Good code is often *boring code*.

It sometimes seems to me that this book Effective Java is less well-known nowadays than it used to be. Yet I remember
that the 3 editions of this book have made a *tremendous positive impact on the quality of Java code* "in the field".

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

The contract of method `equals` states that it must be an *equivalence relation*, so it must be:
* *reflexive* (for non-null reference values)
* *symmetric* (for non-null reference values)
* *transitive* (for non-null reference values)

Moreover, the `equals` contract states that:
* this method must be *consistent* (for non-null reference values), so consistently return `true` or `false` if no data (that `equals` depends on) is changed
* for any non-null reference values, `equals(null)` must return `false`

Symmetry and transitivity are *very easy to break*, potentially causing serious *bugs*, for example when using these
classes having broken equality in collections. E.g., what does `Collection.contains` return if equality is broken for the element type?

Joshua Bloch illustrates broken transitivity of `equals` with imaginary class `Point` (having a position) and subclass
`ColorPoint` (adding a color). He says that class `java.sql.Timestamp` (which extends `java.util.Date`) has the same issue.
The problem is very *fundamental* (and not Java-specific), he says. Namely, in his words:

**There is no way to extend an instantiable class and add a value component while preserving the equals contract**, unless
we sacrifice the *Liskov substitution principle* (which says that a subclass must adhere to the API contract of the superclass).

In that case, *favoring composition over inheritance* (item 18) may be helpful. Alternatively, consider the use of a
`canEqual` method as described in
[how-to-write-an-equality-method-in-java](https://www.artima.com/articles/how-to-write-an-equality-method-in-java).

The *recipe* for writing an overridden `equals` method follows rather easily from the contract. Yet keep the following in mind:
* always override `hashCode` when overriding `equals` (see next item)
* do not write any equality method *overloading* `java.lang.Object.equals`
* do not try to be too clever when overriding `equals`

A *personal note*, applying to relatively recent Java LTS versions such as Java 17 and 21:

These Java versions ship with *Java record classes*. Combined with *Guava immutable collections* it is easier than ever
to create *deeply immutable thread-safe data classes* with automatically created well-behaved `equals` and `hashCode` methods.
The idea is to create such classes by *recursively* following the *construction rules* below:

1. Start with "atomic" *non-record non-collection* immutable classes (such as `String`, `LocalDate` etc.)
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

Well-designed components hide their implementation details, and separate the *API* from the *implementation*.
This is known as *information hiding* or *encapsulation*. It is important, because it *decouples* components comprising
a system, and therefore allows them to be developed, tested, used, understood and maintained in isolation.
Encapsulation therefore helps increase *flexibility*, *correctness* and *development speed*, among other things.

Rule of thumb: *make each class and member as inaccessible as possible*.

Mechanisms for encapsulation:
* For top-level classes/interfaces: `public` or package-private (the default); the latter makes the class part of the implementation
* For members (fields, methods, nested classes/interfaces): `public`, `protected`, package-private and `private`; only the latter 2 provide encapsulation
  * But mind the possibility that serialization breaks encapsulation even for private members (the same is true for reflection)
  * Note that the difference between `protected` and package-private is far greater than the difference between package-private and `private`
  * Method overriding: the compiler does not allow for breaking the *Liskov substitution principle* by *restricting the access level*
* Java 9+ *modules* can help limit access among Java packages
  * In the end (outside the Java platform itself) they are only advisory, because it is still possible to use the class path instead of the module path

Instance fields of public classes should rarely be public. Classes with public mutable fields are generally not thread-safe.
Immutable public static final "constants" are ok, though.

Classes should not have static final array fields, or accessors returning such a field. Defensive copies can fix this.

Summarized: design a *minimal public API* and try to keep implementation details encapsulated and hidden from that public API.

### Item 16: In public classes, use accessor methods, not public fields

### Item 17: Minimize mutability

*Immutable classes* are classes *whose instances cannot be modified*. Some examples in the Java platform are `String`,
`LocalDate`, `URI`, `BigInteger` and `BigDecimal`. Immutable classes are easy to reason about, and hugely helps in
achieving program *correctness*.

Nowadays, with Java records, making immutable (record) classes is quite easy. Ignoring records, a Java class can be made
*immutable* as follows:
* Make all *fields* `private` and `final`, and *ensure exclusive access to mutable components* by *defensive copies* in constructors/getters
* Do not provide any *mutator* methods (that would not work anyway if the requirement above is met)
* Ensure that the class *cannot be extended* (e.g. by making the class `final`), thus preventing subclasses from being mutable

So-called update methods in immutable classes are *functional update* methods, creating a new immutable object instead
of modifying existing objects. This is the *functional* approach, in contrast to the *imperative/procedural* approach.

Again, Java records make this very easy, offering value equality out of the box as well.

Immutable classes are *simple* (instances have just one state, forever), *inherently thread-safe* (but mind the semantics
of `final` in this regard) and they can be *shared freely*.

Defensive copies (or copy constructors) are never needed for immutable classes.

Not only can immutable classes be shared freely, but also their internals can be shared.

Immutable objects are *great building blocks for other objects*. They also provide *failure atomicity* for free. In particular,
they make great `Map` keys and `Set` elements.

The major disadvantage of immutable classes is that they require a *separate object for each distinct value*.

Often it makes sense to have an immutable class and a mutable companion class for creating instances of the immutable
class. A well-known example is the pair of classes `String` (immutable) and `StringBuilder` (mutable).

Implementation freedom for immutable classes can be achieved by not making the class `final`, but instead making it
*effectively final*, using private constructors and public static factory methods. This allows for multiple package-private
implementation classes.

Be aware of the dangers of serialization in combination with fields referring to mutable objects. This is covered in item 88.

In summary:
* Classes should be *immutable* unless there's a good reason to make them mutable
* If a class cannot be made immutable, *limit its mutability* as much as possible
* Declare each field `private final` unless there's a good reason to do otherwise (see item 15 as well)
* Constructors should create *fully initialized objects with all invariants established*

The advice in this item makes it quite easy to *reason about objects* and their state (which is just one state for the
duration of the life of immutable objects), and therefore helps *enforce correctness*.

### Item 18: Favor composition over inheritance

Inheritance from classes not designed for inheritance is dangerous (see below). In the book, inheritance means
*implementation inheritance*, not the far safer *interface inheritance* (i.e. a class implementing an interface, or an
interface extending another interface).

*Unlike method invocation, inheritance violates encapsulation*. After all, the subclass *depends on implementation details
of the superclass*, and these implementation details may change from release to release. Also, "self use" among superclass
methods may break method overriding in the subclass.

*Composition* rather than inheritance does not break encapsulation. The example given in the book uses an imaginary
`InstrumentedSet`, where the alternative using inheritance is broken and the alternative using *forwarding* methods
is safe. (Again, one level of indirection saves the day.) This is known as the *decorator pattern*, and the combination
of composition and forwarding is loosely known as *delegation*.

A disadvantage of wrapper classes is that they are not suited for use in *callback frameworks*, where objects pass self-references
to other objects for subsequent invocations. The wrapped object does not know about the wrapper, however. This is known
as the *SELF problem*.

When choosing to use inheritance, make sure that there really is an *is-a* relationship from subclass to superclass.
The Java platform itself violates this principle, since `Stack` extends `Vector`, whereas it shouldn't.

In summary, *inheritance is powerful but problematic, because it violates encapsulation*. Instead, use *composition*.

### Item 19: Design and document for inheritance or else prohibit it

### Item 20: Prefer interfaces to abstract classes

TODO

Note that a class can only extend one superclass, whereas a class can implement multiple interfaces. Interfaces also
allow for the construction of non-hierarchical type frameworks.

Advantages of interfaces and abstract classes can be combined in the *template method pattern*. For example, interface
`List`, abstract implementation `AbstractList` and concrete subclasses `ArrayList` and `LinkedList`.

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

Java may be a *safe language* compared to C and C++ (with their memory corruption issues), we must still
*program defensively, and assume clients of your class do their best to destroy its invariants*, whether intentionally
or unintentionally.

Suppose you write a `Period` class with 2 legacy `java.util.Date` fields as an "almost immutable class", following the
advice of item 17 (minimize mutability) except for the defensive copies in constructor and getters. Then it is quite
easy to modify the internals of a `Period` instance, through a `Date` mutator method. Of course class `java.util.Date`
should no longer be used, but the point remains. Defensive copies may also be needed for *collection-valued fields*,
for example.

Mind the window of vulnerability between the time parameters are checked and the time they are copied, so check
validity on the copies rather than the originals.

Do not use method `clone` to make defensive copies, if the class is sub-classable by untrusted parties.

This advice about defensive copies of mutable state goes beyond their need in immutable classes containing mutable
internal state. Clearly, this item on *defensive copies* is about *correctness* (and security).

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

*If appropriate interfaces exist, then parameters, return values, variables and fields should be declared using
interface types*. This habit makes programs far more *flexible*. Sometimes there is no appropriate interface, in particular
for "value classes". In that case, use the *least specific class in the class hierarchy providing the required functionality*.

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

Generally, *a failed method call should leave an object in the state that it was in prior to the method call*.
This is called *failure atomicity*. This property of programs helps reason about *correctness*.

For immutable classes, failure atomicity is free. For methods operating on mutable objects, check parameters for
validity (and throw an exception when needed) before performing the operation.

A third approach to achieve failure atomicity is to perform the operation on a temporary copy of the object and replace
the contents of the object with the temporary copy once the operation is complete.

A fourth approach (less common) is to write failure recovery code.

Failure atomicity is not always achievable, nor is it always desirable (if the downside is complexity or performance).
Still, it should be strived for, and for immutable classes it comes for free. When deviating from this advice, this should
be documented in the API documentation.

### Item 77: Don’t ignore exceptions

## Chapter 11. Concurrency

### Item 78: Synchronize access to shared mutable data

Note that a JVM has one stack (of stack frames) per thread, but just one heap, containing the created non-GC'ed objects.
Obviously, some form of synchronisation is needed when multiple threads access the same objects on the heap.

Indeed, as the title of the item says: *synchronize access to shared mutable data*.
Data typically stands for fields of objects. Shared stands for "shared between threads". Mutability stands for mutability
anywhere in the object graph (if the field is not of primitive type).
Synchronization stands not just for the `synchronized` keyword. See the *Java Memory Model* (and the semantics of `synchronized`,
`volatile` and `final`, for example).

From the title it is obvious that 2 potential ways of preventing data corruption due to sharing mutable data are:
* do not shared the data; typically, make the data accessible from only one thread (e.g. *request-handling-thread-scoped* data in a Servlet application)
* do not mutate the data; in other words, make the data *immutable*

Synchronizing access to shared mutable data is needed for *mutual exclusion*, but also for *reliable communication between threads*.
See the Java Memory Model. To help understanding, data may reside outside of main memory during thread context switches, so some
synchronization mechanism is needed to make sure the data is flushed to main memory before the thread context switch.

Also note that in order for synchronization to work *both read and write operations must be synchronized*.

Note that *immutable* classes are *thread-safe*, without needing any further synchronization. Also note that mutable classes like
`AtomicReference` are thread-safe, and often a great tool to make application classes thread-safe.

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
