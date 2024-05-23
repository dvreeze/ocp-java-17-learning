# Chapter 9. Collections and Generics

See [OCP Java SE 17 Developer Study Guide](https://www.amazon.com/Oracle-Certified-Professional-Developer-Study/dp/1119864585/ref=sr_1_1?crid=1GIZNHYFXHAK4&dib=eyJ2IjoiMSJ9.Mz5O0lUSaZhUZ-O1Mi__dRPfXHL9GM_CfZ3JDTz910a2d8XI7Vsfj7zwcywJAfMcubfCglH02m8PwlAk_DORk8SS5460zaDP1fskFDX4sUiFVR4pxE1Ln0VIY-g5awTQaOJKp4t0Y1HchXkrw0HtOeVSHg3dHG8Jql9TibGCj-WeXYyNdMp4zWtgM4EimHpl4wvlJZufvGpNjNEmXIObAd2B1mp1skt5k7v_B-k_Ip4.bRERgxl7gsekO5AihUKuOeT_yoO6Bsg7jHigb4sjHEM&dib_tag=se&keywords=ocp+java+se17&qid=1714573695&sprefix=ocp+java+%2Caps%2C192&sr=8-1).

This chapter is about Java *collections* and *generics*. Thread-safe collections are treated in a later chapter.

### Using common collection APIs

A *collection* is a group of objects contained in a single object.

The 4 *main collection interfaces* in the *Java Collections Framework* are:
* Interface `java.util.List<E>`, which is an *ordered* collection allowing *duplicate elements*
* Interface `java.util.Set<E>`, which (like a *set in mathematics*) is a collection that *does not allow any duplicate elements* (and typically is *unordered*)
* Interface `java.util.Queue<E>`, which is a collection that *orders its elements in a specific order for processing*
* Interface `java.util.Map<K, V>`, which *maps keys to values* and does *not allow duplicate keys*

Of these interfaces, all but `Map<K, V>` extend `java.util.Collection<E>`.

The *diamond operator* allows us to write:

```java
// The diamond operator can (only) be used in the RHS of an assignment operation
Map<Long, List<String>> mapOfLists = new HashMap<>();

// Corner-case (that compiles, and assumes type List<Object>); indeed, the diamond operator is still used in the RHS of the assignment
var xs = new ArrayList<>();
```

Some common `java.util.Collection<E>` methods (whose precise semantics may depend on the interface subtype) are:
* `add(E)`, returning `true` if the parameter value has indeed (just) been added to the collection
* `remove(Object)`, removing a single matching element, and returning `true` if removal was successful
* `isEmpty()` and `size()` (the latter returning the current size)
* `clear()`, removing all elements from the collection
* `contains(Object)`, returning `true` if the collection contains the given elements, based on `Object.equals()`
* `removeIf(Predicate<? super E>)`, removing all elements matching the given condition
* `forEach(Consumer<? super E>)` (from supertype `Iterable<E>`), as an alternative to loops (with or without the use of a `Iterable.iterator()`)

Collections have *overridden* `equals(Object)` for equality comparisons on collections. Note that Lists and Sets behave
differently in such equality comparisons.

### Using the List interface

A *List* is an *ordered collection* that *can have duplicate elements*. Moreover:
* elements can be retrieved and inserted at a specific *int index*, much like arrays
* unlike arrays, the *size* of a List may change after the List has been declared
* List is the "go to" collection (interface) data type, even if we are not interested in the element order 

Two *main implementations* of interface `java.util.List<E>` are:
* Class `java.util.ArrayList<E>`
  * It is like a *resizable array*, growing when elements are added
  * It is optimized for (index-based) element lookup (which takes constant time)
* Class `java.util.LinkedList<E>`, which implements collection interfaces `java.util.List<E>` and `java.util.Deque<T>`
  * Adding elements to beginning and end (or removing elements at beginning and end) takes constant time (using this List as a Deque)
  * Yet looking up elements based on an int index takes linear time

Note that `java.util.Deque<E>` extends interface `java.util.Queue<E>`.

Some static *List factory methods* are:
* `java.util.Arrays.asList(T...)`
  * it returns a *fixed-size* List *backed by an array*; updates to the underlying array are visible in the List and vice versa
  * so elements cannot be added or removed
  * but they can be replaced (either in the List or in the underlying array)
* `java.util.List.of(E...)`
  * it returns an *unmodifiable* List, so elements cannot be added, removed or replaced
* `java.util.List.copyOf(Collection<? extends E>)`
  * returns an *unmodifiable* List containing the same elements as the parameter collection, in its insertion order

The book calls these "unmodifiable" Lists "immutable", which is not correct. *Unmodifiable* collections do not allow
for addition/removal/replacement of elements, but still allow for the contained elements to be mutable. So unmodifiable
collections cause "collection mutator methods" to throw an exception (namely `UnsupportedOperationException`).

Most collection implementations have *two constructors*. For example:

```java
import java.util.LinkedList;

var xs = new LinkedList<String>(); // creating an empty List
var ys = new LinkedList<String>(xs); // creating a copy of another collection
```

Type `ArrayList` has an extra constructor to set the *initial capacity* without adding any elements from the List API perspective.

Some specific methods for interface type *List* (mostly "index-based") are:
* `add(int, E)`, adding an element at the given index, with return type `void`
* `get(int)`, returning the element at the given index
* `remove(int)`, removing the element at the given index (and moving the rest toward the front), returning the removed element
* (default method) `replaceAll(UnaryOperator<E>)`, replacing each element in-place with the result of the operator
* `set(int, E)`, replacing the element at the given index and returning the original element at that index
* (default method) `sort(Comparator<? super E>)`, sorting the List in-place

In these methods, indexes out-of-range lead to an `IndexOutOfBoundsException` (which is "unchecked").

Be careful with the overloaded `remove` methods for `List<Integer>` (recall the rules for method overloading).

Copying a List to an array:
* `List.toArray()`, returning an `Object[]`
* `List.toArray(E[])`, returning an `E[]` (`myList.toArray(new E[0])` will return an array of the right size)

The copy is no longer linked to the original List. Hence, the name *toArray*, instead of "asArray".
