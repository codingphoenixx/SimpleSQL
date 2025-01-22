package dev.coph.simplesql.database.attributes;

import dev.coph.simplesql.utils.Check;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true, fluent = true)
public class Condition {
    private String key;
    private Object value;
    private Type type = Type.AND;
    private Operator operator = Operator.EQUALS;
    private boolean not = false;

    public Condition(String key, Object value) {
        this.key = key;
        this.value = value;
    }

    public Condition(String key, Operator operator, Object value) {
        this.key = key;
        this.value = value;
        this.operator = operator;
    }

    @Override
    public String toString() {
        Check.ifNull(key, "key");
        Check.ifNull(value, "value");

        if (operator == Operator.EQUALS)
            return key + "='" + value + "'";

        Check.ifNotNumber(value, "value");

        return key + " " + operator.operator() + " " + value;
    }

    @Getter
    @Accessors(fluent = true)
    public enum Type {
        AND, OR,
    }
}
