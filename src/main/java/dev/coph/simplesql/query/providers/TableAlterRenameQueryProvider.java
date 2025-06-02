package dev.coph.simplesql.query.providers;

import dev.coph.simplesql.query.Query;
import dev.coph.simpleutilities.check.Check;
import lombok.experimental.Accessors;


/**
 * A provider for generating SQL "ALTER TABLE" queries specifically for renaming a table.
 * This class extends the base functionality provided by {@link TableAlterQueryProvider}
 * and implements the logic for constructing the query to rename an existing table.
 */
public class TableAlterRenameQueryProvider extends TableAlterQueryProvider {
    /**
     * The new name for the table
     */
    private String newTableName;


    @Override
    public String getAlterTableString(Query query) {
        Check.ifNullOrEmptyMap(newTableName, "newTableName");
        return new StringBuilder("RENAME TO ").append(newTableName).toString();
    }

    /**
     * Retrieves the new name for the table to be used in the rename operation.
     *
     * @return the new table name as a {@code String}.
     */
    public String newTableName() {
        return this.newTableName;
    }

    /**
     * Sets the new table name for the SQL "ALTER TABLE RENAME TO" operation.
     * This method is used to specify the target name to which the table should be renamed.
     *
     * @param newTableName The new name for the table.
     * @return The current instance of {@link TableAlterRenameQueryProvider} for chaining further configurations.
     */
    public TableAlterRenameQueryProvider newTableName(String newTableName) {
        this.newTableName = newTableName;
        return this;
    }
}
