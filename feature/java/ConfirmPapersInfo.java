package xing.kdd2013;

import java.util.List;
import java.util.Set;

public class ConfirmPapersInfo {
	
		Set<Integer> conference;
		Set<Integer> journal;
		Set<String> keywords;
		Set<String> titleWords;
		Set<String> coauthors;
		int   minYear=-1, maxYear=-1, sizeYear = 0;
		double meanYear, varYear;

		public ConfirmPapersInfo(Set conference, Set journal, Set keywords,
				Set titleWords, Set coauthors) {
			this.conference = conference;
			this.journal = journal;
			this.keywords = keywords;
			this.titleWords = titleWords;
			this.coauthors = coauthors;
		}
		
		public ConfirmPapersInfo() {
		}
		
		public void setYears(List<Integer> years) {
			if(years.size()==0) return;
            minYear = 2014;
            maxYear = 1900;
            meanYear = 0;
            varYear = 0;
            sizeYear = years.size();
            int year;
           
			for(int i=0; i<years.size(); i++)
			{
				year = years.get(i);
				if(year<minYear)
					minYear = year;
				if(year > maxYear)
					maxYear = year;
				meanYear += year;
			}
			meanYear /= years.size();
			
			for(int i=0; i<years.size(); i++)
			{
				year = years.get(i);
				varYear += (year - meanYear)*(year - meanYear);
			}
			
			varYear /= years.size();
			varYear = Math.sqrt(varYear); 
		}

	}

