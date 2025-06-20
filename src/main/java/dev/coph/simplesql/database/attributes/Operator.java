package dev.coph.simplesql.database.attributes;

import lombok.experimental.Accessors;

/**
 * Represents a set of predefined operators commonly used in query generation
 * and logical expressions. This enumeration provides a standardized way
 * of handling various comparison and matching operations.
 *
 * Each operator is associated with a corresponding string representation
 * that can be used in database queries, logical conditions, or expressions.
 *
 * The operators are designed to handle equality checks, comparisons,
 * and pattern matching with support for case sensitivity variations.
 */
public enum Operator {
    /**
     * Represents the equals operator ("="). Typically used in SQL query generation
     * to specify conditions where values in a column must match a given value.
     */
    EQUALS("="),
    /**
     * Represents the "less than" operator. Commonly used to construct conditions
     * where a value is required to be smaller than a specified counterpart.
     */
    SMALLER_THAN("<"),
    /**
     * Represents the "greater than" operator (">"). Frequently used to specify
     * conditions where a value must be larger than a given counterpart,
     * such as in SQL query generation or logical expressions.
     */
    GREATER_THAN(">"),
    /**
     * Represents the "less than or equal to" operator. Commonly used to construct
     * conditions where a value must be smaller than or equal to a specified counterpart,
     * such as in SQL query generation or logical expressions.
     */
    SMALLER_EQUALS_THAN("<="),
    /**
     * Represents the "greater than or equal to" operator (">="). Commonly used to construct
     * conditions where a value must be larger than or equal to a specified counterpart,
     * such as in SQL query generation or logical expressions.
     */
    GREATER_EQUALS_THAN(">="),
    /**
     * Represents the "not equals" operator. Typically used in SQL query generation
     * to specify conditions where values in a column must not match a given value.
     */
    NOT_EQUALS("<>"),
    /**
     * Represents a case-sensitive match operator ("~"). Typically used to construct
     * conditions where the comparison between values must consider case sensitivity.
     */
    MATCH_CASE_SENSITIVE("~"),
    /**
     * Represents a case-insensitive match operator ("~*"). Typically used to construct
     * conditions where value comparisons should disregard case sensitivity, such as
     * in SQL query generation or logical expressions.
     */
    MATCH_CASE_INSENSITIVE("~*"),
    /**
     * Represents a case-sensitive "not match" operator ("!~").
     * Typically used to construct conditions where a value does not match a given pattern
     * or string while considering case sensitivity, such as in SQL query generation or logical expressions.
     */
    NOT_MATCH_CASE_SENSITIVE("!~"),
    /**
     * Represents a case-insensitive "not match" operator ("!~*").
     * Typically used to construct conditions where a value does not match a given pattern
     * or string while disregarding case sensitivity, such as in SQL query generation
     * or logical expressions.
     */
    NOT_MATCH_CASE_INSENSITIVE("!~*");

    /**
     * Represents the operator used for comparison or logical conditions in a query.
     * This variable stores the specific operation (e.g., EQUALS, LESS_THAN, GREATER_THAN)
     * to be applied when evaluating conditions.
     *
     * The operator is immutable and must be defined at the time of object construction.
     */
    private final String operator;

    /**
     * Constructs an Operator instance with the given operator string.
     *
     * @param operator the string representation of the operator, which defines the type
     *                 of operation to be used in a specific context (e.g., comparisons
     *                 in queries or conditions).
     */
    Operator(String operator) {
        this.operator = operator;
    }

    /**
     * Retrieves the string representation of the operator.
     *
     * @return a string representing the operator, which defines the type of operation to be
     *         used in a specific context (e.g., comparisons in queries or conditions).
     */
    public String operator() {
        return this.operator;
    }
}
