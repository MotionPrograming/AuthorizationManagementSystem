package com.ams.migration;

import java.nio.file.Path;
import java.sql.Connection;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class MigrationExecutor {


    private final SqlScriptReader reader;

    private final SqlScriptParser parser;

    private final SqlExecutor executor;

    private final MigrationHistoryRepository historyRepository;


    public MigrationExecutor() {

        this.reader = new SqlScriptReader();

        this.parser = new SqlScriptParser();

        this.executor = new SqlExecutor();

        this.historyRepository =
                new MigrationHistoryRepository();
    }


    // ==========================
    // RUN MIGRATIONS
    // ==========================

    public void execute(
            Connection connection,
            List<Migration> migrations)
            throws Exception {


        historyRepository
                .createTableIfNotExists(connection);


        for (Migration migration : migrations) {


            if(historyRepository
                    .isApplied(
                            connection,
                            migration.getVersion())) {


                System.out.println(
                        "[SKIP] "
                        + migration.getVersion());

                continue;
            }


            System.out.println(
                    "[START] "
                    + migration.getVersion()
                    + " - "
                    + migration.getDescription());


            Instant start =
                    Instant.now();


            try {


                executeSingle(
                        connection,
                        migration);


                long time =
                        Duration.between(
                                start,
                                Instant.now())
                                .toMillis();


                historyRepository.save(
                        connection,
                        migration,
                        time);


                connection.commit();


                migration.setStatus(
                        MigrationStatus.APPLIED);


                System.out.println(
                        "[DONE] "
                        + migration.getVersion()
                        + " ("
                        + time
                        + " ms)");



            } catch(Exception ex) {


                connection.rollback();


                migration.setStatus(
                        MigrationStatus.FAILED);


                System.err.println(
                        "[FAILED] "
                        + migration.getVersion());


                throw ex;
            }
        }
    }



    // ==========================
    // EXECUTE SINGLE MIGRATION
    // ==========================


    private void executeSingle(
            Connection connection,
            Migration migration)
            throws Exception {


        Path script =
                migration.getUpScript();


        String sql =
                reader.read(script);


        List<String> statements =
                parser.parse(sql);



        executor.execute(
                connection,
                statements);
    }



    // ==========================
    // ROLLBACK MIGRATION
    // ==========================


    public void rollback(
            Connection connection,
            Migration migration)
            throws Exception {


        if(migration.getDownScript()==null){

            System.out.println(
                    "[SKIP] No rollback script "
                    + migration.getVersion());

            return;
        }



        String sql =
                reader.read(
                    migration.getDownScript());



        List<String> statements =
                parser.parse(sql);



        executor.execute(
                connection,
                statements);



        historyRepository.delete(
                connection,
                migration.getVersion());



        connection.commit();



        migration.setStatus(
                MigrationStatus.ROLLED_BACK);



        System.out.println(
                "[ROLLBACK DONE] "
                + migration.getVersion());
    }

}