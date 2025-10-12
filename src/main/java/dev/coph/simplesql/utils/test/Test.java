package dev.coph.simplesql.utils.test;

import dev.coph.simplesql.adapter.DatabaseAdapter;
import dev.coph.simplesql.driver.DriverType;
import dev.coph.simplesql.query.providers.TableCreateQueryProvider;

import java.util.List;

public interface Test {
    String name();

    List<TableCreateQueryProvider> setup(DriverType driverType);

    boolean execute(DatabaseAdapter databaseAdapter);

}
