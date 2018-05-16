package gridlock.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Scanner;

public class BoardSolver {

	Board board;

	public BoardSolver() {
		this.board = new Board();
	}

	/*
	 * process input txt file
	 */
    public void process() {
        Scanner sc = null;
        try {
            sc = new Scanner(new File("src/gridlock/endGameState.txt"));
            for (int row = 0; row < 6; row++) {
                for (int col = 0; col < 6; col++) {
                    String id = sc.next();
                    if (!id.equals("*")) {
                        int blockID = this.board.blockExist(id);
                        if (blockID != -1) this.board.incrementSize(blockID, row, col);
                        else this.board.addBlock(id, row, col);
                    }
                }
            }
            search();
            this.board.printGrid();
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        } finally {
            if (sc != null) sc.close();
        }
    }

	private void search() {
		Block curBlock = this.board.getBlock("z");
		if (movable(curBlock) == 0) return;
	}

	public int movable(Block block) {
    	int count = 0;
    	if (block.isHorizontal()) {
    		int row = block.getPosition().get(0)[0];
    		int startCol = block.getPosition().get(0)[1];
    		int endCol = block.getPosition().get(block.getPosition().size() - 1)[1];
    		while (startCol >= 0) {
    			if (this.board.getGridRow(row)[startCol].equals("*")) {
    				count++;
    				startCol--;
			    }
    			else break;
		    }
		    while(endCol <= 6) {
    			if (this.board.getGridRow(row)[endCol].equals("*")) {
    				count++;
				    endCol++;
			    }
    			else break;
		    }
	    } else {
    		int col = block.getPosition().get(0)[1];
		    int startRow = block.getPosition().get(0)[0];
		    int endRow = block.getPosition().get(block.getPosition().size() - 1)[0];
		    while (startRow >= 0) {
			    if (this.board.getGridRow(startRow)[col].equals("*")) {
			    	count++;
			    	startRow--;
			    }
			    else break;
		    }
		    while (endRow <= 6) {
			    if (this.board.getGridRow(endRow)[col].equals("*")) {
			    	count++;
				    endRow++;
			    }
			    else break;
		    }
	    }
	    return count;
	}


	public LinkedList<ArrayList<Block>> solvePuzzle(ArrayList<Block> startBoard) {
		PriorityQueue<ArrayList<Block>> queue = new PriorityQueue<>();
		ArrayList<ArrayList<Block>> visited = new ArrayList<>();
		
		queue.add(startBoard);
		while (!queue.isEmpty()) {
			ArrayList<Block> curr = queue.poll();
			if (visited.contains(curr)) continue;
			
			visited.add(curr);
			
			for (ArrayList<Block> blocks : getNextPossibleMoves(curr)) queue.add(blocks);
		}
		return null; // SOON
	}
	
	public ArrayList<ArrayList<Block>> getNextPossibleMoves(ArrayList<Block> currState) {
		ArrayList<ArrayList<Block>> possible = new ArrayList<>();
		for (char id = 'a'; id < 'a' + currState.size(); id++) { //SOOON
			
		}
		return possible;
	}
}