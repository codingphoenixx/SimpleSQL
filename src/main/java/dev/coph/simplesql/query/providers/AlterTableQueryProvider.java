package dev.coph.simplesql.query.providers;

import dev.coph.simplesql.query.Query;
import dev.coph.simplesql.query.QueryProvider;
import dev.coph.simplesql.utils.Check;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Getter
@Accessors(fluent = true, chain = true)
public abstract class AlterTableQueryProvider implements QueryProvider {

    @Setter
    protected String table;

    public abstract String getAlterTableString(Query query);

    @Override
    public String generateSQLString(Query query) {
        Check.ifNull(table, "table name");
        return "ALTER TABLE " + table + " " + getAlterTableString(query) + ";";
    }

    //TODO:
}
