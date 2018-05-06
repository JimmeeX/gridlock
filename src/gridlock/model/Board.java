package gridlock.model;
//do we really need an id for a block?
//hmm I reckon the id is to look for the block
//(say when we move a block, we refer to them by their idk) - Alina

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Board {
    private Difficulty difficulty;
    private Mode mode;
    private Integer level;
    private ArrayList<ArrayList<String>> grid;
    private ArrayList<Block> blocks;
    private int numOfMoves;
    private ArrayList<Block> prevLocations;
    private ArrayList<Block> nextLocations;

    public Board() {
        initialiseGrid(6);
        this.blocks = new ArrayList<>();
        this.numOfMoves = 0;
        this.prevLocations = new ArrayList<>();
        this.nextLocations = new ArrayList<>();
    }

    public void process(String fileName) {
        Scanner sc = null;
        try {
            sc = new Scanner(new File(fileName));
            String prevID = "*";
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
            printGrid();
            printBlock();
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        } finally {
            if (sc != null) sc.close();
        }
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

    // CHANGED THIS TO GETTING THE GRID INSTEAD
    public ArrayList<ArrayList<String>> getBoard() {
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

    @Override
    public String toString() {
        return "Gridlock{" +
                "board=" + this.grid +
                ", difficulty=" + difficulty +
                ", mode=" + mode +
                ", level=" + level +
                '}';
    }

    public void initialiseGrid(int size) {
        this.grid = new ArrayList<>();
        for (int row = 0; row < size; row++) {
            ArrayList<String> newRow = new ArrayList<>();
            for (int col = 0; col < size; col++) {
                newRow.add("*");
            }
            this.grid.add(newRow);
        }
    }

    public void printGrid() {
        for (ArrayList<String> row: grid) {
            for (String cell: row) {
                System.out.print(cell + " ");
            }
            System.out.println();
        }
        System.out.println("");
    }

    public void addBlock(String id, int row, int col){
        Block newBlock = new Block(id, row, col);
        this.blocks.add(newBlock);
        ArrayList<String> newRow = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            if (col != i) newRow.add(this.grid.get(row).get(i));
            else newRow.add(newBlock.getID());
        }
        this.grid.add(row, newRow);
        this.grid.remove(row + 1);
    }

    public int blockExist(String id) {
        for (int block = 0; block < this.blocks.size(); block++) {
            if (this.blocks.get(block).getID().equals(id)) return block;
        }
        return -1;
    }

    public void incrementSize(int id, int row, int col) {
        Block thisBlock = this.blocks.get(id);
        if (thisBlock.getCol() == col) thisBlock.addRow(row);
        else thisBlock.addCol(col);
        ArrayList<String> newRow = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            if (col != i) newRow.add(this.grid.get(row).get(i));
            else newRow.add(thisBlock.getID());
        }
        this.grid.add(row, newRow);
        this.grid.remove(row + 1);
    }

    public void printBlock() {
        for (Block block: this.blocks) {
            System.out.println(block.toString());
        }
    }

}
