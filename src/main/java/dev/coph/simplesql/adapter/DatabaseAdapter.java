package dev.coph.simplesql.adapter;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.coph.simplesql.exception.DriverNotLoadedException;
import dev.coph.simplesql.utils.Check;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
public class DatabaseAdapter {
    private final DriverType driverType;
    private final HikariConfig hikariConfig;
    @Getter
    private HikariDataSource dataSource;

    @Getter
    boolean connected = false;

    private DatabaseAdapter(DriverType driverType, String host, int port, String database, String user, String password) {
        this.connected = false;
        this.driverType = driverType;
        this.hikariConfig = new HikariConfig();

        this.hikariConfig.setJdbcUrl("jdbc:" + driverType.name().toLowerCase() + "://" + host + ":" + port + "/" + database + "?autoReconnect=true");
        this.hikariConfig.setUsername(user);
        this.hikariConfig.setPassword(password);
        this.hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
        this.hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
        this.hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        this.hikariConfig.setMaxLifetime(540000);
    }


    public DatabaseAdapter connect() {
        this.connected = false;
        try {
            Class.forName(driverType.driver());
        } catch (ClassNotFoundException e) {
            throw new DriverNotLoadedException(e.getCause());
        }
        this.dataSource = new HikariDataSource(this.hikariConfig);
        this.connected = true;
        return this;
    }



    @Getter
    @Setter
    @Accessors(fluent = true, chain = true)
    public static class Builder {
        private DriverType driverType;
        private String host;
        private int port = 3306;
        private String database;
        private String user;
        private String password;

        public DatabaseAdapter build() {
            Check.ifNull(driverType, "drivertype");
            Check.ifNull(host, "host");
            Check.ifNull(database, "database");
            Check.ifNull(user, "user");
            Check.ifNull(password, "password");
            return new DatabaseAdapter(driverType, host, port, database, user, password);
        }
    }

    @Getter
    @Accessors(fluent = true)
    public enum DriverType {
        MYSQL("com.mysql.cj.jdbc.Driver"),
        MARIADB("org.mariadb.jdbc.Driver");

        private final String driver;

        DriverType(String driver) {
            this.driver = driver;
        }
    }
}
