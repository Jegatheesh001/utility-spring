package com.myweb.utility.test.learning;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Path Finding Algorithm
 * 
 * @author jegatheesh.mageswaran <br>
 *         Created on <b>19-Jun-2020</b>
 *
 */
public class PathFindingAlgorithm {
	public static void main(String[] args) {
		PathFindingAlgorithm self = new PathFindingAlgorithm();
		boolean[][] visited = new boolean[10][10];
		self.findPath(new int[] { 5, 1 }, new int[] { 9, 6 }, new int[] { 10, 10 }, visited);
	}

	void findPath(int[] startPoint, int[] targetPoint, int[] grid, boolean[][] visited) {
		Node startNode = new Node(startPoint[0], startPoint[1], 0);
		Queue<Node> queue = new LinkedList<>();
		addToQueue(queue, startNode, grid, visited);
		searchNeighbours(queue, targetPoint, grid, visited);
	}

	void searchNeighbours(Queue<Node> queue, int[] targetPoint, int[] grid, boolean[][] visited) {
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
			addNeighbours(queue, current, grid, visited);
			searchNeighbours(queue, targetPoint, grid, visited);
		}
	}

	private void addNeighbours(Queue<Node> queue, Node current, int[] grid, boolean[][] visited) {
		// top
		addToQueue(queue, new Node(current.x + 1, current.y, current.distanceFromStart + 1, current), grid, visited);
		// down
		addToQueue(queue, new Node(current.x - 1, current.y, current.distanceFromStart + 1, current), grid, visited);
		// right
		addToQueue(queue, new Node(current.x, current.y + 1, current.distanceFromStart + 1, current), grid, visited);
		// left
		addToQueue(queue, new Node(current.x, current.y - 1, current.distanceFromStart + 1, current), grid, visited);
	}

	void addToQueue(Queue<Node> queue, Node node, int[] grid, boolean[][] visited) {
		int x = grid[0];
		int y = grid[1];
		// visited
		if (node.x >= 0 && node.x < x && node.y >= 0 && node.y < y && !visited[node.x][node.y]) {
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

class Node {
	int x;
	int y;
	int distanceFromStart;
	Node previousNode;

	public Node(int x, int y, int distanceFromStart) {
		super();
		this.x = x;
		this.y = y;
		this.distanceFromStart = distanceFromStart;
	}

	public Node(int x, int y, int distanceFromStart, Node previousNode) {
		super();
		this.x = x;
		this.y = y;
		this.distanceFromStart = distanceFromStart;
		this.previousNode = previousNode;
	}

	@Override
	public String toString() {
		return "Node [x=" + x + ", y=" + y + ", distanceFromStart=" + distanceFromStart + "]";
	}
}
