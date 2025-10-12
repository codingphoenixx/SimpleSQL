package dev.coph.simplesql.query.providers;

import dev.coph.simplesql.database.attributes.Condition;
import dev.coph.simplesql.database.attributes.Limit;
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


public class DeleteQueryProvider implements QueryProvider {

    private String table;
    private Order order;
    private LinkedHashSet<Condition> conditions;
    private Limit limit;

    private RunnableAction<Boolean> actionAfterQuery;

    private List<Object> boundParams = List.of();

    @Override
    public DriverCompatibility compatibility() {
        return driverType -> true;
    }

    @Override
    public String generateSQLString(Query query) {
        Check.ifNullOrEmptyMap(table, "table name");

        DriverType driver = query.databaseAdapter() != null
                ? query.databaseAdapter().driverType()
                : null;

        StringBuilder sql = new StringBuilder("DELETE FROM ").append(table);

        List<Object> params = new ArrayList<>();

        if (conditions != null && !conditions.isEmpty()) {
            sql.append(" WHERE ");
            sql.append(buildConditions(conditions.iterator(), params));
        }

        if (order != null && order.orderRules() != null && !order.orderRules().isEmpty()) {
            DatabaseCheck.requireDriver(driver, DriverType.MYSQL, DriverType.MARIADB);
            sql.append(order.toString(query));
        }

        if (limit != null) {
            DatabaseCheck.requireDriver(driver, DriverType.MYSQL, DriverType.MARIADB);

            int lim = limit.limit();
            int off = limit.offset();

            if (off > 0) {
                if (lim <= 0) {
                    throw new FeatureNotSupportedException(driver);
                }
                if (order == null || order.orderRules() == null || order.orderRules().isEmpty()) {
                    throw new FeatureNotSupportedException(driver);
                }
                sql.append(" LIMIT ").append(off).append(", ").append(lim);
            } else if (lim > 0) {
                sql.append(" LIMIT ").append(lim);
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


    @Override
    public List<Object> parameters() {
        return boundParams != null ? boundParams : List.of();
    }

    @Override
    public RunnableAction<Boolean> actionAfterQuery() {
        return actionAfterQuery;
    }

    public DeleteQueryProvider actionAfterQuery(RunnableAction<Boolean> actionAfterQuery) {
        this.actionAfterQuery = actionAfterQuery;
        return this;
    }


    public DeleteQueryProvider table(String table) {
        this.table = table;
        return this;
    }

    public DeleteQueryProvider limit(int limit) {
        if (this.limit == null) this.limit = new Limit();
        this.limit.limit(limit);
        return this;
    }

    public DeleteQueryProvider limit(int limit, int offset) {
        if (this.limit == null) this.limit = new Limit();
        this.limit.limit(limit);
        this.limit.offset(offset);
        return this;
    }

    public DeleteQueryProvider orderBy(String key) {
        if (order == null) order = new Order();
        order.rule(key, Order.Direction.ASCENDING);
        return this;
    }

    public DeleteQueryProvider orderBy(String key, Order.Direction direction) {
        if (order == null) order = new Order();
        order.rule(key, direction);
        return this;
    }

    public DeleteQueryProvider condition(Condition condition) {
        if (conditions == null) conditions = new LinkedHashSet<>();
        conditions.add(condition);
        return this;
    }

    public DeleteQueryProvider condition(String key, Object value) {
        if (conditions == null) conditions = new LinkedHashSet<>();
        conditions.add(new Condition(key, value));
        return this;
    }

    public DeleteQueryProvider condition(String key, Operator operator, Object value) {
        if (conditions == null) conditions = new LinkedHashSet<>();
        conditions.add(new Condition(key, operator, value));
        return this;
    }


    public String table() { return this.table; }

    public Order order() { return this.order; }

    public Set<Condition> conditions() { return this.conditions; }

    public Limit limit() { return this.limit; }
}
