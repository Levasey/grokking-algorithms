package chapter06.breadthFirstSearch;

import utilities.Node;

import java.util.*;
import java.util.function.Consumer;

public class BreadthFirstSearch {

    /**
     * BFS с выводом в консоль (удобно для демо и учебных примеров).
     */
    public static void performBFS(Node startNode) {
        performBFS(startNode, System.out::println);
    }

    /**
     * BFS с опциональным потребителем строк лога. Каждая строка — то, что в консольном режиме
     * ушло бы в отдельный {@code println}. {@code log == null} — только алгоритм, без логирования.
     *
     * @return число посещённых узлов, или 0 если стартовый узел {@code null}
     */
    public static int performBFS(Node startNode, Consumer<String> log) {
        Consumer<String> sink = log != null ? log : s -> {};
        if (startNode == null) {
            sink.accept("Стартовый узел не может быть null");
            return 0;
        }

        // Очередь для BFS и множество посещённых узлов
        Queue<Node> queue = new LinkedList<>();
        Set<Node> visited = new HashSet<>();

        startNode.setDistance(0);
        startNode.setPrevious(null);

        queue.add(startNode);
        visited.add(startNode);

        sink.accept("Порядок обхода BFS:");
        int step = 1;

        while (!queue.isEmpty()) {
            Node currentNode = queue.poll();

            sink.accept(String.format("%d. Узел: %s, Расстояние от старта: %d",
                    step++,
                    currentNode.getName(),
                    currentNode.getDistance()));

            for (utilities.Edge edge : currentNode.getEdges()) {
                Node neighbor = edge.getTarget();

                if (!visited.contains(neighbor)) {
                    neighbor.setDistance(currentNode.getDistance() + 1);
                    neighbor.setPrevious(currentNode);

                    queue.add(neighbor);
                    visited.add(neighbor);
                }
            }
        }

        sink.accept("");
        sink.accept("BFS завершен. Всего посещено узлов: " + visited.size());
        return visited.size();
    }

    // Дополнительный метод для восстановления пути от стартового узла к целевому
    public static List<Node> getPathTo(Node target) {
        if (target == null || target.getDistance() == Integer.MAX_VALUE) {
            return Collections.emptyList();
        }

        List<Node> path = new LinkedList<>();
        Node current = target;

        while (current != null) {
            path.add(0, current); // Добавляем в начало для правильного порядка
            current = current.getPrevious();
        }

        return path;
    }

    // Дополнительный метод для печати пути
    public static void printPath(Node target) {
        List<Node> path = getPathTo(target);

        if (path.isEmpty()) {
            System.out.println("Путь не найден");
            return;
        }

        System.out.printf("Путь от старта к узлу %s (длина: %d):%n",
                target.getName(), target.getDistance());

        for (int i = 0; i < path.size(); i++) {
            if (i > 0) {
                System.out.print(" -> ");
            }
            System.out.print(path.get(i).getName());
        }
        System.out.println();
    }

    // Дополнительный метод для BFS с поиском конкретного узла
    public static Node findNodeBFS(Node startNode, String targetName) {
        if (startNode == null || targetName == null) {
            return null;
        }

        Queue<Node> queue = new LinkedList<>();
        Set<Node> visited = new HashSet<>();

        startNode.setDistance(0);
        startNode.setPrevious(null);
        queue.add(startNode);
        visited.add(startNode);

        while (!queue.isEmpty()) {
            Node currentNode = queue.poll();

            if (targetName.equals(currentNode.getName())) {
                return currentNode;
            }

            for (utilities.Edge edge : currentNode.getEdges()) {
                Node neighbor = edge.getTarget();

                if (!visited.contains(neighbor)) {
                    neighbor.setDistance(currentNode.getDistance() + 1);
                    neighbor.setPrevious(currentNode);
                    queue.add(neighbor);
                    visited.add(neighbor);
                }
            }
        }

        return null; // Узел не найден
    }
}
