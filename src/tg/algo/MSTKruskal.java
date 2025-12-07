
package tg.algo;

import tg.model.Edge;
import tg.model.Graph;
import java.util.*;

/**
 * Kruskal pour un graphe NON orienté.
 * Complexité : O(m log m) pour le tri des arêtes.
 */
public class MSTKruskal {

    static class DSU {
        Map<String,String> p = new HashMap<>();
        Map<String,Integer> r = new HashMap<>();
        String find(String x){
            p.putIfAbsent(x, x);
            r.putIfAbsent(x, 0);
            if(!p.get(x).equals(x)) p.put(x, find(p.get(x)));
            return p.get(x);
        }
        boolean union(String a, String b){
            a = find(a); b = find(b);
            if(a.equals(b)) return false;
            int ra = r.get(a), rb = r.get(b);
            if(ra < rb) p.put(a, b);
            else if(ra > rb) p.put(b, a);
            else { p.put(b, a); r.put(a, ra+1); }
            return true;
        }
    }

    /** Construit un MST et renvoie les arêtes sélectionnées. */
    public static List<Edge> mst(Graph g){
        List<Edge> E = new ArrayList<>(g.edges());
        E.sort(Comparator.comparingDouble(e -> e.w));
        DSU dsu = new DSU();
        List<Edge> T = new ArrayList<>();
        for(Edge e : E){
            if(dsu.union(e.u, e.v)) T.add(e);
        }
        return T;
    }
}
