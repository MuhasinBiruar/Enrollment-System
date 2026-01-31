/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.biruarenrollmentsystem;

import static com.mycompany.biruarenrollmentsystem.BiruarEnrollmentSystem.pswd;
import static com.mycompany.biruarenrollmentsystem.BiruarEnrollmentSystem.st;
import static com.mycompany.biruarenrollmentsystem.BiruarEnrollmentSystem.uname;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JOptionPane;
import weka.classifiers.Classifier;
import weka.classifiers.trees.J48;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SerializationHelper;
import static weka.core.Utils.maxIndex;
import weka.core.converters.ConverterUtils.DataSource;
import weka.experiment.InstanceQuery;

/**
 *
 * @author asian
 */
public class Predictions extends BiruarEnrollmentSystem {
    private String getPreviousSchema(String currentSchema) {
    // Example: 1st_sy2026_2027
    String[] parts = currentSchema.split("_");
    String sem = parts[0]; // 1st / 2nd / summer
    int start = Integer.parseInt(parts[1].substring(2, 6));
    int end = Integer.parseInt(parts[2]);

    switch (sem.toLowerCase()) {
        case "1st":  return "1st_sy" + (start - 1) + "_" + (end - 1) + ".model"; //temp
        case "2nd":  return "2nd_sy" + (start-1) + "_" + (end-1) + ".model";
        case "summer": return "summer_sy" + (start-1) + "_" + (end-1) + ".model";
        default: return null;
    }
}
    private String getPreviousSchemaArff(String currentSchema) {
    // Example: 1st_sy2026_2027
    String[] parts = currentSchema.split("_");
    String sem = parts[0]; // 1st / 2nd / summer
    int start = Integer.parseInt(parts[1].substring(2, 6));
    int end = Integer.parseInt(parts[2]);

    switch (sem.toLowerCase()) {
        case "1st":  return "1st_sy" + (start - 1) + "_" + (end - 1) + ".arff"; //temp
        case "2nd":  return "2nd_sy" + (start-1) + "_" + (end-1) + ".arff";
        case "summer": return "summer_sy" + (start-1) + "_" + (end-1) + ".arff";
        default: return null;
    }
}
    
    
    private int nextEid(String dbName) throws Exception {
        String sql = "SELECT MAX(eid) AS max FROM `" + dbName + "`.enroll";
        rs = st.executeQuery(sql);

        int nextId = 1; // default if table is empty
        if (rs.next()) {
            String maxId = rs.getString("max");
            if (maxId != null) {
                nextId = Integer.parseInt(maxId) + 1;
            }
        }
        return nextId;
    }

    
    public void predictEnroll(String studid, String dbName) throws Exception{
        //load trained subject prediction
        String modelFile = getPreviousSchema(dbName);
        
        String arffFile = getPreviousSchemaArff(dbName);
        
        if(arffFile == null || modelFile == null)
            return;
        
        Classifier subTree = (Classifier) SerializationHelper.read(modelFile);
        
        //load arff
        DataSource source = new DataSource(arffFile);
        Instances header = source.getDataSet();
        header.setClassIndex(header.numAttributes() - 1);
        
        
        
        String sql = "select * from `"+ dbName +"`.students;";
        rs = st.executeQuery(sql);
        
        while(rs.next()){
            String studidRs = rs.getString("studid");
            String gender = rs.getString("Gender");
            String course = rs.getString("Course");
            String yrlvl = rs.getString("Yearlvl");
            
            if (studidRs.equals(studid)) {
                // create new instance for this student
                Instance inst = new DenseInstance(header.numAttributes());
                inst.setDataset(header);

                inst.setValue(header.attribute("gender"), gender);
                inst.setValue(header.attribute("yearlvl"), yrlvl);
                inst.setValue(header.attribute("course"), course);
                inst.setMissing(header.classIndex()); // target: subjid

                // Predict distribution across ALL subjid classes
                double[] dist = subTree.distributionForInstance(inst);

                // Loop over all possible subjects
                for (int i = 0; i < dist.length; i++) {
                    double confidence = dist[i];
                    String predictedSubjID = header.classAttribute().value(i);

                    if (confidence >= 0.05) { // only take subjects with ≥5% confidence
                        int eid = nextEid(dbName); // generate next available eid

                        String insertSql = String.format(
                                "INSERT INTO `%s`.enroll (eid, studid, subjid) VALUES (%d, '%s', '%s');",
                                dbName, eid, studidRs, predictedSubjID
                        );

                        Statement st2 = con.createStatement();
                        st2.executeUpdate(insertSql);

                        System.out.println("Enrolled studid " + studidRs
                                + " subjid " + predictedSubjID
                                + " (eid=" + eid + ", confidence=" + (confidence * 100) + "%)");
                    }
                }
            }
        }
    }
}
