package gridlock.model;

public class Block {

    private String id;
    private Integer[] startPosition;
    private Integer[] endPosition;

    public Block(String id, int row, int col) {
        this.id = id;
        this.startPosition = new Integer[2];
        this.endPosition = new Integer[2];
        startPosition[0] = endPosition[0] = row;
        startPosition[1] = endPosition[1] = col;
    }

    public void addRow(int row) {
        this.endPosition[0] = row;
    }

    public void addCol(int col) {
        this.endPosition[1] = col;
    }
    public String getID() {
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

    public String toString() {
        return (id + " " + startPosition[0] + "," + startPosition[1]
                + " " + endPosition[0] + "," + endPosition[1] + "\n");
    }

}
