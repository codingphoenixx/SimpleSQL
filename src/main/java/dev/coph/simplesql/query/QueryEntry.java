package dev.coph.simplesql.query;


import java.text.SimpleDateFormat;

/**
 * Represents an entry in a query, used to define a column and its associated value.
 * QueryEntry objects can be used in database operations or query-building logic.
 */
public class QueryEntry {
    private static final SimpleDateFormat DATE_TIME_CONVERTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final SimpleDateFormat DATE_CONVERTER = new SimpleDateFormat("yyyy-MM-dd");

    private String columName;

    private Object value;

    private boolean rawValue = false;

    /**
     * Constructs a new QueryEntry with the specified column name and value.
     *
     * @param columName the name of the column associated with this query entry
     * @param value     the value to be associated with the specified column
     */
    public QueryEntry(String columName, Object value) {
        this.columName = columName;
        this.value = value;
    }

    /**
     * Default constructor for the QueryEntry class.
     * Initializes an instance of QueryEntry with no parameters.
     */
    public QueryEntry() {
    }

    /**
     * Retrieves the value of the rawValue flag.
     * The rawValue flag indicates whether the associated value
     * should be treated as a raw (unprocessed) value in queries
     * or database operations.
     *
     * @return true if the value should be treated as raw; false otherwise
     */
    public boolean rawValue() {
        return rawValue;
    }

    /**
     * Sets the rawValue flag for the current QueryEntry instance.
     * The rawValue flag indicates whether the associated value should
     * be treated as a raw (unprocessed) value in queries or database operations.
     *
     * @param rawValue a boolean flag indicating whether the value should be treated as raw
     * @return the current QueryEntry instance with the updated rawValue flag
     */
    public QueryEntry rawValue(boolean rawValue) {
        this.rawValue = rawValue;
        return this;
    }

    /**
     * Retrieves the name of the column associated with this query entry.
     *
     * @return the column name as a String
     */
    public String columName() {
        return this.columName;
    }

    /**
     * Retrieves the value associated with this QueryEntry instance.
     *
     * @return the value associated with this QueryEntry, which can be of any object type
     */
    public Object value() {
        return this.value;
    }

    /**
     * Sets the name of the column associated with this query entry.
     *
     * @param columName the name of the column to be associated with this query entry
     * @return the current QueryEntry instance with the updated column name
     */
    public QueryEntry columName(String columName) {
        this.columName = columName;
        return this;
    }

    /**
     * Sets the value associated with this QueryEntry instance.
     * This method allows updating or defining the value to be stored in the QueryEntry.
     *
     * @param value the value to be associated with this QueryEntry, can be of any object type
     * @return the current QueryEntry instance with the updated value
     */
    public QueryEntry value(Object value) {
        this.value = value;
        return this;
    }
}
