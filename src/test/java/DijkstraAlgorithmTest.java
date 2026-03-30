import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import utilities.Graph;
import utilities.Node;

import java.util.Arrays;
import java.util.List;

import static chapter09.dijkstraAlgorithm.DijkstraAlgorithm.calculate;
import static chapter09.dijkstraAlgorithm.DijkstraAlgorithm.getShortestPathTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DijkstraAlgorithmTest {

    private Node a;
    private Node b;
    private Node c;
    private Node d;
    private Graph graph;

    @BeforeEach
    void setUp() {
        a = new Node("A");
        b = new Node("B");
        c = new Node("C");
        d = new Node("D");

        graph = new Graph();
        for (Node n : Arrays.asList(a, b, c, d)) {
            graph.addNode(n);
        }
    }

    @Test
    @DisplayName("Кратчайшие расстояния: классический пример с обходом через промежуточную вершину")
    void shortestDistancesPreferCheaperRoute() {
        graph.addEdge(a, b, 6);
        graph.addEdge(a, c, 2);
        graph.addEdge(b, c, 3);
        graph.addEdge(b, d, 1);
        graph.addEdge(c, d, 5);

        calculate(graph, a);

        assertEquals(0, a.getDistance());
        assertEquals(5, b.getDistance());
        assertEquals(2, c.getDistance());
        assertEquals(6, d.getDistance());
    }

    @Test
    @DisplayName("Восстановление кратчайшего пути")
    void rebuildsShortestPath() {
        Node start = new Node("S");
        Node mid = new Node("M");
        Node end = new Node("E");
        Graph chain = new Graph();
        chain.addNode(start);
        chain.addNode(mid);
        chain.addNode(end);
        chain.addEdge(start, mid, 1);
        chain.addEdge(mid, end, 1);
        chain.addEdge(start, end, 10);

        calculate(chain, start);

        List<Node> path = getShortestPathTo(end);
        assertEquals(3, path.size());
        assertEquals(start, path.get(0));
        assertEquals(mid, path.get(1));
        assertEquals(end, path.get(2));
    }

    @Test
    @DisplayName("Повторный запуск с другого источника сбрасывает недостижимые узлы другой компоненты")
    void secondRunResetsUnreachableComponent() {
        Node x = new Node("X");
        Node y = new Node("Y");
        graph.addNode(x);
        graph.addNode(y);
        graph.addEdge(x, y, 1);
        graph.addEdge(a, b, 1);

        calculate(graph, x);
        assertEquals(0, x.getDistance());
        assertEquals(1, y.getDistance());
        assertEquals(Integer.MAX_VALUE, a.getDistance());

        calculate(graph, a);
        assertEquals(0, a.getDistance());
        assertEquals(1, b.getDistance());
        assertEquals(Integer.MAX_VALUE, x.getDistance());
        assertEquals(Integer.MAX_VALUE, y.getDistance());
    }

    @Test
    @DisplayName("Повторный запуск не оставляет устаревший previous у узла вне нового обхода")
    void stalePreviousClearedOnDisconnectedSecondRun() {
        graph.addEdge(a, b, 1);
        Node orphan = new Node("orphan");
        graph.addNode(orphan);

        calculate(graph, a);
        orphan.setPrevious(a);

        calculate(graph, b);

        assertEquals(1, a.getDistance());
        assertEquals(0, b.getDistance());
        assertEquals(Integer.MAX_VALUE, orphan.getDistance());
        assertTrue(orphan.getPrevious() == null);
    }
}
