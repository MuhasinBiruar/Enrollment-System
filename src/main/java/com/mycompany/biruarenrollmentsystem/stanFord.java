/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.biruarenrollmentsystem;

/**
 *
 * @author asian
 */
import java.util.Properties; // For setting up pipeline properties

import edu.stanford.nlp.pipeline.Annotation; // Represents the text and its annotations
import edu.stanford.nlp.pipeline.StanfordCoreNLP; // The main pipeline class
import edu.stanford.nlp.ling.CoreAnnotations; // Provides keys for accessing annotations (like sentences)
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations; // Provides keys specific to sentiment results
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap; // Represents an annotated piece of text, often a sentence
import java.util.List;

public class stanFord {
    private StanfordCoreNLP sentimentPipeline;

    // --- Constructor to initialize the pipeline for each instance ---
    public stanFord() {
        // Set up pipeline properties
        Properties props = new Properties();
        // Annotators needed for sentiment analysis
        props.setProperty("annotators", "tokenize, ssplit, parse, sentiment");
        try {
            // Build pipeline
            this.sentimentPipeline = new StanfordCoreNLP(props);
            System.out.println("Stanford CoreNLP Sentiment Pipeline Initialized for this instance."); // Log initialization
        } catch (Exception e) {
            System.err.println("Failed to initialize Stanford CoreNLP pipeline: " + e.getMessage());
            e.printStackTrace();
            this.sentimentPipeline = null; // Ensure pipeline is null if initialization fails
        }
    }
    
    public String sentiments(String comment){
        if(this.sentimentPipeline == null)
            return "Error empty sentimentPipiline";
        
        if(comment == null)
            return "neutral";
        
        String overallSentiment = "Neutral"; // Default
        double totalScore = 0;
        int sentenceCount = 0;
        try{
            Annotation anno = new Annotation(comment);
            this.sentimentPipeline.annotate(anno);
            
            List<CoreMap> sentences = anno.get(CoreAnnotations.SentencesAnnotation.class);
            if (sentences != null && !sentences.isEmpty()) {
                for (CoreMap sentence : sentences) {
                    // Get the sentiment tree for the sentence
                    Tree sentimentTree = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
                    
                    int score = RNNCoreAnnotations.getPredictedClass(sentimentTree);
                    totalScore += score;
                    sentenceCount++;
                    
                }

                if (sentenceCount > 0) {
                    double averageScore = totalScore / sentenceCount;
                    // Convert average score back to a label
                    if (averageScore >= 3.5) {
                        overallSentiment = "Very positive";
                    } else if (averageScore >= 2.5) {
                        overallSentiment = "Positive";
                    } else if (averageScore >= 1.5) {
                        overallSentiment = "Neutral";
                    } else if (averageScore >= 0.5) {
                        overallSentiment = "Negative";
                    } else {
                        overallSentiment = "Very negative";
                    }
                }
            } // else default Neutral is kept
        }
        catch(Exception e){
            return e.toString();
        }
        return overallSentiment;
    }
}
