package dev.coph.simplesql.query.providers;

import dev.coph.simplesql.database.attributes.InsertMethode;
import dev.coph.simplesql.driver.DriverCompatibility;
import dev.coph.simplesql.driver.DriverType;
import dev.coph.simplesql.query.Query;
import dev.coph.simplesql.query.QueryEntry;
import dev.coph.simplesql.query.QueryProvider;
import dev.coph.simpleutilities.action.RunnableAction;
import dev.coph.simpleutilities.check.Check;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;


public class InsertQueryProvider implements QueryProvider {


    private String table;

    private List<QueryEntry> entries;

    private RunnableAction<Boolean> actionAfterQuery;

    private InsertMethode insertMethode = InsertMethode.INSERT;
    private List<String> conflictColumns;
    private List<Object> boundParams = List.of();

    public InsertQueryProvider entry(String column, Object value) {
        if (entries == null) {
            entries = new ArrayList<>();
        }
        entries.add(new QueryEntry(column, value));
        return this;
    }

    public InsertQueryProvider actionAfterQuery(RunnableAction<Boolean> actionAfterQuery) {
        this.actionAfterQuery = actionAfterQuery;
        return this;
    }

    @Override
    public DriverCompatibility compatibility() {
        return driverType -> true;
    }

    @Override
    public String generateSQLString(Query query) {
        Check.ifNullOrEmptyMap(entries, "entries");
        Check.ifNullOrEmptyMap(table, "tablename");

        List<Object> params = new ArrayList<>();
        DriverType driver = query.databaseAdapter() != null ? query.databaseAdapter().driverType() : null;

        StringBuilder sql = new StringBuilder();

        sql.append("INSERT ");

        if (insertMethode.equals(InsertMethode.INSERT_IGNORE)) {
            if (driver == DriverType.MYSQL || driver == DriverType.MARIADB) {
                sql.append("IGNORE ");
            } else if (driver == DriverType.SQLITE) {
                sql.append("OR IGNORE ");
            }
        }


        sql.append("INTO ").append(table);

        StringJoiner colJoin = new StringJoiner(", ");
        for (QueryEntry e : entries) {
            colJoin.add(e.columName());
        }
        sql.append(" (").append(colJoin).append(") ");

        StringJoiner phJoin = new StringJoiner(", ");
        for (QueryEntry e : entries) {
            phJoin.add("?");
            params.add(e.value());
        }
        sql.append("VALUES (").append(phJoin).append(")");


        if (insertMethode == InsertMethode.INSERT_OR_UPDATE) {
            if (driver == DriverType.MYSQL || driver == DriverType.MARIADB) {
                sql.append(" ON DUPLICATE KEY UPDATE ");
                StringJoiner updJoin = new StringJoiner(", ");
                for (QueryEntry e : entries) {
                    updJoin.add(e.columName() + " = VALUES(" + e.columName() + ")");
                }
                sql.append(updJoin);
            } else if (driver == DriverType.POSTGRESQL || driver == DriverType.SQLITE) {
                Check.ifNullOrEmptyMap(conflictColumns, "conflictColumns");
                sql.append(" ON CONFLICT (");
                sql.append(String.join(", ", conflictColumns));
                sql.append(") DO UPDATE SET ");
                StringJoiner updJoin = new StringJoiner(", ");
                for (QueryEntry e : entries) {
                    String excluded = (driver == DriverType.POSTGRESQL) ? "EXCLUDED" : "excluded";
                    updJoin.add(e.columName() + " = " + excluded + "." + e.columName());
                }
                sql.append(updJoin);
            }
        } else if (insertMethode == InsertMethode.INSERT_IGNORE) {
            if (driver == DriverType.POSTGRESQL && conflictColumns != null && !conflictColumns.isEmpty()) {
                sql.append(" ON CONFLICT (").append(String.join(", ", conflictColumns)).append(") DO NOTHING");
            }
        }

        sql.append(";");

       this.boundParams = List.copyOf(params);
        return sql.toString();
    }

    @Override
    public List<Object> parameters() {
        return boundParams != null ? boundParams : List.of();
    }

    @Override
    public RunnableAction<Boolean> actionAfterQuery() {
        return actionAfterQuery;
    }

    public InsertQueryProvider conflictColumns(List<String> cols) {
        if (cols == null || cols.isEmpty()) {
            this.conflictColumns = null;
        } else {
            this.conflictColumns = new ArrayList<>(cols);
        }
        return this;
    }


    public String table() {
        return this.table;
    }


    public List<QueryEntry> entries() {
        return this.entries;
    }


    public InsertMethode insertMethode() {
        return this.insertMethode;
    }


    public InsertQueryProvider table(String table) {
        this.table = table;
        return this;
    }


    public InsertQueryProvider insertMethode(InsertMethode insertMethode) {
        this.insertMethode = insertMethode;
        return this;
    }
}
