import java.util.ArrayList;
import java.util.List;

/**
 * Very small JSON parser tailored for the input format used in the assignment.
 * It is not a general-purpose JSON parser but is sufficient for the provided
 * structure: { "graphs": [ { "id": ..., "nodes": [...], "edges": [...] }, ... ] }
 */
public class JsonSimpleParser {
    public static List<Graph> parseGraphs(String s) throws Exception {
        List<Graph> graphs = new ArrayList<>();
        String compact = s.replaceAll("\\r\\n", " ").replaceAll("\\n", " ").trim();
        int graphsIdx = compact.indexOf("\"graphs\"");
        if (graphsIdx < 0) return graphs;
        int arrStart = compact.indexOf('[', graphsIdx);
        if (arrStart < 0) return graphs;
        int i = arrStart + 1;
        while (i < compact.length()) {
            // find next '{' for a graph object
            int objStart = compact.indexOf('{', i);
            if (objStart < 0) break;
            // find matching '}'
            int brace = 1;
            int j = objStart + 1;
            for (; j < compact.length(); j++) {
                char c = compact.charAt(j);
                if (c == '{') brace++;
                else if (c == '}') brace--;
                if (brace == 0) break;
            }
            if (j >= compact.length()) break;
            String obj = compact.substring(objStart, j+1);
            Graph g = parseGraphObject(obj);
            graphs.add(g);
            i = j + 1;
            // move to next ',' or end
            int nextComma = compact.indexOf(',', i);
            if (nextComma < 0) break;
            i = nextComma + 1;
        }

        return graphs;
    }

    private static Graph parseGraphObject(String obj) throws Exception {
        // parse id
        int id = 0;
        int idIdx = obj.indexOf("\"id\"");
        if (idIdx >= 0) {
            int colon = obj.indexOf(':', idIdx);
            int comma = obj.indexOf(',', colon);
            String idStr = obj.substring(colon+1, comma).trim();
            id = Integer.parseInt(idStr);
        }
        Graph g = new Graph(id);

        // parse nodes array
        int nodesIdx = obj.indexOf("\"nodes\"");
        if (nodesIdx >= 0) {
            int start = obj.indexOf('[', nodesIdx);
            int end = obj.indexOf(']', start);
            String nodesBlock = obj.substring(start+1, end);
            String[] parts = nodesBlock.split(",");
            for (String p : parts) {
                String t = p.trim();
                if (t.startsWith("\"")) t = t.substring(1);
                if (t.endsWith("\"")) t = t.substring(0, t.length()-1);
                if (!t.isEmpty()) g.addNode(t);
            }
        }

        // parse edges array
        int edgesIdx = obj.indexOf("\"edges\"");
        if (edgesIdx >= 0) {
            int start = obj.indexOf('[', edgesIdx);
            int end = obj.indexOf(']', start);
            String edgesBlock = obj.substring(start+1, end);
            // split individual edge objects by '},{' pattern
            String[] edgeObjs = edgesBlock.split("\\},\\s*\\{");
            for (String eo : edgeObjs) {
                String eclean = eo.trim();
                if (!eclean.startsWith("{")) eclean = "{" + eclean;
                if (!eclean.endsWith("}")) eclean = eclean + "}";
                // parse from, to, weight
                String from = extractStringField(eclean, "from");
                String to = extractStringField(eclean, "to");
                int weight = extractIntField(eclean, "weight");
                if (from != null && to != null) {
                    g.addEdge(new Edge(from, to, weight));
                }
            }
        }

        return g;
    }

    private static String extractStringField(String obj, String key) {
        int idx = obj.indexOf('"' + key + '"');
        if (idx < 0) return null;
        int colon = obj.indexOf(':', idx);
        int quote1 = obj.indexOf('"', colon);
        int quote2 = obj.indexOf('"', quote1+1);
        return obj.substring(quote1+1, quote2);
    }

    private static int extractIntField(String obj, String key) {
        int idx = obj.indexOf('"' + key + '"');
        if (idx < 0) return 0;
        int colon = obj.indexOf(':', idx);
        int start = colon + 1;
        while (start < obj.length() && (obj.charAt(start) == ' ')) start++;
        int end = start;
        while (end < obj.length() && (Character.isDigit(obj.charAt(end)) || obj.charAt(end)=='-')) end++;
        String num = obj.substring(start, end);
        return Integer.parseInt(num);
    }
}
