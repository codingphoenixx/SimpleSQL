package dev.coph.simplesql.database.attributes;

/**
 * An enumeration that defines the type of selection utilized in a SQL query.
 * It indicates whether the query should return all matching rows or restrict the results
 * to unique entries.
 */
public enum SelectType {
    /**
     * Represents a `NORMAL` select type in a SQL query, typically used to retrieve all rows
     * that match the query criteria without filtering for distinct or unique results.
     */
    NORMAL,
    /**
     * Represents a `DISTINCT` select type in a SQL query, used to ensure that the result set contains only unique records by eliminating duplicate rows.
     */
    DISTINCT
}
