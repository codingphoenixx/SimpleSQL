package dev.coph.simplesql.query.providers;

import dev.coph.simplesql.database.Column;
import dev.coph.simplesql.database.attributes.ColumnType;
import dev.coph.simplesql.database.attributes.CreateMethode;
import dev.coph.simplesql.database.attributes.DataType;
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
            colJoin.add(column.toString(query));
        }
        sql.append(" (").append(colJoin).append(")");

        appendTableOptions(sql, driver);

        sql.append(";");

        return sql.toString();
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

    public TableCreateQueryProvider column(String key, DataType dataType, Object dataTypeParameterObject) {
        columns.add(new Column(key, dataType, dataTypeParameterObject));
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
            String key, DataType dataType, Object dataTypeParameterObject, ColumnType columnType) {
        columns.add(new Column(key, dataType, dataTypeParameterObject, columnType));
        return this;
    }

    public TableCreateQueryProvider column(
            String key,
            DataType dataType,
            Object dataTypeParameterObject,
            ColumnType columnType,
            boolean notNull) {
        columns.add(new Column(key, dataType, dataTypeParameterObject, columnType, notNull));
        return this;
    }

    public TableCreateQueryProvider column(String key, DataType dataType, ColumnType columnType) {
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
}
