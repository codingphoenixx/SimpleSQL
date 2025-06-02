package dev.coph.simplesql.database.attributes;

import lombok.experimental.Accessors;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents a grouping mechanism used in a query, combining keys and conditions
 * for generating SQL "GROUP BY" and "HAVING" clauses.
 * This class allows for defining grouping keys and applying conditions
 * to further filter the grouped data.
 */
public class Group {
    /**
     * A set of unique keys used to represent or identify specific entries
     * within a group. These keys are used for query or matching purposes
     * and can be dynamically modified during runtime.
     */
    private Set<String> keys = new HashSet<>();
    /**
     * Represents a collection of conditions that define the constraints or criteria
     * for selecting rows in a query. Each condition in this set specifies a logical and/or
     * comparison operation to be applied during query execution.
     *
     * This set is primarily used to aggregate multiple conditions which can be logically
     * combined (e.g., using "AND" or "OR") to form complex query criteria.
     */
    private Set<Condition> conditions = new HashSet<>();

    @Override
    public String toString() {
        String sql = " GROUP BY ";

        if (!conditions.isEmpty() && !keys.isEmpty()) {
            sql += parseKeys() + " HAVING " + parseCondition() + " ";
            return sql;
        }

        if (!keys.isEmpty()) {
            sql += parseKeys();
        }
        return sql;
    }

    /**
     * Parsed the conditions to a string for executing
     *
     * @return the sql string
     */
    private String parseCondition() {
        if (conditions.isEmpty())
            return "";
        if (conditions.size() == 1)
            return conditions.toArray(new Condition[]{})[0].toString();
        StringBuilder parsedCondition = null;
        for (Condition condition : conditions) {
            if (parsedCondition == null) {
                parsedCondition = new StringBuilder(condition.not() ? " NOT " : "").append(condition);
                continue;
            }
            parsedCondition.append(condition.type().equals(Condition.Type.AND) ? " AND " : " OR ").append(condition.not() ? " NOT " : "").append(condition);
        }
        return parsedCondition.toString();
    }

    /**
     * Parses the set of keys from the Group object and returns them as a single, comma-separated string.
     * If the set is empty, an empty string is returned. If there is only one key, it is returned directly.
     * If there are multiple keys, they are concatenated with a comma and space.
     *
     * @return a comma-separated string of keys or an empty string if no keys are present
     */
    private String parseKeys() {
        if (keys.isEmpty())
            return "";
        if (keys.size() == 1)
            return keys.toArray(new String[]{})[0];
        StringBuilder parsedKey = null;
        for (String key : keys) {
            if (parsedKey == null) {
                parsedKey = new StringBuilder(key);
                continue;
            }
            parsedKey.append(", ").append(key);
        }
        return parsedKey.toString();
    }

    /**
     * Adds a key to the group for query or matching purposes.
     *
     * @param key The key to be added to the group.
     * @return {@link Group} for method chaining.
     */
    public Group key(String key) {
        keys.add(key);
        return this;
    }


    /**
     * Adds and condition that a row must match in order to get selected.
     *
     * @param condition The condition
     * @return {@link Group} for chaining.
     */
    public Group condition(Condition condition) {
        if (conditions == null) {
            conditions = new HashSet<>();
        }
        conditions.add(condition);
        return this;
    }

    /**
     * Adds and condition that a row must match in order to get selected. By column key and value.
     *
     * @param key   The key of the column for the condition
     * @param value The value of the row that must match with the given row key
     * @return {@link Group} for chaining.
     */
    public Group condition(String key, Object value) {
        if (conditions == null) {
            conditions = new HashSet<>();
        }
        conditions.add(new Condition(key, value));
        return this;
    }

    /**
     * Adds a condition to the query specifying that a row must match based on the given key, value, and operator.
     * This method allows for the inclusion of complex conditions by combining a key, value, and a specific operator.
     *
     * @param key      The name of the column that the condition will apply to.
     * @param value    The value that the column's data will be compared against.
     * @param operator The operator used for comparison.
     * @return {@link Group} for chaining.
     */
    public Group condition(String key, Operator operator, Object value) {
        if (conditions == null) {
            conditions = new HashSet<>();
        }
        conditions.add(new Condition(key, operator, value));
        return this;
    }

    /**
     * Retrieves the set of keys associated with the group.
     *
     * @return a set of strings representing the keys in the group.
     */
    public Set<String> keys() {
        return this.keys;
    }

    /**
     * Retrieves the set of conditions associated with this group. These conditions represent
     * the logical constraints or filters used in a query to determine selection criteria for rows.
     *
     * @return a set of {@link Condition} objects representing the conditions in the group.
     */
    public Set<Condition> conditions() {
        return this.conditions;
    }

    /**
     * Sets the keys for the group.
     *
     * @param keys A set of strings representing the keys to be assigned to the group.
     * @return The current {@link Group} instance for method chaining.
     */
    public Group keys(Set<String> keys) {
        this.keys = keys;
        return this;
    }

    /**
     * Assigns a set of {@link Condition} objects to the group, replacing the existing conditions.
     * This method is typically used to define or update the query conditions associated with the group.
     *
     * @param conditions the set of {@link Condition} objects to be assigned to the group.
     *                   Each condition represents a filter or constraint used in a query.
     * @return the current {@link Group} instance for method chaining.
     */
    public Group conditions(Set<Condition> conditions) {
        this.conditions = conditions;
        return this;
    }
}
