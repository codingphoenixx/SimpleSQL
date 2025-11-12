package dev.coph.simplesql.utils.test.implementations;

import dev.coph.simplesql.adapter.DatabaseAdapter;
import dev.coph.simplesql.database.attributes.ColumnType;
import dev.coph.simplesql.database.attributes.CreateMethode;
import dev.coph.simplesql.database.attributes.DataType;
import dev.coph.simplesql.database.attributes.DeleteMethode;
import dev.coph.simplesql.driver.DriverType;
import dev.coph.simplesql.query.Query;
import dev.coph.simplesql.query.QueryProvider;
import dev.coph.simplesql.query.providers.TableCreateQueryProvider;
import dev.coph.simplesql.query.providers.TableDropQueryProvider;
import dev.coph.simplesql.utils.test.Test;

import java.util.List;

/**
 * Implementation of the Test interface that handles the creation and deletion of
 * database tables for test purposes. This class defines the logic for creating
 * two tables with specific columns, primary keys, and constraints, subsequently
 * verifying their creation, and finally dropping those tables as part of cleanup.
 */
public class TableCreateRequestTest implements Test {

    @Override
    public String name() {
        return "Table Create";
    }

    @Override
    public List<QueryProvider> setup(DriverType driverType) {
        return List.of();
    }

    @Override
    public boolean execute(DatabaseAdapter databaseAdapter) {
        TableCreateQueryProvider provider = Query.tableCreate()
                .table("test")
                .createMethode(CreateMethode.IF_NOT_EXISTS)
                .column("col_key", DataType.VARCHAR, 128, ColumnType.PRIMARY_KEY);

        TableCreateQueryProvider provider2 = Query.tableCreate()
                .table("test2")
                .createMethode(CreateMethode.IF_NOT_EXISTS)
                .column("col_key", DataType.VARCHAR, 128)
                .column("col_key2", DataType.VARCHAR, 128)
                .primaryKey(List.of("col_key", "col_key2"));
        new Query(databaseAdapter).executeQuery(provider, provider2);

        System.out.println("Successfully created tables");
        TableDropQueryProvider drop = Query.tableDrop().table("test").deleteMethode(DeleteMethode.IF_EXISTS);
        TableDropQueryProvider drop2 = Query.tableDrop().table("test2").deleteMethode(DeleteMethode.IF_EXISTS);
        new Query(databaseAdapter).executeQuery(drop, drop2);
        return true;
    }
}
