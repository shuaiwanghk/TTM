package utility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SortingUtility {
	
	public static LinkedHashMap sortHashMapByValuesD(HashMap passedMap) {
		   List mapKeys = new ArrayList(passedMap.keySet());
		   List mapValues = new ArrayList(passedMap.values());
		   Collections.sort(mapValues, Collections.reverseOrder());
		   Collections.sort(mapKeys);

		   LinkedHashMap sortedMap = new LinkedHashMap();

		   Iterator valueIt = mapValues.iterator();
		   while (valueIt.hasNext()) {
		       Object val = valueIt.next();
		       Iterator keyIt = mapKeys.iterator();

		       while (keyIt.hasNext()) {
		           Object key = keyIt.next();
		           String comp1 = passedMap.get(key).toString();
		           String comp2 = val.toString();

		           if (comp1.equals(comp2)){
		               passedMap.remove(key);
		               mapKeys.remove(key);
//		               sortedMap.put((String)key, (Double)val);
		               sortedMap.put(String.valueOf(key), (Double)val);
		               break;
		           }

		       }

		   }
		   return sortedMap;
		}
	
	public static void main(String[] args) {
		HashMap< Integer, Double> hm = new HashMap<Integer, Double>();
//		hm.put("A", 12.3);
//		hm.put("B", 14.3);
//		hm.put("C", 13.3);
		hm.put(1, 12.3);
		hm.put(2, 14.3);
		hm.put(3, 13.3);
		
		Map sortedMap = sortHashMapByValuesD(hm);
		
		 Iterator it = sortedMap.entrySet().iterator();
		 while (it.hasNext()) {
	        Map.Entry pair = (Map.Entry)it.next();
//	        System.out.println(pair.getKey() instanceof String);
	        System.out.println(pair.getKey() + " = " + pair.getValue());
	        it.remove(); // avoids a ConcurrentModificationException
	}
	}

}
