package com.myweb.utility.trails.service;

import org.springframework.stereotype.Service;

/**
 * @author Jegatheesh<br>
 *         <b>Created</b> On Jan 10, 2019
 *
 */
@Service
public class TrailsService {

	String lineSeperator = "<br>";

	public String logicForFindingQueens(Integer nQueens) {
		StringBuilder contentLog = new StringBuilder();

		// Initialization
		int board[] = new int[nQueens];
		for (int i = 0; i < board.length; i++) {
			board[i] = -1;
		}

		int currentRow = 0;
		board[currentRow] = 0;
		currentRow++;
		int currentColumn = 0;
		long solutions = 0;
		while (currentRow >= 0) {
			if (isValidMove(currentRow, currentColumn, board)) {
				board[currentRow] = currentColumn;
				currentRow++;
				currentColumn = 0;

				// Board Completed?
				if (currentRow >= board.length) {
					solutions++;
					contentLog.append("Solution : " + solutions + lineSeperator);
					printBoard(board, contentLog);
					currentRow--;
					currentColumn = board[currentRow] + 1;
				}
			} else {
				// If board is not valid
				currentColumn++;
				if (currentColumn >= board.length) {
					board[currentRow] = -1;
					currentRow--;
					if (currentRow >= 0)
						currentColumn = board[currentRow] + 1;
				}
			}
		}
		contentLog.append("Total Solutions -> " + solutions + lineSeperator);
		return contentLog.toString();
	}

	private boolean isValidMove(int currentRow, int currentColumn, int[] board) {
		if (currentColumn >= board.length)
			return false;
		for (int i = 0; i < currentRow; i++) {
			// cross
			if (board[i] == currentColumn)
				return false;
			// Diagonal
			if (Math.abs(currentRow - i) == Math.abs(currentColumn - board[i]))
				return false;
		}
		return true;
	}

	private void printBoard(int[] board, StringBuilder contentLog) {
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board.length; j++) {
				if (board[i] == j)
					contentLog.append("|Q");
				else
					contentLog.append("|--");
			}
			contentLog.append("|" + lineSeperator);
		}
	}
	
	public String logicForTOHMoves(Integer numberOfDiscs) {
		StringBuilder contentLog = new StringBuilder();
		moveDisc(numberOfDiscs, "A", "C", "B", contentLog);
		return contentLog.toString();
	}
	
	private void moveDisc(int numberOfDiscs, String from, String to, String inter, StringBuilder contentLog) {
		if (numberOfDiscs == 1) {
			contentLog.append("Moving Disc 1 from " + from + " to " + to + lineSeperator);
		} else {
			moveDisc(numberOfDiscs - 1, from, inter, to, contentLog);
			contentLog.append("Moving Disc " + numberOfDiscs + " from " + from + " to " + to + lineSeperator);
			moveDisc(numberOfDiscs - 1, inter, to, from, contentLog);
		}
	}

}
