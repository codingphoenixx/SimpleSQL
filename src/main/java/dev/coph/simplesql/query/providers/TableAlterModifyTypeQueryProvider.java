package dev.coph.simplesql.query.providers;

import dev.coph.simplesql.database.attributes.DataType;
import dev.coph.simplesql.database.attributes.UnsignedState;
import dev.coph.simplesql.driver.DriverType;
import dev.coph.simplesql.exception.FeatureNotSupportedException;
import dev.coph.simplesql.query.Query;
import dev.coph.simpleutilities.action.RunnableAction;
import dev.coph.simpleutilities.check.Check;

/**
 * A query provider implementation for altering table structures by modifying the type of a column.
 * Provides support for constructing SQL strings for "ALTER TABLE MODIFY COLUMN" operations
 * with additional configurations like data type, unsigned state, and PostgreSQL-specific clauses.
 */
public class TableAlterModifyTypeQueryProvider extends TableAlterQueryProvider {

    private DataType dataType;
    private Object dataTypeParameter;
    private UnsignedState unsigned = UnsignedState.INACTIVE;
    private String columnName;
    private RunnableAction<Boolean> actionAfterQuery;

    private String postgresUsingExpression;

    @Override
    public String getAlterTableString(Query query) {
        Check.ifNullOrEmptyMap(dataType, "dataType");
        Check.ifNullOrEmptyMap(columnName, "columnName");

        DriverType driver =
                query.databaseAdapter() != null ? query.databaseAdapter().driverType() : null;

        String typeSql = dataType.toSQL(dataTypeParameter, unsigned).toString();

        return switch (driver) {
            case MYSQL, MARIADB -> "MODIFY COLUMN " + columnName + " " + typeSql;
            case POSTGRESQL -> {
                StringBuilder sb = new StringBuilder();
                sb.append("ALTER COLUMN ").append(columnName).append(" TYPE ").append(typeSql);
                if (postgresUsingExpression != null && !postgresUsingExpression.isBlank()) {
                    sb.append(" USING ").append(postgresUsingExpression);
                }
                yield sb.toString();
            }
            case SQLITE -> {
                throw new FeatureNotSupportedException(driver);
            }
        };
    }

    /**
     * Retrieves the current data type associated with this query provider.
     *
     * @return the data type of the column being modified by the query
     */
    public DataType dataType() {
        return this.dataType;
    }

    /**
     * Retrieves the parameter value associated with the data type being modified.
     *
     * @return the parameter object related to the column's data type
     */
    public Object dataTypeParameter() {
        return this.dataTypeParameter;
    }

    /**
     * Retrieves the name of the column associated with the query provider.
     *
     * @return the name of the column being modified or referenced
     */
    public String columnName() {
        return this.columnName;
    }

    /**
     * Retrieves the unsigned state associated with the column or query configuration.
     *
     * @return the unsigned state of the column, indicating if it is unsigned
     */
    public UnsignedState unsigned() {
        return unsigned;
    }

    /**
     * Sets the unsigned state for the column or configuration being modified.
     * The unsigned state specifies whether the column is unsigned (e.g., for numeric types).
     *
     * @param unsigned the {@code UnsignedState} representing the unsigned configuration
     *                 of the column, either {@code ACTIVE} or {@code INACTIVE}
     * @return the current instance of {@code TableAlterModifyTypeQueryProvider} for chaining further modifications
     */
    public TableAlterModifyTypeQueryProvider unsigned(UnsignedState unsigned) {
        this.unsigned = unsigned;
        return this;
    }

    /**
     * Sets the data type for the column being modified in the query.
     * This specifies the new data type that the column should have after the query executes.
     *
     * @param dataType the {@code DataType} representing the new data type for the column
     * @return the current instance of {@code TableAlterModifyTypeQueryProvider} for chaining further modifications
     */
    public TableAlterModifyTypeQueryProvider dataType(DataType dataType) {
        this.dataType = dataType;
        return this;
    }

    /**
     * Sets the parameter value for the data type being modified in the query.
     * This parameter provides additional configuration or details for the column's data type.
     *
     * @param dataTypeParameter the parameter object associated with the data type of the column being modified
     * @return the current instance of {@code TableAlterModifyTypeQueryProvider} for chaining further modifications
     */
    public TableAlterModifyTypeQueryProvider dataTypeParameter(Object dataTypeParameter) {
        this.dataTypeParameter = dataTypeParameter;
        return this;
    }

    /**
     * Sets the name of the column to be modified or referenced in the query.
     *
     * @param columnName the name of the column being modified
     * @return the current instance of {@code TableAlterModifyTypeQueryProvider} for chaining further modifications
     */
    public TableAlterModifyTypeQueryProvider columnName(String columnName) {
        this.columnName = columnName;
        return this;
    }

    /**
     * Sets a PostgreSQL-specific "USING" expression to be included in the query.
     * The "USING" clause is typically used in PostgreSQL during type conversions or modifications
     * to specify how existing column values should be transformed to the new type.
     *
     * @param expr the expression to be used in the PostgreSQL "USING" clause
     * @return the current instance of {@code TableAlterModifyTypeQueryProvider} for chaining further modifications
     */
    public TableAlterModifyTypeQueryProvider postgresUsingExpression(String expr) {
        this.postgresUsingExpression = expr;
        return this;
    }

    /**
     * Sets an action to be executed after the query is run.
     * This action will typically take a {@code Boolean} parameter indicating the success or failure of the query.
     *
     * @param actionAfterQuery the {@code RunnableAction<Boolean>} representing the action to execute post-query
     * @return the current instance of {@code TableAlterModifyTypeQueryProvider} for chaining further modifications
     */
    public TableAlterModifyTypeQueryProvider actionAfterQuery(
            dev.coph.simpleutilities.action.RunnableAction<Boolean> actionAfterQuery) {
        this.actionAfterQuery = actionAfterQuery;
        return this;
    }

    @Override
    public RunnableAction<Boolean> actionAfterQuery() {
        return actionAfterQuery;
    }
}
