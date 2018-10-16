/*  
 *   This file is part of the computer assignment for the
 *   Information Retrieval course at KTH.
 * 
 *   Johan Boye, 2017
 */

package ir;

import java.util.ArrayList;
import java.io.File;

/**
 *  This is the main class for the search engine.
 */
public class Engine {
    
    /** The inverted index. */
    Index index = new HashedIndex();
    //Index index = new PersistentHashedIndex();

    /**  The indexer creating the search index. */
    Indexer indexer;

    //KGramIndex
    KGramIndex kgIndex = new KGramIndex(2);

    /** The searcher used to search the index. */
    Searcher searcher;

    /** The engine GUI. */
    SearchGUI gui;

    /**  Directories that should be indexed. */
    ArrayList<String> dirNames = new ArrayList<String>();

    /**  Lock to prevent simultaneous access to the index. */
    Object indexLock = new Object();

    /** The patterns matching non-standard words (e-mail addresses, etc.) */
    String patterns_file = null;
		
    /** The file containing the logo. */
    String pic_file = "";
		
    /** The file containing the pageranks. */
    String rank_file = "";

    /** For persistent indexes, we might not need to do any indexing. */
    boolean is_indexing = true;

    SpellChecker speller;

    /* ----------------------------------------------- */


    /**  
     *   Constructor. 
     *   Indexes all chosen directories and files
     */
    public Engine( String[] args ) {
	decodeArgs( args );
    //Create also the k-index.
	
    indexer = new Indexer( index, patterns_file, kgIndex);
	searcher = new Searcher( index );
	gui = new SearchGUI( this );
	gui.init();
    indexer.PageRankFile();
	/* 
	 *   Calls the indexer to index the chosen directory structure.
	 *   Access to the index is synchronized since we don't want to 
	 *   search at the same time we're indexing new files (this might 
	 *   corrupt the index).
	 */
	   if (is_indexing) {
            synchronized ( indexLock ) {
                gui.displayInfoText( "Indexing, please wait..." );
                long startTime = System.currentTimeMillis();
                for ( int i=0; i<dirNames.size(); i++ ) {
                    File dokDir = new File( dirNames.get( i ));
                    indexer.processFiles( dokDir );
                }
                long elapsedTime = System.currentTimeMillis() - startTime;
                gui.displayInfoText( String.format( "Indexing done in %.1f seconds.", elapsedTime/1000.0 ));
                index.cleanup();
            //Now we can try to print the results of the queries "ve" and "th he"
            System.out.println("Tokens for ve k-gram");
            String[] kgrams = {"ve"};
            int k = 2;
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
                    System.err.println("The first 30 of them are:");
                    resNum = 30;
                }
                for (int i = 0; i < resNum; i++) {
                    System.err.println(kgIndex.getTermByID(postings.get(i).tokenID));
                }
            }
            }
        } else {
            gui.displayInfoText( "Index is loaded from disk" );
        }
    }

   



    /* ----------------------------------------------- */

    /**
     *   Decodes the command line arguments.
     */
    private void decodeArgs( String[] args ) {
	int i=0, j=0;
	while ( i < args.length ) {
	    if ( "-d".equals( args[i] )) {
		i++;
		if ( i < args.length ) {
		    dirNames.add( args[i++] );
		}
	    }
	    else if ( "-p".equals( args[i] )) {
		i++;
		if ( i < args.length ) {
		    patterns_file = args[i++];
		}
	    }
	    else if ( "-l".equals( args[i] )) {
		i++;
		if ( i < args.length ) {
		    pic_file = args[i++];
		}
	    }
	    else if ( "-r".equals( args[i] )) {
		i++;
		if ( i < args.length ) {
		    rank_file = args[i++];
		}
	    }	
	    else if ( "-ni".equals( args[i] )) {
                i++;
                is_indexing = false;
            }
	    else {
		System.err.println( "Unknown option: " + args[i] );
		break;
	    }
	}				    
    }


    /* ----------------------------------------------- */


    public static void main( String[] args ) {
	Engine e = new Engine( args );
    }

}

