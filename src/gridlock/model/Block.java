package gridlock.model;

/**
 * Block class designed to store a block's information such as
 * its id and position on the grid
 */
public class Block {

    private String id;
    private Integer[] startPosition;
    private Integer[] endPosition;

    /**
     * Block class constructor
     * @param id the id of the block
     * @param row the row position of the block
     * @param col the col position of the block
     */
    public Block(String id, int row, int col) {
        this.id = id;
        this.startPosition = new Integer[2];
        this.endPosition = new Integer[2];
        startPosition[0] = endPosition[0] = row;
        startPosition[1] = endPosition[1] = col;
    }

    /**
     * extend the row of the block
     * @param row the row where the block extends to
     * @post startPosition[0] != endPosition[0]
     */
    public void addRow(int row) {
        this.endPosition[0] = row;
    }

    /**
     * extend the col of the block
     * @param col the col where the block extends to
     * @post startPosition[1] != endPosition[1]
     */
    public void addCol(int col) {
        this.endPosition[1] = col;
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
        return (startPosition[0].equals(endPosition[0]));
    }

    /**
     * get the size of the block
     * @return the size of the block
     */
    public int getSize() {
        return (Math.abs(endPosition[0] + endPosition[1] - startPosition[0] - startPosition[1]));
    }

    /**
     * get the starting row of the block
     * @return the starting position's row of the block
     */
    public int getRow() {
        return this.startPosition[0];
    }

    /**
     * get the starting col of the block
     * @return the starting position's col of the block
     */
    public int getCol() {
        return this.startPosition[1];
    }

    /**
     * to return the block's id and position details
     * @return the block's id and position details
     */
    public String toString() {
        return (id + " " + startPosition[0] + "," + startPosition[1]
                + " " + endPosition[0] + "," + endPosition[1]);
    }

}
