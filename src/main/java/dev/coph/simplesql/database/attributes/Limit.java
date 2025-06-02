package dev.coph.simplesql.database.attributes;

import lombok.experimental.Accessors;

/**
 * Represents a SQL LIMIT clause generator that is used to control the number of rows
 * returned by a query and to specify an optional starting row (OFFSET).
 *
 * This class supports the fluent API style where methods can be chained to configure
 * the limit and offset values directly. It also provides support for generating
 * the SQL representation of the LIMIT and OFFSET clauses.
 */
public class Limit {
    /**
     * Specifies the maximum number of rows to be returned in the SQL query.
     * This value controls the "LIMIT" clause of the SQL statement, which determines
     * how many rows will be included in the result set.
     *
     * A value of 0 or a negative value may generally indicate no limitation on the number
     * of rows to be returned, though this behavior depends on the SQL dialect used.
     */
    private int limit;
    /**
     * Represents the offset value used to specify the starting point of rows
     * in a SQL query when using an "OFFSET" clause.
     *
     * The offset determines how many rows should be skipped before rows are returned.
     * A value of -1 typically signifies that no offset is applied.
     */
    private Offset offset = null;


    @Override
    public String toString() {
        return " LIMIT " + limit + (offset != null ? offset.toString() : "");
    }

    /**
     * Retrieves the maximum number of rows to be returned in the SQL query.
     * This value represents the "LIMIT" clause of the SQL statement.
     *
     * @return the maximum number of rows (limit) for the SQL query.
     */
    public int limit() {
        return this.limit;
    }

    /**
     * Retrieves the {@link Offset} instance representing the SQL OFFSET clause.
     * This object holds the specified offset value, indicating the number of rows
     * to be skipped in the query's result set.
     *
     * @return the current {@link Offset} instance, or {@code null} if no offset
     *         has been set.
     */
    public Offset offset() {
        return this.offset;
    }

    /**
     * Sets the maximum number of rows to be returned in the SQL query.
     *
     * @param limit the maximum number of rows to be returned; a non-negative integer.
     *              A value of 0 or negative may indicate no limitation depending on the SQL dialect used.
     * @return the current {@link Limit} instance for method chaining.
     */
    public Limit limit(int limit) {
        this.limit = limit;
        return this;
    }

    /**
     * Sets the offset value for the SQL query by accepting an {@link Offset} instance.
     * This determines how many rows to skip before returning results, corresponding
     * to the SQL "OFFSET" clause.
     *
     * @param offset the {@link Offset} instance representing the number of rows to skip.
     *               Typically, this value is a non-negative integer. Negative values
     *               may result in no offset being applied, depending on the SQL dialect.
     * @return the current {@code Limit} instance for method chaining.
     */
    public Limit offset(Offset offset) {
        this.offset = offset;
        return this;
    }

    /**
     * Sets the offset value to specify the starting point of rows in a SQL query
     * when using an "OFFSET" clause. This determines how many rows should be
     * skipped before rows are returned.
     *
     * @param offset the number of rows to skip, typically a non-negative integer.
     *               A negative value may indicate no offset is applied, depending
     *               on the SQL dialect.
     * @return the current {@code Limit} instance for method chaining.
     */
    public Limit offset(int offset) {
        this.offset = new Offset(offset);
        return this;
    }
}
