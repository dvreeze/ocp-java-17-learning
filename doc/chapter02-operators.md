# Chapter 2. Operators

See [OCP Java SE 17 Developer Study Guide](https://www.amazon.com/Oracle-Certified-Professional-Developer-Study/dp/1119864585/ref=sr_1_1?crid=1GIZNHYFXHAK4&dib=eyJ2IjoiMSJ9.Mz5O0lUSaZhUZ-O1Mi__dRPfXHL9GM_CfZ3JDTz910a2d8XI7Vsfj7zwcywJAfMcubfCglH02m8PwlAk_DORk8SS5460zaDP1fskFDX4sUiFVR4pxE1Ln0VIY-g5awTQaOJKp4t0Y1HchXkrw0HtOeVSHg3dHG8Jql9TibGCj-WeXYyNdMp4zWtgM4EimHpl4wvlJZufvGpNjNEmXIObAd2B1mp1skt5k7v_B-k_Ip4.bRERgxl7gsekO5AihUKuOeT_yoO6Bsg7jHigb4sjHEM&dib_tag=se&keywords=ocp+java+se17&qid=1714573695&sprefix=ocp+java+%2Caps%2C192&sr=8-1).

Topics in chapter 2 (from this OCP Java SE 17 Developer Study Guide) are summarized below.

More information can be found in the (more low-level)
[Java AST API](https://docs.oracle.com/en/java/javase/17/docs/api/jdk.compiler/com/sun/source/tree/package-summary.html),
which contains links to the Java Language specification for specific language constructs.
In particular, see [UnaryTree](https://docs.oracle.com/en/java/javase/17/docs/api/jdk.compiler/com/sun/source/tree/UnaryTree.html)
and [BinaryTree](https://docs.oracle.com/en/java/javase/17/docs/api/jdk.compiler/com/sun/source/tree/BinaryTree.html),
with their links to corresponding sections in the JLS (Java Language Specification). For assignment, see
[AssignmentTree](https://docs.oracle.com/en/java/javase/17/docs/api/jdk.compiler/com/sun/source/tree/AssignmentTree.html)
and [CompoundAssignmentTree](https://docs.oracle.com/en/java/javase/17/docs/api/jdk.compiler/com/sun/source/tree/CompoundAssignmentTree.html).

### Operators in general

Java *operations* are expressions containing *operators* applied to *operands*, with the latter being
*variables* or *values*. Many operators are widely known from mathematics, like addition, multiplication etc.
Operators compute a value, conceptually much like a mathematical function where the operands are the function parameters.

The following is true for operators in Java:
* Operators are *unary*, *binary* or *ternary* (that are applied to one, two or three operands, respectively)
* Operators apply to operands of one or more types (which are primitive types and/or reference types)
* The *associativity* of operators can be *left-to-right* or *right-to-left*
* *Operator precedence* determines which operators are evaluated in what order
* This order can be changed by the use of *parentheses* (which form "open-close pairs", and must nest properly)
* *Assignment operators* are also operators; they have *side effects*, namely updating a variable
* *Post-unary operators* and *pre-unary operators* also have *side effects*, like assignments do
* Some operators are *short-circuiting*, where evaluation stops when the result is already known

The following table of Java operator precedence (from the above-mentioned study guide) should be memorized by heart:

| Operator             | Symbols and examples        | Associativity |
|----------------------|-----------------------------| ------------- |
| Post-unary           | expression++, expression--  | left-to-right |
| Pre-unary            | ++expression, --expression  | left-to-right |
| Other unary          | -, !, ~, +, (type)          | right-to-left |
| Cast                 | (Type) reference            | right-to-left |
| Multiply etc.        | *, /, %                     | left-to-right |
| Addition etc.        | +, -                        | left-to-right |
| Shift                | <<, >>, >>>                 | left-to-right |
| Relational           | <, >, <=, >=, instanceof    | left-to-right |
| Equal-to etc.        | ==, !=                      | left-to-right |
| Logical AND          | &                           | left-to-right |
| Logical exclusive OR | ^                           | left-to-right |
| Logical inclusive OR | \|                          | left-to-right |
| Conditional AND      | &&                          | left-to-right |
| Conditional OR       | \|\|                        | left-to-right |
| Ternary              | booleanExpr ? expr1 : expr2 | right-to-left |
| Assignment           | =, +=, -=, *=, /=, %= etc.  | right-to-left |
| Arrow                | ->                          | right-to-left |

The arrow operator is used in `switch` expressions. By and large, we already know operator precedence. Leaving out
some rows and combining others, we get this table of operator precedence:

| Operator            | Symbols and examples        | Associativity |
|---------------------|-----------------------------|---------------|
| Post/pre-unary      | expression++ etc.           | left-to-right |
| Other unary         | -, !, ~, +, (type)          | right-to-left |
| Cast                | (Type) reference            | right-to-left |
| Multiply etc.       | *, /, %                     | left-to-right |
| Addition etc.       | +, -                        | left-to-right |
| Relational          | <, >, <=, >=, instanceof    | left-to-right |
| Equal-to etc.       | ==, !=                      | left-to-right |
| Logical AND         | &                           | left-to-right |
| Logical XOR, OR     | ^, \|                       | left-to-right |
| Conditional AND     | &&                          | left-to-right |
| Conditional OR      | \|\|                        | left-to-right |
| Ternary             | booleanExpr ? expr1 : expr2 | right-to-left |
| Assignment          | =, +=, -=, *=, /=, %= etc.  | right-to-left |

### Unary operators

Below *int types* stands for primitive integer numeric types, which includes `char` (besides `byte`, `short`, `int` and `long`).
Also, below *numeric types* stand for primitive numeric types, so "int types" along with `double` and `float`.

It also pays off to know that signed "int" types `byte`, `short`, `int` and `long` contain 1, 2, 4 and 8 (8-bit) bytes, respectively.
Type `char` (which could be seen as an unsigned integer type) contains 2 bytes.

| Operator                 | Examples     | Description                                     | Operand type    |
| ------------------------ | ------------ |-------------------------------------------------|-----------------|
| Logical complement (NOT) | !b           | Inverts a boolean's logical value               | boolean         |
| Bitwise complement       | ~n           | Inverts zeroes and ones in number               | "int types"     |
| Plus                     | +n           | Keeps the sign of a numeric expression the same | "numeric types" |
| Minus                    | -n           | Reverses the sign of a numeric expression       | "numeric types" |
| Increment                | n++, ++n     | Increments a value by 1                         | "numeric types" |
| Decrement                | n--, --n     | Decrements a value by 1                         | "numeric types" |
| Cast                     | (String) obj | Casts a value to a specific type                | any type        |

The *post-unary* operators *post-increment* and *post-decrement* return the *original value*, and apply the side effect
of incrementing/decrementing after the expression has been evaluated.

The *pre-unary* operators *pre-increment* and *pre-decrement* first apply the side effect of incrementing/decrementing,
and then returns the *new value* as expression evaluation result.

The *bitwise complement* operator (`~`) *flips the bits*. This example shows it in action:

```java
var maxByte = (int) Byte.MAX_VALUE; // 127
var minByte = (int) Byte.MIN_VALUE; // -128

// Indeed, they are each other's bitwise complement
System.out.println(Integer.toBinaryString(maxByte)); // prints "1111111"
System.out.println(Integer.toBinaryString(minByte)); // prints "11111111111111111111111110000000"

// It is easy to compute the bitwise complements
var check1 = (minByte == -1 * maxByte - 1); // true
var check2 = (maxByte == -1 * minByte - 1); // true
```

### Binary arithmetic operators

The following holds for binary arithmetic operators:
* They operate on numeric values
* The binary arithmetic operators are:
  * Addition (`+`)
  * Subtraction (`-`)
  * Multiplication (`*`)
  * Division (`/`)
  * Modulus (`%`)
* Division for integer types returns an integer, namely a "floor value" (unless dividing by zero, for integer types)
  * Division by zero for `double` and `float` returns `Infinity` (or `-Infinity`) instead of throwing an exception!
  * In particular, `Double.valueOf(5.0 / 0).isInfinite()` returns `true`
  * So does `Double.valueOf(5.0 / 0).equals(Double.POSITIVE_INFINITY)`
  * Or `5.0 / 0 == Double.POSITIVE_INFINITY`
* There is also a `+` addition operator for Strings, for string concatenation
* Note (for integer types) that if `x / y == i && x % y == j`, then `i * y + j == x`
* Also note (for integer types) that `-x % y` is equal to `-(x % y)`
* It's very important to know the *numeric promotion rules* below

The *numeric promotion rules* for (numeric) operands of binary arithmetic operations are:
1. If the 2 operands have *different numeric types*, where one type is a "subset" of the other type, one operand will be promoted to a value of the larger type
2. If one operand is *integral* and the other *floating-point* (double or float), the integral value will be promoted to the floating-point's data type
3. *Smaller data types than int* (i.e. byte, short, char) are first promoted to int, *if used with a variable rather than a value*
4. After promotion and the operands having the same type, the *resulting value* will have that same type

### Assignment operators

*Assignment* assigns a value on the right to a variable on the left. Example: `n = 3`. The assignment also returns a value,
namely the evaluation result of the assignment.

Compile-time constants (`128`, `127 + 1`) that do not fit in the target type (in this case `byte`) lead to a compiler error
when assigning the value to a variable of the target type (in this case `byte b = 128`), unless we use a "down-cast".

We can use *cast* operators to cast values to "smaller" types, risking *overflow/underflow for primitive numeric types*
and *(compilation/runtime) errors for reference types*.

As an example of overflow/underflow, `(byte) (Byte.MAX_VALUE + 1)` returns `-128`.

Assignments like `n *= 2` are like `n = n * 2`, except that the abbreviated form does a down-cast, if needed, instead of
causing a compilation error. Compound assignments such as `*=`, `+=` etc. are only allowed by the compiler if the variable
on the left-hand side *has already been declared (and initialized) before*.

It's ok to assign integer literals (not "long literals"!) to variables of small integer types like `short` and `byte`,
provided that the integer literal fits in the number range allowed by the type. So this is ok:

```java
byte b = 45; // max: 127
b = -112; // min: -128

byte b2 = 'a'; // Assigns 97 to byte variable b2, which is well within the allowed range
```

A nice puzzle is this combination of ("self"-)assignment and post-increment:

```java
int x = 4;
x = x++; // returns 4, not 5; the post-increment got lost
```

Or this:

```java
var i = 0;
int[] arr = new int[] { 10, 20 };
// This first resolves the LHS as "arr[0]", after that evaluates the RHS assignment, and assigns 30 to "arr[0]"
arr[i] = i = 30;
```

### Comparison and "boolean logic" operators

The *comparison operators* "equal-to" (`==`) and "not-equal-to" (`!=`) mean different things for primitives and reference types:
* For *primitives* the operand types must "match", and equality is value equality for primitive data
* For *reference types* `true` is returned if both operands reference the same object or if both operands are `null` references
  * If the compiler knows the operand types are not compatible (e.g. `String` and `Number`), the comparison does not compile

Equality comparisons for reference types should normally be done using method `Object.equals(Object)`, instead of using comparison
operators. This method can compare "anything" with "anything" (for better or for worse).

*Relational operators* `<`, `<=` etc. only take numeric operands.

The *instanceof* relational operator works on reference types. True is returned if the value to check is not `null` and
can safely be cast to the target type. This operator can be used to make casting safer. If the compiler can determine that
a cast cannot be successful from a typing perspective, a compilation error will result.

*Logical operators* `&`, `|` and `^` mean different things for boolean types and numeric types:
* For boolean types, they are the logical operators "logical AND", "logical OR" and "logical XOR", respectively
* For numeric types, they are bitwise operators (that we do not have to know about, other than the fact that they exist)

*Conditional operators* `&&` and `||` (conditional inclusive OR) take boolean operands. They are *short-circuit* operators
that do not evaluate the right-hand operand if that is not needed for computing the result of the operation.

### Ternary operator

It's like an if-else-statement as expression. The use of parentheses can help readability. If the types of the 2 branches
cannot "match", the compiler will emit an error.
