package dev.coph.simplesql.database.attributes;

import dev.coph.simplesql.driver.DriverType;
import dev.coph.simplesql.query.Query;
import dev.coph.simplesql.utils.DatabaseCheck;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Represents a data type that can be used in database schema definitions or SQL queries.
 * The `DataType` class defines various common database column types, each with specific
 * characteristics such as length limitations, supported ranges, or formatting rules.
 * <p>
 * The class supports both fixed and variable length data types, numeric types,
 * date/time types, and specific character types.
 *
 * @param canHaveObject Indicates whether this DataType instance can associate with an object value.
 *                      <p>
 *                      This field determines if the corresponding data type is capable of handling
 *                      objects as valid values, which might influence how data is represented or
 *                      processed in SQL-related operations.
 * @param requireObject Represents whether a {@code DataType} strictly requires an associated object value.
 *                      <p>
 *                      This field determines if it is mandatory for a data type to be paired with a non-null object
 *                      when performing operations such as validation or SQL generation.
 *                      <p>
 *                      For example, certain data types like {@code VARCHAR} or {@code CHAR} might require an object value
 *                      for processing, whereas others like {@code BOOLEAN} or {@code INTEGER} might not.
 * @param name          Represents the name of the data type. This is a mandatory identifier for a
 *                      specific data type used in SQL-related operations or database interactions.
 *                      The value of this field is immutable once assigned.
 */
public record DataType(boolean canHaveObject, boolean requireObject, boolean canBeUnsigned, String name) {
    /**
     * A FIXED length string (can contain letters, numbers, and special characters). The size parameter specifies the column length in characters - can be from 0 to 255. Default is 1
     */
    public static final DataType CHAR = new DataType(true, false, false, "CHAR");

    /**
     * A VARIABLE length string (can contain letters, numbers, and special characters). The size parameter specifies the maximum string length in characters - can be from 0 to 65535
     */
    public static final DataType VARCHAR = new DataType(true, true, false, "VARCHAR");

    /**
     * Holds a string with a maximum length of 255 characters
     */
    public static final DataType TINYTEXT = new DataType(false, false, false, "TINYTEXT");

    /**
     * Holds a string with a maximum length of 16.777.215 characters
     */
    public static final DataType MEDIUMTEXT = new DataType(false, false, false, "MEDIUMTEXT");

    /**
     * Holds a string with a maximum length of 4.294.967.295 characters
     */
    public static final DataType LONGTEXT = new DataType(false, false, false, "LONGTEXT");

    /**
     * 0 is considered as false, 1 is considered as true.
     */
    public static final DataType BOOLEAN = new DataType(false, false, false, "BOOLEAN");

    /**
     * A very small integer. Signed range is from -128 to 127. Unsigned range is from 0 to 255. The size parameter specifies the maximum display width (which is 255)
     */
    public static final DataType TINYINT = new DataType(true, false, true, "TINYINT");

    /**
     * A small integer. Signed range is from -32768 to 32767. Unsigned range is from 0 to 65535. The size parameter specifies the maximum display width (which is 255)
     */
    public static final DataType SMALLINT = new DataType(true, false, true, "SMALLINT");

    /**
     * A medium integer. Signed range is from -2147483648 to 2147483647. Unsigned range is from 0 to 4294967295. The size parameter specifies the maximum display width (which is 255)
     */
    public static final DataType INTEGER = new DataType(true, false, true, "INTEGER");

    /**
     * A large integer. Signed range is from -9223372036854775808 to 9223372036854775807. Unsigned range is from 0 to 18446744073709551615. The size parameter specifies the maximum display width (which is 255)
     */
    public static final DataType BIGINT = new DataType(true, false, true, "BIGINT");

    /**
     * A floating point number. MySQL uses the object value to determine whether to use FLOAT or DOUBLE for the resulting data type. If object is from 0 to 24, the data type becomes FLOAT(). If object is from 25 to 53, the data type becomes DOUBLE()
     */
    public static final DataType FLOAT = new DataType(true, false, true, "FLOAT");

    /**
     * A normal-size floating point number. Parameters are not available
     */
    public static final DataType DOUBLE = new DataType(false, false, true, "DOUBLE");

    /**
     * A date. Format: YYYY-MM-DD. The supported range is from '1000-01-01' to '9999-12-31'
     */
    public static final DataType DATE = new DataType(false, false, false, "DATE");

    /**
     * A date and time combination. Format: YYYY-MM-DD hh:mm:ss. The supported range is from '1000-01-01 00:00:00' to '9999-12-31 23:59:59'.
     */
    public static final DataType DATETIME = new DataType(false, false, false, "DATETIME");

    /**
     * A timestamp. TIMESTAMP values are stored as the number of seconds since the Unix epoch ('1970-01-01 00:00:00' UTC). Format: YYYY-MM-DD hh:mm:ss. The supported range is from '1970-01-01 00:00:01' UTC to '2038-01-09 03:14:07' UTC.
     */
    public static final DataType TIMESTAMP = new DataType(false, false, false, "TIMESTAMP");

    /**
     * A time. Format: hh:mm:ss. The supported range is from '-838:59:59' to '838:59:59'
     */
    public static final DataType TIME = new DataType(false, false, false, "TIME");

    /**
     * Represents the ENUM SQL data type in a database schema.
     * This data type allows a predefined set of string values,
     * from which a column can store one. Useful for defining
     * constrained categorical data.
     * <p>
     * The attributes of this data type are:
     * - Can have an associated object.
     * - Requires an associated object.
     * - Cannot be unsigned.
     * - Has the name "ENUM".
     */
    public static final DataType ENUM = new DataType(true, true, false, "ENUM");

    /**
     * Represents the BINARY data type, which is a fixed-length binary string.
     */
    public static final DataType BINARY = new DataType(true, true, false, "BINARY");
    /**
     * Represents the VARBINARY data type, which is a variable-length binary string.
     */
    public static final DataType VARBINARY = new DataType(true, true, false, "VARBINARY");

    /**
     * Represents the TINYBLOB data type, which is a very small binary large object capable of storing binary data
     * up to a maximum size of 255 bytes.
     */
    public static final DataType TINYBLOB = new DataType(false, false, false, "TINYBLOB");
    /**
     * Represents the BLOB data type, which is a binary large object capable of storing binary data up to 65,535 bytes.
     */
    public static final DataType BLOB = new DataType(false, false, false, "BLOB");
    /**
     * Represents the MEDIUMBLOB data type, which is a binary large object capable of storing binary data up to 16,777,215 bytes.
     */
    public static final DataType MEDIUMBLOB = new DataType(false, false, false, "MEDIUMBLOB");
    /**
     * Represents the LONGBLOB data type, which is a binary large object capable of storing binary data up to 4,294,967,295 bytes.
     */
    public static final DataType LONGBLOB = new DataType(false, false, false, "LONGBLOB");


    @Override
    public String toString() {
        return name;
    }

    /**
     * Converts this DataType instance to its equivalent SQL representation based on the given parameters.
     *
     * @param query   The query context in which this DataType is being used. This is used for retrieving
     *                database adapter information, such as the driver type.
     * @param value   The value to be included in the SQL representation. For certain data types like
     *                ENUM, this value must not be null.
     * @param unsigned   The unsigned state of this DataType. If specified as UnsignedState.ACTIVE and
     *                the DataType supports unsigned values, the SQL representation includes the UNSIGNED suffix.
     * @return A StringBuilder containing the SQL representation of this DataType.
     * @throws IllegalArgumentException If the ENUM data type is used with a null value.
     */
    public StringBuilder toSQL(Query query, Object value, UnsignedState unsigned) {
        if (name.equals("BLOB") || name.equals("TINYBLOB") || name.equals("MEDIUMBLOB") || name.equals("LONGBLOB") || name.equals("BINARY") || name.equals("VARBINARY"))
            DatabaseCheck.unsupportedDriver(query.databaseAdapter().driverType(), DriverType.POSTGRESQL);


        if (name.equals("ENUM")) {
            if (value == null) {
                throw new IllegalArgumentException("ENUM data type requires a non-null value");
            }
            return new StringBuilder()
                    .append(name)
                    .append("(")
                    .append(
                            Arrays.stream(value.toString().split(";"))
                                    .map(String::trim)
                                    .filter(s -> !s.isEmpty())
                                    .map(s -> "'" + s.replace("'", "''") + "'")
                                    .collect(Collectors.joining(", "))
                    ).append(")");
        }
        String unsignedSuffix = unsigned != null && unsigned.equals(UnsignedState.ACTIVE) && canBeUnsigned ? " UNSIGNED" : "";
        return new StringBuilder()
                .append(name)
                .append(canHaveObject() && value != (requireObject ? 0 : null) ? "(" + value + ")" + unsignedSuffix : unsignedSuffix);
    }


    /**
     * Determines whether this DataType instance can have an associated object.
     *
     * @return true if the DataType can have an associated object; false otherwise.
     */
    @Override
    public boolean canHaveObject() {
        return this.canHaveObject;
    }

    /**
     * Determines whether this DataType instance requires an associated object.
     *
     * @return true if the DataType requires an associated object; false otherwise.
     */
    @Override
    public boolean requireObject() {
        return this.requireObject;
    }

    /**
     * Retrieves the name associated with this DataType instance.
     *
     * @return The name of the DataType as a string.
     */
    @Override
    public String name() {
        return this.name;
    }
}
