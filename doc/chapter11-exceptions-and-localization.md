# Chapter 11. Exceptions and Localization

See [OCP Java SE 17 Developer Study Guide](https://www.amazon.com/Oracle-Certified-Professional-Developer-Study/dp/1119864585/ref=sr_1_1?crid=1GIZNHYFXHAK4&dib=eyJ2IjoiMSJ9.Mz5O0lUSaZhUZ-O1Mi__dRPfXHL9GM_CfZ3JDTz910a2d8XI7Vsfj7zwcywJAfMcubfCglH02m8PwlAk_DORk8SS5460zaDP1fskFDX4sUiFVR4pxE1Ln0VIY-g5awTQaOJKp4t0Y1HchXkrw0HtOeVSHg3dHG8Jql9TibGCj-WeXYyNdMp4zWtgM4EimHpl4wvlJZufvGpNjNEmXIObAd2B1mp1skt5k7v_B-k_Ip4.bRERgxl7gsekO5AihUKuOeT_yoO6Bsg7jHigb4sjHEM&dib_tag=se&keywords=ocp+java+se17&qid=1714573695&sprefix=ocp+java+%2Caps%2C192&sr=8-1).

This chapter is about *exceptions*, *formatting* and *localization*.

More information can be found in the (more low-level)
[Java AST API](https://docs.oracle.com/en/java/javase/17/docs/api/jdk.compiler/com/sun/source/tree/package-summary.html),
which contains links to the Java Language specification for specific language constructs.

In particular, see:
* [MethodTree](https://docs.oracle.com/en/java/javase/17/docs/api/jdk.compiler/com/sun/source/tree/MethodTree.html), in particular the "throws clause"
* [TryTree](https://docs.oracle.com/en/java/javase/17/docs/api/jdk.compiler/com/sun/source/tree/TryTree.html)
* [ThrowTree](https://docs.oracle.com/en/java/javase/17/docs/api/jdk.compiler/com/sun/source/tree/ThrowTree.html)

### Understanding exceptions

*Exceptions* signal that "something is wrong", which could be a programming error, invalid input, network connection problems etc.

When an *exception* is thrown, the *happy path* in the flow is abandoned. After that:
* either the exception is *handled* by the method itself
* or the handling of the exception is made the responsibility of the *method caller* (and so on, typically until handled at some point)

Exception types are *Java classes*. So it is allowed to store exceptions in variables, for instance. Also, just like is the
case for all class instances, an exception constructor is invoked with the `new` keyword.

At the top of the inheritance hierarchy we have class `java.lang.Throwable`.

It has direct subtypes `java.lang.Error` and `java.lang.Exception`. When an `Error` is thrown, there is not much we can do
about that.

There are 2 kinds of exceptions:
* *Checked exceptions*; their type is or extends `java.lang.Exception`, but not its subtype `java.lang.RuntimeException`
* *Unchecked exceptions* (or *runtime exceptions*); their type is or extends `java.lang.RuntimeException`

For *checked exceptions* (and only for checked exceptions) we hava the *handle or declare rule*. That is, if a method
throws a checked exception, it must:
* either *handle* the exception itself, in a *try-catch block*
* or *declare in the method declaration that it may throw the exception* (using keyword `throws`, as opposed to `throw` which is used for throwing exceptions)

Hence, it is important to *recognize checked exceptions* and violations of the "handle or declare rule" for those exceptions.

*Unchecked exceptions* are also often caught and handled in application code, but for them there is no *handle or declare requirement*.

It is allowed for a method to declare that it can throw an exception (of a certain exception type), without even ever doing so.

*Unchecked exceptions* are often documented in a Javadoc comment, although this is not required.

*Throwable* (and *Error*) instances that are not `Exception` instances are typically never thrown from application code.
Neither are they typically caught in application code.

Besides code *explicitly throwing an exception* it is quite possible that *exceptions are thrown implicitly*. For example:
* trying to access an object where the *reference is a null pointer*
* trying to access an array member at an *index that is out of bounds*
* *dividing by zero*

Be aware of the following pitfall: if code throws an exception, while making it impossible to reach code after that,
the compiler will report an *unreachable code error*! Declaring an unused exception is not considered unreachable code,
however. Yet catching a checked exception that could not possibly be thrown at that point (by looking at `throws` clauses)
is considered unreachable code!

Recall that when *overriding instance methods* the *Liskov substitution principle* must be respected in order for
*polymorphism* to work. For exceptions that means that an overridden method (so a method having the exact same
*method signature* as a method in a supertype) *may not throw any additional/broader checked exceptions* that are
not allowed by the supertype method. In order to check this, take into consideration which exception types are subtypes
of each other, if any. The main point is that the "set of checked exceptions" that can be thrown by the subclass method
is a subset of the "set of checked exceptions" thrown by the supertype methods (where both sets may also be equal).

When printing an exception, we can:
* print the exception's `toString()` output (which by default includes exception type and message)
* print the exception's `getMessage()` value
* print the *stack trace*, through method `printStackTrace()`, showing the hierarchy of *method calls* leading to the exception

Some own remarks about exceptions:
* printing a stack trace or exception message is not really *handling the exception*; the question is: can we *still reason about program state*?
* very many exceptions *cannot be successfully handled*; that's often ok, let them ripple up to some outer ("framework") layer
* exceptions should match the "abstraction layer"; this may mean wrapping exceptions in other exceptions may be appropriate

### Recognizing exception classes

Some *unchecked exceptions* to know by heart are:
* `java.lang.ArithmeticException`, thrown when trying to divide by zero
* `java.lang.ArrayIndexOutOfBoundsException`, thrown when using an illegal index to access an array
* `java.lang.ClassCastException`, thrown when trying to cast an object to a class of which it is not an instance (if the compiler could not catch this, of course)
* `java.lang.NullPointerException`, thrown when there is a `null` reference where an object is required
* `java.lang.IllegalArgumentException`, thrown when a method argument is illegal or inappropriate
  * subtype `java.lang.NumberFormatException`, thrown when trying to convert a String of the wrong format to a number

Note that since Java 17 NullPointerExceptions are "more helpful", showing the object reference that triggered the NPE.
These helpful exception messages can be disabled in the `java` command by JVM option:

```shell
-XX:-ShowCodeDetailsInExceptionMessages
```

Some *checked exceptions* to know by heart are:
* `java.io.IOException`, thrown programmatically when there is a problem reading or writing a file
  * `java.io.FileNotFoundException`, thrown programmatically when referencing a file that does not exist
  * `java.io.NotSerializableException`, thrown when attempting to (de)serialize a non-serializable class
* `java.text.ParseException`, which indicates an input parsing problem
* `java.sql.SQLException`, which is a database-access-related exception

Some *error* types are:
* `java.lang.ExceptionInInitializerError`, thrown when a static initializer throws an unhandled exception
* `java.lang.StackOverflowError`, typically caused by *infinite recursion*
* `java.lang.NoClassDefFoundError`, thrown when a used class is available at compile time but not at runtime

### Handling exceptions

TODO
