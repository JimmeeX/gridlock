package gridlock.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Gridlock {

    private Board board;
    private Difficulty difficulty;
    private Mode mode;
    private Integer level;

    public Gridlock() {
        this.board = new Board();
    }

    public void process(String fileName) {
        Scanner sc = null;
        try {
            sc = new Scanner(new File(fileName));
            String prevID = "*";
            for (int row = 0; row < 6; row++) {
                for (int col = 0; col < 6; col++) {
                    String id = sc.next();
                    if (!id.equals("*")) {
                        int blockID = this.board.blockExist(id);
                        if (blockID != -1) this.board.incrementSize(blockID, row, col);
                        else this.board.addBlock(id, row, col);
                    }
                }
            }
            this.board.printGrid();
            System.out.println(this.board.toString());
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        } finally {
            if (sc != null) sc.close();
        }
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