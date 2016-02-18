package vinayak;

import java.util.HashMap;

public class TfIdfCalculator {
	@SuppressWarnings("rawtypes")
	public HashMap Score(HashMap<String, HashMap<Integer, Integer>>main_hmap, int docid, String query_word[]){
		
		HashMap<Integer, Double> rank_hmap = new HashMap<Integer, Double>();
		int total_docs = docid;
		for(String query_w : query_word)
		{
			if(main_hmap.containsKey(query_w)){
				int df = main_hmap.get(query_w).size();
				double a = (double)total_docs/(double) df;
				double idf;
				idf = Math.log10(a);
				for(int t : main_hmap.get(query_w).keySet()){
					double tfidf = (main_hmap.get(query_w).get(t))*idf;
					if(rank_hmap.isEmpty()){
						rank_hmap.put(t, tfidf);
					}
					else{
						if(rank_hmap.containsKey(t)){
							double updated_tfidf = rank_hmap.get(t)+ tfidf;
							rank_hmap.put(t, updated_tfidf);
						}
						else
							rank_hmap.put(t, tfidf);
					}
				}
			}
		}
		return rank_hmap;
	}

}
