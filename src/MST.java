import java.util.*;

/**
 * Implements Prim's and Kruskal's algorithms and collects statistics.
 */
public class MST {
    public static class Result {
        public List<Edge> mstEdges = new ArrayList<>();
        public int totalCost = 0;
        public long operations = 0;
        public double timeMs = 0.0;
    }

    /**
     * Prim's algorithm for MST.
     * Comments: This implementation uses a min-heap of candidate edges.
     * Counts operations as: each edge push/pop and each edge examination.
     */
    public static Result primMST(Graph g) {
        Result res = new Result();
        long ops = 0;
        long start = System.nanoTime();

        List<String> nodes = g.getNodes();
        if (nodes.isEmpty()) { res.timeMs = 0; return res; }

        // Build adjacency map
        Map<String, List<Edge>> adj = new HashMap<>();
        for (String n : nodes) adj.put(n, new ArrayList<>());
        for (Edge e : g.getEdges()) {
            adj.get(e.from).add(e);
            adj.get(e.to).add(new Edge(e.to, e.from, e.weight)); // reverse for easier traversal
        }

        Set<String> visited = new HashSet<>();
        PriorityQueue<Edge> pq = new PriorityQueue<>(Comparator.comparingInt(e -> e.weight));

        String startNode = nodes.get(0);
        visited.add(startNode);
        // push starting edges
        for (Edge e : adj.get(startNode)) { pq.add(e); ops++; }

        while (!pq.isEmpty() && res.mstEdges.size() < nodes.size() - 1) {
            Edge e = pq.poll();
            ops++; // poll
            if (visited.contains(e.to)) continue;
            // accept edge
            visited.add(e.to);
            res.mstEdges.add(new Edge(e.from, e.to, e.weight));
            res.totalCost += e.weight;
            ops++; // adding to mst
            // add adjacent edges
            for (Edge ne : adj.get(e.to)) { if (!visited.contains(ne.to)) { pq.add(ne); ops++; } }
        }

        long end = System.nanoTime();
        res.operations = ops;
        res.timeMs = (end - start) / 1_000_000.0;
        return res;
    }

    /**
     * Kruskal's algorithm for MST.
     * Comments: Sort edges then iterate, using union-find. Counts operations as: edge examinations and union/find calls.
     */
    public static Result kruskalMST(Graph g) {
        Result res = new Result();
        long ops = 0;
        long start = System.nanoTime();

        List<String> nodes = g.getNodes();
        int n = nodes.size();
        Map<String,Integer> idx = new HashMap<>();
        for (int i = 0; i < nodes.size(); i++) idx.put(nodes.get(i), i);

        // create list of undirected edges (ensure single entry per undirected)
        List<Edge> edges = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        for (Edge e : g.getEdges()) {
            String key = e.from + "|" + e.to;
            String key2 = e.to + "|" + e.from;
            if (seen.contains(key) || seen.contains(key2)) continue;
            seen.add(key);
            edges.add(e);
        }

        // sort
        edges.sort(Comparator.comparingInt(e -> e.weight));

        UnionFind uf = new UnionFind(n);
        for (Edge e : edges) {
            ops++; // examine edge
            int a = idx.get(e.from);
            int b = idx.get(e.to);
            // we'll count find/union as operations
            int ra = uf.find(a); ops++;
            int rb = uf.find(b); ops++;
            if (ra != rb) {
                boolean united = uf.union(ra, rb); ops++;
                if (united) {
                    res.mstEdges.add(new Edge(e.from, e.to, e.weight));
                    res.totalCost += e.weight;
                }
            }
            if (res.mstEdges.size() == n-1) break;
        }

        long end = System.nanoTime();
        res.operations = ops;
        res.timeMs = (end - start) / 1_000_000.0;
        return res;
    }
}
