package dev.coph.simplesql.utils.test.implementations;

import dev.coph.simplesql.adapter.DatabaseAdapter;
import dev.coph.simplesql.database.attributes.Condition;
import dev.coph.simplesql.database.attributes.DataType;
import dev.coph.simplesql.database.attributes.Order;
import dev.coph.simplesql.driver.DriverType;
import dev.coph.simplesql.query.Query;
import dev.coph.simplesql.query.QueryProvider;
import dev.coph.simplesql.utils.test.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A test implementation to validate the functionality of select queries on a database.
 * This class implements the Test interface and provides specific logic for testing
 * a "SELECT" operation on a database table named "test". The test ensures query
 * execution succeeds and evaluates the result set behavior.
 */
public class SelectRequestTest implements Test {
    private final String COLUMN_NAME = "number";
    private final int COLUMN_VALUE = 42;

    @Override
    public String name() {
        return "Select Request";
    }

    @Override
    public List<QueryProvider> setup(DriverType driverType) {
        List<QueryProvider> provider = new ArrayList<>(List.of(
                Query.tableCreate().column(COLUMN_NAME, DataType.INTEGER),
                Query.insert().entry(COLUMN_NAME, COLUMN_VALUE)
        ));
        for (int i = 0; i < 1000; i++) {
            provider.add(Query.insert().entry(COLUMN_NAME, new Random().nextInt()));
        }
        return provider;
    }


    @Override
    public boolean execute(DatabaseAdapter databaseAdapter) {
        var ref = new Object() {
            boolean succeeded = false;
        };
        var select = Query.select()
                .table("test")
                .limit(1)
                .order("number", Order.Direction.DESCENDING)
                .condition("number", COLUMN_VALUE)
                .conditionGroup(Condition.Type.OR, new Condition("number", COLUMN_VALUE - 1), new Condition("number", COLUMN_VALUE + 1))
                .resultActionAfterQuery(srs -> {
                    srs.next(resultSet -> {
                        if (resultSet.getInt(COLUMN_NAME) == COLUMN_VALUE)
                            ref.succeeded = true;
                    });
                });
        Query query = new Query(databaseAdapter).executeQuery(select);

        if (!query.succeeded())
            return false;

        return ref.succeeded;
    }
}
