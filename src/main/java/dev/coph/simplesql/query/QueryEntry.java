package dev.coph.simplesql.query;


import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class QueryEntry {
    private static SimpleDateFormat DATE_TIME_CONVERTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static SimpleDateFormat DATE_CONVERTER = new SimpleDateFormat("yyyy-MM-dd");

    private String columName;

    private Object value;

    private boolean rawValue = false;


    public QueryEntry(String columName, Object value) {
        this.columName = columName;
        this.value = value;
    }


    public QueryEntry() {
    }


    public boolean rawValue() {
        return rawValue;
    }


    public QueryEntry rawValue(boolean rawValue) {
        this.rawValue = rawValue;
        return this;
    }


    public String columName() {
        return this.columName;
    }


    public Object value() {
        return this.value;
    }


    public QueryEntry columName(String columName) {
        this.columName = columName;
        return this;
    }


    public QueryEntry value(Object value) {
        this.value = value;
        return this;
    }
}
