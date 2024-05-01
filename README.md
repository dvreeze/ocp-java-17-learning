# Learning for OCP Java SE 17

### Returning from Scala to Java

After many years of predominantly programming in Scala, I have returned to Java, finding a renewed
passion for it. In particular recent versions of Java (starting with Java 17) are powerful expressive
versions of the language.

Much of what I have programmed in Scala in the past, I could now have done in Java, using modern features
(like record classes) along with the standard library and Guava goodies like immutable collections.

To really get to know Java 17 well (as language and standard library), and prove proficiency in it, I'm 
spending time to prepare for OCP Java SE 17 certification.

For that I use the
[OCP Java SE 17 Developer Study Guide](https://www.amazon.com/Oracle-Certified-Professional-Developer-Study/dp/1119864585/ref=sr_1_1?crid=1GIZNHYFXHAK4&dib=eyJ2IjoiMSJ9.Mz5O0lUSaZhUZ-O1Mi__dRPfXHL9GM_CfZ3JDTz910a2d8XI7Vsfj7zwcywJAfMcubfCglH02m8PwlAk_DORk8SS5460zaDP1fskFDX4sUiFVR4pxE1Ln0VIY-g5awTQaOJKp4t0Y1HchXkrw0HtOeVSHg3dHG8Jql9TibGCj-WeXYyNdMp4zWtgM4EimHpl4wvlJZufvGpNjNEmXIObAd2B1mp1skt5k7v_B-k_Ip4.bRERgxl7gsekO5AihUKuOeT_yoO6Bsg7jHigb4sjHEM&dib_tag=se&keywords=ocp+java+se17&qid=1714573695&sprefix=ocp+java+%2Caps%2C192&sr=8-1).

This GitHub repository is used for additional notes and (own) exercises.

### Different philosophies of Scala and Java

Scala has some attractive traits that distinguish it from Java:
* A very powerful *type system*, and *compile-time type-safety*. "If something is not in the type system, it does not exist."
* A philosophy of *unification*, for example between OO and FP. Scala, at its core, tries to offer a *minimal set of orthogonal concepts*.
* As such, Scala is not married to the JVM (to the extent that Java is).

Java is much more rooted in its target platform, the JVM. Unlike Scala, Java makes a clear distinction between:
* *Primitive types* and *reference types* (i.e. "object types")
* *Null references* and references to objects (Scala makes that distinction too, but tries to steer the developer away from null)
* *Static members* and *instance members* (Scala has so-called objects, that can be abstracted over)
* *Fields* and *methods* (Scala supports the "uniform access principle")
* *Implementing* interfaces and *extending* classes
* *Arrays* and *collections* (the latter not built into the language), with different access syntax
* *Methods* and *operators*
* *Statements* and *expressions*, the latter producing a value

To an extent, Java can help in getting a mental picture of how certain Scala constructs are implemented "under the hood".
I think Scala and Java knowledge strengthen each other.

That's not to say that there would be less value in OCP certification. I see value in all of the following:
* OCP certification to really grasp Java 17 and many of its corner cases, even if we would not go there ourselves
* My (previous) habit of internalising practices from the book Effective Java, in order to use Java idiomatically
* My experience with Scala and FP practices, influencing the way I write Java

These bullet points correspond to smaller and smaller "subsets of Java" to use in practice:
* The OCP certification to a large extent is about programs we should not write (whether valid or not from the compiler's POV)
* The book Effective Java helps limit the use of Java to "idiomatic Java"
* Influences from Scala and FP even further helps limit the use of Java to "less mutability" and other FP best practices

### Meta-subjects

The OCP study guide mentioned earlier gives a good overview over the topics that are relevant for OCP certification.
It may make sense to also try to come up with a list of "meta-topics", in order to get an overall idea of what "kinds of
things" to think about when answering exam questions.

My current attempt at a list of "meta-subjects" looks like this:
* Java *syntax details* (what syntax corner cases are allowed or disallowed, and why)
* The Java *type system* (compile-time type-safety, type conversions etc.)
* A *mental picture of the JVM* (primitive data vs. objects, call-by-value where the value is primitive or a reference, etc.)
* *Runtime behaviour* (object construction order, overriding/overloading, inheritance/polymorphism, exceptions, try-finally, threading, etc.)
* *Standard library APIs* (the ones we must really know very well by heart)

Some exam questions may require knowledge spanning multiple such "meta-topics".

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

