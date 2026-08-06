package com.ams.migration;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class SqlScriptReader {

    public String read(Path scriptPath) throws IOException {

        if (scriptPath == null) {
            throw new IllegalArgumentException("Script path cannot be null.");
        }

        if (!Files.exists(scriptPath)) {
            throw new IOException("SQL script not found: " + scriptPath);
        }

        return Files.readString(scriptPath, StandardCharsets.UTF_8);
    }
}