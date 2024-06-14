/*
 * Copyright 2024-2024 Chris de Vreeze
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * JDBC support class, to make the use of JDBC more friendly through wrapping of checked SQLException instances.
 *
 * @author Chris de Vreeze
 */
public class JdbcSupport {

    private JdbcSupport() {
    }

    // Unchecked SQL exception

    public static final class UncheckedSQLException extends RuntimeException {

        public UncheckedSQLException(String message, SQLException cause) {
            super(Objects.requireNonNull(message), Objects.requireNonNull(cause));
        }

        public UncheckedSQLException(SQLException cause) {
            super(Objects.requireNonNull(cause));
        }
    }

    // Functional interfaces

    @FunctionalInterface
    public interface JdbcFunction<T, R> {

        R apply(T value) throws SQLException;
    }

    @FunctionalInterface
    public interface ConnectionFunction<R> extends JdbcFunction<Connection, R> {
    }

    @FunctionalInterface
    public interface PreparedStatementFunction<R> extends JdbcFunction<PreparedStatement, R> {
    }

    @FunctionalInterface
    public interface ResultSetFunction<R> extends JdbcFunction<ResultSet, R> {
    }

    @FunctionalInterface
    public interface JdbcSupplier<T> {

        T get() throws SQLException;
    }

    @FunctionalInterface
    public interface ConnectionSupplier extends JdbcSupplier<Connection> {
    }

    @FunctionalInterface
    public interface JdbcConsumer<T> {

        void accept(T value) throws SQLException;
    }

    @FunctionalInterface
    public interface ConnectionConsumer extends JdbcConsumer<Connection> {
    }

    // Conversion functions

    public static <T, R> Function<T, R> toFunction(JdbcFunction<T, R> f) {
        return conn -> {
            try {
                return f.apply(conn);
            } catch (SQLException e) {
                throw new UncheckedSQLException(e);
            }
        };
    }

    public static <R> Function<Connection, R> toConnectionFunction(ConnectionFunction<R> f) {
        return toFunction(f);
    }

    public static <R> Function<PreparedStatement, R> toPreparedStatementFunction(PreparedStatementFunction<R> f) {
        return toFunction(f);
    }

    public static <R> Function<ResultSet, R> toResultSetFunction(ResultSetFunction<R> f) {
        return toFunction(f);
    }

    public static <T> Supplier<T> toSupplier(JdbcSupplier<T> supplier) {
        return () -> {
            try {
                return supplier.get();
            } catch (SQLException e) {
                throw new UncheckedSQLException(e);
            }
        };
    }

    public static Supplier<Connection> toConnectionSupplier(ConnectionSupplier suppl) {
        return toSupplier(suppl);
    }

    public static <T> Consumer<T> toConsumer(JdbcConsumer<T> c) {
        return v -> {
            try {
                c.accept(v);
            } catch (SQLException e) {
                throw new UncheckedSQLException(e);
            }
        };
    }
}
