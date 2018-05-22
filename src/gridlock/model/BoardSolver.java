package gridlock.model;

import java.util.HashSet;
import java.util.LinkedList;

class BoardSolver {
	Board board;
	int numMoves;

	public BoardSolver(Board board) {
		this.board = board;
	}

	public Block solvePuzzle() {
		LinkedList<Board> queue = new LinkedList<>();
		HashSet<Board> visited = new HashSet<>();

		queue.add(this.board);
		while (!queue.isEmpty()) {
			Board curr = queue.poll();
			if (curr.checkGameOver()) {
				Board nextBoard = curr.getPath().get(1);
				numMoves = curr.getPathSize() - 1;
				return nextBoard.getLastMove();
			}
			if (visited.contains(curr)) continue;

			visited.add(curr);

			for (Board boards : curr.getNextPossible()) {
				queue.add(boards);
			}
		}
		return null;
	}
	
	public int getNumMoves() {
		return numMoves;
	}

}