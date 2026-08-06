package com.ams.migration;

import java.io.IOException;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MigrationValidator {

    public void validate(List<Migration> migrations)
            throws IOException {

        if (migrations == null || migrations.isEmpty()) {
            throw new IllegalStateException("No migration files found.");
        }

        Set<String> versions = new HashSet<>();
        Set<String> descriptions = new HashSet<>();

        String previousVersion = null;

        for (Migration migration : migrations) {

            validateVersion(migration);

            validateDescription(migration);

            validateScripts(migration);

            if (!versions.add(migration.getVersion())) {
                throw new IllegalStateException(
                        "Duplicate migration version: "
                                + migration.getVersion());
            }

            if (!descriptions.add(migration.getDescription())) {

                System.out.println(
                        "[WARNING] Duplicate description: "
                                + migration.getDescription());
            }

            if (previousVersion != null &&
                    previousVersion.compareTo(
                            migration.getVersion()) >= 0) {

                throw new IllegalStateException(
                        "Migration order is invalid: "
                                + migration.getVersion());
            }

            previousVersion = migration.getVersion();
        }

        System.out.println(
                "[VALIDATION SUCCESS] "
                        + migrations.size()
                        + " migration(s) validated.");
    }

    private void validateVersion(Migration migration) {

        if (!migration.getVersion().matches("V\\d+")) {

            throw new IllegalStateException(
                    "Invalid migration version: "
                            + migration.getVersion());
        }
    }

    private void validateDescription(Migration migration) {

        if (migration.getDescription() == null ||
                migration.getDescription().trim().isEmpty()) {

            throw new IllegalStateException(
                    "Description missing for "
                            + migration.getVersion());
        }
    }

    private void validateScripts(Migration migration)
            throws IOException {

        if (migration.getUpScript() == null) {

            throw new IllegalStateException(
                    "Missing UP migration for "
                            + migration.getVersion());
        }

        if (!Files.exists(migration.getUpScript())) {

            throw new IllegalStateException(
                    "UP script not found: "
                            + migration.getUpScript());
        }

        if (Files.size(migration.getUpScript()) == 0) {

            throw new IllegalStateException(
                    "UP script is empty: "
                            + migration.getUpScript());
        }

        if (migration.getDownScript() == null) {

            System.out.println(
                    "[WARNING] Missing DOWN migration: "
                            + migration.getVersion());

            return;
        }

        if (!Files.exists(migration.getDownScript())) {

            throw new IllegalStateException(
                    "DOWN script not found: "
                            + migration.getDownScript());
        }
    }

}