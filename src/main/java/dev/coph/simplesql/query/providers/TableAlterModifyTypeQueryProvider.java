package dev.coph.simplesql.query.providers;

import dev.coph.simplesql.database.attributes.DataType;
import dev.coph.simplesql.query.Query;
import dev.coph.simpleutilities.check.Check;
import lombok.experimental.Accessors;

/**
 * TableAlterModifyTypeQueryProvider is a specific implementation of the TableAlterQueryProvider
 * for generating SQL "ALTER TABLE" queries to modify the data type of an existing column in a table.
 * It provides the logic to construct the "MODIFY COLUMN" clause with the new data type and its
 * parameters when required by the specified DataType.
 * <p>
 * The class supports fluent and chainable setter methods, allowing configuration of its properties
 * in a streamlined manner.
 * <p>
 * Key properties include:
 * - dataType: Specifies the new data type for the column.
 * - dataTypeParameter: Optional parameter required for certain DataType values.
 * - columnName: The name of the target column to be modified.
 * <p>
 * This class utilizes the DataType class to define and validate the structure of the data type.
 * <p>
 * The getAlterTableString method implements the specific logic for generating the "MODIFY COLUMN"
 * SQL clause and ensures all required properties are valid and non-null before constructing the query.
 */
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

    /**
     * Retrieves the data type of the database column that is to be modified.
     *
     * @return the current {@link DataType} representing the new data type of the column.
     */
    public DataType dataType() {
        return this.dataType;
    }

    /**
     * Retrieves the optional parameter associated with the data type of the database column.
     * Some data types may require a specific parameter (e.g., precision, length).
     *
     * @return the current parameter for the data type of the column, or null if no parameter is set.
     */
    public Object dataTypeParameter() {
        return this.dataTypeParameter;
    }

    /**
     * Retrieves the name of the column to be modified in the "ALTER TABLE" SQL query.
     *
     * @return the name of the column as a String.
     */
    public String columnName() {
        return this.columnName;
    }

    /**
     * Sets the data type for the column to be modified in the "ALTER TABLE" SQL query.
     *
     * @param dataType The {@link DataType} specifying the new data type of the column.
     * @return The current instance of {@code TableAlterModifyTypeQueryProvider} for method chaining.
     */
    public TableAlterModifyTypeQueryProvider dataType(DataType dataType) {
        this.dataType = dataType;
        return this;
    }

    /**
     * Sets the parameter associated with the data type of the database column to be modified.
     * Some data types may require specific parameters, such as precision or length.
     *
     * @param dataTypeParameter The parameter for the new data type of the column.
     * @return The current instance of {@code TableAlterModifyTypeQueryProvider} for method chaining.
     */
    public TableAlterModifyTypeQueryProvider dataTypeParameter(Object dataTypeParameter) {
        this.dataTypeParameter = dataTypeParameter;
        return this;
    }

    /**
     * Sets the name of the column to be modified in the "ALTER TABLE" SQL query.
     *
     * @param columnName The name of the column as a String.
     * @return The current instance of {@code TableAlterModifyTypeQueryProvider} for method chaining.
     */
    public TableAlterModifyTypeQueryProvider columnName(String columnName) {
        this.columnName = columnName;
        return this;
    }
}
