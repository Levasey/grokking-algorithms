package utilities;

public class Edge {
    private final Node target;
    private final int weight;

    public Edge(Node target, int weight) {
        this.target = target;
        this.weight = weight;
    }

    public Node getTarget() { return target; }
    public int getWeight() { return weight; }
}
