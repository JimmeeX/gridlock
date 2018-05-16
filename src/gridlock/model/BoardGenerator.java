package gridlock.model;

import java.util.*;

public class BoardGenerator {

    private class Node {
        Board board;
        boolean isWin;
        List <Node> neighbors;
        // boolean isVisited;

        Node (Board board) {
            this.board = board;
            this.isWin = (this.board.getBlocks("z") != null && );
            this.neighbors = new ArrayList<>();
            // developo neigbor lists
        }

        void addOneNeighbor (Node n) {
            this.neighbors.add(n);
        }

        /**
         * Two nodes are equal, iff each block (with same id)'s range is exactly equal
         * @param obj
         * @return
         */
        @Override
        public boolean equals (Object obj) {
            // Assume for now there is no inheritance of Node
            if (!obj.getClass().equals("Node")) return false;
            Node n = (Node) obj;
            return isAllProper(n) && isAllSameRange(n);
        }

        private boolean isAllProper (Node n) {
            for (Block thisBlock: this.board.getBlocks()) {
                Block thatBlock = n.board.getBlock(thisBlock.getID());
                if (thatBlock == null) return false;
                boolean a = thisBlock.isHorizontal(); boolean b = thatBlock.isHorizontal();
                if (a != b) return false;
                if (!(a ? thisBlock.getRow() == thatBlock.getRow() : thisBlock.getCol() == thatBlock.getCol()))
                    return false;
            }
            return true;
        }

        private boolean isAllSameRange (Node n) {
            for (Block thisBlock: this.board.getBlocks()) {
                String id = thisBlock.getID();
                if (!this.board.blockRange(id).equals(n.board.blockRange(id))) return false;
            }
            return true;
        }
    }

    private List <Node> winNodeList = new ArrayList<>();

    public Board generateOneBoard () {
        Node initWinNode = new Node (newRandomWinBoard());
        // BFS
        PriorityQueue <Node> queue = new PriorityQueue<>();
        ArrayList <Node> nodeList = new ArrayList<>(); // the visited nodes will soon be the node lists
        queue.add(initWinNode);
        while (!queue.isEmpty()) {
            Node curr = queue.poll();
            if (nodeList.contains(curr)) continue;
            nodeList.add(curr);
            for (Node n : curr.neighbors) queue.add(n);
        }
        // We now have all nodeList
        return null; // SOON

    }

    private List <Board>

    // -prvt
    // Bare: sometimes working sometimes not
    private Board newRandomWinBoard() {
        int currNumOfBlock = 0;
        Board b = new Board ();
        List <String []> grid = b.getGrid();
        if (b.addBlock("z", 2, 4, 2, true)) currNumOfBlock++;
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
                if (b.addBlock(id, i, j, size[sizeIdx], isHorizontal[isHorizontalIdx])) continue;
                if (b.addBlock(id, i, j, size[sizeIdx], isHorizontal[1-isHorizontalIdx])) continue;
                if (b.addBlock(id, i, j, size[1-sizeIdx], isHorizontal[isHorizontalIdx])) continue;
                if (b.addBlock(id, i, j, size[1-sizeIdx], isHorizontal[1-isHorizontalIdx])) continue;
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
