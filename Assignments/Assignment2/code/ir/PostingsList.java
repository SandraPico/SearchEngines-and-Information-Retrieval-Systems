/*  
 *   This file is part of the computer assignment for the
 *   Information Retrieval course at KTH.
 * 
 *   Johan Boye, 2017
 */  

package ir;

import java.util.ArrayList;
import java.util.Collections;
import java.lang.Math;

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

    //Sort the list based on the score achieved in each Posting Entry
    public void SortList(){
        Collections.sort(list);
    }

    public void CalculateSimilarity(double tf_idf_query){
        //Update all the score for all the documents of the list. 
        for (int i = 0; i < list.size(); i ++){
            //cos(q,d) for only 1 term in the query.
            PostingsEntry aux = list.get(i);
            double tf_idf_document = aux.getTf_idf_Score();
            double cos = (tf_idf_query*tf_idf_document)/(Math.sqrt(tf_idf_query*tf_idf_query)*Math.sqrt(tf_idf_document*tf_idf_document));
            list.get(i).setScore(cos);
        }
    }

    //Calculate the tf-idf score per all elements in the list.
    public void CalculateTfIdfScore(){
        for (int i = 0; i < list.size(); i++){
            PostingsEntry aux = list.get(i);
            //Occurrences of the term in the document.
            int tf_df = aux.getOffset().size();
            int len_d = aux.getLenDocument();
            //Documents in the corpus which contains t.
            int df_t = list.size();
            //Number of total documents in the corpus:
            int N = 17481;
            //Compute idf_t
            double idf_t = Math.log(N/df_t);
            double tf_idf = (tf_df * idf_t)/len_d;
            list.get(i).setTf_idf_Score(tf_idf);
        }
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
