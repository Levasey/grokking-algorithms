package utilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Node implements Comparable<Node> {
    /**
     * Значение {@link #distance} для вершины до поиска путей или если вершина из источника недостижима.
     * Сравнивайте через {@code ==} или используйте после явного сброса алгоритмами ({@link #resetPathfindingState()}).
     */
    public static final long UNREACHABLE = Long.MAX_VALUE;

    private final String name; // Для наглядности используем имя
    private List<Edge> edges = new ArrayList<>();
    private long distance = UNREACHABLE;
    private Node previous = null; // Для восстановления пути

    public Node(String name) {
        this.name = name;
    }

    public void addEdge(Node target, int weight) {
        edges.add(new Edge(target, weight));
    }

    /**
     * Сбрасывает поля, которые алгоритмы на графах используют для результата (расстояние, предшественник).
     * Удобно перед повторным запуском, если ваш обход не делает полный сброс сам.
     */
    public void resetPathfindingState() {
        this.distance = UNREACHABLE;
        this.previous = null;
    }

    // Метод для сравнения узлов в PriorityQueue по дистанции
    @Override
    public int compareTo(Node other) {
        return Long.compare(this.distance, other.distance);
    }

    // Геттеры и сеттеры
    public long getDistance() { return distance; }
    public void setDistance(long distance) { this.distance = distance; }
    public List<Edge> getEdges() {
        return Collections.unmodifiableList(edges);
    }
    public Node getPrevious() { return previous; }
    public void setPrevious(Node previous) { this.previous = previous; }
    public String getName() { return name; }
}
