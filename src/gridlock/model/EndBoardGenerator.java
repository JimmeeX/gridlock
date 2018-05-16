package gridlock.model;

import java.util.*;

public class EndBoardGenerator {

    private int randomInclusive (int lower, int upper) {
        return lower + (int) Math.floor (Math.random() * (upper+1-lower));
    }

    // LEVEL GENERATION
    // Do it in background
    /* Search tree of one Solution
        Djikstra
     */
    public Board newEndBoard () {
        int numSize3 = 4;
        int numSize2 = 4;
        Board b = new Board ();
        b.addBlock("z", 2, 4);
        b.incrementSize(b.blockExist("z"), 2, 5);
        numSize2--;

        int newCol; int newRow; boolean newIsHorizontal; List <Boolean> isHorizontalArr = new ArrayList <> (true, false);
        for (int i = 0; i < numSize3; i++) {
            newRow = randomInclusive(0, 5);
            newCol = randomInclusive(0, 5);
            newIsHorizontal = isHorizontalArr.get(randomInclusive(0, 1));
            b.addBlock("b" + i, newRow, newCol, 3, newIsHorizontal);
        }

        for (int i = 0; i < numSize2; i++) {
            while (true) {
                newCol = randomInclusive(0, 5);
                newRow = randomInclusive(0, 5);//SOON
            }
        }
    }
}
