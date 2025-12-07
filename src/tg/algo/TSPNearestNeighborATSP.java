
package tg.algo;

import java.util.*;


public class TSPNearestNeighborATSP {
    public static List<String> tour(Set<String> nodes,
                                    String depot,
                                    Map<String, Map<String, Double>> dist){
        Set<String> unvisited = new HashSet<>(nodes);
        unvisited.remove(depot);
        List<String> order = new ArrayList<>();
        String u = depot; order.add(u);

        while(!unvisited.isEmpty()){
            String best = null; double bestD = Double.POSITIVE_INFINITY;
            for(String v : unvisited){
                double d = dist.getOrDefault(u, Collections.emptyMap()).getOrDefault(v, Double.POSITIVE_INFINITY);
                if(d < bestD){ bestD = d; best = v; }
            }
            if(best == null) break; // inaccessible
            order.add(best);
            unvisited.remove(best);
            u = best;
        }
        // retour dépôt (dirigé)
        if(order.size() >= 1){
            double back = dist.getOrDefault(order.get(order.size()-1), Collections.emptyMap()).getOrDefault(depot, Double.POSITIVE_INFINITY);
            order.add(depot);
            if(Double.isInfinite(back)){
                // on laisse tout de même le dépôt pour cohérence, mais on sait qu'il n'y a pas de chemin
            }
        }
        return order;
    }
}
