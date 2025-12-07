import java.util.*;

public class ChinesePostmanSolver {

    private static List<Integer> getOddVertices(Graph g) {
        List<Integer> odds = new ArrayList<>();
        for (int i = 0; i < g.getNbSommets(); i++) {
            if (g.degree(i) % 2 != 0) {
                odds.add(i);
            }
        }
        return odds;
    }

    /**
     * Problème du postier chinois sur graphe non orienté.
     * Version simple avec appariement glouton des sommets impairs.
     */
    public static List<Integer> solve(Graph original) {
        if (!original.isPurelyUndirected()) {
            throw new IllegalArgumentException("ChinesePostmanSolver : toutes les arêtes doivent être non orientées.");
        }

        Graph g = original.copy();

        List<Integer> oddVertices = getOddVertices(g);

        while (!oddVertices.isEmpty()) {
            int u = oddVertices.get(0);

            DijkstraSolver.Result res = DijkstraSolver.dijkstra(g, u);

            int bestV = -1;
            double bestDist = Double.POSITIVE_INFINITY;

            for (int i = 1; i < oddVertices.size(); i++) {
                int v = oddVertices.get(i);
                if (res.dist[v] < bestDist) {
                    bestDist = res.dist[v];
                    bestV = v;
                }
            }

            if (bestV == -1 || bestDist == Double.POSITIVE_INFINITY) {
                throw new IllegalStateException("Impossible de relier deux sommets impairs.");
            }

            List<Integer> path = DijkstraSolver.buildPath(res, u, bestV);

            for (int i = 0; i < path.size() - 1; i++) {
                int a = path.get(i);
                int b = path.get(i + 1);
                double w = g.getEdgeWeight(a, b);
                g.addUndirectedEdge(a, b, w); // duplication d'arête
            }

            oddVertices.remove(Integer.valueOf(u));
            oddVertices.remove(Integer.valueOf(bestV));
        }

        return EulerianSolver.findEulerianTrail(g);
    }
}
