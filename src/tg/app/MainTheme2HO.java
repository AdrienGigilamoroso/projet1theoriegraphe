
package tg.app;

import tg.io.GraphIO;
import tg.model.Graph;
import tg.algo.Dijkstra;
import tg.algo.TSPNearestNeighbor;
import tg.algo.TSPMSTApprox;
import tg.algo.TSPNearestNeighborATSP;
import tg.algo.TSPCheapestInsertionATSP;
import tg.planning.CapacitySplitter;
import tg.util.BootstrapData;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

/**
 * Main Thème 2 avec sélection HO1/HO2/HO3:
 * - HO1: non orienté -> NN + MST+DFS+shortcut
 * - HO2/HO3: dirigé/mixte -> NN (ATSP) + Cheapest Insertion (ATSP)
 * - tout repose sur des distances de plus court chemin (Dijkstra) calculées sur le graphe routier.
 */
public class MainTheme2HO {

    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        BootstrapData.ensureDataFilesHO123(); // crée tous les CSV d'exemple
        try {
            System.out.println("Choisir hypothese: 1=HO1(non oriente), 2=HO2(oriente), 3=HO3(mixte)");
            int ho = askInt("Votre choix (def=1): ", 1);

            Graph road;
            if(ho == 1) road = GraphIO.readHO1("data/edges_undirected.csv");
            else if(ho == 2) road = GraphIO.readHO2("data/edges_directed.csv");
            else road = GraphIO.readHO3("data/edges_mixed.csv");

            String DEPOT = road.nodes().contains("Depot") ? "Depot" : road.nodes().iterator().next();

            Map<String,Integer> qty = readPoints("data/points.csv");
            if(qty.isEmpty()){
                System.out.println("[ERREUR] points.csv vide.");
                return;
            }
            for(String p : qty.keySet()){
                if(!road.nodes().contains(p)){
                    System.out.println("[ERREUR] Point "+p+" absent du graphe routier.");
                    return;
                }
            }

            // stops = Depot + points
            List<String> stops = new ArrayList<>(); stops.add(DEPOT); stops.addAll(qty.keySet());

            // Distances de plus court chemin depuis chaque stop
            Map<String, Map<String, Double>> dist = new HashMap<>();
            Map<String, Map<String, String>> parent = new HashMap<>();
            for(String s : stops){
                var res = Dijkstra.shortest(road, s);
                dist.put(s, res.dist);
                parent.put(s, res.parent);
            }

            if(ho == 1){
                // Graphe complet NON oriente (symétrique)
                Graph complete = new Graph(false);
                for(int i=0;i<stops.size();i++){
                    for(int j=i+1;j<stops.size();j++){
                        String u = stops.get(i), v = stops.get(j);
                        double d_uv = dist.get(u).getOrDefault(v, Double.POSITIVE_INFINITY);
                        double d_vu = dist.get(v).getOrDefault(u, Double.POSITIVE_INFINITY);
                        double d = (Double.isInfinite(d_uv) || Double.isInfinite(d_vu)) ? Double.POSITIVE_INFINITY : Math.min(d_uv, d_vu);
                        if(Double.isInfinite(d)){
                            System.out.println("[ERREUR] "+u+" et "+v+" inaccessibles (HO1).");
                            return;
                        }
                        complete.addEdge(u, v, d);
                    }
                }

                var tourNN = TSPNearestNeighbor.tour(complete, DEPOT);
                double lenNN = tourLength(complete, tourNN);
                var detailedNN = detailedPathForTour(tourNN, parent);

                var tourMST = TSPMSTApprox.tour(complete, DEPOT);
                double lenMST = tourLength(complete, tourMST);
                var detailedMST = detailedPathForTour(tourMST, parent);

                System.out.println("\n--- HO1 ---");
                System.out.println("NN: " + tourNN + " | L≈ " + String.format("%.2f", lenNN));
                System.out.println("Trajet NN (réseau): " + detailedNN);
                System.out.println("MST+DFS+shortcut: " + tourMST + " | L≈ " + String.format("%.2f", lenMST));
                System.out.println("Trajet MST (réseau): " + detailedMST);

                int base = askInt("\nDécouper quelle tournée ? (1=NN, 2=MST, def=2): ", 2);
                List<String> baseTour = (base==1) ? tourNN : tourMST;
                int C = askInt("Capacité C (def=10): ", 10);
                var tours = CapacitySplitter.splitSequential(baseTour, qty, C);
                printSubtours(tours, qty, complete, parent);

            } else {
                // Matrice dirigée (ATSP)
                Set<String> nodeSet = new LinkedHashSet<>(stops);

                var tourNN = TSPNearestNeighborATSP.tour(nodeSet, DEPOT, dist);
                double lenNN = tourLengthDirected(dist, tourNN);
                var detailedNN = detailedPathForTourDirected(tourNN, parent);

                var tourCI = TSPCheapestInsertionATSP.tour(nodeSet, DEPOT, dist);
                double lenCI = tourLengthDirected(dist, tourCI);
                var detailedCI = detailedPathForTourDirected(tourCI, parent);

                System.out.println("\n--- " + (ho==2 ? "HO2 (orienté)" : "HO3 (mixte)") + " ---");
                System.out.println("NN (ATSP): " + tourNN + " | L≈ " + String.format("%.2f", lenNN));
                System.out.println("Trajet NN (réseau): " + detailedNN);
                System.out.println("Cheapest Insertion (ATSP): " + tourCI + " | L≈ " + String.format("%.2f", lenCI));
                System.out.println("Trajet CI (réseau): " + detailedCI);

                int base = askInt("\nDécouper quelle tournée ? (1=NN, 2=CheapestInsertion, def=2): ", 2);
                List<String> baseTour = (base==1) ? tourNN : tourCI;
                int C = askInt("Capacité C (def=10): ", 10);
                // Pour l'affichage distance des sous‑tournées, on reconstruit un "complete dirigé" ad-hoc
                Graph completeDir = new Graph(true);
                for(int i=0;i<stops.size();i++){
                    for(int j=0;j<stops.size();j++){
                        if(i==j) continue;
                        String u = stops.get(i), v = stops.get(j);
                        double d = dist.getOrDefault(u, Collections.emptyMap()).getOrDefault(v, Double.POSITIVE_INFINITY);
                        if(!Double.isInfinite(d)) completeDir.addDirectedEdge(u, v, d);
                    }
                }
                var tours = CapacitySplitter.splitSequential(baseTour, qty, C);
                printSubtours(tours, qty, completeDir, parent); // distance approx. via graphe complet dirigé
            }

        } catch (Exception e){
            System.err.println("[ERREUR] " + e.getClass().getSimpleName() + " : " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ---------- util distances / chemins ----------
    private static double tourLength(Graph complete, List<String> tour){
        double L=0.0;
        for(int i=0;i<tour.size()-1;i++) L += complete.weight(tour.get(i), tour.get(i+1));
        return L;
    }
    private static double tourLengthDirected(Map<String, Map<String, Double>> dist, List<String> tour){
        double L=0.0;
        for(int i=0;i<tour.size()-1;i++){
            L += dist.getOrDefault(tour.get(i), Collections.emptyMap()).getOrDefault(tour.get(i+1), Double.POSITIVE_INFINITY);
        }
        return L;
    }
    private static List<String> detailedPathForTour(List<String> tour, Map<String, Map<String, String>> parent){
        List<String> det = new ArrayList<>();
        for(int i=0;i<tour.size()-1;i++){
            String s = tour.get(i), t = tour.get(i+1);
            var seg = reconstruct(parent.get(s), s, t);
            if(det.isEmpty()) det.addAll(seg);
            else if(!seg.isEmpty()) det.addAll(seg.subList(1, seg.size()));
        }
        return det;
    }
    private static List<String> detailedPathForTourDirected(List<String> tour, Map<String, Map<String, String>> parent){
        return detailedPathForTour(tour, parent); // même reconstruction (parent dépend de la direction)
    }
    private static List<String> reconstruct(Map<String,String> parent, String s, String t){
        List<String> path = new ArrayList<>();
        if(parent == null) return path;
        String cur = t;
        while(cur != null){
            path.add(cur);
            if(cur.equals(s)) break;
            cur = parent.get(cur);
        }
        Collections.reverse(path);
        if(path.isEmpty() || !path.get(0).equals(s)) return new ArrayList<>();
        return path;
    }

    private static int askInt(String prompt, int def){
        System.out.print(prompt);
        String s = sc.nextLine();
        try { return Integer.parseInt(s.trim()); } catch(Exception e){ return def; }
    }

    private static Map<String,Integer> readPoints(String path) throws IOException {
        Map<String,Integer> qty = new LinkedHashMap<>();
        Path p = Paths.get(path);
        try(BufferedReader br = Files.newBufferedReader(p)){
            String line; boolean header=true;
            while((line = br.readLine()) != null){
                line = line.trim();
                if(line.isEmpty() || line.startsWith("#")) continue;
                if(header){ header=false; continue; }
                String[] t = line.split(",");
                if(t.length < 2) continue;
                qty.put(t[0].trim(), Integer.parseInt(t[1].trim()));
            }
        }
        return qty;
    }

    private static int loadOfSubtour(List<String> subtour, Map<String,Integer> qty){
        int load=0;
        for(String p : subtour) load += qty.getOrDefault(p, 0);
        return load;
    }

    private static void printSubtours(List<List<String>> tours,
                                      Map<String,Integer> qty,
                                      Graph complete,
                                      Map<String, Map<String, String>> parent){
        System.out.println("\n--- Sous-tournées ---");
        for(int idx=0; idx<tours.size(); idx++){
            var sub = tours.get(idx);
            int load = loadOfSubtour(sub, qty);
            double dist = tourLength(complete, sub);
            var detailed = detailedPathForTour(sub, parent);
            System.out.println("T"+(idx+1)+" : "+sub);
            System.out.println("    Charge: "+load);
            System.out.printf("    Distance approx.: %.2f\n", dist);
            System.out.println("    Trajet détaillé (réseau): "+detailed);
        }
    }
}
