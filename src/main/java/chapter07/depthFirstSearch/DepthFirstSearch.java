package chapter07.depthFirstSearch;

import utilities.Edge;
import utilities.Graph;
import utilities.Node;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class DepthFirstSearch {
    /**
     * Рекурсивный DFS. Перед обходом сбрасывает distance/previous у всех узлов графа,
     * чтобы повторные запуски на тех же экземплярах {@link Node} не оставляли мусор
     * в узлах из других компонент или не посещённых при прошлом обходе.
     */
    public static void performRecursiveDFS(Graph graph, Node startNode) {
        if (graph == null) {
            System.out.println("Граф не может быть null");
            return;
        }
        performRecursiveDFS(graph.getNodes(), startNode);
    }

    /**
     * Рекурсивный DFS по коллекции всех узлов графа (с полным сбросом их состояния).
     *
     * @param allNodes  все вершины графа; узлы вне этой коллекции не сбрасываются
     * @param startNode старт обхода, должен входить в {@code allNodes}
     */
    public static void performRecursiveDFS(Collection<Node> allNodes, Node startNode) {
        if (allNodes == null) {
            System.out.println("Коллекция узлов не может быть null");
            return;
        }
        if (startNode == null) {
            System.out.println("Стартовый узел не может быть null");
            return;
        }
        if (!allNodes.contains(startNode)) {
            System.out.println("Стартовый узел должен входить в коллекцию всех узлов графа");
            return;
        }

        Set<Node> visited = new HashSet<>();
        resetNodes(allNodes);
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
