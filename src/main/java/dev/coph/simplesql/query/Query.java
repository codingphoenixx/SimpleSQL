package dev.coph.simplesql.query;

import dev.coph.simplelogger.Logger;
import dev.coph.simplesql.adapter.DatabaseAdapter;
import dev.coph.simplesql.exception.RequestNotExecutableException;
import dev.coph.simplesql.query.providers.*;
import dev.coph.simpleutilities.check.Check;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * The {@code Query} class provides a robust framework for constructing, managing, and executing
 * database operations. It includes methods for handling various SQL-based queries with support
 * for method chaining and configurable execution modes (synchronous or asynchronous).
 * <p>
 * This class interacts with a database adapter, allowing seamless communication with the underlying
 * database. The {@code Query} class simplifies the management of SQL operations by providing mechanisms
 * for executing queries directly, creating different query types, and managing execution results.<br>
 * <br><br>
 * Fields:<br>
 * - {@code databaseAdapter}: The adapter responsible for handling interactions with the underlying database.<br>
 * - {@code async}: A flag indicating whether query execution is performed asynchronously.<br>
 * - {@code executed}: A boolean flag specifying whether the query has been executed.<br>
 * - {@code succeeded}: A boolean flag representing whether the query execution was successful.<br>
 * - {@code queries}: A collection of queries to be executed.<br>
 */
public class Query {
    /**
     * The {@code databaseAdapter} field provides access to the underlying database connection
     * and configuration through the {@link DatabaseAdapter} class.
     * <p>
     * This adapter serves as the primary interface for managing database connections,
     * such as establishing connections, configuring the connection pool, and executing queries.
     * <p>
     * It is initialized once through the constructor of the {@link Query} class and remains
     * immutable throughout the lifecycle of the {@code Query} instance.
     * <p>
     * The {@link DatabaseAdapter} instance facilitates communication with the database and
     * provides the necessary resources (e.g., data source) for executing SQL operations.
     */
    private final DatabaseAdapter databaseAdapter;
    /**
     * A list containing all the SQL queries associated with the current Query instance.
     * Each entry in the list implements the {@link QueryProvider} interface, which
     * provides the SQL query generation logic and represents a single database action.
     * <p>
     * This collection allows for batch processing of queries, supporting both
     * Insert and Select operations among others, depending on the implementation
     * of {@link QueryProvider}.
     * <p>
     * The queries are executed either synchronously or asynchronously based
     * on the settings of the owning Query object.
     * <p>
     * This field plays a primary role in SQL command preparation and submission
     * to the underlying database system.
     */
    private final ArrayList<QueryProvider> queries = new ArrayList<>();
    /**
     * Indicates whether the query execution should be performed asynchronously.
     * <p>
     * When set to `true`, the queries will be executed asynchronously in a separate thread,
     * leveraging a CompletableFuture mechanism. When set to `false`, the execution will
     * occur synchronously in the main thread.
     * <p>
     * This flag affects the behavior of the {@code execute} method in the {@code Query} class.
     * By default, the value is set to `false`, meaning synchronous execution.
     */
    private boolean async = false;
    /**
     * Indicates whether the execution of the queries associated with this Query
     * instance was successful. This value is set to {@code false} before an
     * execution attempt and updated to {@code true} if the queries execute
     * successfully.
     * <p>
     * This flag is used to monitor the completion status of query execution,
     * whether executed synchronously or asynchronously.
     * <p>
     * Default value is {@code false}.
     */
    private boolean executed = false;
    /**
     * Represents the success status of the most recent query execution.
     * This variable tracks whether the queries executed in the current or previous
     * operation within the {@code Query} class completed successfully.
     * <p>
     * The value is set to {@code false} at the start of each execution and updated
     * upon completion based on the success or failure of the operation.
     * It is primarily used to determine whether subsequent actions or workflows
     * should proceed depending on the result of the SQL query execution.
     */
    private boolean succeeded = false;
    private boolean preserveQueriesAfterExecution = false;
    private boolean useTransaction = true;


    /**
     * Constructor for the Query class that initializes it with a given DatabaseAdapter instance.
     *
     * @param databaseAdapter the DatabaseAdapter instance to be used for database operations
     */
    public Query(DatabaseAdapter databaseAdapter) {
        this.databaseAdapter = databaseAdapter;
    }

    /**
     * Creates and returns a new instance of {@link DatabaseCreateQueryProvider}.
     * <p>
     * This method is used to initiate a CREATE DATABASE SQL query by providing a
     * {@link DatabaseCreateQueryProvider} object, which can be further configured
     * to construct SQL CREATE DATABASE statements.
     *
     * @return A new {@link DatabaseCreateQueryProvider} instance for constructing
     * CREATE DATABASE SQL queries.
     */
    public static DatabaseCreateQueryProvider databaseCreate() {
        return new DatabaseCreateQueryProvider();
    }

    /**
     * Creates and returns an instance of DatabaseDropQueryProvider, which can be used to handle
     * database drop operations or generate queries related to dropping database structures.
     *
     * @return an instance of DatabaseDropQueryProvider to manage database drop functionalities
     */
    public static DatabaseDropQueryProvider databaseDrop() {
        return new DatabaseDropQueryProvider();
    }

    /**
     * Creates and returns a new instance of {@link DeleteQueryProvider}.
     * <p>
     * This method is used to initiate a DELETE SQL query by providing a
     * {@link DeleteQueryProvider} object, which can be further configured
     * to construct SQL DELETE statements.
     *
     * @return A new {@link DeleteQueryProvider} instance for constructing
     * DELETE SQL queries.
     */
    public static DeleteQueryProvider delete() {
        return new DeleteQueryProvider();
    }

    /**
     * Creates and returns a new instance of {@link InsertQueryProvider}.
     * <p>
     * This method is used to initiate an INSERT SQL query by providing
     * an {@link InsertQueryProvider} object, which can be further configured
     * to construct SQL INSERT statements. The returned object allows
     * customization of the table name, insertion method, and specific entries
     * to be added to the database.
     *
     * @return A new {@link InsertQueryProvider} instance for constructing
     * INSERT SQL queries.
     */
    public static InsertQueryProvider insert() {
        return new InsertQueryProvider();
    }

    /**
     * Creates and returns a new instance of {@link SelectQueryProvider}.
     * <p>
     * This method is used to initiate a SELECT SQL query by providing a
     * {@link SelectQueryProvider} object, which can be further configured
     * to construct SQL SELECT statements.
     *
     * @return A new {@link SelectQueryProvider} instance for constructing
     * SELECT SQL queries.
     */
    public static SelectQueryProvider select() {
        return new SelectQueryProvider();
    }

    /**
     * Creates and returns an instance of {@link TableAlterAddAttributeQueryProvider}.
     * <p>
     * This method is used to initiate an ALTER TABLE SQL query for adding an attribute
     * (such as UNIQUE or PRIMARY KEY) to an existing column of a table. The returned
     * {@link TableAlterAddAttributeQueryProvider} object can be further configured
     * to specify the column name and the type of attribute to be added.
     *
     * @return A new {@link TableAlterAddAttributeQueryProvider} instance for constructing
     * ALTER TABLE SQL queries to add attributes to a column.
     */
    public static TableAlterAddAttributeQueryProvider tableAlterAddAttribute() {
        return new TableAlterAddAttributeQueryProvider();
    }

    /**
     * Creates and returns a new instance of {@link TableAlterAddColumnQueryProvider}.
     * <p>
     * This method is used to initiate an ALTER TABLE SQL query for adding a new column
     * to an existing table. The returned {@link TableAlterAddColumnQueryProvider} object
     * allows further configuration, such as specifying the column name, data type,
     * position, default value, and whether the column should only be added if it does not already exist.
     *
     * @return A new {@link TableAlterAddColumnQueryProvider} instance for constructing
     * ALTER TABLE SQL queries to add columns.
     */
    public static TableAlterAddColumnQueryProvider tableAlterAddColumn() {
        return new TableAlterAddColumnQueryProvider();
    }

    /**
     * Creates and returns a new instance of {@link TableAlterColumnDefaultValueQueryProvider}.
     * <p>
     * This method is used to initiate an ALTER TABLE SQL query to set or remove
     * a default value for a specific column in a database table. The returned
     * {@link TableAlterColumnDefaultValueQueryProvider} object allows further
     * configuration, such as specifying the column name, the default value to
     * set, or whether to drop an existing default value.
     *
     * @return A new {@link TableAlterColumnDefaultValueQueryProvider} instance for constructing
     * ALTER TABLE SQL queries to modify the default value of a column.
     */
    public static TableAlterColumnDefaultValueQueryProvider tableAlterColumnDefaultValue() {
        return new TableAlterColumnDefaultValueQueryProvider();
    }

    /**
     * Creates and returns a new instance of {@link TableAlterDropColumnQueryProvider}.
     * <p>
     * This method is used to initiate an ALTER TABLE SQL query for dropping a column or constraint from an existing table.
     * The returned {@link TableAlterDropColumnQueryProvider} object allows further configuration
     * to specify the table name, the column or constraint to be dropped, and additional drop options.
     *
     * @return A new {@link TableAlterDropColumnQueryProvider} instance for constructing ALTER TABLE SQL queries to drop columns or constraints.
     */
    public static TableAlterDropColumnQueryProvider tableAlterDropColumn() {
        return new TableAlterDropColumnQueryProvider();
    }

    /**
     * Creates and returns a new instance of {@link TableAlterModifyTypeQueryProvider}.
     * <p>
     * This method is used to initiate an ALTER TABLE SQL query for modifying the
     * data type of an existing column in a database table. The returned
     * {@link TableAlterModifyTypeQueryProvider} object allows further configuration,
     * such as specifying the table name, column name, and the new data type to be applied.
     *
     * @return A new {@link TableAlterModifyTypeQueryProvider} instance for constructing
     * ALTER TABLE SQL queries to modify column data types.
     */
    public static TableAlterModifyTypeQueryProvider tableAlterModifyType() {
        return new TableAlterModifyTypeQueryProvider();
    }

    /**
     * Creates and returns a new instance of TableAlterRenameQueryProvider.
     * This method is used to initialize and retrieve an object for
     * handling table alteration and renaming queries.
     *
     * @return a new instance of TableAlterRenameQueryProvider
     */
    public static TableAlterRenameQueryProvider tableAlterRename() {
        return new TableAlterRenameQueryProvider();
    }

    /**
     * Creates and returns a new instance of {@link TableCreateQueryProvider}.
     * <p>
     * This method is used to initiate a CREATE TABLE SQL query by providing
     * a {@link TableCreateQueryProvider} object, which can be further configured
     * to construct SQL CREATE TABLE statements.
     *
     * @return A new {@link TableCreateQueryProvider} instance for constructing
     * CREATE TABLE SQL queries.
     */
    public static TableCreateQueryProvider tableCreate() {
        return new TableCreateQueryProvider();
    }

    /**
     * Creates and returns a new instance of {@link TableDropQueryProvider}.
     * <p>
     * This method is used to initiate a DROP TABLE SQL query by providing a
     * {@link TableDropQueryProvider} object, which can be further configured
     * to construct SQL DROP TABLE statements.
     *
     * @return A new {@link TableDropQueryProvider} instance for constructing
     * DROP TABLE SQL queries.
     */
    public static TableDropQueryProvider tableDrop() {
        return new TableDropQueryProvider();
    }

    /**
     * Creates and returns a new instance of {@link TruncateQueryProvider}.
     * <p>
     * This method is used to initiate a TRUNCATE TABLE SQL query by providing
     * a {@link TruncateQueryProvider} object, which can be further configured
     * to specify the table to be truncated.
     *
     * @return A new {@link TruncateQueryProvider} instance for constructing
     * TRUNCATE TABLE SQL queries.
     */
    public static TruncateQueryProvider truncate() {
        return new TruncateQueryProvider();
    }

    /**
     * Creates and returns a new instance of {@link UpdateQueryProvider}.
     * <p>
     * This method is used to initiate an UPDATE SQL query by providing an
     * {@link UpdateQueryProvider} object, which can be further configured
     * to construct SQL UPDATE statements.
     *
     * @return A new {@link UpdateQueryProvider} instance for constructing
     * UPDATE SQL queries.
     */
    public static UpdateQueryProvider update() {
        return new UpdateQueryProvider();
    }

    /**
     * Executes the query using the configured database adapter. The execution can be
     * done synchronously or asynchronously, based on the `async` flag. If executed
     * asynchronously, the execution will happen in a separate thread.
     *
     * @return the current Query instance after execution, allowing for method chaining.
     */
    public Query execute() {
        Check.ifNull(databaseAdapter, "Database Adapter");
        executed = false;
        succeeded = false;
        if (async) {
            CompletableFuture.runAsync(() -> {
                executeDirectly();
                executed = true;
            });
        } else {
            executeDirectly();
            executed = true;
        }
        if (!preserveQueriesAfterExecution) queries.clear();
        return this;
    }

    /**
     * Executes the provided queries by adding them to the query list and then initiating execution.
     *
     * @param queries an array of {@link QueryProvider} objects to be executed.
     *                Must not be null or empty.
     * @return the current {@link Query} instance after executing the queries.
     */
    public Query executeQuery(QueryProvider... queries) {
        Check.ifNullOrEmptyMap(queries, "Query");
        this.queries.addAll(Arrays.asList(queries));
        execute();
        return this;
    }

    /**
     * Executes a list of SQL queries directly against the database.
     * This method handles both single-query and batch-query execution.
     * <br>
     * The method enforces the following:<br>
     * - Verifies the query list is not empty.<br>
     * - Ensures an active database connection is established; if not, throws an IllegalStateException.<br>
     * <br><br>
     * For single-query execution:<br>
     * - Prepares the SQL string from the first query in the list.<br>
     * - Supports different execution flows based on the query type:<br>
     * - If the query is a select query, it executes and processes the result set with optional
     * post-query actions provided by the query object.
     * - If the query is an update query, it executes the update statement.<br>
     * - For other query types, it executes the query directly.<br>
     * - Sets whether execution was successful via the `succeeded` flag.<br>
     * <br><br>
     * For batch-query execution:<br>
     * - Iterates through the query list, generating SQL strings for each query and adding them to a batch.<br>
     * - Executes the batch of queries as a single transaction.<br>
     * - Handles exceptions and sets the `succeeded` flag accordingly.<br>
     * <br><br>
     * Notes:<br>
     * - SQL strings that cannot be generated or are `null` are ignored for execution.<br>
     * - Output messages inform about ignored queries or any errors encountered.<br>
     * - Exceptions during execution result in the `succeeded` flag being set to false,
     * and in the case of a non-recoverable error, a RequestNotExecutableException is thrown.<br>
     * <br>
     * The method operates under the assumption that the provided `databaseAdapter` is properly initialized
     * and the `queries` list consists of appropriate `QueryProvider` instances.
     */
    private void executeDirectly() {
        if (queries.isEmpty()) return;
        if (!databaseAdapter.connected()) {
            throw new IllegalStateException("The connection to the database was not established");
        }

        try (Connection connection = databaseAdapter.dataSource().getConnection()) {
            boolean originalAutoCommit = connection.getAutoCommit();
            if (useTransaction)
                connection.setAutoCommit(false);
            succeeded = false;

            try {
                if (queries.size() == 1) {
                    QueryProvider queryProvider = queries.get(0);
                    if (!queryProvider.compatibility().isCompatible(databaseAdapter.driverType())) {
                        throw new UnsupportedOperationException(
                                "The current request is not supported by the database driver. Failing request. " +
                                        "Please check the documentation for supported database operations and try again with a compatible request."
                        );
                    }

                    String sql = queryProvider.generateSQLString(this);
                    if (sql == null) {
                        Logger.instance().log(Logger.LogLevel.ERROR, "Generated SQL-String is null. Canceling request");
                        if (useTransaction)
                            connection.rollback();
                        return;
                    }

                    try (var ps = connection.prepareStatement(sql)) {
                        queryProvider.bindParameters(ps);
                        Logger.instance().log(Logger.LogLevel.DEBUG, "Executing query: " + sql);

                        if (queryProvider instanceof SelectQueryProvider selectRequest) {
                            try (ResultSet rs = ps.executeQuery()) {
                                selectRequest.resultSet(rs);
                                succeeded = true;
                                if (selectRequest.resultActionAfterQuery() != null) {
                                    selectRequest.resultActionAfterQuery().run(new SimpleResultSet(rs));
                                }
                            }
                        } else if (queryProvider instanceof UpdateQueryProvider) {
                            ps.executeUpdate();
                            succeeded = true;
                        } else {
                            ps.execute();
                            succeeded = true;
                        }
                    } catch (Exception e) {
                        Logger.instance();
                        Logger.error("Failed to execute query: " + sql, e);
                        throw e;
                    }

                    if (queryProvider.actionAfterQuery() != null) {
                        queryProvider.actionAfterQuery().run(succeeded);
                    }

                } else {
                    class Bucket {
                        final String sql;
                        final List<QueryProvider> providers = new ArrayList<>();

                        Bucket(String sql) {
                            this.sql = sql;
                        }
                    }

                    Map<String, Bucket> batchBuckets = new LinkedHashMap<>();
                    List<QueryProvider> selectProviders = new ArrayList<>();

                    for (QueryProvider qp : queries) {
                        try {
                            if (!qp.compatibility().isCompatible(databaseAdapter.driverType())) {
                                throw new UnsupportedOperationException(
                                        "The current request is not supported by the database driver. Failing request. " +
                                                "Please check the documentation for supported database operations and try again with a compatible request."
                                );
                            }
                            String sql = qp.generateSQLString(this);
                            if (sql == null) {
                                System.out.println("Generated SQL-String is null. Ignoring request.");
                                if (qp.actionAfterQuery() != null) qp.actionAfterQuery().run(false);
                                continue;
                            }

                            if (qp instanceof SelectQueryProvider) {
                                selectProviders.add(qp);
                            } else {
                                batchBuckets.computeIfAbsent(sql, Bucket::new).providers.add(qp);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            if (qp.actionAfterQuery() != null) {
                                qp.actionAfterQuery().run(false);
                            }
                        }
                    }

                    boolean allOk = true;

                    for (Bucket bucket : batchBuckets.values()) {
                        String sql = bucket.sql;
                        Logger.instance().log(Logger.LogLevel.DEBUG,
                                "Executing batch for SQL: " + sql + " with size " + bucket.providers.size());
                        try (var ps = connection.prepareStatement(sql)) {
                            for (QueryProvider qp : bucket.providers) {
                                try {
                                    qp.bindParameters(ps);
                                    ps.addBatch();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    allOk = false;
                                    if (qp.actionAfterQuery() != null) qp.actionAfterQuery().run(false);
                                }
                            }
                            try {
                                ps.executeBatch();
                                for (QueryProvider qp : bucket.providers) {
                                    if (qp.actionAfterQuery() != null) qp.actionAfterQuery().run(true);
                                }
                            } catch (SQLException e) {
                                e.printStackTrace();
                                allOk = false;
                                for (QueryProvider qp : bucket.providers) {
                                    if (qp.actionAfterQuery() != null) qp.actionAfterQuery().run(false);
                                }
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                            allOk = false;
                            for (QueryProvider qp : bucket.providers) {
                                if (qp.actionAfterQuery() != null) qp.actionAfterQuery().run(false);
                            }
                        }
                    }

                    for (QueryProvider qp : selectProviders) {
                        String sql = qp.generateSQLString(this);
                        if (sql == null) {
                            System.out.println("Generated SQL-String is null. Ignoring request.");
                            if (qp.actionAfterQuery() != null) qp.actionAfterQuery().run(false);
                            allOk = false;
                            continue;
                        }
                        Logger.instance().log(Logger.LogLevel.DEBUG, "Executing query: " + sql);
                        try (var ps = connection.prepareStatement(sql)) {
                            qp.bindParameters(ps);
                            SelectQueryProvider select = (SelectQueryProvider) qp;
                            try (ResultSet rs = ps.executeQuery()) {
                                select.resultSet(rs);
                                if (select.resultActionAfterQuery() != null) {
                                    select.resultActionAfterQuery().run(new SimpleResultSet(rs));
                                }
                                if (qp.actionAfterQuery() != null) qp.actionAfterQuery().run(true);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            allOk = false;
                            if (qp.actionAfterQuery() != null) qp.actionAfterQuery().run(false);
                        }
                    }

                    succeeded = allOk;
                }
                if (useTransaction) {
                    if (succeeded)
                        connection.commit();
                    else
                        connection.rollback();
                }
            } catch (Exception e) {
                try {
                    if (useTransaction)
                        connection.rollback();
                } catch (Exception ignore) {
                }
                succeeded = false;
                throw new RequestNotExecutableException(e);
            } finally {
                try {
                    if (useTransaction)
                        connection.setAutoCommit(originalAutoCommit);
                } catch (Exception ignore) {
                }
            }

        } catch (Exception e) {
            succeeded = false;
            throw new RequestNotExecutableException(e);
        }
    }


    /**
     * Adds one or more {@code QueryProvider} objects to this {@code Query} instance.
     * <p>
     * This method appends the provided {@link QueryProvider} objects to the internal
     * list of queries that this {@code Query} instance will execute.
     *
     * @param queries One or more {@link QueryProvider} objects to be added.
     * @return The current {@code Query} instance for method chaining.
     */
    public Query queries(QueryProvider... queries) {
        this.queries.addAll(Arrays.asList(queries));
        return this;
    }

    /**
     * Adds a new query to the list of queries and returns the updated Query object.
     *
     * @param query the QueryProvider object to be added to the list of queries
     * @return the updated Query object for method chaining
     */
    public Query query(QueryProvider query) {
        this.queries.add(query);
        return this;
    }

    /**
     * Sets whether to use a transaction for the current query.
     *
     * @param use a boolean indicating whether to enable transaction (true) or disable it (false).
     * @return the current Query instance for method chaining.
     */
    public Query useTransaction(boolean use) {
        this.useTransaction = use;
        return this;
    }

    /**
     * Returns whether transactions are enabled or not.
     *
     * @return true if transactions are enabled, false otherwise
     */
    public boolean useTransaction() {
        return useTransaction;
    }

    /**
     * Configures whether queries should be preserved after execution.
     * If set to true, the executed queries will be stored and remain accessible
     * for inspection or debugging purposes.
     *
     * @param preserve a boolean indicating whether to preserve queries after execution.
     *                 If true, queries will not be cleared after execution; otherwise,
     *                 they will be cleared.
     * @return the current Query instance with the updated configuration.
     */
    public Query preserveQueriesAfterExecution(boolean preserve) {
        this.preserveQueriesAfterExecution = preserve;
        return this;
    }

    /**
     * Indicates whether queries should be preserved after execution.
     * This method returns the state of the preserveQueriesAfterExecution flag.
     *
     * @return true if queries are preserved after execution, false otherwise
     */
    public boolean preserveQueriesAfterExecution() {
        return preserveQueriesAfterExecution;
    }

    /**
     * Provides access to the current instance of the DatabaseAdapter.
     *
     * @return the DatabaseAdapter associated with this context.
     */
    public DatabaseAdapter databaseAdapter() {
        return this.databaseAdapter;
    }

    /**
     * Indicates whether the operation is asynchronous.
     *
     * @return true if the operation is asynchronous, false otherwise
     */
    public boolean async() {
        return this.async;
    }

    /**
     * Checks whether the operation has been executed.
     *
     * @return true if the operation has been executed, false otherwise.
     */
    public boolean executed() {
        return this.executed;
    }

    /**
     * Checks if the operation or process was successful.
     *
     * @return true if the operation succeeded, false otherwise
     */
    public boolean succeeded() {
        return this.succeeded;
    }

    /**
     * Sets the async mode for the query.
     *
     * @param async a boolean value indicating whether the query should be executed asynchronously
     * @return the current instance of the Query object with the updated async mode
     */
    public Query async(boolean async) {
        this.async = async;
        return this;
    }

    /**
     * Sets the executed status of the query.
     *
     * @param executed a boolean value representing whether the query has been executed
     * @return the current Query instance with the updated executed status
     */
    public Query executed(boolean executed) {
        this.executed = executed;
        return this;
    }
}
