
package tg.io;

import tg.model.Graph;
import java.io.*;
import java.nio.file.*;

public class GraphIO {


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
