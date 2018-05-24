package gridlock.model;
//do we really need an id for a block?
//hmm I reckon the id is to look for the block
//(say when we move a block, we refer to them by their id) - Alina

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * the Board class designed to contain the blocks initialised, grid,
 * and other functionality of a board game (make move, undo move, etc)
 */
public class GameBoard {
    private ArrayList<String[]> grid;
    private ArrayList<Block> blocks;
    private ArrayList<Block> prevLocations;
    private ArrayList<Block> nextLocations;
    private int minMoves;

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
                        } else setBlock(id, row, col);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        } finally {
            if (sc != null) sc.close();
        }
        setMinMoves();
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
     * get all blocks initialised
     * @return blocks arraylist
     */
    public ArrayList<Block> getBlocks() {
        return this.blocks;
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
     * Check if the game is over (the "z" car is by the exit)
     * Will send "true" to "gameState" if game is over
     */
    // Added by James :) edited by Alina XD
    public void checkGameOver() {
        this.gameState.setValue(false);
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 6; j++) {
                if (this.grid.get(i)[j].equals("z")) {
                    int count = 0;
                    int k = j + 2;
                    int left = 6 - k;
                    while (k < 6) {
                        if (this.grid.get(i)[k].equals("*")) count++;
                        k++;
                    }
                    if (count == left) {
                        Integer[] lastPosition = new Integer[2];
                        lastPosition[0] = i;
                        lastPosition[1] = 4;
                        this.makeMove("z",  lastPosition, true);
                        this.gameState.setValue(true);
                    }
                    break;
                }
            }
        }
    }

    // Added by James :)
    public BooleanProperty gameStateProperty() {
        return gameState;
    }

    // Added by Edwin
    public boolean setBlock(String id, int row, int col, int size, boolean isHorizontal) {
        if (blockExist(id) != -1
                || (isHorizontal ? col + size > 6 : row + size > 6)
                || row < 0 || col < 0
                || !this.grid.get(row)[col].equals("*")) return false;
        // put scenario: check if the grid unit is empty
        setBlock(id, row, col);
        int idx = blockExist(id);
        Block b = blocks.get(idx);
        if (isHorizontal) {
            for (int i = 1; i < size; i++) {
                if (!this.grid.get(row)[col+i].equals("*")) {
                    clearBlockIDFromGrid(b);
                    blocks.remove(b);
                    return false;
                }
                incrementSize(idx, row, col+i);
            }
        } else {
            for (int i = 1; i < size; i++) {
                if (!this.grid.get(row+i)[col].equals("*")) {
                    clearBlockIDFromGrid(b);
                    blocks.remove(b);
                    return false;
                }
                incrementSize(idx, row+i, col);
            }
        }
        return true;
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
     * Check if the new position of a block collides with others and walls
     * @param thisBlock
     * @param newStartPosition
     * @return
     */
    private boolean collide(Block thisBlock, Integer[] newStartPosition) {
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
    
    /**
     * set minMoves required to finish current puzzle
     */
    public void setMinMoves() {
    	getHint(true);
    }
    
    public int getMinMoves() {
    	return this.minMoves;
    }

    /**
     * get hint of the next block to move
     * @return
     */
    public Block getHint(boolean getMinMoves) {
        Board startBoard = new Board(this.blocks, null, null);
        BoardSolver solver = new BoardSolver(startBoard);
        Block changedBlock = solver.solvePuzzle();
        if (getMinMoves) {
        	this.minMoves = solver.getNumMoves() - 1;
        }
        return changedBlock;
    }

    /**
     * Let blockRange be an interval of the leftest/uppest and the rightest/downest
     * possible block starting column/row when only that particular block is moved
     * E.g. in endGameState.txt file:
     * |* * * a a a
     * |* * * * * *
     * |* * b * z z
     * |c c b * d e
     * |f * b * d e
     * |f * g g g e
     * blockrange(z:horznt) = [3,4], blockrange (b: vert) = [0,2]
     * @param id
     * @return
     */
    // Added by Edwin
    public Integer[] blockRange(String id) {
        int idx = blockExist(id);
        if (idx == -1) return null;
        Block b = blocks.get(idx);
        int size = b.getSize();
        Integer[] intv = new Integer[2];
        if (b.isHorizontal()) {
            intv[0] = b.getCol(); intv[1] = b.getCol();
            while (intv[0] > 0 && grid.get(b.getRow())[intv[0]-1].equals("*")) intv[0]--;
            while (intv[1]+size- 1 < 5 && grid.get(b.getRow())[intv[1]+size].equals("*")) intv[1]++;
        } else {
            intv[0] = b.getRow(); intv[1] = b.getRow();
            while (intv[0] > 0 && grid.get(intv[0]-1)[b.getCol()].equals("*")) intv[0]--;
            while (intv[1]+size- 1 < 5 && grid.get(intv[1]+size)[b.getCol()].equals("*")) intv[1]++;
        }
        return intv;
    }

    // Added by Edwin
    public GameBoard duplicate() {
        // Only need essentially grid and blocks w/ different reference
        GameBoard newBoard = new GameBoard();
        newBoard.grid.clear();
        for (String[] strArr : grid) newBoard.grid.add(strArr.clone());
        for (Block block : blocks) newBoard.getBlocks().add(block.duplicate());
        return newBoard;
    }

    // Added by Edwin
    public List <Block> particularRowOrColumnsBlockList(int rowOrColumnIndex, boolean isRow) {
        List <Block> particularROCsBlockList = new ArrayList<>();
        for (Block b: blocks) {
            if (b.isHorizontal() == isRow) {
                if (isRow ? b.getRow() == rowOrColumnIndex : b.getCol() == rowOrColumnIndex) {
                    particularROCsBlockList.add(b);
                }
            }
        }
        return particularROCsBlockList;
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
        System.out.println();
    }
    /**
     * Print all blocks' details
     */
    public void printBlocks() {
        for (Block block: this.blocks) {
            System.out.println(block.toString());
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
    
}