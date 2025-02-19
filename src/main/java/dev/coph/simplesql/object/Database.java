package dev.coph.simplesql.object;

import dev.coph.simplesql.adapter.DatabaseAdapter;
import dev.coph.simplesql.database.attributes.CharacterSet;
import dev.coph.simplesql.database.attributes.CreateMethode;
import dev.coph.simplesql.database.attributes.DataType;
import dev.coph.simplesql.query.Query;
import dev.coph.simplesql.query.providers.DatabaseCreateQueryProvider;

import java.util.HashSet;

public class Database {
    private final DatabaseAdapter databaseAdapter;

    public Database(DatabaseAdapter databaseAdapter) {
        this.databaseAdapter = databaseAdapter;
    }

    private HashSet<String> tables = new HashSet<>();

    public QueueObject<Boolean> loadOrCreate(String name, CharacterSet characterSet){
        return new QueueObject<>(() -> {
            DatabaseCreateQueryProvider createDatabaseQuery = new DatabaseCreateQueryProvider().database(name).characterSet(characterSet).createMethode(CreateMethode.IF_NOT_EXISTS);
            return new Query(databaseAdapter).executeQuery(createDatabaseQuery).succeeded();
        });
    }

    public void drop(){

    }



}
