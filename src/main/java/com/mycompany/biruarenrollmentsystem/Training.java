/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.biruarenrollmentsystem;

/**
 *
 * @author asian
 */

import java.sql.SQLException;
import javax.swing.JOptionPane;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.classifiers.Classifier;
import weka.classifiers.trees.J48; // Decision Tree
import weka.core.SerializationHelper;
import static weka.core.Utils.maxIndex;
import weka.experiment.InstanceQuery;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.converters.ArffLoader;
import weka.core.converters.ArffSaver;
import weka.filters.unsupervised.attribute.StringToWordVector;


public class Training extends BiruarEnrollmentSystem {
    
    private Instances loadInstances(String dbName, String sql, int classIndex) throws Exception {
        InstanceQuery query = new InstanceQuery();
        query.setDatabaseURL("jdbc:mysql://localhost:3306/" + dbName);
        query.setUsername(uname);   // from BiruarEnrollmentSystem
        query.setPassword(pswd);    // from BiruarEnrollmentSystem
        query.setQuery(sql);

        Instances data = query.retrieveInstances();
        data.setClassIndex(classIndex);
        return data;
}
    
    public void enrollTrain(String dbName) throws Exception{
        try{
            Instances studData = loadInstances(dbName, "SELECT gender, course, yearlvl, CAST(subjid AS CHAR) as subjid "
                    + "FROM students s INNER JOIN enroll e ON e.studid = s.studid order by e.eid;", 3);
            
            ArffSaver studentArff = new ArffSaver();
            studentArff.setInstances(studData);
            studentArff.setFile(new File(dbName + ".arff"));
            studentArff.writeBatch();
            
            J48 subTree = new J48();
            subTree.buildClassifier(studData);
            
            SerializationHelper.write(dbName + ".model", subTree);
            
            JOptionPane.showMessageDialog(null, "Training success for " + dbName);
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
    
    private static final String fac = "Facilities";
    private static final String lab = "Laboratory";
    private static final String inst = "Instruction";
    private static final String cur = "Curriculum";
    private static final String eval = "Evaluation";
    
    private static final List<String> facilityKeywords = Arrays.asList(
            "classroom", "classrooms", "faculty", "faculties", "library", "libraries",
            "cr", "bathroom", "room", "facility", "facilities", "small", "noisy",
            "clean", "dirty", "equipment", "class"); // Added potential related terms

    private static final List<String> laboratoryKeywords = Arrays.asList(
            "laboratory", "lab", "labs");

    private static final List<String> instructionKeywords = Arrays.asList(
            "rule", "rules", "ruleset", "rulesets", "instruction", "instructions",
            "teacher", "professor", "instructor", "teaching", "explain", "clarity"); // Added potential related terms

    private static final List<String> curriculumKeywords = Arrays.asList(
            "group project", "group projects", "project", "projects", "assignment",
            "assignments", "curriculum", "syllabus", "course material", "homework",
            "subject", "topic");
    
    private Classifier categoryClassifier; // Loaded only if using model
    private StringToWordVector stwvFilter; // Loaded only if using model
    private Instances filteredHeader;      // Loaded only if using model
    private Instances inputHeader;         // Structure before filtering
    private Attribute classAttribute;      // Info about the category attribute
    private Attribute textAttribute;       // Info about the text attribute

   
    private static boolean useWekaModel = false;
    private static boolean initializationOk = false;
    private final int MIN_INSTANCES_FOR_MODEL = 50;
    
    private String rawArffFilename = "student_feedback_" + db + ".arff"; // File to check size
    private String modelFilename = "feedback_category_" + db + ".model";
    private String filterFilename = "feedback_filter_" + db + ".filter";
    private String dbIdentifier = db;
    
    public void CommentCategorizerHybrid(String dbName) {
        this.rawArffFilename = "student_feedback_" + dbName + ".arff"; // File to check size
        this.modelFilename = "feedback_category_" + dbName + ".model";
        this.filterFilename = "feedback_filter_" + dbName + ".filter";

        try {
            // 1. Check ARFF file size
            File arffFile = new File(rawArffFilename);
            if (arffFile.exists()) {
                ArffLoader loader = new ArffLoader();
                loader.setFile(arffFile);
                Instances trainingData = loader.getDataSet(); // Load data to check size
                System.out.println("Checking ARFF file: " + rawArffFilename + ", Instances found: " + trainingData.numInstances());

                if (trainingData.numInstances() >= MIN_INSTANCES_FOR_MODEL) {
                    System.out.println("Num of instances " + trainingData.numInstances());
                    useWekaModel = true;
                    System.out.println("Sufficient data found (" + trainingData.numInstances() + " >= " + MIN_INSTANCES_FOR_MODEL + "). Attempting to load Weka model and filter.");

                    // 2. If enough data, load Model and Filter
                    categoryClassifier = (Classifier) SerializationHelper.read(new FileInputStream(modelFilename));
                    stwvFilter = (StringToWordVector) SerializationHelper.read(new FileInputStream(filterFilename));

                    // Get header structures
                    filteredHeader = stwvFilter.getOutputFormat(); // Structure AFTER filter
                    if (filteredHeader.classIndex() == -1) {
                        filteredHeader.setClassIndex(filteredHeader.numAttributes() - 1);
                    }
                    classAttribute = filteredHeader.classAttribute();

                    
                    ArrayList<Attribute> inputAttributes = new ArrayList<>();
                    
                    textAttribute = new Attribute("comment_text", (List<String>) null);
                    inputAttributes.add(textAttribute);
                    inputAttributes.add(classAttribute); // Add class attribute structure
                    inputHeader = new Instances("CommentInputStructure", inputAttributes, 0);
                    inputHeader.setClass(classAttribute);

                    System.out.println("Successfully loaded Weka model and filter for prediction.");
                    initializationOk = true;

                } else {
                    System.out.println("Insufficient data found (" + trainingData.numInstances() + " < " + MIN_INSTANCES_FOR_MODEL + "). Using keyword-based categorization.");
                    useWekaModel = false;
                    initializationOk = true; // Initialization is OK, just using keywords
                }
            } else {
                System.out.println("ARFF file not found: " + rawArffFilename + ". Using keyword-based categorization.");
                useWekaModel = false;
                initializationOk = true; // Initialization is OK, just using keywords
            }

        } catch (Exception e) {
            System.err.println("Error during Hybrid Categorizer initialization for " + dbName + ": " + e.getMessage());
            e.printStackTrace();
            System.err.println("Falling back to keyword-based categorization due to error.");
            useWekaModel = false;
            initializationOk = false; // Indicate initialization failed
        }
    }
    
    public String categorize(String comment) throws Exception {
        /*
        if (!initializationOk && !useWekaModel) { // If init failed and can't use keywords either
             return "yo234234";
        }
*/
        if (comment == null || comment.trim().isEmpty()) {
            return trainCategory(comment); // Let trainCategory handle empty/null return
        }

        if (useWekaModel) {
            // use weka
             try {
                 Instance newInstance = new DenseInstance(inputHeader.numAttributes());
                 newInstance.setDataset(inputHeader);
                 newInstance.setValue(textAttribute, comment);
                 newInstance.setClassMissing();

                 stwvFilter.input(newInstance);
                 Instance filteredInstance = stwvFilter.output();
                 filteredInstance.setDataset(filteredHeader);

                 double predictedClassIndex = categoryClassifier.classifyInstance(filteredInstance);
                 return classAttribute.value((int) predictedClassIndex);

             } catch (Exception e) {
                 System.err.println("Error predicting category using Weka model: " + e.getMessage());
                 e.printStackTrace();
                 System.err.println("Falling back to keyword categorization due to prediction error.");
             }
        }

        String category = trainCategory(comment); 

        try {
            // Load existing ARFF or create new arff
            Instances dataToSave;
            File arffFile = new File(rawArffFilename);

          
            ArrayList<Attribute> attributes = new ArrayList<>();
            Attribute currentTextAttribute = new Attribute("comment_text", (List<String>) null);
            attributes.add(currentTextAttribute);
            ArrayList<String> categoryValues = new ArrayList<>(Arrays.asList(fac, lab, inst, cur, eval));
            Attribute currentCategoryAttribute = new Attribute("predicted_category", categoryValues);
            attributes.add(currentCategoryAttribute);
            Instances structure = new Instances("CategorizedFeedback_" + dbIdentifier, attributes, 0);
            structure.setClass(currentCategoryAttribute); // Set class attribute

            if (arffFile.exists()) {
                // Load existing data
                ArffLoader loader = new ArffLoader();
                loader.setFile(arffFile);
                dataToSave = loader.getDataSet();
                dataToSave.setClass(currentCategoryAttribute); // Ensure class index is set correctly after load
                 System.out.println("Loaded existing ARFF: " + rawArffFilename + " with " + dataToSave.numInstances() + " instances.");

            } else {
                // Create new dataset with the defined structure
                dataToSave = new Instances(structure, 0); // Use structure defined above
                System.out.println("Creating new ARFF file: " + rawArffFilename);
            }


            // Create a new instance for the current comment
            Instance categorizedInstance = new DenseInstance(dataToSave.numAttributes());
            categorizedInstance.setDataset(dataToSave); // Link to dataset structure
            categorizedInstance.setValue(currentTextAttribute, comment); // Set comment text
            categorizedInstance.setValue(currentCategoryAttribute, category); // Set determined category

            // Add the new instance
            dataToSave.add(categorizedInstance);
            System.out.println("Added new instance. Total instances now: " + dataToSave.numInstances());


            // Save the updated dataset back to the ARFF file
            ArffSaver saver = new ArffSaver();
            saver.setInstances(dataToSave);
            saver.setFile(arffFile); // Use the File object
            saver.writeBatch();
            System.out.println("Saved updated data to: " + rawArffFilename);

            if (!useWekaModel && dataToSave.numInstances() >= MIN_INSTANCES_FOR_MODEL) {
                System.out.println("INFO: ARFF instance threshold (" + MIN_INSTANCES_FOR_MODEL + ") met or exceeded. Consider running training process.");
            }


        } catch (IOException ioEx) {
            System.err.println("Error loading/saving ARFF file " + rawArffFilename + ": " + ioEx.getMessage());
            ioEx.printStackTrace();
        } catch (Exception wekaEx) {
             System.err.println("Error processing Weka data for ARFF append: " + wekaEx.getMessage());
             wekaEx.printStackTrace();
        }

        return category;
    }
    
    public String trainCategory(String eval)throws Exception{
        if (eval == null || eval.trim().isEmpty()) {
            return this.eval;
        }
        String lowerCaseComment = eval.toLowerCase();
        
        for (String keyword : instructionKeywords) {
            if (lowerCaseComment.contains(keyword)) {
                return inst;
            }
        }
        for (String keyword : curriculumKeywords) {
            if (lowerCaseComment.contains(keyword)) {
                return cur;
            }
        }
        for (String keyword : laboratoryKeywords) {
            if (lowerCaseComment.contains(keyword)) {
                return lab;
            }
        }
        for (String keyword : facilityKeywords) {
            if (lowerCaseComment.contains(keyword)) {
                return fac;
            }
        }

        return this.eval;
    }
}
