import java.util.List;

public class MainTheme1 {

    public static void main(String[] args) {

        // ============================================
        // PARTIE A : EULER & POSTIER CHINOIS
        // Graphe NON orienté, NON pondéré
        // ============================================
        System.out.println("=== PARTIE A : graphe non orienté, non pondéré ===");

        Graph gEuler = new Graph(5); // sommets 0..4

        // 0-1-2-3-0 et 2-4
        gEuler.addEdge(0, 1);
        gEuler.addEdge(1, 2);
        gEuler.addEdge(2, 3);
        gEuler.addEdge(3, 0);
        gEuler.addEdge(2, 4);

        try {
            List<Integer> eulerTrail = EulerianSolver.findEulerianTrail(gEuler);
            System.out.println("Chemin/circuit eulérien :");
            for (int v : eulerTrail) {
                System.out.print(v + " ");
            }
            System.out.println();
        } catch (Exception e) {
            System.out.println("Pas de chemin eulérien : " + e.getMessage());
        }

        System.out.println("\nTournée du postier chinois :");
        List<Integer> tourPostier = ChinesePostmanSolver.solve(gEuler);
        for (int v : tourPostier) {
            System.out.print(v + " ");
        }
        System.out.println("\n");

        // ============================================
        // PARTIE B1 : DIJKSTRA - GRAPHE ORIENTÉ PONDÉRÉ
        // ============================================
        System.out.println("=== PARTIE B1 : Dijkstra sur graphe orienté pondéré ===");

        Graph gOriente = new Graph(5, true); // arêtes orientées par défaut

        gOriente.addEdge(0, 1, 10);
        gOriente.addEdge(0, 2, 3);
        gOriente.addEdge(2, 1, 1);
        gOriente.addEdge(1, 3, 2);
        gOriente.addEdge(2, 3, 8);
        gOriente.addEdge(3, 4, 7);

        int source = 0;
        int target = 4;

        DijkstraSolver.Result res1 = DijkstraSolver.dijkstra(gOriente, source);
        List<Integer> path1 = DijkstraSolver.buildPath(res1, source, target);

        System.out.println("Distance minimale " + source + " -> " + target + " : " + res1.dist[target]);
        System.out.print("Chemin : ");
        for (int v : path1) {
            System.out.print(v + " ");
        }
        System.out.println("\n");

        // ============================================
        // PARTIE B2 : DIJKSTRA - GRAPHE NON ORIENTÉ PONDÉRÉ
        // ============================================
        System.out.println("=== PARTIE B2 : Dijkstra sur graphe non orienté pondéré ===");

        Graph gNonOriente = new Graph(5, false); // arêtes non orientées par défaut

        gNonOriente.addEdge(0, 1, 5);
        gNonOriente.addEdge(0, 2, 2);
        gNonOriente.addEdge(1, 3, 4);
        gNonOriente.addEdge(2, 3, 1);
        gNonOriente.addEdge(3, 4, 3);

        DijkstraSolver.Result res2 = DijkstraSolver.dijkstra(gNonOriente, source);
        List<Integer> path2 = DijkstraSolver.buildPath(res2, source, target);

        System.out.println("Distance minimale " + source + " -> " + target + " : " + res2.dist[target]);
        System.out.print("Chemin : ");
        for (int v : path2) {
            System.out.print(v + " ");
        }
        System.out.println("\n");

        // ============================================
        // PARTIE B3 : DIJKSTRA - GRAPHE MIXTE
        // Mélange d'arêtes orientées et non orientées
        // ============================================
        System.out.println("=== PARTIE B3 : Dijkstra sur graphe MIXTE (orienté + non orienté) ===");

        Graph gMixte = new Graph(6); // par défaut : non orienté

        // arêtes non orientées
        gMixte.addEdge(0, 1, 2);      // 0 --2-- 1
        gMixte.addEdge(1, 2, 2);      // 1 --2-- 2
        gMixte.addEdge(2, 3, 2);      // 2 --2-- 3

        // arêtes orientées (sens unique)
        gMixte.addDirectedEdge(0, 4, 1); // 0 -> 4 (rapide)
        gMixte.addDirectedEdge(4, 3, 5); // 4 -> 3
        gMixte.addDirectedEdge(3, 5, 1); // 3 -> 5

        int sourceMixte = 0;
        int targetMixte = 5;

        DijkstraSolver.Result res3 = DijkstraSolver.dijkstra(gMixte, sourceMixte);
        List<Integer> path3 = DijkstraSolver.buildPath(res3, sourceMixte, targetMixte);

        System.out.println("Distance minimale " + sourceMixte + " -> " + targetMixte + " : " + res3.dist[targetMixte]);
        System.out.print("Chemin : ");
        for (int v : path3) {
            System.out.print(v + " ");
        }
        System.out.println();
    }
}
