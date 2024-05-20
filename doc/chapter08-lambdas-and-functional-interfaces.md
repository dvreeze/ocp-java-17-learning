# Chapter 8. Lambdas and Functional Interfaces

See [OCP Java SE 17 Developer Study Guide](https://www.amazon.com/Oracle-Certified-Professional-Developer-Study/dp/1119864585/ref=sr_1_1?crid=1GIZNHYFXHAK4&dib=eyJ2IjoiMSJ9.Mz5O0lUSaZhUZ-O1Mi__dRPfXHL9GM_CfZ3JDTz910a2d8XI7Vsfj7zwcywJAfMcubfCglH02m8PwlAk_DORk8SS5460zaDP1fskFDX4sUiFVR4pxE1Ln0VIY-g5awTQaOJKp4t0Y1HchXkrw0HtOeVSHg3dHG8Jql9TibGCj-WeXYyNdMp4zWtgM4EimHpl4wvlJZufvGpNjNEmXIObAd2B1mp1skt5k7v_B-k_Ip4.bRERgxl7gsekO5AihUKuOeT_yoO6Bsg7jHigb4sjHEM&dib_tag=se&keywords=ocp+java+se17&qid=1714573695&sprefix=ocp+java+%2Caps%2C192&sr=8-1).

This chapter is about Java interfaces, enums, records etc.

More information can be found in the (more low-level)
[Java AST API](https://docs.oracle.com/en/java/javase/17/docs/api/jdk.compiler/com/sun/source/tree/package-summary.html),
which contains links to the Java Language specification for specific language constructs.

In particular, see:
* [LambdaExpressionTree](https://docs.oracle.com/en/java/javase/17/docs/api/jdk.compiler/com/sun/source/tree/LambdaExpressionTree.html)

*Lambdas* were introduced in Java 8 to make Java more of a "functional programming language" than it was before Java 8.
With lambdas code becomes more *declarative* (the "what") instead of *manipulating object state* (the "how").

### Writing simple lambdas

Before talking about *lambdas* we should talk about *functional interfaces*, also known as *SAM interfaces* (i.e.
single abstract method interfaces). Roughly, functional interfaces are interfaces with *precisely one abstract (instance)
method*. If the single abstract method has no side effects, we could even think of functional interfaces as *functions
in mathematics*.

To instantiate functional interfaces, we could use *anonymous classes* (overriding the abstract method with a concrete
implementation), but that's inconvenient for the programmer (*too much boilerplate code to write*) and comes with a
*performance cost at runtime* (anonymous class creation, loading that class, instantiating the class etc.).

That's where *lambdas* come in. They provide *convenient syntax to instantiate a functional interface*, with dedicated
support at runtime to make them efficient in practice. The *context*, like the *expected functional interface type*,
fills in the missing parts in the lambda in order to *give the lambda a (functional interface) type* at compile-time,
within the specific context.

Syntactically, a lambda consists of a *lambda parameter list*, followed by an "->" *arrow*, followed by the lambda *body*.

There are 2 kinds of *lambda expressions*:
* *expression lambdas*, where the lambda body is an expression; e.g. `(String s) -> s.startsWith("A")`
* *statement lambdas*, where the lambda body is a statement; e.g. `(String s) -> { return s.startsWith("A"); }`

The *lambda parameter lists* can be given in one of 3 styles:
* all parameters are *typed*; e.g. `(String s, String t) -> s.startsWith(t)`
* all parameters are parameter names *without type*; e.g. `(s, t) -> s.startsWith(t)`
  * a special case is having just one parameter, where we can leave out the parentheses; e.g. `s -> s.isEmpty())`
* all parameters are *var parameters*; e.g. `(var s, var t) -> s.startsWith(t)` or `(final var s, final var t) -> s.startsWith(t)`

It is not allowed to mix these styles. For all parameters the same style is required (typed or without types or `var`).

Note that in most cases (especially if the parameters are given without type in the lambda) we indeed context to type the lambda as
functional interface type. The parameters must match the parameters of the single abstract method in the functional interface
(only the types must match, not the parameter names), in the correct order, and the lambda body's return type must match the
return type of the single abstract method. In other words, we must *match the lambda with the signature of the single
abstract method, and with its return type*.

### Coding functional interfaces

Like said earlier, a *functional interface* (or *SAM interface*) is an interface with *precisely one abstract (instance) method*,
whether declared in the interface or inherited from an ancestor interface.

Note, however, that the following methods do not count as abstract methods, regardless of whether they are mentioned in the interface:
* `java.lang.Object.equals(Object)`
* `java.lang.Object.hashCode()`
* `java.lang.Object.toString()`

After all, these `java.lang.Object` instance methods are implemented by all Java objects, whether the default
`java.lang.Object` implementation is used or not. Mind the exact method signatures when checking the abstract method count!

If an interface is intended to be a functional interface, it makes sense to use the `FunctionalInterface` annotation
to make that intention clear to the compiler (and readers of the code), leading to a compilation error if the interface
is not a functional interface.

### Using method references

Functional interfaces can always be instantiated with a lambda, but sometimes it is more convenient to replace the lambda
by even shorter syntax, namely a *method reference*. A method reference has format `classOrObject::methodName`.

There are 4 formats for *method references*:
* *static methods* as method references, in the format `Class::staticMethodName`
  * as lambda, the lambda parameters are the static method parameters
* *instance methods on a particular object*, as method references, in the format `someObject::instanceMethodName`
  * as lambda, the lambda parameters are the parameters passed to the instance method called on the particular object
* *instance methods on a parameter to be determined at runtime*, as method references, in the format `Class::instanceMethodName`
  * as lambda, the first lambda parameter is the instance itself, and the remaining lambda parameters are the parameters passed to the instance method
* *constructors* as method references, in the format `Class::new`
  * as lambda, the parameters are the constructor parameters

Let's give some examples:

```java
import java.nio.charset.StandardCharsets;
import java.util.function.*;

// Static method example
DoubleToLongFunction round = Math::round;
DoubleToLongFunction roundAsLambda = x -> Math.round(x);

var roundedValue = round.applyAsLong(3.14159); // 3L

// Instance method example for a particular object
final var str = "Sample string"; // final, so also effectively final

Predicate<String> strStartsWith = str::startsWith;
Predicate<String> strStartsWithAsLambda = s -> str.startsWith(s);

boolean strStartsWithCapitalS = strStartsWith.test("S"); // true
boolean strStartsWithCapitalT = strStartsWith.test("T"); // false

// Another instance method example for a particular object, but here the function has no parameters

Supplier<Boolean> strIsEmpty = str::isEmpty;
Supplier<Boolean> strIsEmptyAsLambda = () -> str.isEmpty();

boolean strIsEmptyValue = strIsEmpty.get(); // false

// Instance method on a parameter to be determined at runtime

Predicate<String> isEmpty = String::isEmpty;
Predicate<String> isEmptyAsLambda = s -> s.isEmpty();

boolean isEmptyValue = isEmpty.test(""); // true

BiPredicate<String, String> startsWith = String::startsWith;
BiPredicate<String, String> startsWithAsLambda = (s, t) -> s.startsWith(t);

boolean startsWithExample = startsWith.test("sample", "sam"); // true

// Constructor example

Function<byte[], String> createStringFromBytes = String::new;
Function<byte[], String> createStringFromBytesAsLambda = bytes -> new String(bytes);

String createdString = createStringFromBytes.apply("Abc".getBytes(StandardCharsets.UTF_8));
```

### Working with built-in functional interfaces

TODO

### Working with variables in lambdas

TODO
