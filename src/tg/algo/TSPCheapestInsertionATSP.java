
package tg.algo;

import java.util.*;


public class TSPCheapestInsertionATSP {

    public static List<String> tour(Set<String> nodes,
                                    String depot,
                                    Map<String, Map<String, Double>> dist){
        Set<String> unvisited = new HashSet<>(nodes);
        unvisited.remove(depot);

        // 1) point le plus proche du dépôt
        String first = null; double best = Double.POSITIVE_INFINITY;
        for(String v : unvisited){
            double d = dist.getOrDefault(depot, Collections.emptyMap()).getOrDefault(v, Double.POSITIVE_INFINITY);
            if(d < best){ best = d; first = v; }
        }
        if(first == null){
            return new ArrayList<>(List.of(depot, depot));
        }
        List<String> cycle = new ArrayList<>(List.of(depot, first, depot));
        unvisited.remove(first);

        // 2) insérer progressivement
        while(!unvisited.isEmpty()){
            String chosenV = null; int chosenPos = -1; double chosenDelta = Double.POSITIVE_INFINITY;

            for(String v : unvisited){
                // tester insertion entre i->j pour tout arc du cycle
                for(int i=0;i<cycle.size()-1;i++){
                    String a = cycle.get(i), b = cycle.get(i+1);
                    double a_v = dist.getOrDefault(a, Collections.emptyMap()).getOrDefault(v, Double.POSITIVE_INFINITY);
                    double v_b = dist.getOrDefault(v, Collections.emptyMap()).getOrDefault(b, Double.POSITIVE_INFINITY);
                    double a_b = dist.getOrDefault(a, Collections.emptyMap()).getOrDefault(b, Double.POSITIVE_INFINITY);
                    double delta = a_v + v_b - a_b;
                    if(delta < chosenDelta){
                        chosenDelta = delta;
                        chosenV = v;
                        chosenPos = i + 1; // insérer à cet index
                    }
                }
            }
            if(chosenV == null){
                // aucun insert valide (inaccessibilités) -> break
                break;
            }
            cycle.add(chosenPos, chosenV);
            unvisited.remove(chosenV);
        }
        return cycle;
    }
}
