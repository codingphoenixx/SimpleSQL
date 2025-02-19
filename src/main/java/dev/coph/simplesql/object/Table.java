package dev.coph.simplesql.object;

import dev.coph.simplesql.database.Column;
import lombok.Getter;
import lombok.experimental.Accessors;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

@Getter
@Accessors(fluent = true)
public class Table {

    private final Database parent;
    private final String name;

    public Table(Database parent, String name) {
        this.parent = parent;
        this.name = name;
    }

    private HashMap<String, Object> databaseStructure = new HashMap<>();





}
