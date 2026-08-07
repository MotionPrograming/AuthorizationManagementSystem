package com.ams.config;

import com.ams.migration.MigrationRunner;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;


@WebListener
public class ApplicationStartupListener 
        implements ServletContextListener {


    @Override
    public void contextInitialized(ServletContextEvent sce) {

        System.out.println(
            "===== AMS APPLICATION STARTING ====="
        );


        try {

            MigrationRunner runner =
                    new MigrationRunner();

            runner.migrate();


            System.out.println(
                "===== DATABASE MIGRATION SUCCESS ====="
            );


        } catch(Exception e) {


            System.err.println(
                "===== DATABASE MIGRATION FAILED ====="
            );


            e.printStackTrace();

            throw new RuntimeException(e);
        }
    }
}