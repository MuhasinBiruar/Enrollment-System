/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.biruarenrollmentsystem;

import static com.mycompany.biruarenrollmentsystem.BiruarEnrollmentSystem.st;
//import static com.mycompany.biruarenrollmentsystem.Utility.autoId;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author mxebiruar
 */
public class Subjects extends BiruarEnrollmentSystem{
    int subjid;
    String SubjCode;
    String SubjDesc;
    int SubjUnits;
    String Schedule;
    
    Subjects() {
        connectDB();
    }
    public void connectDB() {
        DBconnect();
    }
    
    public void SaveRecord(String id, String code, String desc, int units, String sched) throws SQLException { //change to 7 params
        
        //int nextID = autoId(2001, "Subjects", "subjid");
        int nextID = 2001;
        String maxIdQuery = String.format("SELECT MAX(%s) FROM %s;", "subjid", "Subjects");
        ResultSet resSet = st.executeQuery(maxIdQuery);
        if (resSet.next()) {
            int currentMax = resSet.getInt(1);
            if (!resSet.wasNull()) {
                nextID = currentMax + 1;
            }
        }
        //autoID
        
        
        String query = "INSERT INTO Subjects values(" + nextID + ", '" + code + "', '" + desc 
                + "', '" + units + "', '" + sched + "');";
        try{
            st.executeUpdate(query);
            System.out.println("Insert success!!");
        } catch(Exception ex){
            System.out.println("failed " + ex);
        }
    }

    public void DeleteRecord(int id) {
        String query = "delete from Subjects where subjid=" + id;
        try{
            st.executeUpdate(query);
            System.out.println("Deletion success");
        } catch(Exception ex){
            System.out.println("failed deletion " + ex);
        }
    }

    public void UpdateRecord(int id, String code, String desc, int units, String sched) {
        String query = "UPDATE Subjects SET SubjCode='" + code + "', SubjDesc='" + desc + "', SubjUnits='" + units + "', Schedule='" 
                + sched + "' WHERE subjid=" + id + ";";
        try {
            st.executeUpdate(query);
            System.out.println("Successfully updated record");
        } catch (Exception ex) {
            System.out.println("Failed to update record: " + ex);
        }
    }
    
}
