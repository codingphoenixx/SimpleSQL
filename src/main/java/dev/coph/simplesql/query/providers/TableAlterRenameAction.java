package dev.coph.simplesql.query.providers;

import de.codingphoenix.phoenixbase.check.Checks;
import de.codingphoenix.phoenixbase.database.request.tablealter.TableAlterRequest;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


/**
 * Action for the {@link TableAlterRequest} that will rename a table.
 */
@Setter
@Getter
@Accessors(fluent = true)
public class TableAlterRenameAction implements TableAlterRequest.AlterRequestAction {
    /**
     * The new name for the table
     */
    private String newTableName;

    @Override
    public StringBuilder generateSQL() {
        Checks.checkIfNullOrEmptyMap(newTableName, "newTableName");
        return new StringBuilder("RENAME TO ").append(newTableName);
    }

    //    ALTER TABLE tabellenname RENAME TO neuer_tabellenname;

}
