package dev.coph.simplesql.database.attributes;

public enum SelectType {
    /**
     * Represents a `NORMAL` select type in a SQL query, typically used to retrieve all rows
     * that match the query criteria without filtering for distinct or unique results.
     */
    NORMAL,
    /**
     * Represents a `DISTINCT` select type in a SQL query, used to ensure that the result set contains only unique records by eliminating duplicate rows.
     */
    DISTINCT;
}
