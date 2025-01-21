package dev.coph.simplesql;

import dev.coph.simplesql.adapter.DatabaseAdapter;
import dev.coph.simplesql.database.Column;
import dev.coph.simplesql.database.attributes.DataType;
import dev.coph.simplesql.query.Query;
import dev.coph.simplesql.query.providers.InsertQueryProvider;
import dev.coph.simplesql.query.providers.TableCreateQueryProvider;

import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {

        startSQLLite();
    }


    private static void startMariaDB() {
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

    private static void startSQLLite() {
        File sqliteFile = new File("test.db");
        if (!sqliteFile.exists()) {
            try {
                sqliteFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        DatabaseAdapter databaseAdapter = new DatabaseAdapter.Builder()
                .driverType(DatabaseAdapter.DriverType.SQLITE)
                .sqliteFile(sqliteFile)
                .build();
        databaseAdapter.connect();

        var tableCreateQuery = Query.tableCreate()
                .table("test")
                .column("uuid", DataType.VARCHAR, 64, Column.ColumnType.PRIMARY_KEY)
                .column("comment", DataType.LONGTEXT)
                .createMethode(TableCreateQueryProvider.CreateMethode.IF_NOT_EXISTS);

        var insertQuery = Query.insert()
                .table("test")
                .insertMethode(InsertQueryProvider.InsertMethode.INSERT_IGNORE)
                .entry("uuid", "1234567890");


        new Query(databaseAdapter).queries(tableCreateQuery).execute();
        new Query(databaseAdapter).queries(insertQuery).execute();
    }
}
