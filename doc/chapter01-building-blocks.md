# Chapter 1. Building Blocks

See [OCP Java SE 17 Developer Study Guide](https://www.amazon.com/Oracle-Certified-Professional-Developer-Study/dp/1119864585/ref=sr_1_1?crid=1GIZNHYFXHAK4&dib=eyJ2IjoiMSJ9.Mz5O0lUSaZhUZ-O1Mi__dRPfXHL9GM_CfZ3JDTz910a2d8XI7Vsfj7zwcywJAfMcubfCglH02m8PwlAk_DORk8SS5460zaDP1fskFDX4sUiFVR4pxE1Ln0VIY-g5awTQaOJKp4t0Y1HchXkrw0HtOeVSHg3dHG8Jql9TibGCj-WeXYyNdMp4zWtgM4EimHpl4wvlJZufvGpNjNEmXIObAd2B1mp1skt5k7v_B-k_Ip4.bRERgxl7gsekO5AihUKuOeT_yoO6Bsg7jHigb4sjHEM&dib_tag=se&keywords=ocp+java+se17&qid=1714573695&sprefix=ocp+java+%2Caps%2C192&sr=8-1).

Topics in chapter 1 (from this OCP Java SE 17 Developer Study Guide):
* Tools like javac, java, jar, javadoc etc. (and choosing the Java version)
  * Make sure to choose a Java major version (in our case Java 17) before compiling and running code (check output of "javac -version" and "java -version")
  * For *single-file source-code* programs, we can omit the "javac" command and directly run `java MyProgram.java <arg0> ...`
  * For running the `javac`, `java` and `jar` commands, also see chapter 12 (about modules)
* Note that since Java 9 there is no separate JRE download anymore ("JDK without compiler")
* Understanding *class structure*:
  * An *object* is also called *instance of a class* (the class is a "template" for construction)
  * Objects are not accessible directly, but only via *references* (or *pointers*, but without the dangers associated with them in languages like C)
  * Primary elements of a class, i.e. class *members*:
    * (instance-level or static) *fields*
    * (instance-level or static) *methods* (the functions of a class or instance)
    * constructors (that create objects/instances)
  * Be careful: *constructors look like methods, but without return type* (so it is easy to mistake some methods for constructors)
  * Also: unlike fields, methods always have a parameter list, even if it is empty
* *Comments* (mind nesting peculiarities):
  * Single-line comments
  * Multi-line comments (even if on a single line)
  * Javadoc comments (the compiler might complain if trying to nest those comments)
* *Source files* and *classes*:
  * Normal: one top-level type (class/interface) corresponds to one source file
  * The (top-level) type must match the source file's name without the ".java" extension
  * We can have multiple top-level types in a source file, but at most one of them can be public
* A program's *main* method (and how to run the program)
  * Three different "ways" of syntactically passing the main method's parameter(s), including the use of *varargs*
    * `String[] args` (or using any other parameter name)
    * `String args[]` (or using any other parameter name)
    * `String... args` (or using any other parameter name)
  * Passing program arguments when running programs
* *Package declarations* and *import statements*:
  * Mind order of (optional) package declaration coming first, then the (optional) import statements, and then the *top-level type definitions*
  * When do we need to import a type, if we do not want to use FQCNs? No imports needed for anything in package `java.lang`, or anything in the "current" package
  * How can we import types (e.g. via wildcards)?
    * Normal (non-static) imports cannot import any methods
    * Wildcards can only appear once, at the end of the import statement (before the semicolon)
  * Note that wildcard imports do not import child packages and their content
  * Solving naming conflicts, e.g. via FQCNs, or via combination of (very specific) non-wildcard and wildcard import
* *Object construction*:
  * Constructor code itself is run after *fields* and *instance initializer blocks*
  * The compiler provides a *default* parameterless "do nothing" constructor if none is provided by the programmer
  * More details about constructors are in chapter 6 on class design
* *Code blocks*:
  * Code blocks are surrounded by braces
  * Code blocks (i.e. their pairs of braces) must nest properly and braces always come in pairs
* *Primitive types* versus *reference types*:
  * Understand the difference: the value is a primitive value versus a *reference* to an object (or a `null` reference)
  * Values of reference types can be *null*; not so for values of primitive types
  * Primitive values have no methods declared on them
  * Know the primitive types and their wrapper types well
    * `boolean`, which is `false` (default) or `true`
    * `byte`, signed 8-bit (single-byte) integer number, default `0`
    * `short`, signed 16-bit (2-byte) integer number, default `0`
    * `int`, signed 32-bit (4-byte) integer number, default `0`
    * `long`, signed 64-bit (8-byte) integer number, default `0L`
    * `float`, signed 32-bit (4-byte) floating point value, default `0.0f`
    * `double`, signed 64-bit (8-byte) floating point value, default `0.0`
    * `char`, 16-bit (2-byte) (unsigned) Unicode value, default `\u0000`
  * The primitive wrapper types (which are reference types) have predictable names, but the `int` wrapper type is called `java.lang.Integer` (similar for `char` and `java.lang.Character`)
    * Except for `Boolean` and `Character`, they extend class `java.lang.Number`
    * Class `Number` offers methods like `byteValue()`, `intValue()`, `doubleValue()` etc.; loss of precision is possible, including overflow
    * Compare `Integer.parseInt("123")` (returning an `int`) with `Integer.valueOf("123")` (returning an `Integer`)
  * Autoboxing and unboxing
  * Also know the corresponding literals well (note that float literals need an "f" suffix; otherwise it's a `double`)
    * That includes octal, hexadecimal and binary number literals
  * Underscores in numeric literals
    * Not allowed at beginning and end, and not allowed directly before or after decimal point
  * *Text blocks* (also known as multi-line strings), and *incidental versus essential whitespace*
* *Identifiers*:
  * They are used for variables, methods, classes, interfaces and packages (with corresponding naming conventions)
  * But the rules are the same for all of them:
    * Identifiers can contain letters, digits, currency symbols and underscores
    * But they cannot start with a digit, and they cannot be just an underscore
    * And they cannot be *reserved words* (note that "var" is not a reserved word, but a "reserved type name")
    * And they cannot be "word" *literal values* like `true`, `false` and `null`
* *Variables*:
  * They are pieces of memory storing data (primitives or references)
  * Declaring them gives them a name (which must be a valid identifier)
  * They can be initialized (but they can also be "undefined")
  * Multiple variables can be declared in one statement (but only of the same type, which is mentioned only once)
    * In `int i1, i2, i3 = 1;` only `i3` is initialized (if this declaration occurs in a method/constructor/initializer block)
  * Variables are either *local variables*, *method/constructor parameters*, *instance variables* (i.e. *fields*) or *class variables* (i.e. static fields)
  * They can be made "final", which for reference types only *prevents reassignment of the reference itself*
  * *Using uninitialized local variables* leads to compilation errors
    * Having uninitialized local variables is allowed, but using them while still being uninitialized is not
    * Passing a variable as argument to a method also counts as "using" it, so that requires first initializing it (so the method can always assume initialized parameters)
  * Yet *instance variables and class variables* are *automatically initialized* with default values (if not explicitly initialized)
    * For all reference types (e.g. `String`), this default value is `null`
    * Maybe it was not mentioned in chapter 1, but the following is true for final (non-)static fields (see the JLS):
      * *Final static fields* must be explicitly initialized *precisely once*, *during class initialization* (so *all static fields are always initialized during class initialization*)
      * *Final instance fields* must be explicitly initialized *precisely once*, *during object construction* (so *all instance fields are always initialized during object construction*)
  * Local variables can use the *var* syntax, to have the compiler infer the type
  * This is called *local variable type inference*, making clear what it is and is not about
    * In this context, method/constructor parameters are not considered local variables, so *var* cannot be used there (let alone for fields)
  * If no type can be inferred, the "var" declaration is rejected by the compiler; after all, *Java is compile-time type-safe*!
    * E.g., `var x = null;` is rejected by the compiler
  * Only one variable can be initialized in a "var" declaration statement
  * *Scope* of local variables, method/constructor parameters, instance variables (i.e. *fields*) and class variables
    * Each *block* has its own scope for local variables declared in them
    * Instance variables (i.e. instance fields) are available during the entire lifetime of the object containing them
    * Class variables (i.e. static fields) are available for the entire remaining life of the program
    * Note that *static members* of a class/interface cannot access *non-static members* of that class/interface
* *Garbage collection*:
  * Understanding when there are no more references to an object (then the object is *eligible for GC*)

It is also important to have a clear mental picture of:
* *references* (to an object, or null) versus *primitive values*
* *stack* versus *heap* data
* *call-by-value* where the value is either primitive data or references (to an object or null)
* *eligibility for garbage collection*

More information can be found in the
[Java AST API](https://docs.oracle.com/en/java/javase/17/docs/api/jdk.compiler/com/sun/source/tree/package-summary.html),
which contains links to the Java Language specification for specific language constructs.
Note, however, that this AST partly feels more low-level than the "semantic" language constructs
that we know as Java programmers. (The Java AST API looks like the result of early Java parsing "phases",
before annotating the AST with typing info.)

For example, for class declarations, see
[ClassTree](https://docs.oracle.com/en/java/javase/17/docs/api/jdk.compiler/com/sun/source/tree/ClassTree.html),
and for the class members, see for example
[VariableTree](https://docs.oracle.com/en/java/javase/17/docs/api/jdk.compiler/com/sun/source/tree/VariableTree.html) for fields, and
[MethodTree](https://docs.oracle.com/en/java/javase/17/docs/api/jdk.compiler/com/sun/source/tree/MethodTree.html)
for methods and constructors (where the latter have *null* return type),
and follow the links to specific relevant parts of the JLS.
