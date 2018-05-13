package gridlock.model;

public class BoardSimulation {

    /**
     * simulation of playing the game
     */
    public void playGame () {
        Board board = new Board();
        board.process("src/gridlock/resources/easy/1.txt");
        //board.printGrid();
        board.makeMove("e", newPosition(3,3));
        //board.printGrid();
        board.makeMove("b", newPosition(3,5));
        //board.printGrid();
        board.makeMove("a", newPosition(0,3));
        //board.printGrid();
        board.makeMove("c", newPosition(0,2));
        //board.printGrid();
        board.makeMove("e", newPosition(3,1));
        //board.printGrid();
        board.undoMove();
        board.undoMove();
        board.makeMove("a", newPosition(1,0));
        System.out.println(board.getBlocks().get(0).toString());
        //board.redoMove();
        //board.makeMove("z", newPosition(2,4));
        //board.restart();
    }
    /**
     * helper method for playing game
     * @param row the row position of the block
     * @param col the col position of the block
     * @return the array of position of the block (row,col)
     */
    private Integer[] newPosition(int row, int col) {
        Integer[] newPosition = new Integer[2];
        newPosition[0] = row;
        newPosition[1] = col;
        return newPosition;
    }
}
