package com.ams.migration;

import java.util.ArrayList;
import java.util.List;

public class SqlScriptParser {

    public List<String> parse(String script) {

        List<String> statements = new ArrayList<>();

        if (script == null || script.trim().isEmpty()) {
            return statements;
        }

        script = removeComments(script);

        StringBuilder current = new StringBuilder();

        boolean insideString = false;

        int beginDepth = 0;

        String[] lines = script.split("\\R");

        for (String line : lines) {

            String trimmed = line.trim();

            if (trimmed.isEmpty()) {
                continue;
            }

            if (trimmed.equals("/")) {

                if (beginDepth > 0) {

                    statements.add(current.toString().trim());

                    current.setLength(0);

                    beginDepth = 0;
                }

                continue;
            }

            current.append(line).append('\n');

            for (int i = 0; i < line.length(); i++) {

                char c = line.charAt(i);

                if (c == '\'') {

                    insideString = !insideString;

                    continue;
                }
            }

            if (!insideString) {

                String upper = trimmed.toUpperCase();

                if (upper.startsWith("BEGIN")) {
                    beginDepth++;
                }

                if (beginDepth == 0 && trimmed.endsWith(";")) {

                    current.setLength(current.length() - 2);

                    statements.add(current.toString().trim());

                    current.setLength(0);
                }
            }
        }

        if (!current.toString().trim().isEmpty()) {

            statements.add(current.toString().trim());
        }

        return statements;
    }

    private String removeComments(String sql) {

        sql = sql.replaceAll("(?s)/\\*.*?\\*/", "");

        sql = sql.replaceAll("(?m)^\\s*--.*$", "");

        return sql;
    }

}