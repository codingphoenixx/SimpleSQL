package dev.coph.simplesql.database.attributes;

/**
 * Represents the behavior for handling dependent records when a record
 * in the database is removed. This enum is commonly used to define how
 * foreign key constraints should behave during a delete operation.
 * <p>
 * NONE:
 * No action is taken on dependent records when the parent record is deleted.
 * <p>
 * CASCADE:
 * Dependent records are also deleted when the parent record is removed.
 * <p>
 * RESTRICT:
 * Prevents the deletion of the parent record if dependent records exist.
 */
public enum DropBehaviour {
    /**
     * No action is taken on dependent records when the parent record is deleted.
     * Represents a default behavior where no cascading or restrictive actions
     * are applied on associated records.
     */
    NONE,
    /**
     * Represents the behavior where dependent records are deleted when the parent
     * record is removed. This is typically used to define a cascading delete
     * action in database operations, ensuring that related records in child tables
     * are automatically removed when a corresponding record in a parent table is
     * deleted.
     */
    CASCADE,
    /**
     * Prevents the deletion of the parent record if dependent records exist.
     * This behavior enforces referential integrity by disallowing the removal
     * of a record when there are associated records in related tables that
     * depend on it.
     */
    RESTRICT

}
