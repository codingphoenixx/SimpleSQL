package dev.coph.simplesql.database.attributes;

import java.util.HashSet;
import java.util.Set;

/**
 * The Group class is used to represent a SQL GROUP BY clause along with its associated
 * HAVING conditions. It allows defining grouping keys as well as conditions that can
 * be applied to the grouped data.
 * <p>
 * The class provides methods to add grouping keys, define conditions, and construct
 * the SQL representation of the GROUP BY clause.
 */
public class Group {

    private Set<String> keys = new HashSet<>();

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
     * Parses the conditions stored in the group and concatenates them into a single string
     * representation. The method handles multiple conditions, combining them with appropriate
     * logical operators (AND/OR) and considering any NOT modifiers.
     *
     * @return A string representation of the combined conditions stored in the group.
     * If no conditions are present, an empty string is returned.
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
     * Parses the keys stored in the set and concatenates them into a single string representation.
     * If there is only one key, it will return that key directly.
     * For multiple keys, they are joined with a comma separator.
     *
     * @return A comma-separated string of keys. If no keys are present, an empty string is returned.
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
     * Adds the specified key to the group and returns the current instance of the Group.
     *
     * @param key The key to be added to the group.
     * @return The current instance of the Group with the newly added key.
     */
    public Group key(String key) {
        keys.add(key);
        return this;
    }

    /**
     * Adds a condition to the current group and returns the updated instance of the group.
     * If the set of conditions is not initialized, it initializes the set before adding
     * the specified condition.
     *
     * @param condition The condition to be added to the group. Must not be null.
     * @return The current instance of the Group with the newly added condition.
     */
    public Group condition(Condition condition) {
        if (conditions == null) {
            conditions = new HashSet<>();
        }
        conditions.add(condition);
        return this;
    }

    /**
     * Adds a condition to the current group using the specified key and value, and returns
     * the updated instance of the group. If the set of conditions is not initialized, it
     * initializes the set before adding the condition.
     *
     * @param key   The key associated with the condition. Must not be null or empty.
     * @param value The value associated with the condition. Can be any object, including null.
     * @return The current instance of the Group with the newly added condition.
     */
    public Group condition(String key, Object value) {
        if (conditions == null) {
            conditions = new HashSet<>();
        }
        conditions.add(new Condition(key, value));
        return this;
    }

    /**
     * Adds a condition to the current group using the specified key, operator, and value,
     * and returns the updated instance of the group. If the set of conditions is not initialized,
     * it initializes the set before adding the condition.
     *
     * @param key      The key associated with the condition. Must not be null or empty.
     * @param operator The operator to be used in the condition. Must not be null.
     * @param value    The value associated with the condition. Can be any object, including null.
     * @return The current instance of the Group with the newly added condition.
     */
    public Group condition(String key, Operator operator, Object value) {
        if (conditions == null) {
            conditions = new HashSet<>();
        }
        conditions.add(new Condition(key, operator, value));
        return this;
    }

    /**
     * Retrieves the set of keys associated with the current group.
     *
     * @return A set of strings representing the keys in the group.
     * If no keys are present, an empty set is returned.
     */
    public Set<String> keys() {
        return this.keys;
    }

    /**
     * Retrieves the set of conditions associated with the current group.
     *
     * @return A set of conditions representing the conditions in the group.
     * If no conditions are present, an empty set is returned.
     */
    public Set<Condition> conditions() {
        return this.conditions;
    }

    /**
     * Sets the keys for the current group.
     *
     * @param keys The set of keys to associate with the group. Cannot be null.
     * @return The current instance of the Group with the updated keys.
     */
    public Group keys(Set<String> keys) {
        this.keys = keys;
        return this;
    }

    /**
     * Sets the conditions for the current group and returns the updated instance
     * of the group. The existing set of conditions will be replaced with the
     * provided set.
     *
     * @param conditions The set of conditions to associate with the group. Cannot be null.
     * @return The current instance of the Group with the updated conditions.
     */
    public Group conditions(Set<Condition> conditions) {
        this.conditions = conditions;
        return this;
    }
}
