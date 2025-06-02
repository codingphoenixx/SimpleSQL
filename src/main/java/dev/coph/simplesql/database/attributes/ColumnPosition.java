package dev.coph.simplesql.database.attributes;

/**
 * Specifies the position of a column when constructing or altering a database table or query.
 * This enumeration defines the relative placement of a column in the context of other columns.
 *<br><br>
 * The available positions are:<br>
 * - DEFAULT: Represents the default behavior or unspecified column position.<br>
 * - FIRST: Places the column as the first column in the table or query.<br>
 * - AFTER: Positions the column after a specific column (usually requires additional context to specify the target).<br>
 */
public enum ColumnPosition {
    /**
     * Represents the default behavior or an unspecified position of a column within a table or query.
     * Typically used when no specific placement (e.g., FIRST or AFTER) is defined for the column.
     */
    DEFAULT,
    /**
     * Places the column as the first column in the table or query.
     * Typically used when a column needs to be added or rearranged to appear at the very beginning
     * of the column sequence in the database table or query result.
     */
    FIRST,
    /**
     * Positions the column after a specific column in the table or query.
     * Typically used to define the placement of a new or modified column
     * relative to an existing column. Additional context is usually required
     * to specify the target column after which this should be placed.
     */
    AFTER
}
