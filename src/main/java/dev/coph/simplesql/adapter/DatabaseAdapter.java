package dev.coph.simplesql.adapter;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.coph.simplesql.exception.DriverNotLoadedException;
import dev.coph.simpleutilities.check.Check;
import lombok.experimental.Accessors;

import java.io.File;

/**
 * A class responsible for managing database connections using HikariCP and supporting MySQL, MariaDB, and SQLite.
 * The database connection can be configured and managed using the Builder pattern.
 */
public class DatabaseAdapter {
    /**
     * Represents the type of database driver used by the DatabaseAdapter.
     * This variable is immutable and must be specified when initializing the DatabaseAdapter.
     * <p>
     * The driverType determines the JDBC driver class name that will be used to establish
     * a connection to the specified database, such as MySQL, MariaDB, or SQLite.
     */
    private final DriverType driverType;
    /**
     * Represents the configuration for the connection pool using HikariCP.
     * This variable holds the {@link HikariConfig} object, which is utilized to set up
     * and manage the database connection pool with properties such as maximum connections,
     * connection timeouts, and other parameters specific to the HikariCP library.
     */
    private final HikariConfig hikariConfig;
    /**
     * A private instance of HikariDataSource used as the data source for managing
     * database connections in the application. This data source provides connection
     * pooling to optimize resource usage and improve performance when interacting with
     * the database.
     * <p>
     * This field is initialized and managed internally within the DatabaseAdapter class
     * and is configured based on the database connection parameters provided to the
     * adapter. It facilitates interactions with the database by providing efficient and
     * reusable connections.
     */
    private HikariDataSource dataSource;
    /**
     * Indicates whether the database connection has been successfully established.
     * The value is {@code true} if the database is connected and {@code false} otherwise.
     * Used internally to track the current state of the connection.
     */
    boolean connected = false;

    /**
     * Constructs a new instance of the DatabaseAdapter class, configuring the JDBC connection parameters
     * and properties based on the provided driver type, host, port, database name, credentials, or SQLite file path.
     *
     * @param driverType the type of database driver to be used (e.g., MYSQL, MARIADB, SQLITE)
     * @param host       the hostname or IP address of the database server (ignored for SQLITE)
     * @param port       the port on which the database server is running (ignored for SQLITE)
     * @param database   the name of the database to connect to (ignored for SQLITE)
     * @param user       the username for database authentication (ignored for SQLITE)
     * @param password   the password for database authentication (ignored for SQLITE)
     * @param sqliteFile the SQLite database file to connect to (used only for SQLITE driver type)
     */
    private DatabaseAdapter(DriverType driverType, String host, int port, String database, String user, String password, File sqliteFile) {
        this.connected = false;
        this.driverType = driverType;
        this.hikariConfig = new HikariConfig();

        if (driverType == DriverType.MYSQL || driverType == DriverType.MARIADB) {
            this.hikariConfig.setJdbcUrl("jdbc:" + driverType.name().toLowerCase() + "://" + host + ":" + port + "/" + database + "?autoReconnect=true");
            this.hikariConfig.setUsername(user);
            this.hikariConfig.setPassword(password);
            this.hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
            this.hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
            this.hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            this.hikariConfig.setMaxLifetime(540000);
            this.hikariConfig.setIdleTimeout(600000);
        } else if (driverType == DriverType.SQLITE) {
            this.hikariConfig.setJdbcUrl("jdbc:" + driverType.name().toLowerCase() + ":" + sqliteFile.getAbsolutePath());
            this.hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
            this.hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
            this.hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            this.hikariConfig.setMaxLifetime(540000);
            this.hikariConfig.setIdleTimeout(600000);
        }

        this.hikariConfig.setDriverClassName(driverType.driver());
    }

    /**
     * Establishes a connection to the database by loading the appropriate driver and configuring the data source.
     * This method initializes the connection settings using the defined `DriverType` and creates a data source
     * instance for database interactions. If the driver class cannot be loaded, a {@code DriverNotLoadedException}
     * is thrown.
     *
     * @return the current instance of {@code DatabaseAdapter} after successfully establishing the connection
     */
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

    /**
     * Retrieves the driver type used by this database adapter instance.
     *
     * @return the {@code DriverType} associated with the database adapter, indicating the type of database driver
     * being utilized (e.g., MYSQL, MARIADB, SQLITE).
     */
    public DriverType driverType() {
        return this.driverType;
    }

    /**
     * Retrieves the data source associated with the database adapter, which is used for establishing
     * and managing database connections.
     *
     * @return an instance of {@code HikariDataSource} configured for use with the current database adapter
     */
    public HikariDataSource dataSource() {
        return this.dataSource;
    }

    /**
     * Checks whether the database connection is currently established.
     *
     * @return true if the connection to the database is active and operational; false otherwise
     */
    public boolean connected() {
        return this.connected;
    }


    /**
     * Builder class for constructing instances of {@link DatabaseAdapter} with customizable
     * configuration options. This builder enables a fluent interface for setting database
     * connection properties such as driver type, host, port, database name, user credentials,
     * and SQLite file paths.
     * <p>
     * The {@link Builder#build()} method validates the provided configuration and constructs a
     * new instance of {@link DatabaseAdapter}. Validation depends on the selected driver type
     * and ensures all required properties are set for successful database connection.
     */
    public static class Builder {
        /**
         * Specifies the type of database driver to be used for establishing a connection.
         * The value of this variable determines the configuration requirements and behavior
         * of the database connection. Supported driver types include:
         * <br><br>
         * - MYSQL: Represents MySQL database connections and uses the `com.mysql.cj.jdbc.Driver` driver.<br>
         * - MARIADB: Represents MariaDB database connections and uses the `org.mariadb.jdbc.Driver` driver.<br>
         * - SQLITE: Represents SQLite database connections and uses the `org.sqlite.JDBC` driver.<br>
         * <br>
         * The selected driver type impacts the required configuration properties. For example,
         * MYSQL and MARIADB require host, port, database, user, and password properties to be set,
         * while SQLITE requires a file path to the SQLite database file.
         */
        private DriverType driverType;
        /**
         * Represents the hostname or IP address of the database server.
         * Used to configure the connection to the target database in the builder process.
         */
        private String host;
        /**
         * Represents the port number for the database connection.
         * Defaults to 3306, which is the standard port for MySQL databases.
         */
        private int port = 3306;
        /**
         * Specifies the name of the database to connect to.
         * This field is part of the Builder class used to configure and
         * construct a database connection object.
         */
        private String database;
        /**
         * Represents the username to access the database in the Builder configuration.
         * It is used to authenticate the connection to the specified database.
         */
        private String user;
        /**
         * The password used to authenticate the database connection.
         */
        private String password;
        /**
         * Represents the SQLite file used for database storage.
         * This field is utilized when the selected driver type is specific to SQLite.
         * It holds the reference to the file location where the SQLite database resides.
         */
        private File sqliteFile;

        /**
         * Builds and returns a new instance of {@code DatabaseAdapter} based on the parameters configured in the Builder.
         * Validates the necessary parameters for the selected database driver type before creating the adapter instance.
         *
         * @return a new instance of {@code DatabaseAdapter} configured with the provided parameters.
         * @throws NullPointerException if required parameters are null or invalid based on the driver type.
         */
        public DatabaseAdapter build() {
            Check.ifNull(driverType, "drivertype");
            if (driverType == DriverType.MYSQL || driverType == DriverType.MARIADB) {
                Check.ifNull(host, "host");
                Check.ifNull(database, "database");
                Check.ifNull(user, "user");
                Check.ifNull(password, "password");
            } else if (driverType == DriverType.SQLITE) {
                Check.ifNullOrNotExits(sqliteFile, "sql-file");
            }
            return new DatabaseAdapter(driverType, host, port, database, user, password, sqliteFile);
        }

        /**
         * Retrieves the currently configured database driver type.
         *
         * @return the {@code DriverType} that is currently set in the {@code Builder}.
         */
        public DriverType driverType() {
            return this.driverType;
        }

        /**
         * Retrieves the currently configured host for the database connection.
         *
         * @return the host address as a {@code String}.
         */
        public String host() {
            return this.host;
        }

        /**
         * Retrieves the currently configured port for the database connection.
         *
         * @return the port number as an integer.
         */
        public int port() {
            return this.port;
        }

        /**
         * Retrieves the currently configured database name for the connection.
         *
         * @return the database name as a {@code String}.
         */
        public String database() {
            return this.database;
        }

        /**
         * Retrieves the currently configured username for the database connection.
         *
         * @return the username as a {@code String}.
         */
        public String user() {
            return this.user;
        }

        /**
         * Retrieves the currently configured password for the database connection.
         *
         * @return the password as a {@code String}.
         */
        public String password() {
            return this.password;
        }

        /**
         * Retrieves the currently configured SQLite database file.
         *
         * @return the SQLite file as a {@code File} object.
         */
        public File sqliteFile() {
            return this.sqliteFile;
        }

        /**
         * Configures the database driver type to be used by the {@code Builder}.
         *
         * @param driverType the {@code DriverType} to set, which specifies the driver implementation
         *                   for the database connection (e.g., MySQL, MariaDB, SQLite).
         * @return the {@code Builder} instance, allowing for method chaining.
         */
        public Builder driverType(DriverType driverType) {
            this.driverType = driverType;
            return this;
        }

        /**
         * Configures the host address for the database connection in the {@code Builder}.
         *
         * @param host the host address as a {@code String}. This specifies the hostname or
         *             IP address of the database server to connect to.
         * @return the {@code Builder} instance, allowing for method chaining.
         */
        public Builder host(String host) {
            this.host = host;
            return this;
        }

        /**
         * Configures the port number for the database connection in the {@code Builder}.
         *
         * @param port the port number as an integer, specifying the port the database server is
         *             listening on.
         * @return the {@code Builder} instance, allowing for method chaining.
         */
        public Builder port(int port) {
            this.port = port;
            return this;
        }

        /**
         * Configures the database name for the connection in the {@code Builder}.
         *
         * @param database the name of the database as a {@code String}.
         *                 This specifies the target database to connect to.
         * @return the {@code Builder} instance, allowing for method chaining.
         */
        public Builder database(String database) {
            this.database = database;
            return this;
        }

        /**
         * Configures the username for the database connection in the {@code Builder}.
         *
         * @param user the username as a {@code String}, used to authenticate the connection to the database.
         * @return the {@code Builder} instance, allowing for method chaining.
         */
        public Builder user(String user) {
            this.user = user;
            return this;
        }

        /**
         * Configures the password for the database connection in the {@code Builder}.
         *
         * @param password the password as a {@code String}, used to authenticate the connection to the database.
         * @return the {@code Builder} instance, allowing for method chaining.
         */
        public Builder password(String password) {
            this.password = password;
            return this;
        }

        /**
         * Configures the SQLite database file to be used by the {@code Builder}.
         *
         * @param sqliteFile the SQLite database file as a {@code File} object.
         *                   This specifies the file path to the SQLite database.
         * @return the {@code Builder} instance, allowing for method chaining.
         */
        public Builder sqliteFile(File sqliteFile) {
            this.sqliteFile = sqliteFile;
            return this;
        }
    }

    /**
     * The {@code DriverType} enum represents different types of database drivers supported
     * for establishing database connections. Each enum constant is associated with the
     * fully qualified class name of the JDBC driver.
     *
     * This enum provides a way to manage available driver types and retrieve the
     * corresponding JDBC driver class names dynamically for connection configurations.
     */
    public enum DriverType {
        /**
         * Represents the MySQL database driver.
         * This constant holds the class name of the MySQL JDBC driver.
         */
        MYSQL("com.mysql.cj.jdbc.Driver"),
        /**
         * Represents the MariaDB database driver.
         * This constant holds the class name of the MariaDB JDBC driver.
         */
        MARIADB("org.mariadb.jdbc.Driver"),
        /**
         * Represents the SQLite database driver.
         * This constant holds the class name of the SQLite JDBC driver.
         */
        SQLITE("org.sqlite.JDBC");

        /**
         * Represents the class name of the JDBC driver associated with the database type.
         * This variable is assigned during the enumeration constant initialization and
         * remains constant for the lifetime of an enum instance.
         */
        private final String driver;

        /**
         * Constructor for the DriverType enum.
         * Initializes the driver class name associated with the specific database type.
         *
         * @param driver the fully qualified class name of the JDBC driver for the respective database
         */
        DriverType(String driver) {
            this.driver = driver;
        }

        /**
         * Retrieves the fully qualified class name of the JDBC driver associated with the database type.
         *
         * @return the driver class name as a {@code String}, which can be used for loading and establishing database connections
         */
        public String driver() {
            return this.driver;
        }
    }
}
