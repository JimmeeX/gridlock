package gridlock.model;

public class Gridlock {

    private Board board;
    private Difficulty difficulty;
    private Mode mode;
    private Integer level;

    public Gridlock() {
        this.board = new Board();
    }

//    public static void main(String[] args) {
//        Gridlock gl = new Gridlock();
//        gl.process();
//    }

    public void process() {
        this.board.printGrid();
    }

    public void setDifficulty(String diff) {
        this.difficulty = Difficulty.valueOf(diff.toUpperCase());
    }

    public void setMode(String gameMode) {
        this.mode = Mode.valueOf(gameMode.toUpperCase());
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Board getBoard() {
        return board;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public Mode getMode() {
        return mode;
    }

    public Integer getLevel() {
        return level;
    }

    @Override
    public String toString() {
        return "Gridlock{" +
                "board=" + board +
                ", difficulty=" + difficulty +
                ", mode=" + mode +
                ", level=" + level +
                '}';
    }
}