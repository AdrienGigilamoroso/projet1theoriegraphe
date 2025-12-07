
package tg.model;

public class Edge {
    public final String u, v;
    public final double w;
    public Edge(String u, String v, double w) { this.u=u; this.v=v; this.w=w; }
    @Override public String toString(){ return u+"-"+v+"("+w+")"; }
}
