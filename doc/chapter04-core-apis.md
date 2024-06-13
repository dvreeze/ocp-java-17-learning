# Chapter 4. Core APIs

See [OCP Java SE 17 Developer Study Guide](https://www.amazon.com/Oracle-Certified-Professional-Developer-Study/dp/1119864585/ref=sr_1_1?crid=1GIZNHYFXHAK4&dib=eyJ2IjoiMSJ9.Mz5O0lUSaZhUZ-O1Mi__dRPfXHL9GM_CfZ3JDTz910a2d8XI7Vsfj7zwcywJAfMcubfCglH02m8PwlAk_DORk8SS5460zaDP1fskFDX4sUiFVR4pxE1Ln0VIY-g5awTQaOJKp4t0Y1HchXkrw0HtOeVSHg3dHG8Jql9TibGCj-WeXYyNdMp4zWtgM4EimHpl4wvlJZufvGpNjNEmXIObAd2B1mp1skt5k7v_B-k_Ip4.bRERgxl7gsekO5AihUKuOeT_yoO6Bsg7jHigb4sjHEM&dib_tag=se&keywords=ocp+java+se17&qid=1714573695&sprefix=ocp+java+%2Caps%2C192&sr=8-1).

This chapter is about some of the most fundamental APIs in the Java standard library.

### Strings

See [java.lang.String](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/String.html)
and [java.lang.StringBuilder](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/StringBuilder.html).

The Java `String` class is a *reference type*, and it can be instantiated with *string literals* or *text blocks*, without
using any String class constructor.

Type `String` is *immutable*, that is, all operations that appear to change a String object in reality return a new String.
Class `String` is also *thread-safe*.

*String concatenation* uses the same "+" operator as addition of numbers. String concatenation appends the RHS String to
the LHS String. When mixing numbers and Strings in operations using "+", remember the following rules:
1. If both operands are numeric, "+" means addition of numbers
2. If either operand is a String, "+" means concatenation
3. The expression is evaluated from left to right

So `1 + 2 + "4"` results in `"34"`. Note that `"a" + null` results in `"anull"`.

Indexing in Strings, like is the case for arrays and collections, is *zero-based*. When passing a start and end index
to a method (like `substring`), the start index is *inclusive* and the end index is *exclusive*.

Some important *instance methods of class String* (or sets of overloaded String instance methods) are:
* `length`
* `charAt` (may throw a `StringIndexOutOfBoundsException`)
* `indexOf` (may return `-1` instead of throwing an exception)
* `substring`
* `toLowerCase`, `toUpperCase`
* `equals` (from the `Object` super-class), `equalsIgnoreCase`
* `startsWith`, `endsWith`, `contains`
* `replace` (taking 2 characters, or 2 strings), as opposed to `replaceAll` (taking a regular expression as first parameter)
* `strip`, `stripLeading`, `stripTrailing`, `trim` (method "trim" is older, using a less advanced definition of whitespace)
* `indent`, `stripIndent` (these methods are powerful, but mind line break normalization and trailing line break), and `lines`
* `translateEscapes` (since Java 15)
* `isEmpty`, `isBlank`
* `formatted` (since Java 15; see static method `format`)

Note that the String API leans itself for *method chaining*. Given that class `String` is *immutable*, this means the
creation of multiple intermediate String objects (at least in theory).

In general, be aware of how to override `equals` and `hashCode`, and that `a.equals(b)` must imply `a.hashCode() == b.hashCode()`.
Also note that `equals` must be an *equivalence relation* (reflexive, symmetric, transitive) and *consistent*.
This is already the case for (final) class `String`. As an aside, this is also supported automatically for (immutable)
*record classes*, with `equals` being *value equality* rather than *reference equality* ("==").

An important *static method of class String* is `format`. Commonly used *formatting symbols* in this method are
"%s" (for any type), "%d" (for integer values), "%f" (for floating-point values) and "%n" (line break).
For example, we can format floating-point values with "%12.8f" (possibly leading to some leading spaces) or "%012f" (possibly leading to some leading zeroes).

When creating a String in many steps, consider using a `StringBuilder` to create the String. The same `StringBuilder` object is used
for creating one String, thus (potentially) getting rid of many intermediate immutable String objects that were only used for creating
the result String. Note that unlike `String`, `StringBuilder` is mutable and not thread-safe.

`StringBuilder` methods like `append` can be chained, but the effect is quite different than method chaining for `String`.
After all, `String` is immutable, so method chaining creates new Strings all the time, whereas for `StringBuilder` it is
one and the same object that is being updated in-place all the time. So for String and StringBuilder we need a completely
*different mental model* to reason about them ("immutable versus mutable").

`StringBuilder` and `String` have several methods in common, partly from common implemented interface `CharSequence`.
Some commonly shared methods are:
* `indexOf`
* `substring`
* `length` (from `CharSequence`)
* `charAt` (from `CharSequence`)

Other important *instance methods of class StringBuilder* are:
* `append` (many overloads)
* `insert` (many overloads)
* `delete`, `deleteCharAt`
* `replace` (quite different from its String counterpart)
* `reverse`
* `toString` (from Object, but in this case building the result String from the StringBuilder)

Back to `String`. We know that class `String` uses value equality (or logical equality) for string comparisons using
method `equals`. But what about "object equality" (with "==")? We need to know that Java stores strings that are *compile-time
constants* into a *string pool*. See the following example, taken from the book:

```java
var name = "Hello World";
var name2 = new String("Hello World").intern();
System.out.println(name == name2); // true
```

Of course, we should not use *object equality* for Strings or method `intern` in our code.

### Arrays

*Arrays* are *reference types*, extending `java.lang.Object`. That is as true for `char[]` as for `String[]`.
Arrays have a *fixed size*, and on array creation the elements of the array get their default values (which is `null` for arrays
of reference types).

Note: `String[]` is a subtype of `Object[]` (which is a subtype of `Object`), but the superclass of `String[]` is `Object`.
I have to get used to that somehow. See:

```java
System.out.println(String[].class.getSuperclass()); // class java.lang.Object
System.out.println(Object[].class.isAssignableFrom(String[].class)); // true
```

Below follow some examples taken from the "OCP book". Creating an array of primitive types:

```java
int[] numbers = new int[3];
int numbers[] = new int[3]; // Equivalent to the preceding declaration, but more rare
int[] numbers = new int[] { 0, 0, 0 }; // Explicitly setting the elements to the default value (which happens anyway)

int[] moreNumbers = new int[] { 42, 55, 99 };
// Anonymous array with shorter syntax (the array type follows from the variable type), achieving exactly the same:
int[] moreNumbers = { 42, 55, 99 };
```

Note the difference between `int[] ids, type` on the one hand and `int ids[], types` on the other hand.

Creating an array of reference types (and "printing" it):

```java
String[] bugs = { "cricket", "beetle", "ladybug" };
String[] alias = bugs; // Copies reference to the same array object
System.out.println(bugs.equals(alias)); // true, because the (default) equality implementation is reference equality
System.out.println(bugs.toString()); // e.g. [Ljava.lang.String;@160bc7c0

System.out.println(java.util.Arrays.toString(bugs)); // prints array members cricket, beetle and ladybug
```

Creating an array of reference types (and casting it):

```java
String[] strings = { "stringValue" };
Object[] objects = strings; // String extends Object, so String[] extends Object[]
String[] againStrings = (String[]) objects; // works, because we happen to know the runtime type

againStrings[0] = new StringBuilder(); // DOES NOT COMPILE

objects[0] = new StringBuilder(); // throws ArrayStoreException at runtime, because the runtime type is String[]
```

An important *field* of array types is `length`.
*Array access* (to get one element of an array) looks like this, for example (getting the first element, if any): `myArray[0]`.

Some (overloaded) array support (static) methods are:
* `java.util.Arrays.sort`
* `java.util.Arrays.binarySearch` (returning index, if array has already been sorted; mind specific negative value `-insertionPoint - 1` if not found)
* `java.util.Arrays.compare` (for arrays of the same type; returning number < 0 if first array is lexicographically "smaller" than the second one, etc.)
* `java.util.Arrrays.mismatch` (returns first index where the 2 arrays differ, and -1 if they are equal)

Note that for some of these methods there are non-generic and generic overloads. For example, the non-generic `sort` method
(taking Object arrays) may throw a ClassCastException at runtime if elements are not comparable, whereas the generic variants
have extra generic `Comparator` method parameters to help prevent those exceptions. The same is true for `binarySearch`.

What does "smaller" (in lexicographical array comparisons) mean:
* null is smaller than any other value
* for numbers, normal numeric order is used
* for strings, if one is a prefix of another string, it is considered smaller
* for strings/characters, numbers are smaller than letters
* for strings/characters, uppercase is smaller than lowercase

*Vararg* method parameters can be used as arrays.

Arrays are objects, so can be array elements themselves, leading to *multidimensional arrays* (which is a somewhat misleading name):

```java
int[] array1 [], array2[][]; // don't do this, but it's equivalent to the following
int[][] array1;
int[][][] array2;
```

Multidimensional array creation:

```java
String[][] arr = new String[3][2]; // array of 3 elements, each of them being an array of 2 strings

int[][] differentSizes = { { 1, 4 }, { 3 }, { 9, 8, 7 } }; // this is equal to the following

int[][] differentSizes = new int[3][];
differentSizes[0] = new int[] { 1, 4 };
differentSizes[1] = new int[] { 3 };
differentSizes[2] = new int[] { 9, 8, 7 };
```

### Math APIs

Some `java.lang.Math` static methods:
* `min` and `max`, with overloads for double, float, int, long
* `round`, with overloads for float (returning int) and double (returning long)
* `ceil`, `floor`, taking and returning a double
* `pow`, taking and returning doubles
* `random`, returing a double between 0 (inclusive) and 1 (exclusive)

### Dates and Times

Forget about the old `java.util.Date` and `java.util.Calendar` types. Use the `java.time` API instead.

Consider the following types:
* `LocalDate` (date, but no time)
* `LocalTime` (time, but no date)
* `LocalDateTime` (date and time, but like LocalDate and LocalTime no time zone)
* `ZonedDateTime` (date, time and time zone)

These classes are *immutable*, *thread-safe* and instances are created using *factory methods* (the constructors are private).

Time zone zero is known as GMT (or UTC). Time zones can be given as offsets (e.g. "+02:00", "GMT+2")
or short strings like "Asia/Kolkata".

Types `LocalDate`, `LocalTime`, `LocalDateTime` and `ZonedDateTime` all have static method `now`.

They also have methods called `of`. For example:

```java
LocalDate.of(2024, Month.MAY, 9)
LocalDate.of(2024, 5, 9) // equivalent to the former expression; months are 1-based, not 0-based

LocalTime.of(6, 15) // 6:15, so no seconds, milliseconds and nanoseconds
LocalTime.of(6, 15, 30, 200) // nanosecond precision (there is no field for millisecond precision)

LocalDateTime.of(2024, Month.MAY, 9, 6, 15, 30) // precision can be minutes to nanoseconds (here it is seconds)
LocalDateTime.of(localDate1, localTime1)
```

Examples to create a `ZonedDateTime`:

```java
var zone = ZoneId.of("US/Eastern");
var zoned1 = ZonedDateTime.of(2022, 1, 20, 6, 15, 30, 200, zone); // nanosecond precision in this case (we cannot pass a Month)
var zoned2 = ZonedDateTime.of(localDate1, localTime1, zone);
var zoned3 = ZonedDateTime.of(localDateTime1, zone);
```

There are methods like `plusDays`, `minusDays`, etc. (also for weeks, months, years, hours, minutes, seconds etc.).
Note that not all of these methods apply to all of the 4 types above (you cannot add days to a LocalTime, for example).
Also note that these types are *immutable*, so these methods do not update dates/times in place.

Below, types `Period`, `Duration` and `Instant` are shown. These types are also *immutable*, *thread-safe* and they have
public static *factory methods* (and private constructors that obviously are not part of the public API).

Examples of creating/using type `Period` are:

```java
var everyOtherDay = Period.ofDays(2); // every 2 days; similar static methods exist for weeks, months, years
var everyYearAndAWeek = Period.of(1, 0, 7); // every year and 7 days (years: 1, months: 0, days: 7)

var today = LocalDateTime.now();
var dayAfterTomorrow = today.plus(everyOtherDay); // adding a Period to a LocalDateTime

System.out.println(Period.of(1, 2, 3)); // prints "P1Y2M3D"
System.out.println(Period.ofMonths(3)); // prints "P3M" (leaving out the "zero parts" among years, months and days)
```

We cannot chain methods on Periods. Also, we cannot use Periods with `LocalTime` (there must be a date component).

Type `Duration` is similar to `Period`, but it is meant for objects that have a time component, so `Duration` cannot be used
with `LocalDate`.

Examples of `Duration` creation:

```java
var daily = Duration.ofDays(1); // PT24H; similar for hours
var daily2 = Duration.of(1, ChronoUnit.DAYS); // equivalent to the former Duration

var everyMinute = Duration.ofMinutes(1); // PT1M
var everyMinute2 = Duration.of(1, ChronoUnit.MINUTES); // equivalent to the former Duration

var everyTenSeconds = Duration.ofSeconds(10); // PT10S; similar for millis and nanos
var everyTenSeconds2 = Duration.of(10, ChronoUnit.SECONDS); // ChronoUnit can also express MILLIS and NANOS)
```

Durations can be used in the same way as Periods, using methods such as `plus`. Durations cannot be used with `LocalDate`.

Using `java.time.temporal.ChronoUnit` for differences:

```java
var one = LocalTime.of(5, 15);
var two = LocalTime.of(6, 30);

ChronoUnit.HOURS.between(one, two); // 1 (note the truncation rather than rounding)
ChronoUnit.MINUTES.between(one, two); // 75
```

Objects with a time component can be truncated, e.g. `LocalTime.of(3, 12, 45).truncatedTo(ChronoUnit.MINUTES)` gets rid of seconds.

Class `Instant` represents a moment in time in the GMT time zone. With `ZonedDateTime.toInstant` we can convert a `ZonedDateTime`
to an `Instant`.

Note how *Daylight Saving Time* influences methods like `plusHours` twice each year (in the US and in many other countries).
Java is smart enough to adapt "date-times with time zone" accordingly. It does this by updating the "offset". For example:

```java
import java.time.*;
import java.time.temporal.ChronoUnit;

var localDate = LocalDate.of(2022, Month.NOVEMBER, 6);
var localTime = LocalTime.of(1, 30);
var zone = ZoneId.of("US/Eastern");

// In half an hour from this moment in time, the clock will go back one hour
var dateTime1 = ZonedDateTime.of(localDate, localTime, zone);

// One hour later, but the clock went back one hour
var dateTime2 = dateTime1.plus(1, ChronoUnit.HOURS);

// The LocalDateTime is the same (after the clock having gone back one hour). This returns true.
var localDateTimesEqual = dateTime1.toLocalDateTime().equals(dateTime2.toLocalDateTime());
// Even the zone stayed the same. This returns true.
var zonesEqual = dateTime1.getZone().equals(dateTime2.getZone());
// Yet the offsets are not the same, going from -04:00 to -05:00. So there is the difference.
var dateTime1Offset = dateTime1.getOffset();
var dateTime2Offset = dateTime2.getOffset();
// And the final test that these moments in time are 1 hour apart is to convert them to an Instant
var dateTime1Instant = dateTime1.toInstant();
var dateTime2Instant = dateTime2.toInstant();
```
