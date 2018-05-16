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
	        this.board.printGrid();
            System.out.println("");
            search(7);
            this.board.printGrid();
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        } finally {
            if (sc != null) sc.close();
        }
    }

	private void search(int numOfMoves) {
		//set initial state
    	Block curBlock = this.board.getBlock("z");
    	//set ArrayList of possible moves
		ArrayList<Integer[]> positions = new Position
		if (movable(curBlock) == 0) return;
		int density = calculateDensity();
		System.out.println("density = " + density);
		BoardState curBoardState = new BoardState(null, density, 0);

		//initialise priority queue and first and end state
		PriorityQueue<BoardState> stateQueue= new PriorityQueue<>();
		stateQueue.add(curBoardState);
		BoardState endState = null;

		//initialise visited array
		ArrayList<BoardState> visited = new ArrayList<>();

		//loop
		while (!stateQueue.isEmpty()) {
			BoardState curState = stateQueue.poll();
			visited.add(curState);

			//check if end condition is met
			if (curState.getNMoves() == numOfMoves) {
				endState = curState;
				//break;
			}

			for ()
		}
	}

	private int calculateDensity() {
    	int count = 0;
    	for (Block block: this.board.getBlocks()) {
    		count = count + movable(block);
	    }
	    return count;
	}

	private void addPosition(ArrayList<Integer[]> list, int row, int col) {
    	Integer[] position = new Integer[2];
    	position[0] = row;
    	position[1] = col;
    	list.add(position);
	}

	public int movable(Block block, ArrayList<Integer[]> positions) {
    	int count = 0;
    	if (block.isHorizontal()) {
    		int row = block.getPosition().get(0)[0];
    		int startCol = block.getPosition().get(0)[1] - 1;
    		int endCol = block.getPosition().get(block.getPosition().size() - 1)[1] + 1;
    		System.out.println("row = " + row + " startCol = " + startCol + " endCol = " + endCol);
    		while (startCol >= 0) {
			    System.out.println("left neighbor = " + this.board.getGridRow(row)[startCol]);
			    System.out.println("COUNT BECOMES = " + count + " STARTCOL = " + startCol);
			    if (this.board.getGridRow(row)[startCol].equals("*")) {
			    	addPosition(positions, row, startCol);
    				count++;
    				startCol--;
			    }
    			else break;
		    }
		    while(endCol < 6) {
			    System.out.println("COUNT BECOMES = " + count + " ENDCOL = " + endCol);
    			if (this.board.getGridRow(row)[endCol].equals("*")) {
    				addPosition(positions, row, endCol);
    				count++;
				    endCol++;
			    }
    			else break;
		    }
	    } else {
    		int col = block.getPosition().get(0)[1];
		    int startRow = block.getPosition().get(0)[0] - 1;
		    int endRow = block.getPosition().get(block.getPosition().size() - 1)[0] + 1;
		    System.out.println("col = " + col + " startRow = " + startRow + " endRow = " + endRow);
		    while (startRow >= 0) {
			    System.out.println("COUNT BECOMES = " + count + " STARTROW = " + startRow);
			    if (this.board.getGridRow(startRow)[col].equals("*")) {
			    	addPosition(positions, startRow, col);
			    	count++;
			    	startRow--;
			    }
			    else break;
		    }
		    while (endRow < 6) {
			    System.out.println("COUNT BECOMES = " + count + " ENDROW = " + endRow);
			    if (this.board.getGridRow(endRow)[col].equals("*")) {
			    	addPosition(positions, endRow, col);
			    	count++;
				    endRow++;
			    }
			    else break;
		    }
	    }
	    System.out.println("count = " + count);
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