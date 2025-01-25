package dev.coph.simplesql.query.providers;

import dev.coph.simplesql.query.Query;
import dev.coph.simplesql.utils.Check;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


/**
 * Action for the {@link TableAlterQueryProvider} that will rename a table.
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

    //    ALTER TABLE tabellenname RENAME TO neuer_tabellenname;

}
