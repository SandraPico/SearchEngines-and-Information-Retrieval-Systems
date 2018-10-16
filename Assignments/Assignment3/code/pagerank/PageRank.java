import java.util.*;
import java.io.*;
import java.util.Arrays;

public class PageRank {

    /**  
     *   Maximal number of documents. We're assuming here that we
     *   don't have more docs than we can keep in main memory;
     */
    final static int MAX_NUMBER_OF_DOCS = 1000;

    /**
     *   Mapping from document names to document numbers.
     */
    Hashtable<String,Integer> docNumber = new Hashtable<String,Integer>();

    /**
     *   Mapping from document numbers to document names
     */
    String[] docName = new String[MAX_NUMBER_OF_DOCS];

    /**  
     *   The transition matrix. p[i][j] = the probability that the
     *   random surfer clicks from page i to page j.
     */
    double[][] p = new double[MAX_NUMBER_OF_DOCS][MAX_NUMBER_OF_DOCS];

    /**
     *   The number of outlinks from each node.
     */
    int[] out = new int[MAX_NUMBER_OF_DOCS];

    /**
     *   The probability that the surfer will be bored, stop
     *   following links, and take a random jump somewhere.
     */
    final static double BORED = 0.15;

    /**
     *   In the initializaton phase, we use a negative number to represent 
     *   that there is a direct link from a document to another.
     */
    final static double LINK = -1.0;
    
    /**
     *   Convergence criterion: Transition probabilities do not 
     *   change more that EPSILON from one iteration to another.
     */
    final static double EPSILON = 0.0001;

    
    /* --------------------------------------------- */

    public PageRank( String filename ) {
		int noOfDocs = readDocs( filename );
		initiateProbabilityMatrix( noOfDocs );
		iterate( noOfDocs, 100 );
    }

    /* --------------------------------------------- */


    /**
     *   Reads the documents and fills the data structures. When this method 
     *   finishes executing, <code>p[i][j] = LINK</code> if there is a direct
     *   link from i to j, and <code>p[i][j] = 0</code> otherwise.
     *   <p>
     *
     *   @return the number of documents read.
     */
    int readDocs( String filename ) {
	int fileIndex = 0;
	try {
	    System.err.print( "Reading file... " );
	    BufferedReader in = new BufferedReader( new FileReader( filename ));
	    String line;
	    while ((line = in.readLine()) != null && fileIndex<MAX_NUMBER_OF_DOCS ) {
		int index = line.indexOf( ";" );
		String title = line.substring( 0, index );
		Integer fromdoc = docNumber.get( title );
		//  Have we seen this document before?
		if ( fromdoc == null ) {	
		    // This is a previously unseen doc, so add it to the table.
		    fromdoc = fileIndex++;
		    docNumber.put( title, fromdoc );
		    docName[fromdoc] = title;
		}
		// Check all outlinks.
		StringTokenizer tok = new StringTokenizer( line.substring(index+1), "," );
		while ( tok.hasMoreTokens() && fileIndex<MAX_NUMBER_OF_DOCS ) {
		    String otherTitle = tok.nextToken();
		    Integer otherDoc = docNumber.get( otherTitle );
		    if ( otherDoc == null ) {
			// This is a previousy unseen doc, so add it to the table.
			otherDoc = fileIndex++;
			docNumber.put( otherTitle, otherDoc );
			docName[otherDoc] = otherTitle;
		    }
		    // Set the probability to LINK for now, to indicate that there is
		    // a link from d to otherDoc.
		    if ( p[fromdoc][otherDoc] >= 0 ) {
			p[fromdoc][otherDoc] = LINK;
			out[fromdoc]++;
		    }
		}
	    }
	    if ( fileIndex >= MAX_NUMBER_OF_DOCS ) {
		System.err.print( "stopped reading since documents table is full. " );
	    }
	    else {
		System.err.print( "done. " );
	    }
	}
	catch ( FileNotFoundException e ) {
	    System.err.println( "File " + filename + " not found!" );
	}
	catch ( IOException e ) {
	    System.err.println( "Error reading file " + filename );
	}
	System.err.println( "Read " + fileIndex + " number of documents" );
	return fileIndex;
    }




    /* --------------------------------------------- */


    /*
     *   Initiates the probability matrix. 
     */
    void initiateProbabilityMatrix( int numberOfDocs ) {
    	//Now we are going to initialize the P matrix. 
    	//What is provided to us is the adjency matrix "A".
		//Now we are going to derive P from the adjency matrix.
		double num_of_ones = 0;

        double[][] J_1 = new double[MAX_NUMBER_OF_DOCS][MAX_NUMBER_OF_DOCS];
        double[][] J_2 = new double[MAX_NUMBER_OF_DOCS][MAX_NUMBER_OF_DOCS];

        //First we are going to compute J_1
        for (int i = 0; i < numberOfDocs; i++){
            //Check how many ones we have in a row.
            num_of_ones = 0.0;
            for (int j = 0; j < numberOfDocs; j++){
                if(p[i][j] == LINK){
                    num_of_ones = num_of_ones + 1.0;
                }
            }
            //Now we need to update the matrix J_1
            if (num_of_ones!= 0){
                for (int j = 0; j < numberOfDocs; j++){
                    if (p[i][j] == LINK){
                        double value = 1.0/(double)num_of_ones;
                        J_1[i][j] = value;
                    }else{
                        J_1[i][j] = 0.0;
                    }
                }
            }else{
                //Si es el 0, hem de posar 1/numOfDocs
                for (int j = 0; j < numberOfDocs; j++){
                    J_1[i][j] = 1.0/(double)numberOfDocs;
                }
            }
        }

        //Now we are going to compute J_2
        for (int i = 0; i < numberOfDocs; i++){
            for (int j = 0; j < numberOfDocs; j++){
                J_2[i][j] = 1.0/(double)numberOfDocs;
            }
        }

        //Now compute the p matrix initialization:
        for (int i = 0; i < numberOfDocs; i++){
            for (int j = 0; j < numberOfDocs; j++){
                p[i][j] = ((1-BORED)*J_1[i][j]) + (BORED)*J_2[i][j];
            }
        }
    }


    /* --------------------------------------------- */

    /* *********************************************
                QUICK SORT FUNCTIONS
    ************************************************/
    int partition(double arr[], int index_array[] ,int left, int right){
        int i = left, j = right;
        double tmp;
        int tmp2;
        double pivot = arr[(left + right) / 2];
        while (i <= j) {
            while (arr[i] < pivot)
                i++;
            while (arr[j] > pivot)
                j--;
            if (i <= j) {
                tmp = arr[i];
                tmp2 = index_array[i];
                arr[i] = arr[j];
                index_array[i] = index_array[j];
                arr[j] = tmp;
                index_array[j] = tmp2;
                i++;
                j--;
            }
        };
        return i;
    }           

    void quickSort(double arr[], int index_array[], int left, int right) {
        int index = partition(arr,index_array,left, right);
        if (left < index - 1)
            quickSort(arr, index_array,left, index - 1);
        if (index < right)
            quickSort(arr, index_array,index, right);
    }

    double difference(double[] vector_1 , double[] vector_2){
        double[] result = new double[vector_1.length];
        double modul = 0.0;
        for (int i = 0; i < vector_1.length; i++){
            result[i] = vector_1[i] - vector_2[i];
        }
        for (int i = 0; i < vector_1.length; i++){
            modul = modul + (result[i]*result[i]);
        }
        modul = Math.sqrt(modul);
        return modul;
    }


    /*
     *   Chooses a probability vector a, and repeatedly computes
     *   aP, aP^2, aP^3... until aP^i = aP^(i+1).
     */
    void iterate( int numberOfDocs, int maxIterations ) {
        //Declarar x' i x.
        double[] x_comma = new double[numberOfDocs];
        double[] x = new double[numberOfDocs];
        double sum = 0.0;

        //Init state:
        for(int i = 0; i < numberOfDocs; i++){
            x[i] = 0.0;
            x_comma[i] = 0.0;
        }
        x_comma[0] = 1.0;

        int iterations = 0;
        while (iterations < maxIterations && difference(x,x_comma) > EPSILON){
            for(int i = 0; i < numberOfDocs; i++){
                x[i]=x_comma[i];
            }
            for(int i = 0; i < numberOfDocs; i++){
                sum = 0.0;
                for (int j = 0; j< numberOfDocs; j++){
                    sum += x[j]*p[j][i];
                }
                x_comma[i] = sum;
            }
            iterations = iterations+ 1;
        }

        double max = -1000000;
        for (int i = 0; i< numberOfDocs; i++){
            if (x_comma[i] > max){
                max = x_comma[i];
            }
        }

        double[] vector_final = new double[x_comma.length];
        int[] vector_indexes = new int[x_comma.length];
        
        for (int i =0; i < x_comma.length; i++){
            vector_final[i] = x_comma[i];
            vector_indexes[i] = i;
        }
        //Now we need to sort them but also keeping the track of the indexes.
        quickSort(vector_final,vector_indexes, 0, vector_final.length-1);

        for (int i = numberOfDocs-1; i > (numberOfDocs-31); i--){
            System.out.printf(docName[vector_indexes[i]] + ":  ");
            System.out.printf("%.5f\n",vector_final[i]);
        }
    }


    /* --------------------------------------------- */


    public static void main( String[] args ) {
	if ( args.length != 1 ) {
	    System.err.println( "Please give the name of the link file" );
	}
	else {
	    new PageRank( args[0] );
	}
    }
}