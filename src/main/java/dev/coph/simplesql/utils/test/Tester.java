package dev.coph.simplesql.utils.test;

import dev.coph.simplelogger.Logger;
import dev.coph.simplesql.adapter.DatabaseAdapter;
import dev.coph.simplesql.database.attributes.ColumnType;
import dev.coph.simplesql.database.attributes.CreateMethode;
import dev.coph.simplesql.database.attributes.DataType;
import dev.coph.simplesql.database.attributes.DeleteMethode;
import dev.coph.simplesql.query.Query;
import dev.coph.simplesql.query.providers.TableCreateQueryProvider;
import dev.coph.simplesql.query.providers.TableDropQueryProvider;
import dev.coph.simplesql.utils.test.implementations.InsertRequestTest;
import dev.coph.simplesql.utils.test.implementations.SelectRequestTest;

import java.util.ArrayList;
import java.util.List;

public class Tester {
    private final String TABLE_NAME = "test";
    private final ArrayList<Test> tests = new ArrayList<>();

    private final DatabaseAdapter databaseAdapter;

    public Tester(DatabaseAdapter databaseAdapter) {
        this.databaseAdapter = databaseAdapter;

        tests.add(new SelectRequestTest());
        tests.add(new InsertRequestTest());

    }

    private void setupForTest(Test test) {
        Query query = new Query(databaseAdapter);
        TableDropQueryProvider tableDropQueryProvider = new TableDropQueryProvider()
                .deleteMethode(DeleteMethode.IF_EXISTS)
                .table(TABLE_NAME);

        query.executeQuery(tableDropQueryProvider);

        List<TableCreateQueryProvider> tableCreateQueryProviders = test.setup(databaseAdapter.driverType());
        for (TableCreateQueryProvider provider : tableCreateQueryProviders) {
            provider.table(TABLE_NAME);
            query.queries(provider);
        }
        query.execute();

    }

    public void runTests() {
        for (Test test : tests) {
            Logger.getInstance().info("Setting up for Test '%s'...".formatted(test.name()));
            setupForTest(test);
            Logger.getInstance().info("Start Test '%s' with Driver %s".formatted(test.name(), databaseAdapter.driverType().name()));
            boolean succeeded;
            Exception ex = null;
            try {
                succeeded = test.execute(databaseAdapter);
            } catch (Exception e) {
                ex = e;
                succeeded = false;
            }
            if (succeeded) {
                Logger.getInstance().success("Test '%s' succeeded".formatted(test.name()));
            } else {
                Logger.getInstance().error("Test '%s' failed".formatted(test.name()));
                if (ex != null)
                    ex.printStackTrace();
            }
        }

    }
}
