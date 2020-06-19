package com.myweb.utility.test.learning;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Path Finding Algorithm in barriers
 * 
 * @author jegatheesh.mageswaran <br>
 *         Created on <b>19-Jun-2020</b>
 *
 */
public class PathFindingAlgorithmTwo {
	public static void main(String[] args) {
		PathFindingAlgorithmTwo self = new PathFindingAlgorithmTwo();
		boolean[][] visited = new boolean[10][10];
		boolean[][] barriers = new boolean[10][10];
		barriers[8][6] = true;
		barriers[9][4] = true;
		barriers[9][7] = true;
		self.findPath(new int[] { 5, 1 }, new int[] { 9, 6 }, new int[] { 10, 10 }, visited, barriers);
	}

	void findPath(int[] startPoint, int[] targetPoint, int[] grid, boolean[][] visited, boolean[][] barriers) {
		Node startNode = new Node(startPoint[0], startPoint[1], 0);
		Queue<Node> queue = new LinkedList<>();
		addToQueue(queue, startNode, grid, visited, barriers);
		searchNeighbours(queue, targetPoint, grid, visited, barriers);
	}

	void searchNeighbours(Queue<Node> queue, int[] targetPoint, int[] grid, boolean[][] visited, boolean[][] barriers) {
		if (queue.isEmpty()) {
			return;
		}
		Node current = queue.poll();
		System.out.println("Current " + current);
		// marking visited
		visited[current.x][current.y] = true;
		// checking current point is target
		if (current.x == targetPoint[0] && current.y == targetPoint[1]) {
			System.out.println("path found at distance " + current.distanceFromStart);
			printRoute(current);
		} else {
			// Adding neighbours to queue
			addNeighbours(queue, current, grid, visited, barriers);
			searchNeighbours(queue, targetPoint, grid, visited, barriers);
		}
	}

	private void addNeighbours(Queue<Node> queue, Node current, int[] grid, boolean[][] visited, boolean[][] barriers) {
		// top
		addToQueue(queue, new Node(current.x + 1, current.y, current.distanceFromStart + 1, current), grid, visited, barriers);
		// down
		addToQueue(queue, new Node(current.x - 1, current.y, current.distanceFromStart + 1, current), grid, visited, barriers);
		// right
		addToQueue(queue, new Node(current.x, current.y + 1, current.distanceFromStart + 1, current), grid, visited, barriers);
		// left
		addToQueue(queue, new Node(current.x, current.y - 1, current.distanceFromStart + 1, current), grid, visited, barriers);
	}

	void addToQueue(Queue<Node> queue, Node node, int[] grid, boolean[][] visited, boolean[][] barriers) {
		int x = grid[0];
		int y = grid[1];
		// boundary & visited & barrier check
		if (node.x >= 0 && node.x < x && node.y >= 0 && node.y < y && !visited[node.x][node.y] && !barriers[node.x][node.y]) {
			queue.add(node);
			System.out.println("Added " + node);
		}
	}

	private void printRoute(Node current) {
		System.out.print("[ " + current.x + ", " + current.y + "] ");
		if(current.previousNode != null) {
			printRoute(current.previousNode);
		}
	}
}
