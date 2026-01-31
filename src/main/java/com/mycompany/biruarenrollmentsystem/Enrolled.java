/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.biruarenrollmentsystem;

//import static com.mycompany.biruarenrollmentsystem.Utility.autoId;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;

/**
 *
 * @author mxebiruar
 */
public class Enrolled extends BiruarEnrollmentSystem{
    int eid;
    int studid;
    private static int subjid;
    
    public void setsubjid(int a){
        subjid = a;
    }
    
    public int getsubjid(){
        //int row = SubjectForm.jTable1.getSelectedRow();
        return subjid;
    }
    
    public void checkConflict(int studid, int subjid) throws SQLException{
        
        try {
            st.executeUpdate(String.format("CALL sched_Conf(%d, %d)", studid, subjid));
        } catch (SQLException ex) {
            System.getLogger(Enrolled.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }
    
    public String enrollStud (int studid) throws SQLException{
        //int nextID = autoId(1, "Enroll", "eid"); //not sure if stay here yo
        int nextID = 1;
        String maxIdQuery = String.format("SELECT MAX(%s) FROM %s;", "eid", "Enroll");
        ResultSet resSet = st.executeQuery(maxIdQuery);
        if (resSet.next()) {
            int currentMax = resSet.getInt(1);
            if (!resSet.wasNull()) {
                nextID = currentMax + 1;
            }
        }
        //autoID
        
        if (getsubjid() <= 0) { // or check for null if it's an object: if (getsubjid() == null)
        JOptionPane.showMessageDialog(null, "No subject selected!");
        return null;
        }
        
        // Check if already enrolled
        String checkQuery = "SELECT * FROM Enroll WHERE studid = " + studid + " AND subjid = " + getsubjid() + ";";
        ResultSet check_rs = st.executeQuery(checkQuery);
        if (check_rs.next()) {
            JOptionPane.showMessageDialog(null, "Student " + studid + " is already enrolled in subject " + getsubjid());
            return null; // Already enrolled, do not insert
        }
        
        int response = JOptionPane.showConfirmDialog(null,"Enroll student ID:" + studid + " to subject ID:" + getsubjid(),"alert",JOptionPane.OK_CANCEL_OPTION);
        
        if(response == JOptionPane.OK_OPTION){
            System.out.println("ENROLLED CLASS SUBJID: "+ getsubjid());
            
            
            
            return "INSERT INTO Enroll values(" + nextID + ", '" + studid +"', '" + getsubjid() + "');";
        }
        
        return"Failed/Cancelled Enroll";
    }
    
    public String DropSubj(int studid) throws SQLException{
        if (getsubjid() <= 0) { // or check for null if it's an object: if (getsubjid() == null)
        JOptionPane.showMessageDialog(null, "No subject selected!");
        return null;
        }
        
        int response = JOptionPane.showConfirmDialog(
                null,
                "Drop the subject: " + getsubjid() + " enrolled by students ID:" + studid,
                "Confirm Enrollment",
                JOptionPane.OK_CANCEL_OPTION
        );
        
        if(response == JOptionPane.OK_OPTION){
            System.out.println("DROPPED CLASS SUBJID: "+ getsubjid());
            JOptionPane.showMessageDialog(null,String.format("Student: %s dropped subject: %d", studid, getsubjid()), "enroll",JOptionPane.INFORMATION_MESSAGE);
            return "DELETE FROM Enroll WHERE studid = " + studid + " AND subjid = " + getsubjid() + ";";
        }
        
        return"Failed/Cancelled Drop";
        
    }
}
