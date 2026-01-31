/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.biruarenrollmentsystem;

import static com.mycompany.biruarenrollmentsystem.BiruarEnrollmentSystem.st;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;

/**
 *
 * @author mxebiruar
 */
public class Assign extends BiruarEnrollmentSystem{
    private static int subjid;
   
    public void setsubjid(int a){
        this.subjid = a;
    }
    
    public int getsubjid(){
        return subjid;
    }
    
    public String AssignSubj(int tid) throws SQLException{
       
        // Check if already assigned
        if (getsubjid() <= 0) { // or check for null if it's an object: if (getsubjid() == null)
        JOptionPane.showMessageDialog(null, "No subject selected!");
        return null;
        }
        
        String checkQuery = "SELECT * FROM Assign WHERE tid = " + tid + " AND subid = " + getsubjid() + ";";
        ResultSet check_rs = st.executeQuery(checkQuery);
        if (check_rs.next()) {
            JOptionPane.showMessageDialog(null, "Teacher " + tid + " is already assigned in Subject: " + getsubjid());
            return null; // Already enrolled, do not insert
        }
        checkQuery = "SELECT subid FROM Assign WHERE subid=" + getsubjid() + ";";
        check_rs = st.executeQuery(checkQuery);
        if (check_rs.next()){
            JOptionPane.showMessageDialog(null,"Subject:" + getsubjid() + " already assigned", "enroll",JOptionPane.INFORMATION_MESSAGE);
            return null; // Subject already Assigned, do not insert
        }
        
        int response = JOptionPane.showConfirmDialog(null,"Assign teacher ID:" + tid + " to subject ID:" + getsubjid(), "alert",JOptionPane.OK_CANCEL_OPTION);
        
        if(response == JOptionPane.OK_OPTION){
            System.out.println("ENROLLED CLASS SUBJID: "+ getsubjid());
            JOptionPane.showMessageDialog(null, String.format("Teacher %s assigned to %d", tid, getsubjid()), "enroll",JOptionPane.INFORMATION_MESSAGE);
            return "INSERT INTO Assign values(" + getsubjid() +", '" + tid + "');";
        }
        
        return "Failed/Cancelled Assign";

    }
    public String DeleteSubj(int tid)throws SQLException{
        if (getsubjid() <= 0) { // or check for null if it's an object: if (getsubjid() == null)
        JOptionPane.showMessageDialog(null, "No subject selected!");
        return null;
        }
        
        int response = JOptionPane.showConfirmDialog(
                null,
                "Delete the subject: " + getsubjid() + " assigned by teacher ID:" + tid,
                "Confirm Enrollment", // <-- Title of the dialog
                JOptionPane.OK_CANCEL_OPTION
        );
        
        if(response == JOptionPane.OK_OPTION){
            System.out.println("DELETED CLASS SUBJID: "+ getsubjid());
            JOptionPane.showMessageDialog(null,String.format("Teacher: %s deleted subject: %d", tid, getsubjid()), "enroll",JOptionPane.INFORMATION_MESSAGE);
            return "DELETE FROM Assign WHERE tid = " + tid + " AND subid = " + getsubjid() + ";";
        }
        
        return"Failed/Cancelled Deletion"; 
    }
}
