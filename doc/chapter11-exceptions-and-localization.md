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
