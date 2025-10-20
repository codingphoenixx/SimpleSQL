package dev.coph.simplesql.database.attributes;

import dev.coph.simplesql.query.Query;

import java.util.HashMap;

/**
 * The Order class is responsible for defining sorting rules applied to queries.
 * It facilitates constructing SQL-style ordering instructions for query results.
 */
public class Order {

    private HashMap<String, Direction> orderRules;

    /**
     * Constructs an empty Order instance.
     * <p>
     * This constructor initializes an Order object for defining sorting rules, where
     * each rule specifies a field and its ordering direction (e.g., ascending or descending).
     */
    public Order() {
    }

    /**
     * Adds a sorting rule to the current Order instance by specifying a field and its direction.
     * The rule is stored as a key-value pair where the key represents the field name, and the value
     * is the sorting direction (e.g., ascending or descending).
     *
     * @param key       the name of the field to be sorted
     * @param direction the sorting direction, either {@code ASCENDING} or {@code DESCENDING}
     * @return the updated Order instance with the new sorting rule included
     */
    public Order rule(String key, Direction direction) {
        if (orderRules == null)
            orderRules = new HashMap<>();
        orderRules.put(key, direction);
        return this;
    }

    /**
     * Generates a SQL-style ORDER BY clause based on the defined sorting rules.
     * <p>
     * The method concatenates field names and their respective sorting directions
     * (ascending or descending) into a single ORDER BY statement suitable for query execution.
     * If no sorting rules are defined, it returns an empty string.
     *
     * @param query the query object used to define context for constructing the ORDER BY clause
     * @return a string representing the ORDER BY clause based on the current sorting rules;
     * an empty string if no sorting rules are available
     */
    public String toString(Query query) {
        if (orderRules == null || orderRules.isEmpty()) {
            return "";
        }
        if (orderRules.size() == 1) {
            String key = orderRules.keySet().toArray(new String[]{})[0];
            return " ORDER BY " + key + " " + orderRules.get(key).operator();
        }
        StringBuilder orderCommand = null;
        for (String key : orderRules.keySet()) {
            if (orderCommand == null) {
                orderCommand = new StringBuilder(" ORDER BY " + key + " " + orderRules.get(key).operator());
                continue;
            }
            orderCommand.append(", ").append(key).append(" ").append(orderRules.get(key).operator());
        }
        return orderCommand.toString();
    }

    /**
     * Retrieves the current sorting rules defined within the Order instance.
     * <p>
     * The sorting rules are represented as a mapping of field names (keys) to their corresponding
     * sorting directions (values), such as ascending or descending. This method returns the rules
     * stored in the current Order object.
     *
     * @return a HashMap containing the field names as keys and their sorting directions as values
     */
    public HashMap<String, Direction> orderRules() {
        return this.orderRules;
    }

    /**
     * Represents the direction of sorting order.
     * <p>
     * The {@code Direction} enum defines two possible values:
     * {@code ASCENDING} and {@code DESCENDING}, which can be used to
     * specify the order in which items should be sorted.
     */
    public enum Direction {
        /**
         * Indicates a descending sorting direction.
         * <p>
         * The {@code DESCENDING} constant is used to specify that items should be
         * sorted in descending order, i.e., from highest to lowest or reverse
         * alphabetical order.
         * <p>
         * It is represented internally by the string "DESC".
         */
        DESCENDING("DESC"),
        /**
         * Indicates an ascending sorting direction.
         * <p>
         * The {@code ASCENDING} constant is used to specify that items should be
         * sorted in ascending order, i.e., from lowest to highest or alphabetical order.
         * <p>
         * It is represented internally by the string "ASC".
         */
        ASCENDING("ASC");


        private final String operator;

        /**
         * Constructs a Direction instance with a specified operator.
         *
         * @param operator the string representation of the sorting direction,
         *                 such as "ASC" for ascending or "DESC" for descending
         */
        Direction(String operator) {
            this.operator = operator;
        }

        /**
         * Retrieves the string representation of the sorting direction associated with this instance.
         * The string representation typically corresponds to predefined values such as "ASC" or "DESC".
         *
         * @return the string representation of the sorting direction
         */
        public String operator() {
            return this.operator;
        }
    }
}
