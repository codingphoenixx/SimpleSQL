package dev.coph.simplesql.query.providers;

import dev.coph.simplesql.database.Column;
import dev.coph.simplesql.database.attributes.*;
import dev.coph.simplesql.database.attributes.tableConstaint.*;
import dev.coph.simplesql.driver.DriverCompatibility;
import dev.coph.simplesql.driver.DriverType;
import dev.coph.simplesql.query.Query;
import dev.coph.simplesql.query.QueryProvider;
import dev.coph.simplesql.utils.DatabaseCheck;
import dev.coph.simpleutilities.action.RunnableAction;
import dev.coph.simpleutilities.check.Check;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;


public class TableCreateQueryProvider implements QueryProvider {

    private final List<Column> columns = new ArrayList<>();
    private final List<TableConstraint> constraints = new ArrayList<>();

    private String table;

    private CreateMethode createMethode = CreateMethode.DEFAULT;

    private RunnableAction<Boolean> actionAfterQuery;

    private boolean temporary;
    private String tableComment;
    private String tableOptions;

    private String engine;


    @Override
    public DriverCompatibility compatibility() {
        return driverType -> true;
    }

    @Override
    public String generateSQLString(Query query) {
        Check.ifNullOrEmptyMap(table, "tablename");
        Check.ifNullOrEmptyMap(columns, "columns");

        DriverType driver = query.databaseAdapter() != null
                ? query.databaseAdapter().driverType()
                : null;

        StringBuilder sql = new StringBuilder("CREATE ");

        if (temporary) {
            DatabaseCheck.requireDriver(driver, DriverType.MYSQL, DriverType.MARIADB, DriverType.SQLITE, DriverType.POSTGRESQL);
            sql.append("TEMPORARY ");
        }

        sql.append("TABLE ");

        if (createMethode == CreateMethode.IF_NOT_EXISTS) {
            sql.append("IF NOT EXISTS ");
        }

        sql.append(table);

        StringJoiner colJoin = new StringJoiner(", ");
        for (Column column : columns) {
            String colSql = column.toString(query);
            Check.ifNullOrEmptyMap(colSql, "column-sql");
            colJoin.add(colSql);
        }

        List<String> implicitPk = new ArrayList<>();
        for (Column c : columns) {
            if (c.columnType() == ColumnType.PRIMARY_KEY
                    || c.columnType() == ColumnType.PRIMARY_KEY_AUTOINCREMENT) {
                implicitPk.add(c.key());
            }
        }

        List<String> renderedConstraints = renderConstraints(query, driver, implicitPk);
        for (String rc : renderedConstraints) {
            if (rc != null && !rc.isBlank()) {
                colJoin.add(rc);
            }
        }

        sql.append(" (").append(colJoin.toString()).append(")");

        appendTableOptions(sql, driver);

        sql.append(";");

        return sql.toString();
    }

    private List<String> renderConstraints(Query query, DriverType driver, List<String> implicitPk) {
        List<String> out = new ArrayList<>();

        if (implicitPk.size() > 1) {
            out.add("PRIMARY KEY (" + String.join(", ", implicitPk) + ")");
        }

        for (TableConstraint tc : constraints) {
            if (tc instanceof PrimaryKeyConstraint pk) {
                List<String> cols = pk.columns();
                out.add(named(tc, "PRIMARY KEY (" + String.join(", ", cols) + ")"));
            } else if (tc instanceof UniqueConstraint uq) {
                out.add(named(tc, "UNIQUE (" + String.join(", ", uq.columns()) + ")"));
            } else if (tc instanceof CheckConstraint ck) {
                out.add(named(tc, "CHECK (" + ck.expression() + ")"));
            } else if (tc instanceof ForeignKeyConstraint fk) {
                StringBuilder sb = new StringBuilder();
                sb.append("FOREIGN KEY (").append(String.join(", ", fk.columns())).append(")")
                        .append(" REFERENCES ").append(fk.refTable())
                        .append(" (").append(String.join(", ", fk.refColumns())).append(")");
                if (fk.onDelete() != null) {
                    sb.append(" ON DELETE ").append(fk.onDelete().sql());
                }
                if (fk.onUpdate() != null) {
                    sb.append(" ON UPDATE ").append(fk.onUpdate().sql());
                }
                out.add(named(tc, sb.toString()));
            } else if (tc instanceof IndexConstraint ix) {
                if (driver == DriverType.MYSQL || driver == DriverType.MARIADB) {
                    String kind = ix.unique() ? "UNIQUE KEY" : "KEY";
                    String name = ix.name() != null ? ix.name() : ("idx_" + table + "_" + String.join("_", ix.columns()));
                    out.add(kind + " " + name + " (" + String.join(", ", ix.columns()) + ")");
                } else {
                    if (actionAfterQuery != null) {
                        String idxName = ix.name() != null ? ix.name() : ("idx_" + table + "_" + String.join("_", ix.columns()));
                        String ddl = "CREATE " + (ix.unique() ? "UNIQUE " : "") + "INDEX IF NOT EXISTS "
                                + idxName + " ON " + table + " (" + String.join(", ", ix.columns()) + ");";
                        RunnableAction<Boolean> prev = actionAfterQuery;
                        actionAfterQuery = (success) -> {
                            prev.run(success);
                            new Query(query.databaseAdapter()).useTransaction(false).executeQuery(new CustomQueryProvider(ddl));
                        };
                    }
                }
            }
        }
        return out;
    }

    private String named(TableConstraint tc, String body) {
        if (tc.name() == null || tc.name().isBlank()) return body;
        return "CONSTRAINT " + tc.name() + " " + body;
    }

    private void appendTableOptions(StringBuilder sql, DriverType driver) {
        if (tableOptions != null && !tableOptions.isBlank()) {
            sql.append(" ").append(tableOptions.trim());
            return;
        }

        StringBuilder opts = new StringBuilder();
        if ((driver == DriverType.MYSQL || driver == DriverType.MARIADB) && engine != null && !engine.isBlank()) {
            if (!opts.isEmpty()) opts.append(" ");
            opts.append("ENGINE=").append(engine.trim());
        }

        if (tableComment != null && !tableComment.isBlank()) {
            DatabaseCheck.requireDriver(driver, DriverType.MYSQL, DriverType.MARIADB);
            if (!opts.isEmpty()) opts.append(" ");
            opts.append("COMMENT='").append(escapeSqlSingleQuote(tableComment)).append("'");
        }

        if (!opts.isEmpty()) {
            sql.append(" ").append(opts);
        }

        if (tableComment != null && !tableComment.isBlank()) {
            DatabaseCheck.requireDriver(driver, DriverType.MYSQL, DriverType.MARIADB);
            sql.append(" COMMENT=").append("'").append(escapeSqlSingleQuote(tableComment)).append("'");
        }
    }

    private String escapeSqlSingleQuote(String s) {
        return s.replace("'", "''");
    }


    public TableCreateQueryProvider table(String table) {
        this.table = table;
        return this;
    }

    public String engine() {
        return engine;
    }

    public void engine(String engine) {
        this.engine = engine;
    }

    public TableCreateQueryProvider createMethode(CreateMethode createMethode) {
        this.createMethode = createMethode;
        return this;
    }

    public TableCreateQueryProvider column(Column column) {
        columns.add(column);
        return this;
    }

    public TableCreateQueryProvider column(String key, DataType dataType) {
        columns.add(new Column(key, dataType));
        return this;
    }

    public TableCreateQueryProvider column(String key, DataType dataType, UnsignedState unsignedState) {
        columns.add(new Column(key, dataType, unsignedState));
        return this;
    }

    public TableCreateQueryProvider column(String key, DataType dataType, Object dataTypeParameterObject) {
        columns.add(new Column(key, dataType, dataTypeParameterObject));
        return this;
    }

    public TableCreateQueryProvider column(String key, DataType dataType, UnsignedState unsignedState, boolean notNull) {
        columns.add(new Column(key, dataType, unsignedState, notNull));
        return this;
    }

    public TableCreateQueryProvider column(String key, DataType dataType, boolean notNull) {
        columns.add(new Column(key, dataType, notNull));
        return this;
    }


    public TableCreateQueryProvider column(
            String key, DataType dataType, Object dataTypeParameterObject, boolean notNull) {
        columns.add(new Column(key, dataType, dataTypeParameterObject, notNull));
        return this;
    }

    public TableCreateQueryProvider column(
            String key, DataType dataType, UnsignedState unsignedState, Object dataTypeParameterObject, ColumnType columnType) {

        if (columnType == ColumnType.PRIMARY_KEY) {
            primaryKey(List.of(key));
            columns.add(new Column(key, dataType, unsignedState, dataTypeParameterObject));
            return this;
        }

        columns.add(new Column(key, dataType, unsignedState, dataTypeParameterObject, columnType));
        return this;
    }

    public TableCreateQueryProvider column(
            String key, DataType dataType, Object dataTypeParameterObject, ColumnType columnType) {

        if (columnType == ColumnType.PRIMARY_KEY) {
            primaryKey(List.of(key));
            columns.add(new Column(key, dataType, dataTypeParameterObject));
            return this;
        }

        columns.add(new Column(key, dataType, dataTypeParameterObject, columnType));
        return this;
    }

    public TableCreateQueryProvider column(
            String key,
            DataType dataType,
            Object dataTypeParameterObject,
            ColumnType columnType,
            boolean notNull) {

        if (columnType == ColumnType.PRIMARY_KEY) {
            primaryKey(List.of(key));
            columns.add(new Column(key, dataType, dataTypeParameterObject, notNull));
            return this;
        }

        columns.add(new Column(key, dataType, dataTypeParameterObject, columnType, notNull));
        return this;
    }

    public TableCreateQueryProvider column(
            String key,
            DataType dataType,
            UnsignedState unsignedState,
            Object dataTypeParameterObject,
            ColumnType columnType,
            boolean notNull) {
        if (columnType == ColumnType.PRIMARY_KEY) {
            primaryKey(List.of(key));
            columns.add(new Column(key, dataType, unsignedState, dataTypeParameterObject).notNull(notNull));
            return this;
        }
        columns.add(new Column(key, dataType, unsignedState, dataTypeParameterObject, columnType, notNull));
        return this;
    }


    public TableCreateQueryProvider column(String key, DataType dataType, ColumnType columnType) {
        if (columnType == ColumnType.PRIMARY_KEY) {
            primaryKey(List.of(key));
            columns.add(new Column(key, dataType));
            return this;
        }
        columns.add(new Column(key, dataType, columnType));
        return this;
    }

    public TableCreateQueryProvider temporary(boolean temporary) {
        this.temporary = temporary;
        return this;
    }

    public TableCreateQueryProvider tableComment(String comment) {
        this.tableComment = comment;
        return this;
    }

    public TableCreateQueryProvider tableOptions(String options) {
        this.tableOptions = options;
        return this;
    }

    public TableCreateQueryProvider actionAfterQuery(RunnableAction<Boolean> actionAfterQuery) {
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

    public CreateMethode createMethode() {
        return this.createMethode;
    }

    public List<Column> columns() {
        return this.columns;
    }

    public TableCreateQueryProvider primaryKey(String name, List<String> columns) {
        constraints.add(new PrimaryKeyConstraint(name, columns));
        return this;
    }

    public TableCreateQueryProvider primaryKey(List<String> columns) {
        return primaryKey(null, columns);
    }

    public TableCreateQueryProvider unique(String name, List<String> columns) {
        constraints.add(new UniqueConstraint(name, columns));
        return this;
    }

    public TableCreateQueryProvider unique(List<String> columns) {
        return unique(null, columns);
    }

    public TableCreateQueryProvider check(String name, String expression) {
        constraints.add(new CheckConstraint(name, expression));
        return this;
    }

    public TableCreateQueryProvider check(String expression) {
        return check(null, expression);
    }

    public TableCreateQueryProvider foreignKey(
            String name, List<String> columns, String refTable, List<String> refColumns,
            ForeignKeyAction onDelete, ForeignKeyAction onUpdate) {
        constraints.add(new ForeignKeyConstraint(name, columns, refTable, refColumns, onDelete, onUpdate));
        return this;
    }

    public TableCreateQueryProvider foreignKey(
            List<String> columns, String refTable, List<String> refColumns) {
        return foreignKey(null, columns, refTable, refColumns, null, null);
    }

    public TableCreateQueryProvider index(String name, List<String> columns, boolean unique) {
        constraints.add(new IndexConstraint(name, columns, unique));
        return this;
    }

    public TableCreateQueryProvider index(List<String> columns) {
        return index(null, columns, false);
    }

    public List<TableConstraint> constraints() {
        return constraints;
    }
}
