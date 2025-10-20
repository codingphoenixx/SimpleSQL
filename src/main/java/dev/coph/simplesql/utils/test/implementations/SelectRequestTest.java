package dev.coph.simplesql.utils.test.implementations;

import dev.coph.simplesql.adapter.DatabaseAdapter;
import dev.coph.simplesql.database.attributes.DataType;
import dev.coph.simplesql.driver.DriverType;
import dev.coph.simplesql.query.Query;
import dev.coph.simplesql.query.providers.TableCreateQueryProvider;
import dev.coph.simplesql.utils.test.Test;

import java.util.List;

/**
 * A test implementation to validate the functionality of select queries on a database.
 * This class implements the Test interface and provides specific logic for testing
 * a "SELECT" operation on a database table named "test". The test ensures query
 * execution succeeds and evaluates the result set behavior.
 */
public class SelectRequestTest implements Test {


    @Override
    public String name() {
        return "Select Request";
    }

    @Override
    public List<TableCreateQueryProvider> setup(DriverType driverType) {
        return List.of(
                Query.tableCreate().column("number", DataType.INTEGER)
        );
    }


    @Override
    public boolean execute(DatabaseAdapter databaseAdapter) {
        var ref = new Object() {
            boolean succeeded = false;
        };
        var select = Query.select()
                .table("test")
                //.limit(1)
                //.order("number", Order.Direction.DESCENDING)
                .resultActionAfterQuery(srs -> {
                    if (!srs.resultSet().next()) {
                        ref.succeeded = true;
                    }
                });
        Query query = new Query(databaseAdapter).executeQuery(select);

        if (!query.succeeded())
            return false;

        return ref.succeeded;
    }
}
