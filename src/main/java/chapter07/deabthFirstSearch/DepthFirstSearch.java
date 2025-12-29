package chapter07.deabthFirstSearch;

import utilities.Node;

import java.util.List;

public class DepthFirstSearch {
    // Рекурсивный DFS для графа в виде списка смежности (List<List<Integer>>)
    public static void dfs(int node, List<List<Integer>> graph, boolean[] visited) {
        // Помечаем текущую вершину как посещенную
        visited[node] = true;
        System.out.print(node + " ");

        // Рекурсивно посещаем всех соседей
        for (int neighbor : graph.get(node)) {
            if (!visited[neighbor]) {
                dfs(neighbor, graph, visited);
            }
        }
    }

    // Рекурсивный DFS для объектов Node
    public static void dfsRecursive(Node node) {
        if (node == null || node.isVisited()) {
            return;
        }

        // Помечаем текущую вершину как посещенную
        node.setVisited(true);
        System.out.print(node.getValue() + " ");

        // Рекурсивно посещаем всех соседей
        for (Node child : node.getChildren()) {
            if (!child.isVisited()) {
                dfsRecursive(child);
            }
        }
    }
}
