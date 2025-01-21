package dev.coph.simplesql.database.attributes;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true, fluent = true)
public class DataType {
    /**
     * A FIXED length string (can contain letters, numbers, and special characters). The size parameter specifies the column length in characters - can be from 0 to 255. Default is 1
     */
    public static final DataType CHAR = new DataType(true, false, "CHAR");

    /**
     * A VARIABLE length string (can contain letters, numbers, and special characters). The size parameter specifies the maximum string length in characters - can be from 0 to 65535
     */
    public static final DataType VARCHAR = new DataType(true, true, "VARCHAR");

    /**
     * Holds a string with a maximum length of 255 characters
     */
    public static final DataType TINYTEXT = new DataType(false, false, "TINYTEXT");

    /**
     * Holds a string with a maximum length of 16.777.215 characters
     */
    public static final DataType MEDIUMTEXT = new DataType(false, false, "MEDIUMTEXT");

    /**
     * Holds a string with a maximum length of 4.294.967.295 characters
     */
    public static final DataType LONGTEXT = new DataType(false, false, "LONGTEXT");

    /**
     * 0 is considered as false, 1 is considered as true.
     */
    public static final DataType BOOLEAN = new DataType(false, false, "BOOLEAN");

    /**
     * A very small integer. Signed range is from -128 to 127. Unsigned range is from 0 to 255. The size parameter specifies the maximum display width (which is 255)
     */
    public static final DataType TINYINT = new DataType(true, false, "TINYINT");

    /**
     * A small integer. Signed range is from -32768 to 32767. Unsigned range is from 0 to 65535. The size parameter specifies the maximum display width (which is 255)
     */
    public static final DataType SMALLINT = new DataType(true, false, "SMALLINT");

    /**
     * A medium integer. Signed range is from -2147483648 to 2147483647. Unsigned range is from 0 to 4294967295. The size parameter specifies the maximum display width (which is 255)
     */
    public static final DataType INTEGER = new DataType(true, false, "INTEGER");

    /**
     * A large integer. Signed range is from -9223372036854775808 to 9223372036854775807. Unsigned range is from 0 to 18446744073709551615. The size parameter specifies the maximum display width (which is 255)
     */
    public static final DataType BIGINT = new DataType(true, false, "BIGINT");

    /**
     * A floating point number. MySQL uses the object value to determine whether to use FLOAT or DOUBLE for the resulting data type. If object is from 0 to 24, the data type becomes FLOAT(). If object is from 25 to 53, the data type becomes DOUBLE()
     */
    public static final DataType FLOAT = new DataType(true, false, "FLOAT");

    /**
     * A normal-size floating point number. Parameters are not available
     */
    public static final DataType DOUBLE = new DataType(false, false, "DOUBLE");

    /**
     * A date. Format: YYYY-MM-DD. The supported range is from '1000-01-01' to '9999-12-31'
     */
    public static final DataType DATE = new DataType(false, false, "DATE");

    /**
     * A date and time combination. Format: YYYY-MM-DD hh:mm:ss. The supported range is from '1000-01-01 00:00:00' to '9999-12-31 23:59:59'.
     */
    public static final DataType DATETIME = new DataType(false, false, "DATETIME");

    /**
     * A timestamp. TIMESTAMP values are stored as the number of seconds since the Unix epoch ('1970-01-01 00:00:00' UTC). Format: YYYY-MM-DD hh:mm:ss. The supported range is from '1970-01-01 00:00:01' UTC to '2038-01-09 03:14:07' UTC.
     */
    public static final DataType TIMESTAMP = new DataType(false, false, "TIMESTAMP");

    /**
     * A time. Format: hh:mm:ss. The supported range is from '-838:59:59' to '838:59:59'
     */
    public static final DataType TIME = new DataType(false, false, "TIME");

    private final boolean canHaveObject;
    private final boolean requireObject;
    private final String name;

    public DataType(boolean canHaveObject, boolean requireObject, String name) {
        this.canHaveObject = canHaveObject;
        this.requireObject = requireObject;
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public StringBuilder toSQL(Object value) {
        return new StringBuilder().append(name).append(canHaveObject() && value != (requireObject ? 0 : null) ? "(" + value + ")" : "");
    }
}
