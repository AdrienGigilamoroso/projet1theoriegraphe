
package tg.algo;

import tg.model.Edge;
import tg.model.Graph;
import java.util.*;


public class TSPMSTApprox {
    public static List<String> tour(Graph complete, String depot){
        List<Edge> T = MSTKruskal.mst(complete);
        Map<String,List<String>> tree = new HashMap<>();
        for(Edge e: T){
            tree.computeIfAbsent(e.u, k->new ArrayList<>()).add(e.v);
            tree.computeIfAbsent(e.v, k->new ArrayList<>()).add(e.u);
        }
        List<String> order = new ArrayList<>();
        Set<String> vis = new HashSet<>();
        Deque<String> st = new ArrayDeque<>();
        st.push(depot);
        Map<String, Iterator<String>> it = new HashMap<>();
        while(!st.isEmpty()){
            String u = st.peek();
            if(!vis.contains(u)){
                vis.add(u); order.add(u);
                it.put(u, tree.getOrDefault(u, List.of()).iterator());
            }
            Iterator<String> iter = it.get(u);
            if(iter != null && iter.hasNext()){
                String v = iter.next();
                if(!vis.contains(v)) st.push(v);
            } else st.pop();
        }
        LinkedHashSet<String> uniq = new LinkedHashSet<>(order);
        List<String> tour = new ArrayList<>(uniq);
        if(!tour.contains(depot)) tour.add(0, depot);
        tour.add(depot);
        return tour;
    }
}
