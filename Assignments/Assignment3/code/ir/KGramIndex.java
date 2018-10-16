/*
 *   This file is part of the computer assignment for the
 *   Information Retrieval course at KTH.
 *
 *   Dmytro Kalpakchi, 2018
 */

package ir;

import java.io.*;
import java.util.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;


public class KGramIndex {

    /** Mapping from term ids to actual term strings */
    HashMap<Integer,String> id2term = new HashMap<Integer,String>();

    /** Mapping from term strings to term ids */
    HashMap<String,Integer> term2id = new HashMap<String,Integer>();

    /** Index from k-grams to list of term ids that contain the k-gram */
    HashMap<String,ArrayList<KGramPostingsEntry>> index = new HashMap<String,ArrayList<KGramPostingsEntry>>();

    /** The ID of the last processed term */
    int lastTermID = -1;

    /** Number of symbols to form a K-gram */
    int K = 3;

    public KGramIndex(int k) {
        K = k;
        if (k <= 0) {
            System.err.println("The K-gram index can't be constructed for a negative K value");
            System.exit(1);
        }
    }

    /** Generate the ID for an unknown term */
    private int generateTermID() {
        return ++lastTermID;
    }

    public int getK() {
        return K;
    }

    /**
     *  Get intersection of two postings lists
     */

    //p1 : ArrayList with all the id of the tokens that contain the k-gram term....
    public ArrayList<KGramPostingsEntry> intersect(ArrayList<KGramPostingsEntry> p1, ArrayList<KGramPostingsEntry> p2) {          
        
        ArrayList<KGramPostingsEntry> answer = new ArrayList<KGramPostingsEntry>();
        
        //Intersection function.
        //Going to the two ArrayList functions and just compare if the token id it's the same.
        for (int i = 0; i < p1.size(); i++){
            int token = p1.get(i).getTokenID();
            for (int j = 0; j < p2.size(); j ++){
                //If the token appears in the two list (we found a token that contains the two k-grams!)
                if (token == p2.get(j).getTokenID()){

                    KGramPostingsEntry token_k = new KGramPostingsEntry(token);
                    answer.add(token_k);
                }   
            }
        }
        //Return an ArrayList with all the token id that contains the intersection of the two different k-grams.
        return answer;
    }


    /** Inserts all k-grams from a token into the index. */
    public void insert( String token ) {
        
        //Create the id_term regarding the token.
        //String->Integer.(term2id), Integer->String.(id2term)

        //If the token is a new one.., if not, there is already an id generated.
        if (!term2id.containsKey(token)){
            
            //New token -> Create an ID.
            int id_term = generateTermID();
            term2id.put(token,id_term);
            id2term.put(id_term,token);
        
            //System.out.println("The token was: " + token);
            //To identify the beginning and the end of a token. 
            String extended_token = "^" + token + "$";
            //System.out.println("Now the extended token is: " + extended_token);

            //How many kgrams should I generate per each token? (Slides formula)
            int n = token.length();
            int num_kgrams = n + 3 - K; 

            //ArrayList to save temporarily the kgrams for a token.
            ArrayList<String> kgrams_temp = new ArrayList<String>();
            for (int i  = 0 ; i < num_kgrams; i++){
                String gram = "";
                char aux = extended_token.charAt(i);
                gram = gram + aux;
                int j = 1;
                //Which chars should I take.
                while (j < K){
                    gram = gram + extended_token.charAt(i+j);
                    j = j + 1;
                }
                kgrams_temp.add(gram);
            }
            //Print the kgrams, to be sure that it is ok:
            //All the k-grams for a specific token.
            /*System.out.println("The kgrams for specific token:" + token);
            for (int i = 0; i < kgrams_temp.size(); i++){
                System.out.println(kgrams_temp.get(i));
            }*/

            //Now we have the k-grams for the token and we need to put all the information into the HashTable.
            //HashMap<String,List<KGramPostingsEntry>> index = new HashMap<String,List<KGramPostingsEntry>>();
            for (int i = 0; i < kgrams_temp.size();i++){
                String k_gram = kgrams_temp.get(i);

                //If it is a new k-gram.
                if (!index.containsKey(k_gram)){
                    //New k_gram. Just create a new List and add it.
                    ArrayList<KGramPostingsEntry> list = new ArrayList<KGramPostingsEntry>();
                    int id = term2id.get(token);
                    KGramPostingsEntry entry = new KGramPostingsEntry(id);
                    list.add(entry);
                    index.put(k_gram,list);
                }else{
                    //This k_gram is already there.
                    ArrayList<KGramPostingsEntry> list_kgram = index.get(k_gram);
                    int id = term2id.get(token);
                    KGramPostingsEntry entry = new KGramPostingsEntry(id);
                    list_kgram.add(entry);
                    //remove the mapping
                    index.remove(k_gram);
                    //adding the mapping again
                    index.put(k_gram,list_kgram);
                }
            }
        }
    }

    /** Get postings for the given k-gram */
    public ArrayList<KGramPostingsEntry> getPostings(String kgram) {
        //This kgram is not existing in the HashMap.
        if (!index.containsKey(kgram)){
            //No existing k-gram
            return null;
        }else{
            //We have something to return. (All the tokens id that contain the specific k-gram.)
            return index.get(kgram);
        }
    }

    /** Get id of a term */
    public Integer getIDByTerm(String term) {
        return term2id.get(term);
    }

    /** Get a term by the given id */
    public String getTermByID(Integer id) {
        return id2term.get(id);
    }

    private static HashMap<String,String> decodeArgs( String[] args ) {
        HashMap<String,String> decodedArgs = new HashMap<String,String>();
        int i=0, j=0;
        while ( i < args.length ) {
            if ( "-p".equals( args[i] )) {
                i++;
                if ( i < args.length ) {
                    decodedArgs.put("patterns_file", args[i++]);
                }
            }
            else if ( "-f".equals( args[i] )) {
                i++;
                if ( i < args.length ) {
                    decodedArgs.put("file", args[i++]);
                }
            }
            else if ( "-k".equals( args[i] )) {
                i++;
                if ( i < args.length ) {
                    decodedArgs.put("k", args[i++]);
                }
            }
            else if ( "-kg".equals( args[i] )) {
                i++;
                if ( i < args.length ) {
                    decodedArgs.put("kgram", args[i++]);
                }
            }
            else {
                System.err.println( "Unknown option: " + args[i] );
                break;
            }
        }
        return decodedArgs;
    }

    public static void main(String[] arguments) throws FileNotFoundException, IOException {
        HashMap<String,String> args = decodeArgs(arguments);

        int k = Integer.parseInt(args.getOrDefault("k", "3"));
        KGramIndex kgIndex = new KGramIndex(k);

        File f = new File(args.get("file"));
        Reader reader = new InputStreamReader( new FileInputStream(f), StandardCharsets.UTF_8 );
        Tokenizer tok = new Tokenizer( reader, true, false, true, args.get("patterns_file") );
        while ( tok.hasMoreTokens() ) {
            String token = tok.nextToken();
            kgIndex.insert(token);
        }

        String[] kgrams = args.get("kgram").split(" ");
        ArrayList<KGramPostingsEntry> postings = null;
        for (String kgram : kgrams) {
            if (kgram.length() != k) {
                System.err.println("Cannot search k-gram index: " + kgram.length() + "-gram provided instead of " + k + "-gram");
                System.exit(1);
            }

            if (postings == null) {
                postings = kgIndex.getPostings(kgram);
            } else {
                postings = kgIndex.intersect(postings, kgIndex.getPostings(kgram));
            }
        }
        if (postings == null) {
            System.err.println("Found 0 posting(s)");
        } else {
            int resNum = postings.size();
            System.err.println("Found " + resNum + " posting(s)");
            if (resNum > 30) {
                System.err.println("The first 10 of them are:");
                resNum = 30;
            }
            for (int i = 0; i < resNum; i++) {
                System.err.println(kgIndex.getTermByID(postings.get(i).tokenID));
            }
        }
    }
}
