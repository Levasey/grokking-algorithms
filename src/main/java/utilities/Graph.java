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
        source.addChild(target);
        target.addChild(source);
    }
}
