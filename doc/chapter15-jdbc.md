# Chapter 15. JDBC

See [OCP Java SE 17 Developer Study Guide](https://www.amazon.com/Oracle-Certified-Professional-Developer-Study/dp/1119864585/ref=sr_1_1?crid=1GIZNHYFXHAK4&dib=eyJ2IjoiMSJ9.Mz5O0lUSaZhUZ-O1Mi__dRPfXHL9GM_CfZ3JDTz910a2d8XI7Vsfj7zwcywJAfMcubfCglH02m8PwlAk_DORk8SS5460zaDP1fskFDX4sUiFVR4pxE1Ln0VIY-g5awTQaOJKp4t0Y1HchXkrw0HtOeVSHg3dHG8Jql9TibGCj-WeXYyNdMp4zWtgM4EimHpl4wvlJZufvGpNjNEmXIObAd2B1mp1skt5k7v_B-k_Ip4.bRERgxl7gsekO5AihUKuOeT_yoO6Bsg7jHigb4sjHEM&dib_tag=se&keywords=ocp+java+se17&qid=1714573695&sprefix=ocp+java+%2Caps%2C192&sr=8-1).

This chapter is about *JDBC* in Java, which is the *Java Database Connectivity API*. The JDBC API is in *module*
[java.sql](https://docs.oracle.com/en/java/javase/17/docs/api/java.sql/module-summary.html). So this is an exception
to "the rule" that all chapters are about Java language features and/or standard library APIs in module *java.base*.

For those familiar with JDBC and the use of databases from Java code there are still a few things to review from this
chapter. For example:
* How to use class `java.sql.DriverManager`, whereas in practice we would use a `javax.sql.DataSource` (note the different package name)
* How to create a *JDBC URL*, in order to connect to a database

JDBC works by sending *SQL commands* to a *relational database*, which is organized in *tables* of *rows* all having the
same *columns* (within a table). Other database access APIs than JDBC still use JDBC under the hood at some point.

The introduction to relational databases and SQL is not summarized here. This summary of chapter 15 only does into JDBC.

### Introducing the interfaces of JDBC

The 5 core *interfaces* in the JDBC API are:
* `java.sql.Driver`, which is the database driver that establishes the connection to the database
* `java.sql.Connection`, which is the "handle" through which we can send SQL commands to the database
* `java.sql.PreparedStatement`, to execute a SQL query
* `java.sql.CallableStatement`, to execute commands stored in the database
* `java.sql.ResultSet`, to read query results

On the classpath we have a JAR file containing the `Driver` implementation through which we can access the database.
For different databases there are different JDBC drivers. In application code we do not see the `Driver`. The JDBC driver
JAR file contains (database-specific) implementations of the core JDBC *interfaces* that have just been mentioned.

### Connecting to a database

To connect to a database, we first need to know the *JDBC URL*. A JDBC URL consists of *3 non-empty colon-separated parts*:
1. The *protocol*, namely "jdbc"
2. The *sub-protocol*, which is typically a product/vendor name, such as "hsqldb"
3. The *sub-name*, which are database-specific connection details

For example, this is a JDBC URL for "hsqldb": `jdbc:hsqldb://localhost:5432/zoo`. This JDBC URL suggests that the "hsqldb"
database engine is listening on the local machine, on port 5432, and that we want to access the "zoo" database.

To get a database connection, in practice we would use a `javax.sql.DataSource`, configured to use the JDBC URL.
For purposes of the OCP certification, a `java.sql.DriverManager` is used instead.

To get a database connection as `java.sql.Connection`, class `java.sql.DriverManager` offers some *static methods*:
* `public static Connection getConnection(String url) throws SQLException`
  * `java.sql.SQLException` is a *checked exception* and is potentially thrown all over the place in JDBC; it is analogous to (checked) `java.io.IOException` in NIO.2
* overloaded `getConnection` static methods taking an additional user and password, or taking additional `java.util.Properties`
  * of course, passing a password in a `getConnection` call is not a secure practice, to say the least

Interface `java.sql.Connection` is *auto-closeable*. It should be used in a try-resources statement, like so:

```java
import java.sql.*;

// In practice we would use a DataSource instead, probably one backed by a connection pool

String jdbcUrl = "jdbc:hsqldb:file:zoo";

try (Connection conn = DriverManager.getConnection(jdbcUrl)) {
    // Use the Connection
}
```

### Working with a PreparedStatement

In order to send a SQL command to the database, we need to get an instance of interface `java.sql.Statement` or one of
the interface subtypes. The 2 interface subtypes are:
* `java.sql.PreparedStatement`
* `java.sql.CallableStatement`

A plain `java.sql.Statement` cannot take parameters, executing the exact SQL given to it. A `java.sql.PreparedStatement`
is far better than a plain `Statement`. Some reasons that `PreparedStatement` is superior are:
* It has better *performance*. After all, a `PreparedStatement` accepts parameters. This enables the database to devise a reusable *query plan* for the query.
* It is more *secure*, because `PreparedStatement` helps protect against *SQL injection*.
* It is more *readable*, compared to string concatenation involving "parameters".
* It is *future-proof*, if at some point a parameter is introduced in a SQL command that currently has no parameters.

We will turn to `java.sql.CallableStatement` later.

A `PreparedStatement` can be obtained from a `java.sql.Connection` with the following *instance method*:
* `public PreparedStatement prepareStatement(String sql) throws SQLException`
* there are several overloads of the `prepareStatement` call, that we will not go into here

Note that we have to pass a SQL string to method `prepareStatement`. It typically has parameters. The prepared statement
*does not run yet* after this call. Parameters, if any, have not yet been provided to the prepared statement.

Like `java.sql.Connection`, interface `java.sql.PreparedStatement` is *auto-closeable*.

This leads to the following "template code":

```java
import java.sql.*;

// In practice we would use a DataSource instead, probably one backed by a connection pool

String jdbcUrl = "jdbc:hsqldb:file:zoo";

// Some SQL string
String sql = "SELECT * FROM names";

try (Connection conn = DriverManager.getConnection(jdbcUrl);
     PreparedStatement ps = conn.prepareStatement(sql)) {
    // Use the PreparedStatement (fill parameters, if any, run the SQL, and get results, if any)
    // After that, do more with the same Connection, if needed
}
```

We will get to setting `PreparedStatement` parameters soon. To run a prepared statement, interface `java.sql.PreparedStatement`
has the following *instance methods*, for example:
* `public ResultSet executeQuery() throws SQLException`
  * all parameters must be set before executing the query (no more, no less), or else a `SQLException` is thrown
  * if the SQL is not a SQL SELECT statement, a `SQLException` is thrown (but there are many more reasons that a `SQLException` can be thrown)
* `public int executeUpdate() throws SQLException`, returning the (affected) row count, if applicable, and zero otherwise
  * all parameters must be set before executing the query (no more, no less), or else a `SQLException` is thrown
  * if the SQL is a SELECT query rather than a INSERT/UPDATE/DELETE SQL query, a corresponding `SQLException` is thrown
* `public boolean execute() throws SQLException`, returning true if the first result is a `ResultSet`
  * all parameters must be set before executing the query (no more, no less), or else a `SQLException` is thrown
  * the SQL can be anything that is accepted by either method `executeQuery` or `executeUpdate`
  * if `true` is returned, get the first result (as `ResultSet`) by calling `Statement` method `getResultSet()`
  * if `false` is returned, get the first result by calling `Statement` method `getUpdateCount()`
  * if `Statement` method `getMoreResults()` returns `true`, repeat this process of getting a result set or update count

#### Working with parameters

The SQL strings can contain *bind variables*, in the form of question marks. They are placeholders that let us provide
the actual values at runtime. We can bind values to these placeholders with `PreparedStatement` instance methods like:
* `public void setString(int parameterIndex, String x) throws SQLException`
  * the database type could be CHAR, VARCHAR etc.
* `public void setInt(int parameterIndex, int x) throws SQLException`
  * the database type could be INTEGER etc.
* `public void setLong(int parameterIndex, long x) throws SQLException`
  * the database type could be BIGINT etc.
* `public void setDouble(int parameterIndex, double x) throws SQLException`
  * the database type could be DOUBLE etc.
* `public void setBoolean(int parameterIndex, boolean x) throws SQLException`
  * the database type could be BOOLEAN etc.
* `public void setNull(int parameterIndex, int sqlType) throws SQLException`
  * the database type could be anything
  * this method is overloaded
* `public void setObject(int parameterIndex, Object x) throws SQLException`
  * the database type could be anything
  * it is better to use more specific parameter setter methods, but in theory they could be replaced by this one
  * this method has many overloads
* and many more similar methods, for XML, dates/times, BLOBs, CLOBs, byte arrays, etc.

Be very careful with using the correct *parameter indices*. These parameter indices *start counting from 1, not zero*!

Here is an example (from the book):

```java
import java.sql.*;

public static void register(Connection conn, int key, int type, String name) throws SQLException {
    String sql = "INSERT INTO names VALUES (?, ?, ?)";

    try (PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, key); // the first bind variable is at position 1!
        ps.setString(3, name);
        ps.setInt(2, type); // that's ok (using position 2 after 3, if we pass the right value)
        // now, before executing the SQL, all parameters must be set
        ps.executeUpdate(); // ignoring the (int) result
    }
}
```

If we want to update multiple records in the database, we could reuse the same `PreparedStatement`, and repeat the following steps:
1. Set all SQL string parameters
2. Run the SQL command

If we want to save network trips to the database, we could *batch SQL commands*. Instead of multiple `executeUpdate()` calls
we would make `addBatch()` calls each time after binding the SQL variables. At the end, we would call method
`executeBatch()` to send the batch to the database.

### Getting data from a ResultSet

A pattern to iterate through a `java.sql.ResultSet` could look like this:

```java
import java.sql.*;

// In practice we would use a DataSource instead, probably one backed by a connection pool

String jdbcUrl = "jdbc:hsqldb:file:zoo";

// Some SQL string
String sql = "SELECT * FROM names";

try (Connection conn = DriverManager.getConnection(jdbcUrl)) {
    try (PreparedStatement ps = conn.prepareStatement(sql)) {
        // Set parameters on the PreparedStatement, if any (not for the parameter-less SQL above)
        try (ResultSet rs = ps.executeQuery(sql)) {
            while (rs.next()) {
                // Process the next row in the ResultSet, using methods like getString, getInt etc.
            }
        }
    }
}
```

The `ResultSet` has a *cursor*. Initially the cursor points to a position *before* (and *not at*) the first row. Always
call `ResultSet.next()` before processing a row in the result set (including the first time). If `ResultSet.next()` returns
`false`, there is no more data to process in the result set.

To retrieve data from a row in the result set, there are pairs of methods where one method uses a *one-based column index*
and the other method uses a *column name*. Probably the latter method is preferred. So if the SQL query is
`SELECT id, name FROM exhibits` we can retrieve the "name" column of a row with:
* `ResultSet.getString(2)`, or:
* `ResultSet.getString("name")`

If the SQL query is `SELECT count(*) FROM exhibits` we can retrieve this count as follows:

```java
if (rs.next()) {
    int count = rs.getInt(1);
    // ...
}
```

If the SQL query is `SELECT count(*) AS count FROM exhibits`, we could have used `rs.getInt("count")` instead.

Typing errors in column names when retrieving data from a result set row will lead to a `java.sql.SQLException`. So does using
zero as column index (remember: *indexing in JDBC is 1-based*). So does failing to call `ResultSet.next()` before processing
the first row (at that point the cursor points before the first row, not at the first row). So does using a closed result
set. In other words, many different kinds of issues can lead to a (checked) `SQLException`, whether caused by the programmer
using JDBC or not.

Some `ResultSet` "get" method pairs are:
* `public boolean getBoolean(int columnIndex) throws SQLException` and `public boolean getBoolean(String columnLabel) throws SQLException`
* analogously, `getDouble(int)` and `getDouble(String)`, both returning a `double`
* analogously, `getInt(int)` and `getInt(String)`, both returning an `int`
* analogously, `getLong(int)` and `getLong(String)`, both returning a `long`
* analogously, `getObject(int)` and `getObject(String)`, both returning a `java.lang.Object`, with several overloads (which work in many cases, but are typically less easy to use than "more specific" "getter" methods)
* analogously, `getString(int)` and `getString(String)`, both returning a `java.lang.String`, which works in many cases (even for numeric data, although "more specific" "getter" methods are preferred for numeric data)
* and many more

### Calling a CallableStatement

Many database products support *stored procedures*, which is pre-compiled code stored in the database.
We are already familiar with instance method `Connection.prepareStatement(String)`. In order to invoke a stored procedure,
we use *instance method* `Connection.prepareCall(String)` instead. It returns an instance of *interface*
`java.sql.CallableStatement`.

For example:

```java
import java.sql.*;

String sql = "{call read_e_names()}"; // SQL to call a stored procedure

try (CallableStatement cs = conn.prepareCall(sql)) {
    // Set parameters on the CallableStatement ...
    // In this example, we want a ResultSet to process (that depends on the stored procedure, of course)
    try (ResultSet rs = cs.executeQuery()) {
        // Process the ResultSet ...
    }
}
```

Interface `java.sql.CallableStatement` extends interface `java.sql.PreparedStatement`, so it has the same methods for
*setting parameters*. The familiar methods to set parameters (e.g. `setString`, `setInt` etc.) are used to set the *IN*
parameters of a stored procedure.

Interface `CallableStatement` adds overloads of those methods that take a *parameter name*, though. Interface `PreparedStatement`
does not have those "setter" method overloads. On `PreparedStatement` we have to use parameter positions (1-based).

For example, let the SQL be `{call read_names_by_letter(?)}`. The question mark is the parameter that must be bound to
a value, for example like so: `cs.setString("prefix", "Z")` (example from the book). This also works: `cs.setString(1, "Z")`.

It is also possible for a stored procedure to have *OUT* parameters. These stored procedures do hot return any result set.
The SQL to call such a stored procedure could look like this: `{?= call magic_number(?)}` (although specific database
products may be more lenient in how to call stored procedures). We can *register an output parameter* by calling a method like this:
* `public void registerOutParameter(int parameterIndex, int sqlType) throws SQLException`, passing one of the constants of `java.sql.Types`
* since Java 1.8: `public void registerOutParameter(int parameterIndex, SQLType sqlType) throws SQLException`, which is more type-safe
  * interface `java.sql.SQLType` is implemented by enum `java.sql.JDBCType`, which contains generic SQL types (called "JDBC types")
* many other `registerOutParameter` overloads, both for *parameter indices* and *parameter names*

When registering an *OUT* parameter, the stored procedure is then typically called with instance method `CallableStatement.execute()`.

A stored procedure can also have *INOUT* (in-out) parameters. In that case *binding an input parameter* and *registering
an output parameter* are combined for the same parameter, either based on parameter index or parameter label.

Interface `java.sql.Connection` has method overloads for methods `prepareStatement` and `prepareCall`, taking an additional
"resultSetType" integer constant and "resultSetConcurrency" integer constant, in that order (first "type", then "concurrency").

The "resultSetType" integer constants are:
* `ResultSet.TYPE_FORWARD_ONLY`
* `ResultSet.TYPE_SCROLL_INSENSITVE`, meaning that we can go through the result set in any order, without seeing any changes made to the underlying database table
* `ResultSet.TYPE_SCROLL_SENSITIVE`, meaning that we can go through the result set in any order, while seeing changes made to the underlying database table

The "resultSetConcurrency" integer constants are:
* `ResultSet.CONCUR_READ_ONLY`, meaning that the result set cannot be updated
* `ResultSet.CONCUR_UPDATABLE`, meaning that the result set can be updated

### Controlling data with transactions

TODO

### Closing database resources

TODO
