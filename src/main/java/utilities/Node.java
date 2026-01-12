package utilities;

import java.util.ArrayList;
import java.util.List;

public class Node implements Comparable<Node> {
    private final String name; // Для наглядности используем имя
    private List<Edge> edges = new ArrayList<>();
    private int distance = Integer.MAX_VALUE; // Начальное расстояние — бесконечность
    private Node previous = null; // Для восстановления пути

    public Node(String name) {
        this.name = name;
    }

    public void addEdge(Node target, int weight) {
        edges.add(new Edge(target, weight));
    }

    // Метод для сравнения узлов в PriorityQueue по дистанции
    @Override
    public int compareTo(Node other) {
        return Integer.compare(this.distance, other.distance);
    }

    // Геттеры и сеттеры
    public int getDistance() { return distance; }
    public void setDistance(int distance) { this.distance = distance; }
    public List<Edge> getEdges() { return edges; }
    public Node getPrevious() { return previous; }
    public void setPrevious(Node previous) { this.previous = previous; }
    public String getName() { return name; }
}
