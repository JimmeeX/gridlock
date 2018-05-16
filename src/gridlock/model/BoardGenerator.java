package gridlock.model;

import java.util.*;
import java.io.*;

public class BoardGenerator {

    private class Node {
        Board board;
        boolean isWin;
        List <Node> neighbors;
        boolean djikIsVisited;
        int djikDist;
        Node djikPred;

        Node (Board board) {
            this.board = board;
            Block zBlock = board.getBlock("z");
            if (zBlock != null && Arrays.equals(zBlock.getPosition().get(0), new Integer[] {2, 4})) this.isWin = true;
                else this.isWin = false;
            this.neighbors = new ArrayList<>();
            this.djikIsVisited = false;
            this.djikDist = 100; // so far there has not been puzzle with >= 100 moves
            this.djikPred = null;
        }

        /**
         * Two nodes are equal, iff each block (with same id)'s range is exactly equal
         * @param obj
         * @return
         */
        @Override
        public boolean equals (Object obj) {
            // Assume for now there is no inheritance of Node
            if (!obj.getClass().equals(this.getClass())) return false;
            Node n = (Node) obj;
            return isAllSameRange(n);
        }

        // This is not actually contributing much
        private boolean isAllProper (Node n) {
            for (Block thisBlock: this.board.getBlocks()) {
                Block thatBlockWithSameID = n.board.getBlock(thisBlock.getID());
                if (thatBlockWithSameID == null) return false;
                boolean a = thisBlock.isHorizontal();
                boolean b = thatBlockWithSameID.isHorizontal();
                if (a != b) return false;
                if (!(a ? thisBlock.getRow() == thatBlockWithSameID.getRow()
                        : thisBlock.getCol() == thatBlockWithSameID.getCol()))
                    return false;
            }
            return true;
        }

        private boolean isAllSameRange (Node n) {
            for (Block thisBlock: this.board.getBlocks()) {
                String id = thisBlock.getID();
                Integer [] br1 = this.board.blockRange(id);
                Integer [] br2 = n.board.blockRange(id);
                if (br1 [0] != br2 [0] || br1 [1] != br2 [1]) return false;
            }
            return true;
        }
    }

    public Board generateOneBoard () {
        Board winBoard = null;
        while (winBoard == null) winBoard = process(); // newR.. ()
        // BFS: use lots of NOde's equals function
        Node initWinNode = new Node (winBoard);
        Queue <Node> queue = new LinkedList<>();
        List <Node> nodeList = new ArrayList<>(); // the visited nodes will soon be the node lists
        queue.add(initWinNode);
        nodeList.add(initWinNode);
        while (!queue.isEmpty()) {
            Node curr = queue.poll();
            // The neighbor "ignoring reference and duplicates" constructions is done here. Otherwise it is gonna loop
            for (Block b: curr.board.getBlocks()) {
                // Consider all possibility of its new position (diff than currently), the new board is a neighbor
                Integer[] intv =  curr.board.blockRange(b.getID());
                if (b.isHorizontal()) {
                    for (int i: intv) {
                        if (i == b.getCol()) continue;
                        Board duplicate = curr.board.duplicate();
                        duplicate.makeMove(b.getID(), new Integer [] {b.getRow(),i}, true);
                        curr.neighbors.add(new Node (duplicate));
                    }
                } else {
                    for (int i: intv) {
                        if (i == b.getRow()) continue;
                        Board duplicate = curr.board.duplicate();
                        duplicate.makeMove(b.getID(), new Integer [] {i,b.getCol()}, true);
                        curr.neighbors.add(new Node (duplicate));
                    }
                }
            }
            // For Djikstra to work, eventually the node's neighbors should be reference based
            // If the new found node exists in nodeList, use the existing one instead
            // Update curr's neighbors
            for (int i = 0; i < curr.neighbors.size(); i++) {
                Node n = curr.neighbors.get(i);
                int nIndex = nodeList.indexOf(n);
                if (nIndex != -1) {
                    // set curr's n reference to the similar NodeList
                    curr.neighbors.remove(i);
                    curr.neighbors.add(nodeList.get(nIndex));
                } else {
                    nodeList.add(n);
                    queue.add(n);
                }
            }
        }

        // We now have all nodeList
        System.out.println("We have " + nodeList.size() + " nodes.");

        // Currently BFS. If weight != 1, SOON: Djikstra.
        // Only consider node as it is
        for (Node n: nodeList) {
            if (n.isWin) {
                n.djikDist = 0;
                queue.add(n);
            }
        }
        while (!queue.isEmpty()) {
            Node curr = queue.poll();
            if (curr.djikIsVisited) continue; // Changed in Djikstra
            curr.djikIsVisited = true;
            for (Node neighbor: curr.neighbors) {
                if (neighbor.djikIsVisited) continue; // Changed in Djikstra
                neighbor.djikDist = curr.djikDist + 1;
                neighbor.djikPred = curr;
                queue.add(neighbor);
            }
        }

        // Find maximal, print with max num of moves
        Node maxNode = initWinNode;
        for (Node n: nodeList) if (n.djikDist > maxNode.djikDist) maxNode = n;
        Board oneBoard = maxNode.board;
        oneBoard.printGrid(); System.out.println("Max move: " + maxNode.djikDist);
        return oneBoard;
    }

    /* -prvt
    * Bare: sometimes working sometimes not
    */
    public Board newRandomWinBoard() {
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

    /* -prvt
	 * process input txt file
	 */
    private Board process() {
        Board board = new Board ();
        Scanner sc = null;
        try {
            sc = new Scanner(new File("src/gridlock/endGameState.txt"));
            for (int row = 0; row < 6; row++) {
                for (int col = 0; col < 6; col++) {
                    String id = sc.next();
                    if (!id.equals("*")) {
                        int blockID = board.blockExist(id);
                        if (blockID != -1) board.incrementSize(blockID, row, col);
                        else board.addBlock(id, row, col);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        } finally {
            if (sc != null) sc.close();
        }
        return board;
    }
}
