package dev.coph.simplesql.query.providers;

import dev.coph.simplesql.database.Column;
import dev.coph.simplesql.database.attributes.ColumnPosition;
import dev.coph.simplesql.database.attributes.ColumnType;
import dev.coph.simplesql.database.attributes.CreateMethode;
import dev.coph.simplesql.database.attributes.DataType;
import dev.coph.simplesql.driver.DriverType;
import dev.coph.simplesql.exception.FeatureNotSupportedException;
import dev.coph.simplesql.query.Query;
import dev.coph.simplesql.utils.DatabaseCheck;
import dev.coph.simpleutilities.action.RunnableAction;
import dev.coph.simpleutilities.check.Check;

public class TableAlterAddColumnQueryProvider extends TableAlterQueryProvider {

    private ColumnPosition postion = ColumnPosition.DEFAULT;
    private String afterColumnName;

    private CreateMethode createMethode = CreateMethode.DEFAULT;
    private Column column;

    private RunnableAction<Boolean> actionAfterQuery;

    @Override
    public String getAlterTableString(Query query) {
        Check.ifNull(column, "column");

        DriverType driver =
                query.databaseAdapter() != null ? query.databaseAdapter().driverType() : null;

        DatabaseCheck.missingDriver(driver);

        boolean ifNotExists = (createMethode == CreateMethode.IF_NOT_EXISTS);

        StringBuilder sb = new StringBuilder("ADD COLUMN ");

        if (ifNotExists) {
            switch (driver) {
                case MYSQL, MARIADB, POSTGRESQL -> sb.append("IF NOT EXISTS ");
                default -> throw new FeatureNotSupportedException(driver);
            }
        }

        sb.append(column.toString(query));

        switch (postion) {
            case DEFAULT -> {
            }
            case FIRST -> {
                switch (driver) {
                    case MYSQL, MARIADB -> sb.append(" FIRST");
                    default -> throw new FeatureNotSupportedException(driver);
                }
            }
            case AFTER -> {
                Check.ifNullOrEmptyMap(afterColumnName, "afterColumnName");
                switch (driver) {
                    case MYSQL, MARIADB -> sb.append(" AFTER ").append(afterColumnName);
                    default -> throw new FeatureNotSupportedException(driver);
                }
            }
        }

        return sb.toString();
    }

    public TableAlterAddColumnQueryProvider column(Column column) {
        this.column = column;
        return this;
    }

    public TableAlterAddColumnQueryProvider column(String key, DataType dataType) {
        this.column = new Column(key, dataType);
        return this;
    }

    public TableAlterAddColumnQueryProvider column(String key, DataType dataType, Object dataTypeParameterObject) {
        this.column = new Column(key, dataType, dataTypeParameterObject);
        return this;
    }

    public TableAlterAddColumnQueryProvider column(String key, DataType dataType, boolean notNull) {
        this.column = new Column(key, dataType, notNull);
        return this;
    }

    public TableAlterAddColumnQueryProvider column(
            String key, DataType dataType, Object dataTypeParameterObject, boolean notNull) {
        this.column = new Column(key, dataType, dataTypeParameterObject, notNull);
        return this;
    }

    public TableAlterAddColumnQueryProvider column(
            String key, DataType dataType, Object dataTypeParameterObject, ColumnType columnType) {
        this.column = new Column(key, dataType, dataTypeParameterObject, columnType);
        return this;
    }

    public TableAlterAddColumnQueryProvider column(
            String key,
            DataType dataType,
            Object dataTypeParameterObject,
            ColumnType columnType,
            boolean notNull) {
        this.column = new Column(key, dataType, dataTypeParameterObject, columnType, notNull);
        return this;
    }

    public TableAlterAddColumnQueryProvider column(String key, DataType dataType, ColumnType columnType) {
        this.column = new Column(key, dataType, columnType);
        return this;
    }

    public ColumnPosition postion() {
        return this.postion;
    }

    public String afterColumnName() {
        return this.afterColumnName;
    }

    public CreateMethode createMethode() {
        return this.createMethode;
    }

    public Column column() {
        return this.column;
    }


    public TableAlterAddColumnQueryProvider postion(ColumnPosition postion) {
        this.postion = postion;
        return this;
    }

    public TableAlterAddColumnQueryProvider afterColumnName(String afterColumnName) {
        this.afterColumnName = afterColumnName;
        return this;
    }

    public TableAlterAddColumnQueryProvider createMethode(CreateMethode createMethode) {
        this.createMethode = createMethode;
        return this;
    }

    public TableAlterAddColumnQueryProvider actionAfterQuery(RunnableAction<Boolean> actionAfterQuery) {
        this.actionAfterQuery = actionAfterQuery;
        return this;
    }

    @Override
    public RunnableAction<Boolean> actionAfterQuery() {
        return actionAfterQuery;
    }
}
