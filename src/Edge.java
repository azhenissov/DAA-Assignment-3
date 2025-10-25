/**
 * Simple Edge representation for an undirected weighted graph.
 */
public class Edge {
    public final String from;
    public final String to;
    public final int weight;

    public Edge(String from, String to, int weight) {
        this.from = from;
        this.to = to;
        this.weight = weight;
    }

    @Override
    public String toString() {
        return String.format("(%s-%s:%d)", from, to, weight);
    }
}
