package chapter09.bellmanFord;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import utilities.Graph;
import utilities.Node;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BellmanFordTest {

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
    @DisplayName("Без отрицательных рёбер совпадает с ожидаемыми расстояниями (тот же пример, что у Дейкстры)")
    void shortestDistancesPositiveWeights() {
        graph.addEdge(a, b, 6);
        graph.addEdge(a, c, 2);
        graph.addEdge(b, c, 3);
        graph.addEdge(b, d, 1);
        graph.addEdge(c, d, 5);

        assertTrue(BellmanFord.calculate(graph, a));

        assertEquals(0, a.getDistance());
        assertEquals(5, b.getDistance());
        assertEquals(2, c.getDistance());
        assertEquals(6, d.getDistance());
    }

    @Test
    @DisplayName("Отрицательное ребро без отрицательного цикла: корректные расстояния")
    void negativeEdgeNoCycle() {
        Node s = new Node("S");
        Node t = new Node("T");
        Graph g = new Graph();
        g.addNode(s);
        g.addNode(t);
        s.addEdge(t, -2);

        assertTrue(BellmanFord.calculate(g, s));
        assertEquals(0, s.getDistance());
        assertEquals(-2, t.getDistance());
    }

    @Test
    @DisplayName("Достижимый отрицательный цикл: алгоритм сообщает о нём")
    void negativeCycleDetected() {
        Node x = new Node("X");
        Node y = new Node("Y");
        Graph g = new Graph();
        g.addNode(x);
        g.addNode(y);
        x.addEdge(y, 1);
        y.addEdge(x, -3);

        assertFalse(BellmanFord.calculate(g, x));
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

        assertTrue(BellmanFord.calculate(chain, start));

        List<Node> path = BellmanFord.getShortestPathTo(end);
        assertEquals(3, path.size());
        assertEquals(start, path.get(0));
        assertEquals(mid, path.get(1));
        assertEquals(end, path.get(2));
    }

    @Test
    @DisplayName("Повторный запуск с другого источника сбрасывает недостижимые узлы")
    void secondRunResetsUnreachableComponent() {
        Node x = new Node("X");
        Node y = new Node("Y");
        graph.addNode(x);
        graph.addNode(y);
        graph.addEdge(x, y, 1);
        graph.addEdge(a, b, 1);

        assertTrue(BellmanFord.calculate(graph, x));
        assertEquals(0, x.getDistance());
        assertEquals(1, y.getDistance());
        assertEquals(Node.UNREACHABLE, a.getDistance());

        assertTrue(BellmanFord.calculate(graph, a));
        assertEquals(0, a.getDistance());
        assertEquals(1, b.getDistance());
        assertEquals(Node.UNREACHABLE, x.getDistance());
        assertEquals(Node.UNREACHABLE, y.getDistance());
    }
}
