package dev.coph.simplesql.database.attributes;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
public enum Operator {
    EQUALS("="), SMALLER_THAN("<"), GREATER_THAN(">"), SMALLER_EQUALS_THAN("<="), GREATER_EQUALS_THAN(">="), NOT_EQUALS("<>");

    private final String operator;

    Operator(String operator) {
        this.operator = operator;
    }
}
