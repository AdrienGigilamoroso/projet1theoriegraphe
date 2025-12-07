import java.util.*;

public class DijkstraSolver {

    public static class Result {
        public double[] dist;  // distance minimale depuis la source
        public int[] prev;     // prédécesseur dans le plus court chemin
    }

    private static class Node {
        int vertex;
        double dist;

        Node(int vertex, double dist) {
            this.vertex = vertex;
            this.dist = dist;
        }
    }


    public static Result dijkstra(Graph g, int source) {
        int n = g.getNbSommets();
        Result res = new Result();
        res.dist = new double[n];
        res.prev = new int[n];

        Arrays.fill(res.dist, Double.POSITIVE_INFINITY);
        Arrays.fill(res.prev, -1);
        res.dist[source] = 0.0;

        boolean[] visited = new boolean[n];

        PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparingDouble(node -> node.dist));
        pq.add(new Node(source, 0.0));

        while (!pq.isEmpty()) {
            Node node = pq.poll();
            int u = node.vertex;

            if (visited[u]) continue;
            visited[u] = true;

            for (Graph.Edge e : g.getAdj(u)) {
                int v;

                if (e.directed) {
                    // arête orientée : on ne peut la suivre que dans le sens from -> to
                    if (e.from != u) continue;
                    v = e.to;
                } else {
                    // arête non orientée : on va vers l'autre extrémité
                    v = (e.from == u) ? e.to : e.from;
                }

                double alt = res.dist[u] + e.weight;
                if (alt < res.dist[v]) {
                    res.dist[v] = alt;
                    res.prev[v] = u;
                    pq.add(new Node(v, alt));
                }
            }
        }

        return res;
    }


    public static List<Integer> buildPath(Result res, int source, int target) {
        List<Integer> path = new ArrayList<>();
        if (res.dist[target] == Double.POSITIVE_INFINITY) {
            return path; // pas de chemin
        }

        int cur = target;
        while (cur != -1) {
            path.add(cur);
            if (cur == source) break;
            cur = res.prev[cur];
        }
        Collections.reverse(path);
        return path;
    }
}
