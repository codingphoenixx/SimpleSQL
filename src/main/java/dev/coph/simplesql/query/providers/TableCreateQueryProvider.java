package dev.coph.simplesql.query.providers;

import dev.coph.simplesql.database.Column;
import dev.coph.simplesql.database.attributes.*;
import dev.coph.simplesql.database.attributes.tableConstaint.*;
import dev.coph.simplesql.driver.DriverCompatibility;
import dev.coph.simplesql.driver.DriverType;
import dev.coph.simplesql.query.Query;
import dev.coph.simplesql.query.QueryProvider;
import dev.coph.simplesql.utils.DatabaseCheck;
import dev.coph.simplesql.utils.QueryResult;
import dev.coph.simpleutilities.action.RunnableAction;
import dev.coph.simpleutilities.check.Check;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

/**
 * The TableCreateQueryProvider class is responsible for constructing and generating SQL queries
 * for creating database tables, including specifying columns, constraints, and additional table options.
 * It provides a fluent interface for customizing various aspects of the table creation process.
 */
public class TableCreateQueryProvider implements QueryProvider {

    private final List<Column> columns = new ArrayList<>();
    private final List<TableConstraint> constraints = new ArrayList<>();

    private String table;

    private CreateMethode createMethode = CreateMethode.DEFAULT;

    private RunnableAction<QueryResult<TableCreateQueryProvider>> actionAfterQuery;

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

        sql.append(" (").append(colJoin).append(")");

        appendTableOptions(sql, driver);

        sql.append(";");

        return sql.toString();
    }

    /**
     * Renders a list of SQL constraints based on the provided query, driver type, and implicit primary keys.
     *
     * @param query the {@link Query} object containing the context and data for the SQL generation
     * @param driver the {@link DriverType} indicating the SQL dialect to be used
     * @param implicitPk a list of column names that form an implicit primary key
     * @return a list of strings representing the rendered SQL constraints
     */
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
                        RunnableAction<QueryResult<TableCreateQueryProvider>> prev = actionAfterQuery;
                        actionAfterQuery = (queryResult) -> {
                            prev.run(queryResult);
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

    /**
     * Escapes single quotes in a given string to prevent SQL injection or syntax errors.
     * Replaces each single quote character with two single quotes.
     *
     * @param s the input string that may contain single quotes
     * @return a new string with single quotes properly escaped
     */
    private String escapeSqlSingleQuote(String s) {
        return s.replace("'", "''");
    }

    /**
     * Sets the name of the table for the query.
     *
     * @param table the name of the table
     * @return the current instance of TableCreateQueryProvider
     */
    public TableCreateQueryProvider table(String table) {
        this.table = table;
        return this;
    }

    /**
     * Retrieves the engine name or type.
     *
     * @return the engine name or type as a String.
     */
    public String engine() {
        return engine;
    }

    /**
     * Sets the engine type for the object.
     *
     * @param engine the engine type to set
     */
    public void engine(String engine) {
        this.engine = engine;
    }

    /**
     * Sets the CreateMethode instance and returns the current instance of TableCreateQueryProvider.
     *
     * @param createMethode the CreateMethode instance to be set
     * @return the current instance of TableCreateQueryProvider
     */
    public TableCreateQueryProvider createMethode(CreateMethode createMethode) {
        this.createMethode = createMethode;
        return this;
    }

    /**
     * Adds a column to the table creation query.
     *
     * @param column the column to be added to the query
     * @return this TableCreateQueryProvider instance for method chaining
     */
    public TableCreateQueryProvider column(Column column) {
        columns.add(column);
        return this;
    }

    /**
     * Adds a column definition to the table creation query.
     *
     * @param key      the name of the column
     * @param dataType the data type of the column
     * @return the current instance of TableCreateQueryProvider for method chaining
     */
    public TableCreateQueryProvider column(String key, DataType dataType) {
        columns.add(new Column(key, dataType));
        return this;
    }

    /**
     * Adds a column definition to the table creation query.
     *
     * @param key           the name of the column
     * @param dataType      the data type of the column
     * @param unsignedState the unsigned state of the column
     * @return the current instance of TableCreateQueryProvider for method chaining
     */
    public TableCreateQueryProvider column(String key, DataType dataType, UnsignedState unsignedState) {
        columns.add(new Column(key, dataType, unsignedState));
        return this;
    }

    /**
     * Adds a new column with the specified key, data type, and data type parameter to the table schema.
     *
     * @param key                     the name or identifier of the column to be added
     * @param dataType                the type of data the column will store
     * @param dataTypeParameterObject additional parameters or constraints for the data type
     * @return the current instance of TableCreateQueryProvider for method chaining
     */
    public TableCreateQueryProvider column(String key, DataType dataType, Object dataTypeParameterObject) {
        columns.add(new Column(key, dataType, dataTypeParameterObject));
        return this;
    }

    /**
     * Adds a column definition to the table creation query.
     *
     * @param key           the name of the column
     * @param dataType      the data type of the column
     * @param unsignedState the unsigned state of the column
     * @param notNull       whether the column should have a NOT NULL constraint
     * @return the current instance of TableCreateQueryProvider
     */
    public TableCreateQueryProvider column(String key, DataType dataType, UnsignedState unsignedState, boolean notNull) {
        columns.add(new Column(key, dataType, unsignedState, notNull));
        return this;
    }

    /**
     * Adds a column definition to the table creation query.
     *
     * @param key      the name of the column to be added
     * @param dataType the data type of the column
     * @param notNull  whether the column should be non-nullable
     * @return the current TableCreateQueryProvider instance for method chaining
     */
    public TableCreateQueryProvider column(String key, DataType dataType, boolean notNull) {
        columns.add(new Column(key, dataType, notNull));
        return this;
    }

    /**
     * Adds a column definition to the table creation query.
     *
     * @param key                     the name of the column to be added
     * @param dataType                the data type of the column
     * @param dataTypeParameterObject additional parameters for the data type, if any
     * @param notNull                 specifies whether the column should be marked as NOT NULL
     * @return the current instance of TableCreateQueryProvider for method chaining
     */
    public TableCreateQueryProvider column(
            String key, DataType dataType, Object dataTypeParameterObject, boolean notNull) {
        columns.add(new Column(key, dataType, dataTypeParameterObject, notNull));
        return this;
    }

    /**
     * Adds a column to the table creation query.
     *
     * @param key                     the name of the column
     * @param dataType                the data type of the column
     * @param unsignedState           specifies if the column is unsigned
     * @param dataTypeParameterObject additional parameters related to the data type
     * @param columnType              the type of the column (e.g., PRIMARY_KEY, NORMAL)
     * @return the updated TableCreateQueryProvider instance
     */
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

    /**
     * Adds a column to the table schema being defined.
     *
     * @param key the name of the column to be added.
     * @param dataType the data type of the column.
     * @param dataTypeParameterObject an additional parameter object for configuring the column's data type.
     * @param columnType the type of the column such as PRIMARY_KEY or a regular column.
     * @return the current instance of TableCreateQueryProvider to allow method chaining.
     */
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

    /**
     * Adds a column definition to the table creation query.
     *
     * @param key                     the name of the column
     * @param dataType                the data type of the column
     * @param dataTypeParameterObject additional parameters related to the data type, if applicable
     * @param columnType              the type of the column (e.g., PRIMARY_KEY, REGULAR)
     * @param notNull                 indicates whether the column should have a NOT NULL constraint
     * @return the current instance of TableCreateQueryProvider for method chaining
     */
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

    /**
     * Adds a column definition to the table creation query.
     *
     * @param key The name of the column.
     * @param dataType The data type of the column.
     * @param unsignedState Indicates whether the column is unsigned (for numerical types).
     * @param dataTypeParameterObject Additional parameters for the column's data type (if applicable).
     * @param columnType The type of the column (e.g., PRIMARY_KEY, FOREIGN_KEY, etc.).
     * @param notNull Specifies whether the column should be marked as NOT NULL.
     * @return The updated {@code TableCreateQueryProvider} instance for method chaining.
     */
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

    /**
     * Adds a column definition to the table creation query.
     *
     * @param key        the name of the column to be added
     * @param dataType   the data type of the column
     * @param columnType the type of the column (e.g., PRIMARY_KEY, NORMAL)
     * @return the current instance of TableCreateQueryProvider for method chaining
     */
    public TableCreateQueryProvider column(String key, DataType dataType, ColumnType columnType) {
        if (columnType == ColumnType.PRIMARY_KEY) {
            primaryKey(List.of(key));
            columns.add(new Column(key, dataType));
            return this;
        }
        columns.add(new Column(key, dataType, columnType));
        return this;
    }

    /**
     * Sets whether the table to be created is temporary.
     *
     * @param temporary a boolean value indicating if the table should be temporary
     * @return the current instance of {@code TableCreateQueryProvider} for method chaining
     */
    public TableCreateQueryProvider temporary(boolean temporary) {
        this.temporary = temporary;
        return this;
    }

    /**
     * Sets the comment for the table and returns the current instance of the {@code TableCreateQueryProvider}.
     *
     * @param comment the comment to set for the table
     * @return the current instance of {@code TableCreateQueryProvider} for method chaining
     */
    public TableCreateQueryProvider tableComment(String comment) {
        this.tableComment = comment;
        return this;
    }

    /**
     * Sets the options for table creation.
     *
     * @param options the table options to be set
     * @return the current instance of TableCreateQueryProvider
     */
    public TableCreateQueryProvider tableOptions(String options) {
        this.tableOptions = options;
        return this;
    }

    /**
     * Sets the action to be executed after the query is performed.
     *
     * @param actionAfterQuery a RunnableAction that performs an operation with a Boolean input
     *                         indicating the result of the query operation
     * @return the instance of TableCreateQueryProvider for method chaining
     */
    public TableCreateQueryProvider actionAfterQuery(RunnableAction<QueryResult<TableCreateQueryProvider>> actionAfterQuery) {
        this.actionAfterQuery = actionAfterQuery;
        return this;
    }

    @Override
    public RunnableAction<QueryResult<TableCreateQueryProvider>> actionAfterQuery() {
        return actionAfterQuery;
    }

    /**
     * Retrieves the name of the table.
     *
     * @return the name of the table as a String
     */
    public String table() {
        return this.table;
    }

    /**
     * Retrieves the current {@code CreateMethode} associated with the table creation query.
     *
     * @return the {@code CreateMethode} instance representing the method of creation for the table.
     */
    public CreateMethode createMethode() {
        return this.createMethode;
    }

    /**
     * Retrieves the list of columns defined for the table creation query.
     *
     * @return a list of {@code Column} objects representing the columns associated with the table.
     */
    public List<Column> columns() {
        return this.columns;
    }

    /**
     * Adds a primary key constraint with a specified name to the provided columns in the table creation query.
     *
     * @param name    the name of the primary key constraint
     * @param columns the list of column names to be included in the primary key constraint
     * @return the current instance of {@code TableCreateQueryProvider} to allow method chaining
     */
    public TableCreateQueryProvider primaryKey(String name, List<String> columns) {
        constraints.add(new PrimaryKeyConstraint(name, columns));
        return this;
    }

    /**
     * Adds a primary key constraint to the specified columns in the table creation query.
     *
     * @param columns the list of column names to be included in the primary key constraint
     * @return the current instance of {@code TableCreateQueryProvider} to allow method chaining
     */
    public TableCreateQueryProvider primaryKey(List<String> columns) {
        return primaryKey(null, columns);
    }

    /**
     * Adds a unique constraint with a specified name to the provided columns in the table creation query.
     *
     * @param name    the name of the unique constraint
     * @param columns the list of column names to which the unique constraint should be applied
     * @return the current instance of {@code TableCreateQueryProvider} to allow method chaining
     */
    public TableCreateQueryProvider unique(String name, List<String> columns) {
        constraints.add(new UniqueConstraint(name, columns));
        return this;
    }

    /**
     * Adds a unique constraint to the specified columns in the table creation query.
     *
     * @param columns the list of column names to which the unique constraint should be applied
     * @return the current instance of {@code TableCreateQueryProvider} to allow method chaining
     */
    public TableCreateQueryProvider unique(List<String> columns) {
        return unique(null, columns);
    }

    /**
     * Adds a check constraint with a specified name and expression to the table creation query.
     *
     * @param name       the name of the check constraint
     * @param expression the check constraint expression to be applied to the table
     * @return the current instance of {@code TableCreateQueryProvider} to allow method chaining
     */
    public TableCreateQueryProvider check(String name, String expression) {
        constraints.add(new CheckConstraint(name, expression));
        return this;
    }

    /**
     * Adds a check constraint to the table creation query using the provided expression.
     *
     * @param expression the check constraint expression to be applied to the table
     * @return the current instance of {@code TableCreateQueryProvider} to allow method chaining
     */
    public TableCreateQueryProvider check(String expression) {
        return check(null, expression);
    }

    /**
     * Adds a foreign key constraint to the table creation query.
     *
     * @param name       the name of the foreign key constraint
     * @param columns    the list of columns in the current table that are part of the foreign key
     * @param refTable   the name of the referenced table
     * @param refColumns the list of columns in the referenced table
     * @param onDelete   the action to take when a row in the referenced table is deleted
     * @param onUpdate   the action to take when a row in the referenced table is updated
     * @return the current instance of {@code TableCreateQueryProvider} to allow method chaining
     */
    public TableCreateQueryProvider foreignKey(
            String name, List<String> columns, String refTable, List<String> refColumns,
            ForeignKeyAction onDelete, ForeignKeyAction onUpdate) {
        constraints.add(new ForeignKeyConstraint(name, columns, refTable, refColumns, onDelete, onUpdate));
        return this;
    }

    /**
     * Adds a foreign key constraint to the table creation query.
     *
     * @param columns    the list of columns in the current table that are part of the foreign key
     * @param refTable   the name of the referenced table
     * @param refColumns the list of columns in the referenced table
     * @return the current instance of {@code TableCreateQueryProvider} to allow method chaining
     */
    public TableCreateQueryProvider foreignKey(
            List<String> columns, String refTable, List<String> refColumns) {
        return foreignKey(null, columns, refTable, refColumns, null, null);
    }

    /**
     * Adds an index constraint to the table creation query.
     *
     * @param name    the name of the index
     * @param columns the list of columns included in the index
     * @param unique  whether the index enforces uniqueness
     * @return the current instance of {@code TableCreateQueryProvider} to allow method chaining
     */
    public TableCreateQueryProvider index(String name, List<String> columns, boolean unique) {
        constraints.add(new IndexConstraint(name, columns, unique));
        return this;
    }

    /**
     * Adds an index constraint to the table creation query for the specified columns.
     *
     * @param columns the list of columns to be included in the index
     * @return the current instance of {@code TableCreateQueryProvider} to allow method chaining
     */
    public TableCreateQueryProvider index(List<String> columns) {
        return index(null, columns, false);
    }

    /**
     * Retrieves the list of constraints defined for the table creation query.
     *
     * @return a list of {@code TableConstraint} objects representing the constraints associated with the table.
     */
    public List<TableConstraint> constraints() {
        return constraints;
    }
}
