import java.util.*;
import java.io.*;
import java.text.DecimalFormat;

public class PageRankSparse {

    /**  
     *   Maximal number of documents. We're assuming here that we
     *   don't have more docs than we can keep in main memory.
     */
    final static int MAX_NUMBER_OF_DOCS = 2000000;

    /**
     *   Mapping from document names to document numbers.
     */
    HashMap<String,Integer> docNumber = new HashMap<String,Integer>();

    /**
     *   Mapping from document numbers to document names
     */
    String[] docName = new String[MAX_NUMBER_OF_DOCS];

    /**  
     *   A memory-efficient representation of the transition matrix.
     *   The outlinks are represented as a HashMap, whose keys are 
     *   the numbers of the documents linked from.<p>
     *
     *   The value corresponding to key i is a HashMap whose keys are 
     *   all the numbers of documents j that i links to.<p>
     *
     *   If there are no outlinks from i, then the value corresponding 
     *   key i is null.
     */
    HashMap<Integer,HashMap<Integer,Boolean>> link = new HashMap<Integer,HashMap<Integer,Boolean>>();

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
     *   Convergence criterion: Transition probabilities do not 
     *   change more that EPSILON from one iteration to another.
     */
    final static double EPSILON = 0.0001;


       
    /* --------------------------------------------- */

    public double Performance(double [] x_real, int[] index_real, double [] x_approx,int[] index_approx ){
    	double score  = 0;
    	//Just the 30 documents with higher score.
    	for (int i = 0; i < 30; i ++){
    		int id = index_real[i];
    		for (int k = 0; k < x_approx.length; k++){
    			//Compute the difference:
    			if (index_approx[k] == id){
    				double difference = 0;
    				difference = x_real[i] - x_approx[k];
    				difference = difference*difference;
    				score = score + difference;
    			}
    		}
    	}
    	return score;
    }

    public  int[] Update_Indexreal(int numberOfDocs){
    	int[] index_real = new int[numberOfDocs];
    	double[] x_real = new double[numberOfDocs];
    	try {
			BufferedReader br = new BufferedReader(new FileReader("PageRankScore.txt"));
    		String line = br.readLine();
    		int k = 0;
    		while (line != null) {
    		String [] parts = line.split(":");
    		String doc_id = parts[0];
    		String value = parts[1];
    		index_real[k] = Integer.parseInt(doc_id);
    		x_real[k] = Double.parseDouble(value);
    		k = k + 1;
    		line = br.readLine();
    	}
    	br.close();
    	//Now we have the x_real and x_approx.
    	//Print performance:
		} catch (IOException e) {
    		e.printStackTrace();
    	}
    	return index_real;
    }
    public double[] Update_xreal(int numberOfDocs){
    	double[] x_real = new double[numberOfDocs];
    	int[] index_real = new int[numberOfDocs];
    	try {
			BufferedReader br = new BufferedReader(new FileReader("PageRankScore.txt"));
    		String line = br.readLine();
    		int k = 0;
    		while (line != null) {
    		String [] parts = line.split(":");
    		String doc_id = parts[0];
    		String value = parts[1];
    		index_real[k] = Integer.parseInt(doc_id);
    		x_real[k] = Double.parseDouble(value);
    		k = k + 1;
    		line = br.readLine();
    	}
    	br.close();
    	//Now we have the x_real and x_approx.
    	//Print performance:
    			
		} catch (IOException e) {
    		e.printStackTrace();
    	}
    	return x_real;
    }

    public void CreateMethodFile(int method, double[] score){
    	try{
    		String name = String.valueOf(method);
    		name = name + ".txt";
			PrintWriter writer = new PrintWriter(name, "UTF-8");
			for (int i = 0; i < score.length; i ++){
				DecimalFormat df = new DecimalFormat("#.########");
				writer.println(df.format(score[i]));
				System.out.println("The score is: " +score[i]);
			}
			writer.close();    	
		}catch (IOException e) {
    		e.printStackTrace();
    	}
    }

    public PageRankSparse( String filename ) {
    	int noOfDocs = readDocs( filename );
    	
    	//If you want montecarlo
    	int montecarlo = 0;
    	//Which montecarlo tecnhique.
        int method = 5;

        //Just the second part of the assignment 2.8
        int part2 = 1;
        int N = 0;
        double[] score = new double[50];
		if (montecarlo == 0){
			//Generate the file using sparse representation.
			iterate( noOfDocs, 1000 );
    	}else{
    		if (part2 == 1){
    			method = 5;
    			N = 500;
    			double [] x_approx = new double[noOfDocs];
   				int [] index_approx = new int[noOfDocs];
   				System.out.println("Comencem montecarlo");
				x_approx = Montecarlo_Function(method,noOfDocs,N);
				System.out.println("Hem acabat el montecarlo");
				//As we have the file created, we just need to read the file:
    			for (int i =0; i < x_approx.length; i++){
					index_approx[i] = i;
        		}
        		quickSort(x_approx,index_approx, 0, x_approx.length-1);
        		for (int i = noOfDocs-1; i > (noOfDocs-31); i--){
            		System.out.printf(docName[index_approx[i]] + ":  ");
            		System.out.printf("%.5f\n",x_approx[i]);
        		}
    		}else{
    		int [] index_real = new int[noOfDocs];
    		double [] x_real = new double[noOfDocs];
    		x_real = Update_xreal(noOfDocs);
    		index_real = Update_Indexreal(noOfDocs);
    		System.out.println("Information updated");
    		//We are going to evaluate the performance per different number of N.
    		for (int numwalks = 1; numwalks < 50; numwalks ++ ){
    		System.out.println("Estem en el numwalk:");
    		System.out.println(numwalks);
    		N = numwalks;
    		//For Montecarlo approximation.To compare it.
    		double [] x_approx = new double[noOfDocs];
   			int [] index_approx = new int[noOfDocs];


    		//We already have the file created.
    		x_approx = Montecarlo_Function(method,noOfDocs,N);
    		//As we have the file created, we just need to read the file:
    		for (int i =0; i < x_approx.length; i++){
				index_approx[i] = i;
        	}
        	quickSort(x_approx,index_approx, 0, x_approx.length-1);
        	//Update the indexes to the proper name:
        	for (int j = 0; j < x_approx.length; j++){
        		index_approx[j] = Integer.parseInt(docName[index_approx[j]]);
        	}
        	double score_aux  = Performance(x_real,index_real,x_approx,index_approx);
    		score[numwalks] = score_aux;
    		}
    		System.out.println("Create!");
    		CreateMethodFile(method,score);
    		System.out.println("File created");
    		//Now we have the total score per specific method + different number of N:
    		//Create a file to plotit:
    		}

    	}
    }

    public ArrayList<Integer> randomWalk(int numberOfDocs,int init_document, int method){
    	Random rand_function = new Random();
    	ArrayList<Integer> path = new ArrayList<Integer>();
    	path.add(init_document);
    	HashMap<Integer,Boolean> link_out;

    	int document = init_document;
    	int finish = 0;	
    	while (finish == 0){
    		if (rand_function.nextDouble() >= BORED){
    			//Start the random walk.
    			//Check where I can go (from my init position -> out-links)
    			link_out = link.get(document);
    			//no outlinks in this specific page:
    			if (link_out == null){
    				//Just go to a new page If it is permitted.
    				if (method != 4 && method != 5){
                        document = rand_function.nextInt(numberOfDocs);
                    }else{
                        finish = 1;
                    }
    			}else{
    				//We have out-links.
    				//Choose randomly one of them. (index selection)
    				int out_link = rand_function.nextInt(link_out.size());
    				//Get the out.links
    				ArrayList<Integer> x_comma_list = new ArrayList<Integer>(link_out.keySet());
    				//Choose the outlink that we are going to use.
    				document = x_comma_list.get(out_link).intValue();
    			}
    			//Add it to the path.
    			path.add(document);
    		}else{
    			//Finish the random walk.
    			finish = 1;
    		}
    	}
    	return path;
    }

   	double[] Montecarlo_Function(int method, int numberOfDocs, int N){
    	//different configurations for the randomWalk.
    	double [] x = new double[numberOfDocs];
    	//We would need to compute all the performance for different values of N.
    	Random rand_function = new Random();

    	if (method ==  1){
    		for (int i = 0; i < N; i++){
    			int init_document = rand_function.nextInt(numberOfDocs);
    			ArrayList<Integer> path = randomWalk(numberOfDocs,init_document,method);
    			int last_element = path.get(path.size() - 1).intValue();
    			x[last_element] = x[last_element] + 1;
    		}
    		//Compute the normalization:
    		for (int i = 0; i < numberOfDocs; i++){
    			x[i] = x[i]/(double)N;
    		}
    		//Now we have the result x_comma.
    	}else{
    		if (method == 2){
                //N = numberOfDocuments*numWalks;
                for (int i = 0; i < numberOfDocs;i++){
                    for (int j = 0; j < N; j++){
                        ArrayList<Integer> path = randomWalk(numberOfDocs,i,method);
                        int last_element = path.get(path.size() - 1).intValue();
                        x[last_element] = x[last_element] + 1;
                    }
                }
                //Compute the normalization:
                for (int i = 0; i < numberOfDocs; i++){
                    x[i] = x[i]/(double)(N*numberOfDocs);
                }
    		}else{
    			if (method ==4){
                    int total_length = 0;
                    for (int i = 0; i < numberOfDocs;i++){
                        for (int j = 0; j < N; j++){
                            ArrayList<Integer> path = randomWalk(numberOfDocs,i,method);
                            for (int k = 0; k < path.size(); k++){
                                int element = path.get(k).intValue();
                                x[element] = x[element] + 1;
                            }
                            total_length = total_length + path.size();
                        }
                    }
                    for (int i = 0; i < numberOfDocs; i++){
                        x[i] = x[i]/(double)total_length;
                    }
    			}else{
    				//method 5
                    int total_length = 0;
                    for (int i = 0; i < N; i++){
                        int init_document = rand_function.nextInt(numberOfDocs);
                        ArrayList<Integer> path = randomWalk(numberOfDocs,init_document,method);
                        System.out.println("Estem al iteration: " + i);
                        for (int k = 0; k < path.size(); k++){
                            int element = path.get(k).intValue();
                            x[element] = x[element] + 1;
                        }
                        total_length = total_length + path.size();
                    }
                    for (int i = 0; i < numberOfDocs; i++){
                        x[i] = x[i]/(double)total_length;
                    }
    			}
    		}
    	}
        //Now we have x computed.
        //Now we need to check the performance compared with the true one.
        return x;
    }


    /* --------------------------------------------- */


    /**
     *   Reads the documents and fills the data structures. 
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
		    // Set the probability to 0 for now, to indicate that there is
		    // a link from fromdoc to otherDoc.
		    if ( link.get(fromdoc) == null ) {
			link.put(fromdoc, new HashMap<Integer,Boolean>());
		    }
		    if ( link.get(fromdoc).get(otherDoc) == null ) {
			link.get(fromdoc).put( otherDoc, true );
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

    /* --------------------------------------------- */


    /*
     *   Chooses a probability vector a, and repeatedly computes
     *   aP, aP^2, aP^3... until aP^i = aP^(i+1).
     */
    void iterate( int numberOfDocs, int maxIterations ) {
    	//Declarar x' i x.
        double[] x_comma = new double[numberOfDocs];
        double[] x = new double[numberOfDocs];
	  	double sum = 0.0;
        long time_1 = new Date().getTime();
        for(int i = 0; i < numberOfDocs; i++){
            x[i] = 0.0;
            x_comma[i] = 0.0;
        }
        x_comma[0] = 1.0;

        int iterations = 0;
        while (iterations < maxIterations && difference(x,x_comma) > EPSILON){
        	//Update x to x_comma
        	for(int i = 0; i < numberOfDocs; i++){
                x[i]=x_comma[i];
            }
            for (int i = 0; i < x.length; i++) {
            	//Here we represent the equation: BORED*J where J is the matrix with 1/N probabilities.
	        	x_comma[i] = BORED/x.length;
                //Per each element:
	        	for (int j = 0; j < x.length; j++) {
	        		//We don't have any outliers. Then the part of the matrix will be also 1/N.
	        		if (link.get(j) == null) {
	        			x_comma[i] = x_comma[i] + (x[j]*(1-BORED)/x.length);
	        		} else {
                        //If they have the page that you are evaluating as an outlier then...
	        			if (link.get(j).get(i) != null) {
                            //With x[j] we have the score from the particular page.
	        				x_comma[i] = x_comma[i]+ (x[j] * (1-BORED)/link.get(j).size());
	        			} 
	        		}
	        	} 
	        }
            iterations = iterations + 1;
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
        long time_2 = new Date().getTime();
        long times = time_2- time_1;
        System.out.println("The time is: " +times + " (ms)");

        //Generate a file with the results:
		//This file will be used in the 2.7 task. 
		try{
			PrintWriter writer = new PrintWriter("PageRankScore.txt", "UTF-8");
			for (int i = numberOfDocs-1; i>= 0; i--){
				DecimalFormat df = new DecimalFormat("#.########");
				writer.println(docName[vector_indexes[i]] + ":" + df.format(vector_final[i]));
			}
			writer.close();    	
		}catch (IOException e) {
    		e.printStackTrace();
    	}
    }


    /* --------------------------------------------- */


    public static void main( String[] args ) {
	if ( args.length != 1 ) {
	    System.err.println( "Please give the name of the link file" );
	}
	else {
	    new PageRankSparse( args[0] );
	}
    }
}