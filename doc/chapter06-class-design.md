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
* A class `B` can (directly) *inherit* from class `A`, meaning that class `B` inherits some members from class `A`
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

There may be multiple levels of inheritance, though (e.g. `C` extends `B`, and `B` extends `A`).
Although a class can have only one direct superclass, it can *implement multiple interfaces*.

All classes have `java.lang.Object` as *ancestor*, which itself is the only class without parent.
If a class does not explicitly extend any other class, it (implicitly) has `java.lang.Object` as its parent.

The *class modifiers* we should know about are:
* `final`, meaning that the class cannot be extended
* `abstract`, meaning that the class may contain abstract members, and that a concrete subclass is required in order to instantiate the class
* `sealed`, meaning that the class may only be extended by a specific list of classes
* `non-sealed`, meaning that potentially unknown subclasses are permitted
* `static`, meaning that the class is a nested static class (in another class)

### Creating classes

*Access modifiers* `private` and `protected` *cannot be used on top-level classes*. All access modifiers can be used on
nested classes, though.

A local variable (including method parameters) can *shadow* an instance variable with the same name (because Java uses the
most granular scope). We can access the instance variable, though, by using the `this` reference, which refers to
*the current instance of the class*. The `this` reference can even be used to access inherited members, but it cannot be
used in static methods and static initializer blocks, obviously.

A method can be defined in both a parent class and a child class (see below). Moreover, even an instance variable can be defined
in both a parent class and a child class. The latter implies that there are *two distinct instance variables with the same name*,
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

Implicitly or explicitly, the *first statement* of every constructor is a `super()` call or a `this()` call, considering that the
Java compiler will generate a `super()` call if no `this()` or `super()` is provided (as first statement in the constructor body).
Conceptually this is very important to understand.

Some important additional *rules about constructors* are:
* As mentioned before, the *first statement* in each constructor is either a `super()` call or `this()` call (potentially compiler-generated)
* If a constructor makes no explicit `this()` or `super()` call (as first statement), the *compiler inserts* `super()` with no arguments
* If a constructor calls `super()`, it must be *the first statement in the constructor body* (analogous to `this()`)

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

### Initializing objects

This section is about *order of initialization*, for classes and class instances, in the presence of inheritance.

A class is *loaded at most once* (by a class loader), typically just *before its first use*, e.g. when its first instance is about to be created.
Loading a class implies that the *static members* are invoked.

The *order of initialization of a class* is as follows:
* Keep track of this class and all superclasses (until `java.lang.Object`), reverse the order, and then, per class:
  * Process all *static field declarations* and *static initializers* in the order of occurrence in the class

Note that the book is wrong here. It says static field declarations come before static initializers, which is not true.

*Final static fields* must be *explicitly* assigned a value *exactly once*.

Something similar is true for *final instance fields*. More precisely:
* *By the time the constructor completes, all final instance fields must be assigned a value exactly once*

Let's now turn to the *order of initialization of an object of a class*, assuming that class loading of the class has
already taken place (otherwise it is triggered just before creating the object of that class).

Let's pretend all default constructors have been generated, and that each constructor starts with a `super()` or `this()` call.
Then the *order of initialization of an instance of a class* is as follows, *once the class has been loaded*:
* Follow the *chain of constructor calls* through the inheritance chain
* For each ancestor-or-self class visited (from far most ancestor down to this class itself), do the following:
  * First process all *instance field declarations* and *instance initializers* in the order of occurrence in the class
  * Only after that, run the *constructor code* for that class itself (note that one constructor can call another using `this()`)

So the net effect is that initialization takes place in a top-down manner, from parent to child, where for each visited class:
* First all *instance field declarations* and *instance initializers* are run in the order of occurrence in the class
* Then the (correct) *constructor* itself is run (again note that one constructor can call another one using `this()`)

Note that the book is wrong here. It says instance field declarations come before instance initializers, which is not true.

### Inheriting members

A superclass and subclass may have *methods with the same method signature*. If they are (non-private) *instance methods*,
the subclass is said to *override* the corresponding superclass method. The subclass method may still refer to the superclass
method using the `super` keyword.

*Instance method overriding* helps support *polymorphism* and the *Liskov substitution principle*. The latter means that
it should be possible to initialize a *variable declared to be of the supertype* with an *instance of the subtype without
breaking anything*. This implies that the method override in the subclass should *not break the API contract* of the superclass
method. In particular:
* The (instance) method in the subclass must have the *same method signature* as the (instance) method in the superclass, or else we have no overriding
* The method in the subclass must be *at least as accessible* as the method in the superclass
* If the method returns a value, its type must be the *same type or a subtype of the return type* of the superclass method (*covariant return types*)
* The subclass method must *not throw any additional/broader checked exceptions* that are not allowed by the superclass method 

When overriding an instance method, use the *Override* annotation to make that intention clear to the compiler.

There is a catch, though. If the *superclass instance method is private*, we have *no overriding*, and the superclass and subclass
methods are *distinct methods*, that have nothing to do with each other, and may have quite different return types.

For *static methods* with the same signature in parent and child classes, we have *no overriding but method hiding*.
The rules for *static method hiding* are the same rules as (above) for *instance method overriding*, plus the additional
rule that if the parent class method is *static*, then so must the child class method be static (and the other way around).
So for *static methods* we do have *inheritance*, but instead of overriding we have method hiding.

If *fields* in a superclass and subclass have the same name, the two fields are *distinct variables*, where the one from
the subclass *hides* the one from the superclass. The *type of the reference variable* determines whether the subclass
or superclass field is referenced, which is quite different from method overriding and polymorphism.

*Final instance/static methods* cannot be overridden in subclasses.

### Creating abstract classes

*Abstract classes*, so classes using the `abstract` modifier, *cannot be instantiated directly* (they can only be
instantiated indirectly, as part of the initialization of a non-abstract subclass).

They can have *abstract methods* (marked `abstract`, and having no method body, but a semicolon instead). Only
*instance methods* can be abstract, so (static/non-static) fields, constructors and static methods *cannot be abstract*.

*Non-abstract classes* (i.e. *concrete classes*) *cannot declare any abstract methods*. Moreover, it *cannot leave any
inherited abstract methods unimplemented*. So a non-abstract class having an abstract class as superclass must implement all
(directly or indirectly) inherited abstract methods.

*Overriding an abstract method* follows all the rules for *overriding (instance) methods* mentioned earlier, in order to
follow the *Liskov substitution principle*. Indeed, *abstract classes/methods* support *polymorphism* very well.

*Object initialization order* is like explained before, as part of object initialization of an instance of a concrete subclass.

The combination `abstract final` makes no sense, and is therefore not allowed by the compiler.
Also, the combination `private abstract` makes no sense, and is therefore not allowed by the compiler.
(The combination `private final` makes sense but is redundant, and the compiler is ok with that.)

### Creating immutable objects

This section hardly goes into the topic of *thread-safety*. It does not talk about the *Java memory model* at all.

*Immutable objects* cannot be modified after creation. This simplifies reasoning about code, and safe sharing of (immutable)
objects between threads.

How to create an *immutable class*:
* Make the class `final` (or all constructors `private`), to prevent the creation of mutable subclasses
* Make all *instance fields* `private final`, to help prevent modification after instantiation
* Don't define any *setter methods* (or other "mutating" methods), to further help prevent modification after instantiation
* Don't allow any *referenced mutable objects* to be modified, e.g. by using *defensive copies*
* Use a *constructor* to set all instance fields of an object, making *defensive copies* if needed

So, for example, if the constructor takes a `java.util.List`, make a copy of the collection before storing it as instance field.
Also, if a "getter" returns a `java.util.List`, make a copy of the collection before returning it to the caller.

Personal note: creating immutable classes becomes easier if the "components" are immutable themselves, e.g. Guava
*immutable collections* rather than potentially mutable standard Java collections, or using a `java.time.Instant` rather
than a `java.util.Date`, etc. Then the need for defensive copies goes away entirely. Of course, *record classes* support
immutability well.
