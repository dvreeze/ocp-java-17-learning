# Chapter 7. Beyond Classes

See [OCP Java SE 17 Developer Study Guide](https://www.amazon.com/Oracle-Certified-Professional-Developer-Study/dp/1119864585/ref=sr_1_1?crid=1GIZNHYFXHAK4&dib=eyJ2IjoiMSJ9.Mz5O0lUSaZhUZ-O1Mi__dRPfXHL9GM_CfZ3JDTz910a2d8XI7Vsfj7zwcywJAfMcubfCglH02m8PwlAk_DORk8SS5460zaDP1fskFDX4sUiFVR4pxE1Ln0VIY-g5awTQaOJKp4t0Y1HchXkrw0HtOeVSHg3dHG8Jql9TibGCj-WeXYyNdMp4zWtgM4EimHpl4wvlJZufvGpNjNEmXIObAd2B1mp1skt5k7v_B-k_Ip4.bRERgxl7gsekO5AihUKuOeT_yoO6Bsg7jHigb4sjHEM&dib_tag=se&keywords=ocp+java+se17&qid=1714573695&sprefix=ocp+java+%2Caps%2C192&sr=8-1).

This chapter is about Java interfaces, enums, records etc.

More information can be found in the (more low-level)
[Java AST API](https://docs.oracle.com/en/java/javase/17/docs/api/jdk.compiler/com/sun/source/tree/package-summary.html),
which contains links to the Java Language specification for specific language constructs.

In particular, see:
* [ClassTree](https://docs.oracle.com/en/java/javase/17/docs/api/jdk.compiler/com/sun/source/tree/ClassTree.html), in this case for *interfaces*, *enums*, *records*, etc.

In chapter 5, we learned some basic rules about *access modifiers* and *static members*, and these basic rules also hold
for interfaces, enums, records etc. Some additional rules may exist for these specific kinds of types.

### Implementing interfaces

An *interface* is like a *public API* for classes implementing that interface:
* That is, interfaces typically consist of *public abstract (instance) methods* (that implementing classes must provide)
* Yet interfaces have *no constructors* (or instance/static initializers), and know nothing about instance/class initialization order
* So interfaces cannot be instantiated, and are therefore by definition `abstract`
* Classes do not extend interfaces, but *implement* interfaces instead (using keyword `implements`)
* Classes may implement *multiple interfaces* (comma-separated) at the same time
* Interfaces may *extend* other interfaces (multiple ones at the same time, comma-separated)
* Not surprisingly, interfaces can only extend other interfaces, and *cannot inherit from classes*
* Compared to abstract classes, interfaces trade constructors for *multiple interface inheritance*

Consider the following interfaces:

```java
public interface Expression {
}

public interface BinaryExpression {
    String getOperator();
    Expression getLhs();
    Expression getRhs();
    
    int MAX_DEPTH = 100;
}
```

The compiler then generates several *implicit modifiers*:
* An interface is always *abstract*, so the compiler adds implicit modifier `abstract` to the interface
* The compiler adds implicit modifier `abstract` to all (non-private) instance methods without body
* The compiler adds implicit modifier `public` to all (abstract/default/static) interface methods that are not declared to be private; interfaces can have the following methods:
  * methods *without body* (they are *abstract* public instance methods)
  * `default` (instance) methods (implicitly public), with body
  * non-private *static* methods (implicitly public), with body
  * private instance methods (marked as `private`, but not as `static`), with body (no implicit modifiers are generated)
  * private static methods (marked as `private static`), with body (no implicit modifiers are generated)
* The compiler adds implicit modifiers `public static final` to all ("constant") fields

So *methods in interfaces* are:
* either explicitly `private` instance or static methods, with body
* or: non-private explicitly `static` methods, with body
* or: implicitly public *instance methods*:
  * either an *instance method without body*, and therefore an *abstract method* (typically forming the bulk of interface methods)
  * or: a *default method*, with body

This leads to the following (more verbose but also semantically more clear) result code for the interfaces above:

```java
public abstract interface Expression {
}

public abstract interface BinaryExpression {
    public abstract String getOperator();
    public abstract Expression getLhs();
    public abstract Expression getRhs();

    public static final int MAX_DEPTH = 100;
}
```

*Overriding* (abstract) interface methods works the same way as overriding abstract methods in abstract classes, with the
same rules concerning proper overriding (e.g. same method signature, and Liskov substitution principle not being violated).

Like abstract classes, interfaces cannot be made `final`. After all, the combination `abstract final` is rejected by the compiler.

Due to multiple interface inheritance, a class may inherit 2 abstract methods that have *compatible method declarations*.
The latter means that a method can be written that properly *overrides both methods*.

If explicitly provided modifiers for methods or fields conflict with *implicit modifiers*, typically a compilation error
is raised.

*Default* interface methods help evolve interfaces without breaking classes implementing the interface.
Default (instance) methods are overridable by a class implementing the interface. Some *default method* rules are:
* A `default` method may only be declared in an *interface*, and not in a class
* A `default` method (i.e. a method marked with the `default` modifier) must have a *body*
* A `default` method is implicitly `public`
* A `default` method *cannot be marked* `abstract`, `final` or `static`
* A `default` method may be overridden by a class implementing the interface
* If a class inherits 2 or more `default` methods with the *same method signature*, it must *override the method*

Note that non-private non-static non-abstract interface methods are required to have an explicit `default` marker.

To access a *hidden default* method `m` in interface supertype `A`, use syntax `A.super.m()` (not `A.this.m()` or `A.m()`).

*Static interface methods* have the following characteristics (that make sense from what we have learned so far):
* They are explicitly marked `static` and must contain a method body
* A static interface method without access specifier is implicitly `public`
* A static interface method *cannot be marked* `abstract` or `final`
* A static interface method is not inherited, and cannot be accessed in a class implementing the interface without a reference to the interface name

Conceptually:
* Treat *instance methods* of an interface (that is, abstract, default and non-static private methods) as belonging to an *instance of the interface*
* Treat *static* methods and fields as belonging to the *interface class object*
* All *private* interface methods are only *accessible within the interface declaration*
