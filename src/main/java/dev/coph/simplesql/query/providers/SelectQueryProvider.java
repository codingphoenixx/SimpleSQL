package dev.coph.simplesql.query.providers;

import dev.coph.simplesql.query.QueryProvider;
import dev.coph.simplesql.utils.RunnableAction;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.sql.ResultSet;

/**
 * The {@code SelectQueryProvider} class implements the {@link QueryProvider} interface
 * and is responsible for constructing and generating SQL SELECT queries.
 *
 * This class provides additional functionality after executing a query by allowing
 * the assignment of a {@link RunnableAction} that processes the {@link ResultSet}.
 * It also includes mechanisms to store the retrieved {@link ResultSet}.
 *
 */
@Getter
@Accessors(fluent = true, chain = true)
public class SelectQueryProvider implements QueryProvider {


    @Override
    public String generateSQLString() {
        return "";
    }

    /**
     * A {@link Runnable} that will be executed after the query operation completes.
     * This can be used to perform additional processing or cleanup once the query
     * has been executed.
     */
    @Setter
    private RunnableAction<ResultSet> actionAfterQuery;



    /**
     * The ResultSet will be stored here after the request is executed.
     */
    @Setter
    private ResultSet resultSet;

}
