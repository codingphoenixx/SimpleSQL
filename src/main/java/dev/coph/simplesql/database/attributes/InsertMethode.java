package dev.coph.simplesql.database.attributes;

/**
 * The {@code InsertMethode} enumeration defines various strategies for SQL INSERT operations.
 * This is used to specify the type of INSERT operation to be performed when constructing
 * and executing a SQL query.
 */
public enum InsertMethode {
    /**
     * Represents the INSERT operation in the {@link InsertMethode} enumeration.
     * This method indicates a standard SQL INSERT operation without any additional conditions or checks.
     * It is used to add new rows of data into a database table.
     */
    INSERT,
    /**
     * Represents the INSERT_OR_UPDATE operation in the {@link InsertMethode} enumeration.
     * This method combines an INSERT operation with an on-duplicate-key update mechanism.
     * If a row with the same unique key already exists in the database, the existing row is updated
     * with the new values provided in the query instead of inserting a duplicate row.
     * It is commonly used for scenarios where upsertion (update or insert) is required.
     */
    INSERT_OR_UPDATE,
    /**
     * Represents the INSERT_IGNORE operation in the {@link InsertMethode} enumeration.
     * This method performs an INSERT operation while ignoring rows that would violate
     * a unique or primary key constraint. Instead of failing due to a duplicate key error,
     * the operation skips the conflicting row and continues with the next one.
     * Commonly used to avoid interruptions when inserting data where some conflicts are permissible.
     */
    INSERT_IGNORE
}
