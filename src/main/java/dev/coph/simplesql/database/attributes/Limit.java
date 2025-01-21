package dev.coph.simplesql.database.attributes;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Getter
@Setter
@Accessors(fluent = true, chain = true)
public class Limit {

    private int limit;

    @Override
    public String toString() {
        return " LIMIT " + limit;
    }
}
