package dev.coph.simplesql.database.attributes;

import dev.coph.simplesql.driver.DriverType;
import dev.coph.simplesql.exception.FeatureNotSupportedException;

import java.util.Locale;

/**
 * Represents the Latin7 character set, also known as ISO-8859-13 or Baltic Rim.
 * It is used for data encoding for Baltic languages such as Lithuanian and Latvian,
 * as well as other languages in the Baltic region.
 */
public enum CharacterSet {
    /**
     * Represents the ARMSCII-8 character set, which is an 8-bit character encoding
     * system primarily used for encoding Armenian script. It defines the usage of
     * characters specific to the Armenian alphabet and other control characters
     * within the context of database systems or textual data processing.
     */
    ARMSCII8,
    /**
     * Represents whether the SQL attribute or data type should use ASCII encoding.
     * When set to true, ensures that the column data is encoded in the ASCII character set.
     * Useful for columns where only English text or characters within the ASCII set are expected.
     */
    ASCII,
    /**
     * Represents the BIG5 character set, commonly used for Traditional Chinese text encoding.
     * Defined as an enumeration value within the CharacterSet enum.
     * Often utilized in database systems to specify text encoding for character string types.
     */
    BIG5,
    /**
     * Represents a binary data type in a database. This data type is typically used to store
     * binary data, such as images, files, or other non-character data. Binary data is generally
     * stored in a fixed-length format, and the maximum length depends on the database configuration.
     */
    BINARY,
    /**
     * Represents the CP850 character set, also known as Code Page 850.
     * It is an extended ASCII character set primarily used for Western European languages.
     * CP850 contains additional characters compared to the standard ASCII
     * to support accented letters and other special characters often used in these languages.
     * This character set is typically utilized in legacy systems (DOS) and applications.
     */
    CP850,
    /**
     * Represents the CP852 character set encoding used for text data.
     * CP852 is an 8-bit character encoding primarily used with MS-DOS systems
     * to support Central and Eastern European languages.
     * This encoding is also known as DOS Latin 2.
     */
    CP852,
    /**
     * Represents the CP866 character encoding, which is a single-byte character encoding
     * used primarily for representing Cyrillic script in DOS environments.
     * This encoding was originally designed to support languages like Russian, Bulgarian,
     * and other Cyrillic-script-based languages in legacy systems.
     */
    CP866,
    /**
     * Represents the CP932 character encoding commonly used for Japanese text.
     * CP932 is an extension of the Shift JIS encoding with additional characters
     * included to cater to specific needs in Windows environments.
     * Typically used when handling or processing text encoded in CP932.
     */
    CP932,
    /**
     * Represents the CP1250 character encoding, commonly known as the Central European Windows code page.
     * This encoding is designed to cover languages that use the Latin alphabet with additional characters
     * necessary for Central European languages, such as Czech, Polish, Hungarian, and others.
     * It is often used in legacy systems and databases to handle text specific to these languages.
     */
    CP1250,
    /**
     * Represents the encoding standard CP1251 (Windows-1251), which is a legacy 8-bit character encoding.
     * It is commonly used for Cyrillic script languages such as Russian and Bulgarian.
     */
    CP1251,
    /**
     * Represents the CP1256 (Windows-1256) character encoding.
     * It is commonly used for representing Arabic and some other languages
     * in Microsoft Windows environments. CP1256 is a single-byte encoding
     * that supports a range of characters including Arabic letters, Latin
     * alphabet letters, and special symbols.
     */
    CP1256,
    /**
     * Represents the CP1257 (Windows-1257) character encoding, commonly used for
     * Baltic languages including Estonian, Latvian, and Lithuanian. This encoding
     * is part of the Windows Code Pages and supports a specific set of characters
     * tailored toward these languages.
     */
    CP1257,
    /**
     * Represents the DEC8 character set, an early single-byte character set used in MySQL.
     * It supports encoding for only a limited subset of characters, primarily suited for Western European languages.
     * This character set has been historically used but is less commonly utilized in modern applications due to its limitations.
     */
    DEC8,
    /**
     * Represents the EUC-JP-MS character set encoding, commonly used in encoding
     * Japanese text for database or text processing operations. The implementation
     * or context in which this variable is used determines its specific usage,
     * such as in defining or handling character encoding in SQL-related tasks.
     */
    EUCJPMS,
    /**
     * Represents the EUC-KR (Extended Unix Code for Korean) character encoding.
     * This encoding is used for representing Korean characters in applications
     * and databases that support EUC-based encoding standards.
     */
    EUCKR,
    /**
     * Represents the GB2312 data type, commonly used for storing text encoded in the GB2312 character encoding,
     * a standard used for Simplified Chinese characters in digital systems.
     */
    GB2312,
    /**
     * Represents the GBK character encoding, a Chinese character encoding standard.
     * GBK is an extension of the GB2312 character set, capable of encoding a wider range of Chinese symbols,
     * punctuation, and characters.
     */
    GBK,
    /**
     * Represents a specific setting often used in geographic or spatial-related
     * contexts. The exact usage or data type corresponding to `geostd8` is not
     * explicitly described.
     * <p>
     * This variable could potentially be linked to database attributes,
     * configuration, or data processing relevant to geographic standards or encoding.
     */
    GEOSTD8,
    /**
     * Represents a SQL-related configuration or utility, potentially related
     * to Greek nomenclature or structures. This placeholder or functional
     * aspect may be linked to certain operations, attributes, or logic
     * within the context of a simple SQL library or API.
     */
    GREEK,
    /**
     * A variable representing Hebrew content or any related configuration.
     * Could potentially be used for localization or language-specific operations.
     */
    HEBREW,
    /**
     * Represents the HP8 character set, a single-byte encoding historically used
     * for Western European languages. Primarily associated with the Hewlett-Packard
     * Roman8 character set. It supports a limited range of characters and has been
     * superseded by more modern character encodings in contemporary applications.
     */
    HP8,
    /**
     * Represents the second key or identifier used in database-related operations.
     * The specific purpose of this variable depends on the context of its usage
     * and additional implementation details. It could be utilized for handling keys,
     * identifiers, or attributes within a database query or schema.
     */
    KEYBCS2,
    /**
     * Represents the KOI8-R character set, a single-byte encoding primarily used
     * for representing Russian and other Cyrillic scripts. It is part of the
     * family of KOI8 encodings and was historically popular in systems that
     * required Cyrillic text support. KOI8-R provides support for Russian
     * alphabet characters and some additional symbols common in Russian texts.
     */
    KOI8R,
    /**
     * Represents a KOI8-U character encoding.
     * KOI8-U is an 8-bit character encoding designed to cover certain
     * Cyrillic alphabets. It is similar to KOI8-R but includes additional
     * Ukrainian characters.
     */
    KOI8U,
    /**
     * Represents the LATIN1 character set, also known as ISO 8859-1 or Western European.
     * It is a single-byte encoding commonly used for Western European languages,
     * including English, Spanish, French, German, and Portuguese.
     * LATIN1 supports a wide range of characters, including accented letters and other special symbols.
     * This character set is widely used in legacy database systems and applications.
     */
    LATIN1,
    /**
     * Represents the LATIN2 character set, also known as ISO 8859-2 or Central European.
     * It is a single-byte encoding commonly used for Central and Eastern European languages,
     * including Czech, Polish, Slovak, Hungarian, and others. LATIN2 provides support for
     * accented letters and special symbols used in these languages. This character set
     * is useful for legacy systems and databases to store text in Central European scripts.
     */
    LATIN2,
    /**
     * Represents the Latin5 character set, also known as ISO-8859-9 or Turkish.
     * It is used for data encoding primarily for the Turkish language.
     */
    LATIN5,
    /**
     * Represents the LATIN7 character set, commonly used in modern SQL database systems.
     * This character set supports ISO 8859-13 encoding, frequently utilized in Baltic languages.
     */
    LATIN7,
    /**
     * The `MACCE` character set is used for Central European languages on classic Macintosh systems,
     * supporting characters specific to these languages.
     */
    MACCE,
    /**
     * The `macroman` variable represents a specific character encoding used for handling text data,
     * particularly in older Macintosh systems. This encoding maps characters to their binary
     * representation, ensuring proper interpretation and conversion of text data.
     */
    MACROMAN,
    /**
     * Represents the Shift-JIS character encoding (a Japanese character encoding standard).
     * Typically used for encoding text in databases or systems that require
     * the Shift-JIS character set to handle Japanese characters.
     */
    SJIS,
    /**
     * The `SWE7` character set is designed for Swedish language support,
     * including specific Swedish characters like `Å`, `Ä`, and `Ö`.
     * It is limited and largely replaced by modern Unicode encodings.
     */
    SWE7,
    /**
     * Represents the TIS-620 Thai character encoding.
     * TIS-620 is a character set standard in Thailand and is commonly used for Thai text in databases or text processing.
     */
    TIS620,
    /**
     * Represents a UCS-2 encoding type or flag typically used for handling
     * character encoding in database attributes or text processing.
     */
    UCS2,
    /**
     * The `UJIS` character set supports Japanese using the EUC-JP encoding.
     * It includes characters from Kanji, Hiragana, and Katakana, suitable for Japanese text processing.
     */
    UJIS,
    /**
     * Represents the MySQL character set `utf8mb3` which uses a maximum of 3 bytes per character.
     * It is primarily used to store multilingual data in a more space-efficient way compared to
     * `utf8mb4`. This character set supports most languages but excludes certain supplementary
     * Unicode characters.
     */
    UTF8MB3,
    /**
     * Represents the UTF-8mb4 character set encoding used in databases.
     * UTF-8mb4 is an extended version of UTF-8 that supports the full range
     * of Unicode characters, including supplementary characters outside
     * the Basic Multilingual Plane.
     */
    UTF8MB4,
    /**
     * A constant representing the UTF-16 encoding for text.
     * UTF-16 is a character encoding capable of encoding the entirety of the Unicode standard.
     * It uses one or two 16-bit code units to represent each character, making it a variable-length encoding format.
     */
    UTF16,
    /**
     * Represents the UTF-16 Little Endian encoding for data storage or manipulation.
     * This encoding uses 16-bit units in little-endian byte order. It is commonly
     * used for text processing where Unicode character representation is required.
     */
    UTF16LE,
    /**
     * Represents a UTF-32 encoding data type. This data type is commonly used for
     * storing and representing Unicode characters where each character is encoded
     * using 4 bytes (32 bits). UTF-32 encoding is known for its fixed width,
     * which ensures simplicity in processing, as each code point is readily addressable.
     * <p>
     * Typically used when consistency in encoding size is essential, or when
     * working with a strict set of multi-language Unicode data without compression.
     */
    UTF32;

    /**
     * Checks if the SQLite database supports the current character set.
     *
     * @return a boolean value indicating whether the SQLite database supports the current character set.
     * Returns false in the current implementation.
     */
    public static boolean sqliteSupports() {
        return false;
    }

    /**
     * Determines whether MySQL supports the current character set.
     *
     * @return a boolean value indicating whether MySQL supports the current character set.
     * Always returns true in the current implementation.
     */
    public static boolean mysqlSupports() {
        return true;
    }

    /**
     * Converts the name of the current character set instance to the MySQL-compatible charset name.
     * The resulting charset name is transformed into lowercase using the root locale.
     *
     * @return the MySQL-compatible charset name as a lowercase string.
     */
    public String toMySqlCharset() {
        return name().toLowerCase(Locale.ROOT);
    }

    /**
     * Converts the current character set to its PostgreSQL-compatible encoding name.
     * If the character set does not have a corresponding PostgreSQL encoding,
     * a {@link FeatureNotSupportedException} is thrown.
     *
     * @return the PostgreSQL-compatible encoding name as a {@code String}.
     * @throws FeatureNotSupportedException if there is no corresponding PostgreSQL encoding
     *                                      for the current character set.
     */
    public String toPostgresEncodingOrThrow() {
        String enc = toPostgresEncoding();
        if (enc == null) throw new FeatureNotSupportedException(DriverType.POSTGRESQL);
        return enc;
    }

    /**
     * Converts the current character set to its corresponding PostgreSQL-compatible encoding name.
     * If the character set is not directly supported for conversion to a PostgreSQL encoding,
     * it will return {@code null}.
     *
     * @return the PostgreSQL-compatible encoding name as a {@code String}, or {@code null}
     * if there is no corresponding PostgreSQL encoding for the current character set.
     */
    public String toPostgresEncoding() {
        return switch (this) {
            case UTF8MB4, UTF8MB3 -> "UTF8";
            case LATIN1 -> "LATIN1";
            case LATIN2 -> "LATIN2";
            case LATIN5 -> "LATIN5";
            case LATIN7 -> "ISO_8859_13";
            case KOI8R -> "KOI8R";
            case KOI8U -> "KOI8U";
            case SJIS -> "SJIS";
            case EUCJPMS, UJIS -> "EUC_JP";
            case EUCKR -> "EUC_KR";
            case GB2312 -> "EUC_CN";
            case GBK -> "GBK";
            case BIG5 -> "BIG5";
            case ASCII -> "SQL_ASCII";
            case TIS620 -> "TIS620";
            case UTF16, UTF16LE, UTF32, UCS2, BINARY, CP850, CP852, CP866, CP932,
                 CP1250, CP1251, CP1256, CP1257, ARMSCII8, DEC8, GEOSTD8, GREEK,
                 HEBREW, HP8, KEYBCS2, MACCE, MACROMAN, SWE7 -> null;
        };
    }

    /**
     * Determines whether the current character set is supported by PostgreSQL.
     *
     * @return true if the current character set has a corresponding PostgreSQL-compatible
     * encoding; false otherwise.
     */
    public boolean postgresSupports() {
        return toPostgresEncoding() != null;
    }
}
