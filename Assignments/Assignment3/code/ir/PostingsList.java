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

    public double Find_di(int doc_id, PostingsList list_term){
        int trobat = 0;
        int i = 0;
        double score = 0.0;
        while (trobat == 0 && i<list_term.size()){
            if (list_term.get(i).getDocID() == doc_id){
                trobat = 1;
                score = list_term.get(i).getTf_idf_Score();
            }
            i = i + 1;
        }
        return score;
    }

    public void ComputeSimilarityScore(ArrayList<PostingsList> list2, ArrayList<Double> query_score){
        //Representing each term...
        for (int i = 0; i < list.size(); i++){
            int doc_id = list.get(i).getDocID();
            double up_term = 0.0;
            double di = 0.0;
            int len_document = 0;
            for (int j = 0; j < query_score.size(); j++){
                di = 0.0;
                di =  Find_di(doc_id,list2.get(j));
                up_term = up_term + (query_score.get(j)*di);
            }
            list.get(i).setScore(up_term);
        }
    }

    //Tf-idf score per more than one document.
    public void CalculateScore(){
        for (int i = 0; i < list.size(); i++){
            int tf_dt = list.get(i).getOffset().size();
            int len_d = list.get(i).getLenDocument();
            int N = 17483;
            int df_t = list.size();
            double idf_t = Math.log10(N/df_t);
            double tf_idf_score = (tf_dt*idf_t)/(double)len_d;
            list.get(i).setTf_idf_Score(tf_idf_score);
            list.get(i).setScore(tf_idf_score);
        }
    }

    //This function only works per 1 element in the query.
    //Calculate the tf-idf score per all elements in the list.
    public void CalculateTfIdfScore(){
        for (int i = 0; i < list.size(); i++){
            PostingsEntry aux = list.get(i);
            int tf_df = aux.getOffset().size();
            int len_d = aux.getLenDocument();
            int df_t = list.size();
            int N = 17483;
            double idf_t = Math.log10(N/df_t);
            double tf_idf = (tf_df)*idf_t/len_d;
            list.get(i).setTf_idf_Score(tf_idf);
            list.get(i).setScore(tf_idf);
        }
    }

    public void InsertAnswer(int docID){
        int existID = 0;
        for(int i = 0; i < list.size(); i++){
            if (list.get(i).docID == docID){
                existID = 1;
            }
        }
        if (existID == 0){
            PostingsEntry postEntry = new PostingsEntry();
            postEntry.setDocID(docID);
            list.add(postEntry);
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
                    if (list.get(i+20).docID < docID){
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

    public void add_PostingList(PostingsEntry a){
      list.add(a);
    }   
}
