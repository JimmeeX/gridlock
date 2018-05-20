package gridlock.model;

import java.util.ArrayList;
import java.util.LinkedList;

class BoardSolver {

	Board board;

	public BoardSolver(Board board) {
		this.board = board;
	}


	public Block solvePuzzle() {
		LinkedList<Board> queue = new LinkedList<>();
		ArrayList<Board> visited = new ArrayList<>();
		int numExpanded = 0;

		queue.add(this.board);
		while (!queue.isEmpty()) {
			Board curr = queue.poll();
			numExpanded++;
			if (curr.checkGameOver()) {
				Board nextBoard = curr.getPath().get(1);
				return nextBoard.getLastMove();
			}
			if (visited.contains(curr)) continue;

			visited.add(curr);

			for (Board boards : curr.getNextPossible()) queue.add(boards);
		}
		return null; // SOON
	}

}