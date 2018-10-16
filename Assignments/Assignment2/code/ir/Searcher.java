/*  
 *   This file is part of the computer assignment for the
 *   Information Retrieval course at KTH.
 * 
 *   Johan Boye, 2017
 */
package ir;
import java.util.ArrayList;
import java.lang.Math;

/**
 *  Searches an index for results of a query.
 */
public class Searcher {

    //This is also implemented through the HashedIndex class.
    /** The index to be searched by this Searcher. */
    Index index;
    
    /** Constructor */
    public Searcher( Index index ) {
        this.index = index;
    }

    /**
     *  Searches the index for postings matching the query.
     *  @return A postings list representing the result of the query.
     */
    
    
    //Intersect algorithm (find in the book)
    public PostingsList Intersect(PostingsList p1,PostingsList p2){
        PostingsList answer = new PostingsList();
        int idx_p1 = 0;
        int idx_p2 = 0;
        
        while ((idx_p1!=p1.size()) && (idx_p2!=p2.size())){
            //Agafem el post_entry de cada un i anem mirant si els docID son iguals.
            //i hem trobat 2 Doc_id iguals...
            if(p1.get(idx_p1).getDocID() == p2.get(idx_p2).getDocID()){
                answer.insert(p1.get(idx_p1).getDocID(),1);
                idx_p1 = idx_p1+ 1;
                idx_p2 = idx_p2+ 1;
            }else{
                if(p1.get(idx_p1).getDocID()< p2.get(idx_p2).getDocID()){
                    idx_p1 = idx_p1 + 1;
                }else{
                    idx_p2 = idx_p2 + 1;
                }
            }
        }
        return answer;
    }


    //Modify the algorithm of page 42 of the book.
    public PostingsList PhraseIntersect(PostingsList p1, PostingsList p2){
        PostingsList answer = new PostingsList();
        for(int idx_p1 = 0; idx_p1 < p1.size(); idx_p1++){
            int idx_p2 = 0;
            while(idx_p2<p2.size()){
                if(idx_p2 < (p2.size()-10)){
                    if (p2.get(idx_p2+10).getDocID() > p1.get(idx_p1).getDocID()){
                        //Compare docs ID.
                        if (p1.get(idx_p1).getDocID() == p2.get(idx_p2).getDocID()){
                            // Posicions on esta la paraula en cada document.
                            ArrayList<Integer> pp1 = p1.get(idx_p1).getOffset();
                            ArrayList<Integer> pp2 = p2.get(idx_p2).getOffset();
                            //Mirem totes les 2 llistes amb les posicions corresponents.
                            int i = 0;
                            int j = 0;
                            while(i< pp1.size()){
                                while(j< pp2.size()){
                                    //Comprobem si estan contigües.
                                    if (j < (pp2.size()-10)){
                                        if (pp2.get(j+10) < pp1.get(i)){
                                            j = j + 10;
                                        }else{
                                            if ((pp2.get(j)-pp1.get(i)) == 1){
                                                answer.insert(p1.get(idx_p1).getDocID(),pp2.get(j));
                                            }else{
                                                if(pp2.get(j) > pp1.get(i)){
                                                    break;
                                                }
                                            }
                                            j = j + 1;
                                        }
                                    }else{
                                        if ((pp2.get(j)-pp1.get(i)) == 1){
                                            answer.insert(p1.get(idx_p1).getDocID(),pp2.get(j));
                                        }else{
                                            if(pp2.get(j) > pp1.get(i)){
                                                break;
                                            }
                                        }
                                        j = j + 1;
                                    }
                                }
                                i = i + 1;
                            }
                        }else{
                            if (p2.get(idx_p2).getDocID()>p1.get(idx_p1).getDocID()){
                                break;
                            }
                        }
                        idx_p2 = idx_p2 + 1;
                    }else{
                        idx_p2 = idx_p2 + 10;
                    }
   
                //Comprobar. Què passa si estem en els ultims 10 numeros de DocID.
                }else{
                    if (p1.get(idx_p1).getDocID() == p2.get(idx_p2).getDocID()){
                        // Posicions on esta la paraula en cada document.
                        ArrayList<Integer> pp1 = p1.get(idx_p1).getOffset();
                        ArrayList<Integer> pp2 = p2.get(idx_p2).getOffset();
                        //Mirem totes les 2 llistes amb les posicions corresponents.
                        int i = 0;
                        int j = 0;
                        while(i< pp1.size()){
                            while(j< pp2.size()){
                                //Comprobem si estan contigües.
                                if (j < (pp2.size()-10)){
                                    if (pp2.get(j+10) < pp1.get(i)){
                                        j = j + 10;
                                    }else{
                                        if ((pp2.get(j)-pp1.get(i)) == 1){
                                            answer.insert(p1.get(idx_p1).getDocID(),pp2.get(j));
                                        }else{
                                            if(pp2.get(j) > pp1.get(i)){
                                                break;
                                            }
                                        }
                                        j = j + 1;
                                    }
                                }else{
                                    if ((pp2.get(j)-pp1.get(i)) == 1){
                                        answer.insert(p1.get(idx_p1).getDocID(),pp2.get(j));
                                    }else{
                                        if(pp2.get(j) > pp1.get(i)){
                                            break;
                                        }
                                    }
                                    j = j + 1;
                                }
                            }
                            i = i + 1;
                        }
                    }
                    idx_p2 = idx_p2 + 1;
                }
            }
        }
        return answer;
    }
    
    //In this moment we only need to compute 1 word query.
    public PostingsList search( Query query, QueryType queryType, RankingType rankingType ) {
        
        //Task 1.2 and 1.3
        ArrayList<PostingsList> list = new ArrayList<PostingsList>();
        PostingsList answer = new PostingsList();
        
        if (queryType.toString().equals("INTERSECTION_QUERY")){
            
            //First, necessitem ordenar les posting list. Quants ID tenen cada term.
            for(int i = 0; i < query.size(); i++){
            	//Li passem totes les posting list corresponents a les terms i les anem ordenant.
                if (index.getPostings(query.queryterm.get(i).term) != null){
                    list.add(index.getPostings(query.queryterm.get(i).term));
                }else{
                    return null;
                }
            }
            
            //Ara tenim una llista de postings list que ja esta ordenada mitjançant la freq.
            PostingsList old = list.get(0);
            for (int i = 1; i< list.size(); i++){
                //El que retorna es la interseccio per això seria interessant ordenarlos per freq.
                //De menys a més freq. 
                old = Intersect(old,list.get(i));
            }
            
            answer = old;
            return answer;
        }else{
            //Task 1.4
            if (queryType.toString().equals("PHRASE_QUERY")){
                //Afegim totes les posting list corresponents a totes les paraules de la query.
                for(int i =0; i< query.size(); i++){
                    if (index.getPostings(query.queryterm.get(i).term) != null){
                        list.add(index.getPostings(query.queryterm.get(i).term));
                    }else{
                        return null;
                    }
                }
                //Documents on surten totes les paraules de la query.
                PostingsList old = list.get(0);
                for (int i = 1; i< list.size(); i++){
                    old = PhraseIntersect(old,list.get(i));
                }
                answer = old;
                //Documents que hem de mirar la phrase.
                return answer;
            }else{
                if (queryType.toString().equals("RANKED_QUERY")){
                	//Intersection query first
                	for(int i = 0; i < query.size(); i++){
            			//Li passem totes les posting list corresponents a les terms i les anem ordenant.
                		if (index.getPostings(query.queryterm.get(i).term) != null){
                    		list.add(index.getPostings(query.queryterm.get(i).term));
                		}else{
                    		return null;
                		}
            		} 
            		//Ara tenim una llista de postings list que ja esta ordenada mitjançant la freq.
            		PostingsList old = list.get(0);
            		for (int i = 1; i< list.size(); i++){
                		//El que retorna es la interseccio per això seria interessant ordenarlos per freq.
                		//De menys a més freq. 
                		old = Intersect(old,list.get(i));
           		 	}
           		 	answer = old;
                    //Now the answer has the list with all the matching documents.

                    //0. Set the lenght of the document in the matching documents. 
                    for (int i = 0; i < answer.size(); i++){
                        int len_document = 0;
                        len_document = index.docLengths.get(answer.get(i).getDocID());
                        answer.get(i).setLenDocument(len_document);
                    }

                    //1. Set the tf-idf score in each of the matching documents with the
                    // len documents already calculated. 
                    answer.CalculateTfIdfScore();

                    //2. Calculate the Tf_idf_Score of the query term for 1 query term.
                    int tf_qf = 1; //Variable that depends of the query itself.
                    int len_q = 1; //Variable 2 that depends of the query itself.
                    int df_t = answer.size();
                    int N = 17481;
                    double idf_t = Math.log(N/df_t);
                    double tf_idf_query = (tf_qf * idf_t)/len_q;

                    //3. Calculate the similarity and update the score per each document. 
                    //Taking into account the tf_idf score calculated for the query. 
                    answer.CalculateSimilarity(tf_idf_query);

                    System.out.println("Anem a printar:");
                    for (int i = 0; i < answer.size(); i++){
                        System.out.println(answer.get(i).getScore());
                    }

                    System.out.println("Hem acabat de printar la llista");
                    //4. Sort the documents in answer based on the score achieved through the similarity formula.
                    answer.SortList();

                    //5. return the answer.
                    return answer;

                }else{
                	return null;
                }
            }
        }
    }
}
