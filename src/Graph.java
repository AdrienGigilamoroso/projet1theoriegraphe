import java.util.ArrayList;
import java.util.List;

public class Graph {

    public static class Edge {
        public int from;
        public int to;
        public double weight;
        public boolean directed;
        public boolean used = false; // pour les algos eulériens

        public Edge(int from, int to, double weight, boolean directed) {
            this.from = from;
            this.to = to;
            this.weight = weight;
            this.directed = directed;
        }
    }

    private int nbSommets;
    private boolean defaultOriented;      // utilisé par addEdge(...)
    private List<List<Edge>> adj;
    private List<Edge> edges;


    public Graph(int nbSommets, boolean oriented) {
        this.nbSommets = nbSommets;
        this.defaultOriented = oriented;
        this.adj = new ArrayList<>();
        this.edges = new ArrayList<>();
        for (int i = 0; i < nbSommets; i++) {
            adj.add(new ArrayList<>());
        }
    }


    public Graph(int nbSommets) {
        this(nbSommets, false);
    }

    public int getNbSommets() {
        return nbSommets;
    }

    public List<Edge> getAdj(int v) {
        return adj.get(v);
    }

    public List<Edge> getEdges() {
        return edges;
    }




    public void addEdge(int from, int to, double weight) {
        addEdgeInternal(from, to, weight, defaultOriented);
    }


    public void addEdge(int from, int to) {
        addEdge(from, to, 1.0);
    }


    public void addUndirectedEdge(int from, int to, double weight) {
        addEdgeInternal(from, to, weight, false);
    }


    public void addDirectedEdge(int from, int to, double weight) {
        addEdgeInternal(from, to, weight, true);
    }


    private void addEdgeInternal(int from, int to, double weight, boolean directed) {
        Edge e = new Edge(from, to, weight, directed);
        edges.add(e);
        adj.get(from).add(e);
        if (!directed) {

            adj.get(to).add(e);
        }
    }


    public double getEdgeWeight(int from, int to) {
        for (Edge e : adj.get(from)) {
            int neighbor = (e.from == from) ? e.to : e.from;
            if (neighbor == to) {
                return e.weight;
            }
        }
        throw new IllegalArgumentException("Pas d'arête entre " + from + " et " + to);
    }


    public void resetUsedFlags() {
        for (Edge e : edges) {
            e.used = false;
        }
    }


    public Graph copy() {
        Graph g2 = new Graph(nbSommets, defaultOriented);
        for (Edge e : edges) {
            g2.addEdgeInternal(e.from, e.to, e.weight, e.directed);
        }
        return g2;
    }


    public int degree(int v) {
        return adj.get(v).size();
    }


    public boolean isPurelyUndirected() {
        for (Edge e : edges) {
            if (e.directed) return false;
        }
        return true;
    }
}
