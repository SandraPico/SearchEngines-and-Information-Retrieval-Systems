/*  
 *   This file is part of the computer assignment for the
 *   Information Retrieval course at KTH.
 * 
 *   Johan Boye, 2017
 */
package ir;
import java.util.ArrayList;
import java.lang.Math;
import java.util.HashMap;
import java.util.Map;

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
    
    public PostingsList ComputePageRank(Query query, QueryType queryType, RankingType rankingType,ArrayList<PostingsList> list,PostingsList answer){
        //Extract the posting list for a given query.
        for(int i = 0; i < query.size(); i++){
            if (index.getPostings(query.queryterm.get(i).term) != null){
                list.add(index.getPostings(query.queryterm.get(i).term));
            }else{
                return null;
            }
        }

        //Generate the answer PostingsList that we would like to generate ( considering the proper ID docs.)
        ArrayList<Integer> doc_repetits = new ArrayList<Integer>();
        for (int i = 0; i < list.size(); i++){
            for (int j = 0; j < list.get(i).size(); j++){
                int doc_aux = list.get(i).get(j).getDocID();
                int trobat = 0;
                for  (int k = 0; k < doc_repetits.size();k++){
                    if (doc_repetits.get(k) == doc_aux){
                        trobat = 1;
                    }
                }
                if (trobat == 0){
                    doc_repetits.add(doc_aux);
                    answer.InsertAnswer(doc_aux);
                }
            }
        }

        //Fill the score:
        for ( int i = 0; i < answer.size(); i++){
            int doc_id = answer.get(i).getDocID();
            for (int j = 0; j < index.pageRank_docid.size(); j++){
                if (index.pageRank_docid.get(j) == doc_id){
                    answer.get(i).setScore(index.pageRank_values.get(j));
                }
            }
        }

        answer.SortList();
        return answer;
    }
   
   public PostingsList NewTFIDF(Query query){
        
        //Compute the tf-idf score and make the ranking.

        //Number of total documents in the corpus.
        double N = index.docNames.size();         

        //Document ID - Score associated.
        HashMap<Integer, PostingsEntry> scores = new HashMap<Integer, PostingsEntry>();
        
        //All the terms in the query...
        for (int i=0; i<query.queryterm.size(); i++) {
            
            //Extract the postingList of this particular term.
            Query.QueryTerm q_term = query.queryterm.get(i);
            PostingsList pList = index.getPostings(q_term.term);
              
            //Compute the idf term of the particular token. 
            double idf = Math.log(N/(pList.size()));
            
            //For each element of the list (document-token)
            for (int j = 0; j < pList.size(); j++) {

                PostingsEntry entry = pList.get(j);
                
                //Extract the PostingEntry of the postingList.

                //Check if the document is the first time that appears...
                PostingsEntry score = scores.get(entry.getDocID());
                if (score == null) {
                    score = new PostingsEntry(entry.getDocID(),0);
                    scores.put(entry.getDocID(), score);
                }
                
                //Compute tf.
                double tf = entry.getOffset().size();
                //Compute score
                double log_tf = 1 + (Math.log(tf));
                //Multiply it for the query terms (already normalized)
                double tf_idf = (log_tf*idf) * q_term.weight;
                
                //Just update the particular score.
                score.score += tf_idf;  
            }
        }

        //Then, in that case, we have a HashMap with:
            //Each document ID -> Score.
            //We already checked the reapeated cases..
        
        //Final PostingList to return.
        PostingsList result = new PostingsList();
        for (Map.Entry<Integer, PostingsEntry> entry : scores.entrySet()) {
            PostingsEntry pEntry = entry.getValue();
            pEntry.score /= (index.docLengths.get(pEntry.getDocID()));
            result.add_PostingList(pEntry);
        }
        
        //Sort the list at the end.
        result.SortList();
        return result;    
   }
    
    public PostingsList ComputeTFIDF(Query query, QueryType queryType, RankingType rankingType,ArrayList<PostingsList> list,PostingsList answer){
        //For assignment 3 do we need to store also the tf-idf scores.
        //We are going to use the hash table.

        //Only one term in the query.
        if (query.size() == 1) {
            for(int i = 0; i < query.size(); i++){
                if (index.getPostings(query.queryterm.get(i).term) != null){
                    list.add(index.getPostings(query.queryterm.get(i).term));
                }else{
                    return null;
                }
            }
            System.out.println("Size: ");
            System.out.println(list.get(0).size());
            
            PostingsList old = list.get(0);
            for (int i = 1; i< list.size(); i++){
                old = Intersect(old,list.get(i));
            }
            answer = old;
            for (int i = 0; i < answer.size(); i++){
                int len_document = 0;
                len_document = index.docLengths.get(answer.get(i).getDocID());
                answer.get(i).setLenDocument(len_document);
            }
            answer.CalculateTfIdfScore();
            
            HashMap<Integer,Double> doc_score = new HashMap<Integer,Double>();
            
            //Update the information regarding the tf-idf scores in the hash table (assignment 3- relevance feedback)
            for (int i = 0; i < answer.size(); i++ ){
                int doc = answer.get(i).getDocID();
                double tfscore = answer.get(i).getTf_idf_Score();
                doc_score.put(doc,tfscore);
            }

            //Token -> In this case we will only have 1 token.
            String token = query.queryterm.get(0).term;
            //Update all the scores in the HashTable. 
            
            //query.PutHash(token,doc_score);

            /*if (query.tf_idf_scores.get(token) == null) {
                query.tf_idf_scores.put(token,doc_score);    
            }*/
            answer.SortList();
            return answer;
        }else{
            //More than one term in the query.
            if (query.size()>1){ 
                //Try if cosinus similarity formula works:
                for(int i = 0; i < query.size(); i++){
                    if (index.getPostings(query.queryterm.get(i).term) != null){
                        list.add(index.getPostings(query.queryterm.get(i).term));
                    }else{
                        return null;
                    }
                }
                //Set document lenght.
                for (int i = 0; i < list.size(); i++){
                    for (int j = 0; j < list.get(i).size(); j++){
                        int len_document = 0;
                        len_document = index.docLengths.get(list.get(i).get(j).getDocID());
                        list.get(i).get(j).setLenDocument(len_document);
                    }
                    //Calculate the tf-idf score.
                    list.get(i).CalculateScore();
                }

                //Here we should update the HashTable with the tf-idf score information per each term-document.
                //Update the information regarding the tf-idf scores in the hash table (assignment 3- relevance feedback)
                //list -> ArrayList<PostingList>
                //Per each term of the query.
                for (int i = 0; i < list.size(); i++){
                    String token = query.queryterm.get(i).term;
                    //Per each token we are going to create our Doc-score hashmap.
                    HashMap<Integer,Double> doc_score = new HashMap<Integer,Double>();
                    for (int j = 0; j < list.get(i).size(); j++){
                        int doc = list.get(i).get(j).getDocID();
                        double tfscore = list.get(i).get(j).getTf_idf_Score();
                        doc_score.put(doc,tfscore);
                    }
                    //It can be that the query is : zombie attack zombie
                    if (index.tf_idf_scores.get(token) == null){
                        index.tf_idf_scores.put(token,doc_score);
                    }
                }
            
                //Create the answer array.
                //Proper size + to store the total score (query-document)
                ArrayList<Integer> doc_repetits = new ArrayList<Integer>();
                for (int i = 0; i < list.size(); i++){
                    for (int j = 0; j < list.get(i).size(); j++){
                        int doc_aux = list.get(i).get(j).getDocID();
                        int trobat = 0;
                        for  (int k = 0; k < doc_repetits.size();k++){
                            if (doc_repetits.get(k) == doc_aux){
                                trobat = 1;
                            }
                        }
                        if (trobat == 0){
                            doc_repetits.add(doc_aux);
                            answer.InsertAnswer(doc_aux);
                        }
                    }
                }
                            
            //Must be an array list.
            ArrayList<Double> query_score = new ArrayList<Double>();
            int len_q = query.size();
            for (int i = 0; i < query.size(); i++){
                String term =  query.queryterm.get(i).term;
                int tf_qt = 0;
                for (int j = 0; j < query.size(); j++){
                    if ((query.queryterm.get(j).term).equals(term)){
                        tf_qt = tf_qt + 1;
                    }
                }
                //tf_qt = query.queryterm.get(i).weight;
                int N = 17483;
                double idf_t = Math.log10(N/(list.get(i).size()));
                double score_query = (tf_qt*idf_t)/(double)len_q;
                query_score.add(score_query);
                //Update the information into the Query class.
                index.queryWeights.add(score_query);
                //query.SetWeight(i,score_query);
                System.out.println("Estem a PostingListm, weight term: " +i+ " is: " + score_query);
            }

            answer.ComputeSimilarityScore(list,query_score);
            //Normalize the scores:
            for(int i = 0; i < answer.size(); i++){
                int len_document = index.docLengths.get(answer.get(i).getDocID());
                double aux_score = (double)answer.get(i).getScore()/((double)len_document*(double)len_q);
                answer.get(i).setScore(aux_score);
            }
            answer.SortList();
            return answer;
        }else{
            return null;
        }
    }
}
																													
public PostingsList ComputeCombination(Query query, QueryType queryType, RankingType rankingType,ArrayList<PostingsList> list,PostingsList answer, double pageRankImportance, double tf_idf_importance,PostingsList answer_tf, PostingsList answer_pr){
	//Combination:
    //Compute TFD
	for(int i = 0; i < query.size(); i++){
        if (index.getPostings(query.queryterm.get(i).term) != null){
            list.add(index.getPostings(query.queryterm.get(i).term));
        }else{
            return null;
        }
    }

    //Create the answer array.
    //Proper size + to store the total score (query-document)
    ArrayList<Integer> doc_repetits = new ArrayList<Integer>();
    for (int i = 0; i < list.size(); i++){
        for (int j = 0; j < list.get(i).size(); j++){
            int doc_aux = list.get(i).get(j).getDocID();
            int trobat = 0;
            for  (int k = 0; k < doc_repetits.size();k++){
                if (doc_repetits.get(k) == doc_aux){
                    trobat = 1;
                }
            }
            if (trobat == 0){
                doc_repetits.add(doc_aux);
                answer_tf.InsertAnswer(doc_aux);
                answer_pr.InsertAnswer(doc_aux);
                answer.InsertAnswer(doc_aux);
            }
        }
    }

    //TF-IDF Score
    if (query.size() == 1){
    	for (int i = 0; i < answer_tf.size(); i++){
        	int len_document = 0;
        	len_document = index.docLengths.get(answer_tf.get(i).getDocID());
            answer_tf.get(i).setLenDocument(len_document);
        }
        answer_tf.CalculateTfIdfScore();
    }else{
    	if (query.size()>1){ 
        	//Set document lenght.
            for (int i = 0; i < list.size(); i++){
                for (int j = 0; j < list.get(i).size(); j++){
                    int len_document = 0;
                    len_document = index.docLengths.get(list.get(i).get(j).getDocID());
                    list.get(i).get(j).setLenDocument(len_document);
                }
                //Calculate the tf-idf score.
                list.get(i).CalculateScore();
            }
            //Compute the query score per each term.
            ArrayList<Double> query_score = new ArrayList<Double>();
            for (int i = 0; i < query.size(); i++){
                String term =  query.queryterm.get(i).term;
                int tf_qt = 0;
                for (int j = 0; j < query.size(); j++){
                    if ((query.queryterm.get(j).term).equals(term)){
                        tf_qt = tf_qt + 1;
                    }
                }
                int len_q = query.size();
                int N = 17483;
                double idf_t = Math.log10(N/(list.get(i).size()));
                double score_query = (tf_qt*idf_t)/len_q;
                query_score.add(score_query);
            }
            answer_tf.ComputeSimilarityScore(list,query_score);
            //Normalize the scores:
           	for(int i = 0; i < answer_tf.size(); i++){
                int len_document = index.docLengths.get(answer_tf.get(i).getDocID());
                double aux_score = (double)answer_tf.get(i).getScore()/(double)len_document;
                answer_tf.get(i).setScore(aux_score);
            }
        }else{
        	return null;
        }
    }
	//Compute the Page Ranking
	for ( int i = 0; i < answer_pr.size(); i++){
        int doc_id = answer_pr.get(i).getDocID();
        for (int j = 0; j < index.pageRank_docid.size(); j++){
        	if (index.pageRank_docid.get(j) == doc_id){
                answer_pr.get(i).setScore(index.pageRank_values.get(j));
            }	
        }
    }

    //Compute the final answer.
    for (int i = 0; i < answer.size(); i++){
        int doc_id = answer.get(i).getDocID();
        double score_pr = answer_pr.get(i).getScore();
        double score_tf = answer_tf.get(i).getScore();
        double final_score = score_pr*pageRankImportance + score_tf*tf_idf_importance;
        answer.get(i).setScore(final_score);
    }
    answer.SortList();
    return answer;
}        


    //In this moment we only need to compute 1 word query.
    public PostingsList search( Query query, QueryType queryType, RankingType rankingType ) {
        
        //Task 1.2 and 1.3
        ArrayList<PostingsList> list = new ArrayList<PostingsList>();
        PostingsList answer = new PostingsList();
        PostingsList answer_tf = new PostingsList();
        PostingsList answer_pr = new PostingsList();
        
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
                	if(rankingType.toString().equals("TF_IDF")){
                        System.out.println("We are in tf-idf type");
                        return NewTFIDF(query);
                        //return ComputeTFIDF(query,queryType,rankingType,list,answer);
                    }else{
                        if (rankingType.toString().equals("PAGERANK")){
                            System.out.println("We are in page rank type");
                            //We just need to convert the file into the answer array.
                            return ComputePageRank(query,queryType,rankingType,list,answer);
                        }else{
                        	double tf_idf_importance = 70;
                        	double pageRankImportance = 30;
                        	return ComputeCombination(query,queryType,rankingType,list,answer,pageRankImportance,tf_idf_importance,answer_tf,answer_pr);
                    	}
                    }   
                }else{
                	return null;
                }
            }
        }
    }
}
