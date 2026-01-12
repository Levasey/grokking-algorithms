package chapter06.breadthFirstSearch;

import utilities.Node;

import java.util.*;

public class BreadthFirstSearch {
    public static void performBFS(Node startNode) {
        // Проверяем, что стартовый узел не null
        if (startNode == null) {
            System.out.println("Стартовый узел не может быть null");
            return;
        }

        // Создаем очередь для BFS
        Queue<Node> queue = new LinkedList<>();

        // Создаем множество для отслеживания посещенных узлов
        Set<Node> visited = new HashSet<>();

        // Инициализируем стартовый узел
        startNode.setDistance(0);
        startNode.setPrevious(null);

        // Добавляем стартовый узел в очередь и отмечаем как посещенный
        queue.add(startNode);
        visited.add(startNode);

        System.out.println("Порядок обхода BFS:");
        int step = 1;

        // Основной цикл BFS
        while (!queue.isEmpty()) {
            Node currentNode = queue.poll();

            // Выводим информацию о текущем узле
            System.out.printf("%d. Узел: %s, Расстояние от старта: %d%n",
                    step++,
                    currentNode.getName(),
                    currentNode.getDistance());

            // Обрабатываем всех соседей текущего узла
            for (utilities.Edge edge : currentNode.getEdges()) {
                Node neighbor = edge.getTarget();

                // Если сосед еще не посещен
                if (!visited.contains(neighbor)) {
                    // Обновляем расстояние и предыдущий узел
                    neighbor.setDistance(currentNode.getDistance() + 1);
                    neighbor.setPrevious(currentNode);

                    // Добавляем в очередь и отмечаем как посещенный
                    queue.add(neighbor);
                    visited.add(neighbor);
                }
            }
        }

        System.out.println("\nBFS завершен. Всего посещено узлов: " + visited.size());
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

        // Инициализация
        startNode.setDistance(0);
        startNode.setPrevious(null);
        queue.add(startNode);
        visited.add(startNode);

        while (!queue.isEmpty()) {
            Node currentNode = queue.poll();

            // Если нашли целевой узел
            if (targetName.equals(currentNode.getName())) {
                return currentNode;
            }

            // Обрабатываем соседей
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