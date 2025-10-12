package dev.coph.simplesql.database.attributes;

import dev.coph.simplesql.query.Query;

import java.util.HashMap;


public class Order {

    private HashMap<String, Direction> orderRules;


    public Order() {
    }


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


    public HashMap<String, Direction> orderRules() {
        return this.orderRules;
    }


    public enum Direction {

        DESCENDING("DESC"),

        ASCENDING("ASC");


        private final String operator;


        Direction(String operator) {
            this.operator = operator;
        }


        public String operator() {
            return this.operator;
        }
    }
}
