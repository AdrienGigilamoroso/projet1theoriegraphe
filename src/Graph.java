import java.util.ArrayList;
import java.util.List;

public class Graph {

    /**
     * Représente une arête entre deux sommets.
     * - from, to : sommets extrémités
     * - weight   : poids de l'arête
     * - directed : true  -> arête orientée  (from -> to)
     *             false -> arête non orientée (from -- to)
     */
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

    /**
     * Constructeur général.
     * @param nbSommets nombre de sommets (0..nbSommets-1)
     * @param oriented  orientation par défaut des arêtes ajoutées avec addEdge(...)
     */
    public Graph(int nbSommets, boolean oriented) {
        this.nbSommets = nbSommets;
        this.defaultOriented = oriented;
        this.adj = new ArrayList<>();
        this.edges = new ArrayList<>();
        for (int i = 0; i < nbSommets; i++) {
            adj.add(new ArrayList<>());
        }
    }

    /**
     * Constructeur simplifié : graphe non orienté par défaut.
     * Utilisé pour Euler / Postier chinois.
     */
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

    // =========================
    // Ajout d'arêtes
    // =========================

    /**
     * Ajoute une arête en utilisant l'orientation par défaut du graphe.
     * Si defaultOriented = false -> arête non orientée.
     * Si defaultOriented = true  -> arête orientée.
     */
    public void addEdge(int from, int to, double weight) {
        addEdgeInternal(from, to, weight, defaultOriented);
    }

    /** Ajoute une arête non pondérée (poids = 1). */
    public void addEdge(int from, int to) {
        addEdge(from, to, 1.0);
    }

    /** Ajoute explicitement une arête non orientée. */
    public void addUndirectedEdge(int from, int to, double weight) {
        addEdgeInternal(from, to, weight, false);
    }

    /** Ajoute explicitement une arête orientée. */
    public void addDirectedEdge(int from, int to, double weight) {
        addEdgeInternal(from, to, weight, true);
    }

    /** Méthode interne commune. */
    private void addEdgeInternal(int from, int to, double weight, boolean directed) {
        Edge e = new Edge(from, to, weight, directed);
        edges.add(e);
        adj.get(from).add(e);
        if (!directed) {
            // graphe non orienté : on met la même arête dans la liste de l'autre sommet
            adj.get(to).add(e);
        }
    }

    /**
     * Renvoie le poids d'une arête (from, to).
     * On suppose qu'il existe au moins une arête entre from et to.
     */
    public double getEdgeWeight(int from, int to) {
        for (Edge e : adj.get(from)) {
            int neighbor = (e.from == from) ? e.to : e.from;
            if (neighbor == to) {
                return e.weight;
            }
        }
        throw new IllegalArgumentException("Pas d'arête entre " + from + " et " + to);
    }

    /** Remet à false tous les flags 'used' des arêtes. */
    public void resetUsedFlags() {
        for (Edge e : edges) {
            e.used = false;
        }
    }

    /** Copie simple du graphe (structure identique). */
    public Graph copy() {
        Graph g2 = new Graph(nbSommets, defaultOriented);
        for (Edge e : edges) {
            g2.addEdgeInternal(e.from, e.to, e.weight, e.directed);
        }
        return g2;
    }

    /** Degré d'un sommet (nombre d'arêtes incidentes).
     *  Attention : valable uniquement si toutes les arêtes sont non orientées.
     */
    public int degree(int v) {
        return adj.get(v).size();
    }

    /** Vrai si toutes les arêtes sont non orientées. */
    public boolean isPurelyUndirected() {
        for (Edge e : edges) {
            if (e.directed) return false;
        }
        return true;
    }
}
