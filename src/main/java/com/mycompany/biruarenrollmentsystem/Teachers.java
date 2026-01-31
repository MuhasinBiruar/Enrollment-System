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
public class Teachers extends BiruarEnrollmentSystem{
    int Tid;
    String Name;
    String Address;
    String Contact;
    String Status;
    String Dept;
    
    Teachers() {
        connectDB();
    }
    public void connectDB() {
        DBconnect();
    }
    
    public void SaveRecord(String id, String name, String address, String contact, String status, String dept) throws SQLException { //change to 7 params
       
        
        //int nextID = autoId(3001, "Teachers", "Tid");
        int nextID = 3001;
        String maxIdQuery = String.format("SELECT MAX(%s) FROM %s;", "Tid", "Teachers");
        ResultSet resSet = st.executeQuery(maxIdQuery);
        if (resSet.next()) {
            int currentMax = resSet.getInt(1);
            if (!resSet.wasNull()) {
                nextID = currentMax + 1;
            }
        }
        //autoID
        
        String query = "INSERT INTO Teachers values(" + nextID + ", '" + name + "', '" + address 
                + "', '" + contact + "', '" + status + "', '" + dept + "');";
        try{
            st.executeUpdate(query);
            System.out.println("Insert success!!");
        } catch(Exception ex){
            System.out.println("failed " + ex);
        }
        
        String newUserQuery = String.format("CREATE USER IF NOT EXISTS '%s'@'localhost' IDENTIFIED BY '%s';",(nextID+name),("AdDU"+name));
        
            st.executeUpdate(newUserQuery);
            st.executeUpdate(String.format("GRANT SELECT,UPDATE,INSERT ON `" + db + "`.* TO '%s'@'localhost';", (nextID + name)));
    }

    public void DeleteRecord(int id) {
        String query = "delete from Teachers where Tid=" + id;
        try{
            st.executeUpdate(query);
            System.out.println("Deletion success");
        } catch(Exception ex){
            System.out.println("failed deletion " + ex);
        }
    }

    public void UpdateRecord(int id, String name, String address, String contact, String status, String dept) {
        String query = "UPDATE Teachers SET Name='" + name + "', Address='" + address + "', Contact='" + contact + "', Status='" + status
                + "', Dept='" + dept + "' WHERE Tid=" + id + ";";
        try {
            st.executeUpdate(query);
            System.out.println("Successfully updated record");
        } catch (Exception ex) {
            System.out.println("Failed to update record: " + ex);
        }
    }
}
