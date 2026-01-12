package chapter08.dijkstraAlgorithm;

import utilities.Edge;
import utilities.Node;

import java.util.*;

public class DijkstraAlgorithm {
    public static void calculate(Node source) {
        source.setDistance(0);

        // PriorityQueue автоматически выбирает узел с наименьшим расстоянием
        PriorityQueue<Node> priorityQueue = new PriorityQueue<>();
        priorityQueue.add(source);

        while (!priorityQueue.isEmpty()) {
            Node currentNode = priorityQueue.poll();

            for (Edge edge : currentNode.getEdges()) {
                Node neighbor = edge.getTarget();
                int newDist = currentNode.getDistance() + edge.getWeight();

                // Если найден более короткий путь
                if (newDist < neighbor.getDistance()) {
                    priorityQueue.remove(neighbor); // Обновляем позицию в очереди
                    neighbor.setDistance(newDist);
                    neighbor.setPrevious(currentNode);
                    priorityQueue.add(neighbor);
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
