package dev.coph.simplesql.query.providers;

import dev.coph.simplesql.database.Column;
import dev.coph.simplesql.database.attributes.ColumnType;
import dev.coph.simplesql.database.attributes.CreateMethode;
import dev.coph.simplesql.database.attributes.DataType;
import dev.coph.simplesql.query.Query;
import dev.coph.simplesql.query.QueryProvider;
import dev.coph.simpleutilities.check.Check;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;


/**
 * This class is responsible for generating SQL statements to create database tables.
 * It implements the {@link QueryProvider} interface and provides methods to configure
 * the table name, creation method, and columns. Additionally, it allows the creation of
 * a SQL query string based on the configuration provided.
 *
 * The {@code TableCreateQueryProvider} supports a fluent API style, enabling method chaining
 * to configure the table schema with columns and other attributes efficiently.
 */
@Getter
@Accessors(fluent = true, chain = true)
public class TableCreateQueryProvider implements QueryProvider {

    /**
     * Represents the name of the table that is associated with a specific query operation.
     */
    @Setter
    private String table;

    /**
     * Represents the method used to create a database structure, such as a table.
     * This variable is of type {@link CreateMethode}, an enumeration that defines
     * the specific strategies for creating database structures, including options
     * such as a default creation method or conditional creation if the structure
     * does not already exist.
     */
    @Setter
    private CreateMethode createMethode = CreateMethode.DEFAULT;

    /**
     * Represents the collection of columns to be included in the SQL table schema.
     * Each column in the list defines the structure, constraints, and attributes
     * of a specific piece of the database table.
     *
     * This list is used during SQL schema generation to accurately define the
     * layout of the table being created. Each column entry in the list is an
     * instance of the {@code Column} class, which provides detailed configuration
     * for the table's structure.
     *
     * The order of the columns in this list reflects the order in which they will
     * appear in the table schema definition.
     */
    private final List<Column> columns = new ArrayList<>();


    @Override
    public String generateSQLString(Query query) {
        Check.ifNullOrEmptyMap(table, "tablename");
        Check.ifNullOrEmptyMap(columns, "columns");
        StringBuilder sql = new StringBuilder("CREATE TABLE ");

        if (createMethode.equals(CreateMethode.IF_NOT_EXISTS))
            sql.append("IF NOT EXISTS ");

        sql.append(table);


        StringBuilder columnString = null;
        for (Column column : columns) {
            if (columnString == null) {
                columnString = new StringBuilder("(").append(column.toString(query));
            } else {
                columnString.append(", ").append(column.toString(query));
            }
        }
        columnString.append(")");
        sql.append(columnString).append(";");
        return sql.toString();
    }


    /**
     * Adds a column to the list of columns to be included in the table creation query.
     * If the list of columns has not been initialized, it is initialized before adding the column.
     * This method supports a fluent API style, allowing method chaining.
     *
     * @param column the Column object representing the database column to be added to the table schema
     * @return the current instance of TableCreateQueryProvider for method chaining
     */
    public TableCreateQueryProvider column(Column column) {
        columns.add(column);
        return this;
    }

    /**
     * Adds a column with the specified key and data type to the list of columns
     * to be included in the table creation query. If the list of columns has not
     * been initialized, it is initialized before adding the column. This method
     * supports a fluent API style, allowing method chaining.
     *
     * @param key the name of the column to be added
     * @param dataType the data type of the column to be added
     * @return the current instance of TableCreateQueryProvider for method chaining
     */
    public TableCreateQueryProvider column(String key, DataType dataType) {
        columns.add(new Column(key, dataType));
        return this;
    }

    /**
     * Adds a column with the specified key, data type, and an additional parameter object
     * to the list of columns to be included in the table creation query.
     * If the list of columns has not been initialized, it is initialized before adding the column.
     * This method supports a fluent API style, allowing method chaining.
     *
     * @param key the name of the column to be added
     * @param dataType the data type of the column to be added
     * @param dataTypeParameterObject an additional parameter object associated with the column's data type
     * @return the current instance of TableCreateQueryProvider for method chaining
     */
    public TableCreateQueryProvider column(String key, DataType dataType, Object dataTypeParameterObject) {
        columns.add(new Column(key, dataType, dataTypeParameterObject));
        return this;
    }

    /**
     * Adds a column with the specified key, data type, and an additional parameter object
     * to the list of columns to be included in the table creation query.
     * If the list of columns has not been initialized, it is initialized before adding the column.
     * This method supports a fluent API style, allowing method chaining.
     *
     * @param key the name of the column to be added
     * @param dataType the data type of the column to be added
     * @param notNull if the column is allowed to have no value
     * @return the current instance of TableCreateQueryProvider for method chaining
     */
    public TableCreateQueryProvider column(String key, DataType dataType,  boolean notNull) {
        columns.add(new Column(key, dataType,  notNull));
        return this;
    }

    /**
     * Adds a column with the specified key, data type, and an additional parameter object
     * to the list of columns to be included in the table creation query.
     * If the list of columns has not been initialized, it is initialized before adding the column.
     * This method supports a fluent API style, allowing method chaining.
     *
     * @param key the name of the column to be added
     * @param dataType the data type of the column to be added
     * @param dataTypeParameterObject an additional parameter object associated with the column's data type
     * @param notNull if the column is allowed to have no value
     * @return the current instance of TableCreateQueryProvider for method chaining
     */
    public TableCreateQueryProvider column(String key, DataType dataType, Object dataTypeParameterObject, boolean notNull) {
        columns.add(new Column(key, dataType, dataTypeParameterObject, notNull));
        return this;
    }

    /**
     * Adds a column with the specified key, data type, an additional parameter object,
     * and column type to the list of columns to be included in the table creation query.
     * If the list of columns has not been initialized, it is initialized before adding the column.
     * This method supports a fluent API style, allowing method chaining.
     *
     * @param key the name of the column to be added
     * @param dataType the data type of the column to be added
     * @param dataTypeParameterObject an additional parameter object associated
     *                                with the column's data type
     * @param columnType the type of the column (e.g., primary key, normal column, etc.)
     * @return the current instance of TableCreateQueryProvider for method chaining
     */
    public TableCreateQueryProvider column(String key, DataType dataType, Object dataTypeParameterObject, ColumnType columnType) {
        columns.add(new Column(key, dataType, dataTypeParameterObject, columnType));
        return this;
    }

    /**
     * Adds a column with the specified key, data type, an additional parameter object,
     * and column type to the list of columns to be included in the table creation query.
     * If the list of columns has not been initialized, it is initialized before adding the column.
     * This method supports a fluent API style, allowing method chaining.
     *
     * @param key the name of the column to be added
     * @param dataType the data type of the column to be added
     * @param dataTypeParameterObject an additional parameter object associated
     *                                with the column's data type
     * @param columnType the type of the column (e.g., primary key, normal column, etc.)
     * @param notNull if the column is allowed to be null
     * @return the current instance of TableCreateQueryProvider for method chaining
     */
    public TableCreateQueryProvider column(String key, DataType dataType, Object dataTypeParameterObject, ColumnType columnType, boolean notNull) {
        columns.add(new Column(key, dataType, dataTypeParameterObject, columnType, notNull));
        return this;
    }


    /**
     * Adds a column with the specified key, data type, and column type to the list of columns
     * to be included in the table creation query. If the list of columns has not been initialized,
     * it is initialized before adding the column. This method supports a fluent API style,
     * allowing method chaining.
     *
     * @param key the name of the column to be added
     * @param dataType the data type of the column to be added
     * @param columnType the type of the column (e.g., primary key, normal column, etc.)
     * @return the current instance of TableCreateQueryProvider for method chaining
     */
    public TableCreateQueryProvider column(String key, DataType dataType, ColumnType columnType) {
        columns.add(new Column(key, dataType, columnType));
        return this;
    }



}
