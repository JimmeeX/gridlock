package gridlock.model;
//do we really need an id for a block?
import java.util.ArrayList;

public class Board {

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

    public String toString() {
        String toRet = "";
        for (Block block: this.blocks) {
            toRet = toRet + block.toString();
        }
        return toRet;
    }

}
