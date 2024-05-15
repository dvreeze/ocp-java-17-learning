# Chapter 6. Class Design

See [OCP Java SE 17 Developer Study Guide](https://www.amazon.com/Oracle-Certified-Professional-Developer-Study/dp/1119864585/ref=sr_1_1?crid=1GIZNHYFXHAK4&dib=eyJ2IjoiMSJ9.Mz5O0lUSaZhUZ-O1Mi__dRPfXHL9GM_CfZ3JDTz910a2d8XI7Vsfj7zwcywJAfMcubfCglH02m8PwlAk_DORk8SS5460zaDP1fskFDX4sUiFVR4pxE1Ln0VIY-g5awTQaOJKp4t0Y1HchXkrw0HtOeVSHg3dHG8Jql9TibGCj-WeXYyNdMp4zWtgM4EimHpl4wvlJZufvGpNjNEmXIObAd2B1mp1skt5k7v_B-k_Ip4.bRERgxl7gsekO5AihUKuOeT_yoO6Bsg7jHigb4sjHEM&dib_tag=se&keywords=ocp+java+se17&qid=1714573695&sprefix=ocp+java+%2Caps%2C192&sr=8-1).

This chapter is about class design in Java.

More information can be found in the (more low-level)
[Java AST API](https://docs.oracle.com/en/java/javase/17/docs/api/jdk.compiler/com/sun/source/tree/package-summary.html),
which contains links to the Java Language specification for specific language constructs.

In particular, see:
* [ClassTree](https://docs.oracle.com/en/java/javase/17/docs/api/jdk.compiler/com/sun/source/tree/ClassTree.html)
* [MethodTree](https://docs.oracle.com/en/java/javase/17/docs/api/jdk.compiler/com/sun/source/tree/MethodTree.html), for *constructors* and *methods*
* [NewClassTree](https://docs.oracle.com/en/java/javase/17/docs/api/jdk.compiler/com/sun/source/tree/NewClassTree.html)

### Inheritance

*Inheritance* terminology:
* A class `B` can *inherit* from class `A`, meaning that class `B` inherits some members from class `A`
* Class `B` is called a *direct subclass* or *child class* of class `A`
* Class `A` is called the *direct superclass* or *parent class* of class `B`
* We can also use the terms *subtype* and *supertype*, but they are more general terms, including *interfaces* as well
* Syntactically we declare `B` a direct subclass of `A` by using the *extends* keyword
* For this it is required that class `A` is *not marked final*
* Inheritance is *transitive*, and we can use the terms *descendant class* (or subclass) and *ancestor class* (or superclass) accordingly

Example:

```java
// Superclass
public class Mammal {}

// Subclass (which can not be extended any further, due to the "final" keyword)
public final class Dog extends Mammal {}
```

Java supports *single inheritance* (and not multiple inheritance), so each class can inherit from only one direct superclass.
This makes perfect sense, once we think about this (and what kinds of issues are thus prevented).

There may be multiple levels of inheritance, though. Although a class can have only one direct superclass, it can
*implement multiple interfaces*.

All classes have `java.lang.Object` as *ancestor*, which itself is the only class without parent.
If a class does not explicitly extend any other class, it (implicitly) has `java.lang.Object` as its parent.

The *class modifiers* we should know about are:
* `final`, meaning that the class cannot be extended
* `abstract`, meaning that the class may contain abstract members, and that a concrete subclass is required in order to instantiate the class
* `sealed`, meaning that the class may only be extended by a specific list of classes
* `non-sealed`, meaning that potentially unnamed subclasses are permitted
* `static`, meaning that the class is a nested static class (in another class)

A `final` class cannot be extended any further.

### Creating classes

*Access modifiers* `private` and `protected` *cannot be used on top-level classes*. All access modifiers can be used on
nested classes, though.

A local variable (including method parameters) can *shadow* an instance variable with the same name (because Java uses the
most granular scope). We can access the instance variable, though, by using the `this` reference, which refers to
*the current instance of the class*. The `this` reference can even be used to access inherited members, but it cannot be
used in static methods and static initializer blocks, obviously.

A method can be defined in both a parent class and a child class. Moreover, even an instance variable can be defined in both
a parent class and a child class. The latter implies that there are *two distinct instance variables with the same name*,
where the one from the subclass *hides* the one from the superclass.

To reference the instance variable or method from the superclass, use the `super` reference.

The `super` reference is like the `this` reference, except that it excludes the members declared in this class itself.
Again, this can only be used if we have a current instance of the class, so it cannot be used in static methods etc.

### Declaring constructors

*Constructors* look like methods, except that:
* constructors have *no return type*
* constructors have the *same name as the class* to which they belong (case-sensitive, as always in Java)

So constructors are easy to distinguish syntactically from methods (if we look carefully).

A class can have *multiple constructors*, leading to *constructor overloading*. The latter is *like method overloading*,
where we can ignore the "method name" since that would always be the class name. Analogously to method overloading, constructor
overloading is based on the *constructor signature* (which is like a method signature where the name can be ignored).

A constructor can call another constructor in the same class, using the `this()` syntax. Mind the parentheses! Of course,
between the parentheses there can be any number of parameters.

Some important *rules about constructors* are:
* A class can have many *overloaded constructors*, all having a different *constructor signature*
* If no constructors are declared at all, the compiler will insert a *default no-argument constructor* with empty body
* If a constructor calls `this()`, it must be *the first statement in the constructor body*
* *Cyclic constructor calls* are detected and *not allowed* by the Java compiler

With the `super()` syntax a *constructor in the direct superclass* can be called.

The *first statement* of every constructor is a `super()` call or a `this()` call, considering that the Java compiler will
generate a `super()` call if no `this()` or `super()` is provided (as first statement in the constructor body). Conceptually
this is very important to understand.

Some important additional *rules about constructors* are:
* As mentioned before, the *first statement* in each constructor is either a `super()` call or `this()` call (potentially compiler-generated)
* If a constructor makes no explicit `this()` or `super()` call (as first statement), the *compiler inserts* `super()` with no arguments
* If a constructor call `super()`, it must be *the first statement in the constructor body* (analogous to `this()`)

A simple example of compiler-generated constructor code:

```java
// This is the class written by the programmer
public class Developer {}

// The compiler generates a default constructor in this case
public class Developer {
    public Developer() {}
}

// The compiler generates a super() call in the generated default constructor
public class Developer {
    public Developer() {
        super();
    }
}
```
