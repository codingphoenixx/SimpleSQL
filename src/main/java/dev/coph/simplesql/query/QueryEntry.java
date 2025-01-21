package dev.coph.simplesql.query;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(fluent = true)
@NoArgsConstructor
public class QueryEntry {
    private String columName;
    private Object value;

    public QueryEntry(String columName, Object value) {
        this.columName = columName;
        this.value = value;
    }

    public String sqlValue() {
        return parseSQLValue(value);
    }

    public static String parseSQLValue(Object value) {
        if (value != null && value instanceof Boolean bool) {
            if (bool) {
                return "'1'";
            } else {
                return "'0'";
            }
        }
        if (value != null && value instanceof Number number) {
            return number.toString();
        }
        return "'%s'".formatted(value);
    }
}
