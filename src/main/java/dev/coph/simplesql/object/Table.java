package dev.coph.simplesql.object;

import dev.coph.simplesql.database.attributes.Condition;
import dev.coph.simplesql.database.attributes.InsertMethode;
import dev.coph.simplesql.query.Query;
import dev.coph.simplesql.query.providers.DeleteQueryProvider;
import dev.coph.simplesql.query.providers.InsertQueryProvider;
import dev.coph.simplesql.query.providers.SelectQueryProvider;
import dev.coph.simpleutilities.cache.CachingList;
import dev.coph.simpleutilities.time.Time;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Getter
@Accessors(fluent = true)
public class Table {

    private final Database parent;
    private final String name;


    CachingList<Map<String, Object>> tableData = new CachingList<>(200, new Time(5, TimeUnit.SECONDS));


    public Table(Database parent, String name) {
        this.parent = parent;
        this.name = name;
    }

    private HashMap<String, Object> databaseStructure = new HashMap<>();


    public QueueObject<Boolean> insert(HashMap<String, Object> data) {
        return new QueueObject<>(() -> {
            var insertQuery = new InsertQueryProvider()
                    .table(name)
                    .insertMethode(InsertMethode.INSERT_OR_UPDATE);
            data.forEach(insertQuery::entry);
            return new Query(parent.databaseAdapter()).executeQuery(insertQuery).executed();
        });
    }

    public QueueObject<Boolean> deleteWhere(Condition condition) {
        return new QueueObject<>(() -> {
            var deleteQuery = new DeleteQueryProvider()
                    .table(name)
                    .condition(condition);
            return new Query(parent.databaseAdapter()).executeQuery(deleteQuery).executed();
        });
    }

    public QueueObject<ArrayList<Map<String, Object>>> get() {
        return new QueueObject<>(() -> {
            SelectQueryProvider selectQuery = new SelectQueryProvider()
                    .table(name);
            ArrayList<Map<String, Object>> queryResult = new ArrayList<>();

            selectQuery.actionAfterQuery(resultSet -> {
                if (resultSet == null) {
                    return;
                }
                while (resultSet.next()) {
                    Map<String, Object> row = new HashMap<>();
                    var metaData = resultSet.getMetaData();
                    for (int i = 1; i <= metaData.getColumnCount(); i++) {
                        row.put(metaData.getColumnName(i), resultSet.getObject(i));
                    }
                    queryResult.add(row);
                }
            });
            new Query(parent.databaseAdapter()).executeQuery(selectQuery);
            tableData.addAll(queryResult);
            return queryResult;
        });
    }
}
