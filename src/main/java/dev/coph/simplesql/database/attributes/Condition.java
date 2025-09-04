package dev.coph.simplesql.database.attributes;

import dev.coph.simpleutilities.check.Check;

/**
 * This class represents a condition used in SQL queries.
 * It serves to define filter criteria and supports various comparison operators,
 * logical operators, and special selection functions.
 * <p>
 * The class enables the creation of complex conditions with a variety of
 * configuration options and supports a fluent API style for method chaining.
 */
public class Condition {
    /**
     * Represents the key of a condition used in SQL queries. This key typically corresponds
     * to the column name or attribute in the database that the condition applies to.
     * It serves as the left-hand operand in SQL expressions, paired with an operator and a value.
     */
    private String key;
    /**
     * Represents the value associated with a condition in an SQL query. This value serves as the
     * right-hand operand of the condition, paired with a key and operator to define the comparison
     * or matching criteria. The value can be of any object type, allowing flexibility to handle
     * various data formats.
     */
    private Object value;
    /**
     * Specifies the logical type used to combine multiple conditions in a query. The type determines
     * whether individual conditions are combined using an "AND" or "OR" logical operator.
     * <p>
     * Default value is {@code Type.AND}.
     */
    private Type type = Type.AND;
    /**
     * Defines the comparison operator used in a condition. The operator specifies how the key and value of the condition
     * will be compared during query execution. It defines the nature of the relationship between the key and value in the condition,
     * such as equality, inequality, greater than, or less than.
     * <p>
     * Default value is {@code Operator.EQUALS}.
     */
    private Operator operator = Operator.EQUALS;
    /**
     * Represents a flag to indicate whether a specific condition is negated.
     * When set to true, it applies a logical NOT operation to the associated condition.
     */
    private boolean not = false;

    /**
     * Represents the function to be applied during the selection of data
     * in a query. This can modify how data is processed or aggregated.
     * <br>
     * This variable, when used, specifies the type of aggregation or
     * processing to be performed on a query result.
     */
    private SelectFunction keySelectFunction;
    /**
     * Represents the {@code SelectFunction} that is applied specifically to the value
     * in the condition. This function determines the SQL aggregation or selection
     * operation that should be performed on the value during query processing.
     * <p>
     * The {@code valueSelectFunction} can be used to specify functions such as
     * {@code COUNT}, {@code SUM}, {@code AVERAGE}, or others to manipulate or
     * aggregate the value associated with the condition.
     */
    private SelectFunction valueSelectFunction;


    /**
     * Constructs a Condition object with a specified key and value.
     *
     * @param key   The key corresponding to the column this condition applies to.
     * @param value The value that the column's data must match for this condition.
     */
    public Condition(String key, Object value) {
        this.key = key;
        this.value = value;
    }

    /**
     * Constructs a Condition object with a specified key, operator, and value.
     *
     * @param key      The key representing the column this condition applies to.
     * @param operator The operator defining how the key's value will be evaluated (e.g., equals, greater than).
     * @param value    The value to compare against the column's data.
     */
    public Condition(String key, Operator operator, Object value) {
        this.key = key;
        this.value = value;
        this.operator = operator;
    }

    /**
     * Converts the current `Condition` object to its string representation.
     * The string representation of the condition is structured to represent an SQL condition
     * based on the key, value, operator, and any specific select functions applied.
     *
     * @return A string representing the condition in an SQL-compatible format.
     * For equality conditions, the format is "{key}='{value}'".
     * For other operators, the format is "{key} {operator} {value}".
     */
    @Override
    public String toString() {
        Check.ifNull(key, "key");
        Check.ifNull(value, "value");

        String queryKey = key;
        if (keySelectFunction != null && !keySelectFunction.equals(SelectFunction.NORMAL)) {
            queryKey = keySelectFunction.function() + "(" + key + ")";
        }

        String queryValue;
        if (operator.needToBeANumber()) {
            queryValue = value.toString();
            Check.ifNotNumber(value, "value");
        } else
            queryValue = "'" + value + "'";

        if (valueSelectFunction != null && !valueSelectFunction.equals(SelectFunction.NORMAL) && queryValue != null) {
            queryValue = valueSelectFunction.function() + "(" + queryValue + ")";
        }

        return queryKey + " " + operator.operator() + " " + queryValue;
    }

    /**
     * Retrieves the key of the condition, representing the column name this condition is applied to.
     *
     * @return a string representing the key of the condition.
     */
    public String key() {
        return this.key;
    }

    /**
     * Retrieves the value associated with this condition.
     * The value represents the data or criteria that this condition is evaluating.
     *
     * @return the value of the condition as an Object.
     */
    public Object value() {
        return this.value;
    }

    /**
     * Retrieves the type of logical operator associated with this condition.
     * The logical operator determines how this condition is combined with other conditions,
     * such as using an AND or OR operation.
     *
     * @return the {@code Type} representing the logical operator (e.g., {@code Type.AND} or {@code Type.OR}).
     */
    public Type type() {
        return this.type;
    }

    /**
     * Retrieves the operator associated with this condition.
     * The operator is used to define how the key's value is evaluated,
     * such as equals, greater than, or less than.
     *
     * @return the operator of this condition as an instance of {@code Operator}.
     */
    public Operator operator() {
        return this.operator;
    }

    /**
     * Indicates whether the condition should be logically negated.
     *
     * @return {@code true} if the condition is negated; {@code false} otherwise.
     */
    public boolean not() {
        return this.not;
    }

    /**
     * Retrieves the {@code SelectFunction} applied to the current condition.
     * The {@code SelectFunction} determines the SQL aggregation or selection function
     * used for the condition, such as COUNT, SUM, or MAX.
     *
     * @return the {@code SelectFunction} associated with this condition.
     */
    public SelectFunction keySelectFunction() {
        return this.keySelectFunction;
    }

    /**
     * Retrieves the {@code SelectFunction} associated with the {@code value} of this condition.
     * The {@code SelectFunction} defines the SQL aggregation or selection function
     * applied specifically to the value of this condition, such as COUNT, SUM, or AVERAGE.
     *
     * @return the {@code SelectFunction} applied to the value of this condition.
     */
    public SelectFunction valueSelectFunction() {
        return this.valueSelectFunction;
    }


    /**
     * Sets the key of the condition, representing the column that this condition applies to.
     *
     * @param key The key corresponding to the column this condition applies to.
     * @return The current {@code Condition} instance with the updated key.
     */
    public Condition key(String key) {
        this.key = key;
        return this;
    }

    /**
     * Sets the value for this condition.
     *
     * @param value The value to be used in the condition. It represents the data or criteria
     *              that the column's data must match for this condition.
     * @return The current {@code Condition} instance with the updated value.
     */
    public Condition value(Object value) {
        this.value = value;
        return this;
    }

    /**
     * Sets the logical operator type for this condition.
     *
     * @param type The {@code Type} representing the logical operator to be applied to this condition (e.g., {@code Type.AND} or {@code Type.OR}).
     * @return The current {@code Condition} instance with the updated logical operator type.
     */
    public Condition type(Type type) {
        this.type = type;
        return this;
    }

    /**
     * Sets the operator for this condition, defining how the key's value will
     * be evaluated in a query or logical expression.
     *
     * @param operator The operator to be applied to this condition. This could represent
     *                 operations such as equality checks, comparisons, or pattern matching.
     * @return The current {@code Condition} instance with the updated operator.
     */
    public Condition operator(Operator operator) {
        this.operator = operator;
        return this;
    }

    /**
     * Sets the negation flag for this condition. If set to true, the condition
     * will be logically negated during evaluation.
     *
     * @param not a boolean flag indicating whether the condition should be negated
     *            ({@code true}) or not ({@code false}).
     * @return the current {@code Condition} instance with the updated negation flag.
     */
    public Condition not(boolean not) {
        this.not = not;
        return this;
    }

    /**
     * Sets the {@code SelectFunction} for the current {@code Condition} object.
     * The {@code SelectFunction} determines the SQL aggregation or selection function
     * applied to the specific condition, such as COUNT, SUM, or MAX.
     *
     * @param selectFunction the {@code SelectFunction} to be applied to this condition.
     *                       This function is used to manipulate or aggregate data.
     * @return the current {@code Condition} instance with the updated {@code SelectFunction}.
     */
    public Condition keySelectFunction(SelectFunction selectFunction) {
        this.keySelectFunction = selectFunction;
        return this;
    }

    /**
     * Sets the {@code SelectFunction} to be applied to the value of the current {@code Condition}.
     * The {@code SelectFunction} defines the SQL aggregation or selection function
     * applied specifically to the value, such as COUNT, SUM, or AVERAGE.
     *
     * @param valueSelectFunction the {@code SelectFunction} to be applied to the value of this condition.
     *                            This determines how the value will be aggregated or manipulated in a query.
     * @return the current {@code Condition} instance with the updated {@code valueSelectFunction}.
     */
    public Condition valueSelectFunction(SelectFunction valueSelectFunction) {
        this.valueSelectFunction = valueSelectFunction;
        return this;
    }

    /**
     * Represents the types of logical operators that can be used in a condition.
     * The {@code Type} enum defines two possible values:
     * <p>
     * AND - Represents a logical AND operation.<br>
     * OR - Represents a logical OR operation.
     * <p>
     * These types are typically used to combine multiple conditions in query statements.
     */
    public enum Type {
        /**
         * Represents a logical AND operation within a condition.
         * This type is used to combine multiple conditions where all
         * conditions must be satisfied for the logical expression to evaluate as true.
         */
        AND,
        /**
         * Represents a logical OR operation within a condition.
         * This type is used to combine multiple conditions where at least one
         * condition must be satisfied for the logical expression to evaluate as true.
         */
        OR,
    }
}
