/*  
 *   This file is part of the computer assignment for the
 *   Information Retrieval course at KTH.
 * 
 *   Johan Boye, 2017
 */  

package ir;

import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Iterator;
import java.nio.charset.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.pdfparser.*;
import org.apache.pdfbox.util.PDFTextStripper;
import org.apache.pdfbox.pdmodel.PDDocument;

/**
 *  A class for representing a query as a list of words, each of which has
 *  an associated weight.
 */
public class Query {

    /**
     *  Help class to represent one query term, with its associated weight. 
     */
    class QueryTerm {
        String term;
        double weight;
        QueryTerm( String t, double w ) {
            term = t;
            weight = w;
        }
    }


    /** 
     *  Representation of the query as a list of terms with associated weights.
     *  In assignments 1 and 2, the weight of each term will always be 1.
     */
    public ArrayList<QueryTerm> queryterm = new ArrayList<QueryTerm>();

    /**  
     *  Relevance feedback constant alpha (= weight of original query terms). 
     *  Should be between 0 and 1.
     *  (only used in assignment 3).
     */
    double alpha = 0.2;

    /**  
     *  Relevance feedback constant beta (= weight of query terms obtained by
     *  feedback from the user). 
     *  (only used in assignment 3).
     */
    double beta = 1 - alpha;
    
    
    /**
     *  Creates a new empty Query 
     */
    public Query() {
    }
    
    
    /**
     *  Creates a new Query from a string of words
     */
    public Query( String queryString  ) {
	StringTokenizer tok = new StringTokenizer( queryString );
	while ( tok.hasMoreTokens() ) {
	    queryterm.add( new QueryTerm(tok.nextToken(), 1.0) );
	}    
    }
    
    
    /**
     *  Returns the number of terms
     */
    public int size() {
	return queryterm.size();
    }
    
    
    /**
     *  Returns the Manhattan query length
     */
    public double length() {
	double len = 0;
	for ( QueryTerm t : queryterm ) {
	    len += t.weight; 
	}
	return len;
    }
    
    
    /**
     *  Returns a copy of the Query
     */
    public Query copy() {
	Query queryCopy = new Query();
	for ( QueryTerm t : queryterm ) {
	    queryCopy.queryterm.add( new QueryTerm(t.term, t.weight) );
	}
	return queryCopy;
    }
   
    
    public String extractPDFContents( File f ) throws IOException {
        FileInputStream fi = new FileInputStream( f );
        PDFParser parser = new PDFParser( fi );
        parser.parse();
        fi.close();
        COSDocument cd = parser.getDocument();
        PDFTextStripper stripper = new PDFTextStripper();
        String result = stripper.getText( new PDDocument( cd ));
        cd.close();
        return result;
    }

    /**
     *  Expands the Query using Relevance Feedback
     *
     *  @param results The results of the previous query.
     *  @param docIsRelevant A boolean array representing which query results the user deemed relevant.
     *  @param engine The search engine object
     */

    public void relevanceFeedback( PostingsList results, boolean[] docIsRelevant, Engine engine ) {
    
        // Euclidean lenght query.
       double queryLen = 0; 
       for(int k=0; k<queryterm.size(); k++){
         queryLen += queryterm.get(k).weight*queryterm.get(k).weight;
       }
       queryLen = Math.sqrt(queryLen);
     
       //Hashmap between the document name and the associated score/document.
       //Unique terms (dj)
       HashMap<String, Double> docScores = new HashMap<String, Double>();

       PostingsEntry aux = null;
       int numDocRelevant = 0;
       int i=0;
       if (docIsRelevant != null) {
         while(i<docIsRelevant.length){
            //If it's a relevant document... (marked by the user...)
           if(docIsRelevant[i]){
             numDocRelevant++;
             aux = results.get(i);
             //Just take the document name and the document length. (Manhattan lenght) -> Euclidean lenght.
             int doclen = engine.index.docLengths.get(aux.getDocID());
             String docname = engine.index.docNames.get(aux.getDocID());
             File f = new File(docname);
            try {
                // Read the document like in the indexer
                Reader reader = new InputStreamReader( new FileInputStream(f), StandardCharsets.UTF_8 );
                char[] buf = new char[4];
                reader.read( buf, 0, 4 );
                reader.close();
                if ( buf[0] == '%' && buf[1]=='P' && buf[2]=='D' && buf[3]=='F' ) {
                    try {
                        String contents = extractPDFContents(f);
                        reader = new StringReader( contents );
                    }catch ( IOException e ) {
                        reader = new InputStreamReader( new FileInputStream(f), StandardCharsets.UTF_8 );
                    }
                } else {
                    reader = new InputStreamReader( new FileInputStream(f), StandardCharsets.UTF_8 );
                }
                
                //All the tokens of the relevant document.
                Tokenizer tok = new Tokenizer(reader, true, true, true, engine.patterns_file);
                  
                //Take all terms in the document into account (if they are repeated)
                HashMap<String, QueryTerm> doc_terms = new HashMap<String, QueryTerm>();
                //Update the doc_terms HashTable.
                //For each token, we have associated a queryTerm (with the weight and the term.)
                while (tok.hasMoreTokens()) {
                    String token = tok.nextToken();
                    QueryTerm val = doc_terms.get(token);
                    if (val == null) {
                        val = new QueryTerm(token, 0.0);
                        doc_terms.put(token, val);
                    }
                    //Count the frequency.
                    val.weight += 1;
                }

                //In the doc_terms HashMap we will have the tf score per term/document.
                
                //We need to compute the length of the document.
                double docLen = 0.0;                     
                //For all the tokens in the doc_terms HashTable...
                for (Map.Entry<String, QueryTerm> termEntry : doc_terms.entrySet()){
                    //To calculate df_t (How many documents in the corpus have the specific token.)
                    PostingsList pList = engine.index.getPostings(termEntry.getValue().term);
                    
                    //idf = log10 (N/df_t)
                    double term_idf = Math.log((engine.index.docNames.size())/(pList.size()));
                    QueryTerm val = termEntry.getValue();
                    //Update the weight of the token/document.
                    val.weight = (1 +   (Math.log(val.weight)));
                    //Multiply x the idf term.
                    val.weight = val.weight * term_idf;
                    docLen += (val.weight*val.weight);
                }
                //Euclidean document length.
                docLen = Math.sqrt(docLen);
                
                //Update the information into the final HashMap (document name, total score.)
                for (Map.Entry<String, QueryTerm> termEntry : doc_terms.entrySet()){
                    Double val = docScores.get(termEntry.getKey());
                    if (val == null) {
                        val = 0.0;
                    }
                    //We need to normalize them.
                    docScores.put(termEntry.getKey(), val+(termEntry.getValue().weight/docLen));
                }
            
            reader.close();
            }catch ( IOException e ) {
                System.err.println( "Warning: IOException during relevance feedback." );
            }   
           }
           i++;
          }
        }  

        //Now we have computed the score (sum dj , all dj of Dr)
        //Now we need to update the query weights.

        //Terms included in the previous query. (original one)
        for (int k = 0; k<queryterm.size(); k++) {
            //Normalize them.
            queryterm.get(k).weight *= (alpha/queryLen);
            Double docScore = docScores.remove(queryterm.get(k).term);
            if (docScore != null) {
                queryterm.get(k).weight += (beta*docScore/numDocRelevant);
            }
            
        }

        //Query expansion
        //All the query terms not included into the previous query. 
        for (Map.Entry<String, Double> termEntry : docScores.entrySet()) {
            queryterm.add(new QueryTerm(termEntry.getKey(), (beta*termEntry.getValue()/numDocRelevant)));
        }

    }
}


