
package tg.planning;

import java.util.*;

/**
 * Découpe une tournée D->...->D en sous-tournées ≤ C (quantités par point).
 * Chaque sous-tournée commence et finit à D.
 */
public class CapacitySplitter {
    public static List<List<String>> splitSequential(List<String> tourWithDepot,
                                                     Map<String,Integer> qty,
                                                     int C){
        List<List<String>> result = new ArrayList<>();
        if(tourWithDepot == null || tourWithDepot.size() < 2) return result;
        String depot = tourWithDepot.get(0);
        int i = 1;
        while(i < tourWithDepot.size()-1){
            int load = 0;
            List<String> sub = new ArrayList<>();
            sub.add(depot);
            while(i < tourWithDepot.size()-1){
                String p = tourWithDepot.get(i);
                int q = qty.getOrDefault(p, 0);
                if(load + q > C) break;
                sub.add(p); load += q; i++;
            }
            sub.add(depot);
            if(sub.size() == 2 && i < tourWithDepot.size()-1){
                // cas rare: un seul point dépasse C -> le prendre seul
                sub.add(tourWithDepot.get(i));
                sub.add(depot); i++;
            }
            result.add(sub);
        }
        return result;
    }
}
