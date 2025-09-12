package dev.coph.simplesql.query;


import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Represents a query entry for a SQL operation, consisting of a column name and a value.
 * Provides functionality to parse the value into a SQL-friendly format.
 */
public class QueryEntry {
    private static SimpleDateFormat DATE_TIME_CONVERTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static SimpleDateFormat DATE_CONVERTER = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * The name of the database column associated with this query entry.
     * Used to specify the target column for the provided value in SQL operations.
     */
    private String columName;
    /**
     * The value to be associated with the specified database column in a SQL operation.
     * Can represent various data types such as Boolean, Number, or String,
     * which are parsed into corresponding SQL-safe formats when generating query statements.
     */
    private Object value;

    private boolean rawValue = false;

    /**
     * Constructs a QueryEntry with the specified column name and value.
     *
     * @param columName the name of the database column associated with this query entry
     * @param value     the value to be associated with the specified column in a SQL operation
     */
    public QueryEntry(String columName, Object value) {
        this.columName = columName;
        this.value = value;
    }

    /**
     * Default constructor for the QueryEntry class.
     * Initializes an empty QueryEntry without a column name or value.
     * The column name and value can be set later using appropriate setter methods.
     */
    public QueryEntry() {
    }

    /**
     * Converts the stored value into a SQL-compatible string representation.
     * The value is formatted based on its type, ensuring proper handling
     * of Boolean, Number, and String types for safe inclusion in SQL queries.
     *
     * @return a SQL-safe string representation of the stored value
     */
    public String sqlValue() {
        return parseSQLValue(value, rawValue());
    }

    /**
     * Parses the given value into a SQL-compatible string representation.
     * The method handles different data types such as Boolean, Number, and others,
     * and converts them into appropriate formats for safe inclusion in SQL queries.
     *
     * @param value    the value to be parsed into a SQL-compatible string.
     *                 Supported types include Boolean, Number, and other generic objects.
     * @param rawValue if the value should be treated as a finished parsed value.
     * @return a SQL-safe string representation of the provided value. For Boolean values,
     * it returns '1' for true and '0' for false. For Number values, it returns
     * the number as a string. For other objects, it returns the string wrapped
     * in single quotes.
     */
    public static String parseSQLValue(Object value, boolean rawValue) {
        if (value != null && value instanceof Boolean bool) {
            if (bool)
                return "'1'";
            else
                return "'0'";
        }
        if (value != null && value instanceof Number number)
            return number.toString();

        if (value != null && value instanceof Date date)
            return DATE_TIME_CONVERTER.format(date);

        if (rawValue)
            return value.toString();

        return "'%s'".formatted(value);
    }


    /**
     * Indicates whether the raw value is used for this QueryEntry.
     *
     * @return true if the raw value is used, false otherwise
     */
    public boolean rawValue() {
        return rawValue;
    }

    /**
     * Sets the raw value flag for this QueryEntry and returns the updated instance.
     *
     * @param rawValue a boolean indicating whether the value should be treated as raw
     *                 (pre-parsed or in its native SQL-compatible format).
     * @return the updated QueryEntry instance with the raw value flag set to the specified value
     */
    public QueryEntry rawValue(boolean rawValue) {
        this.rawValue = rawValue;
        return this;
    }

    /**
     * Retrieves the name of the database column associated with this QueryEntry.
     *
     * @return the column name as a string
     */
    public String columName() {
        return this.columName;
    }

    /**
     * Retrieves the value associated with this QueryEntry.
     *
     * @return the value object associated with the query entry
     */
    public Object value() {
        return this.value;
    }

    /**
     * Sets the column name for the QueryEntry and returns the updated instance.
     *
     * @param columName the name of the database column to be associated with this query entry
     * @return the updated QueryEntry instance with the specified column name
     */
    public QueryEntry columName(String columName) {
        this.columName = columName;
        return this;
    }

    /**
     * Sets the value associated with this QueryEntry and returns the updated instance.
     *
     * @param value the value to be associated with this QueryEntry
     * @return the updated QueryEntry instance with the specified value
     */
    public QueryEntry value(Object value) {
        this.value = value;
        return this;
    }
}
