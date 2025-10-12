package dev.coph.simplesql.query.providers;

import dev.coph.simplesql.database.attributes.*;
import dev.coph.simplesql.driver.DriverCompatibility;
import dev.coph.simplesql.driver.DriverType;
import dev.coph.simplesql.exception.FeatureNotSupportedException;
import dev.coph.simplesql.query.Query;
import dev.coph.simplesql.query.QueryEntry;
import dev.coph.simplesql.query.QueryProvider;
import dev.coph.simplesql.utils.DatabaseCheck;
import dev.coph.simpleutilities.action.RunnableAction;
import dev.coph.simpleutilities.check.Check;

import java.util.*;


public class UpdateQueryProvider implements QueryProvider {

    private String table;
    private UpdatePriority updatePriority = UpdatePriority.NORMAL;
    private boolean updateIgnore = false;
    private List<QueryEntry> entries;
    private List<Object> boundParams = List.of();

    private LinkedHashSet<Condition> conditions;
    private Order order;
    private Limit limit;
    private RunnableAction<Boolean> actionAfterQuery;


    public UpdateQueryProvider order(Order order) {
        if (this.order == null)
            this.order = order;
        else
            this.order.orderRules().putAll(order.orderRules());
        return this;
    }

    public UpdateQueryProvider order(String key, Order.Direction direction) {
        if (this.order == null)
            this.order = new Order();
        order.rule(key, direction);
        return this;
    }

    public UpdateQueryProvider limit(int limit) {
        if (this.limit == null)
            this.limit = new Limit();

        this.limit.limit(limit);
        return this;
    }


    public UpdateQueryProvider limit(int limit, int offset) {
        if (this.limit == null)
            this.limit = new Limit();

        this.limit.limit(limit);
        this.limit.offset(offset);
        return this;
    }


    public UpdateQueryProvider condition(Condition condition) {
        if (conditions == null)
            conditions = new LinkedHashSet<>();

        conditions.add(condition);
        return this;
    }


    public UpdateQueryProvider condition(String column, Object value) {
        if (conditions == null)
            conditions = new LinkedHashSet<>();

        conditions.add(new Condition(column, value));
        return this;
    }


    public UpdateQueryProvider condition(String column, Operator operator, Object value) {
        if (conditions == null)
            conditions = new LinkedHashSet<>();

        conditions.add(new Condition(column, operator, value));
        return this;
    }


    public UpdateQueryProvider entry(String column, Object value) {
        if (entries == null)
            entries = new ArrayList<>();

        entries.add(new QueryEntry(column, value));
        return this;
    }


    public UpdateQueryProvider entry(QueryEntry entry) {
        if (entries == null)
            entries = new ArrayList<>();

        entries.add(entry);
        return this;
    }

    @Override
    public DriverCompatibility compatibility() {
        return driverType -> true;
    }


    @Override
    public String generateSQLString(Query query) {
        Check.ifNullOrEmptyMap(table, "table name");
        Check.ifNullOrEmptyMap(entries, "entries");

        DriverType driver = query.databaseAdapter() != null
                ? query.databaseAdapter().driverType()
                : null;

        List<Object> params = new ArrayList<>();
        StringBuilder sql = new StringBuilder();

        sql.append("UPDATE");

        if (updatePriority == UpdatePriority.LOW) {
            DatabaseCheck.requireDriver(driver, DriverType.MYSQL, DriverType.MARIADB);
            sql.append(" LOW_PRIORITY");
        }

        if (updateIgnore) {
            DatabaseCheck.requireDriver(driver, DriverType.MYSQL, DriverType.MARIADB);
            sql.append(" IGNORE");
        }

        sql.append(" ").append(table).append(" SET ");

        StringJoiner setJoin = new StringJoiner(", ");
        for (QueryEntry e : entries) {
            setJoin.add(e.columName() + " = ?");
            params.add(e.value());
        }
        sql.append(setJoin);

        if (conditions != null && !conditions.isEmpty()) {
            sql.append(" WHERE ");
            String whereFragment = buildWhereAndBind(conditions.iterator(), params);
            sql.append(whereFragment);
        }

        if (order != null && order.orderRules() != null && !order.orderRules().isEmpty()) {
            DatabaseCheck.requireDriver(driver, DriverType.MYSQL, DriverType.MARIADB);
            sql.append(order.toString(query));
        }

        if (limit != null && limit.limit() > 0) {
            DatabaseCheck.requireDriver(driver, DriverType.MYSQL, DriverType.MARIADB);

            if (limit.offset() > 0) {
                if (order == null || order.orderRules() == null || order.orderRules().isEmpty()) {
                    throw new FeatureNotSupportedException(driver);
                }
                sql.append(" LIMIT ").append(limit.offset()).append(", ").append(limit.limit());
            } else {
                sql.append(" LIMIT ").append(limit.limit());
            }
        } else if (limit != null && limit.offset() > 0) {
            DatabaseCheck.requireDriver(driver, DriverType.MYSQL, DriverType.MARIADB);
            throw new FeatureNotSupportedException(driver);
        }

        sql.append(";");
        this.boundParams = List.copyOf(params);
        return sql.toString();
    }

    private String buildWhereAndBind(Iterator<Condition> it, List<Object> params) {
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

    public String table() {
        return this.table;
    }


    public UpdatePriority updatePriority() {
        return this.updatePriority;
    }


    public boolean updateIgnore() {
        return this.updateIgnore;
    }


    public List<QueryEntry> entries() {
        return this.entries;
    }


    public Set<Condition> conditions() {
        return this.conditions;
    }


    public Limit limit() {
        return this.limit;
    }


    public UpdateQueryProvider table(String table) {
        this.table = table;
        return this;
    }


    public UpdateQueryProvider updatePriority(UpdatePriority updatePriority) {
        this.updatePriority = updatePriority;
        return this;
    }


    public UpdateQueryProvider updateIgnore(boolean updateIgnore) {
        this.updateIgnore = updateIgnore;
        return this;
    }

    public UpdateQueryProvider actionAfterQuery(RunnableAction<Boolean> actionAfterQuery) {
        this.actionAfterQuery = actionAfterQuery;
        return this;
    }

    @Override
    public RunnableAction<Boolean> actionAfterQuery() {
        return actionAfterQuery;
    }
}
