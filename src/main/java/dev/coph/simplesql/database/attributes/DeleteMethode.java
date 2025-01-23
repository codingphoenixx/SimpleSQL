package dev.coph.simplesql.database.attributes;

/**
 * The DeleteMethode enum defines different strategies for performing deletion operations
 * in a database system. Each method specifies the conditions or behavior that should
 * be adhered to during the delete operation.
 */
public enum DeleteMethode {
    /**
     * Represents the default deletion method. This method does not impose additional conditions
     * or checks before executing a delete operation, and is intended to provide a straightforward,
     * unguarded deletion mechanism.
     */
    DEFAULT,
    /**
     * Represents a deletion method that checks for the existence of a resource before performing
     * a delete operation. This method ensures that the delete process is only executed if the
     * specified resource exists, providing a conditional safeguard mechanism during deletion.
     */
    IF_EXISTS,
}
