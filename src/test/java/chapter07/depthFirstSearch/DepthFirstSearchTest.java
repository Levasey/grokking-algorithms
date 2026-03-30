package chapter07.depthFirstSearch;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import utilities.Graph;
import utilities.Node;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static chapter07.depthFirstSearch.DepthFirstSearch.performRecursiveDFS;
import static org.junit.jupiter.api.Assertions.*;

class DepthFirstSearchTest {

    private static String joinLog(List<String> lines) {
        return String.join("\n", lines);
    }

    @Test
    @DisplayName("DFS с null-графом")
    void nullGraphMessage() {
        List<String> lines = new ArrayList<>();
        assertEquals(0, performRecursiveDFS((Graph) null, new Node("n"), lines::add));
        assertTrue(joinLog(lines).contains("Граф не может быть null"));
    }

    @Test
    @DisplayName("DFS с null-коллекцией узлов")
    void nullCollectionMessage() {
        List<String> lines = new ArrayList<>();
        assertEquals(0, performRecursiveDFS((List<Node>) null, new Node("n"), lines::add));
        assertTrue(joinLog(lines).contains("Коллекция узлов не может быть null"));
    }

    @Test
    @DisplayName("DFS с null стартовым узлом")
    void nullStartMessage() {
        Graph g = new Graph();
        Node a = new Node("A");
        g.addNode(a);
        List<String> lines = new ArrayList<>();
        assertEquals(0, performRecursiveDFS(g, null, lines::add));
        assertTrue(joinLog(lines).contains("Стартовый узел не может быть null"));
    }

    @Test
    @DisplayName("Старт не из коллекции узлов")
    void startNotInAllNodesMessage() {
        Node a = new Node("A");
        Node b = new Node("B");
        List<String> lines = new ArrayList<>();
        assertEquals(0, performRecursiveDFS(List.of(a), b, lines::add));
        assertTrue(joinLog(lines).contains("Стартовый узел должен входить в коллекцию всех узлов графа"));
    }

    @Test
    @DisplayName("Непосещённая компонента: distance/previous сброшены, не остаются старыми")
    void unvisitedComponentResetToSentinel() {
        Node a = new Node("A");
        Node b = new Node("B");
        Node x = new Node("X");
        Node y = new Node("Y");
        Graph g = new Graph();
        g.addNode(a);
        g.addNode(b);
        g.addNode(x);
        g.addNode(y);
        g.addEdge(a, b);
        g.addEdge(x, y);

        x.setDistance(3);
        x.setPrevious(a);
        y.setDistance(4);
        y.setPrevious(x);

        List<String> lines = new ArrayList<>();
        assertEquals(2, performRecursiveDFS(g, a, lines::add));
        assertTrue(joinLog(lines).contains("Всего посещено узлов: 2"));

        assertEquals(0, a.getDistance());
        assertEquals(1, b.getDistance());
        assertNotNull(b.getPrevious());
        assertEquals(Node.UNREACHABLE, x.getDistance());
        assertNull(x.getPrevious());
        assertEquals(Node.UNREACHABLE, y.getDistance());
        assertNull(y.getPrevious());
    }

    @Test
    @DisplayName("Повторный DFS с другой компоненты очищает поля у ранее посещённых узлов")
    void secondDfsFromOtherComponentClearsFirst() {
        Node a = new Node("A");
        Node b = new Node("B");
        Node x = new Node("X");
        Node y = new Node("Y");
        Graph g = new Graph();
        g.addNode(a);
        g.addNode(b);
        g.addNode(x);
        g.addNode(y);
        g.addEdge(a, b);
        g.addEdge(x, y);

        List<String> lines1 = new ArrayList<>();
        assertEquals(2, performRecursiveDFS(g, a, lines1::add));
        assertTrue(joinLog(lines1).contains("Всего посещено узлов: 2"));
        assertEquals(0, a.getDistance());

        List<String> lines2 = new ArrayList<>();
        assertEquals(2, performRecursiveDFS(g, x, lines2::add));
        assertTrue(joinLog(lines2).contains("Всего посещено узлов: 2"));

        assertEquals(Node.UNREACHABLE, a.getDistance());
        assertNull(a.getPrevious());
        assertEquals(Node.UNREACHABLE, b.getDistance());
        assertNull(b.getPrevious());

        assertEquals(0, x.getDistance());
        assertEquals(1, y.getDistance());
        assertEquals(x, y.getPrevious());
    }

    @Test
    @DisplayName("Перегрузка с коллекцией узлов эквивалентна вызову с Graph")
    void collectionOverloadMatchesGraph() {
        Node a = new Node("A");
        Node b = new Node("B");
        Graph g = new Graph();
        g.addNode(a);
        g.addNode(b);
        g.addEdge(a, b);

        List<String> fromGraph = new ArrayList<>();
        assertEquals(2, performRecursiveDFS(g, a, fromGraph::add));
        String outGraph = joinLog(fromGraph);

        b.setDistance(99);
        b.setPrevious(null);

        List<String> fromList = new ArrayList<>();
        assertEquals(2, performRecursiveDFS(g.getNodes(), a, fromList::add));
        String outList = joinLog(fromList);

        assertEquals(outGraph, outList);
        assertEquals(0, a.getDistance());
        assertEquals(1, b.getDistance());
    }

    @Test
    @DisplayName("DFS обходит все узлы связного графа")
    void visitsAllNodesInConnectedGraph() {
        Node nodeA = new Node("A");
        Node nodeB = new Node("B");
        Node nodeC = new Node("C");
        Graph graph = new Graph();
        graph.addNode(nodeA);
        graph.addNode(nodeB);
        graph.addNode(nodeC);
        graph.addEdge(nodeA, nodeB);
        graph.addEdge(nodeB, nodeC);

        List<String> lines = new ArrayList<>();
        assertEquals(3, performRecursiveDFS(graph, nodeA, lines::add));
        String out = joinLog(lines);
        assertTrue(out.contains("Всего посещено узлов: 3"));
        for (Node n : Arrays.asList(nodeA, nodeB, nodeC)) {
            assertTrue(out.contains("Узел: " + n.getName()));
        }
    }
}
