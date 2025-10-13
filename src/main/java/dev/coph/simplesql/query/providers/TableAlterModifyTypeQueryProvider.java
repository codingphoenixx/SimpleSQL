package dev.coph.simplesql.query.providers;

import dev.coph.simplesql.database.attributes.DataType;
import dev.coph.simplesql.driver.DriverType;
import dev.coph.simplesql.exception.FeatureNotSupportedException;
import dev.coph.simplesql.query.Query;
import dev.coph.simpleutilities.action.RunnableAction;
import dev.coph.simpleutilities.check.Check;

public class TableAlterModifyTypeQueryProvider extends TableAlterQueryProvider {

    private DataType dataType;
    private Object dataTypeParameter;
    private String columnName;
    private RunnableAction<Boolean> actionAfterQuery;

    private String postgresUsingExpression;

    @Override
    public String getAlterTableString(Query query) {
        Check.ifNullOrEmptyMap(dataType, "dataType");
        Check.ifNullOrEmptyMap(columnName, "columnName");

        DriverType driver =
                query.databaseAdapter() != null ? query.databaseAdapter().driverType() : null;

        String typeSql = dataType.toSQL(dataTypeParameter).toString();

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
            default -> throw new FeatureNotSupportedException(driver);
        };
    }

    public DataType dataType() { return this.dataType; }
    public Object dataTypeParameter() { return this.dataTypeParameter; }
    public String columnName() { return this.columnName; }

    public TableAlterModifyTypeQueryProvider dataType(DataType dataType) {
        this.dataType = dataType;
        return this;
    }

    public TableAlterModifyTypeQueryProvider dataTypeParameter(Object dataTypeParameter) {
        this.dataTypeParameter = dataTypeParameter;
        return this;
    }

    public TableAlterModifyTypeQueryProvider columnName(String columnName) {
        this.columnName = columnName;
        return this;
    }

    public TableAlterModifyTypeQueryProvider postgresUsingExpression(String expr) {
        this.postgresUsingExpression = expr;
        return this;
    }

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
