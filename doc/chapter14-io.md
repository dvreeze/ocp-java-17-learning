# Chapter 14. I/O

See [OCP Java SE 17 Developer Study Guide](https://www.amazon.com/Oracle-Certified-Professional-Developer-Study/dp/1119864585/ref=sr_1_1?crid=1GIZNHYFXHAK4&dib=eyJ2IjoiMSJ9.Mz5O0lUSaZhUZ-O1Mi__dRPfXHL9GM_CfZ3JDTz910a2d8XI7Vsfj7zwcywJAfMcubfCglH02m8PwlAk_DORk8SS5460zaDP1fskFDX4sUiFVR4pxE1Ln0VIY-g5awTQaOJKp4t0Y1HchXkrw0HtOeVSHg3dHG8Jql9TibGCj-WeXYyNdMp4zWtgM4EimHpl4wvlJZufvGpNjNEmXIObAd2B1mp1skt5k7v_B-k_Ip4.bRERgxl7gsekO5AihUKuOeT_yoO6Bsg7jHigb4sjHEM&dib_tag=se&keywords=ocp+java+se17&qid=1714573695&sprefix=ocp+java+%2Caps%2C192&sr=8-1).

This chapter is about *I/O* in Java, using an older and newer (*NIO.2*) standard API for I/O.

### Referencing files and directories

#### Conceptualizing the file system

Data can be stored on *persistent storage* such as hard disks and memory cards. A *file* within the storage device holds
data. Files are organized into hierarchies using directories. A *directory* is a location that can hold files as well as
other directories. In Java, we often treat files and directories both as "files", so when this chapter speaks of files
it may mean files or directories.

The *file system* gives us access to files on a computer. Different operating systems (e.g. Linux, Windows) use different
file systems, but the JVM abstracts away many of those differences.

The *root directory* is the topmost directory in the file system. On Windows it could be "C:\\" (on drive "C"), and on
Linux it would probably be a single forward slash, so "/".

A *path* represents a file or directory within the file system. A path is given as a sequence of *path entries* separated
by a *path separator*, which is a backslash on Windows and a forward slash on Linux. Typically, all path entries but the
last one represent directories. Consider path `/home/jane/test.xml`. File `test.xml` is in directory `/home/jane`, which
is the *parent directory* of `test.xml`. Directory `jane` is in directory `/home`, which is the parent directory of
directory `jane`.

Java returns the file separator with a system property:

```java
System.out.println(System.getProperty("file.separator")); // On Linux, it returns a forward slash
```

Paths of files or directories can be *absolute* or *relative*. Absolute paths start with the root directory, such as
"C:\\" on Windows and "/" on Linux. Paths that do not start with a forward slash or drive letter are *relative paths*.
Relative paths can be turned into absolute paths by taking the *current working directory* into account.

Absolute and relative paths can contain *path symbols*:
* `.` refers to the current directory
* `..` refers to the parent directory of the current directory

Some paths can be *normalized*. For example, absolute path `/fish/clownfish/../shark/./swim.txt` can be simplified to
`/fish/shark/swim.txt` (example taken from the book).

A *symbolic link* (or "soft link") is a special file pointing to another file or directory. Symbolic links are automatically
followed when using them in paths, as if they are not links but the "real" file or directory. The "legacy" Java I/O
APIs do not follow them, but NIO.2 has full support for them.

Important to remember for the exam: Although typically regular files have a file extension and directories do not, this
is not something to assume!

#### Creating a File or Path

A path to a file or directory can be created in Java with the old I/O API (using class `java.io.File`) or the NIO.2 API
(using *interface* `java.nio.file.Path`). These types do not support reading from or writing to files; they only represent
paths in the file system.

Below 3 different `java.io.File` constructors are shown, to create a `File` that points to the same location in the file
system:

```java
import java.io.File;

// Constructor File(String)
File file1 = new File("/home/jane/xml/test.xml");

// Constructor File(String, String)
File file2 = new File("/home/jane", "xml/test.xml");

File parent = new File("/home/jane");
// Constructor File(File, String)
File file3 = new File(parent, "xml/test.xml");

// Passing parent null (and absolute path as second constructor argument
File file4 = new File((File) null, "/home/jane/xml/test.xml");

System.out.println("File file1 exists: " + file1.exists());
```

Indeed, class `File` is used both for files and directories, and both for absolute and relative paths, and both for existing
and non-existing files/directories.

Using the NIO.2 API, `java.nio.file.Path` is an interface so has no constructor. We can use static methods of interface
`java.nio.file.Path` or of "factory" class `java.nio.file.Paths`, however. All created `Path` instances in the following
code snippet point to the same location on the file system (whether the file exists or not):

```java
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

// Note that all these paths start with a path entry that is an absolute path
Path path1 = Path.of("/home/jane/xml/test.xml");
Path path2 = Path.of("/home/jane", "xml/test.xml");
Path path3 = Path.of("/home", "jane", "xml", "test.xml");

Path path4 = Paths.get("/home/jane/xml/test.xml");
Path path5 = Paths.get("/home/jane", "xml/test.xml");
Path path6 = Paths.get("/home", "jane", "xml", "test.xml");

System.out.println("Path path1 exists: " + Files.exists(path1));
```

Just like class `java.io.File`, interface `java.nio.file.Path` is used both for files and directories, and both for absolute
and relative paths, and both for existing and non-existing files/directories.

It is also possible to take a `java.net.URI` as input when constructing a `java.io.File` or `java.nio.file.Path`.
In the old I/O API this is supported by `File` constructor `File(URI)`. In NIO.2 it is supported by factory methods
`Path.of(URI)` and `Paths.get(URI)`.

Converting paths between old and new API is easy:
* Class `File` has instance method `toPath()`
* Interface `Path` has instance method `toFile()`

The NIO.2 `Path` interface is much richer than the old `File` class. For example, NIO.2 knows about file systems, and
supports symbolic links. Prefer using NIO.2 whenever feasible.

NIO.2 has *abstract class* `java.nio.file.FileSystem`, and "factory class" `java.nio.file.FileSystems`.
The default `FileSystem` can be obtained with static method `FileSystems.getDefault()`.

Methods `Path.of` and `Paths.get` are shortcuts for `FileSystem` instance methods, such as instance method
`FileSystem.getPath(String, String...)`. This is shown in the following code snippet:

```java
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;

Path path1 = Path.of("/home/jane/xml/test.xml");

FileSystem fileSystem = FileSystems.getDefault();
Path path2 = fileSystem.getPath("/home/jane/xml/test.xml");

System.out.println(path1.equals(path2)); // Prints true
```

Note that NIO.2 makes extensive use of the following pattern:
* Offering an *interface or abstract class* for `Path`, `FileSystem` etc.
  * The interface/abstract class names are "singular" names
* Offering *factory classes with static factory methods* for creating these objects, such as `Paths`, `FileSystems` etc.
  * The factory class names are "plural" names

There is also class `java.nio.file.Files` (note the name in plural again) with *static methods* to interact with the file
system through static methods that take and return `java.nio.file.Path` instances.

### Operating on File and Path

#### Using shared functionality

There are many commonalities between the old `java.io.File` API on the one hand and the NIO.2 `java.nio.file.Path` and
`java.nio.file.Files` APIs on the other hand.

##### Operations on File and Path

Common `java.io.File` and `java.nio.file.Path` operations:

| Description                    | `File` instance method     | `Path` instance method  | Remarks                           |
|--------------------------------|----------------------------|-------------------------|-----------------------------------|
| Gets the file/directory name   | `String getName()`         | `Path getFileName()`    |                                   |
| Retrieves parent directory     | `File getParentFile()`     | `Path getParent()`      | Both return `null` <br/>otherwise |
| Checks if the path is absolute | `boolean isAbsolute()`     | `boolean isAbsolute()`  |                                   |
| Get absolute path              | `String getAbsolutePath()` | `Path toAbsolutePath()` |                                   |

##### Operations on File and Files

Common `java.io.File` *instance methods* and `java.nio.file.Files` *static methods*:

| Description                 | `File` instance method   | `Files` static method                               |
|-----------------------------|--------------------------|-----------------------------------------------------|
| Deletes file/directory      | `boolean delete()`       | `boolean deleteIfExists(Path)`                      |
| Checks existence            | `boolean exists()`       | `boolean exists(Path, LinkOption...)`               |
| Checks if path is directory | `boolean isDirectory()`  | `boolean isDirectory(Path, LinkOption...)`          |
| Checks if path is file      | `boolean isFile()`       | `boolean isRegularFile(Path, LinkOption...)`        |
| Returns last modified time  | `long lastModified()`    | `FileTime getLastModifiedTime(Path, LinkOption...)` |
| Gets number of bytes        | `long length()`          | `long size(Path)`                                   |
| Lists directory contents    | `File[] listFiles()`     | `Stream<Path> list(Path)`                           |
| Creates directory           | `boolean mkdir()`        | `Path createDirectory(Path, FileAttribute...)`      |
| Creates directories         | `boolean mkdirs()`       | `Path createDirectories(Path, FileAttribute...)`    |
| Renames file/directory      | `boolean renameTo(File)` | `Path move(Path, Path, CopyOption...)`              |

Static method `Files.deleteIfExists(Path)` can throw a checked `java.io.Exception`. This is also true for methods
`getLastModifiedTime`, `size`, `list`, `createDirectory`, `createDirectories` and `move`.

Methods `File.mkdirs` and `Files.createDirectories` also create non-existing parent directories.

Method `Path.move` is not only used for renaming, but also for moving files.

Note that NIO.2 is not only more powerful and "strict" than the old I/O API, but it is also cleaner, by making a
distinction between paths (`Path`) and file operations involving paths. For example, creating a directory gets
a path as parameter, unlike method `File.mkdir`, which suggests that a path knows how to create a directory.

When creating code using NIO.2, unlike equivalent code using the old I/O API, it is highly likely that a checked
`java.io.IOException` must be handled or declared.

Consider method `Stream<Path> list(Path)`. This *Stream* is backed by a resource (a connection to the file system), so
must be properly closed, unlike streams that work only with in-memory data without any underlying resources that must be
closed. Interface `Stream<T>` extends `java.lang.AutoCloseable`, so it can be used in a try-resources statement. For example:

```java
// Easy way to get the current working directory
// This is indeed a directory and not a regular file
// Otherwise a checked NotDirectoryException is thrown (extends IOException)
Path path = Path.of("").toAbsolutePath();

try (Stream<Path> childFileStream = Files.list(path)) {
    childFileStream.forEach(p -> System.out.println(p.getFileName()));
}
```

#### Handling methods that declare IOException

Typically, if a NIO.2 method declares a `java.io.IOException`, it requires the `Path` instances it operates on to exist.

#### Providing NIO.2 optional parameters

Some NIO.2 optional parameters passed in `Files` methods are:

| Enum type            | Implemented interfaces     | Enum value          | Details                                       |
|----------------------|----------------------------|---------------------|-----------------------------------------------|
| `LinkOption`         | `CopyOption`, `LinkOption` | `NOFOLLOW_LINKS`    | Do not follow symbolic links                  |
| `StandardCopyOption` | `CopyOption`               | `ATOMIC_MOVE`       | Move file as atomic operation                 |
|                      |                            | `COPY_ATTRIBUTES`   | Copy existing attributes to new file          |
|                      |                            | `REPLACE_EXISTING`  | Overwrite file if it already exists           |
| `StandardOpenOption` | `OpenOption`               | `APPEND`            | If file is open for write, append to the end  |
|                      |                            | `CREATE`            | Create file if it does not already exist      |
|                      |                            | `CREATE_NEW`        | Create new file, and fail if already existing |
|                      |                            | `READ`              | Open for read access                          |
|                      |                            | `TRUNCATE_EXISTING` | If already open for write, erase file first   |
|                      |                            | `WRITE`             | Open for write access                         |
| `FileVisitOption`    | N/A                        | `FOLLOW_LINKS`      | Follow symbolic links                         |

#### Interacting with NIO.2 Paths

Like `String`, `LocalDateTime` and `File`, `java.nio.file.Path` is *immutable*. That also implies that `Path` operation
results *must be explicitly retrieved* or else they get lost. So just writing `myPath.resolve(dir)` without assigning the
result to a variable does not buy us anything.

`Path` operations can often be *chained*. For example: `myPath.getParent().normalize().toAbsolutePath()`.

To view a `Path`, one option is to use overridden method `toString()`. It is even the only `Path` API method to return a
`String`. Many other `Path` methods return `Path` instances.

We can also use instance methods `getNameCount()` and `getName(int)` (repeatedly) to iterate through the *name elements*
of the `Path`. The index passed to `getName(int)` must be between 0, inclusive, and `getNameCount()`, exclusive.
If it is not, an unchecked `IllegalArgumentException` is thrown.

Method `getName(int)` returns a `Path`, not a string. The *root* of the path is not included in the returned names.
For example:

```java
import java.nio.file.Path;
import java.util.stream.IntStream;

Path currentDir = Path.of("").toAbsolutePath();

List<Path> nameElements =
        IntStream.range(0, currentDir.getNameCount()).mapToObj(currentDir::getName).toList();

nameElements.stream().map(nm -> "Name element: " + nm).forEach(System.out::println);

System.out.println(nameElements.stream().allMatch(nm -> nm.getNameCount() == 1)); // prints true

// The root is not part of the name elements
System.out.println(Path.of("/").getNameCount()); // prints 0
System.out.println(Path.of("/home").getNameCount()); // prints 1
System.out.println(Path.of("/home").getName(0)); // prints "home" (without slash)

var strangePath = Path.of("/home/andre/../jane/../andre/./test.xml");
var normalizedPath = strangePath.normalize(); // equal to Path.of("/home/andre/test.xml")

// Path symbols like ".." are name elements too, so no normalization takes place silently
// This returns [home, andre, .., jane, .., andre, ., test.xml]
var strangePathNames =
        IntStream.range(0, strangePath.getNameCount()).mapToObj(strangePath::getName).toList();
// Only after explicitly normalizing the Path, the path symbols are gone
// This returns [home, andre, test.xml]
var normalizedPathNames =
        IntStream.range(0, normalizedPath.getNameCount()).mapToObj(normalizedPath::getName).toList();
```

### Introducing I/O streams

TODO

### Reading and writing files

TODO

### Serializing data

TODO

### Interacting with users

TODO

### Working with advanced APIs

TODO

### Review of key APIs

TODO
