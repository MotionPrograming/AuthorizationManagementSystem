package com.ams.config;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class DatabaseConnectionTest {

    public static void main(String[] args) {

        try (
            Connection connection = DBConnection.getConnection();
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT COUNT(*) FROM USERS")
        ) {

            System.out.println("Database Connected Successfully");

            if (rs.next()) {
                int totalUsers = rs.getInt(1);
                System.out.println("Total Users : " + totalUsers);
            }

        } catch (Exception e) {
            System.out.println("Database Connection Failed!");
            e.printStackTrace();
        }
    }
}