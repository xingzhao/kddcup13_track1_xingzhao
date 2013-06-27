package xing.kdd2013;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MatchAffiliation2 {

	

	// match affiliations approximately

	public static Set fuzzyMatch(String a, String b) {
		a = a.replaceAll("\\|", ",");
		b = b.replaceAll("\\|", ",");
		a = a.replaceAll(";", ",");
		b = b.replaceAll(";", ",");
		a = a.replaceAll(" +", " "); // extra space.
		b = b.replaceAll(" +", " "); // extra space.

		String[] aGroups = a.split(",");
		String[] bGroups = b.split(",");

		Set<String> aSet = new HashSet();
		Set bSet = new HashSet();

		for (String group : aGroups) {
			String cleaned = cleanAffiliation(group);
			if (cleaned.length() >= 3)
				aSet.add(cleaned);
		}
		for (String group : bGroups) {
			String cleaned = cleanAffiliation(group);
			if (cleaned.length() >= 3)
				bSet.add(cleaned);
		}

		aSet.retainAll(bSet);
		return aSet; // fuzzy matched. risky sometimes.

	}


	private static String cleanAffiliation(String a) {
		a = a.trim();
		a = a.toLowerCase();
		String b = "";

		for (String token : a.split(" ")) {
			if (token.length() <= 2)
				continue;
			if (token.equals("and")|| token.equals("the"))
				continue;
			if (token.indexOf("univ") == 0) {
			   token = "univ";
			}
			else if (token.indexOf("depart") == 0 || token.indexOf("dept") == 0)
				token = "dept";
			else if (token.indexOf("inst") == 0)
				token = "inst";
			else if (token.indexOf("tech") == 0)
				token = "tech";
			b += token + " ";
		}
		b = b.trim();
		return b;
	}



}
