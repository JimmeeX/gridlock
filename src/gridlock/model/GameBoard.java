package gridlock.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class GameBoard {
	private ArrayList<String[]> grid;
    private ArrayList<Block> blocks;
	private ArrayList<Block> prevLocations;
    private ArrayList<Block> nextLocations;

    // Added by James :)
    private BooleanProperty gameState;
    private IntegerProperty numMoves;
    
    public GameBoard() {
    	initialiseGrid(6);
    	this.blocks = new ArrayList<>();
    	this.prevLocations = new ArrayList<>();
        this.nextLocations = new ArrayList<>();
        
        // Added by James :)
        // Board starts off as unsolved (ie, false)
        this.gameState = new SimpleBooleanProperty(false);
        this.numMoves = new SimpleIntegerProperty(0);
    }
    
    /**
     * process input txt file
     * @param fileName the file name to be processed
     */
    public void process(String fileName) {
        Scanner sc = null;
        try {
            sc = new Scanner(new File(fileName));
            for (int row = 0; row < 6; row++) {
                for (int col = 0; col < 6; col++) {
                    String id = sc.next();
                    if (!id.equals("*")) {
                        int blockID = blockExist(id);
                        if (blockID != -1) {
                            incrementSize(blockID, row, col);
                            this.grid.get(row)[col] = id;
                        }
                        else addBlock(id, row, col);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        } finally {
            if (sc != null) sc.close();
        }
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
    
    /**
     * add a new block to the grid
     * @param id the block's id
     * @param row the row position of the block
     * @param col the col position of the block
     */
    public void addBlock(String id, int row, int col){
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
    
    // Added by James :)
    public int getNumMoves() {
        return numMoves.get();
    }

    // Added by James :)
    public IntegerProperty numMovesProperty() {
        return numMoves;
    }

    // Added by James :)
    public void updateNumMoves() {
        this.numMoves.setValue(this.prevLocations.size());
    }
    
    /**
     * Two makeMove: public and private. To move a block
     * @param id the id of the block
     * @param newStartPosition the new start position after the move
     * @param redoAutomatisation is the caller redo/undo
     * @pre the move is valid (within grid, according to the block direction)
     */
    public void makeMove(String id, Integer[] newStartPosition, boolean redoAutomatisation) {
        for (Block block : this.blocks) {
            if (block.getID().equals(id)) {
                if(!block.samePosition(newStartPosition)
                        && !collide(block, newStartPosition)) {
                    Block oldBlock = new Block(id, block.getPosition().get(0)[0], block.getPosition().get(0)[1]);
                    clearBlockIDFromGrid(block);
                    this.prevLocations.add(oldBlock);
                    block.setNewPosition(newStartPosition);
                    addBlockIDToGrid(block);
                    if (redoAutomatisation) nextLocations.clear();
                }
            }
        }
    }
    
    /** -prvt
     */
    private void clearBlockIDFromGrid(Block block) {
        for (Integer[] position: block.getPosition()) {
            this.grid.get(position[0])[position[1]] = "*";
        }
    }
    /** -prvt
     */
    private void addBlockIDToGrid(Block block) {
        for (Integer[] position :block.getPosition()) {
            this.grid.get(position[0])[position[1]] = block.getID();
        }
    }
    
    /** -prvt
     * Check if the new position of a block collides with others and walls
     * @param thisBlock
     * @param newStartPosition
     * @return
     */
    public boolean collide(Block thisBlock, Integer[] newStartPosition) {
        for (Block block : this.blocks) {
            if (!block.getID().equals(thisBlock.getID())) {
                for (Integer[] position : block.getPosition()) {
                    if (thisBlock.isHorizontal()
                            ? position[1] >= newStartPosition[1] && position[1] <= newStartPosition[1] + thisBlock.getSize() - 1 &&
                            position[0] == newStartPosition[0]
                            : position[0] >= newStartPosition[0] && position[0] <= newStartPosition[0] + thisBlock.getSize() - 1 &&
                            position[1] == newStartPosition[1]) return true;
                }
            }
        }
        if (thisBlock.isHorizontal()
                ? newStartPosition[1] + thisBlock.getSize() - 1 > 6
                : newStartPosition[0] + thisBlock.getSize() - 1 > 6) return true;
        return false;
    }
    
    /**
     * undo the previous move made
     * @post this.nextLocations.size()++
     * @post this.prevLocation.size()--
     */
    public void undoMove() {
        if (this.prevLocations.size() != 0) {
            Block copy = this.prevLocations.get(this.prevLocations.size() - 1);
            Block block = new Block(copy.getID(), copy.getPosition().get(0)[0], copy.getPosition().get(0)[1]);
            this.prevLocations.remove(this.prevLocations.size() - 1);
            for (Block oldBlock: this.blocks) {
                if (oldBlock.getID().equals(block.getID())) {
                    Block toAdd = new Block(oldBlock.getID(), oldBlock.getPosition().get(0)[0], oldBlock.getPosition().get(0)[1]);
                    this.nextLocations.add(toAdd);
                }
            }
            makeMove(block.getID(), block.getPosition().get(0), false);
            this.prevLocations.remove(this.prevLocations.size() - 1);
        }
    }

    /**
     * redo the move undone
     * @post this.nextLocation.size()--
     * @post this.prevLocation.size()++
     */
    public void redoMove() {
        if (this.nextLocations.size() != 0) {
            Block copy = this.nextLocations.get(this.nextLocations.size() - 1);
            Block block = new Block(copy.getID(), copy.getPosition().get(0)[0], copy.getPosition().get(0)[1]);
            this.nextLocations.remove(this.nextLocations.size()-1);
            for (Block oldBlock: this.blocks) {
                if (oldBlock.getID().equals(block.getID())) {
                    Block toAdd = new Block(oldBlock.getID(), oldBlock.getPosition().get(0)[0], oldBlock.getPosition().get(0)[1]);
                    this.prevLocations.add(toAdd);
                }
            }
            makeMove(block.getID(), block.getPosition().get(0), false);
            this.prevLocations.remove(this.prevLocations.size() - 1);
        }
    }

    /**
     * restart back to the initial position of the board
     * @post this.numOfMoves = 0
     * @post this.prevLocations.size() = 0
     * @post this.nextLocation.size() = 0
     */
    public void restart() {
        while (this.prevLocations.size() > 0) {
            undoMove();
        }
        this.prevLocations.clear();
        this.nextLocations.clear();
    }

    // Added by James :)
    public BooleanProperty gameStateProperty() {
        return gameState;
    }

    /**
     * Check if the game is over (the "z" car is by the exit)
     * Will send "true" to "gameState" if game is over
     */
    // Added by James :)
    public void checkGameOver() {
        this.gameState.setValue(false);
        for (Block block: this.blocks) {
            if (block.getID().equals("z")) {
                if (block.getPosition().get(0)[1] == 4) {
                    this.gameState.setValue(true);
                }
            }
        }
    }
    
    /**
     * Print all next-prev loc details
     */
    public void printNextPrevLocations() {
        System.out.println("nextLocation = " + this.nextLocations.size() + " prevLocation = "
                + this.prevLocations.size() + " numOfMoves = " + this.prevLocations.size());
        System.out.println("prevLoc:");
        for (Block block: prevLocations) {
            System.out.println(block.toString());
        }
        System.out.println("nextLoc:");
        for (Block block: nextLocations) {
            System.out.println(block.toString());
        }
    }
    
    /**
     * get the size of the grid
     * @return grid.size()
     */
    public int getGridSize() {
        return this.grid.size();
    }
    
    /**
     * get all blocks initialised
     * @return blocks arraylist
     */
    public ArrayList<Block> getBlocks() {
        return this.blocks;
    }
    
    public Block getHint() {
    	Board startBoard = new Board(this.blocks, new ArrayList<Board>());
    	BoardSolver solver = new BoardSolver(startBoard);
    	return solver.solvePuzzle();
    }
    
}

