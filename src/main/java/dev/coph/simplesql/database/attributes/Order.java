package dev.coph.simplesql.database.attributes;

import dev.coph.simplesql.query.Query;
import lombok.experimental.Accessors;

import java.util.HashMap;

/**
 * The {@code Order} class provides a mechanism for defining sorting rules for query results.
 * It allows you to specify fields and their sorting directions (ascending or descending)
 * that can then be converted into a SQL "ORDER BY" clause to structure a query's output.
 * This class ensures flexibility in configuring and retrieving sorting orders for dynamic query building.
 */
public class Order {
    /**
     * A mapping of field names to their corresponding sorting directions.
     * This structure is used to maintain the ordering of query results by
     * associating each field (represented as a string) with a {@link Direction}.
     * <p>
     * The {@link Direction} enum defines the sorting order, either ascending
     * or descending, to be applied for each field in the query output.
     * <p>
     * Example scenarios for this variable include cases where query results
     * need to be sorted based on one or more fields in a specific direction.
     */
    private HashMap<String, Direction> orderRules;

    /**
     * Default constructor for the {@link Order} class.
     * Initializes an instance of the {@link Order} object that holds
     * sorting rules for query results. This constructor sets up the
     * necessary structures and allows for further configuration
     * of sorting rules by adding {@link Direction}s mapped to specific fields.
     */
    public Order() {
    }

    /**
     * Adds a sorting rule by mapping a field key to a specified sorting direction.
     * This method allows the configuration of sorting rules for query results, where
     * a specific field can be set to sort in either ascending or descending order.
     * <p>
     * If the rule map is not initialized, it will be created during the method execution.
     *
     * @param key       the name of the field for which the sorting rule will be applied.
     * @param direction the sorting direction to be applied for the specified field,
     *                  either {@link Direction#ASCENDING} or {@link Direction#DESCENDING}.
     * @return the current {@link Order} object for method chaining.
     */
    public Order rule(String key, Direction direction) {
        if (orderRules == null)
            orderRules = new HashMap<>();
        orderRules.put(key, direction);
        return this;
    }

    /**
     * Converts the order rules defined in the current {@link Order} instance into a SQL "ORDER BY" clause.
     * This method generates a string representation of the sorting rules applied to a query,
     * assembling them into a valid SQL format that can be appended to a query string.
     *
     * @param query the {@link Query} object for which the "ORDER BY" clause is generated.
     *              This parameter is not directly utilized in the method but represents
     *              the query associated with the sorting rules.
     * @return a string representing the SQL "ORDER BY" clause based on the sorting rules.
     * If no rules are defined, an empty string is returned.
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
     * Retrieves the current set of sorting rules for the {@link Order} object.
     * This method returns a mapping where each key represents a field, and the associated value
     * indicates the sorting direction (either {@link Direction#ASCENDING} or {@link Direction#DESCENDING}).
     *
     * @return a {@link HashMap} containing field names as keys and {@link Direction} as values,
     * representing the defined sorting rules for query results. If no sorting rules are defined,
     * an empty {@link HashMap} is returned.
     */
    public HashMap<String, Direction> orderRules() {
        return this.orderRules;
    }

    /**
     * The {@code Direction} enum represents the sorting order that can be applied
     * to fields in a query. It defines two possible sorting directions: ascending
     * and descending, represented by constants {@link Direction#ASCENDING} and
     * {@link Direction#DESCENDING} respectively.
     */
    public enum Direction {
        /**
         * Sort by the biggest first than the lowest. Z to A
         */
        DESCENDING("DESC"),
        /**
         * Sort by the smallest first than the highest. A to Z
         */
        ASCENDING("ASC");

        /**
         * Represents the SQL operator associated with a specific sorting direction.
         * This variable stores the operator used in constructing SQL queries,
         * such as "ASC" for ascending order or "DESC" for descending order.
         * It is immutable and set during the initialization of a {@link Direction} instance.
         */
        private final String operator;

        /**
         * Constructs a {@code Direction} with the specified sorting operator.
         * The operator represents the SQL clause associated with the sorting direction,
         * such as "ASC" for ascending or "DESC" for descending.
         *
         * @param operator the SQL sorting operator corresponding to the direction
         */
        Direction(String operator) {
            this.operator = operator;
        }

        /**
         * Retrieves the SQL sorting operator associated with the current {@code Direction} instance.
         * The operator is a string that represents the sorting directive, such as "ASC" for ascending
         * or "DESC" for descending, and is used in constructing SQL query clauses.
         *
         * @return the SQL sorting operator as a string
         */
        public String operator() {
            return this.operator;
        }
    }
}
