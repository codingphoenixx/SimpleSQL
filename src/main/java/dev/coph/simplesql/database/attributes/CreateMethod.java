package dev.coph.simplesql.database.attributes;

/**
 * The {@code CreateMethod} enumeration defines the strategies that can be used
 * during the creation of database structures, such as tables, within a SQL
 * generation framework.
 */
public enum CreateMethod {
    /**
     * Represents the default create method in the {@link CreateMethod} enumeration.
     * This method indicates a standard create operation without specific conditions
     * or checks, typically used for initializing or setting up database structures.
     */
    DEFAULT,
    /**
     * Represents the IF_NOT_EXISTS create method in the {@link CreateMethod} enumeration.
     * This method is used to conditionally create a database structure, such as a table,
     * only if it does not already exist. It helps to avoid errors or conflicts that may
     * occur when attempting to create a structure that is already present.
     */
    IF_NOT_EXISTS,
}
