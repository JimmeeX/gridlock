package gridlock.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * the GameBoard class designed to return the board and functionality of a board game
 * e.g. make move, undo move, redo move, get hint, restart puzzle
 * Added by Alina, edited by Edwin, Joseph and James
 */
public class GameBoard {
    private Board board;
    private ArrayList<Block> prevLocations;
    private ArrayList<Block> nextLocations;
    private int minMoves;
    private BooleanProperty gameState;
    private IntegerProperty numMoves;

    /**
     * Class constructor for GameBoard
     */
    public GameBoard() {
        this.board = new Board();
        this.prevLocations = new ArrayList<>();
        this.nextLocations = new ArrayList<>();
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
                            this.board.getGridRow(row)[col] = id;
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

    /**
     * get a specific block
     * @param id the block id
     * @return the block with the specified id
     */
    public Block getBlock(String id) {
        for (Block block: this.board.getBlocks()) {
            if (block.getID().equals(id)) return block;
        }
        return null;
    }

    /**
     * add a new block to the grid
     * @param idx the block's id
     * @param row the row position of the block
     * @param col the col position of the block
     */

    public void setBlock(String idx, int row, int col){
        Block newBlock = new Block(idx, row, col);
        this.board.getGridRow(row)[col] = idx;
        this.board.getBlocks().add(newBlock);
    }

    /**
     * check if the block has been initialised
     * @param id the id of the block (in String)
     * @return -1 if the block doesn't exist
     * @return the block index if the block exists
     */
    public int blockExist(String id) {
        for (int block = 0; block < this.board.getBlocks().size(); block++) {
            if (this.board.getBlockID(block).equals(id)) return block;
        }
        return -1;
    }

    /**
     * increment the size of initialised blocks
     * @param id the index of the block in blocks
     * @param row the row position of the block
     * @param col the col position of the block
     * @post the block's size will increase by 1
     */
    public void incrementSize(int id, int row, int col) {
        Block thisBlock = this.board.getBlock(id);
        Integer[] newPosition = new Integer[2];
        newPosition[0] = row;
        newPosition[1] = col;
        thisBlock.addPosition(newPosition);
        this.board.getGridRow(row)[col] = thisBlock.getID();
    }

    /**
     * get the grid
     */
    public ArrayList<String[]> getGrid () {
        return this.board.getGrid();
    }

    /**
     * get the size of the grid
     * @return grid.size()
     */
    public int getGridSize() {
        return this.board.getGrid().size();
    }

    /**
     * get all blocks initialised
     * @return blocks arraylist
     */
    public ArrayList<Block> getBlocks() {
        return this.board.getBlocks();
    }

    /**
     * Added by James :)
     * get the number of moves IntegerProperty
     * @return the numberOfMoves
     */
    public IntegerProperty numMovesProperty() {
        return numMoves;
    }

    /**
     * Added by James :)
     * update the Integer Property's number of moves
     */
    public void updateNumMoves() {
        this.numMoves.setValue(this.prevLocations.size());
    }

    /**
     * Check if the game is over (the "z" car is by the exit)
     * Will send "true" to "gameState" if game is over
     * Added by James :) edited by Alina XD
     */
    public void checkGameOver() {
        this.gameState.setValue(false);
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 6; j++) {
                if (this.board.getGridRow(i)[j].equals("z")) {
                    int count = 0;
                    int k = j + 2;
                    int left = 6 - k;
                    while (k < 6) {
                        if (this.board.getGridRow(i)[k].equals("*")) count++;
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

    /**
     * Added by James :)
     * @return the state of the game
     * @return true if the game is won
     * @return false if the game is not won yet
     */
    public BooleanProperty gameStateProperty() {
        return gameState;
    }

    /**
     * set a block for level generator
     * added by Edwin
     * @param id the id/name of the block
     * @param row the starting row position
     * @param col the starting col position
     * @param size the size of the block
     * @param isHorizontal the direction of the block
     * @return true if the block can be set
     * @return false if the block can't be set
     */
    public boolean setBlock(String id, int row, int col, int size, boolean isHorizontal) {
        if (blockExist(id) != -1
                || (isHorizontal ? col + size > 6 : row + size > 6)
                || row < 0 || col < 0
                || !this.board.getGridRow(row)[col].equals("*")) return false;
        // put scenario: check if the grid unit is empty
        setBlock(id, row, col);
        int idx = blockExist(id);
        Block b = this.board.getBlock(idx);
        if (isHorizontal) {
            for (int i = 1; i < size; i++) {
                if (!this.board.getGridRow(row)[col+i].equals("*")) {
                    clearBlockFromGrid(b);
                    this.board.getBlocks().remove(b);
                    return false;
                }
                incrementSize(idx, row, col+i);
            }
        } else {
            for (int i = 1; i < size; i++) {
                if (!this.board.getGridRow(row+i)[col].equals("*")) {
                    clearBlockFromGrid(b);
                    this.board.getBlocks().remove(b);
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
        for (Block block : this.board.getBlocks()) {
            if (block.getID().equals(id)) {
                if(!block.samePosition(newStartPosition)
                        && !collide(block, newStartPosition)) {
                    Block oldBlock = new Block(id, block.getPosition().get(0)[0], block.getPosition().get(0)[1]);
                    clearBlockFromGrid(block);
                    this.prevLocations.add(oldBlock);
                    block.setNewPosition(newStartPosition);
                    addBlockToGrid(block);
                    if (redoAutomatisation) nextLocations.clear();
                }
            }
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
            for (Block oldBlock: this.board.getBlocks()) {
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
            for (Block oldBlock: this.board.getBlocks()) {
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
     * set the min moves required to finish the current puzzle
     */
    public void setMinMoves() {
        getHint(true);
    }

    /**
     * get the min moves required to finish the current puzzle
     * @return
     */
    public int getMinMoves() {
        return this.minMoves;
    }

    /**
     * get hint of the next block to move
     * @return the next block to be moved to finish the puzzle
     */
    public Block getHint(boolean getMinMoves) {
        BoardState startBoard = new BoardState(this.board.getBlocks(), null, null);
        BoardSolver solver = new BoardSolver(startBoard);
        Block changedBlock = solver.solvePuzzle();
        if (getMinMoves) {
            this.minMoves = solver.getNumMoves() - 1;
        }
        return changedBlock;
    }

    /**
     * Check if the new position of a block collides with others and walls
     * @param thisBlock the block to be checked
     * @param newStartPosition the new starting position
     * @return true if the block collides with an existing block
     * Edited by Edwin
     */
    private boolean collide(Block thisBlock, Integer[] newStartPosition) {
        for (Block block : this.board.getBlocks()) {
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
     * clear block from the grid
     * @param block the block to be removed from the grid
     * Added by Edwin
     */
    private void clearBlockFromGrid(Block block) {
        for (Integer[] position: block.getPosition()) {
            this.board.getGridRow(position[0])[position[1]] = "*";
        }
    }

    /**
     * add block to the grid
     * @param block the block to be added to the grid
     * Added by Edwin
     */
    private void addBlockToGrid(Block block) {
        for (Integer[] position :block.getPosition()) {
            this.board.getGridRow(position[0])[position[1]] = block.getID();
        }
    }

    /**
     * Let blockRange be an interval of the most left/up and the most right/down
     * possible block's starting [row,col] when only that particular block is moved
     * e.g. in endGameState.txt file:
     * |* * * a a a
     * |* * * * * *
     * |* * b * z z
     * |c c b * d e
     * |f * b * d e
     * |f * g g g e
     * Then we have blockRange("z") = [3,4], since the horizontal block with id "z" can move
     * to the leftest s.t. its starting position is in column 3, and to the rightest to column 4.
     * Similarly, blockRange ("b") = [0,2]
     * @param id the id/name of the block
     * @return the range of the block
     * Added by Edwin
     */
    public Integer[] blockRange(String id) {
        int idx = blockExist(id);
        if (idx == -1) return null;
        Block b = this.board.getBlock(idx);
        int size = b.getSize();
        Integer[] intv = new Integer[2];
        if (b.isHorizontal()) {
            intv[0] = b.getCol(); intv[1] = b.getCol();
            while (intv[0] > 0 && this.board.getGridRow(b.getRow())[intv[0]-1].equals("*")) intv[0]--;
            while (intv[1]+size- 1 < 5 && this.board.getGridRow(b.getRow())[intv[1]+size].equals("*")) intv[1]++;
        } else {
            intv[0] = b.getRow(); intv[1] = b.getRow();
            while (intv[0] > 0 && this.board.getGridRow(intv[0]-1)[b.getCol()].equals("*")) intv[0]--;
            while (intv[1]+size- 1 < 5 && this.board.getGridRow(intv[1]+size)[b.getCol()].equals("*")) intv[1]++;
        }
        return intv;
    }

    /**
     * this is initially a helper function of GBGenerator to clone a GameBoard w/o copying
     * prevLocations and nextLocations.
     *
     * @return a new GameBoard with exact-but-differently-referenced grid and blocks
     * Added by Edwin
     */
    public GameBoard duplicateGridandBlocks() {
        GameBoard newBoard = new GameBoard();
        newBoard.board.getGrid().clear();
        for (String[] strArr : this.board.getGrid()) newBoard.board.getGrid().add(strArr.clone());
        for (Block block : this.board.getBlocks()) newBoard.getBlocks().add(block.duplicate());
        return newBoard;
    }

    /**
     * find all blocks that is located on particular column/row
     * Added by Edwin
     */
    public List<Block> particularRowOrColumnsBlockList(int rowOrColumnIndex, boolean isRow) {
        List <Block> particularROCsBlockList = new ArrayList<>();
        for (Block b: this.board.getBlocks()) {
            if (b.isHorizontal() == isRow) {
                if (isRow ? b.getRow() == rowOrColumnIndex : b.getCol() == rowOrColumnIndex)
                    particularROCsBlockList.add(b);
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
                System.out.print(this.board.getGridRow(row)[col] + " ");
            }
            System.out.println();
        }
        System.out.println();
    }
}