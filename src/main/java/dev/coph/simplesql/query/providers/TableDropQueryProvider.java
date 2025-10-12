package dev.coph.simplesql.query.providers;

import dev.coph.simplesql.database.attributes.DeleteMethode;
import dev.coph.simplesql.database.attributes.DropBehaviour;
import dev.coph.simplesql.driver.DriverCompatibility;
import dev.coph.simplesql.driver.DriverType;
import dev.coph.simplesql.query.Query;
import dev.coph.simplesql.query.QueryProvider;
import dev.coph.simplesql.utils.DatabaseCheck;
import dev.coph.simpleutilities.action.RunnableAction;
import dev.coph.simpleutilities.check.Check;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringJoiner;


public class TableDropQueryProvider implements QueryProvider {

    private ArrayList<String> tables;

    private DeleteMethode deleteMethode = DeleteMethode.DEFAULT;

    private RunnableAction<Boolean> actionAfterQuery;

    private boolean temporary;
    private DropBehaviour behaviour = DropBehaviour.NONE;

    @Override
    public DriverCompatibility compatibility() {
        return driverType -> true;
    }

    @Override
    public String generateSQLString(Query query) {
        Check.ifNullOrEmptyMap(tables, "tables");

        DriverType driver = query.databaseAdapter() != null
                ? query.databaseAdapter().driverType()
                : null;

        StringBuilder sql = new StringBuilder("DROP ");

        if (temporary) {
            DatabaseCheck.requireDriver(driver, DriverType.MYSQL, DriverType.MARIADB, DriverType.SQLITE);
            sql.append("TEMPORARY ");
        }

        sql.append("TABLE");

        if (deleteMethode == DeleteMethode.IF_EXISTS) {
            sql.append(" IF EXISTS");
        }

        StringJoiner tj = new StringJoiner(", ", " ", "");
        for (String t : tables) {
            Check.ifNull(t, "table");
            tj.add(t);
        }
        sql.append(tj);

        if (behaviour != DropBehaviour.NONE) {
            DatabaseCheck.requireDriver(driver, DriverType.POSTGRESQL);
            sql.append(behaviour.name());
        }

        sql.append(";");
        return sql.toString();
    }



    public TableDropQueryProvider table(String... tables) {
        if (this.tables == null) this.tables = new ArrayList<>();
        this.tables.addAll(Arrays.asList(tables));
        return this;
    }

    public TableDropQueryProvider tables(ArrayList<String> tables) {
        this.tables = tables;
        return this;
    }

    public TableDropQueryProvider deleteMethode(DeleteMethode deleteMethode) {
        this.deleteMethode = deleteMethode != null ? deleteMethode : DeleteMethode.DEFAULT;
        return this;
    }

    public TableDropQueryProvider temporary(boolean temporary) {
        this.temporary = temporary;
        return this;
    }

    public TableDropQueryProvider dropBehaviour(DropBehaviour behaviour) {
        this.behaviour = behaviour;
        return this;
    }


    public TableDropQueryProvider actionAfterQuery(RunnableAction<Boolean> actionAfterQuery) {
        this.actionAfterQuery = actionAfterQuery;
        return this;
    }

    @Override
    public RunnableAction<Boolean> actionAfterQuery() {
        return actionAfterQuery;
    }


    public ArrayList<String> tables() {
        return this.tables;
    }

    public DeleteMethode deleteMethode() {
        return this.deleteMethode;
    }
}
