# Chapter 5. Methods

See [OCP Java SE 17 Developer Study Guide](https://www.amazon.com/Oracle-Certified-Professional-Developer-Study/dp/1119864585/ref=sr_1_1?crid=1GIZNHYFXHAK4&dib=eyJ2IjoiMSJ9.Mz5O0lUSaZhUZ-O1Mi__dRPfXHL9GM_CfZ3JDTz910a2d8XI7Vsfj7zwcywJAfMcubfCglH02m8PwlAk_DORk8SS5460zaDP1fskFDX4sUiFVR4pxE1Ln0VIY-g5awTQaOJKp4t0Y1HchXkrw0HtOeVSHg3dHG8Jql9TibGCj-WeXYyNdMp4zWtgM4EimHpl4wvlJZufvGpNjNEmXIObAd2B1mp1skt5k7v_B-k_Ip4.bRERgxl7gsekO5AihUKuOeT_yoO6Bsg7jHigb4sjHEM&dib_tag=se&keywords=ocp+java+se17&qid=1714573695&sprefix=ocp+java+%2Caps%2C192&sr=8-1).

This chapter is about methods in Java.

More information can be found in the (more low-level)
[Java AST API](https://docs.oracle.com/en/java/javase/17/docs/api/jdk.compiler/com/sun/source/tree/package-summary.html),
which contains links to the Java Language specification for specific language constructs.

In particular, see:
* [MethodTree](https://docs.oracle.com/en/java/javase/17/docs/api/jdk.compiler/com/sun/source/tree/MethodTree.html)
* [MethodInvocationTree](https://docs.oracle.com/en/java/javase/17/docs/api/jdk.compiler/com/sun/source/tree/MethodInvocationTree.html)
* [VariableTree](https://docs.oracle.com/en/java/javase/17/docs/api/jdk.compiler/com/sun/source/tree/VariableTree.html)

### Designing methods

```java
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public static List<Field> getDeclaredFields(Class<?> cls) {
    return Arrays.stream(cls.getDeclaredFields()).collect(Collectors.toList());
}

public static void printDeclaredFields(String className, PrintWriter pw) throws ClassNotFoundException {
    var cls = Class.forName(className);
    for (var fld : getDeclaredFields(cls)) {
        pw.println(fld);
    }
}
```

In this example there are 2 *method declarations*, containing all the information needed to call the method.
In each method declaration, the *method signature* consists of the *method name* and *parameter list*.
The method signature determines *which* method can be called and *how* it can be called (the actual parameter names are
irrelevant in this case). Return type and access modifiers are not part of the method signature, and they determine *where*
the method can be called.

| Element             | Value in "getDeclaredFields"      | Value in "printDeclaredFields"     | Required?            | Other values         |
|---------------------|-----------------------------------|------------------------------------|----------------------|----------------------|
| Access modifier     | public                            | public                             | No                   | private, protected   |
| Optional specifiers | static                            | static                             | No                   | final, abstract etc. |
| Return type         | List&lt;Field&gt;                 | void                               | Yes                  |                      |
| Method name         | getDeclaredFields                 | printDeclaredFields                | Yes                  |                      |
| Parameter list      | (Class&lt;?&gt; cls)              | (String className, PrintWriter pw) | Yes                  |                      |
| Exception list      |                                   | throws ClassNotFoundException      | No                   |                      |
| Method body         | code block after method signature | code block after exception list    | Yes, unless abstract |                      |

The method parameter list can be empty (having no parameters), but the *parentheses are mandatory*.

The relative order of access modifiers and optional specifiers can deviate, although that is not recommended.
In any case they must occur *before the return type*.

So the 2 method signatures (including the parameter names) are:
* `getDeclaredFields(Class<?> cls)`
* `printDeclaredFields(String className, PrintWriter pw)`

*Access modifiers*:
* *private*; method can only be called from within the same class
* package access (no keyword): method can only be called from a class in the same package
* *protected*: method can only be called from a class in the same package or from a subclass
* *public*: method can be called from anywhere

*Optional specifiers for methods*:
* *static*: the method belongs to the class itself instead of to an instance of the class
* *abstract*: abstract method (in an abstract class or interface) where the method body is excluded
* *final*: the method may not be overridden in a subclass
* *default*: used in an interface to provide a default implementation of a method (for classes that implement the interface)
* *synchronized*: used in multithreaded code
* *native*: used when interacting with code in other programming languages (out of scope here)
* *strictfp*: used for making floating-point computations portable (out of scope here)

The *return type* is mandatory, even if it is `void`. If it is not `void`, a `return` statement in the method body is required,
and it must include the primitive or reference to return. For non-`void` return types, *all code paths* in the method body
must lead to such a `return` statement or an exception. *Conceptually*, think of a *total function*
`java.util.function.Function<T, R>` in that case (i.e. a "total function", modulo thrown exceptions).

The *method name* by convention starts with a lowercase letter, although this is not required. Recall from chapter 1
that each *identifier* (and therefore each method name) must obey the following requirements:
* Identifiers can contain letters, digits, currency symbols and underscores
* But they cannot start with a digit, and they cannot be just an underscore
* And they cannot be *reserved words* (note that "var" is not a reserved word, but a "reserved type name")
* And they cannot be "word" literals like `true`, `false` and `null`

The *parameter list* has mandatory *parentheses*, that surround zero or more *comma-separated* parameters.
Each *parameter* consists of a *type* followed by a *parameter name*.

The parameter list is about the *types of parameters* and their *order*, and not about the parameter names.
So, if a class contains 2 methods with the same method signature except for the parameter names, that would lead to a
compilation error.

The *exception list* will be treated in chapter 11.

The *method body* is a statement, in particular a code block. It is mandatory unless the method is abstract (in which case we need
a semicolon to finish the abstract method declaration).

### Declaring local and instance variables

*Local variable declarations* have the following properties:
* They occur *in a block* (or in the header of a basic or enhanced *for* statement, or in a "resource specification", or in a type pattern)
* Typically, that block is a *method body* or a nested block inside a method body
* These variables are *not implicitly initialized* if an initializer is missing
* The scope of such a variable is (the remainder of) the *block in which it is defined*
* But if the variable is of a reference type, the referenced object itself may still exist after the block is executed
* There is only one *modifier* for local variables, and that is *final* (and the combination `final var` is allowed)
* Note that for reference types, `final` *only makes the reference final*, not the referenced object
* A final local variable may initially be initialized, but it must be *initialized exactly once before use*
* It may even get different values in different code paths, as long as it is initialized exactly once before use
* An *effectively final* local variable is one that is not modified after assigning it a value
* In other words, an effectively final local variable is one where the `final` modifier can be used without causing a compilation error
* These remarks about "final" and "effectively final" also hold for method/constructor *parameters*, which can be seen as pre-initialized local variables

*Instance variable declarations* have the following properties:
* They are the *non-static field* declarations in a class, and occur in a class outside of any method
* These variables are *implicitly initialized* to the default value of their type (`null` for reference types) if an initializer is missing
* The scope of such a field is the life-time of the containing object itself
* Instance variables can have the *same access modifiers* (public, private, protected or "default") as methods
* They can have optional specifiers *final*, *volatile* and *transient*
* A final instance variable must be assigned a value *when declared, or when the object is instantiated* (in constructor or instance initializer)
* And not very surprisingly, a final instance variable must be assigned a value *exactly and only once*
* Although non-final instance variables get a default value if not explicitly initialized, this is not allowed with `final`

### Varargs

Methods can have *at most one varargs parameter*, and it must be *the last one*.

*Varargs* method parameters are *arrays*. It is just that *when calling* the method, it is allowed to pass the individual
array elements one by one as method argument.

### Applying access modifiers

The following *access modifiers* can be applied to *methods and fields*, from most to least restrictive:
* `private`: only accessible within the same class
* *package access* (no keyword): accessible to all members of the same package
* `protected`: accessible to all members of the same package (like package access) and accessible within subclasses
* `public`: accessible anywhere (when ignoring *modules*)

So *private* means that even other classes in the same source file cannot access the private method/field.

The details of *protected* access are a bit complicated. For example, consider the following classes:

```java
package rectangle;

public class Rectangle {
    private final double height;
    private final double width;

    public Rectangle(double height, double width) {
        this.height = height;
        this.width = width;
    }

    protected double area() {
        return height * width;
    }
}
```

and

```java
package square;

import rectangle.Rectangle;

public class Square extends Rectangle {

    public Square(double width) {
        super(width, width);
    }

    public void printArea() {
        // Accessing protected member of superclass in this subclass
        System.out.println(area());
    }

    public static void printArea(double width) {
        // Within this subclass Square, create a Square object and use protected members of the superclass via this variable
        Square square = new Square(width);
        System.out.println(square.area());
    }
}
```

Subclass `Square` can access protected members of superclass `Rectangle`:
* *directly* (belonging to the inherited object), *without referring to a variable* (this is the usual case)
* or: *indirectly* (typically belonging to another object), *referring to a variable* declared to be of type `Square`, or a subtype

It is this 2nd case that may be a bit surprising, although it does make sense.

### Accessing static data

A *static* field or method (or class) belongs to the class and not to any specific instance of the class, if any.

Static members can be accessed through the class name, but (confusingly) also through a variable referring to an instance,
even if it is `null` (in which case it does not throw an NPE)! It is still one and the same static member.

Static methods are often used for utility methods (requiring no instance) and for global state shared by all instances of the class.

Be careful: *static members cannot use instance members*, other than through referencing an instance of the class.

The static variable modifiers are the same as for instance variables: `final`, `volatile` and `transient`.
A `static final` variable is known as a *constant*, with a different naming convention (e.g. NUM_PLAYERS), although it would
only really be a constant if the type is either primitive or a reference to a deeply immutable type.

For *instance fields* (i.e. non-static fields), we learned earlier that constructor code itself is run after *fields* and
*instance initializer blocks*. Something analogous is true for *static fields*, except that there is no such thing as a
"static constructor".

*Static initializers* are run when the class is first used, in the order of occurrence in the source code. As for `final`
and the rule that assignment of `final` members must be done exactly once, this can still be the case even when having
multiple static initializers.

*Static imports* import static members of a class, to use them conveniently without prefixing them with the class name.
Static imports can use wildcards too. Note that "own" members have priority over statically imported ones.

### Passing data among methods

Java uses *pass-by-value* (as opposed to pass-by-reference), making a *copy of the variable* to be passed to the method.
That is, the *parameter* is a *new variable*, scoped to the method body.
So in that sense assignments to a parameter within the method do not affect the caller.

Yet for parameters of reference types, *the copy is a reference*, and if the referenced object is mutable any change to
that object made in the method affects the caller too. That gives the appearance of call-by-reference, although it is
still call-by-value, with the value being just the reference.

*Autoboxing* and *auto-unboxing* for all primitive types and their wrapper types works pretty much out of the box.
Yet note that "unboxing and implicit casting in one go" (say, from wrapper type `Integer` to primitive type `long`)
does work, while the other way around "boxing after implicit casting in one go" does not work.
Also, unboxing `null` throws an NPE.

### Overloading

*Method overloading* is not the same as *method overriding*. Method overloading happens when methods in the same class
*have the same name but different method signatures*. Recall that the method signature does not include the return type.
Nor does it include specifiers like static. Also note that the method signature in this context does not include the
parameter names.

Method overloading is resolved at compile-time.

If the class contains 2 overloaded methods with the same signature, the compiler will emit an error.

Overloading a method where one has a more specific parameter type than the other is allowed, and the best (i.e. most specific)
match will be selected.

Overloading for methods with primitives as parameters, in the correct order:
* Exact match by type
* Larger primitive type
* Autoboxing
* Varargs

If there are 2 potential matching overloads and none is more specific than the other, the compiler will emit an error.
