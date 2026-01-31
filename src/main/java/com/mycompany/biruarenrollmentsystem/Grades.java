/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.biruarenrollmentsystem;

import static com.mycompany.biruarenrollmentsystem.BiruarEnrollmentSystem.st;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author asian
 */
public class Grades extends BiruarEnrollmentSystem{
    public void insert(int eid, String prel, String mid, String pref, String finalGrade)throws SQLException{
        //int nextID = autoId(1001, "Students", "studid");
        int nextID = 1;
        String maxIdQuery = String.format("SELECT MAX(%s) FROM %s;", "GradeID", "grades");
        ResultSet resSet = st.executeQuery(maxIdQuery);
        if (resSet.next()) {
            int currentMax = resSet.getInt(1);
            if (!resSet.wasNull()) {
                nextID = currentMax + 1;
            }
        }
        //autoId
        String insert = String.format("INSERT INTO grades VALUES(%d, %d, '%s', '%s', '%s', '%s');", nextID, eid, prel, mid, pref, finalGrade);
        
        try{
            st.executeUpdate(insert);
        }
        catch(SQLException ex){
            update(eid, prel, mid, pref, finalGrade);
            System.out.println("Preceeded to update: " + ex);
        }
    }
    
    public void update(int eid, String prel, String mid, String pref, String finalGrade){
        
        String upd = String.format("UPDATE grades set Prelim = '%s', Midterm = '%s', Prefinal = '%s', Final = '%s' WHERE eid = %d;", prel, mid, pref, finalGrade, eid);
        
        try{
            st.executeUpdate(upd);
        }
        catch(SQLException ex){
            System.out.println("Unable to update grases: " + ex);
        }
        
    }
}
