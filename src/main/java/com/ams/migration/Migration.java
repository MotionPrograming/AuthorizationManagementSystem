package com.ams.migration;

import java.nio.file.Path;
import java.util.Objects;

public class Migration implements Comparable<Migration> {

    private final String version;

    private final String description;

    private final Path upScript;

    private final Path downScript;

    private MigrationStatus status;

    public Migration(
            String version,
            String description,
            Path upScript,
            Path downScript) {

        this.version = version;
        this.description = description;
        this.upScript = upScript;
        this.downScript = downScript;
        this.status = MigrationStatus.PENDING;
    }

    public String getVersion() {
        return version;
    }

    public String getDescription() {
        return description;
    }

    public Path getUpScript() {
        return upScript;
    }

    public Path getDownScript() {
        return downScript;
    }

    public MigrationStatus getStatus() {
        return status;
    }

    public void setStatus(MigrationStatus status) {
        this.status = status;
    }

    @Override
    public int compareTo(Migration other) {
        return this.version.compareTo(other.version);
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj)
            return true;

        if (!(obj instanceof Migration))
            return false;

        Migration other = (Migration) obj;

        return Objects.equals(version, other.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(version);
    }

    @Override
    public String toString() {

        return "Migration{" +
                "version='" + version + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }
}