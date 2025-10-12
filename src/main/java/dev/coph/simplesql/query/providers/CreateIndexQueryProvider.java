package dev.coph.simplesql.query.providers;

import dev.coph.simplesql.database.attributes.Condition;
import dev.coph.simplesql.database.attributes.Operator;
import dev.coph.simplesql.database.attributes.Order;
import dev.coph.simplesql.driver.DriverCompatibility;
import dev.coph.simplesql.driver.DriverType;
import dev.coph.simplesql.exception.FeatureNotSupportedException;
import dev.coph.simplesql.query.Query;
import dev.coph.simplesql.query.QueryProvider;
import dev.coph.simplesql.utils.DatabaseCheck;
import dev.coph.simpleutilities.action.RunnableAction;
import dev.coph.simpleutilities.check.Check;

import java.util.*;


public class CreateIndexQueryProvider implements QueryProvider {

    private final List<IndexColumn> columns = new ArrayList<>();
    private final LinkedHashSet<Condition> where = new LinkedHashSet<>();
    private String table;
    private String indexName;
    private String tableSchema;
    private IndexType indexType = IndexType.NORMAL;
    private Method method;
    private boolean concurrently;
    private List<String> includeColumns;
    private RunnableAction<Boolean> actionAfterQuery;
    private List<Object> boundParams = List.of();

    @Override
    public String generateSQLString(Query query) {
        Check.ifNullOrEmptyMap(table, "table");
        Check.ifNullOrEmptyMap(indexName, "indexName");
        Check.ifNullOrEmptyMap(columns, "columns");

        DriverType driver = query.databaseAdapter() != null
                ? query.databaseAdapter().driverType()
                : null;

        DatabaseCheck.missingDriver(driver);

        List<Object> params = new ArrayList<>();
        StringBuilder sql = new StringBuilder("CREATE ");

        switch (indexType) {
            case NORMAL -> {
            }
            case UNIQUE -> sql.append("UNIQUE ");
            case FULLTEXT -> {
                if (driver != DriverType.MYSQL && driver != DriverType.MARIADB) {
                    throw new FeatureNotSupportedException(driver);
                }
                sql.append("FULLTEXT ");
            }
            case SPATIAL -> {
                if (driver != DriverType.MYSQL && driver != DriverType.MARIADB) {
                    throw new FeatureNotSupportedException(driver);
                }
                sql.append("SPATIAL ");
            }
        }

        if (concurrently) {
            if (driver == DriverType.POSTGRESQL) {
                sql.append("INDEX CONCURRENTLY ");
            } else {
                throw new FeatureNotSupportedException(driver);
            }
        } else {
            sql.append("INDEX ");
        }

        sql.append(indexName).append(" ON ");

        if (tableSchema != null && !tableSchema.isBlank()) {
            sql.append(tableSchema).append(".").append(table);
        } else {
            sql.append(table);
        }

        if (method != null) {
            switch (driver) {
                case POSTGRESQL -> sql.append(" USING ").append(method.name().toLowerCase());
                case MYSQL, MARIADB -> {
                    if (method == Method.BTREE || method == Method.HASH) {
                        sql.append(" USING ").append(method.name());
                    } else {
                        throw new FeatureNotSupportedException(driver);
                    }
                }
                case SQLITE -> {
                    throw new FeatureNotSupportedException(driver);
                }
                default -> throw new FeatureNotSupportedException(driver);
            }
        }

        sql.append(" (");
        boolean first = true;
        for (IndexColumn c : columns) {
            if (!first) sql.append(", ");
            sql.append(c.expressionOrColumn());
            if (c.length() != null) {
                if (driver == DriverType.MYSQL || driver == DriverType.MARIADB) {
                    sql.append("(").append(c.length()).append(")");
                } else {
                    throw new FeatureNotSupportedException(driver);
                }
            }
            if (c.direction() != null) {
                sql.append(c.direction().operator());
            }
            first = false;
        }
        sql.append(")");

        if (includeColumns != null && !includeColumns.isEmpty()) {
            if (driver == DriverType.POSTGRESQL) {
                sql.append(" INCLUDE (");
                for (int i = 0; i < includeColumns.size(); i++) {
                    if (i > 0) sql.append(", ");
                    sql.append(includeColumns.get(i));
                }
                sql.append(")");
            } else {
                throw new FeatureNotSupportedException(driver);
            }
        }

        if (!where.isEmpty()) {
            if (driver == DriverType.POSTGRESQL || driver == DriverType.SQLITE) {
                sql.append(" WHERE ").append(buildConditions(where.iterator(), params));
            } else {
                throw new FeatureNotSupportedException(driver);
            }
        }

        sql.append(";");

        this.boundParams = List.copyOf(params);
        return sql.toString();
    }

    private String buildConditions(Iterator<Condition> it, List<Object> params) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        while (it.hasNext()) {
            Condition c = it.next();
            if (!first) {
                sb.append(c.type() == Condition.Type.AND ? " AND " : " OR ");
            }
            if (c.not()) sb.append("NOT ");

            String col = c.key();
            Operator op = c.operator();
            Object value = c.value();

            switch (op) {
                case IS_NULL -> sb.append(col).append(" IS NULL");
                case IS_NOT_NULL -> sb.append(col).append(" IS NOT NULL");

                default -> {
                    sb.append(col).append(" ").append(toSqlOperator(op)).append(" ?");
                    params.add(value);
                }
            }
            first = false;
        }
        return sb.toString();
    }

    private String toSqlOperator(Operator op) {
        return switch (op) {
            case EQUALS -> "=";
            case NOT_EQUALS -> "<>";
            case GREATER_THAN -> ">";
            case GREATER_EQUALS -> ">=";
            case LESS_THAN -> "<";
            case LESS_EQUALS -> "<=";
            case IS_NULL, IS_NOT_NULL -> throw new IllegalStateException("NULL handled separately");
        };
    }

    @Override
    public List<Object> parameters() {
        return boundParams != null ? boundParams : List.of();
    }

    @Override
    public DriverCompatibility compatibility() {
        return driverType -> true;
    }

    @Override
    public RunnableAction<Boolean> actionAfterQuery() {
        return actionAfterQuery;
    }

    public CreateIndexQueryProvider actionAfterQuery(RunnableAction<Boolean> actionAfterQuery) {
        this.actionAfterQuery = actionAfterQuery;
        return this;
    }

    public CreateIndexQueryProvider table(String table) {
        this.table = table;
        return this;
    }

    public CreateIndexQueryProvider schema(String schema) {
        this.tableSchema = schema;
        return this;
    }

    public CreateIndexQueryProvider indexName(String indexName) {
        this.indexName = indexName;
        return this;
    }

    public CreateIndexQueryProvider indexType(IndexType type) {
        this.indexType = type != null ? type : IndexType.NORMAL;
        return this;
    }

    public CreateIndexQueryProvider method(Method method) {
        this.method = method;
        return this;
    }

    public CreateIndexQueryProvider addColumn(String exprOrColumn) {
        this.columns.add(new IndexColumn(exprOrColumn, null, null));
        return this;
    }

    public CreateIndexQueryProvider addColumn(String exprOrColumn, Order.Direction direction, Integer length) {
        this.columns.add(new IndexColumn(exprOrColumn, direction, length));
        return this;
    }

    public CreateIndexQueryProvider includeColumns(List<String> cols) {
        this.includeColumns = cols != null ? new ArrayList<>(cols) : null;
        return this;
    }

    public CreateIndexQueryProvider where(Condition condition) {
        if (condition != null) this.where.add(condition);
        return this;
    }

    public CreateIndexQueryProvider where(Collection<Condition> conditions) {
        if (conditions != null) this.where.addAll(conditions);
        return this;
    }

    public CreateIndexQueryProvider concurrently(boolean concurrently) {
        this.concurrently = concurrently;
        return this;
    }

    public enum IndexType {
        NORMAL, UNIQUE, FULLTEXT, SPATIAL
    }

    public enum Method {
        BTREE, HASH, GIN, GIST, BRIN
    }

    public static final class IndexColumn {
        private final String expressionOrColumn;
        private final Order.Direction ascending;
        private final Integer length;

        public IndexColumn(String expressionOrColumn, Order.Direction ascending, Integer length) {
            this.expressionOrColumn = Objects.requireNonNull(expressionOrColumn);
            this.ascending = ascending;
            this.length = length;
        }

        public String expressionOrColumn() {
            return expressionOrColumn;
        }

        public Order.Direction direction() {
            return ascending;
        }

        public Integer length() {
            return length;
        }
    }
}
