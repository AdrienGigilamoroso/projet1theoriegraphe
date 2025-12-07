
package tg.planning;

import java.util.*;

public class CapacitySplitter {

    public static List<List<String>> splitSequential(List<String> tourWithDepot,
                                                     Map<String, Integer> qty,
                                                     int C) {
        List<List<String>> result = new ArrayList<>();
        if (tourWithDepot == null || tourWithDepot.size() < 2) return result;

        final String depot = tourWithDepot.get(0);
        int i = 1; // on saute le premier "Depot"

        while (i < tourWithDepot.size() - 1) {
            int load = 0;
            List<String> sub = new ArrayList<>();
            sub.add(depot);

            boolean tookOversize = false;

            while (i < tourWithDepot.size() - 1) {
                String p = tourWithDepot.get(i);
                int q = qty.getOrDefault(p, 0);

                // Cas 1 : le point dépasse C et on n'a encore rien pris => tournée dédiée [D, p, D]
                if (q > C && load == 0) {
                    sub.add(p);
                    i++;                // consommer ce point
                    tookOversize = true;
                    break;              // fermer la sous-tournée tout de suite
                }

                // Cas 2 : accumulation classique si on respecte la capacité
                if (load + q <= C) {
                    sub.add(p);
                    load += q;
                    i++;
                } else {
                    // la capacité serait dépassée => on termine cette sous-tournée
                    break;
                }
            }

            sub.add(depot);

            // Sécurité : si on n'a rien ajouté (rare), injecter au moins un point pour éviter [D, D]
            if (sub.size() == 2 && !tookOversize && i < tourWithDepot.size() - 1) {
                String p = tourWithDepot.get(i);
                sub.add(1, p); // devient [Depot, p, Depot]
                i++;
            }

            result.add(sub);
        }
        return result;
    }
}
