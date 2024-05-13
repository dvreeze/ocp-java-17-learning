# Chapter 5. Methods

See [OCP Java SE 17 Developer Study Guide](https://www.amazon.com/Oracle-Certified-Professional-Developer-Study/dp/1119864585/ref=sr_1_1?crid=1GIZNHYFXHAK4&dib=eyJ2IjoiMSJ9.Mz5O0lUSaZhUZ-O1Mi__dRPfXHL9GM_CfZ3JDTz910a2d8XI7Vsfj7zwcywJAfMcubfCglH02m8PwlAk_DORk8SS5460zaDP1fskFDX4sUiFVR4pxE1Ln0VIY-g5awTQaOJKp4t0Y1HchXkrw0HtOeVSHg3dHG8Jql9TibGCj-WeXYyNdMp4zWtgM4EimHpl4wvlJZufvGpNjNEmXIObAd2B1mp1skt5k7v_B-k_Ip4.bRERgxl7gsekO5AihUKuOeT_yoO6Bsg7jHigb4sjHEM&dib_tag=se&keywords=ocp+java+se17&qid=1714573695&sprefix=ocp+java+%2Caps%2C192&sr=8-1).

This chapter is about methods in Java.

### Designing methods

Method examples (non-generic):

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

| Element             | Value in "getDeclaredFields" | Value in "printDeclaredFields"     | Required?            | Other values         |
|---------------------|------------------------------|------------------------------------|----------------------|----------------------|
| Access modifier     | public                       | public                             | No                   | private, protected   |
| Optional specifiers | static                       | static                             | No                   | final, abstract etc. |
| Return type         | List<Field>                  | void                               | Yes                  |                      |
| Method name         | getDeclaredFields            | printDeclaredFields                | Yes                  |                      |
| Parameter list      | (Class<?> cls)               | (String className, PrintWriter pw) | Yes                  |                      |
| Exception list      |                              | throws ClassNotFoundException      | No                   |                      |
| Method body         | block after method signature | block after exception list         | Yes, unless abstract |                      |

The method parameter list can be empty (having no parameters), but the *parentheses are mandatory*.

The relative order of access modifiers and optional specifiers can deviate, although that is not recommended.
In any case they must occur *before the return type*.

So the 2 method signatures are:
* `getDeclaredFields(Class<?> cls)`
* `printDeclaredFields(String className, PrintWriter pw)`

*Access modifiers*:
* *private*; method can only be called from within the same class
* package access (no keyword): method can only be called from a class in the same package
* *protected*: method can only be called from a class in the same package or from a subclass
* *public*: method can be called from anywhere

*Optional specifiers for methods*:
* *static*: the method belongs to the class itself instead of an instance of the class
* *abstract*: abstract class or interface where the method body is excluded
* *final*: the method may not be overridden in a subclass
* *default*: used in an interface to provide a default implementation of a method (for classes that implement the interface)
* *synchronized*: used in multithreaded code
* *native*: used when interacting with code in other programming languages (out of scope here)
* *strictfp*: used for making floating-point computations portable (out of scope here)

The *return type* is mandatory, even if it is `void`. If it is not `void`, a `return` statement in the method body is required,
and it must include the primitive or object to return. For non-`void` return types, all code paths in the method body must lead
to such a `return` statement or an exception.

The *method name* by convention starts with a lowercase letter, although this is not required. Recall from chapter 1
that each *identifier* (and therefore each method name) must obey the following requirements:
* Identifiers can contain letters, digits, currency symbols and underscores
* But they cannot start with a digit, and they cannot be just an underscore
* And they cannot be *reserved words* (note that "var" is not a reserved word, but a "reserved type name")
* And they cannot be "word" literals like true, false and null

The *parameter list* has mandatory *parentheses*, that surround zero or more *comma-separated* parameters.
Each *parameter* consists of a *type* followed by a *parameter name*.

The parameter list is about the *types of parameters* and their *order*, and not about the parameter names.
So, if a class contains 2 methods with the same method signature except for the parameter names, that would lead to a
compilation error.

The *exception list* will be treated in chapter 11.

The *method body* is a statement, typically a code block. It is mandatory unless the method is abstract (in which case we need
a semicolon to finish the abstract method declaration).
