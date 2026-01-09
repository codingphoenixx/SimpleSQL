package dev.coph.simplesql.driver;

/**
 * The {@code DriverType} enum represents different types of database drivers supported
 * for establishing database connections. Each enum constant is associated with the
 * fully qualified class name of the JDBC driver.
 * <p>
 * This enum provides a way to manage available driver types and retrieve the
 * corresponding JDBC driver class names dynamically for connection configurations.
 */
public enum DriverType {
    /**
     * Represents the MySQL database driver.
     * This constant holds the class name of the MySQL JDBC driver.
     */
    MYSQL("com.mysql.cj.jdbc.Driver", "MySQL"),
    /**
     * Represents the MariaDB database driver.
     * This constant holds the class name of the MariaDB JDBC driver.
     */
    MARIADB("org.mariadb.jdbc.Driver","MariaDB"),
    /**
     * Represents the Postgresql database driver.
     * This constant holds the class name of the Postgresql JDBC driver.
     */
    POSTGRESQL("org.postgresql.Driver"),
    /**
     * Represents the SQLite database driver.
     * This constant holds the class name of the SQLite JDBC driver.
     */
    SQLITE("org.sqlite.JDBC", "SQLite");

    /**
     * Represents the class name of the JDBC driver associated with the database type.
     * This variable is assigned during the enumeration constant initialization and
     * remains constant for the lifetime of an enum instance.
     */
    private final String driver;
    /**
     * Represents a human-readable name for the database driver.
     * This name is used to provide a more descriptive and user-friendly identifier
     * for the respective database type.
     */
    private final String readableName;

    /**
     * Constructs a {@code DriverType} instance with the specified fully qualified driver
     * class name and a human-readable name representing the database driver.
     *
     * @param driver the fully qualified class name of the JDBC driver
     * @param readableName the descriptive, human-readable name of the database driver
     */
    DriverType(String driver, String readableName) {
        this.driver = driver;
        this.readableName = readableName;
    }

    /**
     * Retrieves the fully qualified class name of the JDBC driver associated with the database type.
     *
     * @return the driver class name as a {@code String}, which can be used for loading and establishing database connections
     */
    public String driver() {
        return this.driver;
    }

    public String readableName() {
        return this.readableName;
    }
}
