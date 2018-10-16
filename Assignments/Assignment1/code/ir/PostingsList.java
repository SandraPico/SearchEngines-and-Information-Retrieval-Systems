/*  
 *   This file is part of the computer assignment for the
 *   Information Retrieval course at KTH.
 * 
 *   Johan Boye, 2017
 */  

package ir;

import java.util.ArrayList;

public class PostingsList {
    
    /** The postings list */
    private ArrayList<PostingsEntry> list = new ArrayList<PostingsEntry>();


    /** Number of postings in this list. */
    public int size() {
        return list.size();
    }

    /** Returns the ith posting. */
    public PostingsEntry get( int i ) {
        return list.get(i);
    }

    //Insert another docID and the position of the particular token in the docID.
    public void insert(int docID, int offset){
        int existID = 0;
        int i = 0;
        //To optimize the search we are going to use skip pointers.
        while(i < list.size()){
            if (list.get(i).docID == docID){
                existID = 1;
                list.get(i).setOffset(offset);
                break;
            //Try to implement skip pointers.
            }else{
                if ((i+20)< list.size()){
                    if (list.get(i+20).docID< docID){
                        i = i + 20;
                    }else{
                        i = i + 1;
                    }
                }else{
                    i = i + 1;
                }
            }
        }
        //If it is a new docID
        if (existID == 0){
            PostingsEntry postEntry = new PostingsEntry();
            postEntry.setDocID(docID);
            postEntry.setOffset(offset);
            list.add(postEntry);
        }
    }
    
    
    
    
    
}
