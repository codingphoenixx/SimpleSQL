package dev.coph.simplesql.utils.test;

import dev.coph.simplelogger.Logger;
import dev.coph.simplesql.adapter.DatabaseAdapter;
import dev.coph.simplesql.database.attributes.DeleteMethode;
import dev.coph.simplesql.query.Query;
import dev.coph.simplesql.query.QueryProvider;
import dev.coph.simplesql.query.providers.InsertQueryProvider;
import dev.coph.simplesql.query.providers.SelectQueryProvider;
import dev.coph.simplesql.query.providers.TableCreateQueryProvider;
import dev.coph.simplesql.query.providers.TableDropQueryProvider;
import dev.coph.simplesql.utils.test.implementations.InsertRequestTest;
import dev.coph.simplesql.utils.test.implementations.SelectRequestTest;
import dev.coph.simplesql.utils.test.implementations.TableCreateRequestTest;

import java.util.ArrayList;
import java.util.List;

/**
 * The Tester class is responsible for managing and executing a series of database-related tests.
 * It provides functionality for setting up the required database environment for each test,
 * executing the tests, and logging the results.
 */
public class Tester {
    private final Logger logger = Logger.of("Tester");

    private final String TABLE_NAME = "test";
    private final ArrayList<Test> tests = new ArrayList<>();

    private final DatabaseAdapter databaseAdapter;

    /**
     * Initializes the Tester with the specified database adapter and prepares the tests to be executed.
     * The database adapter is used to interact with the database during test execution.
     *
     * @param databaseAdapter the DatabaseAdapter instance used for database interaction
     */
    public Tester(DatabaseAdapter databaseAdapter) {
        this.databaseAdapter = databaseAdapter;

        tests.add(new SelectRequestTest());
        tests.add(new InsertRequestTest());
        tests.add(new TableCreateRequestTest());

    }

    private void setupForTest(Test test) {
        Query query = new Query(databaseAdapter);
        TableDropQueryProvider tableDropQueryProvider = new TableDropQueryProvider()
                .deleteMethode(DeleteMethode.IF_EXISTS)
                .table(TABLE_NAME);


        query.executeQuery(tableDropQueryProvider);

        List<QueryProvider> tableCreateQueryProviders = test.setup(databaseAdapter.driverType());
        for (QueryProvider provider : tableCreateQueryProviders) {
            if (provider instanceof TableCreateQueryProvider tableCreateQueryProvider) {
                tableCreateQueryProvider.table(TABLE_NAME);
            }
            if (provider instanceof InsertQueryProvider insertQueryProvider) {
                insertQueryProvider.table(TABLE_NAME);
            }
            if (provider instanceof SelectQueryProvider selectQueryProvider) {
                selectQueryProvider.table(TABLE_NAME);
            }
            query.queries(provider);
        }
        query.execute();

    }

    /**
     * Executes a series of predefined tests, logs their results, and reports any exceptions encountered
     * during execution. Each test is set up individually before being executed against the configured
     * database adapter.
     *<p>
     * The method performs the following steps for each test:<br>
     * 1. Logs the start of the test setup process.<br>
     * 2. Invokes the {@code setupForTest} method to configure the database environment needed for the test.<br>
     * 3. Logs the commencement of test execution, including the test name and database driver type.<br>
     * 4. Executes the test logic using the provided database adapter.<br>
     * 5. Logs the outcome of the test, reporting success or failure.<br>
     * 6. If the test fails, logs any exceptions encountered during execution.<br>
     *<p>
     * This method leverages the {@code Logger} instance to provide clear visibility into the
     * test execution process, including system state and errors.
     */
    public void runTests() {
        for (Test test : tests) {
            logger.info("Setting up for Test '%s'...".formatted(test.name()));
            setupForTest(test);
            logger.info("Start Test '%s' with Driver %s".formatted(test.name(), databaseAdapter.driverType().name()));
            boolean succeeded;
            Exception ex = null;
            try {
                succeeded = test.execute(databaseAdapter);
            } catch (Exception e) {
                ex = e;
                succeeded = false;
            }
            if (succeeded) {
                logger.success("Test '%s' succeeded".formatted(test.name()));
            } else {
                logger.error("Test '%s' failed".formatted(test.name()));
                if (ex != null)
                    logger.error(ex);
            }
        }

    }
}
