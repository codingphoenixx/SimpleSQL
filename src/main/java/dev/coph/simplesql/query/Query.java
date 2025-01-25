package dev.coph.simplesql.query;

import dev.coph.simplesql.adapter.DatabaseAdapter;
import dev.coph.simplesql.exception.RequestNotExecutableException;
import dev.coph.simplesql.query.providers.*;
import dev.coph.simplesql.utils.Check;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

@Getter
@Accessors(fluent = true)
public class Query {
    public Query(DatabaseAdapter databaseAdapter) {
        this.databaseAdapter = databaseAdapter;
    }

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
     * Indicates whether the query execution should be performed asynchronously.
     * <p>
     * When set to `true`, the queries will be executed asynchronously in a separate thread,
     * leveraging a CompletableFuture mechanism. When set to `false`, the execution will
     * occur synchronously in the main thread.
     * <p>
     * This flag affects the behavior of the {@code execute} method in the {@code Query} class.
     * By default, the value is set to `false`, meaning synchronous execution.
     */
    @Setter
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
    @Setter
    private boolean executed = false;

    /**
     * Represents the success status of the most recent query execution.
     * This variable tracks whether the queries executed in the current or previous
     * operation within the {@code Query} class completed successfully.
     *
     * The value is set to {@code false} at the start of each execution and updated
     * upon completion based on the success or failure of the operation.
     * It is primarily used to determine whether subsequent actions or workflows
     * should proceed depending on the result of the SQL query execution.
     */
    private boolean succeeded = false;


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
    private ArrayList<QueryProvider> queries = new ArrayList<>();


    /**
     * Executes the given SQL queries associated with this Query instance.
     * <p>
     * If the async mode is enabled, the queries are executed asynchronously
     * using a CompletableFuture. Otherwise, they are executed synchronously.
     * The execution status is updated upon completion.
     * <p>
     * Note: The execution status reset to false at the beginning of this method
     * signifies a fresh attempt at running the queries.
     * <p>
     * Throws:
     * A {@code RequestNotExecutableException} if an error occurs that prevents
     * execution of the queries.
     */
    public void execute() {
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
    }


    /**
     * Executes the queries contained in the query object directly and synchronously.
     * <p>
     * This method performs SQL query execution using a database connection
     * obtained from the associated {@code databaseAdapter}. The logic is as follows:
     * <p>
     * - If no queries are present, the method terminates immediately without action.
     * - For a single query:
     * - It prepares the SQL statement for the query.
     * - Depending on the query type:
     * - If the query is a {@code SelectQueryProvider}, it executes the query,
     * retrieves a {@code ResultSet}, and optionally performs a post-execution
     * action.
     * - If the query is an {@code UpdateQueryProvider}, it executes the update query.
     * - For any other query type, it executes the statement directly.
     * - For multiple queries:
     * - It creates a batch of SQL statements.
     * - Executes all the queries in the batch.
     * - Any SQLExceptions during this process are printed to the error stream.
     * <p>
     * If any error occurs that is not a recoverable {@code SQLException}, a
     * {@code RequestNotExecutableException} is
     */
    private void executeDirectly() {
        if (queries.isEmpty())
            return;
        if (!databaseAdapter.connected())
            throw new IllegalStateException("The connection to the database was not established");

        try (Connection connection = databaseAdapter.dataSource().getConnection()) {
            if (queries.size() == 1) {
                String generateSQLString = queries.getFirst().generateSQLString(this);
                System.out.println("Generated SQL-STRING: " + generateSQLString);
                if(generateSQLString == null){
                    System.out.println("Generated SQL-String is null. Canceling request.");
                    return;
                }
                var statement = connection.prepareStatement(generateSQLString);
                try {
                    if (queries.getFirst() instanceof SelectQueryProvider selectRequest) {
                        System.out.println(statement);
                        ResultSet resultSet = statement.executeQuery();
                        selectRequest.resultSet(resultSet);
                        if (selectRequest.actionAfterQuery() != null) {
                            selectRequest.actionAfterQuery().run(resultSet);
                        }
                        succeeded = true;
                    } else if (queries.getFirst() instanceof UpdateQueryProvider) {
                        System.out.println(statement);
                        statement.executeUpdate();
                        succeeded = true;
                    } else {
                        System.out.println(statement);
                        statement.execute();
                        succeeded = true;
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    succeeded = false;
                }
            } else {
                var statement = connection.createStatement();
                for (QueryProvider queryProvider : queries) {
                    try {
                        String generateSQLString = queryProvider.generateSQLString(this);
                        System.out.println("Generated SQL-STRING: " + generateSQLString);
                        if(generateSQLString == null){
                            System.out.println("Generated SQL-String is null. Ignoring request.");
                            continue;
                        }
                        statement.addBatch(generateSQLString);
                    } catch (Exception e) {
                        e.printStackTrace();
                        succeeded = false;
                    }
                }
                try {
                    statement.executeBatch();
                    succeeded = true;
                } catch (SQLException e) {
                    e.printStackTrace();
                    succeeded = false;
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
     * Creates and returns a new instance of {@link DatabaseCreateQueryProvider}.
     * <p>
     * This method is used to initiate a CREATE DATABASE SQL query by providing a
     * {@link DatabaseCreateQueryProvider} object, which can be further configured
     * to construct SQL CREATE DATABASE statements.
     *
     * @return A new {@link DatabaseCreateQueryProvider} instance for constructing
     * CREATE DATABASE SQL queries.
     */
    public static DatabaseCreateQueryProvider createDatabase() {
        return new DatabaseCreateQueryProvider();
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
     *
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
     *
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
     *
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
     * Creates and returns a new instance of {@link TableAlterDropQueryProvider}.
     *
     * This method is used to initiate an ALTER TABLE SQL query for dropping a column or constraint from an existing table.
     * The returned {@link TableAlterDropQueryProvider} object allows further configuration
     * to specify the table name, the column or constraint to be dropped, and additional drop options.
     *
     * @return A new {@link TableAlterDropQueryProvider} instance for constructing ALTER TABLE SQL queries to drop columns or constraints.
     */
    public static TableAlterDropQueryProvider tableAlterDrop() {
        return new TableAlterDropQueryProvider();
    }

    /**
     * Creates and returns a new instance of {@link TableAlterModifyTypeQueryProvider}.
     *
     * This method is used to initiate an ALTER TABLE SQL query for modifying the
     * data type of an existing column in a database table. The returned
     * {@link TableAlterModifyTypeQueryProvider} object allows further configuration,
     * such as specifying the table name, column name, and the new data type to be applied.
     *
     * @return A new {@link TableAlterModifyTypeQueryProvider} instance for constructing
     * ALTER TABLE SQL queries to modify column data types.
     */
    public static TableAlterModifyTypeQueryProvider tableAlterModifyType(){
        return new TableAlterModifyTypeQueryProvider();
    }

    /**
     * Creates and returns a new instance of TableAlterRenameQueryProvider.
     * This method is used to initialize and retrieve an object for
     * handling table alteration and renaming queries.
     *
     * @return a new instance of TableAlterRenameQueryProvider
     */
    public static TableAlterRenameQueryProvider tableAlterRename(){
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
     * Creates and returns a new instance of {@link TableTruncateQueryProvider}.
     *
     * This method is used to initiate a TRUNCATE TABLE SQL query by providing
     * a {@link TableTruncateQueryProvider} object, which can be further configured
     * to specify the table to be truncated.
     *
     * @return A new {@link TableTruncateQueryProvider} instance for constructing
     * TRUNCATE TABLE SQL queries.
     */
    public static TableTruncateQueryProvider tableTruncate() {
        return new TableTruncateQueryProvider();
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


}
