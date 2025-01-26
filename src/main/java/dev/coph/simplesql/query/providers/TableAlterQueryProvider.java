package dev.coph.simplesql.query.providers;

import dev.coph.simplesql.query.Query;
import dev.coph.simplesql.query.QueryProvider;
import dev.coph.simplesql.utils.Check;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Abstract class representing a provider for generating SQL "ALTER TABLE" queries.
 * Subclasses are expected to implement the logic for constructing specific "ALTER TABLE"
 * operations such as adding a column, renaming a table, or modifying attributes.
 * This class provides a base structure with shared functionality for table alterations.
 */
@Getter
@Accessors(fluent = true, chain = true)
public abstract class TableAlterQueryProvider implements QueryProvider {

    @Setter
    protected String table;

    public abstract String getAlterTableString(Query query);

    @Override
    public String generateSQLString(Query query) {
        Check.ifNull(table, "table name");
        return "ALTER TABLE " + table + " " + getAlterTableString(query) + ";";
    }

}
