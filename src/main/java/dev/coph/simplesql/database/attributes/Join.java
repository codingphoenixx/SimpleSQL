package dev.coph.simplesql.database.attributes;

import java.util.LinkedHashSet;
import java.util.Set;

public class Join {
    private final JoinType type;
    private final String table;
    private final String alias;
    private final LinkedHashSet<Condition> onConditions = new LinkedHashSet<>();
    public Join(JoinType type, String table, String alias) {
        this.type = type;
        this.table = table;
        this.alias = alias;
    }

    public Join on(Condition condition) {
        if (condition != null) onConditions.add(condition);
        return this;
    }

    public Join on(String column, Object value) {
        return on(new Condition(column, value));
    }

    public Join on(String column, Operator operator, Object value) {
        return on(new Condition(column, operator, value));
    }

    public JoinType type() {
        return type;
    }

    public String table() {
        return table;
    }

    public String alias() {
        return alias;
    }

    public Set<Condition> onConditions() {
        return onConditions;
    }

    public enum JoinType {INNER, LEFT, RIGHT, FULL}
}
