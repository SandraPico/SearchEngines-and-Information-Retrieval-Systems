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

    /**
     *  PostingsEntries are compared by their score (only relevant
     *  in ranked retrieval).
     *
     *  The comparison is defined so that entries will be put in 
     *  descending order.
     */
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
    
    public void setScore(double score){
        this.score = score;
    }
}

    
