package dev.coph.simplesql.database.attributes;

import dev.coph.simplesql.query.Query;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.HashMap;

@Getter
@Accessors(chain = true, fluent = true)
@NoArgsConstructor
public class Order {
    private HashMap<String, Direction> orderRules;

    /**
     * @param key
     * @param direction
     * @return
     */
    public Order rule(String key, Direction direction) {
        if (orderRules == null)
            orderRules = new HashMap<>();
        orderRules.put(key, direction);
        return this;
    }

    public String toString(Query query) {
        if (orderRules == null || orderRules.isEmpty()) {
            return "";
        }
        if (orderRules.size() == 1) {
            String key = orderRules.keySet().toArray(new String[]{})[0];
            return " ORDER BY " + key + " " + orderRules.get(key).operator();
        }
        StringBuilder orderCommand = null;
        for (String key : orderRules.keySet()) {
            if (orderCommand == null) {
                orderCommand = new StringBuilder(" ORDER BY " + key + " " + orderRules.get(key).operator());
                continue;
            }
            orderCommand.append(", ").append(key).append(" ").append(orderRules.get(key).operator());
        }
        return orderCommand.toString();
    }

    public enum Direction {
        /**
         * Sort by the biggest first than the lowest. Z to A
         */
        DESCENDING("DESC"),
        /**
         * Sort by the smallest first than the highest. A to Z
         */
        ASCENDING("ASC"),
        ;

        @Getter
        @Accessors(fluent = true)
        private final String operator;

        Direction(String operator) {
            this.operator = operator;
        }
    }
}
