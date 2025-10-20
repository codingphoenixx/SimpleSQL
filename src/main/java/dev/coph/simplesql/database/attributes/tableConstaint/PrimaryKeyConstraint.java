package dev.coph.simplesql.database.attributes.tableConstaint;

import java.util.List;

/**
 * Represents a primary key constraint in a database table. A primary key constraint ensures
 * that the specified columns uniquely identify each row in the table and that no null values
 * are allowed in those columns.
 * <p>
 * A primary key constraint is defined by:
 * - The name of the constraint, which uniquely identifies it within the table.
 * - A list of column names that constitute the primary key.
 * <p>
 * This class implements the {@link TableConstraint} interface, indicating that it is one
 * type of table-level constraint.
 */
public record PrimaryKeyConstraint(String name, List<String> columns) implements TableConstraint {
}
