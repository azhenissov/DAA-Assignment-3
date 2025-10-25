import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Main entry point for the MST assignment.
 * Reads `ass_3_input.json`, runs Prim's and Kruskal's algorithms on each graph,
 * and writes results to `ass_3_output.json`.
 */
public class Main {
    public static void main(String[] args) {
        String inputPath = "ass_3_input.json";
        String outputPath = "ass_3_output.json";

        try {
            String content = new String(Files.readAllBytes(Paths.get(inputPath)), "UTF-8");
            List<Graph> graphs = JsonSimpleParser.parseGraphs(content);

            List<String> resultsJson = new ArrayList<>();
            for (Graph g : graphs) {
                // Input stats
                int vertices = g.getNodes().size();
                int edgesCount = g.getEdges().size();

                // Run Prim
                MST.Result primResult = MST.primMST(g);

                // Run Kruskal
                MST.Result kruskalResult = MST.kruskalMST(g);

                StringBuilder graphResult = new StringBuilder();
                graphResult.append("    {\n");
                graphResult.append("      \"graph_id\": ").append(g.getId()).append(",\n");
                graphResult.append("      \"input_stats\": {\n");
                graphResult.append("        \"vertices\": ").append(vertices).append(",\n");
                graphResult.append("        \"edges\": ").append(edgesCount).append("\n");
                graphResult.append("      },\n");

                graphResult.append("      \"prim\": {")
                        .append("\n        \"mst_edges\": [\n");
                graphResult.append(edgesToJson(primResult.mstEdges));
                graphResult.append("        ],\n");
                graphResult.append("        \"total_cost\": ").append(primResult.totalCost).append(",\n");
                graphResult.append("        \"operations_count\": ").append(primResult.operations).append(",\n");
                graphResult.append(String.format("        \"execution_time_ms\": %.2f\n", primResult.timeMs));
                graphResult.append("      },\n");

                graphResult.append("      \"kruskal\": {")
                        .append("\n        \"mst_edges\": [\n");
                graphResult.append(edgesToJson(kruskalResult.mstEdges));
                graphResult.append("        ],\n");
                graphResult.append("        \"total_cost\": ").append(kruskalResult.totalCost).append(",\n");
                graphResult.append("        \"operations_count\": ").append(kruskalResult.operations).append(",\n");
                graphResult.append(String.format("        \"execution_time_ms\": %.2f\n", kruskalResult.timeMs));
                graphResult.append("      }\n");

                graphResult.append("    }");
                resultsJson.add(graphResult.toString());
            }

            StringBuilder out = new StringBuilder();
            out.append("{\n  \"results\": [\n");
            for (int i = 0; i < resultsJson.size(); i++) {
                out.append(resultsJson.get(i));
                if (i < resultsJson.size() - 1) out.append(",\n");
                else out.append('\n');
            }
            out.append("  ]\n}\n");

            Files.write(Paths.get(outputPath), out.toString().getBytes("UTF-8"));
            System.out.println("Wrote results to " + outputPath);
        } catch (IOException e) {
            System.err.println("I/O error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static String edgesToJson(List<Edge> edges) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < edges.size(); i++) {
            Edge e = edges.get(i);
            sb.append("          {\"from\": \"").append(e.from).append("\", \"to\": \"")
                    .append(e.to).append("\", \"weight\": ").append(e.weight).append("}");
            if (i < edges.size() - 1) sb.append(",\n"); else sb.append('\n');
        }
        return sb.toString();
    }
}