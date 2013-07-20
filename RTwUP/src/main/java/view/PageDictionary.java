package view;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

import view.DomainPageCouple;

/**
 * This class works as a collector of all the URLs.
 * It returns the stringified version of the TopNelements in the map
 * 
 * @author Daniele Morgantini
 * 
 */
public class PageDictionary {
	
	private static PageDictionary instance;
	private Map<DomainPageCouple,Integer> dictionary;
	
	private PageDictionary() {
		this.dictionary = new ConcurrentHashMap<DomainPageCouple,Integer>();
	}
	
	public static synchronized PageDictionary getInstance(){
		if (instance==null)
			instance = new PageDictionary(); 
		return instance;
	}
	
	/**
	 * Adds a linked page to the dictionary
	 * 
	 * @param domain and page 
	 * @return the updated counter
	 * 
	 */
	public int addToDictionary(String domain, String page) {
		DomainPageCouple dp = new DomainPageCouple(domain,page);
		Integer count = this.dictionary.get(dp);
		if(count == null) {
			count = 1;
			this.dictionary.put(dp, count);
		}
		else {
			count++;
			this.dictionary.put(dp,count);
		}
		return count;
	}
	
	
	/**
	 * Returns the stringified version of topNelements in the dictionary
	 * 	 * 
	 */
	public String getTopNelementsStringified(int topN) {
		/* Ordering all the pages by counter */
		DictionaryValueComparator bvc =  new DictionaryValueComparator(dictionary);
		TreeMap<DomainPageCouple,Integer> sorted_map = new TreeMap<DomainPageCouple,Integer>(bvc);
        sorted_map.putAll(dictionary);
        
        /* Retrieving the topN pages and split them between appropriate domains */
        int i = 0;
        ConcurrentSkipListMap<String, String> domains = new ConcurrentSkipListMap<String, String> ();
        for(Map.Entry<DomainPageCouple, Integer> dp : sorted_map.entrySet()) {
        	if(i<topN) {
        		String domain = dp.getKey().getDomain();
        		String page = dp.getKey().getPage();
        		String pages = null;
        		
        		pages = domains.get(domain);
        		if (pages == null)
        			pages = "";
        		pages += page + " - " + dp.getValue() + "times \t";
        		domains.put(domain, pages);
        	}	
        }
        
        /* Formatting the string to be returned */
        String toReturn = "";
        for(Map.Entry<String, String> domain : domains.entrySet())
        	toReturn += domain.getKey() + ":\t" + domain.getValue() + "\n";
        
        return toReturn;
	}

}

class DictionaryValueComparator implements Comparator<DomainPageCouple> {

    Map<DomainPageCouple, Integer> base;
    public DictionaryValueComparator(Map<DomainPageCouple, Integer> base) {
        this.base = base;
    }

    public int compare(DomainPageCouple a, DomainPageCouple b) {
        if (base.get(a) >= base.get(b)) {
            return -1;
        } else {
            return 1;
        }
    }
}