# Chapter 1

See [OCP Java SE 17 Developer Study Guide](https://www.amazon.com/Oracle-Certified-Professional-Developer-Study/dp/1119864585/ref=sr_1_1?crid=1GIZNHYFXHAK4&dib=eyJ2IjoiMSJ9.Mz5O0lUSaZhUZ-O1Mi__dRPfXHL9GM_CfZ3JDTz910a2d8XI7Vsfj7zwcywJAfMcubfCglH02m8PwlAk_DORk8SS5460zaDP1fskFDX4sUiFVR4pxE1Ln0VIY-g5awTQaOJKp4t0Y1HchXkrw0HtOeVSHg3dHG8Jql9TibGCj-WeXYyNdMp4zWtgM4EimHpl4wvlJZufvGpNjNEmXIObAd2B1mp1skt5k7v_B-k_Ip4.bRERgxl7gsekO5AihUKuOeT_yoO6Bsg7jHigb4sjHEM&dib_tag=se&keywords=ocp+java+se17&qid=1714573695&sprefix=ocp+java+%2Caps%2C192&sr=8-1).

Topics (from this OCP Java SE 17 Developer Study Guide):
* Tools like javac, java etc. (and choosing the Java version)
* Understanding *class structure*:
  * An *object* is also called *instance of a class* (the class is a "template" for construction)
  * Objects are not accessible directly, but only via *references* (or *pointers*, without the dangers associated with them)
  * Primary elements of a class, i.e. class *members*:
    * (instance-level or static) *fields*
    * (instance-level or static) *methods* (the functions of a class or instance)
    * constructors (that create objects/instances)
  * Be careful: constructors look like methods, but without return type
  * Also: unlike fields, methods always have a parameter list, even if it is empty
* *Comments* (mind nesting peculiarities):
  * Single-line comments
  * Multi-line comments (even if on a single line)
  * Javadoc comments
* *Source files* and *classes*:
  * Normal: one top-level type (class) corresponds to one source file
  * The (top-level) type must match the source file's name without the ".java" extension
  * We can have multiple top-level types in a source file, but at most one of them can be public
* A program's *main* method (and how to run the program)
  * Three different "ways" of syntactically passing the main method's parameter(s), including the use of *varargs*
  * Passing program arguments when running programs
* *Package declarations* and *import statements*:
  * Mind order of (optional) package declaration, (optional) import statements, and the top-level type definitions
  * When do we need to import a type, if we do not want to use FQCNs?
  * How can we import types (e.g. via wildcards)?
  * Solving naming conflicts, e.g. via FQCNs
* *Object construction*:
  * Constructor code itself is run after *fields* and *instance initializer blocks*
* *Code blocks*:
  * Code blocks are surrounded by braces
  * Code blocks (i.e. their pairs of braces) must nest properly and braces always come in pairs
* *Primitive types* versus *reference types*:
  * Understand the difference: the value is a primitive value versus a *reference* to an object
  * Values of reference types can be *null*; not so for values of primitive types
  * Primitive values have no methods declared on them
  * Know the primitive types and their wrapper types well
  * Autoboxing and unboxing
  * Also know the corresponding literals well
  * Underscores in numeric literals
  * *Text blocks* and incidental versus essential whitespace
* *Identifiers*:
  * They are used for variables, methods, classes, interfaces and packages (with corresponding naming best practices)
  * But the rules are the same for all of them:
    * Identifiers can contain letters, digits, currency symbols and underscores
    * But they cannot start with a digit, and they cannot be just an underscore
    * And they cannot be *reserved words* (note that "var" is not a reserved word, but a "reserved type name")
    * And they cannot be "word" literals like true, false and null
* *Variables*:
  * They are pieces of memory storing data (primitives or references)
  * Declaring them gives them a name (which must be a valid identifier)
  * They can be initialized (but they can also be "undefined")
  * Multiple variables can be declared (but only of the same type)
  * Variables are either *local variables*, *method/constructor parameters*, *instance variables* (i.e. *fields*) or *class variables* (i.e. static fields)
  * They can be made "final", which for reference types only prevents reassignment of the reference itself
  * Using uninitialized local variables leads to compilation errors
  * Yet instance variables and class variables are automatically initialized with default values
  * Local variables can use the *var* syntax, to have the compiler infer the type
  * If no type can be inferred, the "var" declaration is rejected by the compiler
  * *Scope* of local variables, method/constructor parameters, instance variables (i.e. *fields*) and class variables
* *Garbage collection*:
  * Understanding when there are no more references to an object (then the object is eligible for GC)

It is also important to have a clear mental picture of:
* *object references* versus *primitive values*
* *stack* versus *heap* data
* *call-by-value* where the value is either primitive data or object references
* *eligibility for garbage collection*
