package chapter09.bellmanFord;

import utilities.Edge;
import utilities.Graph;
import utilities.Node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Алгоритм Беллмана–Форда: кратчайшие пути из одной вершины с допуском отрицательных весов рёбер.
 * Определяет достижимый из источника отрицательный цикл (если есть).
 */
public final class BellmanFord {

    private BellmanFord() {
    }

    public static boolean calculate(Graph graph, Node source) {
        return calculate(source, graph.getNodes());
    }

    /**
     * Сбрасывает distance и previous у всех узлов, находит кратчайшие расстояния от {@code source}.
     *
     * @return {@code false}, если из источника достижим цикл суммарного веса &lt; 0 (ответ не определён)
     */
    public static boolean calculate(Node source, Iterable<Node> allNodes) {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(allNodes, "allNodes");
        List<Node> vertices = new ArrayList<>();
        for (Node n : allNodes) {
            vertices.add(n);
        }
        for (Node n : vertices) {
            n.setDistance(Integer.MAX_VALUE);
            n.setPrevious(null);
        }
        source.setDistance(0);

        int n = vertices.size();
        for (int i = 0; i < n - 1; i++) {
            if (!relaxAllEdges(vertices)) {
                break;
            }
        }
        return !hasRelaxableEdge(vertices);
    }

    /**
     * Один проход по всем направленным рёбрам: исходящие из каждой вершины ({@code u} → target).
     */
    private static boolean relaxAllEdges(List<Node> vertices) {
        boolean changed = false;
        for (Node u : vertices) {
            int du = u.getDistance();
            if (du == Integer.MAX_VALUE) {
                continue;
            }
            for (Edge edge : u.getEdges()) {
                Node v = edge.getTarget();
                long relaxed = (long) du + edge.getWeight();
                if (relaxed < v.getDistance()) {
                    v.setDistance((int) relaxed);
                    v.setPrevious(u);
                    changed = true;
                }
            }
        }
        return changed;
    }

    private static boolean hasRelaxableEdge(List<Node> vertices) {
        for (Node u : vertices) {
            int du = u.getDistance();
            if (du == Integer.MAX_VALUE) {
                continue;
            }
            for (Edge edge : u.getEdges()) {
                Node v = edge.getTarget();
                if ((long) du + edge.getWeight() < v.getDistance()) {
                    return true;
                }
            }
        }
        return false;
    }

    public static List<Node> getShortestPathTo(Node target) {
        List<Node> path = new ArrayList<>();
        for (Node node = target; node != null; node = node.getPrevious()) {
            path.add(node);
        }
        Collections.reverse(path);
        return path;
    }
}
