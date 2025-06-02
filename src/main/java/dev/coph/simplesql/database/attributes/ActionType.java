package dev.coph.simplesql.database.attributes;

/**
 * Represents the types of actions that can be performed.
 * This enum is used to categorize whether an action
 * is an addition or a removal operation.
 */
public enum ActionType {
    /**
     * Denotes an addition action type.
     * This constant is used to represent an operation where new data or elements
     * are added to a collection, structure, or database.
     */
    ADD,
    /**
     * Denotes a removal action type.
     * This constant is used to represent an operation where data or elements
     * are removed from a collection, structure, or database.
     */
    DROP

}
