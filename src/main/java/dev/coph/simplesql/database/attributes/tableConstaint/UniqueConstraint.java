package dev.coph.simplesql.database.attributes.tableConstaint;

import java.util.List;

/**
 * Represents a unique constraint in a database table. A unique constraint ensures that
 * the values in the specified columns are unique across all rows in the table.
 *
 * This class implements the {@link TableConstraint} interface, indicating that it is one
 * type of table-level constraint.
 *
 * The unique constraint is defined by:
 * - The name of the constraint, which uniquely identifies it within the table.
 * - A list of column names that are subject to the uniqueness rule.
 */
public record UniqueConstraint(String name, List<String> columns) implements TableConstraint {
}
