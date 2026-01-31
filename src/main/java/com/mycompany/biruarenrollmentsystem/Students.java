/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.biruarenrollmentsystem;

//import static com.mycompany.biruarenrollmentsystem.Utility.autoId;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author mxebiruar
 */
public class Students extends BiruarEnrollmentSystem{
    int studid;
    String Name;
    String Address;
    String Course;
    String Gender;
    String Yearlvl;
    
    Students() {
        connectDB();
    }
    public void connectDB() {
        DBconnect();
    }
    
    public void SaveRecord(String id, String name, String address, String course, String gender, String yearLvl) throws SQLException { //change to 7 params
        
        //int nextID = autoId(1001, "Students", "studid");
        int nextID = 1001;
        String maxIdQuery = String.format("SELECT MAX(%s) FROM %s;", "studid", "Students");
        ResultSet resSet = st.executeQuery(maxIdQuery);
        if (resSet.next()) {
            int currentMax = resSet.getInt(1);
            if (!resSet.wasNull()) {
                nextID = currentMax + 1;
            }
        }
        //autoId
        
        String query = "INSERT INTO Students values(" + nextID + ", '" + name + "', '" + address 
                + "', '" + course + "', '" + gender + "', '" + yearLvl + "');";
        try{
            st.executeUpdate(query);
            System.out.println("Insert success!!");
        } catch(Exception ex){
            System.out.println("failed " + ex);
        }
        
        String newUserQuery = String.format("CREATE USER IF NOT EXISTS '%s'@'localhost' IDENTIFIED BY '%s';",(nextID+name),("AdDU"+name));
        
            st.executeUpdate(newUserQuery);
            st.executeUpdate(String.format("GRANT SELECT ON `" + db + "`.* TO '%s'@'localhost';", (nextID + name)));
    }

    public void DeleteRecord(int id){
        String query = "delete from Students where studid=" + id;
        try{
            st.executeUpdate(query);
            System.out.println("Deletion success");
        } catch(Exception ex){
            System.out.println("failed deletion " + ex);
        }

    }

    public void UpdateRecord(int id, String name, String address, String course, String gender, String yearLvl) {
        String query = "UPDATE students SET name='" + name + "', address='" + address + "', course='" + course + "', gender='" + gender + "', yearLvl='" + yearLvl + "' WHERE studid=" + id + ";";
        try {
            st.executeUpdate(query);
            System.out.println("Successfully updated record");
        } catch (Exception ex) {
            System.out.println("Failed to update record: " + ex);
        }
        
    }
}


