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

Below, (simplified) grammars of "try" statements are given. Different alternatives in a grammar rule occur on different lines,
`{}` means zero-or-more, and `[]` means zero-or-one. Terminal symbols (including keywords) are enclosed in double-quotes.

### Understanding exceptions

*Exceptions* signal that "something is wrong", which could be a programming error, invalid input, network connection problems etc.

When an *exception* is thrown, the *happy path* in the flow is abandoned. After that:
* either the exception is *handled* by the method itself
* or the handling of the exception is made the responsibility of the *method caller* (which may make it the responsibility of its caller, and so on)

Exception types are *Java classes*. So it is allowed to store exceptions in variables, for instance. Also, just like is the
case for all class instances, an exception constructor is invoked with the `new` keyword.

At the top of the inheritance hierarchy we have class `java.lang.Throwable`.

It has direct subtypes `java.lang.Error` and `java.lang.Exception`. When an `Error` is thrown, there is not much we can do
about that.

There are 2 kinds of exceptions:
* *Checked exceptions*; their type is or extends `java.lang.Exception`, but not its subtype `java.lang.RuntimeException`
* *Unchecked exceptions* (or *runtime exceptions*); their type is or extends `java.lang.RuntimeException` (which extends `java.lang.Exception`)

For *checked exceptions* (and only for checked exceptions) we have the *handle or declare rule*. That is, if a method
throws a checked exception, it must:
* either *handle* the exception itself, in a *try-catch block*
* or *declare in the method declaration that it may throw the exception* (using keyword `throws`, as opposed to `throw` which is used for actually throwing exceptions)

Hence, it is important to *recognize checked exceptions* and violations of the "handle or declare rule" for those exceptions.

*Unchecked exceptions* are also often caught and handled in application code, but for them there is no *handle or declare requirement*.

It is allowed for a method to declare that it can throw an exception (of a certain exception type), without even ever doing so.

*Unchecked exceptions* are often documented in a Javadoc comment, although this is not required.

*Throwable* (and *Error*) instances that are not `Exception` instances are typically never thrown from application code.
Neither are they typically caught in application code.

Besides code *explicitly throwing an exception* in application code or third party libraries it is quite possible that
*exceptions are thrown by the Java platform*. For example:
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
* exceptions should match the "abstraction layer"; this may mean wrapping exceptions in other exceptions may be appropriate; the wrapped exception can be retrieved with method `getCause()`

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
  * subtype `java.io.FileNotFoundException`, thrown programmatically when referencing a file that does not exist
  * subtype `java.io.NotSerializableException`, thrown when attempting to (de)serialize a non-serializable class
* `java.text.ParseException`, which indicates an input parsing problem
* `java.sql.SQLException`, which is an exception related to database access (the JDBC API)

Some *error* types are:
* `java.lang.ExceptionInInitializerError`, thrown when a static initializer throws an unhandled exception
* `java.lang.StackOverflowError`, typically caused by *infinite recursion*
* `java.lang.NoClassDefFoundError`, thrown when a used class is available at compile time but not at runtime

### Handling exceptions

Java separates the logic that throws an exception from the logic that handles the exception. The latter is done in
*try statements*. Java 7 introduced "try-with-resources" statements, so I dub the other try-statements "regular try statements".
The syntax is as follows:

```
regularTryStatement:
    "try" block catchClause { catchClause }
    "try" block { catchClause } "finally" block

catchClause:
    "catch" "(" catchFormalParameter ")" block
```

Recall that a *block* is a *statement that is surrounded by a pair of curly braces*. A block may itself contain many
statements, such as expression statements that end with a semicolon. So in a try-statement there is a try-block, with
mandatory enclosing curly braces. The catch-blocks and finally-block, if any, are also blocks, so they too have mandatory
surrounding curly braces.

Recall that *non-abstract method bodies are also blocks* (so also need the surrounding curly braces), whereas while-loops and
if-statements do not require the statements within them (i.e. the loop body and the if-branch and else-branch) to be blocks.

The *catchFormalParameter* is typically an exception type followed by a variable name. The scope of that variable is only
that specific catch-clause. It is a compile-time error for the exception type(s) not to be `Throwable` or a subclass.

Note that there must be at least one catch-clause or finally-clause, and there can be no more than one finally-clause
(and the finally-clause must come after the catch-clauses, if any).

Processing is as follows in a try-statement:
1. the try-block runs
2. either an exception is thrown in the try-block, or it runs successfully (in the latter case the flow "jumps to the finally-clause", if any)
3. if an exception has been thrown in the try-block, the flow searches for the first catch-clause that can handle the exception
4. if such a catch-clause has been found, it runs, and the flow goes to the finally-clause, if any, or else processing of the try-statement has finished
5. in all cases (success or thrown exception, whether handled by a catch-clause or not), at the end the finally-clause runs, if there is any

So in a try-statement, any exception thrown in the try-block is handled by *at most one catch-clause* (or *exception handler*),
namely the first one that can handle the exception, if there is any.

It is important to recognize *checked and unchecked exceptions*, because of the "handle-or-declare rule", but it is also
important to recognize *subtype relationships between exception types*. The latter is also important for recognizing
unreachable catch-clauses (which are treated as a compilation error). For example:

```java
try {
    doSomeFileStuff();
} catch (IOException e) {
    handleIOException(e);
} catch (FileNotFoundException e) {
    handleFileNotFoundException(e); // Unreachable code
}
```

It is possible for a catch-clause to be a *multi-catch clause* ("multi-catch block"). For example:

```java
try {
    doSomeStuff();
} catch (IOException | NumberFormatException | ArrayIndexOutOfBoundsException e) {
    handleIOException(e);
}
```

So in this case the *catchFormalParameter* (in the grammar above) has the form `ExcType1 | ExcType2 | ExcType3 excVariable`,
where the number of exception types can be 2 or more. The different exception types should be unrelated, and should not
be subtypes of one another, regardless of the order.

So this leads to a compilation error:

```java
try {
    doSomeStuff();
} catch (FileNotFoundException | IOException e) {
    handleIOException(e);
}
```

To show that the *finally-clause, if any, is always executed*, see the following examples, where an exception is thrown
as a result of the try-statement, but still the finally-clause runs before the try-statement is finished:

```java
// This will print 1 and 3, and throw an IllegalStateException
try {
    System.out.println(1);
    throw new IllegalStateException();
} catch (IllegalArgumentException e) {
    System.out.println(2);
} finally {
    System.out.println(3);
}
```

```java
// This will print 1, 2 and 3, and throw an IllegalStateException("Exception handler throwing exception")
try {
    System.out.println(1);
    throw new IllegalStateException();
} catch (RuntimeException e) {
    System.out.println(2);
    throw new IllegalStateException("Exception handler throwing exception");
} finally {
    System.out.println(3);
}
```

In the following example, the finally-clause determines the value returned:

```java
int tryThis() {
    try {
        System.out.println(1);
        var randomBoolean = new Random().nextBoolean();
        if (randomBoolean) throw new IllegalStateException();
        return -1;
    } catch (IllegalStateException e) {
        System.out.println(2);
        return -2;
    } finally {
        System.out.println(3);
        return -3;
    }
}

// This will return -3, and either print 1 and 3 or 1, 2 and 3
System.out.println(tryThis());
```

A finally-clause itself may also throw an exception, although typically that is not what is wanted.
If the finally-clause does throw an exception, this will be the exception thrown by the try-statement, and the other exceptions
thrown in try-block and/or catch-blocks will be "forgotten".

There is one exception to the rule that the finally-clause runs, and that is if `System.exit(int)` is called before that.

### Automatic resource management

*Resources* like database connections should *not leak*. So we should close them in time, preferably *in the same code*
that opened the connection (ignoring the topic of connection pooling for now).

Try-statements do help, but correctly coding this resource management can become quite verbose and cumbersome, especially
if there are more resources (e.g. besides a database connection in JDBC, a prepared statement and result set). This is
where *try-with-resources statements* come in. They perform *automatic resource management*.

The idea is that any class that implements interface `java.lang.AutoCloseable` (or in particular its subtype `java.io.Closeable`)
can be treated as a *resource* in a try-with-resources statement. For example, in the following code the `FileInputStream`
is automatically closed before leaving this try-with-resources statement:

```java
// FileInputStream is a Closable and therefore AutoCloseable
try (var is = new FileInputStream("myfile.txt")) {
    doSomeFileStuff(is);
} catch (IOException e) {
    handleIOException(e);
}
```

Yet a `String` is NOT an `AutoCloseable` and can therefore not be used as a resource in a try-with-resources statement.

Under the hood, the Java compiler turns this into a regular try-statement. The "hidden" finally-clause will close the
resource. It is still possible to add our own finally-clause, but that one would run after the compiler-generated
hidden one.

If multiple resources are created in a try-with-resources statement, they will be closed *in reverse order*.

The syntax of a try-with-resources statement is as follows:

```
tryWithResourcesStatement:
    "try" resourceSpecification block { catchClause } [ "finally" block ]

resourceSpecification:
    "(" resource { ";" resource } [ ";" ] ")"

resource:
    localVariableDeclaration
    variableAccess

variableAccess:
    expressionName
    fieldAccess

catchClause:
    "catch" "(" catchFormalParameter ")" block
```

Note that before the ending parenthesis of the resource specification, the semicolon there is optional. The semicolon
is required between the resources, though.

The *scope of a resource* is only the try-block itself, and not any catch-clauses or optional finally-clause after that!
The curly braces of the try-block indeed suggest that as well. The fact that the scope of a resource is limited to the
try-block is consistent with the fact that the resource is closed before running any catch-clauses or finally-clause
after that!

If resources are created ahead of time, and only referenced in the "resource specification" as variable name (without
type), they *must be final or effectively final*.

#### Understanding suppressed exceptions

It was said before that in a regular try-statement, if the finally-clause throws an exception, that exception "wins" and
all other exceptions thrown earlier in the try-statement are lost. The same is true for try-with-resources statements
with explicit finally clause. (Here we ignore the use of method `addSuppressed(Throwable)`, and assume that this method is not called.)

Yet what happens if in a try-with-resources statement the try-block throws an exception, and the resource `AutoCloseable.close()`
methods throw exceptions as well? Then the exception thrown from the try-block is known as the *primary exception*, and
exceptions thrown from the `AutoCloseable.close()` methods of the resources are *suppressed exceptions*. It is the
*primary exception* that will be thrown as a result (possibly handled by a subsequent catch-block in that try-with-resources
statement, or "masked" by an exception thrown from the explicit finally-block).

The *primary exception* knows about the *suppressed exceptions*, which are returned as an array of `Throwable` by method
`getSuppressed()`.

What happens if in a try-with-resources statement there are multiple resources, some or all of them throwing an exception
in the `close()` method, and no exceptions being thrown from the try-block? In that case the closing of the last declared
resource, which is the first one to be automatically closed, will throw the *primary exception*, and the other ones will be
*suppressed exceptions*.

### Formatting values

Recall static `String` methods `format(String, Object...)` and `format(Locale, String, Object...)`. These methods allow for
formatting strings containing numbers, dates, times etc. See class `java.util.Formatter`.

Sometimes we may need more fine-grained control. Package `java.text` contains a lot of support for (advanced) formatting
and parsing, for example abstract class `java.text.NumberFormat` (as the abstract base class for all number formats).

These formatters are mutable and not thread-safe, while still being somewhat "expensive" to create at runtime.

For dates and times it is much better to use the *immutable thread-safe* `DateTimeFormatter` in package `java.time.format`.

#### Formatting numbers

We can obtain an instance of `java.text.NumberFormat` through one of its static factory methods, such as
`NumberFormat.getInstance()` or `NumberFormat.getInstance(Locale)`. If we want direct control over the pattern used,
we can use a constructor of subclass `DecimalFormat`, however, for example `DecimalFormat(String)` where we pass the
pattern.

There are 2 formatting characters we need to know for `DecimalFormat`:
* `#`, meaning that the position must be omitted if there is no digit for it
* `0`, meaning that a `0` must be put in that position if no digit exists for it

For example (taken from the book):

```java
import java.text.*;

double d = 1234.567;

NumberFormat f1 = new DecimalFormat("###,###,###.0");
System.out.println(f1.format(d)); // 1,234.6 (note the rounding)

// Leading and trailing zeroes
NumberFormat f2 = new DecimalFormat("000,000,000.00000");
System.out.println(f2.format(d)); // 000,001,234.56700

// The next example may not work well with negative numbers
NumberFormat f3 = new DecimalFormat("Your balance $#,###,###.##");
System.out.println(f3.format(d)); // Your balance $1,234.57
```

#### Formatting dates and times

As said above, formatting (and parsing) dates and times is best done with the support for it in package `java.time.format`.
The formatters are immutable and thread-safe, like the objects formatted (dates and times). Recall that it is important
to know whether the object to format:
* has a date component, e.g. `LocalDate`, `LocalDateTime`, `ZonedDateTime`
* has a time component, e.g. `LocalTime`, `LocalDateTime`, `ZonedDateTime`
* has a timezone (as opposed to being a "local" date/time), e.g. `ZonedDateTime`

Also recall that `Instant` is the time passed since the epoch (1970-01-01T00:00:00Z), and can easily be converted to a
`ZonedDateTime` (with instance method `atZone(ZoneId)`).

Date and time formatting and parsing is done with a `java.time.format.DateTimeFormatter`. It is important to obtain an
instance that is appropriate for the date/time objects to format or parse. An instance can be obtained in several ways:
* Predefined formatters:
  * For example: `DateTimeFormatter.ISO_LOCAL_DATE`, for dates without an offset
  * For example: `DateTimeFormatter.ISO_LOCAL_TIME`, for times without an offset
  * For example: `DateTimeFormatter.ISO_LOCAL_DATE_TIME`, for date-times without an offset
  * For example: `DateTimeFormatter.ISO_ZONED_DATE_TIME`, for date-times with offset and zone
* Formatters obtained with a static factory method:
  * For example, `ofPattern(String)`, `ofPattern(String, Locale)`, `ofLocalizedDateTime(FormatStyle)`
* Using a `DateTimeFormatterBuilder`, which gives maximum control to create formatters

The 2 main *instance methods* of `DateTimeFormatter` are:
* `format(TemporalAccessor)`, returning a `String` and otherwise throwing a `DateTimeException` (where `TemporalAccessor` is a common supertype for all mentioned date/time classes)
* `parse(CharSequence)`, returning a `TemporalAccessor` (throwing an unchecked `DateTimeParseException`, a subtype of `DateTimeException`, if unsuccessful)

For convenience, the date/time classes such as `LocalDateTime`, `ZonedDateTime`, `LocalDate`, `LocalTime` have:
* instance method `format(DateTimeFormatter)`, retuning a `String`, and delegating the call to the `DateTimeFormatter`
* static method `parse(CharSequence, DateTimeFormatter)`, delegating the call to the `DateTimeFormatter`

If we use static method `DateTimeFormatter.ofPattern(String)` or `DateTimeFormatter.ofPattern(String, Locale)` it is
important to understand the date/time symbols we can use.

They are straightforward for years, months, days, hours, minutes and seconds, with the following additions:
* "M" is months, and "m" is minutes
* "s" is seconds, and "S" is fraction-of-second
* "a" is AM or PM
* "z" is time zone name, and "Z" is time zone offset (e.g. "-400")
* custom text we would like to add (like "at") should be enclosed in pairs of single quotes
  * a single quote itself as part of the custom text can be obtained with 2 single quotes next to each other
* also, repeating the same symbol influences the formatting
  * for example, "M" outputs the minimum number of characters for a month, like "1" for January
  * "MM" always outputs 2 digits for the month, like "01" for January
  * "MMM" prints three-letter abbreviations for the month, like "Jul" for July
  * "MMMM" prints the full month name, like "July"

### Supporting internationalization and localization

TODO
