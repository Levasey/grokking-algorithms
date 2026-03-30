package bookdemo;

import chapter01.binarySearch.BinarySearch;
import chapter02.selectionSort.SelectionSort;
import chapter03.recursion.Factorial;
import chapter03.recursion.Fibonacci;
import chapter04.quicksort.DivideAndConquerDemo;
import chapter04.quicksort.QuickSort;
import chapter06.breadthFirstSearch.BreadthFirstSearch;
import utilities.Graph;
import utilities.Node;

import java.util.Arrays;
import java.util.List;

/**
 * Один вход для учебных сценариев в духе книги: последовательные мини-примеры по ранним главам
 * и небольшой граф для BFS. Запуск: {@code mvn -q compile exec:java} или из IDE — этот {@code main}.
 */
public final class BookDemoMain {

    private BookDemoMain() {
    }

    public static void main(String[] args) {
        section('1', "Бинарный поиск");
        int[] sorted = {1, 3, 5, 7, 9};
        int key = 7;
        int idx = BinarySearch.binarySearch(sorted, key);
        System.out.println("arr=" + Arrays.toString(sorted) + ", key=" + key + " → индекс " + idx);

        section('2', "Сортировка выбором");
        int[] messy = {5, 3, 6, 2, 10};
        System.out.println("до:  " + Arrays.toString(messy));
        int[] copy = messy.clone();
        SelectionSort.selectionSort(copy);
        System.out.println("после: " + Arrays.toString(copy));

        section('3', "Рекурсия: факториал и Фибоначчи");
        System.out.println("5! = " + Factorial.factorial(5));
        System.out.println("F(10) (итеративно) = " + Fibonacci.fibonacciIterative(10));

        section('4', "Разделяй и властвуй + быстрая сортировка");
        int[] block = {2, 4, 6, 8, 10, 12, 14};
        System.out.println("сумма через деление пополам: " + DivideAndConquerDemo.divideAndConquer(block));
        int[] qs = {10, 5, 2, 3, 7, 13, 4};
        System.out.println("quicksort до:  " + Arrays.toString(qs));
        QuickSort.quickSort(qs);
        System.out.println("quicksort после: " + Arrays.toString(qs));

        section('6', "Поиск в ширину (малый граф)");
        demoBfs();

        System.out.println("\n--- Готово. Отдельные классы-демо остаются в пакетах глав (например DivideAndConquerDemo). ---");
    }

    private static void section(char chapterDigit, String title) {
        System.out.println("\n=== Глава " + chapterDigit + ": " + title + " ===\n");
    }

    private static void demoBfs() {
        Graph graph = new Graph();
        Node you = new Node("you");
        Node alice = new Node("alice");
        Node bob = new Node("bob");
        Node claire = new Node("claire");
        graph.addNode(you);
        graph.addNode(alice);
        graph.addNode(bob);
        graph.addNode(claire);
        graph.addEdge(you, alice);
        graph.addEdge(you, bob);
        graph.addEdge(alice, claire);
        graph.addEdge(bob, claire);
        List<Node> reset = List.of(you, alice, bob, claire);
        for (Node n : reset) {
            n.resetPathfindingState();
        }
        BreadthFirstSearch.performBFS(you);
    }
}
