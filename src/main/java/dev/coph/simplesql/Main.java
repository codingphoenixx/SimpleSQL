package dev.coph.simplesql;

import dev.coph.simplesql.adapter.DatabaseAdapter;
import dev.coph.simplesql.database.Column;
import dev.coph.simplesql.database.DataType;
import dev.coph.simplesql.query.Query;
import dev.coph.simplesql.query.providers.InsertQueryProvider;
import dev.coph.simplesql.query.providers.TableCreateQueryProvider;

public class Main {
    public static void main(String[] args) {
        DatabaseAdapter databaseAdapter = new DatabaseAdapter.Builder()
                .host("localhost")
                .port(3306)
                .database("testing")
                .user("root")
                .password("")
                .driverType(DatabaseAdapter.DriverType.MARIADB)
                .build();
        databaseAdapter.connect();

        var tableCreateQuery = Query.tableCreate()
                .table("test")
                .column("uuid", DataType.VARCHAR, 64, Column.ColumnType.PRIMARY_KEY)
                .column("comment", DataType.LONGTEXT);

        var insertQuery = Query.insert()
                .table("test")
                .insertMethode(InsertQueryProvider.InsertMethode.INSERT_IGNORE)
                .entry("uuid", "1234567890");



        new Query(databaseAdapter).queries(tableCreateQuery).execute();
        new Query(databaseAdapter).queries(insertQuery).execute();

    }

}
