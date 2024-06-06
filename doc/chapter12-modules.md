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

A good explanation of Java 9 modules is [understanding-java-9-modules](https://www.oracle.com/corporate/features/understanding-java-9-modules.html).

An official specification of (packages and) modules can be found in chapter 7 of the Java Language Specification, at
[Packages and Modules](https://docs.oracle.com/javase/specs/jls/se17/html/jls-7.html).

### Introducing modules

*JPMS* groups code at a high level. It is like a system for "packaging Java packages". Modules provide groups of related
packages that offer a particular set of functionality. This cannot be expressed with the more fine-grained *access modifiers*
that we are familiar with.

Put differently, before Java 9 individual *classes* could be designed to offer *strong encapsulation*. With modules,
*strong encapsulation can be enforced at the level of Java packages*.

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

Each *module* has exactly one `module-info.java` source file (this is not entirely accurate, but we'll get back to that later).
It must occur at top-level in the source tree, in the "default package" directory. The module declaration starts with keyword
`module`. That keyword is followed by the *module name*, which follows the naming rules for package names , so they cannot
contain any dashes. For example:

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
"root directory" into the JAR file. Also note that the name of the JAR file has been chosen to match the module name exactly,
although that is not required.

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

In reality, we would use a build tool instead of entering these commands, but we still need to know how to achieve the
same without the help of build tools. So let's assume that after each time we touch any module's source code, we rerun
compilation and packaging as shown above, so that we don't have to mention that anymore.

### Updating our example for multiple modules

Let's now turn our attention to the module declaration again.

The `module-info.java` file of the "com.test.myproject.dto" module is adapted to make the "com.test.myproject.dto" Java
package available to other modules. In other words, we *export* that package:

```java
module com.test.myproject.dto {
    exports com.test.myproject.dto;
}
```

Mind the semicolon at the end. Also mind keyword `exports` and not "export".

A "com.test.myproject.dao" module may *require* the "com.test.myproject.dto" module, in particular the exported packages
of the latter. It may also expose its own abstract API. This would lead to a `module-info.java` file of the
"com.test.myproject.dao" module that looks much like the following:

```java
module com.test.myproject.dao {
    exports com.test.myproject.dao;

    requires com.test.myproject.dto;
}
```

A "com.test.myproject.service" module may then have a `module-info.java` content like this:

```java
module com.test.myproject.service {
    exports com.test.myproject.service;

    requires com.test.myproject.dto;
    requires com.test.myproject.dao;
}
```

Finally, a "com.test.myproject.web" module may require the modules above but not expose anything itself:

```java
module com.test.myproject.web {
    requires com.test.myproject.dto;
    requires com.test.myproject.dao;
    requires com.test.myproject.service;
}
```

### Diving into the module declaration

In a module declaration, the *order of directives is irrelevant* and can freely be chosen.

What does *exporting a package* mean exactly? It means that all *public top-level classes/interfaces/enums/records* of that
package are *visible to other modules*. Any *public and protected members* of those top-level type declarations are *visible*
to code in other modules (assuming that the export was to all other modules).

Hence, this does not change anything w.r.t. the *access modifiers*. It only *adds extra restrictions/encapsulation at the
level of packages* and their visibility *across module boundaries*.

In other words, given a `public` top-level class/interface declaration and its containing package:
* `protected` members of that class/interface:
  * *within their module*: are available only within same package or to subclasses, as we already know
  * *outside their module*: are available to subclasses only *if the package is exported*
* `public` members of that class/interface:
  * *within their module*: are available to all classes, as we already know
  * *outside their module*: are accessible only *if the package is exported*

Obviously `private` and package-private members are not accessible to other modules.

Packages can be exported to specific modules, with syntax `exports <some-package> to <some-module>`.

A `requires` directive specifies *both a compile-time and runtime dependency on a given module*.
(A `requires static` directive specifies only a compile-time dependency on a module.)

A `requires transitive <moduleName>` directive offers *implied readability*. That is, each module depending on this module
will automatically also depend on module `<moduleName>`. This `requires transitive` directive often makes sense, especially
when we know that compilation will not succeed for some module if it does not explicitly require module `<moduleName>` as
well. In that case it's better to use `requires transitive`, to prevent other modules from having to explicitly require
that module `<moduleName>`.

This also cleans up an earlier example given above significantly. It now becomes:

```java
module com.test.myproject.dto {
    exports com.test.myproject.dto;
}
```

```java
module com.test.myproject.dao {
    exports com.test.myproject.dao;

    requires transitive com.test.myproject.dto;
}
```

```java
module com.test.myproject.service {
    exports com.test.myproject.service;

    requires transitive com.test.myproject.dao;
}
```

```java
module com.test.myproject.web {
    requires com.test.myproject.service;
}
```

It's not an error to still explicitly require module "com.test.myproject.dto" in all other modules, but there is no need
to do so. It is an error, though, to mix `requires <moduleName>` and `requires transitive <moduleName>`, because Java does
not allow us to repeat the same module in a `requires` clause. For example, the following does not compile:

```java
module bad.module {
    requires com.test.myproject.dto;
    requires transitive com.test.myproject.dto; // causes compilation error
}
```

Sometimes we want to allow other modules to *only have runtime access and not compile-time access* to certain packages,
typically to use *Java reflection* on code in those packages. For this we need to *open the package* for only runtime access
in other modules rather than both compile-time and runtime access, which "exporting" would do.

Opening packages is done in one of 3 ways:
* directive `opens <packageName>`
* directive `opens <packageName> to <moduleName>`
* opening the entire module: `open module <thisModule>`, opening all packages for reflection

Again, *opening* a package or the entire module *only gives runtime access*.

In a way, "opening" a package is more dangerous and powerful than "exporting" a package. When *exporting* a package,
reflective access cannot circumvent the guarantees made by *access modifiers*. So private members of exported "package
members" cannot be reflected on. On the other hand, when *opening* a package, even private members of non-public "package
members" can be reflected on (which is needed by many commonly used open source Java libraries). See the Java Language
Specification, in particular [7.7.2. Exported and Opened Packages](https://docs.oracle.com/javase/specs/jls/se9/html/jls-7.html#jls-7.7.2).

Note the 2 different keywords `opens` and `open`. It is a (compilation) error to use `open` and `opens` together.

By the way, the keywords in module declarations are so-called *restricted keywords*. They are keywords only in module
declarations, and can be used as identifiers elsewhere in the code base.

### Creating a service

Consider a neatly *layered* application where Java modules are used to separate the layers, and to enforce an
*acyclic directed module dependency graph*. It would be useful if layers depended only on *public APIs* and not on
"service implementation" classes. The latter can be achieved with what the Java platform calls *services*.

Let's consider a very simple example of a *service* that returns a "quote of the day". The *public API* consists of:
* A `Quote` record as the *model* ("DTOs", or data transfer objects)
* A `QuoteService` *interface* as the *service* API, without implementation; it depends on the `Quote` record type
* Code to *look up* the `QuoteService`, without knowing anything about the implementation

The implementation of the service is "hidden", but found by Java, as shown below.

Let's have the following *modules* in this example:
* `quote.service` containing and exporting the `QuoteService` API and *model* types, in this case the `Quote` record
* `quote.servicelocator`, for looking up the quote service, without exposing any service implementations
* `quote.serviceimpl`, containing the implementation of the quote service
* `quote.console`, containing a console program to lookup quotes

The `quote.service` module contains the following code, in subdirectory "quote/service" of the source tree:

```java
package quote.service;

public record Quote(String attributedTo, String text) { }
```

```java
package quote.service;

public interface QuoteService {
    Quote findQuoteOfTheDay(); // recall that "public" and "abstract" are implied
}
```

The module contains the following straightforward `module-info.java`, in the root of the source tree:

```java
module quote.service {
    exports quote.service;
}
```

Having compiled and deployed this module, we turn our attention to the module to locate the service:

```java
package quote.service.locator;

import java.util.ServiceLoader;
import quote.service.QuoteService;

public class QuoteServiceFinder {

    public static QuoteService findQuoteService() {
        ServiceLoader<QuoteService> loader = ServiceLoader.load(QuoteService.class);
        for (QuoteService service : loader) {
            return service;
        }
        return null;
    }
}
```

Generic type `java.util.ServiceLoader` is provided by the Java platform. Some things we need to know about this class are:
* Class `java.util.ServiceLoader<T>` can locate services of type `T`, where the latter type is an *interface* or *abstract class* (a concrete class is allowed but not recommended)
* Type `java.util.ServiceLoader<T>` extends `java.lang.Iterable<T>`, so we can use it in an *enhanced for-loop* to iterate over the service implementations
* One static method we need to know is `public static <S> ServiceLoader<S> load(Class<S> service)`
* One instance method we need to know is `public Stream<ServiceLoader.Provider<S>> stream()`
* Type `ServiceLoader.Provider<S>` extends `Supplier<S>`, so it has instance method `public S get()` to obtain the service

This "locator" module contains the following `module-info.java`, in the root of the source tree:

```java
module quote.servicelocator {
    exports quote.service.locator;
    requires transitive quote.service;
    uses quote.service.QuoteService;
}
```

Note the `uses <serviceInterface>` directive, making explicit to the Java module system that service type `QuoteService`
is referenced. Without it, lookup would not work. The use of a `requires` directive does not achieve that, although it is
still needed in order to have access to the `QuoteService` API. So `requires` is needed for successful compilation, and
`uses` is needed for successful service lookup.

If this module is also compiled and deployed, we do not have to compile and deploy again after creating and deploying
implementations of the service!

Let's turn to module `quote.console` to use the service, even without yet having an implementation.

Suppose we use the service in package `quote.console`, via the lookup code in module `quote.servicelocator`. Then the
"console" module may contain the following straightforward `module-info.java`, in the root of the source tree:

```java
module quote.console {
    requires quote.service;
    requires quote.servicelocator;
}
```

That is, this module needs no more than the modules making up the public service API. It also exports nothing.

Let's turn to the `quote.serviceimpl` module, containing the *service provider*.

Suppose the service implementation of interface `QuoteService` is in package `quote.service.impl`. Then the
service provider module may contain the following `module-info.java`, in the root of the source tree:

```java
module quote.serviceimpl {
    requires quote.service;
    provides quote.service.QuoteService with quote.service.impl.QuoteServiceImpl;
}
```

As intended, this module does not export the service implementation. It is a *service provider* because of the `provides`
directive, mentioning the service API and the provided implementation of that service API. This module depends on the
service API module, but not on the lookup part of the public API. More importantly, this module declaration tells the
module system that it provides the given implementation of the `QuoteService` service API. If we deploy this module, this
service implementation will be found by the service lookup code in module `quote.servicelocator`.

Note that this arrangement allows for *application layers* to depend as much as possible on *public APIs* rather than
implementations, thus offering *loose coupling* between layers while enforcing a layered architecture.

In summary:
* The service provider interface:
  * is part of the service's public API
  * and requires directives `exports`
* The service provider (i.e. service implementation):
  * is NOT part of the service's public API
  * and requires directives `requires` and `provides`
* The service locator:
  * is part of the service's public API
  * and requires directives `exports`, `requires` and `uses`
* The service consumers:
  * are obviously NOT part of the service's public API
  * and require directive `requires`

Summarizing the directives themselves:
* Making packages available outside this module:
  * `exports <package>`
  * `exports <package> to <module>`
* Specifying a dependency on another module:
  * `requires <module>`
  * `requires transitive <module>`
* Allowing a package to be used at runtime with reflection, even giving reflective access to private members of non-public package members:
  * `opens <package>`
  * `opens <package> to <module>`
* Making a service implementation available:
  * `provides <serviceInterface> with <serviceImplementation>`
* Referencing a service:
  * `uses <serviceInterface>`

### Discovering modules

To get a feel for the available modules in the JDK itself, run `java --list-modules`.
To exclude the APIs that are internal to the JDK, run `java --list-modules | grep "java\."`.

To get a description of a module, in particular "java.base" which is always available, run
`java --describe-module java.base`.

We need to be able to recognize most of the output of these specific commands.

There are several commands we need to be able to use. In particular:
* Compiling non-modular code:
  * `javac --class-path <classpath> -d <outputDirectory> <classesToCompile>`
  * abbreviated: `javac -classpath <classpath> -d <outputDirectory> <classesToCompile>`
  * abbreviated: `javac -cp <classpath> -d <outputDirectory> <classesToCompile>`
* Running non-modular code:
  * `java --class-path <classpath> <fullyQualifiedClassName>`
  * abbreviated: `java -classpath <classpath> <fullyQualifiedClassName>`
  * abbreviated: `java -cp <classpath> <fullyQualifiedClassName>`
* Compile a module (where "the classes to compile" must include the `module-info.java` source file):
  * `javac --module-path <moduleFolderName> -d <outputDirectory> <classesToCompile>`
  * abbreviated: `javac -p <moduleFolderName> -d <outputDirectory> <classesToCompile>`
* Run a module:
  * `java --module-path <moduleFolderName> --module <moduleName/fullyQualifiedClassName>`
  * abbreviated: `java -p <moduleFolderName> -m <moduleName/fullyQualifiedClassName>`
* Describe a module (note that `-d` means something else entirely than for `javac`):
  * `java --module-path <moduleFolderName> --describe-module <moduleName>`
  * abbreviated: `java -p <moduleFolderName> -d <moduleName>`
  * `jar --file <jarName> --describe-module`
  * abbreviated: `jar -f <jarName> -d`
* List available modules:
  * `java --module-path <moduleFolderName> --list-modules`
  * abbreviated: `java -p <moduleFolderName> --list-modules`
  * listing only JDK modules: `java --list-modules`
* View dependencies (note that it is `-summary` with only one dash in front):
  * `jdeps -summary --module-path <moduleFolderName> <jarName>`
  * somewhat abbreviated: `jdeps -s --module-path <moduleFolderName> <jarName>`
  * `jdeps --jdk-internals <jarName>`
  * `jdeps -jdkinternals <jarName>`
* Show module resolution while starting to run a program:
  * `java --show-module-resolution --module-path <moduleFolderName> --module <moduleName/fullyQualifiedClassName>`
  * abbreviated: `java --show-module-resolution -p <moduleFolderName> -m <moduleName/fullyQualifiedClassName>`
* Create (smaller) runtime JAR:
  * `jlink --module-path <moduleFolderName> --add-modules <moduleName> --output <outputAppName>`
  * abbreviated: `jlink -p <moduleFolderName> --add-modules <moduleName> --output <outputAppName>`

Command-line options for `javac` we need to know:
* Location of JARs in non-modular program: `-cp <classpath>`, `-classpath <classpath>`, `--class-path <classpath>`
* Compiler output directory, for the generated class files: `-d <outputDir>`
* Location of JARs in modular program: `--module-path <path>`, `-p <path>`

Command-line options for `java` we need to know:
* Location of JARs in modular program: `--module-path <path>`, `-p <path>`
* Module-specific fully qualified class name to run: `--module <moduleName/fullyQualifiedClassName>`, `-m <moduleName/fullyQualifiedClassName>`
* Describing details of a module: `--describe-module`, `-d`
* Listing observable modules without running a program: `--list-modules`
* Showing modules when running a program: `--show-module-resolution`

Command-line options for `jar` we need to know:
* Creating a new JAR: `--create`, `-c`
* Prints details when working with JAR files: `--verbose`, `-v`
* JAR file name: `--file`, `-f`
* Directory containing the files to be used to create a JAR file: `-C`
* Describing details of a module: `--describe-module`, `-d`

Command-line options for `jdeps` we need to know:
* Location of JARs in modular program (no abbreviation): `--module-path <path>`
* Summarizes output: `-summary` (note the single dash), `-s`
* Lists uses of internal APIs: `--jdk-internals`, `-jdkinternals`

Command-line options for `jlink` we need to know:
* Location of JARs in modular program: `--module-path <path>`, `-p <path>`
* List of modules to package: `--add-modules`
* Name of output directory: `--output`

There is also a `jmod` command to create another file format than JAR if that is unavoidable, like when having native libraries.

### Comparing types of modules

So far we have considered only so-called *named modules*. There are 3 types of *modules*:
* *Named modules*:
  * appear on the *module path* (not on the classpath)
  * and contain a `module-info.java` file, where the name inside that file is the name of the module
* *Automatic modules*:
  * appear on the *module path* (not on the classpath)
  * and do NOT contain a `module-info.java` file
  * instead, the name of the automatic module is *automatically determined*
  * *all packages* are exported
* *Unnamed modules*, which is a bit of a misnomer:
  * appear on the *classpath* (not on the module path)
  * it is irrelevant whether there is a `module-info.java` file or not; if there is one, it is ignored

The algorithm for determining the name of an *automatic module* is as follows:
1. If the "META-INF/MANIFEST.MF" file specifies an "Automatic-Module-Name", that name is used. Otherwise, proceed with the rules below.
2. Remove the file extension from the JAR file name.
3. Remove any version information (such as "-1.0.0" or "-1.0-RC) from the end of the name.
4. Replace any remaining characters other than letters and numbers with dots.
5. Replace any sequence of dots with a single dot.
6. Remove the dot if it is the first or last character of the result.

Important note:
* Code on the *classpath* can access the *module path*
* But conversely, code on the *module path* CAN NOT access the *classpath*

### Migrating an application

To migrate an application to the use of modules, first we need to know the structure of packages and libraries.
So first draw a *directed dependency graph*, with arrows from "projects" that requires a dependency to "projects" that
make the dependency available. So conceptually the arrows go from `requires <A>` to `exports <A>` (had we finished the migration).

Depending on the result of that exercise, and on who owns what part of the code base and its dependencies, choose a
*migration strategy*, either *bottom-up* or else *top-down*.

In a *bottom-up* migration strategy (this is preferred if feasible) already migrated projects live on the *module path*
and the still-to-migrate projects are still on the classpath. In this approach there is *no pollution* of the module path.

In a *top-down* migration strategy (typically needed if we do not own all code) first all projects are moved to the module
path as *automatic modules*. Then from top to bottom those automatic modules are turned into *named modules*.

JPMS will detect *circular dependencies* between modules and will *not allow it*. That's much of the point of a module
system in the first place. Often a circular dependency can be solved by introducing *another module with shared code*.
