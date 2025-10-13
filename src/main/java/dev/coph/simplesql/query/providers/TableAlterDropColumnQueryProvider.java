package dev.coph.simplesql.query.providers;

import dev.coph.simplesql.driver.DriverType;
import dev.coph.simplesql.exception.FeatureNotSupportedException;
import dev.coph.simplesql.query.Query;
import dev.coph.simplesql.utils.DatabaseCheck;
import dev.coph.simpleutilities.action.RunnableAction;
import dev.coph.simpleutilities.check.Check;

public class TableAlterDropColumnQueryProvider extends TableAlterQueryProvider {

    private DropType dropType;
    private String dropObjectName;
    private String constraintName;
    private RunnableAction<Boolean> actionAfterQuery;

    @Override
    public String getAlterTableString(Query query) {
        Check.ifNull(dropType, "dropType");

        DriverType driver =
                query.databaseAdapter() != null ? query.databaseAdapter().driverType() : null;

        DatabaseCheck.missingDriver(driver);
        DatabaseCheck.unsupportedDriver(driver, DriverType.SQLITE);

        return switch (dropType) {
            case PRIMARY_KEY -> dropPrimaryKey(query, driver);
            case COLUMN -> dropColumn(query, driver);
        };
    }

    private String dropPrimaryKey(Query query, DriverType driver) {
        return switch (driver) {
            case MYSQL, MARIADB -> "DROP PRIMARY KEY";
            case POSTGRESQL -> {
                if (constraintName == null || constraintName.isBlank()) {
                    throw new FeatureNotSupportedException(driver);
                }
                yield "DROP CONSTRAINT " + constraintName;
            }
            default -> throw new FeatureNotSupportedException(driver);
        };
    }

    private String dropColumn(Query query, DriverType driver) {
        Check.ifNullOrEmptyMap(dropObjectName, "dropObjectName");
        return switch (driver) {
            case MYSQL, MARIADB, POSTGRESQL -> "DROP COLUMN " + dropObjectName;
            default -> throw new FeatureNotSupportedException(driver);
        };
    }

    public DropType dropType() {
        return this.dropType;
    }

    public String dropObjectName() {
        return this.dropObjectName;
    }

    public String constraintName() {
        return this.constraintName;
    }

    public TableAlterDropColumnQueryProvider dropType(DropType dropType) {
        this.dropType = dropType;
        return this;
    }

    public TableAlterDropColumnQueryProvider dropObjectName(String dropObjectName) {
        this.dropObjectName = dropObjectName;
        return this;
    }

    public TableAlterDropColumnQueryProvider constraintName(String constraintName) {
        this.constraintName = constraintName;
        return this;
    }

    public TableAlterDropColumnQueryProvider actionAfterQuery(RunnableAction<Boolean> actionAfterQuery) {
        this.actionAfterQuery = actionAfterQuery;
        return this;
    }

    @Override
    public RunnableAction<Boolean> actionAfterQuery() {
        return actionAfterQuery;
    }

    public enum DropType {COLUMN, PRIMARY_KEY}
}
