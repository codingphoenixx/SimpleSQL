package dev.coph.simplesql.database.attributes.tableConstaint;

/**
 * Represents a general table constraint in a database. A table constraint defines
 * rules or behaviors applied to columns or data within a table.
 * <p>
 * This is a sealed interface, which allows only specific implementations
 * that define different types of constraints. The permitted implementations
 * include:
 * - {@code PrimaryKeyConstraint}: Represents the primary key of a table.
 * - {@code UniqueConstraint}: Ensures a set of columns have unique values.
 * - {@code IndexConstraint}: Defines an index on one or more columns.
 * - {@code ForeignKeyConstraint}: Establishes relationships between tables.
 * - {@code CheckConstraint}: Enforces a condition on the data in a table.
 * <p>
 * Each table constraint has a name that uniquely identifies it within the context
 * of the table it is applied to.
 */
public sealed interface TableConstraint permits PrimaryKeyConstraint, UniqueConstraint, IndexConstraint, ForeignKeyConstraint, CheckConstraint {
    /**
     * Retrieves the name of the table constraint. The name uniquely identifies
     * the constraint within the context of the table it is associated with.
     *
     * @return the name of the table constraint
     */
    String name();
}

