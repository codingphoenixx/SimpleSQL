package dev.coph.simplesql.database.attributes;

/**
 * Represents a set of predefined operators commonly used in query generation
 * and logical expressions. This enumeration provides a standardized way
 * of handling various comparison and matching operations.
 * <p>
 * Each operator is associated with a corresponding string representation
 * that can be used in database queries, logical conditions, or expressions.
 * <p>
 * The operators are designed to handle equality checks, comparisons,
 * and pattern matching with support for case sensitivity variations.
 */
public enum Operator {
    /**
     * Represents the equals operator ("="). Typically used in SQL query generation
     * to specify conditions where values in a column must match a given value.
     */
    EQUALS("=", false),
    /**
     * Represents the "less than" operator. Commonly used to construct conditions
     * where a value is required to be smaller than a specified counterpart.
     */
    LESS_THAN("<", true),
    /**
     * Represents the "greater than" operator (">"). Frequently used to specify
     * conditions where a value must be larger than a given counterpart,
     * such as in SQL query generation or logical expressions.
     */
    GREATER_THAN(">", true),
    /**
     * Represents the "less than or equal to" operator. Commonly used to construct
     * conditions where a value must be smaller than or equal to a specified counterpart,
     * such as in SQL query generation or logical expressions.
     */
    LESS_EQUALS("<=", true),
    /**
     * Represents the "greater than or equal to" operator (">="). Commonly used to construct
     * conditions where a value must be larger than or equal to a specified counterpart,
     * such as in SQL query generation or logical expressions.
     */
    GREATER_EQUALS(">=", true),
    /**
     * Represents the "not equals" operator. Typically used in SQL query generation
     * to specify conditions where values in a column must not match a given value.
     */
    NOT_EQUALS("!=", false),
    /**
     * Represents the "IS NULL" operator used in expressions or queries.
     * <p>
     * This operator checks whether a given value is null. It is commonly
     * utilized in conditions where determining nullability is relevant, for
     * instance, in SQL queries or data filtering operations.
     * <p>
     * The {@code IS_NULL} operator does not require its operand to be a number.
     */
    IS_NULL("IS NULL", false),
    /**
     * Represents the "IS NOT NULL" operator in a query, used to determine if
     * a specific attribute or column contains a non-null value.
     * <p>
     * This operator is typically utilized in database queries or conditions
     * to filter results that explicitly have a non-null value for the specified column.
     */
    IS_NOT_NULL("IS NOT NULL", false),
    IN("IN", false),
    NOT_IN("NOT IN", false),
    BETWEEN("BETWEEN", false),
    /**
     * Represents the SQL pattern matching operator "LIKE".
     * Used for wildcard searches with '%' and '_' characters.
     * Value does not need to be a number.
     */
    LIKE("LIKE", false);

    /**
     * Represents the operator used for comparison or logical conditions in a query.
     * This variable stores the specific operation (e.g., EQUALS, LESS_THAN, GREATER_THAN)
     * to be applied when evaluating conditions.
     * <p>
     * The operator is immutable and must be defined at the time of object construction.
     */
    private final String operator;

    /**
     * Indicates whether the operator requires its operand to be a numeric value.
     * This field is used to enforce constraints on the operand type, ensuring
     * that certain operations are only performed on numerical data when required.
     */
    private final boolean needToBeANumber;

    /**
     * Constructs an Operator instance with the given operator string.
     *
     * @param operator        the string representation of the operator, which defines the type
     *                        of operation to be used in a specific context (e.g., comparisons
     *                        in queries or conditions).
     * @param needToBeANumber
     */
    Operator(String operator, boolean needToBeANumber) {
        this.operator = operator;
        this.needToBeANumber = needToBeANumber;
    }

    /**
     * Retrieves the string representation of the operator.
     *
     * @return a string representing the operator, which defines the type of operation to be
     * used in a specific context (e.g., comparisons in queries or conditions).
     */
    public String operator() {
        return this.operator;
    }

    /**
     * Determines whether the operator requires the operand to be a number.
     *
     * @return true if the operand needs to be a number; false otherwise.
     */
    public boolean needToBeANumber() {
        return needToBeANumber;
    }
}
