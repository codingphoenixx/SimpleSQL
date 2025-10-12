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

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class DropIndexQueryProvider implements QueryProvider {

    private final List<String> indexNames = new ArrayList<>();
    private String table;
    private String schema;
    private boolean ifExists;
    private boolean concurrently;
    private DropBehaviour behaviour;

    private RunnableAction<Boolean> actionAfterQuery;

    @Override
    public DriverCompatibility compatibility() {
        return driverType -> true;
    }

    @Override
    public String generateSQLString(Query query) {
        Check.ifNullOrEmptyMap(indexNames, "indexNames");
        DriverType driver = query.databaseAdapter() != null
                ? query.databaseAdapter().driverType()
                : null;

        DatabaseCheck.missingDriver(driver);

        StringBuilder sql = new StringBuilder();

        switch (driver) {
            case MYSQL, MARIADB -> {
                Check.ifNullOrEmptyMap(table, "table (required for DROP INDEX in MySQL/MariaDB)");
                if (indexNames.size() != 1) {
                    throw new IllegalArgumentException("MySQL/MariaDB can drop one index per statement");
                }
                sql.append("DROP INDEX ");
                if (ifExists) {
                    sql.append("IF EXISTS ");
                }
                sql.append(indexNames.get(0)).append(" ON ").append(table).append(";");
            }
            case POSTGRESQL -> {
                sql.append("DROP INDEX ");
                if (concurrently) sql.append("CONCURRENTLY ");
                if (ifExists) sql.append("IF EXISTS ");

                StringJoiner joiner = new StringJoiner(", ");
                for (String idx : indexNames) {
                    String name = (schema != null && !schema.isBlank())
                            ? schema + "." + idx
                            : idx;
                    joiner.add(name);
                }
                sql.append(joiner);

                if (behaviour != null) {
                    sql.append(behaviour.name());
                }
                sql.append(";");
            }
            case SQLITE -> {
                if (indexNames.size() != 1) {
                    throw new IllegalArgumentException("SQLite can drop one index per statement");
                }
                sql.append("DROP INDEX ");
                if (ifExists) sql.append("IF EXISTS ");
                sql.append(indexNames.get(0)).append(";");
            }
            default -> throw new FeatureNotSupportedException(driver);
        }

        return sql.toString();
    }


    @Override
    public RunnableAction<Boolean> actionAfterQuery() {
        return actionAfterQuery;
    }

    public DropIndexQueryProvider actionAfterQuery(RunnableAction<Boolean> actionAfterQuery) {
        this.actionAfterQuery = actionAfterQuery;
        return this;
    }


    public DropIndexQueryProvider addIndex(String indexName) {
        this.indexNames.add(indexName);
        return this;
    }

    public DropIndexQueryProvider addIndexes(List<String> indexNames) {
        this.indexNames.addAll(indexNames);
        return this;
    }

    public DropIndexQueryProvider table(String table) {
        this.table = table;
        return this;
    }

    public DropIndexQueryProvider schema(String schema) {
        this.schema = schema;
        return this;
    }

    public DropIndexQueryProvider ifExists(boolean ifExists) {
        this.ifExists = ifExists;
        return this;
    }

    public DropIndexQueryProvider concurrently(boolean concurrently) {
        this.concurrently = concurrently;
        return this;
    }

    public DropIndexQueryProvider cascade(DropBehaviour behaviour) {
        this.behaviour = behaviour;
        return this;
    }


}
