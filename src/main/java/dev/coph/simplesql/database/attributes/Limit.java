package dev.coph.simplesql.database.attributes;


/**
 * Represents a limit and offset configuration typically used for pagination
 * in database queries. This class allows setting and retrieving the limit
 * (maximum number of records) and offset (starting position for the records).
 */
public class Limit {

    private int limit;
    private int offset = 0;

    /**
     * Retrieves the maximum number of records (limit) set for pagination or query configuration.
     *
     * @return the limit value representing the maximum number of records to retrieve
     */
    public int limit() {
        return this.limit;
    }

    /**
     * Retrieves the offset value, which indicates the starting position
     * for records in a paginated or limited query.
     *
     * @return the offset value representing the number of records to skip
     * before starting to return results
     */
    public int offset() {
        return this.offset;
    }

    /**
     * Sets the limit (maximum number of records) for pagination or query configuration.
     *
     * @param limit the maximum number of records to be retrieved
     * @return the current instance of the Limit class, allowing for method chaining
     */
    public Limit limit(int limit) {
        this.limit = limit;
        return this;
    }

    /**
     * Sets the offset, which indicates the starting position for records in a
     * paginated or limited query.
     *
     * @param offset the number of records to skip before starting to return results
     * @return the current instance of the Limit class, allowing for method chaining
     */
    public Limit offset(int offset) {
        this.offset = offset;
        return this;
    }

}
