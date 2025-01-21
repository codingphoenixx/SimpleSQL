package dev.coph.simplesql.database;

import dev.coph.simplesql.utils.Check;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


/**
 * The Column class represents a column in a database table. It provides
 * properties and methods to define and manipulate the structure of the column,
 * including its key, data type, constraints, and default values. This class
 * supports a fluent and chainable configuration style.
 */
@Getter
@Setter
@Accessors(fluent = true, chain = true)
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
     *
     * The {@code DataType} includes predefined constants like {@code CHAR}, {@code VARCHAR},
     * {@code BOOLEAN}, {@code INT}, etc., to represent various SQL data types.
     *
     * This variable helps determine the structure and constraints of the database column
     * during schema creation or modification operations.
     */
    private DataType dataType;

    /**
     * Represents an optional parameter or additional configuration associated with the column's data type.
     *
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

    @Override
    public String toString() {
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
                if (!dataType.equals(DataType.TINYTEXT) && !dataType.equals(DataType.INT) && !dataType.equals(DataType.BIGINT)) {
                    System.out.println("ERROR: You cannot set an autoincrement to a non int value. Setting it to default primary key.");
                    columnType = ColumnType.PRIMARY_KEY;
                }
            }
            column.append(" ").append(columnType.name().replaceAll("_", " "));
        }

        if (defaultValue != null) {
            column.append(" DEFAULT '").append((defaultValue instanceof Boolean bool ? (bool ? 1 : 0) : defaultValue)).append("'");

        }

        return column.toString();
    }

    public Column() {
    }

    public Column(String key, DataType dataType) {
        this.key = key;
        this.dataType = dataType;
    }

    public Column(String key, DataType dataType, Object dataTypeParamenterObject) {
        this.key = key;
        this.dataType = dataType;
        this.dataTypeParamenterObject = dataTypeParamenterObject;
    }

    public Column(String key, DataType dataType, ColumnType columnType) {
        this.key = key;
        this.dataType = dataType;
        this.columnType = columnType;
    }

    public Column(String key, DataType dataType, boolean notNull) {
        this.key = key;
        this.dataType = dataType;
        this.notNull = notNull;
    }

    public Column(String key, DataType dataType, Object dataTypeParamenterObject, ColumnType columnType) {
        this.key = key;
        this.dataType = dataType;
        this.dataTypeParamenterObject = dataTypeParamenterObject;
        this.columnType = columnType;
    }

    public Column(String key, DataType dataType, Object dataTypeParamenterObject, ColumnType columnType, Object defaultValue, boolean notNull) {
        this.key = key;
        this.dataType = dataType;
        this.dataTypeParamenterObject = dataTypeParamenterObject;
        this.columnType = columnType;
        this.defaultValue = defaultValue;
        this.notNull = notNull;
    }



    /**
     * Enum representing the various types of constraints that can be applied to a database column.
     * It is used to define the specific constraints for a column in the database schema.
     */
    public enum ColumnType {
        /**
         * Represents the default column type in a database.
         * This is typically used when no specific constraint is applied to the column.
         */
        DEFAULT,
        /**
         * Represents the PRIMARY_KEY constraint of a database column.
         * This ensures that the column has unique values and does not contain null values.
         * Typically used to uniquely identify each record in a table.
         */
        PRIMARY_KEY,
        /**
         * Represents the PRIMARY_KEY constraint with AUTOINCREMENT functionality for a database column.
         * This ensures that the column has unique values, does not contain null values, and its values are
         * automatically incremented by the database whenever a new record is inserted.
         */
        PRIMARY_KEY_AUTOINCREMENT,
        /**
         * Represents the UNIQUE constraint of a database column.
         * This ensures that all values in the column are distinct.
         * The constraint prohibits duplicate entries in the column,
         * but unlike the PRIMARY_KEY constraint, it allows null values.
         */
        UNIQUE
    }


}
