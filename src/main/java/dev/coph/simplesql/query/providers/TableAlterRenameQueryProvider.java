package dev.coph.simplesql.query.providers;

import dev.coph.simplesql.query.Query;
import dev.coph.simpleutilities.check.Check;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


/**
 * A provider for generating SQL "ALTER TABLE" queries specifically for renaming a table.
 * This class extends the base functionality provided by {@link TableAlterQueryProvider}
 * and implements the logic for constructing the query to rename an existing table.
 */
@Setter
@Getter
@Accessors(fluent = true)
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

}
