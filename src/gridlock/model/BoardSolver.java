package gridlock.model;

import java.util.ArrayList;
import java.util.LinkedList;

class BoardSolver {

	Board board;

	public BoardSolver(Board board) {
		this.board = board;
	}

<<<<<<< HEAD
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
                        else this.board.setBlock(id, row, col);
                    }
                }
            }
	        this.board.printGrid();
            System.out.println("");
            //hint(3,9);
            this.board.printGrid();
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        } finally {
            if (sc != null) sc.close();
        }
    }

	private void hint(Board board) {
		//set initial state
    	Block curBlock = this.board.getBlock("z");

    	//set ArrayList of possible moves
		ArrayList<Integer[]> positions = new ArrayList<>();
		if (movable(curBlock, positions) == 0) return;
		BoardState curBoardState = new BoardState(null,0, curBlock, positions);

		//initialise priority queue and first and end state
		PriorityQueue<BoardState> stateQueue= new PriorityQueue<>();
		stateQueue.add(curBoardState);
		BoardState endState = null;

		//initialise visited array
		ArrayList<BoardState> visited = new ArrayList<>();

		//loop
		while (!stateQueue.isEmpty()) {
			BoardState curState = stateQueue.poll();
			if (visited.contains(curState)) continue;
			visited.add(curState);

			//check if end condition is met
			/*if (curState.getNMoves() >= minMoves && curState.getNMoves() <= maxMoves) {
				endState = curState;
				//break;
			}*/

			//Board board = curState.getBoard();

			for (Integer[] position: positions) {
				board.makeMove(curState.getBlock().getID(), position, true);

				//BoardState boardState = new BoardState(curState, curState.getNMoves() + 1, );

			}
		}
	}

	private int calculateDensity() {
    	int count = 0;
    	for (Block block: this.board.getBlocks()) {
    		count = count + movable(block, new ArrayList<>());
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
=======
	public Block solvePuzzle() {
		LinkedList<Board> queue = new LinkedList<>();
		ArrayList<Board> visited = new ArrayList<>();
		int numExpanded = 0;

		queue.add(this.board);
>>>>>>> afca3cd71b0f4141a6bc12787f51a3d142ae8fde
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