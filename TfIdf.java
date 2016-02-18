package vinayak;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class TfIdf {
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static HashMap sortByValue(HashMap unSortedHmap){
		List list = new LinkedList(unSortedHmap.entrySet());
		Collections.sort(list, new Comparator() {
			public int compare(Object o1, Object o2) {
				return ((Comparable) ((HashMap.Entry) (o2)).getValue()).compareTo(((HashMap.Entry) (o1)).getValue());
			}
		});
		HashMap sortedHmap = new LinkedHashMap();
		for (Iterator it = list.iterator(); it.hasNext();) {
			HashMap.Entry entry = (HashMap.Entry) it.next();
			sortedHmap.put(entry.getKey(), entry.getValue());
		}
		return sortedHmap;
	}
	
	@SuppressWarnings({ "unchecked" })
	public static void main(String[] args) throws IOException
	{
		HashMap<Integer, String> doc_hmap = new HashMap<Integer, String>();
		HashMap<String, Integer> stopword_hmap = new HashMap<String, Integer>();
		HashMap<String, HashMap<Integer, Integer>> main_hmap = new HashMap<String, HashMap<Integer, Integer>>();
		HashMap<Integer, HashMap<String, Integer>> direct_hmap = new HashMap<Integer, HashMap<String, Integer>>();
		HashMap<String, Integer> word_hmap = new HashMap<String, Integer>();
		HashMap<Integer, Integer> rank_doc_hmap = new HashMap<Integer, Integer>();
		
		Scanner user_input = new Scanner(System.in);
		
		System.out.println("Enter path of stopword: ");
		String stopword_dir = user_input.next(); 		//"/Users/Chelsea/Desktop/stop_words";
		
		System.out.println("Enter path of test cases: ");
		String target_directory = user_input.next();
		
		//Reading Stopword file and storing it in an array stopword_arr
		File stop_dir = new File(stopword_dir);
		File stop_file = stop_dir.getAbsoluteFile();
		if(stop_file.isFile()){
				BufferedReader inputStopwords= null;
				try{
					inputStopwords = new BufferedReader(new FileReader(stop_file));
				
					while(inputStopwords.readLine()!=null)
					{
					
						String[] stopwords=inputStopwords.readLine().split(" ");
						for(String s : stopwords){
							stopword_hmap.put(s,1);
						}
					}
				} finally {
					if (inputStopwords != null) {
		                inputStopwords.close();
		            }
				}
			} //End of reading stopwords
		
		//reading text files from testcase folder
		File dir = new File(target_directory);
		File[] files = dir.listFiles();
		int docid=0; //doc id of the file 
        for (File f : files) {
            if(f.isFile()) {
                BufferedReader inputStream = null;
                docid++;  //giving the doc id
                doc_hmap.put(docid, f.getName());
                try {
                    inputStream = new BufferedReader(new FileReader(f));
                    String line;
                    List<String> word_list = new ArrayList<String>();
                    
                    //tokenize the lines and add to wordlist arraylist
                    while ((line = inputStream.readLine()) != null) {
                        String[] words = line.split(" ");
                        for(String st : words){
                        	word_list.add(st);
                        }
                    }
                    
                
                    //finding term frequency of and associating doc ids.
                    for(String word : word_list){
                    	if(stopword_hmap.containsKey(word) || word.equals("") || word.equals(" ")){
                		}
                    	else{
                    		//making direct index and storing it in direct_hmap
                    		int term_freq= Collections.frequency(word_list,word);
                    		HashMap<String, Integer> sub_hmap1 = new HashMap<String, Integer>();
                    		if(direct_hmap.isEmpty()){
                    			sub_hmap1.put(word, term_freq);
                    			direct_hmap.put(docid, sub_hmap1);
                    		}
                    		else{
                    			direct_hmap.get(docid);
                    			direct_hmap.put(docid, sub_hmap1);
                    		}
                    		HashMap<Integer, Integer> temp_hmap = new HashMap<Integer, Integer>();
                    		temp_hmap.put(docid, term_freq);
                    		// logic to put docids, tf of a word in a hashmap
                    		if(main_hmap.isEmpty())
                    		{
                    			main_hmap.put(word, temp_hmap);
                    		}
                    		else
                    		{
                    			HashMap<Integer, Integer> sub_hmap = new HashMap<Integer, Integer>();
                    			if(main_hmap.containsKey(word))
                    			{
                    				sub_hmap= main_hmap.get(word);
                    				sub_hmap.put(docid, term_freq);
                    				main_hmap.put(word, sub_hmap);
                    			}
                    			else main_hmap.put(word, temp_hmap);
                    		}
                		}
                    }   
                }
                finally {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                }
            }
        System.out.println("file name:"+f.getName());  
        }
    /*PrintStream output = new PrintStream(new FileOutputStream("output.txt"));
    System.setOut(output);*/
    /*for(String s: main_hmap.keySet()){
        System.out.print(s+" [");
        for(int p:main_hmap.get(s).keySet()){
        	System.out.print(" Docid: "+p);
        	System.out.print(" TF: "+main_hmap.get(s).get(p));
        }	
        System.out.println("]");
        }*/
    System.out.println("Search: ");
    @SuppressWarnings("resource")
	Scanner sc = new Scanner(System.in);
	String query = sc.nextLine();
	String[] query_word = query.split(" ");
	TfIdfCalculator Object = new TfIdfCalculator();
	HashMap<Integer, Double> rank_hmap = Object.Score(main_hmap, docid, query_word);
	rank_hmap = sortByValue(rank_hmap);
	int i=1;
	for(int t : rank_hmap.keySet()){
		if(i<=10){
		System.out.println("Rank:"+i+" Doc ID: "+t+" Document Name: "+doc_hmap.get(t)+" TFIDF: "+rank_hmap.get(t));
		}
		i++;
	}
	//relevance feedback goes here
	HashMap<Integer, double[]> relevant_hmap = new HashMap<Integer, double[]>();
	HashMap<Integer, double[]> nonrelevant_hmap = new HashMap<Integer, double[]>();
	HashMap<String, Double> king_hmap = new HashMap<String, Double>();
	System.out.println("Enter '0' for User Feedback\nEnter '1' for Pseudo Relevance Feedback: ");
	int integer = sc.nextInt();
	String input_rank = null;
	if(integer==0){
	System.out.println("Enter Relevant Documents Rank: ");
	@SuppressWarnings("resource")
	Scanner sc1 = new Scanner(System.in);
	input_rank = sc1.nextLine();
	}
	else if(integer==1){
		@SuppressWarnings("resource")
		Scanner sc1 = new Scanner("1 2 3 4 5");
		input_rank = sc1.nextLine();
	}
	
	String [] ranks = input_rank.split(" ");
	
	int [] rd = new int [10];
	for(int j = 0; j<ranks.length; j++){
		if(ranks[j]!=null)
			rd[j]= Integer.parseInt(ranks[j]);	
	}
	//making word_hmap<word, Index>
	int q=1;
	for(String s: main_hmap.keySet()){
		word_hmap.put(s, q);
		q= q+1;
	}
	//making rank_doc_hmap<rank, docid>
	int m=1;
	for(int z: rank_hmap.keySet()){
		rank_doc_hmap.put(m, z);
		m=m+1;
	}
	//making query_vector= q0
	int [] query_vector = new int [word_hmap.size()];
	for(int j=0; j<word_hmap.size(); j++){
		query_vector[j]=0;
	}
	for(int j=0; j<query_word.length;j++){
		if(word_hmap.containsKey(query_word[j])){
			int index = word_hmap.get(query_word[j]);
			query_vector[index-1] = 1;
		}
	}
	//computing relevant documents vector
	for(int j = 0; j<rd.length; j++){
		double[] rv = new double [word_hmap.size()];
		for(int k=0; k<word_hmap.size();k++){
			rv[k]=0;
			if(rank_doc_hmap.get(rd[j])!=null){
			int did = rank_doc_hmap.get(rd[j]);
			HashMap<String, Integer> sub_hmap = direct_hmap.get(did);
			for(String s: sub_hmap.keySet()){
				int index = word_hmap.get(s);
				rv[index]= sub_hmap.get(s);
				}
			}
		}
		relevant_hmap.put(rd[j], rv);
	}
	//adding relevant docs vector
	double[] sum= new double [word_hmap.size()];
	double[] sum1=new double [word_hmap.size()];
	for(int k=0;k<word_hmap.size(); k++){
		sum[k]=0;
		sum1[k]=0;
	}
	for(int j= 0; j<rd.length; j++){
		for(int k=0;k<word_hmap.size();k++){
			sum[k] = sum[k]+relevant_hmap.get(rd[j])[k];
		}
	}
	//multiplying second vector by 0.75/rd.length
	double beta =  0.75/(double)rd.length;  
	for(int k =0; k<word_hmap.size();k++){
		sum[k] = beta*sum[k];
	}
	//second term of rocchio algo end here
	
	//computing relevant docs from rank_doc_hmap
	for(int k = 0 ; k<rd.length; k++){
		if(rank_doc_hmap.containsKey(rd[k])){
			rank_doc_hmap.remove(rd[k]);
		}
	}
	//computing non relevant document vectors
	int x=0;
	for(int k : rank_doc_hmap.keySet()){
		if(x<500){
		double[] nrv=new double [word_hmap.size()];
		for(int j =0; j<word_hmap.size();j++){
			nrv[j]=0;
			int did = rank_doc_hmap.get(k);
			HashMap<String, Integer> sub_hmap = direct_hmap.get(did);
			for(String s:sub_hmap.keySet()){
				int index = word_hmap.get(s);
				nrv[index]= sub_hmap.get(s);
			}
			nonrelevant_hmap.put(k, nrv);
		}
	} x=x+1;
}
	//adding non relevant doc vectors
	int x1=0;
	for(int j: rank_doc_hmap.keySet()){
		if(x1<100){
		for(int k=0; k<word_hmap.size();k++){
			sum1[k]=sum1[k]+nonrelevant_hmap.get(j)[k];
		}
		} x1=x1+1;
	}
	//multiplying third vector by 0.25/rank_doc_hmap.size
	double theta = 0.25/(double)rank_doc_hmap.size();
	for(int k =0; k<word_hmap.size();k++){
		sum1[k] = theta*sum1[k];
	}
	//calculating query+sum-sum1;
	for(int j=0; j<word_hmap.size();j++){
		sum[j]= query_vector[j]+sum[j]-sum1[j];
	}
	//make the king_map<word,sum>
	for(String s: word_hmap.keySet()){
		king_hmap.put(s, sum[word_hmap.get(s)-1]);
	}
	//sort the king_map
	String[] query_new = new String[10];
	king_hmap= sortByValue(king_hmap);
	int w=1;
	for(String s: king_hmap.keySet()){
		if(w<=10){
			//System.out.println("New query:"+s);
			query_new[w-1]=s;
		} 
		w++;
	}
	//run tfidf for new query
	HashMap<Integer, Double> rank_hmap1 = Object.Score(main_hmap, docid, query_new);
	rank_hmap1 = sortByValue(rank_hmap1);
	int u=1;
	for(int t : rank_hmap1.keySet()){
		if(u<=10){
		System.out.println("Rank:"+u+" Doc ID: "+t+" Document Name: "+doc_hmap.get(t)+" TFIDF: "+rank_hmap1.get(t));
		}
		u++;
	}
	}
}
