package xing.kdd2013;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fairisaac.mb.analytics.binning.auto.FreqTable;
import com.fairisaac.mb.api.data.Dataset;
import com.fairisaac.mb.data.api.factory.DatasetFactory;

// read paper.mbd train.mbd, generate cluster related dataset. 
public class ClusterTitle {

	public static void genClusterVars(Dataset trainDs,
			String output) {

		
		int author, paper, numCurAuthor;

		Set<String> commonWords = new HashSet();
		commonWords.add("the");
		// TODO. 
		
		Map<Integer, Set<Integer>> confirmPapers = new HashMap();
		String title;
		Map<Integer, Set<String>> paperTitles = new HashMap();
		Set<Integer> papers;
		for (Map rec : trainDs) {
			author = ((Number) rec.get("id")).intValue();
			paper = ((Number) rec.get("paperId")).intValue();
    		numCurAuthor = ((Number) rec.get("numCurAuthor")).intValue();
    	    title = (String) rec.get("Title");
    	    Set<String> words = cleanTitle(title);
    	    
    	    // remove freq words? 
			if (numCurAuthor > 1) {
				if (confirmPapers.containsKey(author))
					confirmPapers.get(author).add(paper);
				else {
					papers = new HashSet();
					papers.add(paper);
					confirmPapers.put(author, papers);
				}
			}
			words.removeAll(commonWords);
			paperTitles.put(paper, words);
		}
		
		System.out.println("confirmed authors: " + confirmPapers.size());
	
		// generate confirm paper info: conference/journal ids, keywords set,
		// title keywords set.

		Map<Integer, ConfirmPapersInfo> confirmDetails = new HashMap();
		Map paperRow;
		ConfirmPapersInfo confirmRow;
		Set<Integer> conferenceIds;
		Set<Integer> journalIds;
		Set<String> keywords;
		Set<String> titleWords;
		Set<String> coauthors;
		int conferenceId, journalId;
		String keyword;
		String paperKeywords;
		for (int key : confirmPapers.keySet()) {
			papers = confirmPapers.get(key);
			conferenceIds = new HashSet();
			journalIds = new HashSet();
			keywords = new HashSet();
			titleWords = new HashSet();
			coauthors = new HashSet();
		
			for (int paperid : papers) {
				titleWords.addAll((Set<String>) paperTitles.get(paperid));
			}
			confirmRow = new ConfirmPapersInfo(conferenceIds, journalIds,
					keywords, titleWords, coauthors);
			confirmDetails.put(key, confirmRow);
		}
		System.out.println("confirm papers info generated");

		// var gen:

		Dataset dsOut = DatasetFactory.create(output, true);
		int cluster_title;

		
		String cluster_title2="";

		int i = 0;
		for (Map rec : trainDs) {
			author = ((Number) rec.get("id")).intValue();
			paper = ((Number) rec.get("paperId")).intValue();
			numCurAuthor = ((Number) rec.get("numCurAuthor")).intValue();
			
			
			cluster_title2="";

			if (numCurAuthor == 1) {
					
				confirmRow = confirmDetails.get(author);

				if (confirmRow == null)
					cluster_title = 0;
				else {

					Set<String> matchWords  = new HashSet(paperTitles.get(paper));
					
					
					
					// nothing.
					if (matchWords.size() == 0) {
						if (confirmRow.titleWords.size() == 0)
							cluster_title = 300;
						else
							cluster_title = 100;
					} else {
						if (confirmRow.titleWords.size() == 0)
							cluster_title = 200;
						else {
							matchWords.retainAll(confirmRow.titleWords);
							/*
							Set<String> filtered = new HashSet();
							for(String word: matchWords) {
                                cluster_title3 += 1.0/wordFreq.get(word);
								if(wordFreq.get(word) > 1000)
                            		continue;
								filtered.add(word);
                            	
                            }
                            */
							for(String word: matchWords)
							cluster_title2 += word + " ";
									cluster_title2 = cluster_title2.trim();
							cluster_title = 1000 + matchWords.size();
						}
					}
				}

			} else {
				cluster_title = -1;
			}
			
			rec.put("cluster_title", cluster_title);
			rec.put("cluster_title2", cluster_title2);
			dsOut.add(rec);
			if (++i % 10000 == 0)
				System.out.println("recs processed: " + i);
		}
		dsOut.save();
	}
	
	public static Set<String> cleanTitle(String title) {
		title = title.replaceAll(":", " ");
		title = title.replaceAll("\"", "");
		title = title.replaceAll("\\.", "");
		title = title.replaceAll(" +", " ");
		
		title = title.toLowerCase();
		title = title.trim();
		
		Set words = new HashSet();

		String[] raws = title.split(" ");

		for (String raw : raws) {
			if (raw.length() >= 5) // TODO. remove freq.
				words.add(raw);
		}

		return words;
	}

	
}

