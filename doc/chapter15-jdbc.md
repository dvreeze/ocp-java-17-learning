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
