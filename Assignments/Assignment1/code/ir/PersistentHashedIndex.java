/*  
 *   This file is part of the computer assignment for the
 *   Information Retrieval course at KTH.
 * 
 *   Johan Boye, KTH, 2018
 */  

package ir;

import java.io.*;
import java.util.*;
import java.nio.charset.*;


/*
 *   Implements an inverted index as a hashtable on disk.
 *   
 *   Both the words (the dictionary) and the data (the postings list) are
 *   stored in RandomAccessFiles that permit fast (almost constant-time)
 *   disk seeks. 
 *
 *   When words are read and indexed, they are first put in an ordinary,
 *   main-memory HashMap. When all words are read, the index is committed
 *   to disk.
 */
public class PersistentHashedIndex implements Index {

    /** The directory where the persistent index files are stored. */
    public static final String INDEXDIR = "./index";

    /** The dictionary file name */
    public static final String DICTIONARY_FNAME = "dictionary";

    /** The dictionary file name */
    public static final String DATA_FNAME = "data";

    /** The terms file name */
    public static final String TERMS_FNAME = "terms";

    /** The doc info file name */
    public static final String DOCINFO_FNAME = "docInfo";

    /** The dictionary hash table on disk can fit this many entries. */
    public static final long TABLESIZE = 611953L;  // 50,000th prime number

    /** The dictionary hash table is stored in this file. */
    RandomAccessFile dictionaryFile;

    /** The data (the PostingsLists) are stored in this file. */
    RandomAccessFile dataFile;

    /** Pointer to the first free memory cell in the data file. */
    long free = 0L;

    //Fixed size for store it in the dictionary.
    int fix_size = 800;

    /** The cache as a main-memory hash map. */
    HashMap<String,PostingsList> index = new HashMap<String,PostingsList>();


    // ===================================================================
    /**
     *   A helper class representing one entry in the dictionary hashtable.
     */ 
    public class Entry {

		private int length;       //Length of the string.   
		private long address;   //Address to the data file.
        private String token;  //Checksum to avoid colisions.

		public Entry(int length, long address, String token){
			this.length = length;
			this.address = address;
            this.token = token;
		}

		public void setLength(int length){
			this.length = length;
		}

		public int getLength(){
			return this.length;
		}

		public long getAddress(){
			return this.address;
		}

		public void setAddress(long address){
			this.address = address;
		}

        public String getToken(){
            return this.token;
        }

        public void setToken(String token){
            this.token = token;   
        }
    }

    // ==================================================================
 
    /**
     *  Constructor. Opens the dictionary file and the data file.
     *  If these files don't exist, they will be created. 
     */
    public PersistentHashedIndex() {
        try {
            dictionaryFile = new RandomAccessFile( INDEXDIR + "/" + DICTIONARY_FNAME, "rw" );
            dataFile = new RandomAccessFile( INDEXDIR + "/" + DATA_FNAME, "rw" );
        }
        catch ( IOException e ) {
            e.printStackTrace();
        }
        try {
            readDocInfo();
        }
        catch ( FileNotFoundException e ) {
        }
        catch ( IOException e ) {
            e.printStackTrace();
        }
    }

    /**
     *  Writes data to the data file at a specified place.
     *
     *  @return The number of bytes written.
     */ 
    int writeData( String dataString, long ptr ) {
        try {
            dataFile.seek(ptr); 
            byte[] data = dataString.getBytes();
            dataFile.write(data);
            return data.length;
        }
        catch ( IOException e ) {
            e.printStackTrace();
            return -1;
        }
    }


    /**
     *  Reads data from the data file
     */ 
    String readData( long ptr, int size ) {
        try {
            dataFile.seek( ptr );
            byte[] data = new byte[size];
            dataFile.readFully(data);
            return new String(data);
        }
        catch ( IOException e ) {
            e.printStackTrace();
            return null;
        }
    }


    // ==================================================================
    //
    //  Reading and writing to the dictionary file.

    public static String fixedLengthString(String string,int length){
        return String.format("%1$"+length+ "s", string);
    }

    void InitDictionary(){
        try{
            long ptr = 0L;
            String empty = new String("123456789");            
            while(ptr < ((TABLESIZE*fix_size) - fix_size)){
                dictionaryFile.seek(ptr);
                dictionaryFile.writeUTF(empty);
                ptr = ptr + fix_size;
            }
        }catch(IOException e ){
            e.printStackTrace();
        }
    }

    /*
     *  Writes an entry to the dictionary hash table file. 
     *
     *  @param entry The key of this entry is assumed to have a fixed length
     *  @param ptr   The place in the dictionary file to store the entry
     */
    void writeEntry( Entry entry,long ptr) {      
		try {
            int empty = 0;
            long max = (TABLESIZE*fix_size)-fix_size;
            long newptr = ptr*fix_size;
            while(empty == 0){
                dictionaryFile.seek(newptr);
                if (dictionaryFile.readUTF().toString().equals(new String("123456789"))){
                    empty = 1;
                }else{
                    newptr += fix_size;
                    if (newptr >= max){
                        newptr = 0;
                    }
                }
            }
            StringBuilder dataentry = new StringBuilder();
            dataentry.append(entry.getLength());
            dataentry.append("/");
            dataentry.append(entry.getAddress());
            dataentry.append("/");
            dataentry.append(entry.getToken());
            dataentry.append("/");
            dictionaryFile.seek(newptr);
            dictionaryFile.writeUTF(dataentry.toString());
            }catch ( IOException e ) {
                e.printStackTrace();
            }
    }

    /**
     *  Reads an entry from the dictionary file.
     *
     *  @param ptr The place in the dictionary file where to start reading.
     */
    Entry readEntry(long ptr, String token) {   
		try {
            int find = 0;
            String[] parts = new String[3];
            String part1,part2,part3,dat_string;
            //As in the writeEntry...
            long max = (TABLESIZE*fix_size)-fix_size;
            long newptr = ptr*fix_size;
            while (find == 0){
                dictionaryFile.seek(newptr);
                String aux = dictionaryFile.readUTF();
                parts = aux.split("/");
                part3 = parts[2];
                if (part3.toString().equals(token)){
                    find = 1;
                }else{
                    newptr += fix_size;
                    if (newptr >= max){
                        ptr = 0;
                    }
                }
            }
            part1 = parts[0];
            part2 = parts[1];
            Entry entry = new Entry(Integer.parseInt(part1),Long.parseLong(part2),token);
            return entry;
        }
        catch ( IOException e ) {
            e.printStackTrace();
            return null;
        }
    }

    //Compute the hash code regarding to the token.
    long HashFunction(String token){
    	long hash = 0;
        long x = token.hashCode();
    	hash = Math.abs(x)%TABLESIZE;
    	return hash;
    }	

    // ==================================================================

    /**
     *  Writes the document names and document lengths to file.
     *
     * @throws IOException  { exception_description }
     */
    private void writeDocInfo() throws IOException {
        FileOutputStream fout = new FileOutputStream( INDEXDIR + "/docInfo" );
        for (Map.Entry<Integer,String> entry : docNames.entrySet()) {
            Integer key = entry.getKey();
            String docInfoEntry = key + ";" + entry.getValue() + ";" + docLengths.get(key) + "\n";
            fout.write(docInfoEntry.getBytes());
        }
        fout.close();
    }

    /**
     *  Reads the document names and document lengths from file, and
     *  put them in the appropriate data structures.
     *
     * @throws     IOException  { exception_description }
     */
    private void readDocInfo() throws IOException {
        File file = new File( INDEXDIR + "/docInfo" );
        FileReader freader = new FileReader(file);
        try (BufferedReader br = new BufferedReader(freader)) {
            String line;
            while ((line = br.readLine()) != null) {
               String[] data = line.split(";");
               docNames.put(new Integer(data[0]), data[1]);
               docLengths.put(new Integer(data[0]), new Integer(data[2]));
            }
        }
        freader.close();
    }


    //String to PostingList.
    public PostingsList GeneratePostingList(String poststring){
    		PostingsList posting = new PostingsList();
    		//Each of the parts has the information of a particular document.
    		String[] parts = poststring.split("/");
  			//Go to each PostEntry
    		for (int i = 0; i < parts.length; i++){			
				//Achieve the docID.
				String[] parts2 = parts[i].split(",");
				//docID.
				int docID = Integer.parseInt(parts2[0]);
				//In parts3, we have the whole positions.
				for (int j  = 1; j < parts2.length; j++){
					int offset = Integer.parseInt(parts2[j]);
					posting.insert(docID,offset);
				} 			
    		}
    		return posting;
    }


    //PostingsList to String. 
    public String GenerateString(PostingsList value){
        StringBuilder sb = new StringBuilder();
        PostingsEntry tempEntry = null;
        int tempID = 0;
        for (int i = 0; i < value.size(); i++){
            tempEntry = value.get(i);
            tempID = tempEntry.docID;
            ArrayList<Integer> tempoffset = tempEntry.offsetlist;
            sb.append(tempID);
            for (int k = 0; k< tempoffset.size(); k++){
                sb.append(',');
                sb.append(tempoffset.get(k));
            }
            sb.append("/");
        }
        return sb.toString();
    }

    /**
     *  Write the index to files.
     */
    public void writeIndex() {
        int collisions = 0;
        try {
            writeDocInfo();
            System.out.println("Init the dictionary");
            InitDictionary();
            int length_aux_string = 0;
            String aux_string;
            String token;
            int non_free = 0;
            PostingsList aux =  null;
            System.out.println("We are going to print the HashIndex");
			for (Map.Entry<String,PostingsList> entry : index.entrySet()){
				token = entry.getKey();
				aux = entry.getValue();
				aux_string = GenerateString(aux);
                length_aux_string = aux_string.length();
                non_free = writeData(aux_string,free);
                writeEntry(new Entry(length_aux_string,free,token),HashFunction(token));
                free = free + (long)non_free;
			}
            System.out.println("We finished the HashIndex");
        }
        catch ( IOException e ) {
            e.printStackTrace();
        }
        System.err.println( collisions + " collisions.");
    }
    // ==================================================================
    /**
     *  Returns the postings for a specific term, or null
     *  if the term is not in the index.
     */
    public PostingsList getPostings( String token ) {
    	try{
    		//To get the address in the dictionary file.
			long dic_address = HashFunction(token);
			Entry token_entry = readEntry(dic_address,token);
			int length_entry = token_entry.getLength();
			long address_entry = token_entry.getAddress();
			String posting = readData(address_entry,length_entry);
			PostingsList posts = GeneratePostingList(posting);
			return posts;
		}catch(Exception e){
			return null;
		}    
    }

    /**
     *  Inserts this token in the main-memory hashtable.
     */
    public void insert( String token, int docID, int offset ) {
    	if(!index.containsKey(token)){
            index.put(token,new PostingsList());
            index.get(token).insert(docID,offset);
        }else{
            index.get(token).insert(docID,offset);
        }
    }

    public void PrintPostings(String token){
        System.out.println("HI HI");
    }

    /**
     *  Write index to file after indexing is done.
     */
    public void cleanup() {
        System.err.println( index.keySet().size() + " unique words" );
        System.err.print( "Writing index to disk..." );
        writeIndex();
        System.err.println( "done!" );
     }

}
