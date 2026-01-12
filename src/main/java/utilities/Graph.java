package utilities;

import java.util.LinkedList;
import java.util.List;

public class Graph {
    private final List<Node> nodes;

    public Graph() {
        nodes = new LinkedList<>();
    }

    public void addNode(Node node) {
        nodes.add(node);
    }

    public void addEdge(Node source, Node target) {
        // Используем правильный метод из класса Node
        source.addEdge(target, 1); // Вес по умолчанию 1 для невзвешенных графов
        target.addEdge(source, 1); // Для неориентированного графа
    }

    public void addEdge(Node source, Node target, int weight) {
        source.addEdge(target, weight);
        target.addEdge(source, weight); // Для неориентированного графа
    }

    public List<Node> getNodes() {
        return nodes;
    }
}