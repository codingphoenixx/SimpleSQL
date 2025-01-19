package dev.coph.simplesql.query;

import dev.coph.simplesql.adapter.DatabaseAdapter;
import dev.coph.simplesql.exception.RequestNotExecutableException;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

@Getter
@Accessors(fluent = true)
public class Query {
    public Query(DatabaseAdapter databaseAdapter) {
        this.databaseAdapter = databaseAdapter;
    }

    private final DatabaseAdapter databaseAdapter;

    @Setter
    private boolean async = false;
    @Setter
    private boolean executedSuccessfully = false;


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
        executedSuccessfully = false;
        if (async) {
            CompletableFuture.runAsync(() -> {
                executeDirectly();
                executedSuccessfully = true;
            });
        } else {
            executeDirectly();
            executedSuccessfully = true;
        }
    }


    private void executeDirectly() {
        try (Connection connection = databaseAdapter.dataSource().getConnection()) {
            Statement statement = connection.createStatement();

            for (QueryProvider queryProvider : queries) {
                try {
                    statement.addBatch(queryProvider.generateSQLString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try {
                statement.executeBatch();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            throw new RequestNotExecutableException(e);
        }
    }

    public Query queries(QueryProvider... queries) {
        this.queries.addAll(Arrays.asList(queries));
        return this;
    }

}
