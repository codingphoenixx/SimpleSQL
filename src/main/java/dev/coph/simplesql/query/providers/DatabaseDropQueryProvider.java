package dev.coph.simplesql.query.providers;

import dev.coph.simplesql.database.attributes.DeleteMethode;
import dev.coph.simplesql.driver.DriverCompatibility;
import dev.coph.simplesql.driver.DriverType;
import dev.coph.simplesql.query.Query;
import dev.coph.simplesql.query.QueryProvider;
import dev.coph.simplesql.utils.DatabaseCheck;
import dev.coph.simpleutilities.action.RunnableAction;
import dev.coph.simpleutilities.check.Check;

public class DatabaseDropQueryProvider implements QueryProvider {

    private String database;

    private DeleteMethode deleteMethode = DeleteMethode.DEFAULT;

    private RunnableAction<Boolean> actionAfterQuery;


    @Override
    public DriverCompatibility compatibility() {
        return driverType -> driverType != DriverType.SQLITE;
    }

    @Override
    public String generateSQLString(Query query) {
        Check.ifNull(database, "database name");

        DriverType driver =
                query.databaseAdapter() != null ? query.databaseAdapter().driverType() : null;

        DatabaseCheck.missingDriver(driver);
        DatabaseCheck.unsupportedDriver(driver, DriverType.SQLITE);


        StringBuilder sql = new StringBuilder();

        sql.append("DROP DATABASE ");
        if (deleteMethode == DeleteMethode.IF_EXISTS) {
            sql.append("IF EXISTS ");
        }
        sql.append(database);
        sql.append(";");


        return sql.toString();
    }


    @Override
    public RunnableAction<Boolean> actionAfterQuery() {
        return actionAfterQuery;
    }

    public DatabaseDropQueryProvider actionAfterQuery(RunnableAction<Boolean> actionAfterQuery) {
        this.actionAfterQuery = actionAfterQuery;
        return this;
    }


    public DatabaseDropQueryProvider database(String database) {
        this.database = database;
        return this;
    }

    public DatabaseDropQueryProvider deleteMethode(DeleteMethode deleteMethode) {
        this.deleteMethode = deleteMethode != null ? deleteMethode : DeleteMethode.DEFAULT;
        return this;
    }


    public String database() {
        return this.database;
    }

    public DeleteMethode deleteMethode() {
        return this.deleteMethode;
    }

}
