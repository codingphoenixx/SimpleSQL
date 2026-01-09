package dev.coph.simplesql.query.providers;

import dev.coph.simplesql.driver.DriverCompatibility;
import dev.coph.simplesql.query.Query;
import dev.coph.simplesql.query.QueryProvider;
import dev.coph.simpleutilities.action.RunnableAction;

/**
 * CustomQueryProvider is an implementation of the QueryProvider interface,
 * providing functionality to generate custom SQL strings and define actions
 * to be performed after executing queries. It allows for a static SQL string
 * provided at instantiation and supports defining post-query actions.
 */
public class CustomQueryProvider implements QueryProvider {

    private final String sql;
    private RunnableAction<Boolean> actionAfterQuery;

    /**
     * Constructs a CustomQueryProvider with the specified SQL string.
     *
     * @param sql the SQL string to be used by this provider
     */
    public CustomQueryProvider(String sql) {
        this.sql = sql;
    }

    @Override
    public DriverCompatibility compatibility() {
        return driverType -> true;
    }

    @Override
    public String generateSQLString(Query query) {
        return sql;
    }

    @Override
    public RunnableAction<Boolean> actionAfterQuery() {
        return actionAfterQuery;
    }

    /**
     * Sets the action to be executed after a query is run.
     *
     * @param actionAfterQuery the {@code RunnableAction<Boolean>} to be executed post-query,
     *                         where the Boolean parameter represents the success or failure of the query
     */
    public void actionAfterQuery(RunnableAction<Boolean> actionAfterQuery) {
        this.actionAfterQuery = actionAfterQuery;
    }
}
