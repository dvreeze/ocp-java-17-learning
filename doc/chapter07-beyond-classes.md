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
* Compared to abstract classes, interfaces "trade constructors for multiple interface inheritance"

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
  * the compiler does not allow any other kinds of fields than *public static final* fields, so e.g. "private" fields are not allowed

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

Be careful: (non-private) abstract instance methods are implicitly `public` in interfaces, but the method overrides in
implementing classes must *explicitly add the public modifier*, in order to properly override the interface method.

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
Enumerations offer compile-time type-safety for such sets of constants. This set of constants *may be empty*.

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

With enums we limit the number of values/instances to a fixed set known at compile-time. What if we only want to limit the
number of *direct subtypes* of a class/interface, instead of the number of instances of one class? For that, Java 17 offers
*sealed classes* and *sealed interfaces*.

A *sealed class* is a class that limits the *direct subclasses* to a fixed set of classes. These direct subclasses are required
to be in the *same package or module*. A sealed class is explicitly marked `sealed`, and the allowed direct subclasses of the
sealed class are specified in a `permits`-clause. For example:

```java
public sealed abstract class Expression permits UnaryExpression, BinaryExpression, OtherExpression { }
```

In this example, only classes `UnaryExpression`, `BinaryExpression` and `OtherExpression` are permitted as *direct subclasses*,
and they have to be in the same package or module.

Each of these permitted direct subclasses (of the sealed class) must themselves be:
* either a `sealed` class itself
* or a `final` class, that therefore cannot be extended any further
* or a `non-sealed` class, which opens up that class for direct extension by any subclass (known or not yet known)

For example (leaving out class members for brevity):

```java
public sealed abstract class Expression permits UnaryExpression, BinaryExpression, OtherExpression { }

public final class UnaryExpression extends Expression { }

public final class BinaryExpression extends Expression { }

public non-sealed abstract class OtherExpression extends Expression { }
```

Note that there are no implicit modifiers for sealed class hierarchies. Everything is explicit (`sealed`, `permits`, `extends`, `final` etc.).

The `permits` clause can be left out, though, in 2 cases:
* The permitted direct subclasses are *nested* in the sealed parent class (typically as static inner classes)
* The permitted direct subclasses belong to the same source file (but then they cannot be public if the sealed parent is public)

Besides sealed classes, we can have *sealed interfaces*. The `permits`-clause of a *sealed interface* can contain:
* Classes that directly implement that interface (they must be `sealed`, `final` or `non-sealed`)
* Interfaces that directly extend that interface (they must be `sealed` or `non-sealed`)

Analogously to sealed classes, these direct subtypes permitted by a sealed interface must belong to *the same package or module*
as the sealed interface.

Note that in Java 17, the use of sealed classes in `switch` statements/expressions is supported, be it in Preview.

### Encapsulating data with records

Suppose we want to pass data carriers around as *POJOs* (plain old Java objects). In order to prevent someone outside the
class to tamper with the data of the class (if it is mutable), we need *encapsulation*. In particular, we could benefit
from *immutability*, which was treated in chapter 6.

That would lead to a lot of *boilerplate*, for fields, constructors, accessor methods, overridden `equals`, `hashCode` and
`toString`, etc. *Records* support *immutable POJOs* with much less boilerplate.

Consider the following rather trivial *record*:

```java
public record Point(double x, double y) {

    // Immutability requires the creation of a new Point object
    public Point move(double deltaX, double deltaY) {
        return new Point(x + deltaX, y + deltaY);
    }
}
```

Let's peek inside using *javap*:

```shell
javap -cp ./classes -p chapter07.Point
```

```
public final class chapter07.Point extends java.lang.Record {
  private final double x;
  private final double y;
  public chapter07.Point(double, double);
  public chapter07.Point move(double, double);
  public final java.lang.String toString();
  public final int hashCode();
  public final boolean equals(java.lang.Object);
  public double x();
  public double y();
}
```

The `Record` superclass itself:

```shell
javap -protected java.lang.Record
```

```
public abstract class java.lang.Record {
  protected java.lang.Record();
  public abstract boolean equals(java.lang.Object);
  public abstract int hashCode();
  public abstract java.lang.String toString();
}
```

This shows the following about *records*:
* Despite the friendly syntax for records, *under the hood* records are *classes that extend abstract class Record*
* Records are (implicitly) `final`, so cannot be extended (the `final` keyword is allowed, but in any case assumed)
* The *record components* are `private final` instance fields, and therefore *encapsulated*
* There is by default a generated *public constructor*, setting all record component fields (but we can write custom constructor code ourselves)
* Records have `public` *accessor methods*, each with the same name as the component/field
* Although not visible from the *javap* output, `equals` is overridden for *value equality*, based on the record components
* Of course, along with `equals`, `hashCode` is overridden as well, and so is `toString`

Note that records without any components are allowed by the compiler.

Also note that just like enums, records have a pre-defined superclass, and no subclasses. Hence, for code reuse we need
*interfaces*, since enums and records can implement any number of interfaces.

The *long constructor* (setting all instance fields) is normally *generated* by the compiler. We can define it ourselves,
however. If we do so, with the same constructor signature, no constructor will be generated by the compiler.
In any case, the long constructor must set all instance fields. After all, they are `private final` fields, and
*instance initializers are not allowed* in records.

Fortunately, we can also provide a *compact constructor*, for example for record component validation purposes, or to
make defensive copies of mutable data. The compact constructor syntactically *takes no constructor parameters, not even
the parentheses*.

In the *compact constructor* we can provide some validation/transformation code that work on the (invisible)
*constructor arguments*. At the end, the long constructor is called automatically. In the compact constructor, we
cannot *modify the fields of the record*, or else the record will not compile.

It is possible to *overload constructors* (not the compact constructor, obviously), but:
* the first line must be an explicit `this()` call to another constructor
* after that first line, we can no longer modify the already initialized final instance fields

Finally, it is also possible to:
* override automatically provided instance methods like `equals`, `hashCode`, `equals` and the *accessor methods*
* add custom (static or instance) methods, static fields, nested classes/interfaces etc.

Yet it is *not allowed to add instance fields*, not even private ones, which makes sense because a record is essentially
*a struct of data components*.

### Creating nested classes

A *nested class* is a class defined with another class. There are 4 flavours:
* *Inner class* (or *member inner class*), which is a *non-static type member* of the outer class
* *Static nested class*, which is a *static type member* of the outer class
* *Local class*, which is defined *within a method body*
* *Anonymous class*, which is a special kind of *local class without a name*

When we say *nested class* we actually mean any *nested type*, including nested interfaces, enums, records and annotations.

A *member inner class*:
* is a nested class whose instances are linked to a *specific instance of the outer class*
* is therefore a nested class that can access *instance members and static members* of the outer class, even private members
* can *extend a class and implement interfaces* (after all, what would be the point of disallowing that?)
* can be marked `abstract` or `final` (again, why not?)
* can be declared `public`, `protected`, package-private or `private`
* can have static members (other than just static constants), since Java 16

Some examples of how to create an instance of an inner class (which requires an instance of the outer class):
* `outerObject.new Inner()`
* `Outer.this.new Inner()`
* `new Outer().new Inner()`

A *static nested class*:
* is a nested class whose instances are *not linked to specific instances of the outer class*, but *to the outer class as a whole* instead
* is therefore a nested class that can access *only static members* of the outer class, even private static members
* is a nested class within an outer class that acts as a "namespace"
* has fields and methods that can be referred to by the enclosing class (!)
* can *extend a class and implement interfaces*
* can be `abstract` or `final`
* can be declared `public`, `protected`, package-private or `private`

Note that nested interfaces, enums and records are always `static` members of the outer class, even if the keyword `static`
is missing.

A *local class*:
* is a nested class *defined within a method* (or within a constructor or initializer)
* is therefore a class *scoped to a specific method call*, going out of scope when the method returns
* is therefore a class *whose instances can only be created from within the method*, although *those instances can be returned from the method* (just like local variables)
* can access *all fields and methods* of the outer class, provided the enclosing method is an *instance method*
* can access *final and effectively final* local variables
* can *extend a class and implement interfaces*
* can be `abstract` or `final`
* has *no access modifiers* (obviously)

The reason for *only final and effectively local variables* to be available is that nested classes lead to separate "class"
files, and there is no way for the separate class to refer to local variables. If the local variable is final or effectively
final, a copy of that local variable can be passed to the constructor of the local class, however.

An *anonymous class* is a special kind of *local class without name*. For anonymous classes the declaration of the class
and the creation of an instance of that class are combined.

The template for *creation of an anonymous class and its instance* is as follows, where type `A` is the *name of a known
class or interface*:

```java
new A() {
    // implementations of abstract methods etc.
}
```

Note that the anonymous class *directly extends class A or directly implements interface A*. Only one direct supertype
can be given in an anonymous class instantiation expression.

Like is the case for local classes, anonymous classes:
* can access *all fields and methods* of the outer class, provided the enclosing method is an *instance method*
* can access *final and effectively final* local variables

Although we said that anonymous classes are special kinds of local classes, they can also occur *outside of method bodies*.

Note that with the advent of *lambdas* in Java 8, anonymous classes are rarely used anymore.

### Understanding polymorphism

Conceptually, distinguish *in-memory (heap) objects* on the one hand from *references* (or *pointers*) to those in-memory
objects on the other hand. Objects are not directly accessible, as we know, but only accessible *through references*.
Note that *assignment for reference types* only *assigns references*, copying a reference to *the same in-memory object*.
Recall this is also the case when calling methods and passing parameters of reference types. If we want to copy an in-memory
object itself, we need to create a new object through a constructor.

The *runtime type* of the (reference to) the in-memory object is the type of the constructor that created the object.
We may *assign this reference to a variable of a supertype*, *without casting*. This supertype may be a superclass or
(implemented) interface.

The in-memory object determines *which capabilities exist* within the object. The *type of the reference* determines which
of those fields and methods can be accessed through that reference. Hence, the term *polymorphism*.

As for polymorphism:
* *Implicit up-casting* for reference types (to a supertype, which is an interface or class type) always succeeds (see above)
* Only up-casting may be implicit; otherwise we need explicit casting
* *Explicit down-casting* for reference types is allowed by the compiler, but may lead to a `ClassCastException` at runtime
* *Explicit casting* for *unrelated types* is disallowed by the compiler

Yet what are *unrelated types*? Two classes having only supertype `java.lang.Object` in common are clearly unrelated
(if we try to cast to another type than `java.lang.Object`). But for interfaces it is more difficult for the compiler to
determine whether 2 types are unrelated. After all, we have multiple inheritance for interfaces, and if some class does not
implement some interface, a subclass might still implement that interface. Only if the compiler knows for sure the cast
cannot succeed, it will disallow the cast.

Analogously, the compiler will only disallow an occurrence of the `instanceof` operator if it knows for sure it cannot return `true`.

Recall from chapter 6 the rules for (non-private) *instance method overriding*, and for *static method hiding* and *field hiding*.
These rules were given for classes, but easily generalize to classes and interfaces. *Polymorphism* through *method overriding*
is one of the *most important properties of java*.
