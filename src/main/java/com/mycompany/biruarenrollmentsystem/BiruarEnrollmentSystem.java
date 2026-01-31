/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Project/Maven2/JavaApp/src/main/java/${packagePath}/${mainClassName}.java to edit this template
 */

package com.mycompany.biruarenrollmentsystem;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Year;
import java.sql.Statement;

/**
 *
 * @author mxebiruar
 */
public class BiruarEnrollmentSystem {

    static Connection con;
    static Statement st;
    static ResultSet rs;
    
    static String db;
    static String uname;
    static String pswd;
    
    public static void main(String[] args) {
        //DBconnect();
        Login b = new Login();
        b.setVisible(true);
    }
    
    public static void DBconnect() {
        int currYear = Year.now().getValue(), nextYear = currYear+1;
        
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            // 2) CONNECT TO THE SERVER (no default DB)
            try (java.sql.Connection serverConn = java.sql.DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/?zeroDateTimeBehavior=CONVERT_TO_NULL",
                    uname, pswd); java.sql.Statement serverSt = serverConn.createStatement()) {

                serverSt.executeUpdate(
                        "CREATE DATABASE IF NOT EXISTS `" + db + "` "
                        + "DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci"
                );
            }

            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/" + db + "?zeroDateTimeBehavior=CONVERT_TO_NULL",uname, pswd);
            st = con.createStatement();
            System.out.println("Connected to DB: " + db);
            

        } catch (ClassNotFoundException | SQLException ex) {
            System.out.println("Failed to Connect " + ex);
        }
        
    }
}
