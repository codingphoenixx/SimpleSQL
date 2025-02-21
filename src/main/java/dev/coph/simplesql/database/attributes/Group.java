package dev.coph.simplesql.database.attributes;

import dev.coph.simplesql.query.providers.SelectQueryProvider;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Accessors(fluent = true, chain = true)
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

        if(!keys.isEmpty()){
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
}
