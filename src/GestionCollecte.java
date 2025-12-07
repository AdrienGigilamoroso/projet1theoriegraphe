import java.util.ArrayList;
import java.util.List;

public class GestionCollecte {

    public static final double MAX_POIDS = 200.0;
    public static void lancerAlgo(List<Secteur> listeSommets, boolean checkPoids) {

        for (int i = 0; i < listeSommets.size(); i++) {
            for (int j = 0; j < listeSommets.size() - 1; j++) {
                Secteur s1 = listeSommets.get(j);
                Secteur s2 = listeSommets.get(j + 1);
                if (s1.getDegre() < s2.getDegre()) {
                    listeSommets.set(j, s2);
                    listeSommets.set(j + 1, s1);
                }
            }
        }
        int numJour = 1;
        int compteur = 0;
        int total = listeSommets.size();
        while (compteur < total) {
            double poidsDuJour = 0;
            List<Secteur> groupeDuJour = new ArrayList<>();
            for (Secteur s : listeSommets) {
                if (s.getCouleur() != 0) {
                    continue;
                }
                boolean toucheVoisin = false;
                for (Secteur voisin : groupeDuJour) {
                    if (s.getAdjacents().contains(voisin)) {
                        toucheVoisin = true;
                        break;
                    }
                }
                boolean poidsOk = true;
                if (checkPoids == true) {
                    if ((poidsDuJour + s.getQuantDechets()) > MAX_POIDS) {
                        poidsOk = false;
                    }
                }
                if (toucheVoisin == false && poidsOk == true) {
                    s.setCouleur(numJour);
                    groupeDuJour.add(s);
                    poidsDuJour = poidsDuJour + s.getQuantDechets();
                    compteur++;
                }
            }
            numJour++;
        }
    }
}