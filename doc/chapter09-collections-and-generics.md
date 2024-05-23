# Chapter 9. Collections and Generics

See [OCP Java SE 17 Developer Study Guide](https://www.amazon.com/Oracle-Certified-Professional-Developer-Study/dp/1119864585/ref=sr_1_1?crid=1GIZNHYFXHAK4&dib=eyJ2IjoiMSJ9.Mz5O0lUSaZhUZ-O1Mi__dRPfXHL9GM_CfZ3JDTz910a2d8XI7Vsfj7zwcywJAfMcubfCglH02m8PwlAk_DORk8SS5460zaDP1fskFDX4sUiFVR4pxE1Ln0VIY-g5awTQaOJKp4t0Y1HchXkrw0HtOeVSHg3dHG8Jql9TibGCj-WeXYyNdMp4zWtgM4EimHpl4wvlJZufvGpNjNEmXIObAd2B1mp1skt5k7v_B-k_Ip4.bRERgxl7gsekO5AihUKuOeT_yoO6Bsg7jHigb4sjHEM&dib_tag=se&keywords=ocp+java+se17&qid=1714573695&sprefix=ocp+java+%2Caps%2C192&sr=8-1).

This chapter is about Java *collections* and *generics*.

### Using common collection APIs

A *collection* is a group of objects contained in a single object.

The 4 *main collection interfaces* in the *Java Collections Framework* are:
* Interface `java.util.List<E>`, which is an *ordered* collection allowing *duplicate elements*
* Interface `java.util.Set<E>`, which (like a *set in mathematics*) is a collection that *does not allow any duplicate elements* (and typically is *unordered*)
* Interface `java.util.Queue<E>`, which is a collection that *orders its elements in a specific order for processing*
* Interface `java.util.Map<K, V>`, which *maps keys to values* and does *not allow duplicate keys*

Of these interfaces, all but `Map<K, V>` extends `java.util.Collection<E>`.

The *diamond operator* allows us to write:

```java
// The diamond operator can (only) be used in the RHS of an assignment operation
Map<Long, List<String>> mapOfLists = new HashMap<>();
```

Some common `java.util.Collection<E>` methods (whose precise semantics may depend on the interface subtype) are:
* `add(E)`, returning `true` if the parameter value has indeed (just) been added to the collection
* `remove(Object)`, removing a single matching element, and returning `true` if removal was successful
* `isEmpty()` and `size()`
* `clear()`, removing all elements from the collection
* `contains(Object)`, returning `true` if the collection contains the given elements, based on `Object.equals()`
* `removeIf(Predicate<? super E>)`, removing all elements matching the given condition
* `forEach(Consumer<? super E>)` (from supertype `Iterable<E>`), as an alternative to loops (with or without the use of a `Iterable.iterator()`)

Collections have *overridden* `equals(Object)` for equality comparisons on collections. Note that Lists and Sets behave
differently in such equality comparisons.

### Using the List interface

TODO
