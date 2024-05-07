# Learning for OCP Java SE 17

### Returning from Scala to Java

After many years of predominantly programming in Scala, I have returned to Java, finding a renewed
passion for it. In particular recent versions of Java (starting with Java 17) are powerful expressive
versions of the language.

Much of what I have programmed in Scala in the past, I could now have done in Java, using modern features
(like record classes) along with the standard library and [Guava](https://guava.dev/) goodies like immutable collections.

To really get to know Java 17 well (as language and standard library), and prove proficiency in it, I'm 
spending time to prepare for OCP Java SE 17 certification.

For that I use the
[OCP Java SE 17 Developer Study Guide](https://www.amazon.com/Oracle-Certified-Professional-Developer-Study/dp/1119864585/ref=sr_1_1?crid=1GIZNHYFXHAK4&dib=eyJ2IjoiMSJ9.Mz5O0lUSaZhUZ-O1Mi__dRPfXHL9GM_CfZ3JDTz910a2d8XI7Vsfj7zwcywJAfMcubfCglH02m8PwlAk_DORk8SS5460zaDP1fskFDX4sUiFVR4pxE1Ln0VIY-g5awTQaOJKp4t0Y1HchXkrw0HtOeVSHg3dHG8Jql9TibGCj-WeXYyNdMp4zWtgM4EimHpl4wvlJZufvGpNjNEmXIObAd2B1mp1skt5k7v_B-k_Ip4.bRERgxl7gsekO5AihUKuOeT_yoO6Bsg7jHigb4sjHEM&dib_tag=se&keywords=ocp+java+se17&qid=1714573695&sprefix=ocp+java+%2Caps%2C192&sr=8-1).

This GitHub repository is used for summaries of topics, additional notes and (own) exercises.

### Different philosophies of Scala and Java

Scala has some attractive traits that distinguish it from Java:
* A very powerful *type system*, and *compile-time type-safety* (Java is also compile-time type-safe, but its type system is less powerful). "If something is not in the type system, it does not exist."
* A philosophy of *unification*, for example between OO and FP. Scala, at its core, tries to offer a *minimal set of orthogonal concepts*.
* *Term derivation*, better known as *implicits*
* As such, Scala is not married to the JVM (to the extent that Java is).

Java is much more rooted in its target platform, the JVM. Unlike Scala, Java makes a clear distinction between:
* *Primitive types* and *reference types* (i.e. "object types"), where *generic* types such as collections can only abstract over reference types
* *Null references* and references to objects (Scala makes that distinction too, but tries to steer the developer away from null)
* *Static members* and *instance members* (Scala has so-called *objects*, that can be abstracted over)
* *Fields* and *methods* (Scala supports the "uniform access principle")
* *Implementing* interfaces and *extending* classes
* *Arrays* and *collections* (the latter not built into the language), with different access syntax (whereas Scala tries to make them "look the same")
* *Methods* and *operators*
* *Statements* and *expressions*, the latter producing a value (and the use of the *return* keyword in method bodies returning a value)

This more tight coupling of the Java language to the JVM target platform is also visible when looking at the
[JVM instruction set](https://docs.oracle.com/javase/specs/jvms/se7/html/jvms-6.html).

To an extent, Java can help in getting a mental picture of how certain Scala constructs are implemented "under the hood".
I think Scala and Java knowledge strengthen each other.

That's not to say that there would be less value in OCP certification. I see value in all of the following:
* OCP certification to really grasp Java 17 and many of its corner cases, even if we would not go there ourselves
* My (previous) habit of internalising practices from the book Effective Java, in order to use Java idiomatically
* My experience with Scala and FP practices, influencing the way I (prefer to) write Java

These bullet points correspond to smaller and smaller "subsets of Java" to use in practice:
* The OCP certification to a large extent is about programs we should not write (whether valid or not from the compiler's POV)
* The book Effective Java helps limit the use of Java to "idiomatic Java"
* Influences from Scala and FP even further helps limit the use of Java to "less mutability" and other FP best practices

### Meta-subjects

The OCP study guide mentioned earlier gives a good overview over the topics that are relevant for OCP certification.
It may make sense to also try to come up with a list of "meta-topics", in order to get an overall idea of what "kinds of
things" to think about when answering exam questions.

My current attempt at a list of "meta-subjects" looks like this:
* Java *syntax details* (recognising fields/methods/constructors etc.; what syntax corner cases are allowed or disallowed, and why)
* The Java *type system* (compile-time type-safety, type conversions etc.)
* A *mental picture of the JVM* (primitive data vs. objects, call-by-value where the value is primitive or a reference, "javap" output, etc.)
* *Runtime behaviour* (object construction order, overriding/overloading, inheritance/polymorphism, exceptions, try-finally, threading, etc.)
* *Standard library APIs* (the ones we must really know very well by heart)

Many exam questions typically require knowledge spanning multiple such "meta-topics", trying to trick you into overlooking
things.

It is also easy to overlook "small details" when not reading carefully, such as:
* Uppercase versus lowercase (such as "amount" versus "Amount", keeping in mind that identifiers are case-sensitive)
* Commas versus semicolons (which typically affect where statements end)
* Single equals sign ("=") for assignment versus double equals sign ("==") for (primitive/reference) equality test
* Methods looking like constructors, if we overlook the method return type

### Writing and running Java code

The code in this repository should be written, compiled and run without the help of an IDE.
Note that the code here has no dependencies outside the JDK standard library, so that makes compilation/running easy.

For example (without passing any program arguments):

```shell
javac -d ./classes src/chapter01/CallByValueExample.java

java -cp ./classes chapter01.CallByValueExample
```

The program execution step could be written in 3 different but equivalent ways (analogous remarks apply to compilation through "javac"):

```shell
java -cp ./classes chapter01.CallByValueExample
java -classpath ./classes chapter01.CallByValueExample
java --class-path ./classes chapter01.CallByValueExample
```

For a single-file source-code program we can skip the compilation step, and simply run the program like this:

```shell
java src/chapter01/CallByValueExample.java
```

### Resources

More information about Java 17 language constructs can be found in the (more low-level)
[Java AST API](https://docs.oracle.com/en/java/javase/17/docs/api/jdk.compiler/com/sun/source/tree/package-summary.html).

For example, statements (and different kinds of statements) are modelled in
[StatementTree](https://docs.oracle.com/en/java/javase/17/docs/api/jdk.compiler/com/sun/source/tree/StatementTree.html),
and expressions (and different kinds of expressions) are modelled in
[ExpressionTree](https://docs.oracle.com/en/java/javase/17/docs/api/jdk.compiler/com/sun/source/tree/ExpressionTree.html).

This gives a good idea of how Java "hangs together" conceptually as AST, but it does not show the exact syntax with
the "terminal symbols".

For example, [WhileLoopTree](https://docs.oracle.com/en/java/javase/17/docs/api/jdk.compiler/com/sun/source/tree/WhileLoopTree.html)
exposes methods to get the loop condition as expression, and to get the loop body as statement, but it does not show the exact
syntax (except for a non-normative example in the Javadoc documentation of the class).

The AST classes seem to result from an early compiler phase in which language constructs are recognized, but where type checking
has not yet taken place. Still, keeping this in mind, this Java AST API aids in understanding Java.

Moreover, from these AST classes we find links to the *Java Language specification* for specific language constructs.
The JLS is very detailed, obviously. It is meant for implementers, not for regular Java programmers. The grammars in
the JLS are also very detailed, to a large part for disambiguation. So the grammar in the JLS is too complex as an aid
to understand the syntax of Java. Still, the JLS provides a lot of useful information, even to users of the Java language,
so browsing it for specific language constructs often makes sense, also for coming up with a simplified grammar for
specific language constructs. We need to have a good grasp of the precise syntax when answering exam questions, after all,
to catch small syntax errors.

