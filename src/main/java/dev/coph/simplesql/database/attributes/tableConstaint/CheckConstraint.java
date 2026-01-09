package dev.coph.simplesql.database.attributes.tableConstaint;

/**
 * Represents a check constraint on a database table. A check constraint is used
 * to specify a condition that must be satisfied for all rows in a table.
 * <p>
 * This class implements the {@link TableConstraint} interface, indicating that it
 * is a type of table-level constraint.
 * <p>
 * The check constraint is defined by:
 * - A name, which identifies the constraint.
 * - An expression, which specifies the condition to enforce.
 */
public record CheckConstraint(String name, String expression) implements TableConstraint {
    /**
     * Constructs a new {@code CheckConstraint} instance with the specified name and expression.
     *
     * @param name       the name of the check constraint, used to uniquely identify the constraint
     * @param expression the condition or expression that defines the check constraint
     */
    public CheckConstraint {
    }

    /**
     * Retrieves the name of the constraint. The name uniquely identifies
     * this constraint within the context of a database table.
     *
     * @return the name of the constraint
     */
    @Override
    public String name() {
        return name;
    }

    /**
     * Retrieves the expression that defines the condition enforced by the check constraint.
     *
     * @return the expression representing the condition of the check constraint
     */
    @Override
    public String expression() {
        return expression;
    }
}
