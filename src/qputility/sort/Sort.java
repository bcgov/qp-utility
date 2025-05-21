package qputility.sort;

import java.util.Collections;
import java.util.List;

/**
 * @author chris.ditcher
 * Utility class for various sorting algorithms for maps
 * and sets, etc.
 */
public class Sort {
	
	/**
	 * This method takes an implementation of a list (ArrayList, etc)
	 * and attempts to sort naturally. IE Alphabetically, not ASCIIbetically.
	 * 
	 * Example: 
	 * {@code}
	 * naturalSort(myList);
	 * 
	 * For more information see the notes in AlphanumComparator class or
	 * go to: http://www.davekoelle.com/alphanum.html
	 * 
	 * @param list
	 * @return a naturally sorted list
	 */
	@SuppressWarnings("unchecked")
	public static List<?> naturalSort(List<?> list){
		Collections.sort(list, new AlphanumComparator());
		return list;	
	}

}
