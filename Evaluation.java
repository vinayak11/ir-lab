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

public class Evaluation {
	
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
	
	@SuppressWarnings({ "unchecked", "resource" })
	public static void main(String[] args) throws IOException
	{
		HashMap<Integer, String> doc_hmap = new HashMap<Integer, String>();
		HashMap<String, Integer> stopword_hmap = new HashMap<String, Integer>();
		HashMap<String, HashMap<Integer, Integer>> main_hmap = new HashMap<String, HashMap<Integer, Integer>>();
		
		Scanner user_input = new Scanner(System.in);
		System.out.println("Enter path of stopword: ");
		String stopword_dir = user_input.next(); 		
		System.out.println("Enter path of test cases: ");
		String target_directory = user_input.next();
		
		//Reading Stopword file and storing it in an array stopword_arr
		File stop_dir = new File(stopword_dir);
		//File stop_dir = new File("/Users/Chelsea/Desktop/stop_words");
		File [] stop_files= stop_dir.listFiles();
		//File stop_file = stop_dir.getAbsoluteFile();
		for(File f: stop_files){
		if(f.isFile()){
			BufferedReader inputStopwords= null;
				try{
					inputStopwords = new BufferedReader(new FileReader(f));
				
					String line;
					while((line=inputStopwords.readLine())!=null)
					{
					
						String[] stopwords=line.split(" ");
						for(String s : stopwords){
							stopword_hmap.put(s,1);
						}
					}
				} finally {
					if (inputStopwords != null) {
		                inputStopwords.close();
		            }
				}
			}
		}
		//End of reading stopwords
		
		//reading text files from testcase folder
		File dir = new File(target_directory);
		//File dir = new File("/Users/Chelsea/Desktop/hindi");
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
        }
    */
    /*Evaluation goes here*/
    System.out.println(+docid+" Documents Indexed");
    System.out.println("Press '1' for evaluation and '0' to EXIT: ");
    Scanner abc = new Scanner(System.in);
    int test = abc.nextInt();
    while(test!=0){
    	switch(test){
    		case 1 :{
    			System.out.println("Please input query: ");
    			Scanner sc = new Scanner(System.in);
    			String query = sc.nextLine();
    			String[] query_word = query.split(" ");
    			System.out.println("please input query no.(in 2 digits): ");
    			String query_num= sc.next();
    			String full_queryname= "english-document-000"+query_num+".txt";
    			HashMap<String, HashMap<String, Integer>> QrelHmap = new HashMap<String, HashMap<String, Integer>>();
    			System.out.println("please provide qrel path:");
    			String qrel_path = sc.next();
    			//reading qrel file
    			File qrel_dir = new File(qrel_path);
    			//File qrel_file = qrel_dir.getAbsoluteFile();
    			File [] qrel_files= qrel_dir.listFiles();
    			for(File f: qrel_files){
    				if(f.isFile()){
    					BufferedReader br= null;
    					try{
    						br = new BufferedReader(new FileReader(f));
    						String newLine;
    						while((newLine = br.readLine())!=null)
    						{
    							String[] line = newLine.split(" ");
    							String eng_query=null;
    							String hin_doc=null;
    							String boolval=null;
    							int count=0;
    							for(String s : line){
    								count=count+1;
    								switch(count){
    								case 1 :
    									eng_query = s;
    									break;
    								case 3 :
    									hin_doc = s;
    									break;
    								case 4 :
    									boolval = s;
    									break;
    								default : 
    									break;
    								}
    								//Constructing Qrel Hashmap
    								if(boolval!=null){
    									if(boolval.equals("1")){
    										HashMap<String, Integer> temp_hmap = new HashMap<String, Integer>();
    										if(QrelHmap.isEmpty()){
    											temp_hmap.put(hin_doc, 1);
    											QrelHmap.put(eng_query, temp_hmap);
    										}
    										else if(QrelHmap.containsKey(eng_query)){
    											HashMap<String, Integer> sub_hmap = new HashMap<String, Integer>();
    											sub_hmap=QrelHmap.get(eng_query);
    											sub_hmap.put(hin_doc, 1);
    											QrelHmap.put(eng_query, sub_hmap);
    										}
    										else{
    											temp_hmap.put(hin_doc, 1);
    											QrelHmap.put(eng_query, temp_hmap);
    										}
    									}
    								} 
    							}
    						}
    					} finally {
    						if (br != null) {
    							br.close();
    						}
    					}
    				}
    			} //end of reading all qrel files
	
    			TfIdfCalculator Object = new TfIdfCalculator();
    			HashMap<Integer, Double> rank_hmap = Object.Score(main_hmap, docid, query_word);
    			//System.out.println("rankhmap:"+Object.Score(main_hmap, docid, query_word));
    			rank_hmap = sortByValue(rank_hmap);
    			int i=1;
    			for(int t : rank_hmap.keySet()){
    				if(i<=20){
    					System.out.println("Rank:"+i+" Doc ID: "+t+" Document Name: "+doc_hmap.get(t)+" TFIDF: "+rank_hmap.get(t));
    				}
    				i++;
    			}
    			HashMap<String, Integer> submap = new HashMap<String, Integer>();
	
    			submap = QrelHmap.get(full_queryname);
    			int num_of_rel_docs = submap.size();
    			int relevant_in_ranklist_count = 0;
    			double arth_mean=0;
    			int num=1;
    			for(int rankset : rank_hmap.keySet()){
    				if(num<=20){
    					if(submap.containsKey(doc_hmap.get(rankset))){
    						relevant_in_ranklist_count++;
    						arth_mean = arth_mean+(double)(1/num);
    					}
    				}
    				num++;
    			}
    			double recall;
    			//System.out.println("rel in ranklist :"+relevant_in_ranklist_count+" "+" num of rel docs"+num_of_rel_docs);
    			recall = (double)relevant_in_ranklist_count/(double)num_of_rel_docs;
    			double precision;
    			precision = (double)relevant_in_ranklist_count/(double)20;
    			arth_mean = (double)arth_mean/(double)relevant_in_ranklist_count;
    			System.out.println("For this ranklist and query= "+full_queryname);
    			System.out.println("Precision: " +precision);
    			System.out.println("Recall: " +recall);
    			System.out.println("AP: "+arth_mean);
    		}
    		break;
    		default:
    			break;
    		}
    		System.out.println("Press '1' for evaluation and '0' to EXIT: ");
    		test= abc.nextInt();
    	}
    }
}

// /Users/Chelsea/Desktop/stop_words
//	/Users/Chelsea/Desktop/vinayak
// /Users/Chelsea/Desktop/hindiz
// /Users/Chelsea/Desktop/New