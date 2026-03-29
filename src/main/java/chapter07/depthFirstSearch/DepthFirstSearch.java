package chapter07.depthFirstSearch;

import utilities.Edge;
import utilities.Graph;
import utilities.Node;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class DepthFirstSearch {

    /**
     * Рекурсивный DFS. Перед обходом сбрасывает distance/previous у всех узлов графа,
     * чтобы повторные запуски на тех же экземплярах {@link Node} не оставляли мусор
     * в узлах из других компонент или не посещённых при прошлом обходе.
     */
    public static void performRecursiveDFS(Graph graph, Node startNode) {
        performRecursiveDFS(graph, startNode, System.out::println);
    }

    /**
     * Рекурсивный DFS по коллекции всех узлов графа (с полным сбросом их состояния), с выводом в консоль.
     */
    public static void performRecursiveDFS(Collection<Node> allNodes, Node startNode) {
        performRecursiveDFS(allNodes, startNode, System.out::println);
    }

    /**
     * @return число посещённых узлов, или 0 при ошибке валидации
     */
    public static int performRecursiveDFS(Graph graph, Node startNode, Consumer<String> log) {
        if (graph == null) {
            Consumer<String> sink = log != null ? log : s -> {};
            sink.accept("Граф не может быть null");
            return 0;
        }
        return performRecursiveDFS(graph.getNodes(), startNode, log);
    }

    /**
     * Рекурсивный DFS по коллекции всех узлов графа (с полным сбросом их состояния).
     *
     * @param allNodes  все вершины графа; узлы вне этой коллекции не сбрасываются
     * @param startNode старт обхода, должен входить в {@code allNodes}
     * @param log       потребитель строк лога; {@code null} — без вывода
     * @return число посещённых узлов, или 0 при ошибке валидации
     */
    public static int performRecursiveDFS(Collection<Node> allNodes, Node startNode, Consumer<String> log) {
        Consumer<String> sink = log != null ? log : s -> {};
        if (allNodes == null) {
            sink.accept("Коллекция узлов не может быть null");
            return 0;
        }
        if (startNode == null) {
            sink.accept("Стартовый узел не может быть null");
            return 0;
        }
        if (!allNodes.contains(startNode)) {
            sink.accept("Стартовый узел должен входить в коллекцию всех узлов графа");
            return 0;
        }

        Set<Node> visited = new HashSet<>();
        resetNodes(allNodes);
        startNode.setDistance(0);

        sink.accept("Порядок обхода DFS (рекурсивный):");

        AtomicInteger step = new AtomicInteger(1);
        recursiveDFSHelper(startNode, visited, step, sink);

        sink.accept("");
        sink.accept("Рекурсивный DFS завершен. Всего посещено узлов: " + visited.size());
        return visited.size();
    }

    /**
     * Вспомогательный метод для рекурсивного DFS
     */
    private static void recursiveDFSHelper(Node currentNode, Set<Node> visited, AtomicInteger step,
                                           Consumer<String> sink) {
        visited.add(currentNode);

        sink.accept(String.format("%d. Узел: %s, Глубина: %d",
                step.getAndIncrement(),
                currentNode.getName(),
                currentNode.getDistance()));

        for (Edge edge : currentNode.getEdges()) {
            Node neighbor = edge.getTarget();

            if (!visited.contains(neighbor)) {
                neighbor.setDistance(currentNode.getDistance() + 1);
                neighbor.setPrevious(currentNode);
                recursiveDFSHelper(neighbor, visited, step, sink);
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
