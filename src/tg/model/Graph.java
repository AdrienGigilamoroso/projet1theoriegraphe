
package tg.model;

import java.util.*;


public class Graph {
    private final boolean directed;
    private final Map<String, Map<String, Double>> adj = new HashMap<>();

    public Graph(boolean directed){ this.directed = directed; }

    public boolean isDirected(){ return directed; }


    public void addEdge(String u, String v, double w){
        adj.computeIfAbsent(u, k->new HashMap<>()).put(v, w);
        adj.computeIfAbsent(v, k->new HashMap<>());
        if(!directed){
            adj.get(v).put(u, w);
        }
    }


    public void addUndirectedEdge(String u, String v, double w){
        adj.computeIfAbsent(u, k->new HashMap<>()).put(v, w);
        adj.computeIfAbsent(v, k->new HashMap<>()).put(u, w);
    }


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
