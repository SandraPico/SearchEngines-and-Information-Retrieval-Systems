/*  
 *   This file is part of the computer assignment for the
 *   Information Retrieval course at KTH.
 * 
 *   Johan Boye, 2017
 */  

package ir;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.io.Serializable;

public class PostingsEntry implements Comparable<PostingsEntry>, Serializable {
    
    //Properties/Atributes.
    public int docID;
    public ArrayList<Integer> offsetlist = new ArrayList<Integer>(); //Save all the positions of the word in each document id.
    public double score = 0;

    public double tf_idf_score = 0;

    //Must be filled with the HashMap table with docLenghts.
    public int len_document = 0;

    /**
     *  PostingsEntries are compared by their score (only relevant
     *  in ranked retrieval).
     *
     *  The comparison is defined so that entries will be put in 
     *  descending order.
     */
    
    public PostingsEntry (int docID, int offset){
        this.docID = docID;
        this.offsetlist.add(offset);
    }
    
    public PostingsEntry(){
        
    }
    
    public int compareTo( PostingsEntry other ) {
        return Double.compare( other.score, score );
    }
    
    public String toStringArray(){
        return offsetlist.toString();
    }
    public int getDocID() {
        return docID;
    }
    
    public void setDocID(int docID) {
        this.docID = docID;
    }

    public void setOffset(int offset){
        offsetlist.add(offset);
    }
    
    public ArrayList<Integer> getOffset(){
        return offsetlist;
    }
    
    public double getScore(){
        return score;
    }

    public int getLenDocument(){
        return len_document;
    }

    public void setLenDocument(int len_document){
        this.len_document = len_document;
    }
    
    public void setScore(double score){
        this.score = score;
    }

    public double getTf_idf_Score(){
        return tf_idf_score;
    }

    public void setTf_idf_Score(double tf_idf_score){
        this.tf_idf_score = tf_idf_score;
    }
}

    
