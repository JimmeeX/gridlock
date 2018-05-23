package gridlock.model;

import java.util.ArrayList;

/**
 * the BoardState class designed for internal back end implementation
 * Added by Joseph
 */
public class BoardState {
	private Board board;
	private ArrayList<BoardState> path;
	private Block lastMove;

	/**
	 * BoardState class constructor
	 * @param blocks the list of blocks
	 * @param prev the list of previous board states
	 * @param lastMove the last Block that was moved
	 */
	public BoardState(ArrayList<Block> blocks, ArrayList<BoardState> prev, Block lastMove) {
		this.board = new Board();
		this.board.setGrid();
		this.board.setBlocks(blocks);
		path = new ArrayList<>();
		for (BoardState parentBoard: prev) {
			this.path.add(parentBoard);
		}
		this.path.add(this);
		this.lastMove = lastMove;
	}

	/**
	 * get the size of the path from the current board state to
	 * the end board state
	 * @return the number of moves needed to get to the end board state
	 */
	public int getPathSize() {
		return this.path.size();
	}

	/**
	 * get the path from the current board state to the end board state
	 * @return the ArrayList of board states to get to the end board state
	 */
	public ArrayList<BoardState> getPath() {
		return this.path;
	}

	/**
	 * get the last move made
	 * @return the last block that was moved
	 */
	public Block getLastMove() {
		return this.lastMove;
	}

	/**
	 * Print all blocks' details
	 */
	public void printBlocks() {
		for (Block block: this.board.getBlocks()) {
			System.out.println(block.toString());
		}
	}

	/**
	 * Check if the goal of the game is fulfilled
	 * @return true if the red car is on the goal position
	 * @return false if the red car is not on the goal position
	 */
	public boolean checkGameOver() {
		for (Block block: this.board.getBlocks()) {
			if (block.getID().equals("z")) {
				if (block.getPosition().get(0)[1] == 4) return true;
				else return false;
			}
		}
		return false;
	}

	/**
	 * get the next possible board states
	 * @return the list of possible-to-visit board states
	 */
	public ArrayList<BoardState> getNextPossible() {
		ArrayList<BoardState> next = new ArrayList<>();
		for (Block block : this.board.getBlocks()) {
			Integer[] newPos = new Integer[2];
			newPos[0] = block.getRow();
			newPos[1] = block.getCol();
			if (block.isHorizontal()) {
				String[] row = this.board.getGridRow(newPos[0]);
				if (newPos[1] > 0 && row[newPos[1] - 1].equals("*")) {
					// go as far left as it can go
					while (newPos[1] > 0 && row[newPos[1] - 1].equals("*")) {
						newPos[1]--;
						next.add(createNewBoard(block, newPos));
					}
				}
				// go as far right as it can go
				newPos[1] = block.getCol();
				if (newPos[1] < 6 - block.getSize() && row[newPos[1] + block.getSize()].equals("*")) {
					while (newPos[1] < 6 - block.getSize() && row[newPos[1] + block.getSize()].equals("*")) {
						newPos[1]++;
						next.add(createNewBoard(block, newPos));
					}
				}
			} else {
				if (newPos[0] > 0 && this.board.getGridRow(newPos[0] - 1)[newPos[1]].equals("*")) {
					// go as far up as it can go
					while (newPos[0] > 0 && this.board.getGridRow(newPos[0] - 1)[newPos[1]].equals("*")) {
						newPos[0]--;
						next.add(createNewBoard(block, newPos));
					}
				}
				// go as far down as it can go
				newPos[0] = block.getRow();
				if (newPos[0] < 6 - block.getSize() && this.board.getGridRow
						(newPos[0] + block.getSize())[newPos[1]].equals("*")) {
					while (newPos[0] < 6 - block.getSize() && this.board.getGridRow
							(newPos[0] + block.getSize())[newPos[1]].equals("*")) {
						newPos[0]++;
						next.add(createNewBoard(block, newPos));
					}
				}
			}
		}
		return next;
	}

	/**
	 * helper function to create a new board with one block moved into a new position
	 * @param block the block that's moved to the new position
	 * @param newPosition the new position [row,col] the block is moved into
	 * @return the new Board
	 */
	private BoardState createNewBoard(Block block, Integer[] newPosition) {
		ArrayList<Block> copyBlocks = new ArrayList<>();
		Block changedBlock = null;
		for (Block blockCopy: this.board.getBlocks()) {
			Block newBlock = blockCopy.duplicate();
			if (newBlock.getID().equals(block.getID())) {
				newBlock.setNewPosition(newPosition);
				changedBlock = newBlock;
			}
			copyBlocks.add(newBlock);
		}
		return new BoardState(copyBlocks, this.path, changedBlock);
	}

	@Override
	public int hashCode() {
		int result = ((this.board.getBlocks() == null) ? 0 : this.board.getBlocks().hashCode());
		return result;
	}

	@Override
	public boolean equals (Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BoardState other = (BoardState) obj;
		if (this.board.getBlocks() == null) {
			if (other.board.getBlocks() != null)
				return false;
		}
		else {
			for (Block block : this.board.getBlocks()) {
				if (!other.board.getBlocks().contains(block)) return false;
			}
		}
		return true;
	}

	@Override
	public String toString() {
		printBlocks();
		return this.getClass().getName();
	}
}
