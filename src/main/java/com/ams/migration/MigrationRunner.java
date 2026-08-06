package com.ams.migration;

import com.ams.config.DBConnection;

import java.sql.Connection;
import java.util.Collections;
import java.util.List;


public class MigrationRunner {


    private final MigrationScanner scanner;

    private final MigrationValidator validator;

    private final MigrationExecutor executor;

    private final MigrationHistoryRepository historyRepository;



    public MigrationRunner() {


        this.scanner =
                new MigrationScanner();


        this.validator =
                new MigrationValidator();


        this.executor =
                new MigrationExecutor();


        this.historyRepository =
                new MigrationHistoryRepository();

    }



    // =================================
    // MIGRATE UP
    // =================================

    public void migrate() throws Exception {


        try(Connection connection =
                DBConnection.getConnection()) {


            connection.setAutoCommit(false);



            List<Migration> migrations =
                    scanner.scan();



            validator.validate(
                    migrations);



            executor.execute(
                    connection,
                    migrations);



            System.out.println(
                    "\nMigration completed successfully.");

        }

    }



    // =================================
    // ROLLBACK LAST MIGRATION
    // =================================


    public void rollback() throws Exception {


        try(Connection connection =
                DBConnection.getConnection()) {


            connection.setAutoCommit(false);



            List<Migration> migrations =
                    scanner.scan();



            Collections.reverse(
                    migrations);



            for(Migration migration : migrations){


                if(historyRepository
                        .isApplied(
                            connection,
                            migration.getVersion())){


                    executor.rollback(
                            connection,
                            migration);


                    break;

                }

            }


            System.out.println(
                    "\nRollback completed.");

        }

    }



    // =================================
    // STATUS
    // =================================


    public void status() throws Exception {


        try(Connection connection =
                DBConnection.getConnection()) {



            List<String> applied =
                    historyRepository
                    .findAll(connection);



            System.out.println(
                    "\nMigration Status");

            System.out.println(
                    "------------------------");



            for(String version : applied){


                System.out.println(
                        version
                        + "  [APPLIED]");

            }

        }

    }



    // =================================
    // VALIDATE
    // =================================


    public void validate() throws Exception {


        List<Migration> migrations =
                scanner.scan();



        validator.validate(
                migrations);


    }

}