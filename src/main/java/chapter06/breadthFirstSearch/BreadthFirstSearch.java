package chapter06.breadthFirstSearch;

import utilities.Node;

import java.util.*;

public class BreadthFirstSearch {
    public static void search(Node startNode) {
        Queue<Node> queue = new LinkedList<>();
        startNode.setVisited(true);
        queue.offer(startNode);

        while (!queue.isEmpty()) {
            Node currentNode = queue.poll();
            System.out.println("Current node: " + currentNode);

            for (Node neighbor : currentNode.getChildren()) {
                if (!neighbor.isVisited()) {
                    neighbor.setVisited(true);
                    queue.offer(neighbor);
                }
            }
        }



    }
}
