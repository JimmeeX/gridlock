
public class Block {

    private int id;
    private Integer[] startPosition;
    private Integer[] endPosition;

    public Block(int id, int size, boolean isHorizontal, int row, int col) {
        this.id = id;
        this.startPosition = new Integer[2];
        this.endPosition = new Integer[2];
        if(isHorizontal) {
            startPosition[0] = endPosition[0] = row;
            startPosition[1] = col;
            endPosition[1] = col + size - 1;
        } else {
            startPosition[1] = endPosition[1] = col;
            startPosition[0] = row;
            endPosition[0] = row + size - 1;
        }
    }

    public int getID() {
        return this.id;
    }

    public boolean isHorizontal() {
        return (startPosition[0].equals(endPosition[0]));
    }

    public int getSize() {
        return (Math.abs(endPosition[0] + endPosition[1] - startPosition[0] - startPosition[1]));
    }

    public int getRow() {
        return this.startPosition[0];
    }

    public int getCol() {
        return this.startPosition[1];
    }

}
