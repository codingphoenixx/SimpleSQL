package dev.coph.simplesql.utils.test.implementations;

import dev.coph.simplelogger.Logger;
import dev.coph.simplesql.adapter.DatabaseAdapter;
import dev.coph.simplesql.database.attributes.DataType;
import dev.coph.simplesql.driver.DriverType;
import dev.coph.simplesql.query.Query;
import dev.coph.simplesql.query.providers.SelectQueryProvider;
import dev.coph.simplesql.query.providers.TableCreateQueryProvider;
import dev.coph.simplesql.utils.test.Test;

import java.util.List;

public class InsertRequestTest implements Test {
    private final String COLUMN_NAME = "number";
    private final int COLUMN_VALUE = 42;


    @Override
    public String name() {
        return "Insert Request";
    }

    @Override
    public List<TableCreateQueryProvider> setup(DriverType driverType) {
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
                .actionAfterQuery(success -> {
                    if (!success) {
                        Logger.instance().debug("Not succeeded");
                    }
                });
        Query query = new Query(databaseAdapter).executeQuery(insert);

        if (!query.succeeded())
            return false;

        SelectQueryProvider select = Query.select()
                .table("test")
                .resultActionAfterQuery(srs -> {
                    if (!srs.resultSet().next()) {
                        return;
                    }
                    int value = srs.resultSet().getInt(COLUMN_NAME);
                    if (value == COLUMN_VALUE) {
                        ref.succeeded = true;
                    }
                });
        query = new Query(databaseAdapter).executeQuery(select);

        return ref.succeeded;
    }
}
