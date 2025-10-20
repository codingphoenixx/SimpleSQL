package dev.coph.simplesql.utils;

import dev.coph.simplesql.driver.DriverType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Utility class for extracting and managing database metadata.
 * This class provides methods to detect database driver types, list catalogs, schemas, tables,
 * columns, and retrieve table row counts through the database metadata available via JDBC connections.
 * The class is designed to be used with various database types such as MySQL, MariaDB,
 * PostgreSQL, and SQLite.
 * <p>
 * The class provides static methods for working with database metadata, and instances of
 * this class cannot be created.
 */
public final class DatabaseMetadataUtil {

    /**
     * Utility class providing methods for retrieving and processing metadata
     * from database connections. This class offers various functionalities to
     * query catalogs, schemas, tables, columns, and other database-related metadata.
     * <p>
     * It contains only static methods and is designed to be used without instantiation.
     * The constructor is private to enforce non-instantiability.
     */
    private DatabaseMetadataUtil() {
    }

    /**
     * Detects the type of database driver being used based on the connection's metadata.
     * The method inspects the JDBC URL of the provided {@link Connection} to determine
     * the corresponding {@code DriverType}.
     *
     * @param conn the database connection from which the driver type should be detected; must not be null
     * @return the detected {@code DriverType} based on the JDBC URL;
     * returns {@code null} if the URL is unavailable or does not match known types
     * @throws SQLException if a database access error occurs while retrieving metadata
     */
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

    /**
     * Retrieves a list of available catalogs from the database metadata.
     * This method queries the database using the provided connection to retrieve the catalog names.
     *
     * @param conn the database connection used to retrieve the metadata; must not be null
     * @return a list of {@code CatalogInfo} objects representing the available catalogs in the database
     * @throws SQLException if a database access error occurs while retrieving the catalog information
     */
    public static List<CatalogInfo> listCatalogs(Connection conn) throws SQLException {
        List<CatalogInfo> result = new ArrayList<>();
        try (ResultSet rs = conn.getMetaData().getCatalogs()) {
            while (rs.next()) {
                result.add(new CatalogInfo(rs.getString("TABLE_CAT")));
            }
        }
        return result;
    }

    /**
     * Retrieves a list of available databases from the database metadata.
     * This method is a wrapper around {@link #listCatalogs(Connection)} and queries the database
     * using the provided connection to retrieve the database names.
     *
     * @param conn the database connection used to retrieve the metadata; must not be null
     * @return a list of {@code CatalogInfo} objects representing the available databases in the database
     * @throws SQLException if a database access error occurs while retrieving the database information
     */
    public static List<CatalogInfo> listDatabases(Connection conn) throws SQLException {
        return listCatalogs(conn);
    }

    /**
     * Retrieves a list of available schemas from the database metadata.
     * This method queries the database using the provided connection to retrieve the schema names.
     *
     * @param conn the database connection used to retrieve the metadata; must not be null
     * @return a list of {@code SchemaInfo} objects representing the available schemas in the database
     * @throws SQLException if a database access error occurs while retrieving the schema information
     */
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

    /**
     * Retrieves a list of tables from the database metadata based on the given parameters.
     * This method queries the database using the provided connection and filters the results
     * based on catalog, schema, table name pattern, and table types. If no table types are
     * provided, the method defaults to retrieving tables of type "TABLE".
     *
     * @param conn             the database connection used to retrieve the metadata; must not be null
     * @param catalog          the catalog name used to filter the tables; can be null to not apply catalog filtering
     * @param schemaPattern    a schema name pattern used to filter the tables; can be null to not apply schema filtering
     * @param tableNamePattern a table name pattern used to filter the tables; can be null to not apply table name filtering
     * @param types            a variable-length array of table types used to filter the results (e.g., "TABLE", "VIEW");
     *                         if null or empty, defaults to retrieving tables of type "TABLE"
     * @return a list of {@code TableInfo} objects representing the tables that match the specified criteria
     * @throws SQLException if a database access error occurs while retrieving the table information
     */
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

    /**
     * Retrieves a list of column metadata for the specified table from the database.
     * The method queries the database metadata using the provided connection and returns
     * information about the columns of the specified table, including details such as
     * column name, type, size, and nullability.
     *
     * @param conn      the database connection used to retrieve the metadata; must not be null
     * @param catalog   the catalog name used to narrow down the search; can be null to not apply catalog filtering
     * @param schema    the schema name used to narrow down the search; can be null to not apply schema filtering
     * @param tableName the name of the table for which column metadata should be retrieved; must not be null
     * @return a list of {@code ColumnInfo} objects representing the metadata of the columns in the specified table
     * @throws SQLException         if a database access error occurs while retrieving the column information
     * @throws NullPointerException if the {@code tableName} parameter is null
     */
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
                int dataType = rs.getInt("DATA_TYPE");
                String typeName = rs.getString("TYPE_NAME");
                int columnSize = rs.getInt("COLUMN_SIZE");
                int decimalDigits = rs.getInt("DECIMAL_DIGITS");
                boolean nullable = DatabaseMetaData.columnNullable == rs.getInt("NULLABLE");
                result.add(new ColumnInfo(cat, sch, tbl, col, dataType, typeName, columnSize, decimalDigits, nullable));
            }
        }
        return result;
    }

    /**
     * Retrieves the number of rows in the specified table within the database.
     *
     * @param conn   the database connection used to run the query; must not be null
     * @param schema the schema name of the table; can be null to use the database's default schema
     * @param table  the name of the table for which the row count is to be retrieved; must not be null
     * @return the total number of rows in the specified table
     * @throws SQLException         if a database access error occurs while executing the query
     * @throws NullPointerException if the {@code table} parameter is null
     */
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

    /**
     * Constructs a fully qualified table name by combining the schema and table name
     * with appropriate quoting based on the database driver type. If the schema is
     * null or blank, only the quoted table name is returned.
     *
     * @param dt     the {@code DriverType} representing the type of database driver; must not be null
     * @param schema the schema name to qualify the table; can be null or blank to exclude schema
     * @param table  the name of the table to be qualified; must not be null
     * @return the fully qualified table name as a {@code String}, either in the format "schema.table"
     * or simply "table" if schema is null or blank
     */
    private static String qualify(DriverType dt, String schema, String table) {
        String t = quote(dt, table);
        if (schema == null || schema.isBlank()) return t;
        return quote(dt, schema) + "." + t;
    }

    /**
     * Quotes an identifier according to the syntax rules of the specified database driver type.
     * Ensures that special characters in the identifier are escaped appropriately for use in SQL queries.
     *
     * @param dt    the {@code DriverType} representing the type of database driver; must not be null
     * @param ident the identifier to be quoted; can be null, in which case null is returned
     * @return the quoted identifier as a {@code String}, or {@code null} if the identifier is null
     */
    private static String quote(DriverType dt, String ident) {
        if (ident == null) return null;
        return switch (dt) {
            case POSTGRESQL -> "\"" + ident.replace("\"", "\"\"") + "\"";
            case MYSQL, MARIADB, SQLITE -> "`" + ident.replace("`", "``") + "`";
            default -> ident;
        };
    }

    /**
     * Safely retrieves a string value from the specified column in the given {@code ResultSet}.
     * If a {@link SQLException} is thrown during the column access, the method suppresses
     * the exception and returns {@code null}.
     *
     * @param rs  the {@code ResultSet} object from which the string value is to be retrieved;
     *            must not be null
     * @param col the name of the column whose value is to be retrieved; must not be null
     * @return the string value of the specified column, or {@code null} if a {@link SQLException}
     * occurs or the column contains SQL {@code NULL}
     */
    private static String safeGet(ResultSet rs, String col) {
        try {
            return rs.getString(col);
        } catch (SQLException ignored) {
            return null;
        }
    }

    /**
     * Represents information about a catalog.
     * This record encapsulates a catalog-related string.
     */
    public record CatalogInfo(String catalog) {

        @Override
        public String toString() {
            return catalog;
        }
    }

    /**
     * Represents database schema information consisting of a catalog and a schema name.
     * This class is implemented as a record, providing an immutable representation of the schema metadata.
     * <p>
     * The `catalog` represents the database catalog name and can be null.
     * The `schema` represents the database schema name and is required.
     * <p>
     * The {@code toString()} method is overridden to provide a string representation
     * combining the catalog and schema, separated by a period (".") if the catalog is non-null.
     */
    public record SchemaInfo(String catalog, String schema) {

        @Override
        public String toString() {
            return (catalog != null ? catalog + "." : "") + schema;
        }
    }

    /**
     * Represents metadata information about a database table.
     * This record encapsulates details such as the catalog, schema,
     * table name, and table type.
     * <p>
     * TableInfo provides a way to describe database table properties
     * in a structured manner and includes a custom string representation.
     * <p>
     * Immutable by design due to use of Java's Record feature.
     *
     * @param catalog   The catalog of the table; can be null.
     * @param schema    The schema of the table; can be null.
     * @param tableName The name of the table; cannot be null.
     * @param tableType The type of the table (e.g., "TABLE", "VIEW"); cannot be null.
     */
    public record TableInfo(String catalog, String schema, String tableName, String tableType) {

        @Override
        public String toString() {
            return (schema != null ? schema + "." : "") + tableName + " (" + tableType + ")";
        }
    }

    /**
     * This record represents metadata information of a database column. It encapsulates
     * various details regarding a column in a database table, such as catalog name, schema name,
     * table name, column name, data type, type name, size, precision, and nullability.
     * <p>
     * The class is immutable and provides a compact and easy-to-use representation of column metadata.
     */
    public record ColumnInfo(String catalog, String schema, String tableName, String columnName, int dataType,
                             String typeName, int columnSize, int decimalDigits, boolean nullable) {

        @Override
        public String toString() {
            return columnName + " " + typeName +
                    "(" + columnSize + (decimalDigits > 0 ? "," + decimalDigits : "") + ")" +
                    (nullable ? "" : " NOT NULL");
        }
    }
}
