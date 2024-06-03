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
* Partitioning of the JDK into modules
* Additional command-line options for Java tools

A *module* is a group of one or more Java packages plus a `module-info.java` file. The contents of that file are known as
the *module declaration*. Via the module declarations the *dependencies* between modules are specified.

The compiled version of `module-info.java` is called the *module descriptor*.

Important note: the term *dependency* in JPMS is different from the same term in for example Apache Maven. Maven dependencies:
* have *Maven coordinates* that are decoupled from and unaware of Java packages
* include a *version* as one of the Maven coordinates
* have no corresponding Java language constructs in the JDK, so the JDK does not depend on Maven

As it turns out, JPMS and Maven (and by extension Gradle etc.) are quite complementary.

Modules are designed to solve the following problems:
* *Better access control*, specifying which Java packages can access other Java packages
* *Clearer dependency management*, enabling Java to already detect a missing JAR during program startup instead of later when it is first accessed
* *Custom Java builds*, creating Java runtimes that contain only the relevant parts of the JDK instead of "everything"
* *Improved security*, because of omitting parts of the JDK that are not used, and therefore also not worrying about vulnerabilities in those parts
* *Improved performance*, again because of using only relevant parts of the JDK, and this leading to improved startup times, for example
* *Unique package enforcement*, because Java ensures that each package comes from only one module (because of modules specifying exposed packages)

### Creating and running a modular program

Each *module* has exactly one `module-info.java` source file. It must occur at top-level in the source tree, as if it has
the "default package". The module declaration starts with keyword `module`. That keyword is followed by the *module name*,
which follows the naming rules for package names , so they cannot contain any dashes. For example:

```java
module com.test.myproject.dto {
}
```

Below, assume we have the following simplistic directory structure (which we would not use in practice, and certainly not
in Maven or Gradle projects):
* some "top level" directory, from which we run commands to compile code, package modules etc.
* direct subdirectories for the individual modules
* these module subdirectories contain Java sources in that directory as source tree (for packages there are corresponding subtrees, according to the rules for Java package directory trees)
* these module subdirectories also contain the "*.class" files produced by the compiler (also in the appropriate subdirectories following the package structure)
* there is a "mods" subdirectory (which is a sibling directory of the module-specific subdirectories), containing packaged modules

Suppose the "dto" module's Java sources are in package "com.test.myproject.dto". Then we can compile this module as follows:

```shell
javac --module-path mods \
    -d dto \
    dto/com/test/myproject/dto/*.java dto/module-info.java
```

The "--module-path" option is used to locate custom module files. It can be thought of as replacing the "--class-path" option
in a modular program. This option can be abbreviated to "-p". So the following command is equivalent:

```shell
javac -p mods \
    -d dto \
    dto/com/test/myproject/dto/*.java dto/module-info.java
```

After compiling we can package the module as module JAR file:

```shell
jar -cvf mods/com.test.myproject.dto.jar -C dto/ .
```

With options "-cvf" we create a JAR file with the given JAR file name. With option "-C" we specify the "root directory"
to copy the JAR file contents from. Don't forget the "." at the end, to specify that we want to copy all files from that
"root directory" into the JAR file. Also note that the name of the JAR file matches the module name exactly.

Suppose we have another module called "com.test.myproject.console", with the sources under the "console" subdirectory of
the current directory. Assume we have Java program "com.test.myproject.console.MyTask" in that source tree.
Also assume we have compiled and packaged the module, as described above, but for the module mentioned here. Then we can
run the "MyTask" program as follows:

```shell
java --module-path mods \
    --module com.test.myproject.console/com.test.myproject.console.MyTask
```

So the module to run has format `module-name/fully-qualified-class-name`.

We can abbreviate option "--module" to "-m", so we could more briefly achieve the same as follows:

```shell
java -p mods \
    -m com.test.myproject.console/com.test.myproject.console.MyTask
```

In reality, we would use a build tool instead of entering these commands, but we still need to know how to do that.
So let's assume that after each time we touch any module's source code, we rerun compilation and packaging as shown above.

Let's now turn our attention to the module declaration again.
