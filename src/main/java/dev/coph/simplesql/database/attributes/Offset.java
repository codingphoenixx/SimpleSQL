package dev.coph.simplesql.database.attributes;

/**
 * Represents an SQL OFFSET clause used to skip a specified number of rows in query results.
 * The class holds the offset value, which determines how many rows to bypass in the query.
 *
 * Instances of this class are immutable, ensuring thread safety and preventing modification
 * of the offset value once an instance has been created.
 *
 * The `toString` method generates the SQL-compliant OFFSET clause as a string.
 */
public class Offset {
    /**
     * Represents the number of rows to be skipped in an SQL query's result.
     * Used as the count value in an OFFSET clause to bypass a specific number of rows.
     * This value is immutable and set upon initialization of the containing class.
     */
    private final int count;

    /**
     * Constructs an Offset object with the specified count.
     *
     * @param count The number of rows to be skipped in an SQL query's result.
     */
    public Offset(int count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return " OFFSET " + count;
    }
}
