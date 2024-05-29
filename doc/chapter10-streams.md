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
* Intermediate operations *return a (transformed) Stream*, whereas a terminal operation *does not return a Stream*, so *the return type of the operation clearly separates intermediate from terminal operations*
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
* Static method `Stream.iterate(T, Predicate<? super T>, UnaryOperator<T>)` (potentially finite, depending on the "loop condition")

#### Using common terminal operations

*Terminal operations* turn a Stream into a (non-Stream) result. A special case are *reduction operations*, where all the
contents of the Stream are combined into a single primitive or `Object`. Typical reduction results are primitives or
*collections*.

A very powerful `Stream<T>` *reduction operation*, in terms of which many terminal operations can be understood, is:

```java
public <U> U reduce(U identity, BiFunction<U, ? super T, U> accumulator, BinaryOperator<U> combiner);
```

With the `identity` and `accumulator` method parameters the `reduce` method *loops* over the stream, and the `combiner`
method parameter is useful for parallel streams (to combine partial results).

Ignoring the `combiner` parameter and parallel stream processing for the moment, method `reduce` *recursively creates
an accumulated result*, by starting with `identity` and then repeatedly calling function `accumulator`, passing the next
element in the stream as second argument to that function, until the end of the stream has been reached.

*Reduction operations* process the entire Stream, and therefore do *not terminate* for *infinite streams*.

Some common `Stream<T>` *terminal operations* are:
* `count()`, returning a `long`
  * it is a *reduction operation*, and is equivalent to `reduce(0L, (acc, nextElem) -> acc + 1L, Long::sum)`
  * as a reduction operation, it does *not terminate* for infinite streams
* `min(Comparator<? super T>)` and `max(Comparator<? super T>)`, both returning an `Optional<T>`
  * they are *reduction operations*, and can easily be written as equivalent (3-parameter) `reduce` calls
  * as reduction operations, they do *not terminate* for infinite streams
* `findFirst()` and `findAny()`, both returning an `Optional<T>` (`findAny` may return an element that is not the first one)
  * they are not reduction operations, since they typically return without processing all of the elements
  * they *do terminate* for infinite streams
* `allMatch(Predicate<? super T>)`, `anyMatch(Predicate<? super T>)` and `noneMatch(Predicate<? super T>)`, all returning a `boolean`
  * they are not reduction operations, since they are *short-circuiting terminal operations*
  * depending on the data and the predicate, these methods may or may not terminate for infinite streams
* `forEach(Consumer<? super T>)`, with return type `void`
  * it is the "Stream equivalent of looping"
  * it is not a reduction operation, since it does not return any result but instead relies on the Consumer's side-effects
  * it does *not terminate* for infinite streams
* overloaded method `reduce`, e.g. the above-mentioned `reduce(U, BiFunction<U, ? super T, U>, BinaryOperator<U>)`, returning a `U`
  * this above-mentioned overload of `reduce` is very powerful, as mentioned earlier, and many terminal operations can be understood in terms of `reduce`
  * there are also "friendly" overloads if the type does not change, namely `reduce(T, BinaryOperator<T>)` (returning `T`) and `reduce(BinaryOperator<T>)` (returning `Optional<T>`)
  * clearly, *reduction* processes all elements so does *not terminate* for infinite streams
* overloaded *mutable reduction* method `collect`, e.g. `collect(Collector<? super T, A, R>)`, returning an `R`
  * this is a special case of reduction, namely a *mutable reduction*, where the same mutable object is used while accumulating (for efficiency, typically)
  * a simplified static method signature for obtaining a `Collector<T, R, R>` (input `T`, output `R`) is `Collector.of(Supplier<R>, BiConsumer<R, T>, BinaryOperator<R>)`
  * looking at the signature of method `Collector.of`, note that this is indeed about side effects and *mutability*
  * class `Collectors` contains many static methods to obtain a `Collector`, which is used to produce collections, strings (via `StringBuilder`), primitives etc.
  * indeed, `collect` calls can be (naively) rewritten in terms of `reduce`, so *collect* is about *reduction*
  * the other overload (where we do not pass a `Collector`) is `collect(Supplier<R>, BiConsumer<R, ? super T>, BiConsumer<R, R>)`, returning `R`
  * clearly, *mutable reduction* processes all elements so does *not terminate* for infinite streams

Some static *Collector factory methods* in class `Collectors` are:
* `toList()` and `toSet()`, which can be understood in terms of `toCollection(ArrayList::new)` and `toCollection(HashSet::new)`, respectively
* `toCollection(Supplier<C>)` where `C` is a collection type, which can be understood in terms of direct `Collector` creation with static method `Collector.of`
* overloads of `joining`, to produce a `String` from the Stream
* collectors that produce primitive wrappers, such as `counting()`, `summingInt(ToIntFunction<? super T>)` and `averagingDouble(ToDoubleFunction<? super T>)` (`averagingInt` and `averagingLong` exist as well)
* collectors that collect multiple statistics in one go (count, average, sum etc.), e.g. `summarizingDouble(ToDoubleFunction<? super T>)`
* `maxBy(Comparator<? super T>)` etc., creating a collector that returns an `Optional<T>`
* collectors that produce `Map` instances, such as overloads of `toMap`, `groupingBy` and `partitioningBy`
* `reducing` collectors, which look like alternatives to direct `reduce` calls, but used in `collect` calls

There are also *Collector factory methods* that *transform or even combine Collectors*, such as:
* `collectingAndThen`, taking a Collector and a "post-processing function" (e.g. the collector gradually "fills" a `StringBuilder`, and the "post-processing function" turns that into a `String`)
* `teeing`, creating a composite of 2 downstream collectors
* collectors mimicking some intermediate operations, e.g.`filtering`, `mapping`, `flatMapping`

For example, `filtering` has method signature `filtering(Predicate<? super T>, Collector<? super T, A, R>)`, returning a
`Collector<T, ?, R>`.

Of course, there is always the option to create a *Collector* from scratch, with method `Collector.of`, if needed.

Clearly, the "Collector system" is very powerful for *mutable Stream reductions*. Mutable reduction operations on streams
can also be understood (more verbosely) in terms of applying collectors. For example:

```java
List<Integer> intRange = IntStream.range(0, 20).boxed().toList();

// Below, max1 and max2 are equal according to the "equals" method
Optional<Integer> max1 = intRange.stream().max(Comparator.naturalOrder());
Optional<Integer> max2 = intRange.stream().collect(Collectors.maxBy(Comparator.naturalOrder()));
```

#### Using common intermediate operations

*Intermediate operations* transform Streams, and are *lazily evaluated*, as explained earlier in some detail.

A very powerful `Stream<T>` *intermediate operation*, in terms of which many intermediate operations can be understood, is:

```java
public <R> Stream<R> flatMap(Function<? super T, ? extends Stream<? extends R>> mapper);
```

The `flatMap` method maps each element in the stream to another stream, and then "flattens" the result (turning a stream
of streams of `R` into a flattened stream of `R`).

Note that in the "Scala world" so-called *monads* can roughly be considered *flat-mappable* data structures, and their
power is well-known in that community (and also elsewhere, of course).

Some other *intermediate operations* (that return `Stream<T>` unless said otherwise) are:
* `filter(Predicate<? super T>)` (which is easy to understand in terms of `flatMap`)
* `map(Function<? super T, ? extends R>)`, returning a `Stream<R>` (which is easy to understand in terms of `flatMap`)
* `distinct()`, to remove duplicates (according to method `Object.equals`)
* `skip(long)` and `limit(long)`, the latter potentially turning an infinite stream into a finite one
* `sorted()` and `sorted(Comparator<? super T>)` (take care not to call these methods on infinite streams)
* `peek(Consumer<? super T>)`
  * it is like "forEach as intermediate operation" that does not alter the stream
  * therefore it is great for debugging, provided we do not change the data in the Consumer

As said, some intermediate operations can be understood in terms of `flatMap`. For example:

```java
var xs = Stream.iterate(0L, n -> n <= 100, n -> n + 1).toList();

// The following 2 variables are equal
var divisibleBy10 = xs.stream().filter(n -> n % 10 == 0).toList();
var alsoDivisibleBy10 = xs.stream().flatMap(n -> (n % 10 == 0) ? Stream.of(n) : Stream.empty()).toList();

// The following 2 variables are equal
var timesTwo = xs.stream().map(n -> n * 2).toList();
var alsoTimesTwo = xs.stream().flatMap(n -> Stream.of(n * 2)).toList();
```

*Stream concatenation* can be achieved with static method `Stream.concat(Stream<? extends T>, Stream<? extends T>)`.

#### Putting together the pipeline

*Stream pipelines* express *intent*. That is, they express the *WHAT* rather than the *HOW*, which is a good thing.
They support *functional programming* (not according to purists, though), and many intermediate operations are conceptually
*HOFs* (i.e. *higher-order functions*, which are functions that take and/or return functions).

For example, look at how the following example (from the book) indeed shows *intent*, and consider what the code would look
like without streams but with loops etc.:

```java
stringList.stream()
    .filter(s -> s.length() <= 4)
    .sorted()
    .limit(2)
    .forEach(System.out::println);
```

When reasoning about *stream pipeline evaluation order*, do not think in terms of "intermediate collections" (there are not
any), but think in terms of *lazy evaluation*, and the pipeline trying to *entirely process one Stream element at a time*,
to the extent possible. Of course, sorting does not allow complete one-element-at-a-time processing, but to the extent
that this is possible this will be the evaluation order. This should help to get a *mental picture* of evaluation order in
stream processing.

### Working with primitive streams

There are no dedicated (non-generic) collections for primitive types (the type variable for the collection element type is instantiated
with a wrapper type instead), but there are *arrays of primitives* (such as `byte[]`), and there are *Optional* types
for primitives `int`, `long` and `double`, namely `OptionalInt`, `OptionalLong` and `OptionalDouble`. There are also
"custom" *functional interface* types for primitives, as we have seen. For example: `LongPredicate`, `IntToDoubleFunction`.

Analogously, there are non-generic *Stream types* `IntStream`, `LongStream` and `DoubleStream`. Not only are these
custom Stream types more efficient for primitives (by avoiding lots of boxing/unboxing, at least in theory), but they
also contain custom methods that make sense for these specific Stream types. `IntStream` is useful for primitive types
`int`, `char`, `short` and `byte`. `DoubleStream` is useful for primitive types `double` and `float`.

Below, wherever it makes sense, and unless stated otherwise, whatever is said about one primitive stream type (e.g.
`DoubleStream`) also holds for the other 2 primitive stream types.

As an example of a "custom Stream method", interface type `DoubleStream` has method `average()`, returning an `OptionalDouble`.

*Creation* of *primitive streams* is analogous to creation of *Object Streams*. For example, there is static method
`DoubleStream.iterate(double, DoubleUnaryOperator)`, returning a `DoubleStream`. Indeed, these methods work entirely
with primitives (in this case "double" values), and use no generics at all.

For `IntStream` and `LongStream` (and NOT for `DoubleStream`) there are also static factory methods to create "ranges":
* `range(long, Long)` and `rangeClosed(long, long)` for `LongStream`, and similar methods for `IntStream`

*Terminal operations* for primitive streams are also relatively easy to predict, and quite similar to those for Object Streams.
Some differences (other than non-generic parameter and return types) are:
* methods `min()` and `max()` do not take any Comparator parameter, returning `OptionalDouble` for `DoubleStream` etc.
* there are only 2 overloaded `reduce` functions, and for `DoubleStream` they are `reduce(double, DoubleBinaryOperator)` and `reduce(DoubleBinaryOperator)`, the latter returning `OptionalDouble`
* method `collect` is not overloaded, and for `DoubleStream` we just have `collect(Supplier<R>, ObjDoubleConsumer<R>, BiConsumer<R, R>)`
* as said earlier, there are some "custom terminal operations", such as (for `DoubleStream`) `average()` (returning `OptionalDouble`) and `sum()` (returning `double`)
* in particular, there are also `summaryStatistics()` methods

The *intermediate operations* that leave the "kind of stream" the same (so operations that leave a `DoubleStream` a `DoubleStream` etc.)
are easy to predict as well, knowing the "Object stream" counterparts. For example:
* `DoubleStream` method `flatMap(DoubleFunction<? extends DoubleStream>)` returns a `DoubleStream`, and `flatMap` for the 2 other primitive streams are quite similar
* `DoubleStream` method `map(DoubleUnaryOperator)` returns a `DoubleStream`, and `map` for the 2 other primitive streams are quite similar
* method `sorted()` does not have any Comparator parameter

There are also several *intermediate operations* to "hop between different kinds of streams". For example:
* `boxed()` turns a primitive stream into a stream of the corresponding primitive wrappers (as a convenience method, but it is trivial to implement in terms of `mapToObj`)
* functions like `mapToInt`, `mapToLong` and `mapToDouble` to "hop between different kinds of primitive streams"
* functions like `mapToObj` to "hop from primitive stream to Object stream"
* `LongStream` has convenience method `asDoubleStream()`, and `IntStream` has convenience methods `asLongStream()` and `asDoubleStream()`
* `Stream<T>` has methods like `flatMapToDouble(Function<? super T, ? extends DoubleStream>)` etc.
* similarly, `Stream<T>` has methods like `mapToDouble(ToDoubleFunction<? super T>)` etc.

Several terminal operations for primitive streams return "optional types" like `OptionalDouble`, `OptionalLong` and `OptionalInt`.
These types are quite similar in their API to `Optional<T>`. The most important differences are that instead of `get()`
there are methods like `getAsDouble()` (for `OptionalDouble`), to make clear that (specific) primitives are retrieved.

### Working with advanced stream pipeline concepts

#### Linking streams to the underlying data

Recall that stream pipelines are executed no earlier than *when the terminal operation is called*. So the following returns 3:

```java
var people = new ArrayList<String>();
people.addAll(List.of("Alex", "Bjorn", "Chris"));

// The stream pipeline is not yet executed
var peopleStream = people.stream().filter(s -> s.startsWith("A"));

people.addAll(List.of("Andre", "Amy", "Christina"));

// Only now the stream pipeline will run, and the underlying List is still the same object in memory
var filteredPeopleCount = peopleStream.count();

// Prints 3, not 1
System.out.println(filteredPeopleCount);
```

#### Chaining Optionals

For type `java.util.Optional<T>` there are methods like `filter`, `map` and `flatMap`. For example:
* `flatMap(Function<? super T, ? extends Optional<? extends U>>)`, returning an `Optional<U>`.

This is quite convenient. It prevents us from having to transform "optionals" using the Stream API. It is also safe,
because these "Optional method chains" work exclusively with `Optional`, thus preventing us from having to call functions
like `findFirst()`.

Note that these "higher-order functions" exist for `Optional<T>`, but NOT for `OptionalDouble`, `OptionalLong` and `OptionalInt`.

A side-note on *functional interfaces and checked exceptions*: most functional interfaces do not declare any checked exceptions.
A strategy that works in these cases is to wrap checked exceptions in unchecked exceptions in code that is used in Suppliers
and other functional interfaces.

#### Using a Spliterator

A *java.util.Spliterator<T>* can be used for *traversing and partitioning elements of a source*. That source can be a
collection or Stream, for example. The details are far from straightforward, but the most important *instance methods* are:
* `trySplit()`, returning another `Spliterator<T>`, which ideally contains half of the data, which is removed from the current `Spliterator`
* `tryAdvance(Consumer<? super T>)`, trying to perform the given action on one of the remaining elements (and return `true`), if there is any, and returning `false` otherwise
* `forEachRemaining(Consumer<? super T>)` (return type `void`), trying to perform the given action on all remaining elements

*Spliterators* are used under the hood as base utility for Streams, in particular parallel ones. See
[Introduction to Spliterator in Java](https://www.baeldung.com/java-spliterator).

#### Collecting results

Recall that `Stream<T>` has method `collect(Collector<? super T, A, R>)`, returning an `R` (whatever that `R` is, like
a String, collection, primitive etc.). Also recall that class `Collectors` has many static methods to create `Collector`
instances for *mutable reduction operations*. Many of those methods have at least been briefly mentioned.

Let's now zoom in on *Collectors* that produce *Map* "collections". Below it is assumed implicitly that the Collectors
being discussed are used in a `Stream<T>` call to instance method `collect(Collector<? super T, A, R>))`.

To collect data into *Map* instances, we cannot use `Collectors.toCollection(Supplier<C)` (with `C` being a `Collection` subtype).
We do have overloaded `Collectors.toMap` static methods, though.

The simplest one is `Collectors.toMap(keyMapper, valueMapper)` (giving parameter names rather than complex method signatures).
Parameter `keyMapper` turns an element of the Stream into a key, and parameter `valueMapper` turns the element into a mapped value.

For example:

```java
List<String> words = List.of("this", "is", "very", "challenging");

// Creating a Map, mapping "this" to 4, "is" to 2, etc.
Map<String, Integer> wordLengths =
        words.stream().collect(Collectors.toMap(Function.identity(), String::length));
```

The method above will cause an `IllegalStateException` in the `collect` call if there are duplicate keys!

What do we want to do if we have duplicate keys? Maybe merge the mapped values (and keep the same type)? If so,
use the `toMap` overload `Collectors.toMap(keyMapper, valueMapper, mergeFunction)`. If we additionally want to have
control over the runtime type of the result *Map*, consider using the most complex `toMap` overload, namely
`Collectors.toMap(keyMapper, valueMapper, mergeFunction, mapFactory)`.

For example:

```java
List<String> words = List.of("this", "is", "very", "challenging");

// Creates a TreeMap containing pairs like 4 -> "this, very"
Map<Integer, String> lengthToWordsMap =
        words.stream().collect(Collectors.toMap(
                String::length,
                Function.identity(),
                (word1, word2) -> word1 + ", " + word2,
                TreeMap::new
        ));
```

Let's now do something similar, namely grouping the words based on the word length as key. For that we use method
`Collectors.groupingBy(classifier)`, where the `classifier` is a function from a stream element to a key (in our example
a function from word to word length). The returned `Collector` produces a `Map<K, List<T>>` (where `K` is the key type).

For example:

```java
List<String> words = List.of("this", "is", "very", "challenging");

// Creates a Map containing pairs like 4 -> [ "this", "very" ]
Map<Integer, List<String>> lengthToWordsMap =
        words.stream().collect(Collectors.groupingBy(String::length));
```

Maybe we want the mapped values to be a *Set* rather than a *List*. For that we can use another `groupingBy` overload,
namely `Collectors.groupingBy(classifier, downstreamCollector)`. The *downstream collector* processes the "mapped values"
per key.

For example:

```java
List<String> words = List.of("this", "is", "very", "challenging");

// Creates a Map containing pairs like 4 -> [ "this", "very" ]
Map<Integer, Set<String>> lengthToWordsMap =
        words.stream().collect(Collectors.groupingBy(String::length, Collectors.toSet()));

// Creates a Map containing pairs like 4 -> 2 (the latter being a word count for words of length 4)
Map<Integer, Long> lengthToWordCountMap =
        words.stream().collect(Collectors.groupingBy(String::length, Collectors.counting()));
```

Again, the type of *Map* can be adapted. For that we have the 3rd `groupingBy` overload, namely
`Collectors.groupingBy(classifier, mapFactory, downstreamCollector)`.

For example:

```java
List<String> words = List.of("this", "is", "very", "challenging");

// Creates a Map containing pairs like 4 -> [ "this", "very" ]
SortedMap<Integer, Set<String>> lengthToWordsMap =
        words.stream().collect(Collectors.groupingBy(String::length, TreeMap::new, Collectors.toSet()));
```

Note that `Collectors.groupingBy` is quite powerful when it comes to "grouping data". The `toMap` Collectors above
can undoubtedly be written in terms of `groupingBy`.

Another special case of grouping is *partitioning*. Partitioning splits a stream into 2 parts, with keys `true` and
`false`, respectively (as their wrappers). To create a "partitioning collector", we pass a *Predicate* rather than
a "classifier function". A difference with "grouping" is that even if a partition (for `true` or `false`) is empty, it
is still part of the result, so there are always 2 partitions.

There are 2 overloads of `partitioningBy`:
* `Collectors.partitioningBy(predicate)`
* `Collectors.partitioningBy(predicate, downstreamCollector)`

Their use is quite similar to `groupingBy`, and not shown in an example here.

Another important *Collector* factory method is `Collectors.mapping(mappingFunction, downstreamCollector)`.
Below it is used for collecting "mapped values", so we have 2 levels of "downstream collectors" in the following example:

```java
List<String> words = List.of("this", "is", "very", "challenging");

// Creates a Map containing pairs like 4 -> [ "THIS", "VERY" ]
Map<Integer, List<String>> lengthToWordsMap =
        words.stream().collect(
                Collectors.groupingBy(
                        String::length,
                        Collectors.mapping(String::toUpperCase, Collectors.toList())));
```

Finally, be aware of method `Collectors.teeing(downstreamCollector1, downstreamCollector2, mergingFunction)`.
It allows us to go only once through the stream, but still using 2 collectors on that stream, and combine the
collector results afterwards.
