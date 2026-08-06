package com.ams.migration;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MigrationScanner {

    private static final String MIGRATION_FOLDER = "db/migration";

    // V001__create_users.up.sql
    private static final Pattern FILE_PATTERN =
            Pattern.compile("(V\\d+)__(.+)\\.(up|down)\\.sql");

    public List<Migration> scan() throws IOException, URISyntaxException {

        URL url = getClass().getClassLoader().getResource(MIGRATION_FOLDER);

        if (url == null) {
            throw new RuntimeException("Migration folder not found: " + MIGRATION_FOLDER);
        }

        Path migrationDirectory = Paths.get(url.toURI());

        Map<String, Path> upScripts = new HashMap<>();
        Map<String, Path> downScripts = new HashMap<>();
        Map<String, String> descriptions = new HashMap<>();

        try (DirectoryStream<Path> stream =
                     Files.newDirectoryStream(migrationDirectory, "*.sql")) {

            for (Path file : stream) {

                String fileName = file.getFileName().toString();

                Matcher matcher = FILE_PATTERN.matcher(fileName);

                if (!matcher.matches()) {
                    continue;
                }

                String version = matcher.group(1);

                String description = matcher.group(2);

                String type = matcher.group(3);

                descriptions.put(version, description);

                if ("up".equals(type)) {

                    if (upScripts.containsKey(version)) {
                        throw new IllegalStateException(
                                "Duplicate UP migration: " + version);
                    }

                    upScripts.put(version, file);

                } else {

                    if (downScripts.containsKey(version)) {
                        throw new IllegalStateException(
                                "Duplicate DOWN migration: " + version);
                    }

                    downScripts.put(version, file);
                }
            }
        }

        List<Migration> migrations = new ArrayList<>();

        for (String version : upScripts.keySet()) {

            Path up = upScripts.get(version);

            Path down = downScripts.get(version);

            if (down == null) {
                System.out.println(
                        "[WARNING] Missing down migration for " + version);
            }

            Migration migration = new Migration(
                    version,
                    descriptions.get(version),
                    up,
                    down
            );

            migrations.add(migration);
        }

        Collections.sort(migrations);

        return migrations;
    }
}