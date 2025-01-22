package dev.coph.simplesql.query.providers;

import dev.coph.simplesql.database.attributes.Condition;
import dev.coph.simplesql.database.attributes.Limit;
import dev.coph.simplesql.database.attributes.UpdatePriority;
import dev.coph.simplesql.query.Query;
import dev.coph.simplesql.query.QueryEntry;
import dev.coph.simplesql.query.QueryProvider;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Set;


@Getter
@Accessors(fluent = true)
public class UpdateQueryProvider implements QueryProvider {
    @Setter
    private String table;
    @Setter
    private UpdatePriority updatePriority = UpdatePriority.NORMAL;
    @Setter
    private boolean updateIgnore = false;
    private List<QueryEntry> entries;
    private Set<Condition> conditions;
    private Limit limit;

    @Override
    public String generateSQLString(Query query) {
        return "";
    }
}
