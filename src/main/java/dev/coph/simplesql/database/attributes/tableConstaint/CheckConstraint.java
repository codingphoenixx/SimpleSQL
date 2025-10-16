package dev.coph.simplesql.database.attributes.tableConstaint;

public final class CheckConstraint implements TableConstraint {
    private final String name;
    private final String expression; // raw SQL expression

    public CheckConstraint(String name, String expression) {
        this.name = name;
        this.expression = expression;
    }

    public String name() {
        return name;
    }

    public String expression() {
        return expression;
    }
}
