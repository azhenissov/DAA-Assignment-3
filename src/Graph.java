import java.util.ArrayList;
import java.util.List;

/**
 * Graph container with node labels and edge list.
 */
public class Graph {
    private final int id;
    private final List<String> nodes;
    private final List<Edge> edges;

    public Graph(int id) {
        this.id = id;
        this.nodes = new ArrayList<>();
        this.edges = new ArrayList<>();
    }

    public int getId() { return id; }
    public List<String> getNodes() { return nodes; }
    public List<Edge> getEdges() { return edges; }

    public void addNode(String n) { nodes.add(n); }
    public void addEdge(Edge e) { edges.add(e); }
}
