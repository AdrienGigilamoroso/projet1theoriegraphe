
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


public class MainTheme2HO {

    private static final Scanner SC = new Scanner(System.in);

    private static final int CAPACITY_C = 10;

    public static void main(String[] args) {

        BootstrapData.ensureDataFilesHO123();

        try {
            System.out.println("Choisir hypothese: 1=HO1(non oriente), 2=HO2(oriente), 3=HO3(mixte)");
            int ho = askInt("Votre choix (def=1): ", 1);


            Graph road;
            if (ho == 1) road = GraphIO.readHO1("data/edges_undirected.csv");
            else if (ho == 2) road = GraphIO.readHO2("data/edges_directed.csv");
            else road = GraphIO.readHO3("data/edges_mixed.csv");


            String DEPOT = road.nodes().contains("Depot") ? "Depot" : road.nodes().iterator().next();


            Map<String, Integer> qty = readPoints("data/points.csv");
            if (qty.isEmpty()) {
                System.out.println("[ERREUR] points.csv vide. Ajoutez des points présents dans le graphe routier.");
                return;
            }
            for (String p : qty.keySet()) {
                if (!road.nodes().contains(p)) {
                    System.out.println("[ERREUR] Point " + p + " absent du graphe routier.");
                    return;
                }
            }


            List<String> stops = new ArrayList<>();
            stops.add(DEPOT);
            stops.addAll(qty.keySet());

            Map<String, Map<String, Double>> dist = new HashMap<>();
            Map<String, Map<String, String>> parent = new HashMap<>();
            for (String s : stops) {
                var res = Dijkstra.shortest(road, s);
                dist.put(s, res.dist);
                parent.put(s, res.parent);
            }

            if (ho == 1) {

                Graph complete = new Graph(false);
                for (int i = 0; i < stops.size(); i++) {
                    for (int j = i + 1; j < stops.size(); j++) {
                        String u = stops.get(i), v = stops.get(j);
                        double d_uv = dist.get(u).getOrDefault(v, Double.POSITIVE_INFINITY);
                        double d_vu = dist.get(v).getOrDefault(u, Double.POSITIVE_INFINITY);
                        // Symétriser (si un sens n'existe pas, prendre l'autre si valable)
                        double d = (Double.isInfinite(d_uv) || Double.isInfinite(d_vu))
                                ? Math.min(d_uv, d_vu)
                                : Math.min(d_uv, d_vu);
                        if (Double.isInfinite(d)) {
                            System.out.println("[ERREUR] " + u + " et " + v + " inaccessibles (HO1).");
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
                System.out.println("NN: " + tourNN + " | L≈ " + fmt(lenNN));
                System.out.println("Trajet NN (réseau): " + detailedNN);
                System.out.println("MST+DFS+shortcut: " + tourMST + " | L≈ " + fmt(lenMST));
                System.out.println("Trajet MST (réseau): " + detailedMST);


                int base = askInt("\nDécouper quelle tournée ? (1=NN, 2=MST, def=2): ", 2);
                List<String> baseTour = (base == 1) ? tourNN : tourMST;


                var subtours = CapacitySplitter.splitSequential(baseTour, qty, CAPACITY_C);
                printSubtours(subtours, qty, complete, parent);

            } else {

                Set<String> nodeSet = new LinkedHashSet<>(stops);


                var tourNN = TSPNearestNeighborATSP.tour(nodeSet, DEPOT, dist);
                double lenNN = tourLengthDirected(dist, tourNN);
                var detailedNN = detailedPathForTourDirected(tourNN, parent);


                var tourCI = TSPCheapestInsertionATSP.tour(nodeSet, DEPOT, dist);
                double lenCI = tourLengthDirected(dist, tourCI);
                var detailedCI = detailedPathForTourDirected(tourCI, parent);

                System.out.println("\n--- " + (ho == 2 ? "HO2 (orienté)" : "HO3 (mixte)") + " ---");
                System.out.println("NN (ATSP): " + tourNN + " | L≈ " + fmt(lenNN));
                System.out.println("Trajet NN (réseau): " + detailedNN);
                System.out.println("Cheapest Insertion (ATSP): " + tourCI + " | L≈ " + fmt(lenCI));
                System.out.println("Trajet CI (réseau): " + detailedCI);


                int base = askInt("\nDécouper quelle tournée ? (1=NN, 2=CheapestInsertion, def=2): ", 2);
                List<String> baseTour = (base == 1) ? tourNN : tourCI;


                Graph completeDir = new Graph(true);
                for (int i = 0; i < stops.size(); i++) {
                    for (int j = 0; j < stops.size(); j++) {
                        if (i == j) continue;
                        String u = stops.get(i), v = stops.get(j);
                        double d = dist.getOrDefault(u, Collections.emptyMap()).getOrDefault(v, Double.POSITIVE_INFINITY);
                        if (!Double.isInfinite(d)) completeDir.addDirectedEdge(u, v, d);
                    }
                }


                var subtours = CapacitySplitter.splitSequential(baseTour, qty, CAPACITY_C);
                printSubtours(subtours, qty, completeDir, parent);
            }

        } catch (Exception e) {
            System.err.println("[ERREUR] " + e.getClass().getSimpleName() + " : " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ---------------------- Utilitaires distances / reconstruction ----------------------
    private static String fmt(double x) { return String.format("%.2f", x).replace('.', ','); }


    private static double tourLength(Graph complete, List<String> tour) {
        double L = 0.0;
        for (int i = 0; i < tour.size() - 1; i++) {
            String a = tour.get(i), b = tour.get(i + 1);
            if (a.equals(b)) continue; // sécurité
            L += complete.weight(a, b);
        }
        return L;
    }

    private static double tourLengthDirected(Map<String, Map<String, Double>> dist, List<String> tour) {
        double L = 0.0;
        for (int i = 0; i < tour.size() - 1; i++) {
            String a = tour.get(i), b = tour.get(i + 1);
            double w = dist.getOrDefault(a, Collections.emptyMap()).getOrDefault(b, Double.POSITIVE_INFINITY);
            if (!Double.isInfinite(w)) L += w;
        }
        return L;
    }

    /** Concatène les plus courts chemins (réseau) entre arrêts consécutifs. */
    private static List<String> detailedPathForTour(List<String> tour,
                                                    Map<String, Map<String, String>> parent) {
        List<String> det = new ArrayList<>();
        for (int i = 0; i < tour.size() - 1; i++) {
            String s = tour.get(i), t = tour.get(i + 1);
            var seg = reconstruct(parent.get(s), s, t);
            if (det.isEmpty()) det.addAll(seg);
            else if (!seg.isEmpty()) det.addAll(seg.subList(1, seg.size()));
        }
        return det;
    }

    /** Pour HO2/HO3 : reconstruction idem (parent dépend déjà de la direction). */
    private static List<String> detailedPathForTourDirected(List<String> tour,
                                                            Map<String, Map<String, String>> parent) {
        return detailedPathForTour(tour, parent);
    }

    /** Reconstruction générique via parent[]. */
    private static List<String> reconstruct(Map<String, String> parent, String s, String t) {
        List<String> path = new ArrayList<>();
        if (parent == null) return path;
        String cur = t;
        while (cur != null) {
            path.add(cur);
            if (cur.equals(s)) break;
            cur = parent.get(cur);
        }
        Collections.reverse(path);
        if (path.isEmpty() || !path.get(0).equals(s)) return new ArrayList<>();
        return path;
    }

    // ---------------------- Lecture & affichage ----------------------
    private static int askInt(String prompt, int def) {
        System.out.print(prompt);
        String s = SC.nextLine();
        try { return Integer.parseInt(s.trim()); } catch (Exception e) { return def; }
    }

    /** Lecture data/points.csv : header "point,qty" */
    private static Map<String, Integer> readPoints(String path) throws IOException {
        Map<String, Integer> qty = new LinkedHashMap<>();
        Path p = Paths.get(path);
        try (BufferedReader br = Files.newBufferedReader(p)) {
            String line; boolean header = true;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                if (header) { header = false; continue; }
                String[] t = line.split(",");
                if (t.length < 2) continue;
                qty.put(t[0].trim(), Integer.parseInt(t[1].trim()));
            }
        }
        return qty;
    }

    /** Affiche les sous‑tournées avec charge, distance et trajet détaillé. */
    private static void printSubtours(List<List<String>> tours,
                                      Map<String, Integer> qty,
                                      Graph complete,
                                      Map<String, Map<String, String>> parent) {
        System.out.println("\n--- Sous-tournées (C=" + CAPACITY_C + ") ---");
        double totalDist = 0.0;
        int totalLoad = 0;

        for (int idx = 0; idx < tours.size(); idx++) {
            var sub = tours.get(idx);

            // Charge (somme des quantités, dépôt ignoré)
            int load = 0;
            for (String p : sub) load += qty.getOrDefault(p, 0);

            // Distance approx. (somme sur graphe complet / dirigé)
            double distSub = 0.0;
            for (int i = 0; i < sub.size() - 1; i++) {
                String a = sub.get(i), b = sub.get(i + 1);
                if (a.equals(b)) continue; // sécurité
                double w = complete.weight(a, b);
                if (!Double.isInfinite(w)) distSub += w;
            }

            // Trajet détaillé (concaténation de plus courts chemins)
            var detailed = detailedPathForTour(sub, parent);

            System.out.println("T" + (idx + 1) + " : " + sub);
            System.out.println("    Charge: " + load);
            System.out.println("    Distance approx.: " + fmt(distSub));
            System.out.println("    Trajet détaillé (réseau): " + detailed);

            totalDist += distSub;
            totalLoad += load;
        }

        System.out.println("\nRésumé découpe : totalDistance=" + fmt(totalDist) +
                " | totalLoad=" + totalLoad + " | nbSousTournées=" + tours.size());
    }
}
