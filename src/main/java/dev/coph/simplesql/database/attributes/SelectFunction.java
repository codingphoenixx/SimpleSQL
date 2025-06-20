package dev.coph.simplesql.database.attributes;

import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * Represents different SQL aggregation or selection functions that can be applied
 * to the results of a SELECT statement in a database query.
 * <p>
 * These functions determine how data is aggregated or manipulated during query processing.
 */
public enum SelectFunction {
    /**
     * The default function. This will return the results directly.
     */
    NORMAL(""),
    /**
     * Counts the amount of rows that returns from the SELECT statement.
     */
    COUNT("COUNT"),
    /**
     * Calculates the average value of a numerical dataset that returns from the SELECT statement.
     */
    AVERAGE("AVG"),
    /**
     * Calculates the sum value of a numerical dataset that returns from the SELECT statement.
     */
    SUM("SUM"),
    /**
     * Selects the maximum value of a specific column
     */
    MAX("MAX");

    /**
     * Represents the SQL aggregation or selection function associated with a
     * specific enumeration value in the {@link SelectFunction} enum.
     * This string is used to define how data is aggregated or manipulated
     * in a database query.
     */
    private final String function;

    /**
     * Constructs a new {@code SelectFunction} instance with the specified SQL aggregation
     * or selection function represented as a string.
     *
     * @param funktion the SQL aggregation or selection function to be associated with this instance.
     *                 This string determines how the data is aggregated or manipulated during a query.
     */
    SelectFunction(String funktion) {
        this.function = funktion;
    }

    /**
     * Retrieves the SQL function associated with the current instance of the {@link SelectFunction} enum.
     * The returned function string specifies how data is aggregated or manipulated in a database query.
     *
     * @return a string representing the SQL function for this enum instance (e.g., "COUNT", "AVG", "SUM", or an empty string for default).
     */
    public String function() {
        return this.function;
    }
}
