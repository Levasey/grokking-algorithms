package utilities;

import java.util.ArrayList;
import java.util.List;

public class Node {
    private final int value;
    private boolean visited;
    private List<Node> children;

    public Node(int value) {
        this.value = value;
        this.visited = false;
        this.children = new ArrayList<Node>();
    }

    public void addChild(Node child) {
        this.children.add(child);
    }

    public int getValue() {
        return value;
    }
    public boolean isVisited() {
        return visited;
    }
    public void setVisited(boolean visited) {
        this.visited = visited;
    }
    public List<Node> getChildren() {
        return children;
    }
}
