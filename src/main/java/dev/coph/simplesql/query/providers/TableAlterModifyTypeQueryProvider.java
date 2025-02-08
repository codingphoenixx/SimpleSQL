package dev.coph.simplesql.query.providers;

import dev.coph.simplesql.database.attributes.DataType;
import dev.coph.simplesql.query.Query;
import dev.coph.simpleutilities.check.Check;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * TableAlterModifyTypeQueryProvider is a specific implementation of the TableAlterQueryProvider
 * for generating SQL "ALTER TABLE" queries to modify the data type of an existing column in a table.
 * It provides the logic to construct the "MODIFY COLUMN" clause with the new data type and its
 * parameters when required by the specified DataType.
 *
 * The class supports fluent and chainable setter methods, allowing configuration of its properties
 * in a streamlined manner.
 *
 * Key properties include:
 * - dataType: Specifies the new data type for the column.
 * - dataTypeParameter: Optional parameter required for certain DataType values.
 * - columnName: The name of the target column to be modified.
 *
 * This class utilizes the DataType class to define and validate the structure of the data type.
 *
 * The getAlterTableString method implements the specific logic for generating the "MODIFY COLUMN"
 * SQL clause and ensures all required properties are valid and non-null before constructing the query.
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
