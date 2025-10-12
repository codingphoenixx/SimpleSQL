package dev.coph.simplesql.query.providers;

import dev.coph.simplesql.database.attributes.*;
import dev.coph.simplesql.driver.DriverCompatibility;
import dev.coph.simplesql.driver.DriverType;
import dev.coph.simplesql.exception.FeatureNotSupportedException;
import dev.coph.simplesql.query.Query;
import dev.coph.simplesql.query.QueryProvider;
import dev.coph.simplesql.query.SimpleResultSet;
import dev.coph.simpleutilities.action.RunnableAction;
import dev.coph.simpleutilities.check.Check;

import java.sql.ResultSet;
import java.util.*;


public class SelectQueryProvider implements QueryProvider {

    private String table;

    private SelectFunction function = SelectFunction.NORMAL;
    private SelectType selectType = SelectType.NORMAL;

    private Order order;

    private LinkedHashSet<Condition> whereConditions = new LinkedHashSet<>();

    private List<String> columnKeys = new ArrayList<>();

    private Limit limit;

    public enum LockMode { FOR_UPDATE, FOR_SHARE, FOR_NO_KEY_UPDATE, FOR_KEY_SHARE }
    private LockMode lockMode;
    private boolean skipLocked;
    private boolean noWait;

    private Group group;


    private RunnableAction<SimpleResultSet> resultActionAfterQuery;
    private RunnableAction<Boolean> actionAfterQuery;

    private ResultSet resultSet;

    private List<Object> boundParams = List.of();

    @Override
    public DriverCompatibility compatibility() {
        return driverType -> true;
    }

    @Override
    public String generateSQLString(Query query) {
        if (columnKeys.isEmpty()) columnKeys.add("*");
        Check.ifNullOrEmptyMap(table, "table name");

        DriverType driver =
                query.databaseAdapter() != null ? query.databaseAdapter().driverType() : null;

        StringBuilder sql = new StringBuilder("SELECT ");
        List<Object> params = new ArrayList<>();

        // DISTINCT
        if (selectType == SelectType.DISTINCT) {
            sql.append("DISTINCT ");
        }

        // Function or columns
        if (function != null && function != SelectFunction.NORMAL) {
            String col = columnKeys.get(0);
            sql.append(function.name()).append("(").append(col).append(")");
        } else {
            sql.append(parseColumnNames());
        }

        sql.append(" FROM ").append(table);

        // WHERE
        if (whereConditions != null && !whereConditions.isEmpty()) {
            sql.append(" WHERE ");
            sql.append(buildConditions(whereConditions.iterator(), params));
        }

        // GROUP BY
        if (group != null && group.keys() != null && !group.keys().isEmpty()) {
            sql.append(" ").append(buildGroupBy(group));
        }

        // HAVING
        if (group != null && group.conditions() != null && !group.conditions().isEmpty()) {
            // If no GROUP BY keys but HAVING exists, standard SQL allows HAVING without GROUP BY
            // in some DBs it acts like WHERE on aggregates. We still permit it.
            if (group.keys() == null || group.keys().isEmpty()) {
                sql.append(" HAVING ");
            } else {
                sql.append(" HAVING ");
            }
            sql.append(buildConditions(group.conditions().iterator(), params));
        }

        // ORDER BY
        if (order != null && order.orderRules() != null && !order.orderRules().isEmpty()) {
            sql.append(order.toString(query));
        }

        // LIMIT/OFFSET
        int lim = (limit != null) ? limit.limit() : 0;
        int off = (limit != null) ? limit.offset() : 0;
        if (lim > 0 || off > 0) {
            appendLimitOffset(sql, driver, lim, off);
        }

        // Locking
        if (lockMode != null || skipLocked || noWait) {
            appendLocking(sql, driver);
        }

        sql.append(";");

        this.boundParams = List.copyOf(params);
        return sql.toString();
    }

    private String parseColumnNames() {
        if (columnKeys == null || columnKeys.isEmpty()) return "*";
        if (columnKeys.size() == 1) return columnKeys.get(0);
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (String c : columnKeys) {
            if (!first) sb.append(", ");
            sb.append(c);
            first = false;
        }
        return sb.toString();
    }

    private String buildGroupBy(Group group) {
        // Render "GROUP BY k1, k2"
        StringBuilder sb = new StringBuilder("GROUP BY ");
        boolean first = true;
        for (String k : group.keys()) {
            if (!first) sb.append(", ");
            sb.append(k);
            first = false;
        }
        return sb.toString();
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

            String column = c.key();
            Operator op = c.operator();
            Object value = c.value();

            switch (op) {
                case IS_NULL -> sb.append(column).append(" IS NULL");
                case IS_NOT_NULL -> sb.append(column).append(" IS NOT NULL");
                default -> {
                    sb.append(column).append(" ").append(op.operator()).append(" ?");
                    params.add(value);
                }
            }
            first = false;
        }
        return sb.toString();
    }

    private int appendIn(StringBuilder sb, String column, boolean not, Object value, List<Object> params) {
        Collection<?> coll = null;
        if (value instanceof Collection<?> c) {
            coll = c;
        } else if (value != null && value.getClass().isArray()) {
            int len = java.lang.reflect.Array.getLength(value);
            List<Object> list = new ArrayList<>(len);
            for (int i = 0; i < len; i++) list.add(java.lang.reflect.Array.get(value, i));
            coll = list;
        }
        if (coll == null || coll.isEmpty()) return 0;

        sb.append(column).append(not ? " NOT IN (" : " IN (");
        StringJoiner join = new StringJoiner(", ");
        for (Object v : coll) {
            join.add("?");
            params.add(v);
        }
        sb.append(join).append(")");
        return coll.size();
    }


    private void appendLimitOffset(StringBuilder sql, DriverType driver, int limit, int offset) {
        // Use ANSI-ish "LIMIT n OFFSET m" which is supported by PostgreSQL and SQLite.
        // MySQL/MariaDB support it in modern versions; if you must support very old MySQL, switch to "LIMIT m, n".
        if (limit > 0) {
            sql.append(" LIMIT ").append(limit);
            if (offset > 0) {
                sql.append(" OFFSET ").append(offset);
            }
        } else {
            // explicit offset without limit: treat as unsupported explicit request
            throw new FeatureNotSupportedException(driver);
        }
    }

    private void appendLocking(StringBuilder sql, DriverType driver) {
        // Only if user explicitly requested locking, enforce driver support
        switch (driver) {
            case MYSQL, MARIADB -> {
                if (lockMode == LockMode.FOR_UPDATE) {
                    sql.append(" FOR UPDATE");
                } else if (lockMode == LockMode.FOR_SHARE) {
                    // Prefer FOR SHARE (MySQL 8+/MariaDB) over deprecated "LOCK IN SHARE MODE"
                    sql.append(" FOR SHARE");
                } else if (lockMode == LockMode.FOR_NO_KEY_UPDATE || lockMode == LockMode.FOR_KEY_SHARE) {
                    throw new FeatureNotSupportedException(driver);
                }
                if (skipLocked) {
                    // Requires MySQL 8+/MariaDB 10.3+. Without version probing, be strict:
                    throw new FeatureNotSupportedException(driver);
                }
                if (noWait) {
                    // Also version/engine dependent in MySQL; throw to be safe
                    throw new FeatureNotSupportedException(driver);
                }
            }
            case POSTGRESQL -> {
                if (lockMode == null) return;
                switch (lockMode) {
                    case FOR_UPDATE -> sql.append(" FOR UPDATE");
                    case FOR_SHARE -> sql.append(" FOR SHARE");
                    case FOR_NO_KEY_UPDATE -> sql.append(" FOR NO KEY UPDATE");
                    case FOR_KEY_SHARE -> sql.append(" FOR KEY SHARE");
                }
                if (skipLocked) sql.append(" SKIP LOCKED");
                if (noWait) sql.append(" NOWAIT");
            }
            case SQLITE -> {
                // SQLite does not support SELECT ... FOR UPDATE
                throw new FeatureNotSupportedException(driver);
            }
            default -> throw new FeatureNotSupportedException(driver);
        }
    }

    // QueryProvider

    @Override
    public List<Object> parameters() {
        return boundParams != null ? boundParams : List.of();
    }

    @Override
    public RunnableAction<Boolean> actionAfterQuery() {
        return actionAfterQuery;
    }

    public SelectQueryProvider actionAfterQuery(RunnableAction<Boolean> actionAfterQuery) {
        this.actionAfterQuery = actionAfterQuery;
        return this;
    }

    // Fluent API

    public SelectQueryProvider table(String table) {
        this.table = table;
        return this;
    }

    public SelectQueryProvider function(SelectFunction function) {
        this.function = (function != null) ? function : SelectFunction.NORMAL;
        return this;
    }

    public SelectQueryProvider selectType(SelectType selectType) {
        this.selectType = (selectType != null) ? selectType : SelectType.NORMAL;
        return this;
    }

    public SelectQueryProvider order(Order order) {
        if (order == null) return this;
        if (this.order == null) this.order = order;
        else this.order.orderRules().putAll(order.orderRules());
        return this;
    }

    public SelectQueryProvider order(String key, Order.Direction direction) {
        if (this.order == null) this.order = new Order();
        this.order.rule(key, direction);
        return this;
    }

    public SelectQueryProvider condition(Condition condition) {
        if (condition != null) this.whereConditions.add(condition);
        return this;
    }

    public SelectQueryProvider condition(String column, Object value) {
        this.whereConditions.add(new Condition(column, value));
        return this;
    }

    public SelectQueryProvider condition(String column, Operator operator, Object value) {
        this.whereConditions.add(new Condition(column, operator, value));
        return this;
    }

    public SelectQueryProvider columnKey(String columnKey) {
        if (this.columnKeys == null) this.columnKeys = new ArrayList<>();
        this.columnKeys.add(columnKey);
        return this;
    }

    public SelectQueryProvider limit(int limit) {
        if (this.limit == null) this.limit = new Limit();
        this.limit.limit(limit);
        return this;
    }

    public SelectQueryProvider limit(int limit, int offset) {
        if (this.limit == null) this.limit = new Limit();
        this.limit.limit(limit);
        this.limit.offset(offset);
        return this;
    }

    public SelectQueryProvider group(Group group) {
        this.group = group;
        return this;
    }

    public SelectQueryProvider resultActionAfterQuery(RunnableAction<SimpleResultSet> resultActionAfterQuery) {
        this.resultActionAfterQuery = resultActionAfterQuery;
        return this;
    }

    public SelectQueryProvider resultSet(ResultSet resultSet) {
        this.resultSet = resultSet;
        return this;
    }

    // Locking options
    public SelectQueryProvider lock(LockMode mode) {
        this.lockMode = mode;
        return this;
    }

    public SelectQueryProvider skipLocked(boolean skip) {
        this.skipLocked = skip;
        return this;
    }

    public SelectQueryProvider noWait(boolean nw) {
        this.noWait = nw;
        return this;
    }

    // Getters

    public String table() { return this.table; }
    public SelectFunction function() { return this.function; }
    public SelectType selectType() { return this.selectType; }
    public Order order() { return this.order; }
    public Set<Condition> conditions() { return this.whereConditions; }
    public List<String> columnKey() { return this.columnKeys; }
    public Limit limit() { return this.limit; }
    public RunnableAction<SimpleResultSet> resultActionAfterQuery() { return this.resultActionAfterQuery; }
    public Group group() { return this.group; }
    public ResultSet resultSet() { return this.resultSet; }

    public SimpleResultSet simpleResultSet() {
        if (resultSet == null) throw new NullPointerException("ResultSet is null");
        return new SimpleResultSet(resultSet());
    }
}
