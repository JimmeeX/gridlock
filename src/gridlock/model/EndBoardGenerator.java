package gridlock.model;

import java.util.*;

public class EndBoardGenerator {

    // Bare: sometimes working sometimes not
    public Board newEndBoard () {
        int currNumOfBlock = 0;
        Board b = new Board ();
        List <String []> grid = b.getGrid();
        if (b.setBlock("z", 2, 4, 2, true)) currNumOfBlock++;
        // cheat
        grid.get(2)[3] = "-";

        for (int i = 0; i < grid.size(); i++) {
            if (i == 2) continue;
            for (int j = 0; j < grid.size(); j++) {
                if (!grid.get(i)[j].equals("*")) continue;
                String fillOrNot = randomBinaryChoice("yes", "no", 0.6);
                if (fillOrNot.equals("no")) continue;
                String id = Character.toString((char)(96 + currNumOfBlock));
                boolean [] isHorizontal = {false, true};
                int [] size = {2, 3};
                int isHorizontalIdx = randomBinaryChoice(0, 1, 0.55);
                int sizeIdx = randomBinaryChoice(0, 1, 0.51);
                currNumOfBlock++;
                if (b.setBlock(id, i, j, size[sizeIdx], isHorizontal[isHorizontalIdx])) continue;
                if (b.setBlock(id, i, j, size[sizeIdx], isHorizontal[1-isHorizontalIdx])) continue;
                if (b.setBlock(id, i, j, size[1-sizeIdx], isHorizontal[isHorizontalIdx])) continue;
                if (b.setBlock(id, i, j, size[1-sizeIdx], isHorizontal[1-isHorizontalIdx])) continue;
                currNumOfBlock--;
            }
        }
        grid.get(2)[3] = "*";
        return (currNumOfBlock > 7 && currNumOfBlock < 12) ? b : null;
    }

    private <E> E randomBinaryChoice (E item1, E item2, double probItem1) {
        return (Math.random() < probItem1) ? item1 : item2;
    }
}
