package dev.coph.simplesql.database.attributes;

import java.util.HashSet;
import java.util.Set;


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


    public Group key(String key) {
        keys.add(key);
        return this;
    }


    public Group condition(Condition condition) {
        if (conditions == null) {
            conditions = new HashSet<>();
        }
        conditions.add(condition);
        return this;
    }


    public Group condition(String key, Object value) {
        if (conditions == null) {
            conditions = new HashSet<>();
        }
        conditions.add(new Condition(key, value));
        return this;
    }


    public Group condition(String key, Operator operator, Object value) {
        if (conditions == null) {
            conditions = new HashSet<>();
        }
        conditions.add(new Condition(key, operator, value));
        return this;
    }


    public Set<String> keys() {
        return this.keys;
    }


    public Set<Condition> conditions() {
        return this.conditions;
    }


    public Group keys(Set<String> keys) {
        this.keys = keys;
        return this;
    }


    public Group conditions(Set<Condition> conditions) {
        this.conditions = conditions;
        return this;
    }
}
