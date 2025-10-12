package dev.coph.simplesql.driver;
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
     * Represents the Postgresql database driver.
     * This constant holds the class name of the Postgresql JDBC driver.
     */
    POSTGRESQL("org.postgresql.Driver"),
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
