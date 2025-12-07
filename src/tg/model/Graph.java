
package tg.model;

import java.util.*;

/**
 * Graphe pondéré avec option d'orientation.
 * directed=false -> non orienté (addEdge ajoute u->v et v->u).
 * directed=true  -> orienté (addEdge n'ajoute que u->v).
 *
 * Pour HO3 (mixte), on prendra directed=true et on ajoutera:
 *  - arêtes orientées (u->v),
 *  - arêtes "U" (double sens) via addUndirectedEdge(u,v,w).
 */
public class Graph {
    private final boolean directed;
    private final Map<String, Map<String, Double>> adj = new HashMap<>();

    public Graph(boolean directed){ this.directed = directed; }

    public boolean isDirected(){ return directed; }

    /** Arête "générique": si non orienté -> u<->v ; si orienté -> u->v uniquement. */
    public void addEdge(String u, String v, double w){
        adj.computeIfAbsent(u, k->new HashMap<>()).put(v, w);
        adj.computeIfAbsent(v, k->new HashMap<>());
        if(!directed){
            adj.get(v).put(u, w);
        }
    }

    /** Force l'ajout comme NON orienté (utile pour HO3) même si directed=true au niveau du graphe. */
    public void addUndirectedEdge(String u, String v, double w){
        adj.computeIfAbsent(u, k->new HashMap<>()).put(v, w);
        adj.computeIfAbsent(v, k->new HashMap<>()).put(u, w);
    }

    /** Force l'ajout orienté (u->v) (utile pour HO3). */
    public void addDirectedEdge(String u, String v, double w){
        adj.computeIfAbsent(u, k->new HashMap<>()).put(v, w);
        adj.computeIfAbsent(v, k->new HashMap<>());
    }

    public Set<String> nodes(){ return Collections.unmodifiableSet(adj.keySet()); }

    public Map<String, Double> neighbors(String u){ return adj.getOrDefault(u, Collections.emptyMap()); }

    public double weight(String u, String v){
        return adj.getOrDefault(u, Collections.emptyMap()).getOrDefault(v, Double.POSITIVE_INFINITY);
    }

    public List<Edge> edges(){
        List<Edge> E = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        for(String u : adj.keySet()){
            for(var e : adj.get(u).entrySet()){
                String v = e.getKey(); double w = e.getValue();
                if(directed){
                    E.add(new Edge(u,v,w));
                } else {
                    String key = u+"#"+v, rev = v+"#"+u;
                    if(!seen.contains(key) && !seen.contains(rev)){
                        E.add(new Edge(u,v,w));
                        seen.add(key);
                    }
                }
            }
        }
        return E;
    }
}
