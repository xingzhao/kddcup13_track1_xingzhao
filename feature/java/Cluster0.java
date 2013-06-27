package xing.kdd2013;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fairisaac.mb.api.data.Dataset;
import com.fairisaac.mb.data.api.factory.DatasetFactory;

// read paper.mbd train.mbd, generate cluster related dataset. 
// TODO: keyword, title word match, coauthors. 
public class Cluster0 {

	public static void genClusterVars(Dataset paperDs, Dataset trainDs,
			String output) {

		int author, paper, numCurAuthor;

		// Map<Object, Integer> matchStats = new HashMap();

		Map<Integer, Set<Integer>> confirmPapers = new HashMap();
		Set<Integer> papers;
		Map<Integer, List<String>> allPapers = new HashMap();
		Map<Integer, String> authorNames = new HashMap();
		String authorName;
		List<String> paperAuthorNames;
		for (Map rec : trainDs) {
			author = ((Number) rec.get("id")).intValue();
			paper = ((Number) rec.get("paperId")).intValue();
			authorName = (String) rec.get("authorName");
			authorName = Match0.cleanName(authorName);
			authorNames.put(author, authorName);
			paperAuthorNames = (List) rec.get("paperNames");
			
			for(int i=0; i<paperAuthorNames.size(); i++) {
				paperAuthorNames.set(i, Match0.cleanName(paperAuthorNames.get(i)));
			}

			allPapers.put(paper, paperAuthorNames);
			numCurAuthor = ((Number) rec.get("numCurAuthor")).intValue();


			if (numCurAuthor > 1 ) {
				if (confirmPapers.containsKey(author))
					confirmPapers.get(author).add(paper);
				else {
					papers = new HashSet();
					papers.add(paper);
					confirmPapers.put(author, papers);
				}
			}
		}
		System.out.println("confirmed authors: " + confirmPapers.size());
		System.out.println("all papers: " + allPapers.size());
		Map<Integer, Map> paperDetails = new HashMap();
		for (Map rec : paperDs) {
			paper = ((Number) rec.get("Id")).intValue();
			if (allPapers.containsKey(paper))
				paperDetails.put(paper, rec);
		}
		System.out.println("paper details loaded");

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
			authorName = authorNames.get(key);
		
			for (int paperid : papers) {
				paperRow = paperDetails.get(paperid);
				conferenceId = ((Number) paperRow.get("ConferenceId"))
						.intValue();
				journalId = ((Number) paperRow.get("JournalId")).intValue();
				paperKeywords = (String) paperRow.get("Keyword");
				if (conferenceId > 0)
					conferenceIds.add(conferenceId);
				if (journalId > 0)
					journalIds.add(journalId);
				keywords.addAll(cleanKeywords(paperKeywords)); // freq?
				paperAuthorNames = allPapers.get(paperid);
				for (String name : paperAuthorNames) {
					if (name.length()>3 && !Match0.sameLastName(authorName, name)) // coauthor
																	// with
																	// different
																	// last
																	// name.
						coauthors.add(name);
				}

			}
			confirmRow = new ConfirmPapersInfo(conferenceIds, journalIds,
					keywords, titleWords, coauthors);
			confirmDetails.put(key, confirmRow);
		}
		System.out.println("confirm papers info generated");

		// var gen:

		Dataset dsOut = DatasetFactory.create(output, true);
		int cluster_conference, cluster_journal, cluster_keyword, cluster_coauthor;

		String cluster_keyword2, cluster_coauthor2;
		int i = 0;
		for (Map rec : trainDs) {
			author = ((Number) rec.get("id")).intValue();
			paper = ((Number) rec.get("paperId")).intValue();
			numCurAuthor = ((Number) rec.get("numCurAuthor")).intValue();
			cluster_keyword2 = "";
			cluster_coauthor2 = "";

			if (numCurAuthor == 1) {
				paperRow = paperDetails.get(paper);
				conferenceId = ((Number) paperRow.get("ConferenceId"))
						.intValue();
				journalId = ((Number) paperRow.get("JournalId")).intValue();
				paperKeywords = (String) paperRow.get("Keyword");

				confirmRow = confirmDetails.get(author);

				if (confirmRow == null)
					cluster_conference = cluster_journal = cluster_keyword = cluster_coauthor = 0;
				else {
					cluster_conference = confirmRow.conference
							.contains(conferenceId) ? 1 : 0;
					cluster_journal = confirmRow.journal.contains(journalId) ? 1
							: 0;

					/*
					 * if(cluster_journal==1) { if
					 * (matchStats.containsKey(journalId))
					 * matchStats.put(journalId, matchStats.get(journalId) + 1);
					 * else matchStats.put(journalId, 1);
					 * 
					 * }
					 */
					Set<String> matchWords = cleanKeywords(paperKeywords);
					// nothing.
					if (matchWords.size() == 0) {
						if (confirmRow.keywords.size() == 0)
							cluster_keyword = 300;
						else
							cluster_keyword = 100;
					} else {
						if (confirmRow.keywords.size() == 0)
							cluster_keyword = 200;
						else {
							matchWords.retainAll(confirmRow.keywords);
							cluster_keyword = 1000 + matchWords.size();
							if (matchWords.size() > 0) {
								for (String word : matchWords) {
									cluster_keyword2 += word + ",";
									/*
									 * if (matchWordsStats.containsKey(word))
									 * matchWordsStats.put(word,
									 * matchWordsStats.get(word) + 1); else
									 * matchWordsStats.put(word, 1);
									 */
								}
							}
						}
					}
					// coauthor
					authorName = authorNames.get(author);
					paperAuthorNames = allPapers.get(paper);

					int match = 0;
					int numCoauthor = 0;
					for (String coauthor : paperAuthorNames) {
						if (coauthor.length()<=3 || Match0.sameLastName(authorName, coauthor) )
							continue;
						numCoauthor++;

						for (String coauthor2 : confirmRow.coauthors) {
							if (Match0.matchNameFast(coauthor, coauthor2)>=0 ) {
								match++;
								cluster_coauthor2 += coauthor2 + ",";
								break;
							}
						}

					}
					if (numCoauthor == 0) {
						if (confirmRow.coauthors.size() == 0)
							cluster_coauthor = 300;
						else
							cluster_coauthor = 100;
					} else {
						if (confirmRow.coauthors.size() == 0)
							cluster_coauthor = 200;
						else
							cluster_coauthor = 1000 + match;
					}
				}

			} else {
				cluster_conference = cluster_journal = cluster_keyword = cluster_coauthor = -1;
			}
			rec.put("cluster_conference", cluster_conference);
			rec.put("cluster_journal", cluster_journal);
			rec.put("cluster_keyword", cluster_keyword);
			rec.put("cluster_keyword2", cluster_keyword2);
			rec.put("cluster_coauthor", cluster_coauthor);
			rec.put("cluster_coauthor2", cluster_coauthor2);
			dsOut.add(rec);
			if (++i % 10000 == 0)
				System.out.println("recs processed: " + i);
		}
		dsOut.save();

		/*
		 * System.out.println("matched words: " + matchStats.size());
		 * 
		 * 
		 * System.out.println(">1000");
		 * 
		 * for(Object word: matchStats.keySet()) { int freq =
		 * matchStats.get(word); if(freq > 1000) System.out.println(word + "," +
		 * freq); }
		 * 
		 * System.out.println(">100 words");
		 * 
		 * for(Object word: matchStats.keySet()) { int freq =
		 * matchStats.get(word); if(freq > 100) System.out.println(word + "," +
		 * freq); }
		 */
	}

	public static Set<String> cleanKeywords(String keyword) {
		Set words = new HashSet();
		if (keyword == null || keyword.length() < 2)
			return words;

		keyword = keyword.toLowerCase();
		keyword = keyword.replaceAll("\\|", " ");
		keyword = keyword.replaceAll("\"", " ");
		keyword = keyword.replaceAll(":", " ");
		keyword = keyword.replaceAll(",", " ");
		keyword = keyword.replaceAll(";", " ");
		keyword = keyword.replaceAll("keyword", " ");
		keyword = keyword.replaceAll(" +", " ");

		String[] tokens = keyword.split(" ");
		for (int i = 0; i < tokens.length; i++) {
			if (tokens[i].length() > 1) {
				if (tokens[i].equals("words"))
					continue;
				if (tokens[i].equals("key"))
					continue;
				if (tokens[i].equals("index"))
					continue;
				if (tokens[i].equals("and"))
					continue;
				if (tokens[i].equals("of"))
					continue;
				words.add(tokens[i]);
			}
		}

		return words;
	}
	
}


