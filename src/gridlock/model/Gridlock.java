package gridlock.model;

public class Gridlock {

    private Board board;

    public Gridlock() {
        this.board = new Board();
    }

    public static void main(String[] args) {
        Gridlock gl = new Gridlock();
        gl.process();
    }

    public void process() {
        this.board.printGrid();
    }
}