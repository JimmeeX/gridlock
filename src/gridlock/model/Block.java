package gridlock.model;

import java.util.ArrayList;

/**
 * Block class designed to store a block's information such as
 * its id and position on the grid
 */
public class Block {

    private String id;
    private ArrayList<Integer[]> position;

    /**
     * Block class constructor
     * @param id the id of the block
     * @param row the row position of the block
     * @param col the col position of the block
     */
    public Block(String id, int row, int col) {
        this.id = id;
        this.position = new ArrayList<>();
        Integer[] newPosition = new Integer[2];
        newPosition[0] = row;
        newPosition[1] = col;
        this.position.add(newPosition);
    }

    public void addPosition(Integer[] newPosition) {
        this.position.add(newPosition);
    }

    /**
     * get the ID of the block
     * @return the id of the block
     */
    public String getID() {
        return this.id;
    }

    /**
     * check if the block is horizontal or vertical
     * @return true if the block is horizontal
     * @return false if the block is vertical
     */
    public boolean isHorizontal() {
        return (position.get(0)[0] == position.get(1)[0]);
    }

    /**
     * get the size of the block
     * @return the size of the block
     */
    public int getSize() {
        return position.size();
    }

    public ArrayList<Integer[]> getPosition() {
        return this.position;
    }

    /**
     * get the starting column of the block
     * @return the starting column of the block
     */
    public int getCol() {
        return position.get(0)[1];
    }

    /**
     * get the starting row of the block
     * @return the starting row of the block
     */
    public int getRow() {
        return position.get(0)[0];
    }

    public void setNewPosition(Integer[] newPosition) {
        if (isHorizontal()) {
            for (int item = 0; item < this.position.size(); item++) {
                this.position.get(item)[1] = newPosition[1] + item;
            }
        } else {
            for (int item = 0; item < this.position.size(); item++) {
                this.position.get(item)[0] = newPosition[0] + item;
            }
        }
    }

    /**
     * to return the block's id and position details
     * @return the block's id and position details
     */
    public String toString() {
        String toRet =  id + " ";
        for (Integer[] position : this.position) {
            toRet = toRet + "(" +  position[0] + "," + position[1] + ") ";
        }
        return toRet;
    }

}
