import java.util.*;

public class EulerianSolver {

    /** Vérifie la connexité (en ignorant les sommets isolés). */
    private static boolean isConnected(Graph g) {
        int n = g.getNbSommets();
        boolean[] visited = new boolean[n];

        int start = -1;
        for (int i = 0; i < n; i++) {
            if (g.degree(i) > 0) {
                start = i;
                break;
            }
        }
        if (start == -1) return true; // pas d'arêtes

        Deque<Integer> stack = new ArrayDeque<>();
        stack.push(start);
        visited[start] = true;

        while (!stack.isEmpty()) {
            int u = stack.pop();
            for (Graph.Edge e : g.getAdj(u)) {
                int v = (e.from == u) ? e.to : e.from;
                if (!visited[v]) {
                    visited[v] = true;
                    stack.push(v);
                }
            }
        }

        for (int i = 0; i < n; i++) {
            if (g.degree(i) > 0 && !visited[i]) return false;
        }
        return true;
    }

    /**
     * Chemin / circuit eulérien (Hierholzer) pour graphe non orienté.
     */
    public static List<Integer> findEulerianTrail(Graph g) {
        if (!g.isPurelyUndirected()) {
            throw new IllegalArgumentException("EulerianSolver : toutes les arêtes doivent être non orientées.");
        }
        if (!isConnected(g)) {
            throw new IllegalArgumentException("Le graphe n'est pas connexe : pas de chemin eulérien.");
        }

        int n = g.getNbSommets();
        int oddCount = 0;
        int start = 0;

        for (int i = 0; i < n; i++) {
            if (g.degree(i) % 2 != 0) {
                oddCount++;
                start = i;
            }
        }

        if (oddCount != 0 && oddCount != 2) {
            throw new IllegalStateException("Il faut 0 ou 2 sommets impairs (ici : " + oddCount + ").");
        }

        if (oddCount == 0) {
            for (int i = 0; i < n; i++) {
                if (g.degree(i) > 0) {
                    start = i;
                    break;
                }
            }
        }

        g.resetUsedFlags();

        Stack<Integer> stack = new Stack<>();
        List<Integer> circuit = new ArrayList<>();
        stack.push(start);

        while (!stack.isEmpty()) {
            int v = stack.peek();
            Graph.Edge edgeToUse = null;

            for (Graph.Edge e : g.getAdj(v)) {
                if (!e.used) {
                    edgeToUse = e;
                    break;
                }
            }

            if (edgeToUse != null) {
                edgeToUse.used = true;
                int next = (edgeToUse.from == v) ? edgeToUse.to : edgeToUse.from;
                stack.push(next);
            } else {
                circuit.add(stack.pop());
            }
        }

        Collections.reverse(circuit);
        return circuit;
    }
}
