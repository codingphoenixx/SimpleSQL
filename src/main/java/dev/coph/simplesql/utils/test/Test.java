package dev.coph.simplesql.utils.test;

import dev.coph.simplesql.adapter.DatabaseAdapter;
import dev.coph.simplesql.driver.DriverType;
import dev.coph.simplesql.query.QueryProvider;
import dev.coph.simplesql.query.providers.TableCreateQueryProvider;

import java.util.List;

/**
 * Represents a contract for classes that define database-related tests. This interface
 * provides methods for setting up database structures, executing test logic, and
 * retrieving the name of the test.
 */
public interface Test {
    /**
     * Retrieves the name of the test.
     *
     * @return the name of the test as a string
     */
    String name();

    /**
     * Configures and provides a list of query providers responsible for creating database tables
     * specific to the given driver type.
     *
     * @param driverType the driver type used to determine specific table creation queries
     * @return a list of TableCreateQueryProvider instances for setting up database tables
     */
    List<QueryProvider> setup(DriverType driverType);

    /**
     * Executes the test logic against the provided database adapter.
     *
     * @param databaseAdapter the database adapter to be used for executing the test logic
     * @return true if the test execution is successful, false otherwise
     */
    boolean execute(DatabaseAdapter databaseAdapter);

}
