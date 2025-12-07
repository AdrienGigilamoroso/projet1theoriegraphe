
package tg.algo;

import tg.model.Graph;
import java.util.*;


public class Dijkstra {
    public static class Result {
        public final Map<String, Double> dist;
        public final Map<String, String> parent;
        public Result(Map<String, Double> d, Map<String, String> p){ this.dist=d; this.parent=p; }
    }
    public static Result shortest(Graph g, String s){
        Map<String, Double> dist = new HashMap<>();
        Map<String, String> parent = new HashMap<>();
        for(String u: g.nodes()){ dist.put(u, Double.POSITIVE_INFINITY); parent.put(u, null); }
        dist.put(s, 0.0);

        PriorityQueue<String> pq = new PriorityQueue<>(Comparator.comparingDouble(dist::get));
        pq.add(s);

        while(!pq.isEmpty()){
            String u = pq.poll();
            double du = dist.get(u);
            for(var e: g.neighbors(u).entrySet()){
                String v = e.getKey(); double w = e.getValue();
                if(w < 0) throw new IllegalArgumentException("Poids négatif non supporté.");
                if(dist.get(v) > du + w){
                    dist.put(v, du + w);
                    parent.put(v, u);
                    pq.remove(v); pq.add(v);
                }
            }
        }
        return new Result(dist, parent);
    }
}
