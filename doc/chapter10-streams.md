# Chapter 10. Streams

See [OCP Java SE 17 Developer Study Guide](https://www.amazon.com/Oracle-Certified-Professional-Developer-Study/dp/1119864585/ref=sr_1_1?crid=1GIZNHYFXHAK4&dib=eyJ2IjoiMSJ9.Mz5O0lUSaZhUZ-O1Mi__dRPfXHL9GM_CfZ3JDTz910a2d8XI7Vsfj7zwcywJAfMcubfCglH02m8PwlAk_DORk8SS5460zaDP1fskFDX4sUiFVR4pxE1Ln0VIY-g5awTQaOJKp4t0Y1HchXkrw0HtOeVSHg3dHG8Jql9TibGCj-WeXYyNdMp4zWtgM4EimHpl4wvlJZufvGpNjNEmXIObAd2B1mp1skt5k7v_B-k_Ip4.bRERgxl7gsekO5AihUKuOeT_yoO6Bsg7jHigb4sjHEM&dib_tag=se&keywords=ocp+java+se17&qid=1714573695&sprefix=ocp+java+%2Caps%2C192&sr=8-1).

This chapter is about Java *streams* (and *Optional*). The *Java Stream API* should not be confused with input and output
streams in the Java I/O API. The latter is treated in another chapter.

The material (on the Java Stream API) in this chapter builds heavily on chapter 8 ("Lambdas and Functional Interfaces")
and chapter 9 ("Collections and Generics").

### Returning an Optional

Type `java.util.Optional<T>` is a more expressive and type-safe way than `null` to model potentially missing data.

There are also specific *Optional* types for `double`, `int` and `long`, namely `OptionalDouble`, `OptionalInt` and `OptionalLong`.
This is analogous to *streams*, for which the same is true (namely `DoubleStream`, `IntStream` and `LongStream`).

Some *static methods* of generic class `java.util.Optional<T>` are:
* Factory method `empty()`, returning an empty Optional
* Factory method `of(T)`, returning a non-empty Optional, provided the passed value is not `null` (or else an NPE is thrown)
* Factory method `ofNullable(T)`, returning a non-empty Optional if the given value is not `null`, and an empty Optional otherwise

Note that `Optional.ofNullable(v)` is equal to `(v == null) ? Optional.empty() : Optional.of(v)`.

Some *instance methods* of class `java.util.Optional<T>` are:
* `isPresent()`, returning `true` if the Optional is not empty
* `isEmpty()`, returning `true` if the Optional is empty (equal to the negation of `isPresent()`)
* `get()`, returning the contained element, but throwing a `NoSuchElementException` if the Optional is empty (note: prefer method `orElseThrow()` to this one)
* `orElse(T)`, returning the contained element, and if empty, falling back to the parameter value
* `orElseGet(Supplier<? extends T>)`, returning the contained element, and if empty, uses the given Supplier to obtain a value
* `orElseThrow()`, returning the contained element, but throwing a `NoSuchElementException` if the Optional is empty
* `orElseThrow(Supplier<? extends X>)`, where `X extends Throwable`, returning the contained element, but throwing an exception created by the given Supplier otherwise 
* `ifPresent(Consumer<? super T>)`, running the given Consumer on the contained value, if any, and a no-op otherwise
* `ifPresentOrElse(Consumer<? super T>, Runnable)`, like `ifPresent` but running the given Runnable if the Optional is empty

Note that some methods above are easy to express in terms of other ones (e.g. `orElseThrow` without parameters in terms of `orElseThrow` taking one parameter).

Some "Stream-related" *instance methods* (all but the first one looking very much like similar *Stream API methods*) are:
* `stream()`
* `filter(Predicate<? super T>)`
* `map(Function<? super T, ? extends U>)`, returning an `Optional<U>`
* `flatMap(Function<? super T, ? extends Optional<? extends U>>)`, returning an `Optional<U>`

The methods above, except for the first one, are convenience methods. For example, `myOptional.filter(p)` is equal to
`myOptional.stream().filter(p).findFirst()`.

### Using streams

#### Understanding the pipeline flow

A Java *stream* is a *sequence of data, that can be used only once*. So, unlike collections (especially immutable collections)
that can be reused again and again, streams are much more *like iterators* than collections in that they are not reusable.

The Stream interface type is `java.util.stream.Stream<T>`.

For example:

```java
import java.util.stream.Stream;

var stream = Stream.of(1, 2, 3, 4, 5);

// Ok, using the stream the first time, which will print the 5 numbers
stream.toList().forEach(System.out::println);

// NOT OK! The stream is closed and cannot be used again!
stream.toList().forEach(System.out::println); // Throws an IllegalStateException!
```

Streams offer a *functional programming style* in Java, but streams themselves are not "functional objects" (unlike
immutable records, for example). This is very important to keep in mind! Whereas streams are not reusable, *Supplier*s
of (new) streams or *collections* as "stream factories" are reusable. So "stream pipelines" should normally have a *very local scope*.

Put differently, with streams data is created only when needed. In other words, stream processing is an example of *lazy evaluation*.
Data flows through the pipeline and does not come back after completion of each "step".

A *stream pipeline* consists of (at most) 3 parts:
1. the mandatory stream *source*, from which the stream is created
2. zero or more *intermediate stream operations*, transforming the stream into another one (filtering/mapping/flat-mapping etc.)
3. exactly one *terminal operation*, producing a result, after which the stream is closed and no longer available

Above, *lazy evaluation* means the following:
* Nothing is evaluated immediately when the intermediate stream operations are called
  * Think of the chain of intermediate stream operations as a "transformation recipe" that not yet runs
* Only when the terminal operation is called, *processing of the intermediate stream operations is triggered*

Streams may be *finite* or *infinite*. This is not clearly visible from the code, but still very important to keep in mind!

Some important differences between *intermediate operations* and the *terminal operation* are:
* Each useful stream pipeline *ends with exactly 1 terminal operation* (a terminal operation cannot exist multiple times in a stream pipeline), but there can be *zero or more intermediate operations*
* Intermediate operations *return a (transformed) Stream*, whereas a terminal operation *does not return a Stream*
* When calling an intermediate operation, it is *not yet evaluated*, whereas *on calling the terminal operation the pipeline runs*
* After an intermediate operation call the stream is still valid, but *after the terminal operation the stream is closed* and no longer available for processing

#### Creating stream sources

*Creating finite streams*:
* Static method `Stream.empty()`
* Static method `Stream.of(T...)`
* Collection instance methods `stream()` and `parallelStream()` (the latter potentially running in parallel)
* Static method `java.util.Arrays.stream(T[])`

*Creating (potentially) infinite streams*:
* Static method `Stream.generate(Supplier<? extends T>)`
* Static method `Stream.iterate(T, UnaryOperator<T>)`
* Static method `Stream.iterate(T, Predicate<? super T>, UnaryOperator<t>)` (potentially finite, depending on the "loop condition")

#### Using common terminal operations

*Terminal operations* turn a Stream into a (non-Stream) result. A special case are *reduction operations*, where all the
contents of the Stream are combined into a single primitive or `Object`. Typical reduction results are primitives or
*collections*.
