package com.ams.migration;

public class Main {

    public static void main(String[] args) throws Exception {

        MigrationRunner runner = new MigrationRunner();

        runner.migrate();
    }
}

/*Main.java
    |
    |
    v
MigrationRunner.java
    |
    |
    v
DBConnection.java
    |
    |
    v
DatabaseConfig.java
    |
    |
    v
db.properties
    |
    |
    v
Oracle Database*/