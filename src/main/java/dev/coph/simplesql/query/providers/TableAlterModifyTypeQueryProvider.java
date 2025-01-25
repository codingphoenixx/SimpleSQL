package dev.coph.simplesql.query.providers;

import dev.coph.simplesql.database.attributes.DataType;
import dev.coph.simplesql.query.Query;
import dev.coph.simplesql.utils.Check;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Action for the {@link TableAlterQueryProvider} that will modify a Type action.
 */
@Setter
@Getter
@Accessors(fluent = true, chain = true)
public class TableAlterModifyTypeQueryProvider extends TableAlterQueryProvider {

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
    public String getAlterTableString(Query query) {
        Check.ifNullOrEmptyMap(dataType, "dataType");
        Check.ifNullOrEmptyMap(columnName, "columnName");

        return new StringBuilder("MODIFY COLUMN ").append(columnName).append(" ").append(dataType.toSQL(dataTypeParameter)).toString();
    }
}
