# Datenbank-Zugriffs-Bibliothek mit HikariCP

## Übersicht

Diese Java-Bibliothek bietet eine benutzerfreundliche Schnittstelle zur einfachen Durchführung von Datenbankzugriffen. Durch die Integration von HikariCP als Connection Pooling-Mechanismus gewährleistet sie eine effiziente und performante Verbindung zu relationalen Datenbanken. Ziel ist es, Entwicklern die Interaktion mit Datenbanken zu erleichtern, indem häufige Aufgaben abstrahiert und optimiert werden.

## Funktionen

- **Einfache Konfiguration**: Intuitive API zur Konfiguration von Datenbankverbindungen.
- **Effizientes Connection Pooling**: Nutzung von HikariCP für hohe Leistung und geringere Latenz.
- **Einfache CRUD-Operationen**: Bereitstellung von Methoden zur einfachen Durchführung von Create, Read, Update und Delete-Operationen.

## Voraussetzungen

- Java 21 oder höher
- Maven oder Gradle (zum Build-Management)
- Eine Datenbank (z.B. MySQL, MariaDB, ...)

## Installation

### Maven

Fügen Sie die folgende Abhängigkeit in Ihre `pom.xml` ein:

```xml
	<repositories>
        <repository>
            <id>cophrepository-releases</id>
            <name>CoPh Repository</name>
            <url>https://repo.coph.dev/releases</url>
        </repository>
	</repositories>

        <dependency>
            <groupId>dev.coph</groupId>
            <artifactId>simplesql</artifactId>
            <version>TAG</version>
        </dependency>
```

### Gradle

Fügen Sie die folgende Zeile in Ihre `build.gradle` ein:

```groovy
    maven {
        name "cophrepositoryReleases"
        url "https://repo.coph.dev/releases"
    }

    implementation "dev.coph:simplesql:TAG"
```

## Nutzung

### Grundlegende Konfiguration

```java
import dev.coph.simplesql.database.DatabaseAdapter;

public class Main {
    public static void main(String[] args) {
       //Create DatabaseAdapter and connect
       DatabaseAdapter databaseAdapter = new DatabaseAdapter.Builder()
                .driverType(DatabaseAdapter.DriverType.MARIADB)
                .host(HOST)
                .port(PORT)
                .database(DATABASE)
                .user(USERNAME)
                .password(PASSWORD)
                .build();
        databaseAdapter.connect();

        //Erstellen der Request ...
        TableCreateQueryProvider tableCreateQuery = Query.tableCreate()
                .table("test")
                .column("uuid", DataType.VARCHAR, 64, ColumnType.UNIQUE)
                .column("comment", DataType.LONGTEXT)
                .column("number", DataType.INTEGER, ColumnType.PRIMARY_KEY_AUTOINCREMENT)
                .createMethode(CreateMethode.IF_NOT_EXISTS);

        //Senden der Request
        new Query(databaseAdapter).async(true).queries(tableCreateQuery).execute();
    }
}
```

## Dokumentation

Aktuell existiert keine öffentliche Dokumentation. Dies kommt in Zukunft dazu. 

## Kontakt

Für Fragen oder Anregungen kontaktieren Sie uns bitte unter: [codingphoenix@atirion.de](mailto:codingphoenix@atirion.de).
