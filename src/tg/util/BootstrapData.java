
package tg.util;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

/** Crée data/ + HO1/HO2/HO3 + points.csv si absents. */
public class BootstrapData {

    public static void ensureDataFilesHO123(){
        try {
            Path dataDir = Paths.get(System.getProperty("user.dir")).toAbsolutePath().resolve("data");
            if(!Files.exists(dataDir)) Files.createDirectories(dataDir);

            // HO1 (non orienté)
            Path ho1 = dataDir.resolve("edges_undirected.csv");
            if(!Files.exists(ho1)){
                String content = String.join("\n", List.of(
                        "u,v,w",
                        "Depot,A,2",
                        "A,B,2",
                        "B,C,2",
                        "C,Depot,3",
                        "A,D,1.5",
                        "D,E,2",
                        "E,C,2.5",
                        "B,E,2",
                        "Depot,F,4",
                        "F,C,2.5"
                ));
                Files.writeString(ho1, content);
                System.out.println("[INFO] Créé: "+ho1);
            }

            // HO2 (orienté)
            Path ho2 = dataDir.resolve("edges_directed.csv");
            if(!Files.exists(ho2)){
                String content = String.join("\n", List.of(
                        "u,v,w",
                        "Depot,A,2",
                        "A,B,2",
                        "B,C,2",
                        "C,Depot,5",
                        "A,D,2",
                        "D,E,2",
                        "E,C,3",
                        "Depot,F,4",
                        "F,C,2"
                ));
                Files.writeString(ho2, content);
                System.out.println("[INFO] Créé: "+ho2);
            }

            // HO3 (mixte: colonne type U/D)
            Path ho3 = dataDir.resolve("edges_mixed.csv");
            if(!Files.exists(ho3)){
                String content = String.join("\n", List.of(
                        "u,v,w,type",
                        "Depot,A,2,U",
                        "A,B,2,U",
                        "B,C,2,D",   // sens unique B->C
                        "C,Depot,3,U",
                        "A,D,2,D",   // sens unique A->D
                        "D,E,2,U",
                        "E,C,2.5,U",
                        "Depot,F,4,U",
                        "F,C,2.5,D"  // sens unique F->C
                ));
                Files.writeString(ho3, content);
                System.out.println("[INFO] Créé: "+ho3);
            }

            // points.csv
            Path points = dataDir.resolve("points.csv");
            if(!Files.exists(points)){
                String content = String.join("\n", List.of(
                        "point,qty",
                        "A,4",
                        "C,3",
                        "D,3",
                        "E,5"
                ));
                Files.writeString(points, content);
                System.out.println("[INFO] Créé: "+points);
            }

        } catch (IOException e){
            System.err.println("[ERREUR] Bootstrap data: "+e.getMessage());
        }
    }
}
