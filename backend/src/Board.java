//do we really need an id for a block?

import java.util.ArrayList;

public class Board {

    private ArrayList<ArrayList<Integer>> grid;
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
            ArrayList<Integer> newRow = new ArrayList<>();
            for (int col = 0; col < size; col++) {
                newRow.add(-1);
            }
            if(row == 2) newRow.add(-1);
            this.grid.add(newRow);
        }
    }

    /*public boolean validMove(Block block, Integer[] startPosition) {
        for (blocks in )
        return false;
    }*/

    public ArrayList<ArrayList<Integer>> getGrid() {
        return this.grid;
    }

    public void printGrid() {
        for (ArrayList<Integer> row: grid) {
            for (Integer cell: row) {
                System.out.print(cell + " ");
            }
            System.out.println();
        }
    }

}
