import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        Secteur s1 = new Secteur(1, 80);
        Secteur s2 = new Secteur(2, 70);
        Secteur s3 = new Secteur(3, 90);
        Secteur s4 = new Secteur(4, 85);
        Secteur s5 = new Secteur(5, 75);
        Secteur s6 = new Secteur(6, 150);
        Secteur s7 = new Secteur(7, 60);

        List<Secteur> ville = new ArrayList<>();
        ville.add(s1); ville.add(s2); ville.add(s3);
        ville.add(s4); ville.add(s5); ville.add(s6); ville.add(s7);

        s6.addLiaison(s1); s6.addLiaison(s2); s6.addLiaison(s3);
        s6.addLiaison(s4); s6.addLiaison(s5); s6.addLiaison(s7);
        s3.addLiaison(s2); s3.addLiaison(s5);
        s4.addLiaison(s7);

        System.out.println("=== Test de l'hypothèse 1 (sans limite de poids) ===");
        GestionCollecte.lancerAlgo(ville, false);
        afficher(ville, false);
        for(Secteur s : ville) {
            s.reset();
        }

        System.out.println("\n=== Test de l'hypothèse 2 (avec une limite de 200kg) ==");
        GestionCollecte.lancerAlgo(ville, true);
        afficher(ville, true);
    }

    public static void afficher(List<Secteur> liste, boolean details) {
        int max = 0;
        for(Secteur s : liste) {
            if(s.getCouleur() > max) max = s.getCouleur();
        }
        for (int i = 1; i <= max; i++) {
            System.out.print("JOUR " + i + " : ");
            double totalJour = 0;
            for (Secteur s : liste) {
                if (s.getCouleur() == i) {
                    if (details == true) {
                        System.out.print(s + " ");
                    } else {
                        System.out.print("Secteur " + s.getNum() + " ");
                    }
                    totalJour = totalJour + s.getQuantDechets();
                }
            }
            if (details == true) {
                System.out.println(" | Total: " + (int)totalJour + "kg");
            } else {
                System.out.println("");
            }
        }
    }
}