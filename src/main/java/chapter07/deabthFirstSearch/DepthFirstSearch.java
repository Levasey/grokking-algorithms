package chapter07.deabthFirstSearch;

import utilities.Edge;
import utilities.Node;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class DepthFirstSearch {
    /**
     * Рекурсивная реализация DFS
     */
    public static void performRecursiveDFS(Node startNode) {
        if (startNode == null) {
            System.out.println("Стартовый узел не может быть null");
            return;
        }

        Set<Node> visited = new HashSet<>();
        resetNodes(Collections.singletonList(startNode));
        startNode.setDistance(0);

        System.out.println("Порядок обхода DFS (рекурсивный):");

        AtomicInteger step = new AtomicInteger(1);
        recursiveDFSHelper(startNode, visited, step);

        System.out.println("\nРекурсивный DFS завершен. Всего посещено узлов: " + visited.size());
    }

    /**
     * Вспомогательный метод для рекурсивного DFS
     */
    private static void recursiveDFSHelper(Node currentNode, Set<Node> visited, AtomicInteger step) {
        // Помечаем текущий узел как посещенный
        visited.add(currentNode);

        // Выводим информацию о текущем узле
        System.out.printf("%d. Узел: %s, Глубина: %d%n",
                step.getAndIncrement(),
                currentNode.getName(),
                currentNode.getDistance());

        // Рекурсивно посещаем всех непосещенных соседей
        for (Edge edge : currentNode.getEdges()) {
            Node neighbor = edge.getTarget();

            if (!visited.contains(neighbor)) {
                neighbor.setDistance(currentNode.getDistance() + 1);
                neighbor.setPrevious(currentNode);
                recursiveDFSHelper(neighbor, visited, step);
            }
        }
    }

    /**
     * Сброс состояния всех узлов
     */
    private static void resetNodes(Collection<Node> nodes) {
        for (Node node : nodes) {
            node.setDistance(Integer.MAX_VALUE);
            node.setPrevious(null);
        }
    }
}
