package gridlock.model;

import java.util.ArrayList;

/**
 * Block class designed to store a block's information such as
 * its id and position on the grid
 * Added by Alina
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

	/**
	 * add a new position where the block is at
	 * @param newPosition the new [row,col] the block is at
	 * @post this.position.size()++
	 */
	public void addPosition(Integer[] newPosition) {
		this.position.add(newPosition);
	}

	/**
	 * change the position of a block
	 * @param newPosition the starting new position [row,col]
	 */
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

	/**
	 * check if the position passed coincides with the positions of the block
	 * @param newPosition [row,col] to be checked
	 * @return true if the position coincides with the block
	 * @return false if the position doesn't coincide with the block
	 */
	public boolean samePosition(Integer[] newPosition) {
        if (newPosition[0] == this.position.get(0)[0]) {
            if (newPosition[1] == this.position.get(0)[1]) {
                return true;
            }
        }
        return false;
    }

	/**
	 * Helper function for BFS to deep copy a block
	 * @return a clone of the current block
	 */
	public Block duplicate() {
		int row = this.position.get(0)[0];
		int col = this.position.get(0)[1];
		Block newBlock = new Block(this.id, row, col);
		for (int i = 1; i < this.position.size(); i++) {
			Integer[] newPosition = new Integer[2];
			Integer[] currPosition = this.position.get(i);
			newPosition[0] = currPosition[0];
			newPosition[1] = currPosition[1];
			newBlock.addPosition(newPosition);
		}
		return newBlock;
	}

    @Override
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

    @Override
    /**
     * generating hashCode for each block to implement hashMap for the graphs
     * @return the hashCode given
     */
	public int hashCode() {
		int hash = 1;
		hash = hash * 2 + this.id.hashCode();
		hash = hash * 3 + (this.isHorizontal() ? 0 : 1);
		hash = hash * 5 + this.getSize();
		hash = hash * 7 + this.getRow();
		hash = hash * 11 + this.getCol();
		return hash;
	}
    
    @Override
    /**
     * check if two objects are the same
     * @return true if two objects are the same
     * @return false if two objects are different
     */
    public boolean equals(Object obj) {
		if (this == obj) return true;
    	if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		Block other = (Block) obj;
		if (!this.id.equals(other.id)) return false;
		if (this.isHorizontal() != other.isHorizontal()) return false;
		if (this.getSize() != other.getSize()) return false;
		if (this.getRow() != other.getRow()) return false;
		if (this.getCol() != other.getCol()) return false;
		return true;
    }

}
