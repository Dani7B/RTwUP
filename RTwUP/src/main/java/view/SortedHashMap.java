package view;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

/**
 * This class returns the string containing the all the tuples in the map.
 * 
 * @author Daniele Morgantini
 * 
 */
public class SortedHashMap {

	private TreeMap<String,Integer> sorted_map;
	
	public SortedHashMap(Map<String,Integer> map) {
        ValueComparator bvc =  new ValueComparator(map);
        this.sorted_map = new TreeMap<String,Integer>(bvc);
        sorted_map.putAll(map);
	}
	
	public String toString() {
		return this.sorted_map.toString();
	}

}

class ValueComparator implements Comparator<String> {

    Map<String, Integer> base;
    public ValueComparator(Map<String, Integer> base) {
        this.base = base;
    }

    public int compare(String a, String b) {
        if (base.get(a) >= base.get(b)) {
            return -1;
        } else {
            return 1;
        }
    }
}