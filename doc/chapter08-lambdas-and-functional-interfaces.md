# Chapter 8. Lambdas and Functional Interfaces

See [OCP Java SE 17 Developer Study Guide](https://www.amazon.com/Oracle-Certified-Professional-Developer-Study/dp/1119864585/ref=sr_1_1?crid=1GIZNHYFXHAK4&dib=eyJ2IjoiMSJ9.Mz5O0lUSaZhUZ-O1Mi__dRPfXHL9GM_CfZ3JDTz910a2d8XI7Vsfj7zwcywJAfMcubfCglH02m8PwlAk_DORk8SS5460zaDP1fskFDX4sUiFVR4pxE1Ln0VIY-g5awTQaOJKp4t0Y1HchXkrw0HtOeVSHg3dHG8Jql9TibGCj-WeXYyNdMp4zWtgM4EimHpl4wvlJZufvGpNjNEmXIObAd2B1mp1skt5k7v_B-k_Ip4.bRERgxl7gsekO5AihUKuOeT_yoO6Bsg7jHigb4sjHEM&dib_tag=se&keywords=ocp+java+se17&qid=1714573695&sprefix=ocp+java+%2Caps%2C192&sr=8-1).

This chapter is about *functional interfaces*, *lambda expressions* and *method references*.

More information can be found in the (more low-level)
[Java AST API](https://docs.oracle.com/en/java/javase/17/docs/api/jdk.compiler/com/sun/source/tree/package-summary.html),
which contains links to the Java Language specification for specific language constructs.

In particular, see:
* [LambdaExpressionTree](https://docs.oracle.com/en/java/javase/17/docs/api/jdk.compiler/com/sun/source/tree/LambdaExpressionTree.html)
* [MemberReferenceTree](https://docs.oracle.com/en/java/javase/17/docs/api/jdk.compiler/com/sun/source/tree/MemberReferenceTree.html)

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

After all, these `java.lang.Object` instance methods are implemented by all Java objects, whether overridden or not. Mind
the exact method signatures when checking the abstract method count!

If an interface is intended to be a functional interface, it makes sense to use the `java.lang.FunctionalInterface` annotation
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

BooleanSupplier strIsEmpty = str::isEmpty;
BooleanSupplier strIsEmptyAsLambda = () -> str.isEmpty();

boolean strIsEmptyValue = strIsEmpty.getAsBoolean(); // false

// Instance method on a parameter to be determined at runtime

Predicate<String> isEmpty = String::isEmpty;
Predicate<String> isEmptyAsLambda = s -> s.isEmpty();

boolean isEmptyValue = isEmpty.test(""); // true

BiPredicate<String, String> startsWith = String::startsWith;
BiPredicate<String, String> startsWithAsLambda = (s, t) -> s.startsWith(t);

boolean startsWithExample = startsWith.test("sample", "sam"); // true

// Constructor example

Function<byte[], String> createStringFromBytes = String::new;
Function<byte[], String> createStringFromBytesAsLambda = bytes -> new String(bytes, StandardCharsets.UTF_8);

String createdString = createStringFromBytes.apply("Abc".getBytes(StandardCharsets.UTF_8));
```

### Working with built-in functional interfaces

The most fundamental built-in functional interface in package `java.util.function` is `Function<T, R>`,
which is the *functional interface* type for functions from `T` to `R`. The *single abstract method* signature is `apply(T)`.

Let's "rediscover" some other widely used built-in functional interfaces:

```java
import java.util.function.Function;

// Predicates
Function<String, Boolean> isEmpty = s -> Boolean.valueOf(s.isEmpty());

Boolean emptyStringIsEmpty = isEmpty.apply("");

// Predicates are so common-place that it makes sense to have a dedicated Predicate functional interface type.
// That type would return boolean primitives, thus preventing the need for lots of boxing and unboxing of booleans.

// Suppliers
Function<Void, String> createHelloString = ignoredValue -> "Hello";

String hello = createHelloString.apply(null);

// Not very convenient, of course. Why is that unused parameter even needed? So we need a Supplier functional interface.

// Consumers
Function<String, Void> printString = s -> { System.out.println(s); return null; };

Void ignoredReturnValue = printString.apply("Hello");

// That is even less convenient. Why is the return value even needed? So we need a Consumer functional interface.
```

This leads to the following "core" built-in functional interfaces:
* `Function<T, R>`, with abstract method signature `apply(T)` and return type `R`
* `Predicate<T>`, with abstract method signature `test(T)` and (primitive) return type `boolean`
* `Supplier<T>`, with abstract method signature `get()` and return type `T`
* `Consumer<T>`, with abstract method signature `accept(T)` and return type `void`

The preceding example then becomes a lot more attractive:

```java
import java.util.function.*;

Predicate<String> isEmpty = String::isEmpty;
boolean emptyStringIsEmpty = isEmpty.test(""); // true

Supplier<String> createHelloString = () -> "Hello";
String hello = createHelloString.get();

Consumer<String> printString = System.out::println;
printString.accept("Hello");
```

Let's add some functional interfaces that take 2 parameters instead of 1 parameter, and some `Function` subtypes where
parameter types and return types are the same. This list must be remembered, but is (now) relatively easy to remember:
* `Function<T, R>`, with abstract method signature `apply(T)` and return type `R`
  * For 2 parameters: `BiFunction<T, U, R>`
  * Subtype `UnaryOperator<T>` of `Function<T, T>`
  * Subtype `BinaryOperator<T>` of `BiFunction<T, T, T>`
* `Predicate<T>`, with abstract method signature `test(T)` and (primitive) return type `boolean`
  * For 2 parameters: `BiPredicate<T, U>`
* `Supplier<T>`, with abstract method signature `get()` and return type `T`
* `Consumer<T>`, with abstract method signature `accept(T)` and return type `void`
  * For 2 parameters: `BiConsumer<T, U>`

Many built-in functional interfaces have some default instance methods for convenience, to "compose" functional interfaces.
For example:
* `Function<T, R>` has "method chaining" *higher order functions* `andThen` ("first apply this function, then apply the parameter function") and `compose` ("apply this function after the parameter function")
* `Predicate<T>` has functions to combine independent predicates, such as (short-circuiting) `and`, (short-circuiting) `or` and `negate`
* `Consumer<T>` has function `andThen`

For *primitives* (`double`, `int`, `long`, and in one case `boolean`) there are dedicated variants of `Function`,
`Predicate`, `Supplier` and `Consumer`. Below some of them are given, and the rest can be guessed rather easily:
* Functions:
  * `DoubleFunction<R>` with SAM signature `apply(double)` and return type `R`; analogous functional interfaces for `int` and `long`
  * `DoubleUnaryOperator` with SAM signature `applyAsDouble(double)` and return type `double`; analogous functional interfaces for `int` and `long`
  * `DoubleBinaryOperator` with SAM signature `applyAsDouble(double, double)`; analogous functional interfaces for `int` and `long`
  * `ToDoubleFunction<T>` with SAM signature `applyAsDouble(T)` and return type `double`; analogous functional interfaces for `int` and `long`
  * `ToDoubleBiFunction<T, U>` returning a `double`; analogous functional interfaces for `int` and `long`
  * Conversion functions like `DoubleToLongFunction` with SAM signature `applyAsLong(double)` and return type `long`; similar conversion functions converting between `double`, `long` and `int`
* Predicates:
  * `DoublePredicate` with SAM signature `test(double)` and return type `boolean`; analogous functional interfaces for `int` and `long`
* Suppliers:
  * `DoubleSupplier` with SAM signature `getAsDouble()` and return type `double`; analogous functional interfaces for `int` and `long`
  * `BooleanSupplier` with SAM signature `getAsBoolean()` and return type `boolean`
* Consumers:
  * `DoubleConsumer` with SAM signature `accept(double)` and return type `void`; analogous functional interfaces for `int` and `long`
  * `ObjDoubleConsumer<T>` with SAM signature `accept(T, double)` and return type `void`; analogous functional interfaces for `int` and `long`

### Working with variables in lambdas

From a *lambda body* inside a method we can access:
* instance fields (if the method is not static) and static fields
* lambda parameters
* local variables and method parameters, provided they are *effectively final*

Note that *local variables cannot shadow each other* in Java. So redefining a variable (e.g. as lambda parameter) where
a variable with the same name is already in scope is disallowed by the Java compiler.

### Check lists

How to quickly but thoroughly check that a *lambda* is *syntactically correct*:
1. The lambda must be a *parameter list*, followed by a "->" *arrow*, followed by a *lambda body*
2. The parameter list must have *surrounding parentheses* (unless it is just one parameter name, without type or `var`), and use *consistent style* (parameters all typed, or all `var` or all without type)
3. The *body* must either be an *expression* (without trailing semicolon) or a *block* (mind semicolons within the block, and `return` statements)
4. No 2 *local variables* with the *same name* can be in-scope at the same time (that includes lambda parameters, method parameters etc.)
5. *Local variables* from outside the lambda must be *effectively final* if they are used within the lambda

Checking built-in *functional interface* types:
* Mind the correct names: it is `BiFunction` but `BinaryOperator`, for example
* Mind the *type parameters* (in particular also the number of type parameters)

Checking *functional interface* declarations:
* Only an *interface* can be a functional interface; classes cannot be functional interfaces
* A functional interface must be a *valid interface* (so no `protected` modifier, no `default` method without body etc.)
* Adding abstract methods through inheritance from a functional interface sneakily violates the *SAM rule*
* `java.lang.Object` instance methods do not count for the SAM rule (but mind the exact method signatures and return types)
