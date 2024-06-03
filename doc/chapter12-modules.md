# Chapter 12. Modules

See [OCP Java SE 17 Developer Study Guide](https://www.amazon.com/Oracle-Certified-Professional-Developer-Study/dp/1119864585/ref=sr_1_1?crid=1GIZNHYFXHAK4&dib=eyJ2IjoiMSJ9.Mz5O0lUSaZhUZ-O1Mi__dRPfXHL9GM_CfZ3JDTz910a2d8XI7Vsfj7zwcywJAfMcubfCglH02m8PwlAk_DORk8SS5460zaDP1fskFDX4sUiFVR4pxE1Ln0VIY-g5awTQaOJKp4t0Y1HchXkrw0HtOeVSHg3dHG8Jql9TibGCj-WeXYyNdMp4zWtgM4EimHpl4wvlJZufvGpNjNEmXIObAd2B1mp1skt5k7v_B-k_Ip4.bRERgxl7gsekO5AihUKuOeT_yoO6Bsg7jHigb4sjHEM&dib_tag=se&keywords=ocp+java+se17&qid=1714573695&sprefix=ocp+java+%2Caps%2C192&sr=8-1).

This chapter is about Java *modules*, also known as *JPMS* (Java Platform Module System). JPMS was introduced in Java 9.

More information can be found in the (more low-level)
[Java AST API](https://docs.oracle.com/en/java/javase/17/docs/api/jdk.compiler/com/sun/source/tree/package-summary.html),
which contains links to the Java Language specification for specific language constructs.

In particular, see:
* [ModuleTree](https://docs.oracle.com/en/java/javase/17/docs/api/jdk.compiler/com/sun/source/tree/ModuleTree.html)
* [DirectiveTree](https://docs.oracle.com/en/java/javase/17/docs/api/jdk.compiler/com/sun/source/tree/DirectiveTree.html), and subtypes:
  * [ExportsTree](https://docs.oracle.com/en/java/javase/17/docs/api/jdk.compiler/com/sun/source/tree/ExportsTree.html)
  * [RequiresTree](https://docs.oracle.com/en/java/javase/17/docs/api/jdk.compiler/com/sun/source/tree/RequiresTree.html)
  * [ProvidesTree](https://docs.oracle.com/en/java/javase/17/docs/api/jdk.compiler/com/sun/source/tree/ProvidesTree.html)
  * [UsesTree](https://docs.oracle.com/en/java/javase/17/docs/api/jdk.compiler/com/sun/source/tree/UsesTree.html)
  * [OpensTree](https://docs.oracle.com/en/java/javase/17/docs/api/jdk.compiler/com/sun/source/tree/OpensTree.html)

### Introducing modules

*JPMS* groups code at a high level. It is like a system for "packaging Java packages". Modules provide groups of related
packages that offer a particular set of functionality. This cannot be expressed with the more fine-grained *access modifiers*
that we are familiar with.

JPMS includes:
* A format for module JAR files
* Partitioning of the JDK info modules
* Additional command-line options for Java tools

A *module* is a group of one or more Java packages plus a `module-info.java` file. The contents of that file are known as
the *module declaration*. Via the module declarations the *dependencies* between modules are specified.

The compiled version of `module-info.java` is called the *module descriptor*.

Important note: the term *dependency* in JPMS is different from the same term in for example Apache Maven. Maven dependencies
include a version, and there are no Java language constructs in the JDK for Maven dependencies. As it turns out, JPMS and Maven
(and by extension Gradle etc.) are quite complementary.

Modules are designed to solve the following problems:
* *Better access control*, specifying which Java packages can access other Java packages
* *Clearer dependency management*, enabling Java to already detect a missing JAR during program startup instead of later when it is first accessed
* *Custom Java builds*, creating Java runtimes that contain only the relevant parts of the JDK instead of "everything"
* *Improved security*, because of omitting parts of the JDK that are not used, and therefore also not worrying about vulnerabilities in those parts
* *Improved performance*, again because of using only relevant parts of the JDK, and this leading to improved startup times, for example
* *Unique package enforcement*, because Java ensures that each package comes from only one module (because of modules specifying exposed packages)

### Creating and running a modular program

TODO
