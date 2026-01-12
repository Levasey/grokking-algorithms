import org.junit.jupiter.api.*;
import utilities.Node;
import utilities.Graph;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static chapter06.breadthFirstSearch.BreadthFirstSearch.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BreadthFirstSearchTest {

    private Node nodeA, nodeB, nodeC, nodeD, nodeE, nodeF;
    private Graph graph;

    @BeforeEach
    void setUp() {
        // Создаем тестовый граф:
        //     A
        //    / \
        //   B   C
        //  / \   \
        // D   E - F

        nodeA = new Node("A");
        nodeB = new Node("B");
        nodeC = new Node("C");
        nodeD = new Node("D");
        nodeE = new Node("E");
        nodeF = new Node("F");

        graph = new Graph();
        graph.addNode(nodeA);
        graph.addNode(nodeB);
        graph.addNode(nodeC);
        graph.addNode(nodeD);
        graph.addNode(nodeE);
        graph.addNode(nodeF);

        graph.addEdge(nodeA, nodeB);
        graph.addEdge(nodeA, nodeC);
        graph.addEdge(nodeB, nodeD);
        graph.addEdge(nodeB, nodeE);
        graph.addEdge(nodeC, nodeF);
        graph.addEdge(nodeE, nodeF); // Создаем цикл
    }

    @AfterEach
    void tearDown() {
        // Сбрасываем состояние узлов для каждого теста
        resetNodes();
    }

    private void resetNodes() {
        for (Node node : Arrays.asList(nodeA, nodeB, nodeC, nodeD, nodeE, nodeF)) {
            node.setDistance(Integer.MAX_VALUE);
            node.setPrevious(null);
        }
    }

    @Test
    @DisplayName("Тест BFS с null начальным узлом")
    void testBFSWithNullStartNode() {
        // Capture System.out for testing
        final java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(outContent));

        performBFS(null);

        assertTrue(outContent.toString().contains("Стартовый узел не может быть null"));
        System.setOut(System.out); // Reset System.out
    }

    @Test
    @DisplayName("Тест BFS на графе с одним узлом")
    void testBFSWithSingleNode() {
        Node singleNode = new Node("Single");

        final java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(outContent));

        performBFS(singleNode);

        String output = outContent.toString();
        assertTrue(output.contains("Узел: Single"));
        assertTrue(output.contains("Расстояние от старта: 0"));
        assertTrue(output.contains("Всего посещено узлов: 1"));

        System.setOut(System.out);
    }

    @Test
    @DisplayName("Тест BFS обходит все узлы в графе")
    void testBFSVisitsAllNodes() {
        final java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(outContent));

        performBFS(nodeA);

        String output = outContent.toString();

        // Проверяем, что все узлы были посещены
        assertTrue(output.contains("Узел: A"));
        assertTrue(output.contains("Узел: B"));
        assertTrue(output.contains("Узел: C"));
        assertTrue(output.contains("Узел: D"));
        assertTrue(output.contains("Узел: E"));
        assertTrue(output.contains("Узел: F"));

        // Проверяем общее количество посещенных узлов
        assertTrue(output.contains("Всего посещено узлов: 6"));

        System.setOut(System.out);
    }

    @Test
    @DisplayName("Тест BFS устанавливает правильные расстояния")
    void testBFSSetsCorrectDistances() {
        // Выполняем BFS
        performBFS(nodeA);

        // Проверяем расстояния
        assertEquals(0, nodeA.getDistance());
        assertEquals(1, nodeB.getDistance());
        assertEquals(1, nodeC.getDistance());
        assertEquals(2, nodeD.getDistance());
        assertEquals(2, nodeE.getDistance());
        assertEquals(2, nodeF.getDistance()); // F может быть достигнут через C или E
    }

    @Test
    @DisplayName("Тест BFS устанавливает правильные предыдущие узлы")
    void testBFSSetsCorrectPreviousNodes() {
        performBFS(nodeA);

        // Проверяем предыдущие узлы
        assertNull(nodeA.getPrevious());
        assertEquals(nodeA, nodeB.getPrevious());
        assertEquals(nodeA, nodeC.getPrevious());
        assertEquals(nodeB, nodeD.getPrevious());
        assertEquals(nodeB, nodeE.getPrevious());
        // F может быть достигнут через C или E - зависит от порядка обхода
        assertTrue(nodeF.getPrevious() == nodeC || nodeF.getPrevious() == nodeE);
    }

    @Test
    @DisplayName("Тест восстановления пути")
    void testGetPathTo() {
        performBFS(nodeA);

        // Путь от A к D
        List<Node> pathToD = getPathTo(nodeD);
        assertEquals(3, pathToD.size());
        assertEquals(nodeA, pathToD.get(0));
        assertEquals(nodeB, pathToD.get(1));
        assertEquals(nodeD, pathToD.get(2));

        // Путь от A к A
        List<Node> pathToA = getPathTo(nodeA);
        assertEquals(1, pathToA.size());
        assertEquals(nodeA, pathToA.get(0));

        // Путь к непосещенному узлу
        Node unvisitedNode = new Node("Unvisited");
        List<Node> pathToUnvisited = getPathTo(unvisitedNode);
        assertTrue(pathToUnvisited.isEmpty());
    }

    @Test
    @DisplayName("Тест печати пути")
    void testPrintPath() {
        performBFS(nodeA);

        final java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(outContent));

        printPath(nodeD);

        String output = outContent.toString();
        assertTrue(output.contains("Путь от старта к узлу D (длина: 2):"));
        assertTrue(output.contains("A -> B -> D"));

        System.setOut(System.out);
    }

    @Test
    @DisplayName("Тест печати пути к непосещенному узлу")
    void testPrintPathToUnvisitedNode() {
        Node unvisitedNode = new Node("Unvisited");

        final java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(outContent));

        printPath(unvisitedNode);

        String output = outContent.toString();
        assertTrue(output.contains("Путь не найден"));

        System.setOut(System.out);
    }

    @Test
    @DisplayName("Тест поиска узла BFS")
    void testFindNodeBFS() {
        // Поиск существующего узла
        Node foundNode = findNodeBFS(nodeA, "D");
        assertNotNull(foundNode);
        assertEquals("D", foundNode.getName());
        assertEquals(2, foundNode.getDistance());

        // Поиск стартового узла
        Node foundStartNode = findNodeBFS(nodeA, "A");
        assertNotNull(foundStartNode);
        assertEquals("A", foundStartNode.getName());
        assertEquals(0, foundStartNode.getDistance());

        // Поиск несуществующего узла
        Node notFoundNode = findNodeBFS(nodeA, "NonExistent");
        assertNull(notFoundNode);

        // Поиск с null параметрами
        assertNull(findNodeBFS(null, "A"));
        assertNull(findNodeBFS(nodeA, null));
    }

    @Test
    @DisplayName("Тест BFS на несвязном графе")
    void testBFSOnDisconnectedGraph() {
        // Создаем несвязный граф
        Node isolated1 = new Node("Isolated1");
        Node isolated2 = new Node("Isolated2");

        Graph disconnectedGraph = new Graph();
        disconnectedGraph.addNode(isolated1);
        disconnectedGraph.addNode(isolated2);

        final java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(outContent));

        performBFS(isolated1);

        String output = outContent.toString();
        assertTrue(output.contains("Узел: Isolated1"));
        assertFalse(output.contains("Узел: Isolated2")); // Не должен быть посещен
        assertTrue(output.contains("Всего посещено узлов: 1"));

        System.setOut(System.out);
    }

    @Test
    @DisplayName("Тест BFS на полном графе (Complete Graph)")
    void testBFSOnCompleteGraph() {
        // Создаем полный граф K3
        Node k1 = new Node("K1");
        Node k2 = new Node("K2");
        Node k3 = new Node("K3");

        Graph completeGraph = new Graph();
        completeGraph.addNode(k1);
        completeGraph.addNode(k2);
        completeGraph.addNode(k3);

        completeGraph.addEdge(k1, k2);
        completeGraph.addEdge(k1, k3);
        completeGraph.addEdge(k2, k3);

        performBFS(k1);

        assertEquals(0, k1.getDistance());
        assertEquals(1, k2.getDistance());
        assertEquals(1, k3.getDistance());

        // Проверяем предыдущие узлы
        assertEquals(k1, k2.getPrevious());
        assertEquals(k1, k3.getPrevious());
    }

    private Node findNodeByName(Node startNode, String name) {
        // Выполняем поиск узла по имени с помощью BFS
        return findNodeBFS(startNode, name);
    }

    @Test
    @DisplayName("Тест BFS на графе с весом ребер")
    void testBFSOnWeightedGraph() {
        // BFS игнорирует веса, тестируем это поведение
        Node w1 = new Node("W1");
        Node w2 = new Node("W2");
        Node w3 = new Node("W3");

        // Добавляем ребра с разными весами
        w1.addEdge(w2, 10); // Вес 10
        w1.addEdge(w3, 1);  // Вес 1

        // BFS найдет путь через любое ребро, независимо от веса
        performBFS(w1);

        // Оба соседа должны быть на расстоянии 1
        assertEquals(1, w2.getDistance());
        assertEquals(1, w3.getDistance());
    }

    @Test
    @DisplayName("Тест BFS сохраняет порядок обхода по уровням")
    void testBFSLevelOrder() {
        // Граф с четкой структурой уровней:
        //     A
        //    / \
        //   B   C
        //   |   |
        //   D   E

        Node a = new Node("A");
        Node b = new Node("B");
        Node c = new Node("C");
        Node d = new Node("D");
        Node e = new Node("E");

        Graph levelGraph = new Graph();
        levelGraph.addNode(a);
        levelGraph.addNode(b);
        levelGraph.addNode(c);
        levelGraph.addNode(d);
        levelGraph.addNode(e);

        levelGraph.addEdge(a, b);
        levelGraph.addEdge(a, c);
        levelGraph.addEdge(b, d);
        levelGraph.addEdge(c, e);

        final java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(outContent));

        performBFS(a);

        String output = outContent.toString();

        // Проверяем порядок вывода (примерный паттерн)
        // A (уровень 0), затем B и C (уровень 1), затем D и E (уровень 2)
        int indexA = output.indexOf("Узел: A");
        int indexB = output.indexOf("Узел: B");
        int indexC = output.indexOf("Узел: C");
        int indexD = output.indexOf("Узел: D");
        int indexE = output.indexOf("Узел: E");

        // A должен быть первым
        assertTrue(indexA < indexB && indexA < indexC);

        // B и C должны быть после A, но до D и E
        assertTrue(indexB > indexA && indexC > indexA);
        assertTrue(indexB < indexD && indexC < indexE);

        System.setOut(System.out);
    }

    @Test
    @DisplayName("Тест BFS на большом графе")
    void testBFSOnLargeGraph() {
        // Создаем линейный граф из 100 узлов
        List<Node> nodes = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            nodes.add(new Node("Node" + i));
        }

        Graph linearGraph = new Graph();
        for (Node node : nodes) {
            linearGraph.addNode(node);
        }

        // Соединяем последовательно
        for (int i = 0; i < nodes.size() - 1; i++) {
            linearGraph.addEdge(nodes.get(i), nodes.get(i + 1));
        }

        final java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(outContent));

        performBFS(nodes.get(0));

        String output = outContent.toString();

        // Проверяем, что все узлы были посещены
        assertTrue(output.contains("Всего посещено узлов: 100"));

        // Проверяем расстояния для последнего узла
        Node lastNode = nodes.get(nodes.size() - 1);
        assertEquals(99, lastNode.getDistance());

        System.setOut(System.out);
    }

    @Test
    @DisplayName("Тест повторного использования BFS")
    void testBFSReuse() {
        // Первый запуск BFS
        performBFS(nodeA);
        int distanceFirst = nodeD.getDistance();

        // Сбрасываем узлы
        resetNodes();

        // Второй запуск BFS с другого узла
        performBFS(nodeD);
        int distanceSecond = nodeA.getDistance();

        // Проверяем симметричность (для неориентированного графа)
        assertEquals(distanceFirst, distanceSecond);
    }

    @Test
    @DisplayName("Тест BFS на графе с петлями")
    void testBFSWithSelfLoops() {
        Node selfLoopNode = new Node("SelfLoop");

        // Добавляем петлю (ребро к самому себе)
        // В текущей реализации addEdge в Graph добавляет ребро в обе стороны,
        // что создаст петлю
        selfLoopNode.addEdge(selfLoopNode, 1);

        final java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(outContent));

        performBFS(selfLoopNode);

        String output = outContent.toString();

        // Узел должен быть посещен только один раз
        int count = countOccurrences(output, "Узел: SelfLoop");
        assertEquals(1, count);

        System.setOut(System.out);
    }

    private int countOccurrences(String text, String pattern) {
        int count = 0;
        int index = 0;
        while ((index = text.indexOf(pattern, index)) != -1) {
            count++;
            index += pattern.length();
        }
        return count;
    }

    @Test
    @DisplayName("Тест производительности BFS")
    void testBFSPerformance() {
        // Тест на время выполнения (не строгий, скорее проверка на отсутствие бесконечного цикла)
        int nodeCount = 1000;
        List<Node> nodes = new ArrayList<>();
        Graph largeGraph = new Graph();

        // Создаем звездообразный граф
        Node center = new Node("Center");
        largeGraph.addNode(center);

        for (int i = 0; i < nodeCount; i++) {
            Node node = new Node("Leaf" + i);
            nodes.add(node);
            largeGraph.addNode(node);
            largeGraph.addEdge(center, node);
        }

        long startTime = System.currentTimeMillis();
        performBFS(center);
        long endTime = System.currentTimeMillis();

        long duration = endTime - startTime;

        // BFS на 1001 узлах должен завершиться быстро
        // Даем запас - 5 секунд должно хватить даже на медленных машинах
        assertTrue(duration < 5000,
                String.format("BFS занял слишком много времени: %d мс", duration));
    }
}