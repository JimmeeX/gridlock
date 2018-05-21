package gridlock.model;

import java.util.HashSet;
import java.util.LinkedList;

class BoardSolver {

    Board board;

	public BoardSolver(Board board) {
		this.board = board;
	}

	public Block solvePuzzle() {
		LinkedList<Board> queue = new LinkedList<>();
		HashSet<Board> visited = new HashSet<>();
		int numExpanded = 0;

		queue.add(this.board);
		while (!queue.isEmpty()) {
			Board curr = queue.poll();
			numExpanded++;
			if (visited.contains(curr)) continue;

			visited.add(curr);

			for (Board boards : curr.getNextPossible()) {
				if (boards.checkGameOver()) {
					Board nextBoard = boards.getPath().get(1);
					System.out.println("Hint found.");
					return nextBoard.getLastMove();
				}
				queue.add(boards);
			}
		}
		System.out.println("Hint not found :(");
		return null;
	}

}
