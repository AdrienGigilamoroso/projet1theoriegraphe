
package tg.algo;

import tg.model.Graph;
import java.util.*;

public class TSPNearestNeighbor {
    public static List<String> tour(Graph complete, String depot){
        Set<String> unvisited = new HashSet<>(complete.nodes());
        unvisited.remove(depot);
        List<String> order = new ArrayList<>();
        String u = depot; order.add(u);
        while(!unvisited.isEmpty()){
            String best = null; double bestD = Double.POSITIVE_INFINITY;
            for(String v: unvisited){
                double d = complete.weight(u, v);
                if(d < bestD){ bestD = d; best = v; }
            }
            if(best == null) break;
            order.add(best);
            unvisited.remove(best);
            u = best;
        }
        order.add(depot);
        return order;
    }
}
