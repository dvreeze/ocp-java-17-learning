# Chapter 4. Core APIs

See [OCP Java SE 17 Developer Study Guide](https://www.amazon.com/Oracle-Certified-Professional-Developer-Study/dp/1119864585/ref=sr_1_1?crid=1GIZNHYFXHAK4&dib=eyJ2IjoiMSJ9.Mz5O0lUSaZhUZ-O1Mi__dRPfXHL9GM_CfZ3JDTz910a2d8XI7Vsfj7zwcywJAfMcubfCglH02m8PwlAk_DORk8SS5460zaDP1fskFDX4sUiFVR4pxE1Ln0VIY-g5awTQaOJKp4t0Y1HchXkrw0HtOeVSHg3dHG8Jql9TibGCj-WeXYyNdMp4zWtgM4EimHpl4wvlJZufvGpNjNEmXIObAd2B1mp1skt5k7v_B-k_Ip4.bRERgxl7gsekO5AihUKuOeT_yoO6Bsg7jHigb4sjHEM&dib_tag=se&keywords=ocp+java+se17&qid=1714573695&sprefix=ocp+java+%2Caps%2C192&sr=8-1).

This chapter is about some of the most fundamental APIs in the Java standard library.

### Strings

See [java.lang.String](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/String.html)
and [java.lang.StringBuilder](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/StringBuilder.html).

The Java `String` class is a *reference type*, and it can be instantiated with *string literals* or *text blocks*, without
using any String class constructor.

Type `String` is *immutable*, that is, all operations that appear to change a String object in reality return a new String.
Class `String` is also *thread-safe*.

*String concatenation* uses the same "+" operator as addition of numbers. String concatenation appends the RHS String to
the LHS String. When mixing numbers and Strings in operations using "+", remember the following rules:
1. If both operands are numeric, "+" means addition of numbers
2. If either operand is a String, "+" means concatenation
3. The expression is evaluated from left to right

So `1 + 2 + "4"` results in `"34"`. Note that `"a" + null` results in `"anull"`.

Indexing in Strings, like is the case for arrays and collections, is *zero-based*. When passing a start and end index
to a method (like `substring`), the start index is *inclusive* and the end index is *exclusive*.

Some important *instance methods of class String* (or sets of overloaded String instance methods) are:
* `length`
* `charAt` (may throw a `StringIndexOutOfBoundsException`)
* `indexOf` (may return `-1` instead of throwing an exception)
* `substring`
* `toLowerCase`, `toUpperCase`
* `equals` (from the `Object` super-class), `equalsIgnoreCase`
* `startsWith`, `endsWith`, `contains`
* `replace` (taking 2 characters, or 2 strings), as opposed to `replaceAll` (taking a regular expression as first parameter)
* `strip`, `stripLeading`, `stripTrailing`, `trim` (method "trim" is older, using a less advanced definition of whitespace)
* `indent`, `stripIndent` (these methods are powerful, but mind line break normalization and trailing line break), and `lines`
* `translateEscapes` (since Java 15)
* `isEmpty`, `isBlank`
* `formatted` (since Java 15; see static method `format`)

Note that the String API leans itself for *method chaining*. Given that class `String` is *immutable*, this means the
creation of multiple intermediate String objects (at least in theory).

In general, be aware of how to override `equals` and `hashCode`, and that `a.equals(b)` must imply `a.hashCode() == b.hashCode()`.
Also note that `equals` must be an *equivalence relation* (reflexive, symmetric, transitive) and *consistent*.
This is already the case for (final) class `String`. As an aside, this is also supported automatically for (immutable)
*record classes*, with `equals` being *value equality* rather than *reference equality* ("==").

An important *static method of class String* is `format`. Commonly used *formatting symbols* in this method are
"%s" (for any type), "%d" (for integer values), "%f" (for floating-point values) and "%n" (line break).
For example, we can format floating-point values with "%12.8f" (possibly leading to some leading spaces) or "%012f" (possibly leading to some leading zeroes).

When creating a String in many steps, consider using a `StringBuilder` to create the String. The same `StringBuilder` object is used
for creating one String, thus (potentially) getting rid of many intermediate immutable String objects that were only used for creating
the result String. Note that unlike `String`, `StringBuilder` is mutable and not thread-safe.

`StringBuilder` methods like `append` can be chained, but the effect is quite different than method chaining for `String`.
After all, `String` is immutable, so method chaining creates new Strings all the time, whereas for `StringBuilder` it is
one and the same object that is being updated in-place all the time. So for String and StringBuilder we need a completely
*different mental model* to reason about them ("immutable versus mutable").

`StringBuilder` and `String` have several methods in common, partly from common implemented interface `CharSequence`.
Some commonly shared methods are:
* `indexOf`
* `substring`
* `length` (from `CharSequence`)
* `charAt` (from `CharSequence`)

Other important *instance methods of class StringBuilder* are:
* `append` (many overloads)
* `insert` (many overloads)
* `delete`, `deleteCharAt`
* `replace` (quite different from its String counterpart)
* `reverse`
* `toString` (from Object, but in this case building the result String from the StringBuilder)

Back to `String`. We know that class `String` uses value equality (or logical equality) for string comparisons using
method `equals`. But what about "object equality" (with "==")? We need to know that Java stores strings that are *compile-time
constants* into a *string pool*. See the following example, taken from the book:

```java
var name = "Hello World";
var name2 = new String("Hello World").intern();
System.out.println(name == name2); // true
```

Of course, we should not use *object equality* for Strings or method `intern` in our code.

### Arrays

TODO
