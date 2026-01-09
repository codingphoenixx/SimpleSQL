package dev.coph.simplesql.database.attributes.tableConstaint;

import dev.coph.simplesql.database.attributes.ForeignKeyAction;

import java.util.List;

/**
 * Represents a foreign key constraint in a database table. A foreign key constraint
 * establishes a relationship between two tables and enforces referential integrity
 * by ensuring that the referencing table's columns only allow values that exist
 * in the referenced table's columns.
 * <p>
 * A foreign key constraint is defined by:
 * - The name of the constraint, uniquely identifying it within the table.
 * - The list of columns in the referencing table.
 * - The name of the referenced table.
 * - The list of columns in the referenced table.
 * - The action to perform on the referencing rows when rows in the referenced table are deleted.
 * - The action to perform on the referencing rows when rows in the referenced table are updated.
 * <p>
 * This class implements the {@code TableConstraint} interface, indicating that it
 * is one type of table-level constraint.
 */
public record ForeignKeyConstraint(String name, List<String> columns, String refTable, List<String> refColumns,
                                   ForeignKeyAction onDelete, ForeignKeyAction onUpdate) implements TableConstraint {
}
