import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import utilities.Graph;
import utilities.Node;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

import static chapter07.depthFirstSearch.DepthFirstSearch.performRecursiveDFS;
import static org.junit.jupiter.api.Assertions.*;

class DepthFirstSearchTest {

    private static void withCapturedOut(Runnable body, java.util.function.Consumer<String> assertOutput) {
        PrintStream original = System.out;
        try {
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            System.setOut(new PrintStream(buf));
            body.run();
            assertOutput.accept(buf.toString());
        } finally {
            System.setOut(original);
        }
    }

    @Test
    @DisplayName("DFS с null-графом")
    void nullGraphMessage() {
        withCapturedOut(
                () -> performRecursiveDFS((Graph) null, new Node("n")),
                out -> assertTrue(out.contains("Граф не может быть null")));
    }

    @Test
    @DisplayName("DFS с null-коллекцией узлов")
    void nullCollectionMessage() {
        withCapturedOut(
                () -> performRecursiveDFS((List<Node>) null, new Node("n")),
                out -> assertTrue(out.contains("Коллекция узлов не может быть null")));
    }

    @Test
    @DisplayName("DFS с null стартовым узлом")
    void nullStartMessage() {
        Graph g = new Graph();
        Node a = new Node("A");
        g.addNode(a);
        withCapturedOut(
                () -> performRecursiveDFS(g, null),
                out -> assertTrue(out.contains("Стартовый узел не может быть null")));
    }

    @Test
    @DisplayName("Старт не из коллекции узлов")
    void startNotInAllNodesMessage() {
        Node a = new Node("A");
        Node b = new Node("B");
        withCapturedOut(
                () -> performRecursiveDFS(List.of(a), b),
                out -> assertTrue(out.contains("Стартовый узел должен входить в коллекцию всех узлов графа")));
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

        withCapturedOut(() -> performRecursiveDFS(g, a), out ->
                assertTrue(out.contains("Всего посещено узлов: 2")));

        assertEquals(0, a.getDistance());
        assertEquals(1, b.getDistance());
        assertNotNull(b.getPrevious());
        assertEquals(Integer.MAX_VALUE, x.getDistance());
        assertNull(x.getPrevious());
        assertEquals(Integer.MAX_VALUE, y.getDistance());
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

        withCapturedOut(() -> performRecursiveDFS(g, a), out ->
                assertTrue(out.contains("Всего посещено узлов: 2")));
        assertEquals(0, a.getDistance());

        withCapturedOut(() -> performRecursiveDFS(g, x), out ->
                assertTrue(out.contains("Всего посещено узлов: 2")));

        assertEquals(Integer.MAX_VALUE, a.getDistance());
        assertNull(a.getPrevious());
        assertEquals(Integer.MAX_VALUE, b.getDistance());
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

        String outGraph;
        PrintStream original = System.out;
        try {
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            System.setOut(new PrintStream(buf));
            performRecursiveDFS(g, a);
            outGraph = buf.toString();
        } finally {
            System.setOut(original);
        }

        b.setDistance(99);
        b.setPrevious(null);

        String outList;
        try {
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            System.setOut(new PrintStream(buf));
            performRecursiveDFS(g.getNodes(), a);
            outList = buf.toString();
        } finally {
            System.setOut(original);
        }

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

        withCapturedOut(() -> performRecursiveDFS(graph, nodeA), out -> {
            assertTrue(out.contains("Всего посещено узлов: 3"));
            for (Node n : Arrays.asList(nodeA, nodeB, nodeC)) {
                assertTrue(out.contains("Узел: " + n.getName()));
            }
        });
    }
}
