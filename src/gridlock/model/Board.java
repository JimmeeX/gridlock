package gridlock.model;
//do we really need an id for a block?
//hmm I reckon the id is to look for the block
//(say when we move a block, we refer to them by their idk) - Alina

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;

/**
 * the Board class designed to contain the blocks initialised, grid,
 * and other functionality of a board game (make move, undo move, etc)
 */
public class Board {
    private Difficulty difficulty;
    private Mode mode;
    private Integer level;
    private ArrayList<String[]> grid;
    private ArrayList<Block> blocks;
    private ArrayList<Block> prevLocations;
    private ArrayList<Block> nextLocations;

    // Added by James :)
    private BooleanProperty gameState;
    private IntegerProperty numMoves;

    /**
     * Board class constructor
     */
    public Board() {
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
                        if (blockID != -1) incrementSize(blockID, row, col);
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

    public void setDifficulty(String diff) {
        this.difficulty = Difficulty.valueOf(diff.toUpperCase());
    }

    public void setMode(String gameMode) {
        this.mode = Mode.valueOf(gameMode.toUpperCase());
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public ArrayList<String[]> getBoard() {
        return this.grid;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public Mode getMode() {
        return mode;
    }

    public Integer getLevel() {
        return level;
    }


    // Added by James :)
    public void updateNumMoves() {
        this.numMoves.setValue(this.prevLocations.size());
    }

    // Added by James :)
    public int getNumMoves() {
        return numMoves.get();
    }

    // Added by James :)
    public IntegerProperty numMovesProperty() {
        return numMoves;
    }


    @Override
    public String toString() {
        return "Gridlock{" +
                "board=" + this.grid +
                ", difficulty=" + this.difficulty +
                ", mode=" + this.mode +
                ", level=" + this.level + ", numOfMoves=" + this.prevLocations.size()+
                '}';
    }

    /**
     * initialise the grid (size x size)
     * @param size the length of the grid (square)
     * @post this.grid.size() >= 0
     */
    public void initialiseGrid(int size) {
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
     * print the grid
     */
    public void printGrid() {
        initialiseGrid(6);
        for (Block block: this.blocks) {
            ArrayList<Integer[]> positions = block.getPosition();
            for (Integer[] position : positions ) {
                this.grid.get(position[0])[position[1]] = block.getID();
            }
        }
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 6; col++) {
                System.out.print(this.grid.get(row)[col] + " ");
            }
            System.out.println();
        }
        System.out.println("list of blocks:");
        printBlock();
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
        System.out.println();
    }

    /**
     * add a new block to the grid
     * @param id the block's id
     * @param row the row position of the block
     * @param col the col position of the block
     */
    public void addBlock(String id, int row, int col){
        Block newBlock = new Block(id, row, col);
        this.blocks.add(newBlock);
    }

    /**
     * check if the block has been initialised
     * @param id the id of the block (in String)
     * @return -1 if the block doesn't exist
     * @return the block id if the block exists
     */
    public int blockExist(String id) {
        for (int block = 0; block < this.blocks.size(); block++) {
            if (this.blocks.get(block).getID().equals(id)) return block;
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
        Block thisBlock = this.blocks.get(id);
        Integer[] newPosition = new Integer[2];
        newPosition[0] = row;
        newPosition[1] = col;
        thisBlock.addPosition(newPosition);
    }

    /**
     * print all blocks' details
     */
    public void printBlock() {
        for (Block block: this.blocks) {
            System.out.println(block.toString());
        }
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
                    this.prevLocations.add(oldBlock);
                    block.setNewPosition(newStartPosition);
                    if (redoAutomatisation) nextLocations.clear();
                }
            }
        }
    }

    /**
     * Check if the new position of a block collides with others and walls
     * @param thisBlock
     * @param newStartPosition
     * @return
     */
    public boolean collide(Block thisBlock, Integer[] newStartPosition) {
        for (Block block: this.blocks) {
            if (!block.getID().equals(thisBlock.getID())) {
                for (Integer[] position : block.getPosition()) {
                    if (thisBlock.isHorizontal()
                            ? position[1] >= newStartPosition[1] && position[1] <= newStartPosition[1] + thisBlock.getSize()-1 &&
                            position[0] == newStartPosition[0]
                            : position[0] >= newStartPosition[0] && position[0] <= newStartPosition[0] + thisBlock.getSize()-1 &&
                            position[1] == newStartPosition[1]) return true;
                }
            }
        }
        if (thisBlock.isHorizontal()
            ? newStartPosition[1] + thisBlock.getSize()-1 > 6
            : newStartPosition[0] + thisBlock.getSize()-1 > 6) return true;
        return false;
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

    // Added by James :)
    public boolean isGameState() {
        return gameState.get();
    }

    // Added by James :)
    public BooleanProperty gameStateProperty() {
        return gameState;
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

    /** For hints (?)
     *
     * @return
     */
    public boolean solvePuzzle() {
        LinkedList<ArrayList<Block>> queue = new LinkedList<>();
        ArrayList<ArrayList<Block>> visited = new ArrayList<>();
        ArrayList<Block> blocksCopy = new ArrayList<>();
        for (Block b : this.blocks) blocksCopy.add(b);
        queue.add(blocksCopy);
        while (!queue.isEmpty()) {
            ArrayList<Block> curr = queue.poll();
            for (Block block : curr) {
                if (block.getID().equals("z")) {
                    if (block.getPosition().get(0)[1] == 6 - block.getSize()) return true;
                }
            }
            if (visited.contains(curr)) continue;

            visited.add(curr);

            ArrayList<ArrayList<Block>> nextPossible = new ArrayList<>();
            nextPossible = getNextPossibleMoves(curr);
            for (int i = 0; i < nextPossible.size(); i++) {
                ArrayList<Block> newBlock = new ArrayList<>();
                newBlock = nextPossible.get(i);
                queue.add(newBlock);
            }
        }
        return false;
    }

    /** Helper function for solvePuzzle()
     *
     * @param currState
     * @return
     */
    public ArrayList<ArrayList<Block>> getNextPossibleMoves(ArrayList<Block> currState) {
        ArrayList<ArrayList<Block>> possible = new ArrayList<>();
        for (Block b : currState) {
            if (b.isHorizontal()) {
                int row = b.getRow();
                int col = b.getCol();
                if (col > 0 && (this.grid.get(col - 1)[row].equals("*"))) {
                    int newCol = col;
                    while (newCol > 0 && this.grid.get(newCol - 1)[row].equals("*")) {
                        newCol--;
                    }

                    ArrayList<Block> newBlocks = new ArrayList<>();
                    for (Block block : currState) {
                        if (block.getID().equals(b.getID())) {
                            Block newBlock = new Block(block.getID(), row, newCol);
                            for (int i = 1; i < block.getSize(); i++) {
                                Integer[] newPosition = new Integer[2];
                                newPosition[0] = row;
                                newPosition[1] = newCol + i;
                                newBlock.addPosition(newPosition);
                            }
                            newBlocks.add(newBlock);
                        } else {
                            newBlocks.add(block.duplicate());
                        }
                    }
                    possible.add(newBlocks);
                }

                if (col + b.getSize() < 6 && (this.grid.get(col + b.getSize())[row].equals("*"))) {
                    int newCol = col;
                    while (newCol < 6 - b.getSize() && this.grid.get(newCol)[row].equals("*")) {
                        newCol++;
                    }

                    ArrayList<Block> newBlocks = new ArrayList<>();
                    for (Block block : currState) {
                        if (block.getID().equals(b.getID())) {
                            Block newBlock = new Block(block.getID(), row, newCol);
                            for (int i = 1; i < block.getSize(); i++) {
                                Integer[] newPosition = new Integer[2];
                                newPosition[0] = row;
                                newPosition[1] = newCol + i;
                                newBlock.addPosition(newPosition);
                            }
                            newBlocks.add(newBlock);
                        } else {
                            newBlocks.add(block.duplicate());
                        }
                    }
                    possible.add(newBlocks);
                }
            } else {
                int row = b.getRow();
                int col = b.getCol();
                if (row > 0 && (this.grid.get(col)[row - 1].equals("*"))) {
                    int newRow = row;
                    while (newRow > 0 && this.grid.get(col)[newRow - 1].equals("*")) {
                        newRow--;
                    }

                    ArrayList<Block> newBlocks = new ArrayList<>();
                    for (Block block : currState) {
                        if (block.getID().equals(b.getID())) {
                            Block newBlock = new Block(block.getID(), newRow, col);
                            for (int i = 1; i < block.getSize(); i++) {
                                Integer[] newPosition = new Integer[2];
                                newPosition[0] = newRow + i;
                                newPosition[1] = col;
                                newBlock.addPosition(newPosition);
                            }
                            newBlocks.add(newBlock);
                        } else {
                            newBlocks.add(block.duplicate());
                        }
                    }
                    possible.add(newBlocks);
                }

                if (row + b.getSize() < 6 && (this.grid.get(col)[row + b.getSize()].equals("*"))) {
                    int newRow = row;
                    while (newRow < 6 - b.getSize() && this.grid.get(col)[newRow + b.getSize()].equals("*")) {
                        newRow++;
                    }

                    ArrayList<Block> newBlocks = new ArrayList<>();
                    for (Block block : currState) {
                        if (block.getID().equals(b.getID())) {
                            Block newBlock = new Block(block.getID(), newRow, col);
                            for (int i = 1; i < block.getSize(); i++) {
                                Integer[] newPosition = new Integer[2];
                                newPosition[0] = newRow + i;
                                newPosition[1] = col;
                                newBlock.addPosition(newPosition);
                            }
                            newBlocks.add(newBlock);
                        } else {
                            newBlocks.add(block.duplicate());
                        }
                    }
                    possible.add(newBlocks);
                }
            }
        }
        return possible;
    }

    /**
     * If possible, use the algorithm to generate for Campaigns, too. The sandbox is a live performance
     *
     * Might be changed:
        Difficulty metrics:
        * num of steps
        * num of occupyingBoxes
        * num of red block moves
        * num of notMaximalMoves
        Initial expectation:
        1. Trivial: 3-9 steps, >= 14 empty boxes for block-moving spaces (<= 22 occboxes)
        2. Easy: 10-14 steps, >= 14 empties
                   15-19 steps, >= 14 empties
        3. Medium: 15-19 steps, <= 13 empties
                   20-29 steps
        4. Hard: 30-39 steps
        5. Extreme: 40+
     */
    public void generateLevel () {
        /*
         Alternative 1: Put random boxes one per one, calculate the shortest path.
                        Pick the longest one, repeat. (Inspired: https://github.com/Unknowncmbk/unblock-me-generator)
                        Chance based, maybe long.
         Alternative 2: BFS, restriction for connection
                           * do not move same block twice
                           * do not move s.t. the ranges of blocks do not differ
        */
    }

}