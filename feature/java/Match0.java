package xing.kdd2013;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Match0 {

	public static boolean sameLastName(String name1, String name2) {

		String[] name1s = name1.split(" ");
		String[] name2s = name2.split(" ");

		return name1s[name1s.length - 1]
				.equalsIgnoreCase(name2s[name2s.length - 1]);
	}

	
	public static String cleanName(String name1) {
		name1 = name1.replaceAll(" +", " ");
		name1 = name1.replaceAll("\\.", "");
		name1 = name1.toLowerCase();
		name1 = name1.trim();
		return name1;
	}

	public static String cleanName2(String name1) {

		name1 = name1.replaceAll("\\.", " ");
		name1 = name1.toLowerCase();
		name1 = name1.replaceAll(" jr ", " ");
		// remove (*)
		int startIndex = name1.indexOf("(");
		int endIndex = name1.indexOf(")");
		if (startIndex >= 0 && startIndex < endIndex) {
			String remove = name1.substring(startIndex, endIndex + 1);
			name1 = name1.replace(remove, "");
		}
		name1 = name1.replaceAll(" +", " ");
		name1 = name1.trim();
		return name1;
	}
	
	public static String cleanTitle(String name1) {

		name1 = name1.replaceAll("\\.", " ");
		name1 = name1.replaceAll("\"", "");
		name1 = name1.toLowerCase();
		// remove (*)
		int startIndex = name1.indexOf("(");
		int endIndex = name1.indexOf(")");
		if (startIndex >= 0 && startIndex < endIndex) {
			String remove = name1.substring(startIndex, endIndex + 1);
			name1 = name1.replace(remove, "");
		}
		name1 = name1.replaceAll(" +", " ");
		name1 = name1.trim();
		return name1;
	}


	public static int matchNameFast(String name1, String name2) {

		// assuming name is cleaned, trimed, same case. length >3,
		int matched = 0;

		String[] name1s = name1.split(" ");
		String[] name2s = name2.split(" ");
	
		if (!name1s[name1s.length - 1].equals(name2s[name2s.length - 1])) // different
																			// last
		{
				return -1;

		}
		if (name1s.length != name2s.length) { // check first only

			if (name1s[0].length() > 1 && name2s[0].length() > 1) {
				if (name1s[0].equals(name2s[0]))
					return 2; // same first full name
				else
					return -2; // different full first names.
			}
			if (name1s[0].substring(0, 1).equals(name2s[0].substring(0, 1)))
				return 3; // same first init.
			else
				return -3; // different first init.
		} else { // check all.
			boolean sameInit = false;

			boolean differentLength = false;
			for (int i = 0; i < name1s.length - 1; i++) { // different init or
															// full names.
				if (name1s[i].length() != name2s[i].length())
					differentLength = true;
				if (name1s[i].length() > 1 && name2s[i].length() > 1
						&& !name1s[i].equals(name2s[i]))
					return -4;
				if (!name1s[i].substring(0, 1)
						.equals(name2s[i].substring(0, 1)))
					return -5;
			}
			return differentLength ? 1 : 0;
		}

	}

	public static int matchNameFast2(String name1, String name2) {

		// assuming name is cleaned, trimed, same case. length >3,
		int matched = 0;

		String[] name1s = name1.split(" ");
		String[] name2s = name2.split(" ");

		for (int i = 0; i < name1s.length; i++) {
			if (name1s[i].indexOf('-') > 0) { // remove, keep?

				String remove = name1s[i].replace("-", "");

				for (String iter : name2s) {

					if (iter.equals(remove)) {
						name1s[i] = remove;
						break;
					} else if (iter.equals(name1s[i]))
						break;
				}
			}
		}

		// cleanSpecialChar(name1s, name2s); // deal with special char, minor
		// impact.

		if (!name1s[name1s.length - 1].equals(name2s[name2s.length - 1])) // different
																			// last
		{

			if (name1s.length == 2 && name2s.length == 2
					&& name1s[0].equals(name2s[1])
					&& name2s[0].equals(name1s[1])) // first last name switched.
				return 4;

			Set<String> longWords1 = new HashSet();
			Set<String> longWords2 = new HashSet();

			for (String name : name1s) {
				if (name.length() >= 3)
					longWords1.add(name);
			}
			for (String name : name2s) {
				if (name.length() >= 3)
					longWords2.add(name);
			}

			longWords1.retainAll(longWords2);

			if (longWords1.size() >= 2)
				return 5; // two long names matched.

			return -1;

		}
		if (name1s.length != name2s.length) { // check first only

			if (name1s[0].length() > 1 && name2s[0].length() > 1) {
				if (name1s[0].equals(name2s[0]))
					return 2; // same first full name
				else
					return -2; // different full first names.
			}
			if (name1s[0].substring(0, 1).equals(name2s[0].substring(0, 1)))
				return 3; // same first init.
			else
				return -3; // different first init.
		} else { // check all.
			boolean sameInit = false;

			boolean differentLength = false;
			for (int i = 0; i < name1s.length - 1; i++) { // different init or
															// full names.
				if (name1s[i].length() != name2s[i].length())
					differentLength = true;
				if (name1s[i].length() > 1 && name2s[i].length() > 1
						&& !name1s[i].equals(name2s[i]))
					return -4;
				if (!name1s[i].substring(0, 1)
						.equals(name2s[i].substring(0, 1)))
					return -5;
			}
			return differentLength ? 1 : 0;
		}

	}

	

	
	public static int matchNameFastSorted(String name1, String name2) {
		int result = matchNameFast2(name1, name2);
		int[] seq = new int[] { 5, -2, -4, -3, -5, 0, 1, 2, 3, 4 };
		Map<Integer, Integer> reorder = new HashMap();
		for (int i = 0; i < seq.length; i++) {
			reorder.put(seq[i], i);
		}

		if (result != -1)
			result = reorder.get(result);

		return result;
	}

	public static int matchNameFastSorted2(String name1, String name2) {
		int result = matchNameFast2(name1, name2);
		int[] seq = new int[] { 5, -4, -2, -3, 0, -5, 1, 2, 3, 4 };
		Map<Integer, Integer> reorder = new HashMap();
		for (int i = 0; i < seq.length; i++) {
			reorder.put(seq[i], i);
		}

		if (result != -1)
			result = reorder.get(result);

		return result;
	}

	
}
