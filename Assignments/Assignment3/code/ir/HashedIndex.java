/*  
 *   This file is part of the computer assignment for the
 *   Information Retrieval course at KTH.
 * 
 *   Johan Boye, 2017
 */  


package ir;

import java.util.HashMap;
import java.util.Iterator;


/**
 *   Implements an inverted index as a Hashtable from words to PostingsLists.
 */
public class HashedIndex implements Index {

    /** The index as a hashtable. */
    private HashMap<String,PostingsList> index = new HashMap<String,PostingsList>();

    
    /***  Inserts this token in the hashtable.*/
    public void insert( String token, int docID, int offset) {
        if(!index.containsKey(token)){
            index.put(token,new PostingsList());
            index.get(token).insert(docID,offset);
        }else{
            index.get(token).insert(docID,offset);
        }
    }

    /**
     *  Returns the postings for a specific term, or null
     *  if the term is not in the index.
     */
    public PostingsList getPostings( String token ) {
        if (index.containsKey(token)){
            return index.get(token);
        }else{
            return null;
        }
    }
    
    public void PrintPostings(String token){
        if (index.containsKey(token)){
            PostingsList post = index.get(token);
            for (int i = 0; i < post.size(); i++){
                System.out.println(post.get(i).getDocID());
            }
        }
    }
        
    /**
     *  No need for cleanup in a HashedIndex.
     */
    public void cleanup() {
    }
}
