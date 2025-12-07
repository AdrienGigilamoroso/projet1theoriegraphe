
package tg.io;

import tg.model.Graph;
import java.io.*;
import java.nio.file.*;

/**
 * HO1 (non orienté) : u,v,w
 * HO2 (orienté)     : u,v,w
 * HO3 (mixte)       : u,v,w,type   avec type in {"U","D"}
 *  - "U" : double-sens -> arête non orientée
 *  - "D" : sens unique -> arête orientée u->v
 */
public class GraphIO {

    /** Lecture simple: HO1 (non orienté). */
    public static Graph readHO1(String path) throws IOException {
        Graph g = new Graph(false);
        try(BufferedReader br = Files.newBufferedReader(Paths.get(path))){
            String line; boolean header=true;
            while((line = br.readLine()) != null){
                line = line.trim();
                if(line.isEmpty() || line.startsWith("#")) continue;
                if(header){ header=false; continue; }
                String[] t = line.split(",");
                String u = t[0].trim(), v = t[1].trim();
                double w = (t.length>=3 && !t[2].isBlank()) ? Double.parseDouble(t[2]) : 1.0;
                g.addEdge(u,v,w); // non orienté
            }
        }
        return g;
    }

    /** Lecture HO2 (orienté). */
    public static Graph readHO2(String path) throws IOException {
        Graph g = new Graph(true);
        try(BufferedReader br = Files.newBufferedReader(Paths.get(path))){
            String line; boolean header=true;
            while((line = br.readLine()) != null){
                line = line.trim();
                if(line.isEmpty() || line.startsWith("#")) continue;
                if(header){ header=false; continue; }
                String[] t = line.split(",");
                String u = t[0].trim(), v = t[1].trim();
                double w = (t.length>=3 && !t[2].isBlank()) ? Double.parseDouble(t[2]) : 1.0;
                g.addDirectedEdge(u,v,w); // orienté
            }
        }
        return g;
    }

    /** Lecture HO3 (mixte: colonne 'type' U/D). */
    public static Graph readHO3(String path) throws IOException {
        Graph g = new Graph(true); // on gère tout en orienté, et on ajoute U comme double arête
        try(BufferedReader br = Files.newBufferedReader(Paths.get(path))){
            String line; boolean header=true;
            while((line = br.readLine()) != null){
                line = line.trim();
                if(line.isEmpty() || line.startsWith("#")) continue;
                if(header){ header=false; continue; }
                String[] t = line.split(",");
                String u = t[0].trim(), v = t[1].trim();
                double w = (t.length>=3 && !t[2].isBlank()) ? Double.parseDouble(t[2]) : 1.0;
                String type = (t.length>=4) ? t[3].trim().toUpperCase() : "U"; // défaut: U
                if("U".equals(type)) g.addUndirectedEdge(u,v,w);
                else g.addDirectedEdge(u,v,w);
            }
        }
        return g;
    }
}
