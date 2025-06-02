package dev.coph.simplesql.database;

import dev.coph.simplesql.adapter.DatabaseAdapter;
import dev.coph.simplesql.database.attributes.ColumnType;
import dev.coph.simplesql.database.attributes.DataType;
import dev.coph.simplesql.query.Query;
import dev.coph.simpleutilities.check.Check;
import lombok.experimental.Accessors;


/**
 * The Column class represents a column in a database table. It provides
 * properties and methods to define and manipulate the structure of the column,
 * including its key, data type, constraints, and default values. This class
 * supports a fluent and chainable configuration style.
 */
public class Column {

    /**
     * Represents the key or identifier for a column in a database schema.
     * This key is typically used to uniquely identify the column within its context,
     * such as in defining column constraints or performing operations on the database table.
     */
    private String key;

    /**
     * Represents the data type of a column in a database schema, defining the kind of values
     * that can be stored in the column. It is used to specify the type of data, such as
     * strings, integers, floating-point numbers, dates, timestamps, and others.
     * <p>
     * The {@code DataType} includes predefined constants like {@code CHAR}, {@code VARCHAR},
     * {@code BOOLEAN}, {@code INT}, etc., to represent various SQL data types.
     * <p>
     * This variable helps determine the structure and constraints of the database column
     * during schema creation or modification operations.
     */
    private DataType dataType;

    /**
     * Represents an optional parameter or additional configuration associated with the column's data type.
     * <p>
     * This object allows for specifying parameters required by certain data types,
     * such as length, precision, or scale. It is often used for cases where a data type
     * requires or supports a specific object to define additional attributes. The exact
     * usage depends on the associated data type.
     */
    private Object dataTypeParamenterObject;

    /**
     * Represents the specific type of constraint applied to the column in the database schema.
     * It defines additional properties or roles for the column such as being a primary key,
     * unique identifier, or default column type.
     * This variable is used in conjunction with the Column's attributes to build a well-defined schema.
     */
    private ColumnType columnType;

    /**
     * Represents the default value for the column in a database table.
     * This value is used when no explicit value is provided during data insertion.
     */
    private Object defaultValue;

    /**
     * Indicates whether the column should enforce a NOT NULL constraint.
     * If set to true, the column cannot contain null values.
     */
    private boolean notNull;

    /**
     * Generates a string representation of the column definition using the provided query context.
     * The generated string outlines the column attributes such as key, data type, constraints,
     * and default value, based on the column's configuration and the supported database features.
     *
     * @param query the query context providing database-specific details and configurations for the column
     * @return a string representation of the column definition in SQL format, or null in case of unsupported configurations
     */
    public String toString(Query query) {
        Check.ifNull(key, "column-key");
        Check.ifNull(dataType, "datatype");
        if (dataType.requireObject()) {
            Check.ifNull(dataTypeParamenterObject, "dataTypeParameterObject");
        }
        StringBuilder column = new StringBuilder(key).append(" ").append(dataType.toSQL(dataTypeParamenterObject));

        if (notNull) {
            column.append(" NOT NULL");
        }

        if (columnType != null && columnType != ColumnType.DEFAULT) {
            if (columnType.equals(ColumnType.PRIMARY_KEY_AUTOINCREMENT)) {
                if (query.databaseAdapter() == null) {
                    return null;
                }
                if (query.databaseAdapter().driverType().equals(DatabaseAdapter.DriverType.MYSQL) || query.databaseAdapter().driverType().equals(DatabaseAdapter.DriverType.MARIADB)) {
                    if (!dataType.equals(DataType.TINYTEXT) && !dataType.equals(DataType.INTEGER) && !dataType.equals(DataType.BIGINT)) {
                        System.out.println("ERROR: You cannot set an autoincrement to a non int value. Setting it to default primary key.");
                        columnType = ColumnType.PRIMARY_KEY;
                    }
                } else if (query.databaseAdapter().driverType().equals(DatabaseAdapter.DriverType.SQLITE)) {
                    if (!dataType.equals(DataType.INTEGER)) {
                        System.out.println("ERROR: You cannot set an autoincrement to a non integer. Setting it to default primary key.");
                        columnType = ColumnType.PRIMARY_KEY;
                    }
                }
            }
            column.append(" ").append(columnType.toString(query));
        }

        if (defaultValue != null) {
            column.append(" DEFAULT '").append((defaultValue instanceof Boolean bool ? (bool ? 1 : 0) : defaultValue)).append("'");

        }

        return column.toString();
    }

    @Override
    public String toString() {
        return null;
    }

    /**
     * Default constructor for the Column class.
     * <p>
     * Initializes a new instance of the Column class with no predefined attributes.
     * It is typically used when the column definition will be customized
     * or set using other methods or properties of the class after instantiation.
     */
    public Column() {
    }

    /**
     * Constructs a Column instance with the specified key and data type.
     *
     * @param key      the name of the column, representing its unique identifier
     * @param dataType the data type of the column, specifying the type of data it holds
     */
    public Column(String key, DataType dataType) {
        this.key = key;
        this.dataType = dataType;
    }

    /**
     * Constructs a Column instance with the specified key, data type,
     * and an additional parameter object associated with the data type.
     *
     * @param key                      the name of the column, representing its unique identifier
     * @param dataType                 the data type of the column, defining the type of data it holds
     * @param dataTypeParamenterObject an additional parameter object associated with the column's data type
     */
    public Column(String key, DataType dataType, Object dataTypeParamenterObject) {
        this.key = key;
        this.dataType = dataType;
        this.dataTypeParamenterObject = dataTypeParamenterObject;
    }

    /**
     * Constructs a new Column with the specified key, data type, and column type.
     *
     * @param key        the unique identifier for the column, representing its name
     * @param dataType   the data type defining the kind of data this column holds
     * @param columnType the column type specifying constraints or attributes of the column, such as PRIMARY_KEY or UNIQUE
     */
    public Column(String key, DataType dataType, ColumnType columnType) {
        this.key = key;
        this.dataType = dataType;
        this.columnType = columnType;
    }

    /**
     * Constructs a Column instance with the specified key, data type, and nullability constraint.
     *
     * @param key      the unique identifier for the column, representing its name
     * @param dataType the data type of the column, defining the type of data it holds
     * @param notNull  a boolean indicating whether the column disallows null values
     */
    public Column(String key, DataType dataType, boolean notNull) {
        this.key = key;
        this.dataType = dataType;
        this.notNull = notNull;
    }

    /**
     * Constructs a Column instance with the specified key, data type, data type parameter object, and column type.
     *
     * @param key                      the name of the column, representing its unique identifier
     * @param dataType                 the data type of the column, defining the type of data it holds
     * @param dataTypeParamenterObject an additional parameter object associated with the column's data type
     * @param columnType               the column type specifying constraints or attributes of the column, such as PRIMARY_KEY or UNIQUE
     */
    public Column(String key, DataType dataType, Object dataTypeParamenterObject, ColumnType columnType) {
        this.key = key;
        this.dataType = dataType;
        this.dataTypeParamenterObject = dataTypeParamenterObject;
        this.columnType = columnType;
    }

    /**
     * Constructs a Column instance with the specified key, data type, additional parameter object, and nullability constraint.
     *
     * @param key                      the unique identifier for the column, representing its name
     * @param dataType                 the data type of the column, defining the type of data it holds
     * @param dataTypeParamenterObject an additional parameter object associated with the column's data type
     * @param notNull                  a boolean indicating whether the column disallows null values
     */
    public Column(String key, DataType dataType, Object dataTypeParamenterObject, boolean notNull) {
        this.key = key;
        this.dataType = dataType;
        this.dataTypeParamenterObject = dataTypeParamenterObject;
        this.notNull = notNull;
    }

    /**
     * Constructs a Column instance with the specified key, data type, additional parameter object, column type,
     * and a constraint on whether the column allows null values.
     *
     * @param key                      the unique identifier for the column, representing its name
     * @param dataType                 the data type of the column, defining the type of data it holds
     * @param dataTypeParamenterObject an additional parameter object associated with the column's data type
     * @param columnType               the column type specifying constraints or attributes of the column, such as PRIMARY_KEY or UNIQUE
     * @param notNull                  a boolean indicating whether the column disallows null values
     */
    public Column(String key, DataType dataType, Object dataTypeParamenterObject, ColumnType columnType, boolean notNull) {
        this.key = key;
        this.dataType = dataType;
        this.dataTypeParamenterObject = dataTypeParamenterObject;
        this.columnType = columnType;
        this.notNull = notNull;
    }

    /**
     * Constructs a Column instance with the specified key, data type, data type parameter object, column type,
     * default value, and nullability constraint.
     *
     * @param key                      the unique identifier for the column, representing its name
     * @param dataType                 the data type of the column, defining the type of data it holds
     * @param dataTypeParamenterObject an additional parameter object associated with the column's data type
     * @param columnType               the column type specifying constraints or attributes of the column, such as PRIMARY_KEY or UNIQUE
     * @param defaultValue             the default value assigned to the column when no value is provided
     * @param notNull                  a boolean indicating whether the column disallows null values
     */
    public Column(String key, DataType dataType, Object dataTypeParamenterObject, ColumnType columnType, Object defaultValue, boolean notNull) {
        this.key = key;
        this.dataType = dataType;
        this.dataTypeParamenterObject = dataTypeParamenterObject;
        this.columnType = columnType;
        this.defaultValue = defaultValue;
        this.notNull = notNull;
    }

    /**
     * Retrieves the key or unique identifier of the column.
     *
     * @return a string representing the key or name of the column
     */
    public String key() {
        return this.key;
    }

    /**
     * Retrieves the data type of the column.
     *
     * @return the DataType representing the type of data the column holds
     */
    public DataType dataType() {
        return this.dataType;
    }

    /**
     * Retrieves the data type parameter object associated with the column.
     *
     * @return an Object representing an additional parameter related to the column's data type
     */
    public Object dataTypeParamenterObject() {
        return this.dataTypeParamenterObject;
    }

    /**
     * Retrieves the column type associated with this instance of the Column.
     *
     * @return the ColumnType representing the type of constraint or attribute
     *         applied to the column, such as DEFAULT, PRIMARY_KEY, or UNIQUE
     */
    public ColumnType columnType() {
        return this.columnType;
    }

    /**
     * Retrieves the default value associated with the column.
     *
     * @return an Object representing the default value of the column, or null if no default value is set
     */
    public Object defaultValue() {
        return this.defaultValue;
    }

    /**
     * Checks if the column is configured to disallow null values.
     *
     * @return true if the column is marked as not allowing null values, false otherwise
     */
    public boolean notNull() {
        return this.notNull;
    }

    /**
     * Sets the key or unique identifier of the column.
     *
     * @param key the unique identifier for the column, representing its name
     * @return the current instance of the Column for method chaining
     */
    public Column key(String key) {
        this.key = key;
        return this;
    }

    /**
     * Sets the data type of the column.
     *
     * @param dataType the data type of the column, specifying the type of data it holds
     * @return the current instance of the Column for method chaining
     */
    public Column dataType(DataType dataType) {
        this.dataType = dataType;
        return this;
    }

    /**
     * Sets the data type parameter object associated with the column.
     *
     * @param dataTypeParamenterObject an Object representing an additional parameter
     *                                 related to the column's data type
     * @return the current instance of the Column for method chaining
     */
    public Column dataTypeParamenterObject(Object dataTypeParamenterObject) {
        this.dataTypeParamenterObject = dataTypeParamenterObject;
        return this;
    }

    /**
     * Sets the column type for this column instance.
     *
     * @param columnType the column type specifying constraints or attributes of the column,
     *                   such as PRIMARY_KEY or UNIQUE
     * @return the current instance of the Column for method chaining
     */
    public Column columnType(ColumnType columnType) {
        this.columnType = columnType;
        return this;
    }

    /**
     * Sets the default value for the column.
     *
     * @param defaultValue the default value to be assigned to the column
     * @return the current instance of the Column for method chaining
     */
    public Column defaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }

    /**
     * Sets whether the column disallows null values.
     *
     * @param notNull a boolean indicating whether the column should not allow null values
     * @return the current instance of the Column for method chaining
     */
    public Column notNull(boolean notNull) {
        this.notNull = notNull;
        return this;
    }
}
