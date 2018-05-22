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
				System.out.println("Hint found.");
				return nextBoard.getLastMove();
			}
			if (visited.contains(curr)) continue;

			visited.add(curr);

			for (Board boards : curr.getNextPossible()) {
				queue.add(boards);
			}
		}
		System.out.println("Hint not found :(");
		return null;
	}
	
	public int getNumMoves() {
		return numMoves;
	}

}
