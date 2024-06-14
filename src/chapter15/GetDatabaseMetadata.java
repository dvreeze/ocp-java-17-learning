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

package chapter15;

import java.sql.*;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static jdbc.JdbcSupport.toFunction;
import static jdbc.JdbcSupport.toSupplier;

/**
 * Gets database metadata for the "Zoo" HSQL database.
 *
 * @author Chris de Vreeze
 */
public class GetDatabaseMetadata {

    public record ColumnMetaData(
            String tableCategory,
            String tableSchema,
            String tableName,
            String columnName,
            String columnType,
            String typeName,
            String isNullable) {
    }

    public record TableMetaData(String tableName, List<ColumnMetaData> columns) {
        public TableMetaData {
            if (!columns.stream().map(ColumnMetaData::tableName).distinct().toList().equals(List.of(tableName))) {
                throw new IllegalArgumentException("Data inconsistency with respect to table name");
            }
        }
    }

    public record MetaData(
            String driverName,
            String driverVersion,
            String jdbcUrl,
            String jdbcConnection,
            List<TableMetaData> tables) {
    }

    private static final String JDBC_URL = "jdbc:hsqldb:file:zoo";

    private static MetaData getDatabaseMetaDataInternal(Supplier<Connection> connSupplier) throws SQLException {
        try (Connection conn = connSupplier.get()) {
            DatabaseMetaData metaData = conn.getMetaData();

            try (ResultSet columnRs = metaData.getColumns(null, null, null, null)) {
                List<ColumnMetaData> columns = Stream.generate(toSupplier(columnRs::next)).takeWhile(b -> b)
                        .map(
                                toFunction(
                                        ignored -> new ColumnMetaData(
                                                columnRs.getString("TABLE_CAT"),
                                                columnRs.getString("TABLE_SCHEM"),
                                                columnRs.getString("TABLE_NAME"),
                                                columnRs.getString("COLUMN_NAME"),
                                                columnRs.getString("DATA_TYPE"),
                                                columnRs.getString("TYPE_NAME"),
                                                columnRs.getString("IS_NULLABLE")))).toList();

                List<TableMetaData> tables =
                        columns.stream()
                                .collect(Collectors.groupingBy(
                                        ColumnMetaData::tableName,
                                        Collectors.collectingAndThen(
                                                Collectors.toList(),
                                                cols -> new TableMetaData(cols.get(0).tableName(), cols))))
                                .values()
                                .stream()
                                .toList();
                return new MetaData(
                        metaData.getDriverName(),
                        metaData.getDriverVersion(),
                        metaData.getURL(),
                        conn.toString(),
                        tables);
            }
        }
    }

    public static MetaData getDatabaseMetadata(Supplier<Connection> connSupplier) {
        return toSupplier(() -> getDatabaseMetaDataInternal(connSupplier)).get();
    }

    public static void main(String[] args) {
        MetaData metaData = getDatabaseMetadata(toSupplier(() -> DriverManager.getConnection(JDBC_URL)));
        System.out.printf("Driver name: %s%n", metaData.driverName());
        System.out.printf("Driver version: %s%n", metaData.driverVersion());
        System.out.printf("JDBC URL: %s%n", metaData.jdbcUrl());
        System.out.printf("JDBC Connection: %s%n", metaData.jdbcConnection());

        System.out.println();
        System.out.printf("Number of tables: %d%n", metaData.tables().size());

        metaData.tables().forEach(t -> {
            System.out.println();
            System.out.printf("Table: %s%n", t.tableName());

            t.columns().forEach(c -> System.out.printf("Column: %s%n", c));
        });
    }
}
