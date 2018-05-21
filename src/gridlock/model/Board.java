package gridlock.model;

//do we really need an id for a block?
//hmm I reckon the id is to look for the block
//(say when we move a block, we refer to them by their id) - Alina

import java.util.ArrayList;

/**
 * the Board class designed to contain the blocks initialised, grid,
 * and other functionality of a board game (make move, undo move, etc)
 */
public class Board {
    private ArrayList<String[]> grid;
    private ArrayList<Block> blocks;
    private ArrayList<Board> path;
    private Block lastMove;

    /**
     * Board class constructor
     */
    public Board(ArrayList<Block> blocks, ArrayList<Board> prev, Block lastMove) {
        initialiseGrid(6);
        this.blocks = blocks;
        path = new ArrayList<>();
        for (Board parentBoard: prev) {
            this.path.add(parentBoard);
        }
        this.path.add(this);
        this.lastMove = lastMove;
        fillGrid();
    }
    /** -prvt
     * initialise the grid (size x size)
     * @param size the length of the grid (square)
     * @post this.grid.size() >= 0
     */
    private void initialiseGrid(int size) {
        this.grid = new ArrayList<>();
        for (int row = 0; row < size; row++) {
            String[] newRow = new String[size];
            for (int col = 0; col < size; col++) {
                newRow[col] = "*";
            }
            this.grid.add(newRow);
        }
    }
    
    private void fillGrid() {
    	for (Block block : this.blocks) {
    		for (Integer[] position : block.getPosition()) {
    			this.grid.get(position[0])[position[1]] = block.getID();
    		}
    	}
    }

    public Block getBlock(String id) {
        for (Block block: this.blocks) {
            if (block.getID().equals(id)) return block;
        }
        return null;
    }

    public String[] getGridRow(int row) {
        return this.grid.get(row);
    }

    public Block getBlock(String id) {
        for (Block block: this.blocks) {
            if (block.getID().equals(id)) return block;
        }
        return null;
    }

    public String[] getGridRow(int row) {
        return this.grid.get(row);
    }

    /**
     * add a new block to the grid
     * @param id the block's id
     * @param row the row position of the block
     * @param col the col position of the block
     */

    public void setBlock(String id, int row, int col){
        Block newBlock = new Block(id, row, col);
        this.grid.get(row)[col] = id;
        this.blocks.add(newBlock);
    }

    /**
     * check if the block has been initialised
     * @param id the id of the block (in String)
     * @return -1 if the block doesn't exist
     * @return the block index if the block exists
     */
    public int blockExist(String id) {
        for (int block = 0; block < this.blocks.size(); block++) {
            if (this.blocks.get(block).getID().equals(id)) return block;
        }
        return -1;
    }

    /**
     * increment the size of initialised blocks
     * @param idx the index of the block in blocks
     * @param row the row position of the block
     * @param col the col position of the block
     * @post the block's size will increase by 1
     */
    public void incrementSize(int idx, int row, int col) {
        Block thisBlock = this.blocks.get(idx);
        Integer[] newPosition = new Integer[2];
        newPosition[0] = row;
        newPosition[1] = col;
        thisBlock.addPosition(newPosition);
        this.grid.get(row)[col] = thisBlock.getID();
    }

    /**
     * get the grid
     */

    public void setBlock(String id, int row, int col){
        Block newBlock = new Block(id, row, col);
        this.grid.get(row)[col] = id;
        this.blocks.add(newBlock);
    }

    /**
     * check if the block has been initialised
     * @param id the id of the block (in String)
     * @return -1 if the block doesn't exist
     * @return the block index if the block exists
     */
    public int blockExist(String id) {
        for (int block = 0; block < this.blocks.size(); block++) {
            if (this.blocks.get(block).getID().equals(id)) return block;
        }
        return -1;
    }

    /**
     * increment the size of initialised blocks
     * @param idx the index of the block in blocks
     * @param row the row position of the block
     * @param col the col position of the block
     * @post the block's size will increase by 1
     */
    public void incrementSize(int idx, int row, int col) {
        Block thisBlock = this.blocks.get(idx);
        Integer[] newPosition = new Integer[2];
        newPosition[0] = row;
        newPosition[1] = col;
        thisBlock.addPosition(newPosition);
        this.grid.get(row)[col] = thisBlock.getID();
    }

    /**
     * get the grid
     */
    public ArrayList<String[]> getGrid () {
        return this.grid;
    }

    /**
     * get the size of the grid
     * @return grid.size()
     */
    public int getGridSize() {
        return this.grid.size();
    }
    
    /**
     * print the grid
     */
    public void printGrid() {
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 6; col++) {
                System.out.print(this.grid.get(row)[col] + " ");
            }
            System.out.println();
        }
    }
  
    /**
     * Print all blocks' details
     */
    public void printBlocks() {
        for (Block block: this.blocks) {
            System.out.println(block.toString());
        }
    }

    public boolean checkGameOver() {
        for (Block block: this.blocks) {
            if (block.getID().equals("z")) {
                if (block.getPosition().get(0)[1] == 4) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }

    public ArrayList<Board> getNextPossible() {
        ArrayList<Board> next = new ArrayList<>();
        Block changedBlock = null;
        for (Block block : this.blocks) {
            if (block.isHorizontal()) {
                Integer[] newPos = new Integer[2];
                newPos[0] = block.getRow();
                newPos[1] = block.getCol();
                String[] row = this.grid.get(newPos[0]);
                if (newPos[1] > 0 && row[newPos[1] - 1].equals("*")) {
	                // go as far left as it can go
	                while (newPos[1] > 0 && row[newPos[1] - 1].equals("*")) {
	                    newPos[1]--;
                        ArrayList<Block> copyBlocks = new ArrayList<>();
                        for (Block blockCopy: this.blocks) {
                            Block newBlock = blockCopy.duplicate();
                            if (newBlock.getID().equals(block.getID())) {
                                newBlock.setNewPosition(newPos);
                                changedBlock = newBlock;
                            }
                            copyBlocks.add(newBlock);
                        }
                        Board newBoard = new Board(copyBlocks, this.path, changedBlock);
                        next.add(newBoard);
	                }
                }
                // go as far right as it can go
                newPos[1] = block.getCol();
                if (newPos[1] < 6 - block.getSize() && row[newPos[1] + block.getSize()].equals("*")) {
                	while (newPos[1] < 6 - block.getSize() && row[newPos[1] + block.getSize()].equals("*")) {
                        newPos[1]++;
                        ArrayList<Block> copyBlocks = new ArrayList<>();
                        for (Block blockCopy: this.blocks) {
                            Block newBlock = blockCopy.duplicate();
                            if (newBlock.getID().equals(block.getID())) {
                                newBlock.setNewPosition(newPos);
                                changedBlock = newBlock;
                            }
                            copyBlocks.add(newBlock);
                        }
                        Board newBoard = new Board(copyBlocks, this.path, changedBlock);
                        next.add(newBoard);
                    }
                }
            } else {
            	Integer[] newPos = new Integer[2];
                newPos[0] = block.getRow();
                newPos[1] = block.getCol();
                if (newPos[0] > 0 && this.grid.get(newPos[0] - 1)[newPos[1]].equals("*")) {
	                // go as far up as it can go
	                while (newPos[0] > 0 && this.grid.get(newPos[0] - 1)[newPos[1]].equals("*")) {
	                    newPos[0]--;
                        ArrayList<Block> copyBlocks = new ArrayList<>();
                        for (Block blockCopy: this.blocks) {
                            Block newBlock = blockCopy.duplicate();
                            if (newBlock.getID().equals(block.getID())) {
                                newBlock.setNewPosition(newPos);
                                changedBlock = newBlock;
                            }
                            copyBlocks.add(newBlock);
                        }
                        Board newBoard = new Board(copyBlocks, this.path, changedBlock);
                        next.add(newBoard);
	                }
                }
                // go as far down as it can go
                newPos[0] = block.getRow();
                if (newPos[0] < 6 - block.getSize() && this.grid.get(newPos[0] + block.getSize())[newPos[1]].equals("*")) {
                	while (newPos[0] < 6 - block.getSize() && this.grid.get(newPos[0] + block.getSize())[newPos[1]].equals("*")) {
                        newPos[0]++;
                        ArrayList<Block> copyBlocks = new ArrayList<>();
                        for (Block blockCopy: this.blocks) {
                            Block newBlock = blockCopy.duplicate();
                            if (newBlock.getID().equals(block.getID())) {
                                newBlock.setNewPosition(newPos);
                                changedBlock = newBlock;
                            }
                            copyBlocks.add(newBlock);
                        }
                        Board newBoard = new Board(copyBlocks, this.path, changedBlock);
                        next.add(newBoard);
                    }
                }
            }
        }
        return next;
    }

    @Override
	public int hashCode() {
		int result = ((this.blocks == null) ? 0 : this.blocks.hashCode());
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
        Board other = (Board) obj;
        if (this.blocks == null) {
            if (other.blocks != null)
                return false;
        }
        else {
            for (Block block : this.blocks) {
                if (!other.blocks.contains(block)) return false;
            }
        }
        return true;
    }


    @Override
    public String toString() {
        printBlocks();
        return this.getClass().getName();
    }

    public int getPathSize() {
        return this.path.size();
    }

    public ArrayList<Board> getPath() {
        return this.path;
    }

    public Block getLastMove() {
        return this.lastMove;
    }

}