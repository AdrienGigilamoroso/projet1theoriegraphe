import java.util.ArrayList;
import java.util.List;

public class Secteur {
    private int num;
    private double quantDechets;
    private List<Secteur> adjacents;
    private int couleur;

    public Secteur(int num, double quantDechets) {
        this.num = num;
        this.quantDechets = quantDechets;
        this.adjacents = new ArrayList<>();
        this.couleur = 0;
    }

    public void addLiaison(Secteur s) {
        if (this.adjacents.contains(s) == false) {
            this.adjacents.add(s);
            s.getAdjacents().add(this);
        }
    }
    public void reset() {
        this.couleur = 0;
    }
    public int getDegre() {
        return this.adjacents.size();
    }

    public int getNum() { return num; }
    public double getQuantDechets() { return quantDechets; }
    public List<Secteur> getAdjacents() { return adjacents; }
    public int getCouleur() { return couleur; }
    public void setCouleur(int c) { this.couleur = c; }

    public String toString() {
        return "Secteur " + num + " (" + (int)quantDechets + "kg)";
    }
}