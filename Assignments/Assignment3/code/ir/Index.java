/*  
 *   This file is part of the computer assignment for the
 *   Information Retrieval course at KTH.
 * 
 *   Johan Boye, 2017
 */  

package ir;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ArrayList;

/**
 *  Defines some common data structures and methods that all types of
 *  index should implement.
 */
public interface Index {

    /** Mapping from document identifiers to document names. */
    public HashMap<Integer,String> docNames = new HashMap<Integer,String>();
    
    /** Mapping from document identifier to document length. */
    public HashMap<Integer,Integer> docLengths = new HashMap<Integer,Integer>();

    //Store the query initial values.
    public ArrayList<Double> queryWeights = new ArrayList<Double>();

    /**For assignment 3 we need to compute the relevant feedback algorithm.
        This imply that we should be able to store the tf-idf score per each term-document.
        I decide to use a HashMap. Key: will be all the terms and the value will be basically 
        the document id- score (array)
    */
    //We also need to update the weights regarding the query.
    public HashMap<String, HashMap<Integer, Double>> tf_idf_scores = new HashMap<String, HashMap<Integer, Double>>();


    //Store the PageRank for the 2.7
    public ArrayList<Double> pageRank_values = new ArrayList<Double>();
    public ArrayList<Integer> pageRank_docid = new ArrayList<Integer>();

    /** Inserts a token into the index. */
    public void insert( String token, int docID, int offset );

    /** Returns the postings for a given term. */
    public PostingsList getPostings( String token );
    
    public void PrintPostings(String token);

    /** This method is called on exit. */
    public void cleanup();
    
}
		    
