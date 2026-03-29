package chapter08.dijkstraAlgorithm;

import utilities.Edge;
import utilities.Graph;
import utilities.Node;

import java.util.*;

public class DijkstraAlgorithm {

    /** Запись в очереди: при улучшении пути кладём новую пару вместо remove + decrease-key (O(log n) на offer). */
    private record QueueEntry(int distance, Node node) implements Comparable<QueueEntry> {
        @Override
        public int compareTo(QueueEntry other) {
            int c = Integer.compare(distance, other.distance);
            if (c != 0) {
                return c;
            }
            return node.getName().compareTo(other.node.getName());
        }
    }

    public static void calculate(Graph graph, Node source) {
        calculate(source, graph.getNodes());
    }

    /**
     * Перед обходом сбрасывает distance и previous у всех узлов графа — повторный запуск не наследует
     * состояние прошлого вычисления на недостижимых узлах.
     */
    public static void calculate(Node source, Iterable<Node> allNodes) {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(allNodes, "allNodes");
        for (Node n : allNodes) {
            n.setDistance(Integer.MAX_VALUE);
            n.setPrevious(null);
        }
        source.setDistance(0);

        PriorityQueue<QueueEntry> priorityQueue = new PriorityQueue<>();
        priorityQueue.add(new QueueEntry(0, source));

        while (!priorityQueue.isEmpty()) {
            QueueEntry entry = priorityQueue.poll();
            if (entry.distance != entry.node.getDistance()) {
                continue;
            }
            Node currentNode = entry.node;

            for (Edge edge : currentNode.getEdges()) {
                Node neighbor = edge.getTarget();
                int newDist = currentNode.getDistance() + edge.getWeight();

                if (newDist < neighbor.getDistance()) {
                    neighbor.setDistance(newDist);
                    neighbor.setPrevious(currentNode);
                    priorityQueue.add(new QueueEntry(newDist, neighbor));
                }
            }
        }
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
