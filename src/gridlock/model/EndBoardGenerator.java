package gridlock.model;

import java.util.*;

public class EndBoardGenerator {

    /**
     *
     * @param occupyingSpace: the number of 'box' occupying the 36 spaces
     * @return
     */
    public Board newEndBoard (int occupyingSpace) {
        if (occupyingSpace <= 4 && occupyingSpace >= 36) return null;
        int numSize3 = 12;
        // Find a random numSize3 and numSize2 based on 3a+2b=n
        if (occupyingSpace % 2 == 0) {
            // a = 2k (0 <= k <= \floor{n-2/6})
            while (numSize3 > 5) numSize3 = 2 * randomInclusive(0, (int) Math.floor((occupyingSpace-2) / 6));
        } else {
            // a = 1 + 2k (0 <= k <= \floor{n-5/6})
            while (numSize3 > 5) numSize3 = 1 + 2 * randomInclusive(0, (int) Math.floor((occupyingSpace -5)/ 6));
        }
        int numSize2 = (int) (occupyingSpace - 3 * numSize3)/2;

        Board b = new Board ();
        b.addBlock("z", 2, 4);
        b.incrementSize(b.blockExist("z"), 2, 5);
        numSize2--;

        List <Integer> chosenRow = new ArrayList <> (3, 3, 0, 1, 5);
        List <Integer> chosenCol = new ArrayList <> (5, 4, 3, 1, 1);
        List <Boolean> chosenIsHorizontal = new ArrayList <> (false, false, true, true, true);
        for (int i = 0; i < numSize3; i++) {
            b.addBlock("b" + i, chosenRow.get(i), chosenCol.get(i));
            if (chosenIsHorizontal.get(i)) {
                b.incrementSize(b.blockExist("b"+i), chosenRow.get(i), chosenCol.get(i)+1));
                b.incrementSize(b.blockExist("b"+i), chosenRow.get(i), chosenCol.get(i)+2));
            } else {
                b.incrementSize(b.blockExist("b"+i), chosenRow.get(i)+1, chosenCol.get(i)));
                b.incrementSize(b.blockExist("b"+i), chosenRow.get(i)+2, chosenCol.get(i));
            }
        }

        for (int i = 0; i < numSize2; i++) {
            int newCol; int newRow;
            while (true) {
                newCol = randomInclusive(0, 5);
                newRow = randomInclusive(0, 5);//SOON
            }
        }
    }

    /** Helper random number
     *
     * @param lower
     * @param upper
     * @return
     */-
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
