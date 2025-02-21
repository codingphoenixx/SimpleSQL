package dev.coph.simplesql.database.attributes;

import dev.coph.simpleutilities.check.Check;
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

    private SelectFunction selectFunction;
    private String selectKey;


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

        String queryKey = key;
        if (selectFunction != null && !selectFunction.equals(SelectFunction.NORMAL) && selectKey != null) {
            queryKey = selectFunction.function() + "(" + selectKey + ")";
        }

        if (operator == Operator.EQUALS)
            return queryKey + "='" + value + "'";

        Check.ifNotNumber(value, "value");

        return queryKey + " " + operator.operator() + " " + value;
    }

    @Getter
    @Accessors(fluent = true)
    public enum Type {
        AND, OR,
    }
}
