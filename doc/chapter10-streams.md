# Chapter 10. Streams

See [OCP Java SE 17 Developer Study Guide](https://www.amazon.com/Oracle-Certified-Professional-Developer-Study/dp/1119864585/ref=sr_1_1?crid=1GIZNHYFXHAK4&dib=eyJ2IjoiMSJ9.Mz5O0lUSaZhUZ-O1Mi__dRPfXHL9GM_CfZ3JDTz910a2d8XI7Vsfj7zwcywJAfMcubfCglH02m8PwlAk_DORk8SS5460zaDP1fskFDX4sUiFVR4pxE1Ln0VIY-g5awTQaOJKp4t0Y1HchXkrw0HtOeVSHg3dHG8Jql9TibGCj-WeXYyNdMp4zWtgM4EimHpl4wvlJZufvGpNjNEmXIObAd2B1mp1skt5k7v_B-k_Ip4.bRERgxl7gsekO5AihUKuOeT_yoO6Bsg7jHigb4sjHEM&dib_tag=se&keywords=ocp+java+se17&qid=1714573695&sprefix=ocp+java+%2Caps%2C192&sr=8-1).

This chapter is about Java *streams* (and *optional*). The *Java Stream API* should not be confused with input and output
stream in the Java I/O API. The latter is treated in another chapter.

The material (on the Java Stream API) in this chapter builds heavily on chapter 8 ("Lambdas and Functional Interfaces")
and chapter 9 ("Collections and Generics").

### Returning an Optional

Type `java.util.Optional<T>` is a more expressive and type-safe way than `null` to model potentially missing data.

There are also specific *Optional* types for `double`, `int` and `long`, namely `OptionalDouble`, `OptionalInt` and `OptionalLong`.
This is analogous to *streams*, for which the same is true.

Some *static methods* of generic class `java.util.Optional<T>` are:
* Factory method `empty()`, returning an empty Optional
* Factory method `of(T)`, returning a non-empty Optional, provided the passed value is not `null` (or else an NPE is thrown)
* Factory method `ofNullable(T)`, returning a non-empty Optional if the given value is not `null`, and an empty Optional otherwise

Some *instance methods* of class `java.util.Optional<T>` are:
* `isPresent()`, returning `true` if the Optional is not empty
* `isEmpty()`, returning `true` if the Optional is empty
* `get()`, returning the contained element, but throwing a `NoSuchElementException` if the Optional is empty (note: prefer method `orElseThrow()` to this one)
* `orElse(T)`, returning the contained element, and if empty, falling back to the parameter value
* `orElseGet(Supplier<? extends T>)`, returning the contained element, and if empty, uses the given Supplier to obtain a value
* `orElseThrow()`, returning the contained element, but throwing a `NoSuchElementException` if the Optional is empty
* `orElseThrow(Supplier<? extends X>)`, where `X extends Throwable`, returning the contained element, but throwing an exception created by the given Supplier otherwise 
* `ifPresent(Consumer<? super T>)`, running the given Consumer on the contained value, if any, and a no-op otherwise
* `ifPresentOrElse(Consumer<? super T>, Runnable)`, like `ifPresent` but running the given Runnable if the Optional is empty

Some "Stream-related" *instance methods* (all but the first one looking very much like similar *Stream API methods*) are:
* `stream()`
* `filter(Predicate<? super T>)`
* `map(Function<? super T, ? extends U>)`, returning an `Optional<U>`
* `flatMap(Function<? super T, ? extends Optional<? extends U>>)`, returning an `Optional<U>`
