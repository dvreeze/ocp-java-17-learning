# Chapter 3. Making Decisions

See [OCP Java SE 17 Developer Study Guide](https://www.amazon.com/Oracle-Certified-Professional-Developer-Study/dp/1119864585/ref=sr_1_1?crid=1GIZNHYFXHAK4&dib=eyJ2IjoiMSJ9.Mz5O0lUSaZhUZ-O1Mi__dRPfXHL9GM_CfZ3JDTz910a2d8XI7Vsfj7zwcywJAfMcubfCglH02m8PwlAk_DORk8SS5460zaDP1fskFDX4sUiFVR4pxE1Ln0VIY-g5awTQaOJKp4t0Y1HchXkrw0HtOeVSHg3dHG8Jql9TibGCj-WeXYyNdMp4zWtgM4EimHpl4wvlJZufvGpNjNEmXIObAd2B1mp1skt5k7v_B-k_Ip4.bRERgxl7gsekO5AihUKuOeT_yoO6Bsg7jHigb4sjHEM&dib_tag=se&keywords=ocp+java+se17&qid=1714573695&sprefix=ocp+java+%2Caps%2C192&sr=8-1).

Topics in chapter 3 (from this OCP Java SE 17 Developer Study Guide) are summarized below.

More information can be found in the (more low-level)
[Java AST API](https://docs.oracle.com/en/java/javase/17/docs/api/jdk.compiler/com/sun/source/tree/package-summary.html),
which contains links to the Java Language specification for specific language constructs.

In particular, see [Statement](https://docs.oracle.com/en/java/javase/17/docs/api/jdk.compiler/com/sun/source/tree/StatementTree.html).

For the different "control flow" statements, see:
* [IfTree](https://docs.oracle.com/en/java/javase/17/docs/api/jdk.compiler/com/sun/source/tree/IfTree.html)
* [SwitchTree](https://docs.oracle.com/en/java/javase/17/docs/api/jdk.compiler/com/sun/source/tree/SwitchTree.html)
* [WhileLoopTree](https://docs.oracle.com/en/java/javase/17/docs/api/jdk.compiler/com/sun/source/tree/WhileLoopTree.html)
* [DoWhileLoopTree](https://docs.oracle.com/en/java/javase/17/docs/api/jdk.compiler/com/sun/source/tree/DoWhileLoopTree.html)
* [ForLoopTree](https://docs.oracle.com/en/java/javase/17/docs/api/jdk.compiler/com/sun/source/tree/ForLoopTree.html)
* [EnhancedForLoopTree](https://docs.oracle.com/en/java/javase/17/docs/api/jdk.compiler/com/sun/source/tree/EnhancedForLoopTree.html)

For the "switch expressions", see:
* [SwitchExpressionTree](https://docs.oracle.com/en/java/javase/17/docs/api/jdk.compiler/com/sun/source/tree/SwitchExpressionTree.html)

For language constructs related to the above, see:
* [InstanceOfTree](https://docs.oracle.com/en/java/javase/17/docs/api/jdk.compiler/com/sun/source/tree/InstanceOfTree.html)
* [LabeledStatementTree](https://docs.oracle.com/en/java/javase/17/docs/api/jdk.compiler/com/sun/source/tree/LabeledStatementTree.html)
* [BreakTree](https://docs.oracle.com/en/java/javase/17/docs/api/jdk.compiler/com/sun/source/tree/BreakTree.html)
* [ContinueTree](https://docs.oracle.com/en/java/javase/17/docs/api/jdk.compiler/com/sun/source/tree/ContinueTree.html)
* [ReturnTree](https://docs.oracle.com/en/java/javase/17/docs/api/jdk.compiler/com/sun/source/tree/ReturnTree.htmll)

In exam questions, it is very easy to overlook things related to the control flow constructs above.
We have to think like the Java compiler, to a large extent. For example:
1. From the point of view of the raw Java parser, is the *syntax* entirely correct (ignoring types etc.)?
2. From the point of view of *types*, variable *scopes*, *(im)mutability*, *compile-time constants*, etc., is the code correct according to the compiler?
3. Do we see any peculiarities, such as missing "break statements" from the cases in a switch statement?
4. If the compiler was happy, does the code *run* successfully, does the code end etc.?

Especially for switch statements and expressions, there is a lot to check, w.r.t. syntax, types etc.

Below, (simplified) grammars of flow control statements are given. Different alternatives in a grammar rule occur on different lines,
`{}` means zero-or-more, and `[]` means zero-or-one. Terminal symbols (including keywords) are enclosed in double-quotes.

### Expression statements and blocks

In *control flow statements* the statements can typically be "single statements" or blocks of zero or more statements,
and the programmer is free to choose "single statements" or "blocks" (which are statements themselves).

Typically, a "single statement" used directly (instead of as one of the individual statements of a "block") is
an *expression statement*, that is, an expression followed by a semicolon, turning it into a statement:

```
expressionStatement:
    expression ";"
```

The expression in an expression statement is very often an *assignment expression* or a *method call expression*.

For expression statements, see [ExpressionStatementTree](https://docs.oracle.com/en/java/javase/17/docs/api/jdk.compiler/com/sun/source/tree/ExpressionStatementTree.html).
Don't forget to look at the relevant links to the JLS.

Note that "single statements" (and certainly expression statements) mostly *end with a semicolon*. This is something
to be aware of when answering exam questions.

*Blocks* combine zero or more statements, such as "single statements", *enclosed in braces*. The closing brace
is *not followed by a semicolon*. So be careful when checking statement syntax, w.r.t. braces and semicolons.

A much simplified grammar rule for blocks is like this:

```
block:
    "{" { statement } "}"
```

For blocks, see [BlockTree](https://docs.oracle.com/en/java/javase/17/docs/api/jdk.compiler/com/sun/source/tree/BlockTree.html).

### If-statements

*Syntactically*, an *if-statement*:
* may or may not have an *else-branch*
* the *condition* must be enclosed in *parentheses*
* the branch statement or statements can be "single statements" or blocks (see preceding section)

Grammar:

```
ifStatement:
    "if" "(" expression ")" statement
    "if" "(" expression ")" statement "else" statement
```

In nested if-statements, we may encounter the [dangling else](http://www.cs.emory.edu/~cheung/Courses/561/Syllabus/2-C/dangling-else.html)
problem. This is resolved by the following rule (as mentioned in that article): An "else" keyword always associates with
the nearest preceding "if" keyword that does not cause a syntax error.

As for *correct use of types*, the condition must be a *boolean expression*.

In exam questions, indentation is often used to obscure the structure of the if-statement. Don't fall for it. Whitespace
is not evaluated as part of the execution.

### Pattern matching

Java 16 introduced *pattern matching* with the *instanceof* operator. This can be used in the condition of an if-statement,
to shorten the code (by getting rid of the distinction between a type check and the actual cast).

The pattern match looks like a normal "instanceof check" enhanced with a *pattern variable*. The pattern variable should
not be reassigned within its scope.

Pattern matching works only for *strict subtypes* of the declared type of the checked variable (the LHS of the "instanceof"
operator), or else the compiler emits an error. This assumes that we are not mixing classes and interfaces.

Consider the following example:

```java
boolean isJavaProgrammer(Employee employee) {
    return employee instanceof Developer developer && developer.getProgrammingLanguages().contains("Java");
}
```

The compiler understands that after the "conditional AND operator" variable "developer" with type "Developer" is in scope.
This is called *flow scope*. Replace "&&" by "||", and this is no longer true, causing a compilation error.

Consider the same example, written in a more verbose way:

```java
boolean isJavaProgrammer(Employee employee) {
    if (employee instanceof Developer developer) {
        return developer.getProgrammingLanguages().contains("Java");
    } else {
        return false;
    }
}
```

Now the compiler understands that in the if-branch the variable "developer" of type "Developer" is in scope, whereas
in the else-branch that is not the case. This is the same *flow scope* at work. In short, *flow scoping* means that the
variable is only in scope if the compiler can definitely determine its type. Note that this is more complicated than
the scoping rules we have seen so far for variables (for static fields, instance fields, parameters and local variables).

### Switch statements

*Syntactically*, a *switch statement*:
* starts with the "switch" keyword
* followed by the "target expression", which is an *expression* that must be *surrounded by parentheses*
* followed by a "switch block", which is zero or more "cases" *surrounded by a pair of braces* (hence the name "switch block")

*Syntactically*, a (non-default) *switch case*:
* starts with the "case" keyword
* followed by one or more comma-separated "case values" (before Java 14 it could only be one such expression)
* followed by a colon
* followed by a branch, which is zero or more statements
* the last statement should be a "break statement", or else execution "falls through" to the remaining cases, which can be unexpected

The other *switch case* is the "default case", which:
* starts with the "default" keyword
* followed by a colon
* followed by a branch, which is zero or more statements
* there can be only 1 default case at most in the switch statement, or else the compiler emits an error
* this should be the last case of the switch statement, but this is not checked by the compiler
* for consistency, the default case should also end with a "break statement"

As for the *type* of the "target expression", it can only be one of:
* integral primitive types no larger than int, so byte, short, int and char
* or the corresponding wrapper types
* or the type String
* or an enumeration type

A "var" is also allowed, if it resolves to one of the types mentioned above

Each "case value" must be a "compile-time constant". Its type must be *assignable to the type of the target expression*.
With *compile-time constant* we mean:
* either a literal
* or an enum constant
* or a trivial "operation" (such as `3 * 5`) that can easily be resolved by the compiler
* or a final variable initialized with a literal or enum constant

### Switch expressions

Java 14 added *switch expressions*, which are expressions instead of statements. As expressions, their result can be
assigned to a variable, given some requirements are met (ensuring that the switch expression always returns a value).

Syntactically they look very much like switch statements, but the colons in cases are replaced by *arrows* ("->").
Also, the "break statements" are gone (they are not needed for switch expressions), and we may see "yield statements" instead.

The "case branches" are:
* either *case expression statements* that by definition *end with a semicolon*
* or *case blocks*, which are blocks

The case blocks must contain *yield statements*, to make sure that the compiler knows that all code paths in the case block
lead to a value (of the correct type) or throw an exception. If the entire switch expression returns no value (so is of type void),
this is not needed, of course.

A great feature of switch expressions versus switch statements is that there is no more need for break statements,
and that fall-through behaviour is impossible.

As for *typing*, we have the following requirements:
* the type of the "target expression" of the switch expression is restricted in exactly the same way as for switch statements
* the "case values" are also restricted in exactly the same way as for switch statements
* if the switch expression returns a value, this value must be assignable to the expected return type
* for "case blocks" this means that all code paths that don't throw an exception must lead to a "yield statement" returning a value
* to make sure that a value (of the correct type) is always returned, the *default case* is needed, unless the compiler knows it is not needed

Note that if the switch expression is the RHS of an assignment expression used as statement, we will see a semicolon
(ending the expression statement) after a closing brace (of the switch expression).

### While-loops

*While-loops* are statements that loop zero or more times over the loop's statement, depending on a boolean condition that is checked
before each iteration, ending the loop once the condition is false.

*Syntactically*, a *while-statement*:
* starts with the "while" keyword
* followed by a *condition* that must be enclosed in *parentheses*
* followed by the statement to iterate over, which can be a "single statement" or block

Grammar:

```
whileLoop:
    "while" "(" expression ")" statement
```

As for *correct use of types*, the condition must be a *boolean expression*.

These loops are very basic, leaving details like "creation of a loop variable" to the programmer.
Be aware of infinite loops, if the loop condition is not "progressing towards successful termination".

### Do-while-loops

If we want a loop where we iterate one or more times instead of zero of more times, use a *do-while-loop*.

*Syntactically*, a *do-while-statement*:
* starts with the "do" keyword
* followed by the statement to iterate over, which can be a "single statement" or block
* followed by the "while" keyword
* followed by a *condition* that must be enclosed in *parentheses*
* followed by a *semicolon*

Grammar:

```
doWhileLoop:
    "do" statement "while" "(" expression ")" ";"
```

As for *correct use of types*, the condition must be a *boolean expression*.

Like for regular while-loops, be aware of infinite loops.

Note that variables introduced in the statement to iterate over are no longer in scope in the loop condition!

### For-loops

A *for-loop* offers some support in creating and using "loop variables".

*Syntactically*, a *for-loop*:
* starts with the "for" keyword
* followed by the "init-condition-update" part, *enclosed in parentheses*
* followed by the statement to iterate over, which can be a "single statement" or block

The "init-condition-update" part contains exactly *2 (top-level) semicolons*, to separate the "init part" from the
"condition", and the "condition" from the "update part". All those 3 parts can be empty, leaving only 2 semicolons!

The "init part", if present, is either a local variable declaration or a comma-separated list of so-called "statement expressions".
So, in practice, the "init part" is either a local variable declaration or a comma-separated list of assignments or
pre-/post-unary expressions.

The optional condition is an expression, and the optional "update part" is a comma-separated list of these so-called
"statement expressions" (so typically assignments or pre-/post-unary expressions).

Grammar:

```
forLoop:
    "for" "(" [ initializer ] ";" [ condition ] ";" [ update ] ")" statement

initializer:
    statementExpressionList
    localVariableDeclaration

condition:
    expression

update:
    statementExpressionList

statementExpressionList:
    statementExpression { "," statementExpression }
```

As for *correct use of types*, the condition must be a *boolean expression*.

Be careful to not redeclare a variable in the loop "init part", or else the compiler will emit an error.
This makes sense, because there cannot be 2 local variables with the same name in scope at the same time.

Also note that loop variables are no longer in scope after the loop. Finally, do not modify loop variables (to prevent
having "2 owners" of that loop variable).

It is still possible to create infinite loops.

### Enhanced-for-loops

The *enhanced-for-loop* (or *for-each-loop*) has been designed to loop over collections or arrays.

*Syntactically*, a *for-each-loop*:
* starts with the "for" keyword
* followed by en "initialization section" that *must be surrounded by parentheses*
* followed by the statement to iterate over, which can be a "single statement" or block

The "initialization section", within the pair of parentheses:
* starts with a type (can be "var")
* followed by a *variable name*
* followed by a *colon* (and not a keyword like "in"!)
* followed by an expression for the collection or array to loop over

Grammar:

```
enhancedForLoop:
    "for" "(" variable ":" expression ")" statement
```

The collection/array to loop over must either be a *Java array* or a collection implementing *java.lang.Iterable*.
The latter includes most collections, but excludes Maps (from the Collections Framework). Of course the data type
of the element variable must match the element type of the array or collection to loop over.

### Break and continue

It is possible to assign *labels* to statements, in particular looping statements (like for-loops). Then using
*break statements* or *continue statements* we can break out of nested loops or loop iterations when needed.

With *break statements* (with or without mentioned label) we can break out of enclosing loops. With *continue statements*
(with or without mentioned label) we can break out of an iteration of an enclosing loop.

Of course, with a *return statement* we can break out of an entire method body.

Of course, in general "go-to statements" are considered harmful (as argued a long time ago by Edsger Dijkstra).
But these specific "go-to statements" are safe in that they only break out of some enclosing context, or else
the compiler will emit an error. More specifically, if a "continue statement" has no (enclosing) "continue target",
the compiler will consider that an error. A similar remark holds for "break statements" and "break targets".

Note that break/continue/return are all "safe go-to statements" *returning control to a syntactically enclosing context*.
Also note that it makes sense for switch statements to support the break statement inside switch cases, and that
the continue statement makes no sense for switch statements (leading to a compilation error if we try to use it).

If the compiler detects that break/continue/return statements make pieces of code unreachable, the compiler will
emit an error.
