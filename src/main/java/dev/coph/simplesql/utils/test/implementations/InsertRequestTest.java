package dev.coph.simplesql.utils.test.implementations;

import dev.coph.simplelogger.Logger;
import dev.coph.simplesql.adapter.DatabaseAdapter;
import dev.coph.simplesql.database.attributes.DataType;
import dev.coph.simplesql.driver.DriverType;
import dev.coph.simplesql.query.Query;
import dev.coph.simplesql.query.QueryProvider;
import dev.coph.simplesql.query.providers.SelectQueryProvider;
import dev.coph.simplesql.utils.test.Test;

import java.util.List;

/**
 * The InsertRequestTest class is a test case implementation of the Test interface
 * which validates the insertion of data into a database table and verifies the
 * correctness of the operation.
 * <p>
 * This class defines a test to insert a value into a specific column of a table
 * and subsequently verifies if the data inserted is correct by performing a
 * selection query.
 */
public class InsertRequestTest implements Test {
    private final Logger logger = Logger.of("Test - Insert");

    private final String COLUMN_NAME = "number";
    private final int COLUMN_VALUE = 42;


    @Override
    public String name() {
        return "Insert Request";
    }

    @Override
    public List<QueryProvider> setup(DriverType driverType) {
        return List.of(
                Query.tableCreate().column(COLUMN_NAME, DataType.INTEGER)
        );
    }

    @Override
    public boolean execute(DatabaseAdapter databaseAdapter) {
        var ref = new Object() {
            boolean succeeded = false;
        };
        var insert = Query.insert()
                .table("test")
                .entry(COLUMN_NAME, COLUMN_VALUE)
                .actionAfterQuery(result -> {
                    if (!result.success()) {
                        logger.debug("Not succeeded");
                    }
                    ref.succeeded = result.queryProvider().affectedRows() == 1;
                });
        Query query = new Query(databaseAdapter).executeQuery(insert);

        if (query.notSucceeded())
            return false;

        return ref.succeeded;
    }
}
