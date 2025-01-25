package dev.coph.simplesql.query.providers;

import de.codingphoenix.phoenixbase.check.Checks;
import de.codingphoenix.phoenixbase.database.request.tablealter.TableAlterRequest;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Action for the {@link TableAlterRequest} that will drop a table.
 */
@Setter
@Getter
@Accessors(fluent = true)
public class TableAlterDropAction implements TableAlterRequest.AlterRequestAction {

    /**
     * The type of Action.
     */
    private int dropType = Integer.MIN_VALUE;
    /**
     * The named attribute. Only required if {@linkplain dropType} is set to INDEX_DROP_TYPE or COLUMN_DROP_TYPE
     */
    private String dropObjectName;

    @Override
    public StringBuilder generateSQL() {
        Checks.checkIfIntMinValue(dropType, "dropType");
        if (dropType == PRIMARY_KEY_DROP_TYPE) {
            return new StringBuilder("DROP PRIMARY KEY");
        }
        Checks.checkIfNullOrEmptyMap(dropObjectName, "dropObjectName");
        if (dropType != 1 && dropType != 2) {
            throw new IllegalArgumentException("Drop type not found.");
        }
        return new StringBuilder("DROP ").append((dropType == COLUMN_DROP_TYPE ? "COLUMN " : "INDEX ")).append(dropObjectName);
    }

    /**
     * Action will drop the named column.
     */
    public static final int COLUMN_DROP_TYPE = 1;
    /**
     * Action will drop the column with the named index.
     */
    public static final int INDEX_DROP_TYPE = 2;
    /**
     * Action will drop the primary key attribute and its provided features.
     */
    public static final int PRIMARY_KEY_DROP_TYPE = 3;


//    ALTER TABLE tabellenname DROP COLUMN spaltenname;
//    ALTER TABLE tabellenname DROP INDEX indexname;
//    ALTER TABLE tabellenname DROP PRIMARY KEY;


}
