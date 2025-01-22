package dev.coph.simplesql.database.attributes;

import lombok.Getter;
import lombok.experimental.Accessors;

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
    SUM("SUM");

    /**
     * The sql key for the function
     */
    @Getter
    @Accessors(fluent = true)
    private final String function;

    SelectFunction(String funktion) {
        this.function = funktion;
    }
}
