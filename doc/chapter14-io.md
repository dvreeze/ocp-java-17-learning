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
last one represent directories (and often the last one as well). Consider path `/home/jane/test.xml`. File `test.xml` is
in directory `/home/jane`, which is the *parent directory* of file `test.xml`. Directory `jane` is in directory `/home`,
which is the parent directory of directory `jane`.

Java returns the file separator with a system property:

```java
System.out.println(System.getProperty("file.separator")); // On Linux, it returns a forward slash
```

Paths of files or directories can be *absolute* or *relative*. Absolute paths start with the root directory, such as
"C:\\" on Windows and "/" on Linux. Paths that do not start with a forward slash or drive letter are *relative paths*.
Relative paths can be turned into absolute paths by taking the *current working directory* into account.

Absolute and relative paths can contain *path symbols*:
* `.` at the beginning of a path refers to the current directory
* `..` at the beginning of a path refers to the parent directory of the current directory

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
`java.nio.file.Path` or of "factory" class `java.nio.file.Paths`, however. These (equivalent) methods require at least
one *path entry*:
* Static `Path` method `of(String first, String... more)`
* Static `Paths` method `get(String first, String... more)`

All created `Path` instances in the following code snippet point to the same location on the file system (whether the file
exists or not):

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

// Path creation is quite lenient. These paths are equal to the preceding ones (on my machine)
Path path8 = Paths.get("/", "home", "jane", "xml", "test.xml");
Path path9 = Paths.get("/home", "/jane", "/xml/", "/test.xml");
Path path10 = Paths.get("/", "/home", "/jane", "/xml/", "/test.xml");
```

Just like class `java.io.File`, interface `java.nio.file.Path` is used both for files and directories, and both for absolute
and relative paths, and both for existing and non-existing files/directories. Most `Path` methods seem to know nothing about
existence of files/directories. That's more like the domain of the `java.nio.file.Files` class (delegating to a `FileSystem`
instance).

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

| Description                    | `File` instance method     | `Path` instance method  | Remarks                                       |
|--------------------------------|----------------------------|-------------------------|-----------------------------------------------|
| Gets the file/directory name   | `String getName()`         | `Path getFileName()`    |                                               |
| Retrieves parent directory     | `File getParentFile()`     | `Path getParent()`      | Both return `null` <br/>if there is no parent |
| Checks if the path is absolute | `boolean isAbsolute()`     | `boolean isAbsolute()`  |                                               |
| Get absolute path              | `String getAbsolutePath()` | `Path toAbsolutePath()` |                                               |

##### Operations on File and Files

Common `java.io.File` *instance methods* and `java.nio.file.Files` *static methods*:

| Description                 | `File` instance method   | `Files` static method                               |
|-----------------------------|--------------------------|-----------------------------------------------------|
| Deletes file/directory      | `boolean delete()`       | `boolean deleteIfExists(Path)`, `void delete(Path)` |
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
`delete`, `getLastModifiedTime`, `size`, `list`, `createDirectory`, `createDirectories` and `move`, and for many other
methods not mentioned here.

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
// Otherwise a checked NotDirectoryException (which extends IOException) is thrown by method "list"
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
| `LinkOption`         | `CopyOption`, `OpenOption` | `NOFOLLOW_LINKS`    | Do not follow symbolic links                  |
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

With `Path` method `subpath(int, int)` we can retrieve a sub-path. Like `String.substring(int, int)` the start index is
inclusive, the end index is exclusive, and both indices are zero-based. Unlike `String.substring`, method `subpath(int, int)`
does not allow both indices to be the same. The root is not part of the returned `Path`. Wrong index parameters lead to
an `IllegalArgumentException` being thrown. This is an example of using the `subpath` method:

```java
import java.nio.file.Path;

var strangePath = Path.of("/home/andre/../jane/../andre/./test.xml");
System.out.println(strangePath.getRoot() == null); // prints false

var subpath = strangePath.subpath(3, strangePath.getNameCount()); // "jane/../andre/./test.xml"
System.out.println(subpath.getRoot() == null); // prints true

var firstName = strangePath.subpath(0, 1); // "home", without the slash
System.out.println(firstName.getNameCount() == 1); // prints true
System.out.println(firstName.getName(0).equals(Path.of("home"))); // prints true

var strangePathAgain = Path.of("/").resolve(strangePath.subpath(0, strangePath.getNameCount()));
// This returns true
System.out.println(strangePath.equals(strangePathAgain));
```

So methods `getName(int)` and `subpath(int, int)` return `Path` instances *without root*. The original path may or may
not have a root, so may be absolute or not. Yet the paths returned by `getName` and `subpath` are all relative. These
2 methods treat all paths as if they are all relative, which is logical and predictable, because the root is not a "name".

Interface `Path` also has methods `getFileName()`, `getRoot()` and `getParent()`, all returning a `Path` that can be `null`.
Method `getParent` retains the root, if any. Method `getFileName` returns the last name element, if any, and `null` otherwise.
Method `getParent` is shown in the example below:

```java
import java.nio.file.Path;

public List<Path> getAncestorsOrSelf(Path path) {
    return Stream.iterate(path, Objects::nonNull, Path::getParent).toList();
}

var path1 = Path.of("home/jane/xml/test.xml");
// Returns [home/jane/xml/test.xml, home/jane/xml, home/jane, home]
var ancestors1 = getAncestorsOrSelf(path1);

var path2 = Path.of("/home/jane/xml/test.xml");
// Returns [/home/jane/xml/test.xml, /home/jane/xml, /home/jane, /home, /]
var ancestors2 = getAncestorsOrSelf(path2);

var path3 = Path.of("home/jane/../jane/xml/test.xml");
// Returns the parent paths without interpreting path symbols, so ".." is one of the name elements
var ancestors3 = getAncestorsOrSelf(path3);
```

As we can see, the methods above do not clean up paths with path symbols. The same is true for the next `Path` method:
`resolve(Path)`, returning a `Path`, which is the "concatenation" of this path and the parameter path. For example:

```java
import java.nio.file.Path;

// Returns true, so the ending slash does not change anything
var check = Path.of("home/jane/../jane/").equals(Path.of("home/jane/../jane"));

// First path is relative. Second path is relative.
var path1 = Path.of("home/jane/../jane/");
var path2 = Path.of("xml/test.xml");
// Returns "home/jane/../jane/xml/test.xml"
var path3 = path1.resolve(path2);

// First path is absolute. Second path is relative.
var path4 = Path.of("/home/jane/../jane/");
var path5 = Path.of("xml/test.xml");
// Returns "/home/jane/../jane/xml/test.xml"
var path6 = path4.resolve(path5);

// Returns true, so ignoring the root both paths (as relative paths) are equal
var check2 = path1.subpath(0, path1.getNameCount())
        .equals(path4.subpath(0, path4.getNameCount()));
// Also true
var check3 = path3.subpath(0, path3.getNameCount())
        .equals(path6.subpath(0, path6.getNameCount()));

// Second path is absolute, and is the path that is returned.
var path7 = path4.resolve(Path.of("/home/jim/test.json"));

// Second path is empty, so first path is returned.
var path8 = path1.resolve(Path.of(""));

// The empty path is lost in the result, which is equal to the parameter path
var path9 = Path.of("").resolve(path1);
```

Method `resolve(Path)` has an overloading counterpart `resolve(String)`, which works exactly the same way.

The inverse of path *resolution* is method `relativize(Path)`. How do we get from this path to the parameter path?
That's what method `relativize` returns, as `Path`.

When calling method `relativize(Path)`, *the 2 paths must either both be absolute or both be relative*. If they are absolute
Windows paths, they must start with the same drive letter. An exception will be thrown otherwise. For example:

```java
import java.nio.file.Path;

// Straightforward relativization
var path1 = Path.of("home/jane/../jane/");
var path2 = Path.of("xml/test.xml");
// Returns "home/jane/../jane/xml/test.xml"
var path3 = path1.resolve(path2);

// Returns path2
var path4 = path1.relativize(path3);
// Returns true
var check1 = path1.resolve(path1.relativize(path3)).normalize().equals(path3.normalize());

// Relativization with 2 "unrelated" relative paths
var path5 = Path.of("test.txt");
var path6 = Path.of("json/test.json");
// Returns "../json/test.json"
var path7 = path5.relativize(path6);
// Returns "../../test.txt"
var path8 = path6.relativize(path5);

// Both checks return true
var check2 = path5.resolve(path5.relativize(path6)).normalize().equals(path6.normalize());
var check3 = path6.resolve(path6.relativize(path5)).normalize().equals(path5.normalize());

// Relativization with 2 "unrelated" absolute paths
var path9 = Path.of("/test.txt");
var path10 = Path.of("/user/json/test.json");
// Returns "../user/json/test.json"
var path11 = path9.relativize(path10);
// Returns "../../../test.txt"
var path12 = path10.relativize(path9);

// Both checks return true
var check4 = path9.resolve(path9.relativize(path10)).normalize().equals(path10.normalize());
var check5 = path10.resolve(path10.relativize(path9)).normalize().equals(path9.normalize());
```

On calling method `relativize`, some normalization seems to take place if applicable:

```java
import java.nio.file.Path;
import java.nio.file.Paths;

Path p1 = Paths.get("/personal/./photos/../readme.txt");
Path p2 = Paths.get("/personal/index.html");
Path p3 = p1.relativize(p2); // Returns path "../index.html"
```

Method `normalize()` removes redundancies, in particular path symbols "." and "..", to the extent possible.
So `Path.of("../../test.xml").normalize()` can only return the same path, with the repeated ".." path symbols.

Method `normalize` helps in comparing paths for equality. Paths can better be compared for equality after normalization.

Method `toRealPath(LinkOption...)` is different from other methods in interface `Path`, in that it does inspect existence
of the file. Method `toRealPath(LinkOption...)`:
* Eliminates redundant path symbols, like method `normalize()`
* Resolves the path against the current working directory if the path is relative, like method `toAbsolutePath()`
* It follows symbolic links, unless the parameter `LinkOption` tells the method not to do so
* It throws an exception if the file or directory does not exist on the file system
* It can show the current working directory like this: `Path.of(".").toRealPath()`

Summary of the `Path` *instance methods* seen so far:

| Description                                    | `Path` instance method                              |
|------------------------------------------------|-----------------------------------------------------|
| Returns file path as string                    | `public String toString()`                          |
| Retrieves a single name element                | `public Path getName(int index)`                    |
| Gets the number of name elements               | `public int getNameCount()`                         |
| Retrieves a sub-range of name elements         | `public Path subpath(int beginIndex, int endIndex)` |
| Gets the final name element                    | `public Path getFileName()`                         |
| Gets the immediate parent path, if any         | `public Path getParent()`                           |
| Gets the root                                  | `public Path getRoot()`                             |
| Appends a path                                 | `public Path resolve(Path other)`                   |
|                                                | `public Path resolve(String other)`                 |
| The inverse of resolving paths                 | `public Path relativize(Path other)`                |
| Removes redundant parts of a path              | `public Path normalize()`                           |
| Tries to find the real path on the file system | `public Path toRealPath(LinkOption...)`             |

#### Creating, moving and deleting files and directories

Directories can be created with `java.nio.file.Files` *static methods*:
* `public static Path createDirectory(Path dir, FileAttribute<?>... attrs) throws IOException`
* `public static Path createDirectories(Path dir, FileAttribute<?>... attrs) throws IOException`

The following holds for these methods:
* As the names imply, they create directories, not regular files
* Method `createDirectory` throws an exception if the file/directory already exists, or if the paths leading up to the directory do not exist
* Method `createDirectories` throws an exception if the file exists but not as a directory; otherwise it will create all missing (parent) directories as well
* If all directories exist, method `createDirectories` will do nothing
* File attributes are discussed later in this chapter

Class `java.nio.file.Files` offers the following static methods for *copying* and *moving/renaming* files/directories:
* "Copying" method `public static Path copy(Path source, Path target, CopyOption... options) throws IOException`
* "Moving/renaming" method `public static Path move(Path source, Path target, CopyOption... options) throws IOException`

The following holds for method `copy`:
* It can be used for copying regular files (where source and target are regular files) or directories (where source and target are directories)
* When used for copying directories, the copy is a *shallow copy*; i.e. files/directories within the source directory are not copied
* For deeply copying directories, this method should be called recursively (see below)
* Unlike a Unix `cp` command, the `copy` method does not directly support "copying into an existing directory"; copying source `Path` to target `Path` must be *taken literally*
* So, if you want to copy `regularFilePath` *into* directory `directoryPath`, calling `copy(regularFilePath, directoryPath.resolve(regularFilePath.getFileName()))` would work
* By default, if the target already exists, an exception will be thrown
* This behavior can be changed with "copy option" `StandardCopyOption.REPLACE_EXISTING`

The following holds for method `move`:
* It can be used for moving/renaming regular files (where source and target are regular files) or directories (where source and target are directories)
* Moving and renaming can be combined in one `move` call, but just moving or just renaming is also possible, of course
* When used for moving/renaming directories, the contents of the source directory moves to the target directory and will not be lost
* Like is the case for `copy`, source and target parameters must be *taken literally*
* By default, if the target already exists, an exception will be thrown
* This behavior can be changed with "copy option" `StandardCopyOption.REPLACE_EXISTING`
* An *atomic move* (which is a single indivisible operation in the file system) is achieved with `StandardCopyOption.ATOMIC_MOVE` (or else a checked `AtomicMoveNotSupportedException` is thrown)

Copying a directory tree recursively:

```java
public void copyPath(Path source, Path target) {
    try {
        Files.copy(source, target);
        if (Files.isDirectory(source)) {
            // Symbolic links are not followed
            try (Stream<Path> childStream = Files.list(source)) {
                // Recursion
                childStream.forEach(ch -> copyPath(ch, target.resolve(ch.getFileName())));
            }
        }
    } catch (IOException e) {
        throw new UncheckedIOException(e);
    }
}
```

There are also overloaded `copy` methods:
* `public static long copy(InputStream in, Path target, CopyOption... options) throws IOException`, returning the number of bytes read/written
* `public static long copy(Path source, OutputStream out) throws IOException`, returning the number of bytes read/written

Of course, most parameter I/O streams can only be processed once, but `System.in`, `System.out` etc. are not created/closed by us.

For example, copying a file to `System.out`: 

```java
Files.copy(Path.of("/home/jane/test.xml"), System.out);
```

Class `java.nio.file.Files` offers the following static methods for *deleting* regular files or non-empty directories:
* `public static void delete(Path path) throws IOException`
* `public static boolean deleteIfExists(Path path) throws IOException`

The following holds for these `delete` and `deleteIfExists` methods:
* When trying to delete a directory, both methods expect the *directory to be empty*, or else an exception is thrown
* If the path is a symbolic link, the link itself will be deleted, not the path that the link points to
* If the path does not exist, `delete` throws an exception, whereas `deleteIfExists` returns `false` in that case

#### Comparing files with isSameFile() and mismatch()

Paths may contain path symbols, and they may be symbol links, relative paths etc. So method `Path.equals(Object other)`
is not a reliable way to compare paths for equality.

Static `Files` method `isSameFile(Path, Path)` is more reliable to compare paths of regular files or directories
(resolving relative paths, normalizing paths, following symbolic links, etc.). It can throw an exception if the paths
do not exist. There is one case where this method will not check existence of the files, and that is the case where
both paths are equal according to method `Path.equals(Object other)`.

If we want to compare the *contents* of 2 files, we can use method `mismatch(Path path1, Path path2)` (introduced in Java 12).
Only regular files can be compared with this method. Comparing directories will throw an exception. The method returns
an `int`. It returns `-1` if the files are equal (as physical files or in their contents). Otherwise, the first position
where the file contents differ will be returned (zero-based).

### Introducing I/O streams

*I/O streams* can be used to read or write data. Do not confuse them with *(Java) streams*. They are a completely different
API. I/O streams are in package `java.io`.

#### Understanding I/O stream fundamentals

*I/O streams* can conceptually be thought of as large *streams of water*, with *data presented only one wave at a time*.
Such a "wave" could be a byte, a character, a byte array, a text line, a serialized Java object, etc.

I/O streams can be used with files, web URLs, in-memory data (byte arrays, character arrays, Strings etc.), or other
*sequential data sources*. I/O streams can be *big* without using much memory themselves, because at each time only one
"wave/block" is processed.

#### Learning I/O stream nomenclature

All I/O streams inherit from one of four *abstract base classes*:
* `java.io.InputStream`, which is an input *stream of bytes*
* `java.io.OutputStream`, which is an output stream of bytes
* `java.io.Reader`, which is an input *stream of characters*
* `java.io.Writer`, which is an output stream of characters

Data is stored in a file system and in memory as *8-bit bytes*. So it makes sense that I/O streams can read or write
Java `byte` or `byte[]` values. (Note: primitive type `byte` is signed, but for I/O we ignore that and just regard
the bytes as chunks of 8 bits.) Hence, we have abstract classes `java.io.InputStream` and `java.io.OutputStream`.

Yet often we want to deal with *text data*. Hence, we have abstract classes `java.io.Reader` and `java.io.Writer`.
Whenever converting between bytes and characters (which we also do when using a `Reader` or `Writer`), we need to know
the used *character encoding*. Examples of character encodings are US-ASCII (1 byte character encoding), UTF-8, a whole
suite of "Windows encodings", UTF-16 (taking 2 bytes per character) etc.

A character encoding is represented by *abstract class* `java.nio.charset.Charset` (mind the lowercase letter "s").
Some predefined character set constants can be found in class `java.nio.charset.StandardCharsets`. For example,
`Charset.forName("UTF-8")` is equivalent to `StandardCharsets.UTF_8`.

It is often quite simple to predict the existence of several I/O stream class names. For example, for reading from
a `java.io.File` it is logical that class `java.io.FileInputStream` exists, a constructor of which takes a `File` object.
Knowing this class name, it is easy to predict the existence of 3 other I/O stream classes. So the following makes sense:
* Class `java.io.FileInputStream`, inheriting from `java.io.InputStream`
* Class `java.io.FileOutputStream`, inheriting from `java.io.OutputStream`
* Class `java.io.FileReader`, inheriting from `java.io.Reader` (and using some explicit or default character encoding)
* Class `java.io.FileWriter`, inheriting from `java.io.Writer` (and using some explicit or default character encoding)

Most output stream classes have *corresponding* input stream classes. Exceptions are `java.io.PrintStream` and
`java.io.PrintWriter`. These convenient classes offer writing of formatted representations of Java objects ("printf-style").

*Low-level streams* connect with a "source of data". *High-level streams* are built on top of other I/O streams using wrapping.

Some other *low-level stream* pairs are:
* `java.io.ByteArrayInputStream` and `java.io.ByteArrayOutputStream`, working with byte arrays in memory
* `java.io.StringReader` and `java.io.StringWriter`, working with character strings in memory

Some *high-level stream* pairs are:
* `java.io.BufferedInputStream` and `java.io.BufferedOutputStream`, processing data in a buffered manner, for improved efficiency and performance
* `java.io.BufferedReader` and `java.io.BufferedWriter`, processing data in a buffered manner, for improved efficiency and performance
* `java.io.ObjectInputStream` and `java.io.ObjectOutputStream`, deserializing and serializing Java primitives and Objects

The following code snippet is an example of "stacking" streams:

```java
import java.io.*;

try (var ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(file)))) {
    // Method readObject is specific to ObjectInputStream
    System.out.print(ois.readObject());
}
```

Note that in this example a higher-level `java.io.InputStream` instance wraps another `java.io.InputStream` one (even any
other `java.io.InputStream`). You cannot mix `InputStream` with `Reader` or with `OutputStream`, when wrapping I/O streams.
Also, the first I/O stream in this "I/O stream wrapping chain" must typically be a low-level one, that is connected to
some data source.

### Reading and writing files

#### Using I/O streams

To copy an input stream into an output stream in a loop, we need to know about *low-level read/write methods*. This table
is a summary of those instance methods:

| I/O stream class       | Read/write method                                                     |
|------------------------|-----------------------------------------------------------------------|
| `java.io.InputStream`  | `public abstract int read() throws IOException`                       |
| `java.io.InputStream`  | `public int read(byte[] b) throws IOException`                        |
| `java.io.InputStream`  | `public int read(byte[] b, int off, int len) throws IOException`      |
| `java.io.Reader`       | `public int read() throws IOException`                                |
| `java.io.Reader`       | `public int read(char[] cbuf) throws IOException`                     |
| `java.io.Reader`       | `public int read(char[] cbuf, int off, int len) throws IOException`   |
| `java.io.OutputStream` | `public void write(int b) throws IOException`                         |
| `java.io.OutputStream` | `public void write(byte[] b) throws IOException`                      |
| `java.io.OutputStream` | `public void write(byte[] b, int off, int len) throws IOException`    |
| `java.io.Writer`       | `public void write(int c) throws IOException`                         |
| `java.io.Writer`       | `public void write(char[] cbuf) throws IOException`                   |
| `java.io.Writer`       | `public void write(char[] cbuf, int off, int len) throws IOException` |

The read methods taking a byte array or character array return the number of bytes/characters read, but `-1` if the end
of the stream has been reached, and no data could be read. The read methods without parameters return a byte or char as
`int`, or `-1` if no data could be read. All these read methods *block* until some data was found, EOF was reached, or an
exception was thrown.

Class `java.io.Writer` also has the following (convenient) "write" methods:
* `public void write(String str) throws IOException`
* `public void write(String str, int off, int len) throws IOException`

Moreover, class `java.io.Writer` has the following (convenient) methods:
* `public Writer append(char c) throws IOException`
  * Note that this method takes a `char`, as one would expect, unlike method `write(int)`, which takes an `int` in order to be consistent with `read()`
  * A `Writer` is returned, so different `append` calls can be *chained*
* `public Writer append(CharSequence csq) throws IOException`
  * This method takes a `String` or even any `CharSequence`, which is more convenient than passing `char[]`
  * A `Writer` is returned, so different `append` calls can be *chained*
* `public Writer append(CharSequence csq, int off, int len) throws IOException`
  * This method takes a `String` or even any `CharSequence`, which is more convenient than passing `char[]`
  * A `Writer` is returned, so different `append` calls can be *chained*

Class `java.io.InputStream` also has "read" method `public byte[] readAllBytes() throws IOException`, but it should not be
used for reading large amounts of data.

Copying input streams to output streams using these low-level methods is a bit cumbersome and also not quite efficient.
For example (from the book):

```java
import java.io.*;

public void copy(InputStream in, OutputStream os) throws IOException {
    int b;
    while ((b = in.read()) != -1) {
        out.write(b);
    }
}
```

All `InputStream`, `OutputStream`, `Reader` and `Writer` (sub-)classes inherit from `java.io.Closeable`, so can be used
in a try-resources statement.

Abstract classes `java.io.OutputStream` and `java.io.Writer` also have method `flush()` to "commit" the write(s), but this
may be costly if called too often.

Let's copy a text file to another file in a slightly higher-level way, by using a `BufferedReader` and `PrintWriter`
(example from the book):

```java
import java.io.*;

public void copyTextFile(File src, File dest) throws IOException {
    try (var reader = new BufferedReader(new FileReader(src));
         var writer = new PrintWriter(new FileWriter(dest))) {
        String line;
        while ((line = reader.readLine()) != null) {
            writer.println(line); // This also adds the "lost" newline
        }
    }
}
```

Classes `java.io.PrintStream` and `java.io.PrintWriter` have many overloaded `println` methods. Think of most of these
overloads as calling `println(String)` on the result value of `String.valueOf` called on the parameter. Class `PrintWriter`
should be preferred to `PrintStream` when outputting text characters. Methods in these 2 classes never throw `IOException`
in their methods, but can be queried for the error status using method `checkError()`. This is quite different from
what we are used to from other Java APIs, of course.

The `println` methods in both classes use the `System.lineSeparator()` (or `System.getProperty("line.separator")`) as
line separator, which is "\\n" on Linux and "\\r\\n" on Windows. Other output methods apply some "printf-style" formatting
before writing the output.

#### Enhancing with Files

The NIO.2 `Files` class has a few *static methods* for conveniently reading and writing data. Most of them may potentially
use a lot of memory, but one of them works "in a streaming way". Here is a list:

| Description                      | `Files` static method                                                                                                               |
|----------------------------------|-------------------------------------------------------------------------------------------------------------------------------------|
| Reading all bytes from a Path    | `public static byte[] readAllBytes(Path path) throws IOException`                                                                   |
| Writing byte array to a Path     | `public static Path write(Path path, byte[] bytes, OpenOption... options) throws IOException`                                       |
| Reading all lines from a Path    | `public static List<String> readAllLines(Path path, Charset cs) throws IOException`                                                 |
| Writing lines to a Path          | `public static Path write(Path path, Iterable<? extends CharSequence> lines, Charset cs, OpenOption... options) throws IOException` |
| Reading a String from a Path     | `public static String readString(Path path, Charset cs) throws IOException`                                                         |
| Writing a String to a Path       | `public static Path writeString(Path path, CharSequence csq, Charset cs, OpenOption... options) throws IOException`                 |
| Lazily reading lines from a Path | `public static Stream<String> lines(Path path, Charset cs) throws IOException`                                                      |

The methods taking an extra `Charset` also have overloads not taking that extra parameter for the character encoding.
In those cases, `StandardCharsets.UTF_8` is used.

Class `Files` also has a few static convenience methods to create readers and writers from a `Path`, such as:
* `newBufferedReader(Path, Charset)` and an overload leaving out the character encoding
* `newBufferedWriter(Path, Charset, OpenOption...)` and an overload leaving out the character encoding
* `newInputStream(Path, OpenOption...)`
* `newOutputStream(Path, OpenOption...)`

### Serializing data

There are many ways of serializing Java objects to a file using some serialization format, and deserializing them back
into Java objects again. Such formats include XML, JSON, but also the *Java serialization format*, which is "baked into
the Java platform". Using Java (de)serialization, we can use the Java I/O stream API to serialize and deserialize Java
objects.

No Java class is "automatically" serializable. This is also true for Java *record classes*. The class must be "marked"
as such. To use Java serialization with a Java class, the following is required:
* The class must be marked as serializable, by having the class *implement* the *marker interface* `java.io.Serializable`
* Recursively, each *instance member* of the class must be *serializable*, marked `transient`, or be `null` at the time of serialization

If the superclass of a serializable class is not serializable, *the instance fields of the non-serializable superclass will
not be serialized*.

Normally only *data-oriented* class should be made serializable (if we want to use them with Java serialization).
All Java primitives and many well-known Java classes (such as `String` and collections) are serializable.

*Transient* instance members of a serializable class do not take part in serialization, and get their default values
(such as `null` for objects) on deserialization.

We can (de)serialize classes transient non-serializable fields, however, if we take more explicit control over (de)serialization
using methods like `readObject(ObjectInputStream)` and `writeObject(ObjectOutputStream)`.

For versioning of serializable classes, use static field `private static final long serialVersionUID = 1L;` (where the
constant value is incremented when needed). This "serial version UID" is part of the serialized data, along with the
non-transient serialized instance fields. On deserialization, a checked `java.io.InvalidClassException` may be thrown
(which inherits from `java.io.IOException`). Except for the "serial version UID", static fields take no part in (de)serialization.

Using Java (de)serialization requires the use of a `java.io.ObjectInputStream` and/or `java.io.ObjectOutputStream`. Their
instance methods are:
* `ObjectInputStream` has method `public Object readObject() throws IOException, ClassNotFoundException`
* `ObjectOutputStream` has method `public void writeObject(Object obj) throws IOException`

Writing and reading Java objects could look like this:

```java
import java.io.*;

public record Quote(String attributedTo, String text, List<String> subjects) implements Serializable { }

public void saveQuotes(List<Quote> quotes, File file) throws IOException {
    try (var out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file)))) {
        for (Quote quote : quotes) out.writeObject(quote);
    }
}

public List<Quote> readQuotes(File file) throws IOException, ClassNotFoundException {
    var quotes = new ArrayList<Quote>();
    try (var in = new ObjectInputStream(new BufferedInputStream(new FileInputStream(file)))) {
        // This is quite clunky, but apparently needed
        while (true) {
            var obj = in.readObject();
            if (obj instanceof Quote quote) quotes.add(quote);
        }
    } catch (EOFException e) {
        // File end reached. Method "available()" does not work as EOF check.
    }
    return quotes;
}
```

*Deserialization* is quite an *insecure* process, *bypassing normal construction*. Instead, Java will *call the no-arg
constructor of the first non-serializable ancestor class*. Often that is `java.lang.Object`. After calling that no-arg
constructor of a non-serializable ancestor class, the non-transient instance fields will be filled from the serialized data,
the transient instance fields will get the default value, and static fields are left alone.

Again, on deserialization constructors of serializable classes are not called. Neither are initializers called. Obviously,
this is potentially very unsafe. All *encapsulation* offered by *OO best practices* are *thrown out of the window* with
Java serialization (at least with the techniques presented so far). As far as I know, in practice Java serialization is
not very widely used.

Java serialization also breaks the "S" of *single responsibility* of the *SOLID principles*.

### Interacting with users

Java provides `System.out` and `System.err` as out-of-the-box `java.io.PrintStream` instances. A Java process can redirect
the standard output and standard error channels to different files. Still, the use of a *proper logging solution* is much
better in practice.

Java also provides `System.in`, which is a `java.io.InputStream`. It can be wrapped in an `InputStreamReader`, which
can be wrapped in a `BufferedReader`. Still, do *not close* these 3 streams, so do *not use them in try-resources statements*.

Class `java.io.Console` is designed for user interactions. It cannot be created by us using a constructor. Instead,
obtain the *Console* singleton provided by the Java platform like this (taken from the book):

```java
import java.io.*;

Console console = System.console();
if (console != null) {
    // Use the console
    String userInput = console.readLine();
    console.writer().println("You entered: " + userInput);
} else {
    System.err.println("Console not available");
}
```

In this example, method `readLine()` does a *blocking read* on user input, until the user hits ENTER.
Then method `writer()` returns a `PrintWriter` that can be used to give output to the user.

Some `java.io.Console` instance methods are:
* `reader()`, returning a `java.io.Reader`, that can be used if we need more power than the Console API provides directly
* `writer()`, returning a `java.io.PrintWriter`, that is used to write output to the console
* `readLine()`, doing a *blocking read*, waiting for user input until ENTER is hit (and returning `null` on reaching end of input, resulting from CTRL-D etc.)
* `readLine(String, Object...)`, which is like `readLine()`, but providing a formatted prompt (consistent with method `String.format`)
* `readPassword()` and `readPassword(String, Object...)`, which is like `readLine` but disabling echoing; the return type is `char[]` instead of `String`
* `format(String, Object...)` and equivalent method `printf(String, Object...)`, writing a formatted string to the console's output stream

If we want to call `format` but explicitly provide a `Locale`, we can do that by calling the appropriate formatting method
on the `PrintWriter` obtained through method `writer()`.

### Working with advanced APIs

#### Manipulating input streams

Sometimes we may want to "put some data back" into an input stream, possibly to first "look ahead" and then process
the same data again with more info because of the "look ahead". Input streams may offer this functionality through
the *marking of data*. This is probably implemented as some kind of temporary buffer "proxying the real input stream".

Abstract classes `java.io.InputStream` and `java.io.Reader` have the following methods in the context of marking data:
* `public boolean markSupported()`; this method should be called first before trying to mark data in the input stream
* `public void mark(int readLimit)`; this method demarcates the temporary buffer, starting at the current position in the stream, and having the provided size
  * the provided read limit is in bytes for `InputStream` and in characters for `Reader`
* `public void reset() throws IOException`; this method resets the input stream to the starting point of the "marked region" in the input stream
  * when called outside the "marked region", all bets are off (maybe an exception is thrown, maybe not)
* `public long skip(long n) throws IOException`; this method simply reads the given number of bytes or characters
  * it returns the actual number of bytes (for `InputStream`) or characters (for `Reader`) skipped

#### Discovering file attributes

Recall the following `Files` methods:
* `isDirectory(Path, LinkOption...)`
* `isRegularFile(Path, LinkOption...)`
* `isSymbolicLink(Path)`

If a certain `Path` is a symbolic link pointing to a regular file, methods `isSymbolicLink` and `isRegularFile` will
both return `true` (if no link option was passed to method `isRegularFile` in order not to follow symbolic links).
If the path is a symbolic link pointing to a directory, methods `isSymbolicLink` and `isDirectory` (by default) will
both return `true`. Method `isSymbolicLink` does not look at the file/directory pointed to by the symbolic link; it only
looks at whether the file is a symbolic link or not.

Class `Files` also contains some `boolean` methods that query individual *file attributes*. For example:
* `isHidden(Path)`, potentially throwing an `IOException`
* `isReadable(Path)`
* `isWritable(Path)`
* `isExecutable(Path)`

As an aside, class `Files` also has method `getPosixFilePermissions(Path path, LinkOption... options)`. The resulting
set of `java.nio.file.attribute.PosixFilePermission` instances remind us of the same permissions shown in the output
of Unix command `ls -al`.

If we need to query multiple attributes of multiple files, these fine-grained methods are not the best fit, since too many
interactions with the file system would be needed to retrieve that data. NIO.2 does have a solution for that: a *read-only
attributes* method and an *updatable attributes view* method.

The corresponding "data holder" interfaces are:
* Interface `java.nio.file.attribute.BasicFileAttributes` (we can ignore the OS-specific ones); some methods are:
  * `isDirectory()`, `isRegularFile()`, `isSymbolicLink()`, `isOther()`
  * `creationTime()`, `lastModifiedTime()`, `lastAccessTime()`, all returning a `java.nio.file.attribute.FileTime`
  * `size()`, returning the size in bytes as `long`
* Interface `java.nio.file.attribute.BasicFileAttributeView`, inheriting from `AttributeView` (we can ignore the OS-specific ones); some methods are:
  * `readAttributes()`, returning a `BasicFileAttributes`
  * `setTimes(FileTime, FileTime, FileTime)`, to set lastModifiedTime, lastAccessTime and createTime (possibly `null` in order to leave that parameter alone)

As apparent from the interfaces and their methods above, these interfaces model a basic set of attributes supported by all file
systems.

Class `Files` has the following methods to retrieve "attribute sets":
* `public static <A extends BasicFileAttributes> A readAttributes(Path path, Class<A> type, LinkOption... options) throws IOException`
  * Typically, we would pass `BasicFileAttributes.class` as 2nd argument
* `public static <V extends FileAttributeView> V getFileAttributeView(Path path, Class<V>, LinkOption... options)`
  * Typically, we would pass `FileAttributeView.class` as 2nd argument

Above we have seen how we can use the return types of `readAttributes` and `getFileAttributeView`. With the latter we can
update the lastModifiedTime, for example.

#### Traversing a directory tree

With method `Files.list(Path)` we can traverse the (direct) "children" of a directory. But what if we want to traverse
all "descendants (or self)"? That is called *traversing a directory*, or *walking a directory tree*.

NIO.2 has support for that. First of all, do not use the legacy `DirectoryStream` and `FileVisitor` APIs. Use `Stream`-based
methods in class `Files` instead.

The *NIO.2 Stream API* methods use *depth-first searching* (as opposed to breadth-first searching).

Two straightforward `Files` (static) methods for traversing a directory are:
* `public static Stream<Path> walk(Path start, FileVisitOption... options) throws IOException`
* `public static Stream<Path> walk(Path start, int maxDepth, FileVisitOption... options) throws IOException`

These methods do a *lazy walk* through the returned `Stream<Path>`. It is strongly recommended to use this `Stream`
in a try-resources statement, because the stream is backed by a connection to the file system. Obviously, we have
all the combined power of the `Stream` API and the `Files` API at our disposal when walking a directory tree with these
methods.

These methods do NOT follow symbolic links, unless `FileVisitOption.FOLLOW_LINKS` is passed to the `walk` method.

If we do follow symbolic links, there obviously is the risk of *cycles* in the search. When symbolic links are followed,
the `walk` method will track all visited paths, and throw a `FileSystemLoopException` on encountering a cycle.

There is a more convenient `Files` static method to traverse directory trees, though, namely:
* `public static Stream<Path> find(Path start, int maxDepth, BiPredicate<Path, BasicFileAttributes> matcher, FileVisitOption... options) throws IOException`

This method takes a "maxDepth" as 2nd parameter, and (unlike for method `walk`) there is *no overload* that does not take
this "maxDepth" parameter!

The `BiPredicate<Path, BasicFileAttributes>` parameter gives us access to the `BasicFileAttributes`, as well as the `Path`,
so the file attributes can be queried directly instead of having to call a `Files` method that throws an `IOException`
that we have to wrap in an unchecked exception (such as `UncheckedIOException`). Of course, the stream returned by the
`find` method is "only" a `Stream<Path>`.

### Review of key APIs

The key APIs can be summarized as follows:

| Class/Interface               | Description                                                     |
|-------------------------------|-----------------------------------------------------------------|
| `java.io.File`                | I/O representation of location in file system ("old" API)       |
| `java.nio.file.Files`         | Static helper methods for working with `java.nio.file.Path`     |
| `java.nio.file.Path`          | NIO.2 representation of location in file system                 |
| `java.nio.file.Paths`         | Factory class to get `java.nio.file.Path` instances             |
| `java.net.URI`                | Uniform resource identifier for URLs, files, etc.               |
| `java.nio.file.FileSystem`    | NIO.2 representation of file system                             |
| `java.nio.file.FileSystems`   | Factory class to get `java.nio.file.FileSystem`                 |
| `java.io.InputStream`         | Input stream of bytes                                           |
| `java.io.OutputStream`        | Output stream of bytes                                          |
| `java.io.Reader`              | Input stream of characters                                      |
| `java.io.Writer`              | Output stream of characters                                     |

Also revisit the different kinds of `InputStream`, `OutputStream`, `Reader` and `Writer`.

Note that `java.io.InputStreamReader` "turns an InputStream into a Reader", and that `java.io.FileReader` inherits from
that class. An analogous remark applies to `java.io.OutputStreamWriter` and `java.io.FileWriter`.

Also note that `java.io.BufferedInputStream` inherits from `java.io.FilterInputStream`, and  `java.io.BufferedOutputStream`
inherits from `java.io.FilterOutputStream`.

### Checklist

Some things to be on the outlook for during the exam are:
* NIO.2 is a rather *functional API*, with types like `Path` being *immutable*, so look out for *non-side-effecting methods calls where the result is ignored*
* Many NIO.2 methods declare that they can throw `IOException`, so beware of required *"throws" clauses in method signatures*
* When using a `java.io.OutputStream` or `java.io.Writer` on a file, if method `flush()` has not been called *you cannot assume anything about when exactly the written data is visible in the file*
* Is a type an *interface* or *class*, so do we need keyword `implements` or `extends`? For example, `java.io.Serializable` is an interface

Sometimes we can make a good educated guess if there is something we do not know by heart. For example, I did not know
whether interface `java.nio.file.Path` had a method `toUri()`. I could have guessed that such a method would exist in class
`Path`, because:
* There is a factory method to create a `Path` from a `java.net.URI`, and `Path` has enough information to convert back to `URI`
* Class `java.io.File` is conceptually similar to `Path`, and that class also has methods to convert a ("file" protocol) `URI` to a `File` and back

I was not sufficiently aware of class `java.io.RandomAccessFile`, but it did pop up multiple times in sample exam questions.
As an example of its usage, see the code below:

```java
import java.io.*;

RandomAccessFile file = new RandomAccessFile("someFile.txt", "rw"); // Opened for reading/writing
file.seek(file.length()); // Go to the end, in order to append data
file.writeChars("Extra characters");
```
