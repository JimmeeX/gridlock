package gridlock.model;

import java.util.ArrayList;
import java.util.LinkedList;

class BoardSolver {

	Board board;

	public BoardSolver(Board board) {
		this.board = board;
	}


	public boolean solvePuzzle() {
		LinkedList<Board> queue = new LinkedList<>();
		ArrayList<Board> visited = new ArrayList<>();
		int numExpanded = 0;
		
		queue.add(this.board);
		while (!queue.isEmpty()) {
			Board curr = queue.poll();
			numExpanded++;
			//System.out.println("curr = " + curr.getPathSize());
			//curr.printBlocks();
			if (curr.checkGameOver()) {
				System.out.println(numExpanded + " boards expanded.");
				//curr.printPath();
				System.out.println("curr = " + curr.getPathSize());
				return true;
			}
			if (visited.contains(curr)) {
				//System.out.println("same");
				continue;
			}
			
			visited.add(curr);
			
			for (Board boards : curr.getNextPossible()) queue.add(boards);
			//System.out.println("There are " + queue.size() + " boards in queue.");
		}
		System.out.println(numExpanded + " boards expanded.");
		return false; // SOON
	}
	
}
