package com.ams.migration;


public class Main {


    public static void main(String[] args) {


        MigrationRunner runner =
                new MigrationRunner();



        try {


            if(args.length == 0){

                printHelp();

                return;
            }



            String command =
                    args[0].toLowerCase();



            switch(command){


                case "migrate":

                    runner.migrate();

                    break;



                case "rollback":

                    runner.rollback();

                    break;



                case "status":

                    runner.status();

                    break;



                case "validate":

                    runner.validate();

                    break;



                default:

                    System.out.println(
                            "Unknown command: "
                            + command);


                    printHelp();

            }



        } catch(Exception e){


            System.err.println(
                    "\nMigration failed");


            e.printStackTrace();

        }

    }





    private static void printHelp(){


        System.out.println(
                """

                ============================
                AMS Migration Tool
                ============================


                Available Commands:


                migrate
                    Execute pending migrations


                rollback
                    Rollback latest migration


                status
                    Show migration status


                validate
                    Validate migration files



                Example:


                java Main migrate


                ============================

                """
        );

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