package dev.coph.simplesql.database.attributes.tableConstaint;

import java.util.List;

/**
 * Represents an index constraint in a database table. An index constraint defines
 * an index on one or more columns of a table to improve query performance.
 * Additionally, this constraint can indicate whether the index enforces uniqueness.
 * <p>
 * This class implements the {@link TableConstraint} interface, indicating that it
 * is a type of table-level constraint.
 * <p>
 * The index constraint is defined by:
 * - The name of the constraint, which uniquely identifies it.
 * - The list of columns involved in the index.
 * - A flag indicating whether the index enforces unique values across the specified columns.
 */
public record IndexConstraint(String name, List<String> columns, boolean unique) implements TableConstraint {
}
