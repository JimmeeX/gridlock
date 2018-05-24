package gridlock.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;

/**
 * the BoardSolver Class for implementing the hint functionality of the board
 * added by Joseph
 */
class BoardSolver {
	BoardState board;
	int numMoves;

	/**
	 * Class constructor for BoardSolver
	 * @param board the state of the board
	 */
	public BoardSolver(BoardState board) {
		this.board = board;
	}

	/**
	 * bfs to get the end state of the board
	 * @return the block that is to be moved next to reach
	 * the end board state with least number of moves
	 */
	public Block solvePuzzle() {
		LinkedList<BoardState> queue = new LinkedList<>();
		HashSet<BoardState> visited = new HashSet<>();

		queue.add(this.board);
		while (!queue.isEmpty()) {
			BoardState curr = queue.poll();
			if (curr.checkGameOver()) {
<<<<<<< HEAD
				ArrayList<Board> path = new ArrayList<>();
				while (curr != null) {
					path.add(curr);
					curr = curr.getPrevBoard();
				}
				Board nextBoard = path.get(path.size() - 2);
				numMoves = path.size() - 1;
=======
				BoardState nextBoard = curr.getPath().get(1);
				numMoves = curr.getPathSize() - 1;
>>>>>>> 0c2dac6ea30b19b41f76b9eda51e56aa767b1afa
				return nextBoard.getLastMove();
			}
			if (visited.contains(curr)) continue;

			visited.add(curr);

			for (BoardState boards : curr.getNextPossible()) {
				queue.add(boards);
			}
		}
		return null;
	}

	/**
	 * get the number of moves from the current board state to
	 * the the end board state
	 * @return
	 */
	public int getNumMoves() {
		return numMoves;
	}

}