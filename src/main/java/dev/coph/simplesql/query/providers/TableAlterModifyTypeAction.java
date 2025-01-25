package dev.coph.simplesql.query.providers;

import de.codingphoenix.phoenixbase.check.Checks;
import de.codingphoenix.phoenixbase.database.DataType;
import de.codingphoenix.phoenixbase.database.request.tablealter.TableAlterRequest;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Action for the {@link TableAlterRequest} that will modify a Type action.
 */
@Setter
@Getter
@Accessors(fluent = true)
public class TableAlterModifyTypeAction extends TableAlterQueryProvider {
    /**
     * The datatype of the database column.
     */
    private DataType dataType;

    /**
     * The parameter with is required for some {@link DataType}.
     */
    private Object dataTypeParameter;
    /**
     * The name of the column with will be modified.
     */
    private String columnName;

    @Override
    public StringBuilder generateSQL() {
        Checks.checkIfNullOrEmptyMap(dataType, "dataType");
        Checks.checkIfNullOrEmptyMap(columnName, "columnName");

        return new StringBuilder("MODIFY COLUMN ").append(columnName).append(" ").append(dataType.toSQL(dataTypeParameter));
    }

    //    ALTER TABLE tabellenname MODIFY COLUMN spaltenname NEUER_DATENTYP;

}
