package dev.coph.simplesql.utils;

import dev.coph.simplesql.driver.DriverType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class DatabaseMetadataUtil {

    private DatabaseMetadataUtil() {
    }

    public static DriverType detectDriverType(Connection conn) throws SQLException {
        String url = conn.getMetaData().getURL();
        if (url == null) return null;
        String u = url.toLowerCase();
        if (u.startsWith("jdbc:mysql:")) return DriverType.MYSQL;
        if (u.startsWith("jdbc:mariadb:")) return DriverType.MARIADB;
        if (u.startsWith("jdbc:postgresql:")) return DriverType.POSTGRESQL;
        if (u.startsWith("jdbc:sqlite:")) return DriverType.SQLITE;
        return null;
    }

    public static List<CatalogInfo> listCatalogs(Connection conn) throws SQLException {
        List<CatalogInfo> result = new ArrayList<>();
        try (ResultSet rs = conn.getMetaData().getCatalogs()) {
            while (rs.next()) {
                result.add(new CatalogInfo(rs.getString("TABLE_CAT")));
            }
        }
        return result;
    }

    public static List<CatalogInfo> listDatabases(Connection conn) throws SQLException {
        return listCatalogs(conn);
    }

    public static List<SchemaInfo> listSchemas(Connection conn) throws SQLException {
        List<SchemaInfo> result = new ArrayList<>();
        try (ResultSet rs = conn.getMetaData().getSchemas()) {
            while (rs.next()) {
                String catalog = safeGet(rs, "TABLE_CATALOG");
                if (catalog == null) catalog = safeGet(rs, "TABLE_CAT");
                String schema = rs.getString("TABLE_SCHEM");
                result.add(new SchemaInfo(catalog, schema));
            }
        }
        return result;
    }

    public static List<TableInfo> listTables(
            Connection conn, String catalog, String schemaPattern, String tableNamePattern, String... types)
            throws SQLException {
        if (types == null || types.length == 0) {
            types = new String[]{"TABLE"};
        }
        List<TableInfo> result = new ArrayList<>();
        try (ResultSet rs = conn.getMetaData()
                .getTables(catalog, schemaPattern, tableNamePattern, types)) {
            while (rs.next()) {
                String cat = rs.getString("TABLE_CAT");
                String sch = rs.getString("TABLE_SCHEM");
                String name = rs.getString("TABLE_NAME");
                String type = rs.getString("TABLE_TYPE");
                result.add(new TableInfo(cat, sch, name, type));
            }
        }
        return result;
    }

    public static List<ColumnInfo> listColumns(
            Connection conn, String catalog, String schema, String tableName) throws SQLException {
        Objects.requireNonNull(tableName, "tableName");
        List<ColumnInfo> result = new ArrayList<>();
        try (ResultSet rs = conn.getMetaData()
                .getColumns(catalog, schema, tableName, null)) {
            while (rs.next()) {
                String cat = rs.getString("TABLE_CAT");
                String sch = rs.getString("TABLE_SCHEM");
                String tbl = rs.getString("TABLE_NAME");
                String col = rs.getString("COLUMN_NAME");
                int dataType = rs.getInt("DATA_TYPE");     // java.sql.Types
                String typeName = rs.getString("TYPE_NAME");
                int columnSize = rs.getInt("COLUMN_SIZE");
                int decimalDigits = rs.getInt("DECIMAL_DIGITS");
                boolean nullable = DatabaseMetaData.columnNullable == rs.getInt("NULLABLE");
                result.add(new ColumnInfo(cat, sch, tbl, col, dataType, typeName, columnSize, decimalDigits, nullable));
            }
        }
        return result;
    }

    public static long getTableRowCount(Connection conn, String schema, String table) throws SQLException {
        Objects.requireNonNull(table, "table");
        DriverType dt = detectDriverType(conn);

        String qualified = qualify(dt, schema, table);
        String sql = "SELECT COUNT(*) FROM " + qualified;
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getLong(1);
            }
        }
        return 0L;
    }

    private static String qualify(DriverType dt, String schema, String table) {
        String t = quote(dt, table);
        if (schema == null || schema.isBlank()) return t;
        return quote(dt, schema) + "." + t;
    }

    private static String quote(DriverType dt, String ident) {
        if (ident == null) return null;
        return switch (dt) {
            case POSTGRESQL -> "\"" + ident.replace("\"", "\"\"") + "\"";
            case MYSQL, MARIADB, SQLITE -> "`" + ident.replace("`", "``") + "`";
            default -> ident;
        };
    }

    private static String safeGet(ResultSet rs, String col) {
        try {
            return rs.getString(col);
        } catch (SQLException ignored) {
            return null;
        }
    }

    public static final class CatalogInfo {
        public final String catalog;

        public CatalogInfo(String catalog) {
            this.catalog = catalog;
        }

        @Override
        public String toString() {
            return catalog;
        }
    }

    public static final class SchemaInfo {
        public final String catalog;
        public final String schema;

        public SchemaInfo(String catalog, String schema) {
            this.catalog = catalog;
            this.schema = schema;
        }

        @Override
        public String toString() {
            return (catalog != null ? catalog + "." : "") + schema;
        }
    }

    public static final class TableInfo {
        public final String catalog;
        public final String schema;
        public final String tableName;
        public final String tableType;

        public TableInfo(String catalog, String schema, String tableName, String tableType) {
            this.catalog = catalog;
            this.schema = schema;
            this.tableName = tableName;
            this.tableType = tableType;
        }

        @Override
        public String toString() {
            return (schema != null ? schema + "." : "") + tableName + " (" + tableType + ")";
        }
    }

    public static final class ColumnInfo {
        public final String catalog;
        public final String schema;
        public final String tableName;
        public final String columnName;
        public final int dataType;
        public final String typeName;
        public final int columnSize;
        public final int decimalDigits;
        public final boolean nullable;

        public ColumnInfo(
                String catalog,
                String schema,
                String tableName,
                String columnName,
                int dataType,
                String typeName,
                int columnSize,
                int decimalDigits,
                boolean nullable) {
            this.catalog = catalog;
            this.schema = schema;
            this.tableName = tableName;
            this.columnName = columnName;
            this.dataType = dataType;
            this.typeName = typeName;
            this.columnSize = columnSize;
            this.decimalDigits = decimalDigits;
            this.nullable = nullable;
        }

        @Override
        public String toString() {
            return columnName + " " + typeName +
                    "(" + columnSize + (decimalDigits > 0 ? "," + decimalDigits : "") + ")" +
                    (nullable ? "" : " NOT NULL");
        }
    }
}
