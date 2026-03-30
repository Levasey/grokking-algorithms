package utilities;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public class Edge {
    private final Node target;
    private final int weight;

    @SuppressFBWarnings(value = "EI_EXPOSE_REP2",
            justification = "Ребро хранит ссылку на вершину графа по определению структуры.")
    public Edge(Node target, int weight) {
        this.target = target;
        this.weight = weight;
    }

    @SuppressFBWarnings(value = "EI_EXPOSE_REP",
            justification = "Ребро хранит ссылку на вершину графа по определению структуры.")
    public Node getTarget() { return target; }
    public int getWeight() { return weight; }
}
