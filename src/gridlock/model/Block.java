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

    /**
     * add a new position where the block is at
     * @param newPosition the new [row,col] the block is at
     */
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
     * check if the block is horizontal or vertical
     * @return true if the block is horizontal
     * @return false if the block is vertical
     */
    public boolean isHorizontal() {
        return (position.get(0)[0] == position.get(1)[0]);
    }

    public boolean samePosition(Integer[] newPosition) {
        if (newPosition[0] == this.position.get(0)[0]) {
            if (newPosition[1] == this.position.get(0)[1]) {
                //System.out.println(newPosition[0] + "," + newPosition[1]);
                return true;
            }
        }
        return false;
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

    /** Helper function for BFS
     *
     * @return
     */
    public Block duplicate() {
        int row = this.position.get(0)[0];
        int col = this.position.get(0)[1];
        Block newBlock = new Block(this.id, row, col);
        for (int i = 1; i < this.position.size(); i++) {
            Integer[] newPosition = new Integer[2];
            newPosition[0] = this.position.get(i)[0];
            newPosition[1] = this.position.get(i)[1];
            newBlock.addPosition(newPosition);
        }
        return newBlock;
    }
    
    /*@Override
	public int hashCode() {
		int hash = 1;
		hash = hash * 2 + ((this.id == null) ? 0 : this.id.hashCode());
		hash = hash * 3 + (this.isHorizontal() ? 0 : 1);
		hash = hash * 5 + this.getSize();
		hash = hash * 7 + this.getCol();
		hash = hash * 11 + this.getRow();
		return hash;
	}*/
    
    @Override
    public boolean equals(Object obj) {
		if (this == obj)
			return true;
    	if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Block other = (Block) obj;
		if (!this.id.equals(other.id))
			return false;
		if (this.isHorizontal() != other.isHorizontal())
			return false;
		if (this.getSize() != other.getSize())
			return false;
		if (this.getRow() != other.getRow())
			return false;
		if (this.getCol() != other.getCol())
			return false;
		/*System.out.println(this.id + " = " + other.id);
		System.out.println(this.isHorizontal() + " = " + other.isHorizontal());
		System.out.println(this.getSize() + " = " + other.getSize());
		System.out.println(this.getRow() + " = " + other.getRow());
		System.out.println(this.getCol() + " = " + other.getCol());
		*/
		return true;
    }

}
