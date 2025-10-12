package dev.coph.simplesql.query.providers;

import dev.coph.simplesql.database.attributes.DropBehaviour;
import dev.coph.simplesql.driver.DriverCompatibility;
import dev.coph.simplesql.driver.DriverType;
import dev.coph.simplesql.exception.FeatureNotSupportedException;
import dev.coph.simplesql.query.Query;
import dev.coph.simplesql.query.QueryProvider;
import dev.coph.simplesql.utils.DatabaseCheck;
import dev.coph.simpleutilities.action.RunnableAction;
import dev.coph.simpleutilities.check.Check;

public class TruncateQueryProvider implements QueryProvider {

    private String table;
    private RunnableAction<Boolean> actionAfterQuery;
    private IdentityMode identityMode;
    private DropBehaviour behaviour = DropBehaviour.NONE;

    @Override
    public DriverCompatibility compatibility() {
        return driverType -> driverType != DriverType.SQLITE;
    }

    @Override
    public String generateSQLString(Query query) {
        Check.ifNull(table, "table name");

        DriverType driver = query.databaseAdapter() != null
                ? query.databaseAdapter().driverType()
                : null;

        DatabaseCheck.missingDriver(driver);
        DatabaseCheck.unsupportedDriver(driver, DriverType.SQLITE);

        StringBuilder sql = new StringBuilder();

        switch (driver) {
            case MYSQL, MARIADB -> {
                if (identityMode != null || behaviour != DropBehaviour.NONE) {
                    throw new FeatureNotSupportedException(driver);
                }
                sql.append("TRUNCATE TABLE ").append(table).append(";");
            }
            case POSTGRESQL -> {
                sql.append("TRUNCATE TABLE ").append(table);
                if (identityMode == IdentityMode.RESTART) {
                    sql.append(" RESTART IDENTITY");
                } else if (identityMode == IdentityMode.CONTINUE) {
                    sql.append(" CONTINUE IDENTITY");
                }
                if (behaviour != null) {
                    sql.append(behaviour.name());
                }
                sql.append(";");
            }
            default -> {
                throw new FeatureNotSupportedException(driver);
            }
        }

        return sql.toString();
    }

    public TruncateQueryProvider table(String table) {
        this.table = table;
        return this;
    }

    public TruncateQueryProvider identityMode(IdentityMode mode) {
        this.identityMode = mode;
        return this;
    }

    public TruncateQueryProvider dropBehaviour(DropBehaviour behaviour) {
        this.behaviour = behaviour;
        return this;
    }

    public TruncateQueryProvider actionAfterQuery(RunnableAction<Boolean> actionAfterQuery) {
        this.actionAfterQuery = actionAfterQuery;
        return this;
    }

    @Override
    public RunnableAction<Boolean> actionAfterQuery() {
        return actionAfterQuery;
    }

    public String table() {
        return this.table;
    }

    public IdentityMode identityMode() {
        return this.identityMode;
    }

    public DropBehaviour behaviour() {
        return this.behaviour;
    }

    public enum IdentityMode {RESTART, CONTINUE}

}
