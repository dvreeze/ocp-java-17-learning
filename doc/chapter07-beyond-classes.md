# Chapter 7. Beyond Classes

See [OCP Java SE 17 Developer Study Guide](https://www.amazon.com/Oracle-Certified-Professional-Developer-Study/dp/1119864585/ref=sr_1_1?crid=1GIZNHYFXHAK4&dib=eyJ2IjoiMSJ9.Mz5O0lUSaZhUZ-O1Mi__dRPfXHL9GM_CfZ3JDTz910a2d8XI7Vsfj7zwcywJAfMcubfCglH02m8PwlAk_DORk8SS5460zaDP1fskFDX4sUiFVR4pxE1Ln0VIY-g5awTQaOJKp4t0Y1HchXkrw0HtOeVSHg3dHG8Jql9TibGCj-WeXYyNdMp4zWtgM4EimHpl4wvlJZufvGpNjNEmXIObAd2B1mp1skt5k7v_B-k_Ip4.bRERgxl7gsekO5AihUKuOeT_yoO6Bsg7jHigb4sjHEM&dib_tag=se&keywords=ocp+java+se17&qid=1714573695&sprefix=ocp+java+%2Caps%2C192&sr=8-1).

This chapter is about Java interfaces, enums, records etc.

More information can be found in the (more low-level)
[Java AST API](https://docs.oracle.com/en/java/javase/17/docs/api/jdk.compiler/com/sun/source/tree/package-summary.html),
which contains links to the Java Language specification for specific language constructs.

In particular, see:
* [ClassTree](https://docs.oracle.com/en/java/javase/17/docs/api/jdk.compiler/com/sun/source/tree/ClassTree.html), in this case for *interfaces*, *enums*, *records*, etc.

In chapter 5, we learned some basic rules about *access modifiers* and *static members*, and these basic rules also hold
for interfaces, enums, records etc. The same is true for what we learned in chapter 6 about *overriding* etc. Some additional
rules exist for these specific kinds of types.

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

public interface BinaryExpression extends Expression {
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

Put differently, *methods in interfaces* are:
* either explicitly `private` or (explicitly or implicitly) `public`; interface members cannot be "protected" or "package-private"
* either explicitly `static` methods or otherwise *instance methods*
* there are exactly 2 flavours of *non-private instance methods* in an interface:
  * *instance methods without body*, which obviously are *abstract* (explicitly or implicitly) as well as `public` (explicitly or implicitly)
  * *default methods with body* (the `default` keyword is mandatory, but modifier `public` may be inferred)

This leads to the following (more verbose but also semantically more clear) result code for the interfaces above:

```java
public abstract interface Expression {
}

public abstract interface BinaryExpression extends Expression {
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

### Working with enums

An *enumeration* or *enum* in short is like a *fixed set of constants*. For example, the seasons of the year.
Enumerations offer compile-time type-safety for such sets of constants.

Enum values are not created by us, but by the compiler. In other words, *enum constructors* are *private*, either implicitly
or explicitly (if we explicitly provide a constructor and explicitly mark it as `private`).

Enum types are final and can therefore *not be extended*, thus preventing the introduction of more enum values.

Despite the dedicated *syntax for enums*, under the hood enums are *classes extending abstract class java.lang.Enum* (which
has a type parameter for the actual enum subtype itself).

Consider the following *simple enum*:

```java
public enum Season {

    WINTER, SPRING, SUMMER, FALL; // The semicolon is optional, but that is the case only for simple enums
}
```

Let's look with *javap* which methods such a simple enum contains:

```shell
javap -cp ./classes -p chapter07.Season
```

```
public final class chapter07.Season extends java.lang.Enum<chapter07.Season> {
  public static final chapter07.Season WINTER;
  public static final chapter07.Season SPRING;
  public static final chapter07.Season SUMMER;
  public static final chapter07.Season FALL;
  private static final chapter07.Season[] $VALUES;
  public static chapter07.Season[] values();
  public static chapter07.Season valueOf(java.lang.String);
  private chapter07.Season();
  static {};
}
```

The `Enum` superclass itself:

```shell
javap -protected java.lang.Enum
```

```
public abstract class java.lang.Enum<E extends java.lang.Enum<E>> implements java.lang.Comparable<E>, java.io.Serializable {
  public final java.lang.String name();
  public final int ordinal();
  protected java.lang.Enum(java.lang.String, int);
  public java.lang.String toString();
  public final boolean equals(java.lang.Object);
  public final int hashCode();
  protected final java.lang.Object clone() throws java.lang.CloneNotSupportedException;
  public final int compareTo(E);
  public final java.lang.Class<E> getDeclaringClass();
  public static <T extends java.lang.Enum<T>> T valueOf(java.lang.Class<T>, java.lang.String);
  protected final void finalize();
  public int compareTo(java.lang.Object);
}
```

Let's have a look at this *javap output* (for this simple enum):
* The *enum type* is a `final` class, so it cannot be extended
* The *enum values* are indeed `public static final` constants
* The *constructor* is `private`; each enum value is automatically created *only once*
* An array of all the values is returned by static method `values`
* Static method `valueOf` returns the enum value whose name exactly matches the passed String (and throws an exception otherwise)
* Enums have instance ("getter") methods `name` and `ordinal`
* Enums have overridden methods `equals` and `hashCode`; even reference comparison suffices to compare enum values for equality
* Enums have overridden method `toString`, printing the name of the value

Note that `Season.SUMMER == 2` does not compile.

When using enums in `switch` expressions and statements, be careful not to prefix the enum values to match on with the
enum class name, or else the expression/statement does not compile.

Enums can also have custom *constructors*, *fields* and *methods*. Enums can also *implement interfaces*.

The following example shows the use of custom (private) constructors, fields and methods in a so-called *complex enum*:

```java
public enum ZooSeason {

    // No "new" keyword, again illustrating that the compiler creates the enum values and creates them only once
    WINTER("Low") {
        public String getHours() {
            return "10am-3pm";
        }
    },
    SPRING("Medium"),
    SUMMER("High") {
        public String getHours() {
            return "9am-7pm";
        }
    },
    FALL("Medium");

    private final String expectedVisitors; // Immutable field, as should be the case within enum values

    // The constructor is implicitly private, and cannot be changed to protected or public
    ZooSeason(String expectedVisitors) {
        this.expectedVisitors = expectedVisitors;
    }

    public String getHours() {
        return "9am-5pm";
    }

    public void printExpectedVisitors() {
        System.out.println(expectedVisitors);
    }
}
```

As can be seen above:
* Enum values are listed first, before the other members of the enum class
* Enums can have custom *constructors*, but these constructors are always `private` (whether explicitly or implicitly)
* The `new` keyword is not used in enum values, again stressing that enum values are constructed automatically and only once
* Custom *instance methods* can even be *overridden per enum value*

In summary, ignoring the enum syntactic sugar, *under the hood* an *enum* class (whether simple or complex) is like a regular
*class*, except that there are some *restrictions*:
* There is a *fixed set of instances* of the enum class, which are `public static final` constants known at compile-time
* Indeed, construction of these constants takes place only once, on initialization of these constants
* Enum *constructors* are always `private`, to help enforce the core property of enums that they are a fixed set of constants
* And, of course, enumeration classes directly extend `java.lang.Enum` (with the type argument set to the enum subtype itself)

Note that an *enum* containing only one enum value could be used to implement *singleton classes*.

### Sealing classes

TODO
