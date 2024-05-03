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

### Expression statements and blocks

In *control flow statements* the statements can typically be "single statements" or blocks of zero or more statements,
and the programmer is free to choose "single statements" or "block statements".

Typically, a "single statement" used directly (instead of as one of the individual statements of a "block statement") is
an *expression statement*, that is, an expression followed by a semicolon, turning it into a statement.

The expression in an expression statement is very often an *assignment expression* or a *method call expression*.

For expression statements, see [ExpressionStatementTree](https://docs.oracle.com/en/java/javase/17/docs/api/jdk.compiler/com/sun/source/tree/ExpressionStatementTree.html).
Don't forget to look at the relevant links to the JLS.

Note that "single statements" (and certainly expression statements) mostly *end with a semicolon*. This is something
to be aware of when answering exam questions.

*Block statements* combine zero or more statements, such as "single statements", *enclosed in braces*. The closing brace
is *not followed by a semicolon*. So be careful when checking statement syntax, w.r.t. braces and semicolons.

For block statements, see [BlockTree](https://docs.oracle.com/en/java/javase/17/docs/api/jdk.compiler/com/sun/source/tree/BlockTree.html).

### If-statements

*Syntactically*, an *if-statement*:
* may or may not have an *else-branch*
* the *condition* must be enclosed in *parentheses*
* the branch statement or statements can be "single statements" or block statements (see preceding section)

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
* or "var", if it resolves to one of the types mentioned above

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
Also, the "break statements" are gone (they are not needed for switch expressions), and we may see "yield expressions" instead.

The "case branches" are:
* either *case expression statements* that by definition *end with a semicolon*
* or *case blocks*, which are block statements

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

### Do-while-loops

### For-loops

### Enhanced-for-loops

### Break and continue
