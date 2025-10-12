package dev.coph.simplesql.database.attributes;

import lombok.experimental.Accessors;


public class Limit {

    private int limit;
    private int offset = 0;


    public int limit() {
        return this.limit;
    }


    public int offset() {
        return this.offset;
    }


    public Limit limit(int limit) {
        this.limit = limit;
        return this;
    }


    public Limit offset(int offset) {
        this.offset = offset;
        return this;
    }

}
