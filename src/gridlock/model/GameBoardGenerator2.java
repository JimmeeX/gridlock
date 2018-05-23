package gridlock.model;

import java.util.*;
import java.io.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import static java.lang.Thread.sleep;

public class GameBoardGenerator2 implements Runnable {

    private int minMoves;
    private int maxMoves;
    private int minBlocks;
    private int maxBlocks;
    private double fillInProb;
    private String keyToReferToCampaignMode;

    private ArrayList<GameBoard> medium;
    private ArrayList<GameBoard> hard;
    private boolean running;
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition notFull = lock.newCondition();
    private final Condition notEmpty = lock.newCondition();

    public GameBoardGenerator2() {
        this.medium = new ArrayList<>();
        this.hard = new ArrayList<>();
    }

    public GameBoard getEasy() {
        return generateAPuzzle(Difficulty.EASY);
    }

    public GameBoard getMedium() {
        GameBoard med;
        if (this.medium.size() > 0) {
            this.lock.lock();
            med = this.medium.remove(0);
            this.lock.unlock();
        } else {
            med = generateAPuzzle(Difficulty.MEDIUM);
        }
        return med;
    }

    public GameBoard getHard() {
        GameBoard h;
        if (this.hard.size() > 0) {
            this.lock.lock();
            h = this.hard.remove(0);
            this.lock.unlock();
        } else {
            h = generateAPuzzle(Difficulty.HARD);
        }
        return h;
    }

    private class Node {
        GameBoard board;
        boolean isWin;
        int dist;
        Node pred;

        Node (GameBoard board) {
            this.board = board;
            Block zBlock = board.getBlock("z");
            if (zBlock != null && Arrays.equals(zBlock.getPosition().get(0), new Integer[] {2, 4})) this.isWin = true;
            else this.isWin = false;
            this.dist = 60; // so far there has not been puzzle with >= 49
            this.pred = null;
        }

        /**
         * Two nodes are equal, iff each block (with same id)'s range is exactly equal
         * AND for each block id A, and for every other blocks with diff orientation whose range
         * intersects A, A in both nodes are in same hemispheres
         * Execption holds when a node is a win node. In this case if the z block is located differently,
         * assume they are different.
         *
         * @param obj
         * @return
         */
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            // Assume for now there is no inheritance of Node
            if (!obj.getClass().equals(this.getClass())) return false;
            Node n = (Node) obj;
            for (int i = 0; i < 6; i++) {
                for (int j = 0; j < 6; j++) {
                    if (!(this.board.getGrid().get(i)[j].equals(n.board.getGrid().get(i)[j]))) return false;
                }
            }
            return true;
        }

        @ Override
        public int hashCode () {
            // For first eleven block IDs (z, a, b, c, d, ...),  ordered and filled with
            // its changeable row/col val. 0-5 values, prime 7, size: 7^11 = 1.9 * 10^9
            int prime = 7;
            int result = 1; // recommended as non-zero
            for (int i = 0; i < 11; i++) {
                String id = Character.toString((char)('a' + (i == 10 ? 25 : i)));
                Block b = this.board.getBlock(id);
                int digit = (b == null ? 5 :
                        b.isHorizontal() ? b.getCol() : b.getRow());
                result = prime * result + digit;
            }
            return result;
        }

        public boolean isNeighbor (Node n) {
            // Decide if n is a neighbor (assuming only one move is performed), either:
            // * If one win and one not, accept directly
            // * If they are ranged differently, accept directly
            // * If same, check if thay are same hemisphere or not. Yes <=> reject
            return (!(isBothYesorNoWinCriteria(n) && isSameRange(n) && isSameHemisphere(n)));
        }

        private boolean isBothYesorNoWinCriteria (Node n) {
            // false only if one is winning and the other is not
            return ((this.isWin && n.isWin) || (!this.isWin && !n.isWin));
        }

        private boolean isSameRange(Node n) {
            // After ensuring n is "proper", comparing range
            for (Block thisBlock : this.board.getBlocks()) {
                String id = thisBlock.getID();
                Block thatBlock = n.board.getBlock(id);
                int thisRow = thisBlock.getRow();
                int thisCol = thisBlock.getCol();
                int thatRow = thatBlock.getRow();
                int thatCol = thatBlock.getCol();
                Integer[] br1 = this.board.blockRange(id);
                Integer[] br2 = n.board.blockRange(id);
                //System.out.println("id " + id + " br1 " + Arrays.toString(br1) + " br2 " + Arrays.toString(br2));
                //System.out.println("loc1 " + thisRow + thisCol + " loc2 " + thatRow + thatCol);
                if (br1[0] != br2[0] || br1[1] != br2[1]) return false;
                //System.out.println("pass range");
            }
            return true;
        }

        private boolean isSameHemisphere(Node n) {
            // After ensuring n is same ranges, comparing hemisphere
            for (Block thisBlock : this.board.getBlocks()) {
                String id = thisBlock.getID();
                Block thatBlock = n.board.getBlock(id);
                int thisRow = thisBlock.getRow();
                int thisCol = thisBlock.getCol();
                int thatRow = thatBlock.getRow();
                int thatCol = thatBlock.getCol();
                //System.out.println("id " + id + " this pos " + thisRow + thisCol + " that pos " + thatRow + thatCol);
                for (Block otherOrientationBlock : this.board.getBlocks()) {
                    boolean a = thisBlock.isHorizontal();
                    // To reduce nodes: otherOri should "intersect thisBlock's range"
                    if (a == otherOrientationBlock.isHorizontal()) continue;
                    int otherRow = otherOrientationBlock.getRow();
                    int otherCol = otherOrientationBlock.getCol();
                    Integer[] thisBr = this.board.blockRange(id);
                    Integer[] otherBr = this.board.blockRange(otherOrientationBlock.getID());
                    //System.out.println("id-other " + otherOrientationBlock.getID() + " other position " + otherRow + otherCol);
                    //System.out.println("thisbr " + Arrays.toString(thisBr) + " otherbr " + Arrays.toString(otherBr));
                    //System.out.println("thisblock itself is horizontal: " + String.valueOf(a));
                    if (a) {
                        if (thisBr[0] <= otherCol && thisBr[1] + thisBlock.getSize()-1 >= otherCol
                                && otherBr[0] <= thisRow && otherBr[1] + otherOrientationBlock.getSize()-1 >= thisRow
                                && (thisCol-otherCol)*(thatCol-otherCol) <= 0) return false;
                    } else {
                        if (thisBr[0] <= otherRow && thisBr[1] + thisBlock.getSize()-1 >= otherRow
                                && otherBr[0] <= thisCol && otherBr[1] + otherOrientationBlock.getSize()-1 >= thisCol
                                && (thisRow-otherRow)*(thatRow-otherRow) <= 0) return false;
                    }
                }
                //System.out.println("pass hemi");
            }
            return true;
        }
    }

    public GameBoard generateAPuzzle (Difficulty d) {
        GameBoard result = null;
        int retry = 0;

        generateInitialHeuristics (d);
        long startTime = System.nanoTime();
        while (result == null && retry < 50) {
            System.out.println("Retry " + retry);
            result = generateOneBoard();
            retry++;
        }
        long endTime = System.nanoTime();
        long duration = (endTime - startTime)/1000000;
        System.out.println("Duration " + duration + "/1000 seconds.");
        if (result == null) {
            System.out.println("Too long");
            result = new GameBoard();
            result.process("src/gridlock/resources/" + keyToReferToCampaignMode + "/20.txt");
            //+ "/" + (int)(1 + (new Random ()).nextInt(19))
        }
        result.setMinMoves();
        return result;
    }

    private void generateInitialHeuristics (Difficulty d) {
        if (d.equals(Difficulty.EASY)) {
            minMoves = 4;
            maxMoves = 7;
            minBlocks = 4;
            maxBlocks = 6;
            fillInProb = 0.3;
            keyToReferToCampaignMode = "easy";
        } else if (d.equals(Difficulty.MEDIUM)) {
            minMoves = 8;
            maxMoves = 13;
            minBlocks = 7;
            maxBlocks = 9;
            fillInProb = 0.5;
            keyToReferToCampaignMode = "medium";
        } else {
            minMoves = 14;
            maxMoves = 50;
            minBlocks = 8;
            maxBlocks = 12;
            fillInProb = 0.6;
            keyToReferToCampaignMode = "hard";
        }
    }

    private List <GameBoard> newRandomWinBoardList() {
        // The end list guarantees that in actual implementations, the returned board
        // is guaranteed to have a solution among these winning boards.
        GameBoard referencedWinBoard = newRandomWinBoard();

        // NEED: each row & col block numbers configurations to rearrange (Exception holds for "z")
        List <GameBoard> result = new ArrayList<>();
        List <GameBoard> tempResult = new ArrayList<>();
        result.add(new GameBoard());
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 2; j++) {
                for (GameBoard gb : result) {
                    // pre-con: all previous iterated i,j are covered in result list
                    GameBoard tempGb;
                    List<Block> bl = referencedWinBoard.particularRowOrColumnsBlockList(i, (j == 0));
                    if (bl.size() == 3) {
                        tempResult.add(gb);
                        for (Block b: bl) {
                            if (!gb.setBlock(b.getID(), b.getRow(), b.getCol(), b.getSize(), b.isHorizontal())) {
                                tempResult.remove(tempResult.size()-1);
                                break;
                            }
                        }
                    } else if (bl.size() == 2) {
                        // if there is one "z"
                        Block primaryBlock = bl.get(1);
                        Block secondaryBlock = bl.get(0);
                        if (j == 0 ? bl.get(0).getCol() < bl.get(1).getCol()
                                : bl.get(0).getRow() < bl.get(1).getRow()) {
                            primaryBlock = bl.get(0);
                            secondaryBlock = bl.get(1);
                        }
                        for (int pCount = 0; pCount + primaryBlock.getSize() - 1 < 6; pCount++) {
                            if (secondaryBlock.getID().equals("z")) {
                                tempGb = gb.duplicate();
                                if (tempGb.setBlock("z", 2, 4, 2, true) &&
                                        tempGb.setBlock(primaryBlock.getID(), j == 0 ? primaryBlock.getRow() : pCount,
                                                j == 0 ? pCount : primaryBlock.getCol(), primaryBlock.getSize(),
                                                primaryBlock.isHorizontal()))
                                        tempResult.add(tempGb);
                            } else {
                                for (int sCount = pCount + primaryBlock.getSize();
                                     sCount + secondaryBlock.getSize() - 1 < 6; sCount++) {
                                    tempGb = gb.duplicate();
                                    if (tempGb.setBlock(secondaryBlock.getID(), j == 0 ? secondaryBlock.getRow() : sCount,
                                            j == 0 ? sCount : secondaryBlock.getCol(), secondaryBlock.getSize(),
                                            secondaryBlock.isHorizontal()) &&
                                        tempGb.setBlock(primaryBlock.getID(), j == 0 ? primaryBlock.getRow() : pCount,
                                            j == 0 ? pCount : primaryBlock.getCol(), primaryBlock.getSize(),
                                            primaryBlock.isHorizontal()))
                                        tempResult.add(tempGb);
                                }
                            }
                        }
                    } else if (bl.size() == 1) {
                        Block b = bl.get(0);
                        if (b.getID().equals("z")) {
                            if (gb.setBlock("z", 2, 4, 2, true))
                                tempResult.add(gb);
                        } else {
                            for (int count = 0; count + b.getSize() - 1 < 6; count++) {
                                tempGb = gb.duplicate();
                                //System.out.println("Clearly ");
                                //tempGb.printGrid();
                                //System.out.println("is going to be replaced with " + b.getID() + (j == 0 ? b.getRow() : count)
                                //        + (j == 0 ? count : b.getCol()) + b.getSize() + b.isHorizontal());
                                if (tempGb.setBlock(b.getID(), j == 0 ? b.getRow() : count, j == 0 ? count : b.getCol(),
                                        b.getSize(), b.isHorizontal()))
                                    tempResult.add(tempGb);
                            }
                        }
                    } else {
                        tempResult.add(gb);
                    }
                }
                result = tempResult;
                tempResult = new ArrayList<>();
            }
        }
        System.out.println("Win list size " + result.size());
        //for(GameBoard gb: result) { System.out.println("****"); gb.printGrid();}
        return result;
    }
    private GameBoard newRandomWinBoard () {
        int currNumOfBlock = 0;
        GameBoard b = new GameBoard();
        List <String []> grid = b.getGrid();
        if (b.setBlock("z", 2, 4, 2, true)) currNumOfBlock++;
        // cheat
        grid.get(2)[3] = "-";

        Random random = new Random();
        int row = random.nextInt(5);
        int col = randomBinaryChoice(4, 5, 0.5);
        int s = randomBinaryChoice(2, 3, 0.5);
        if (b.setBlock("a", row, col, s, false)) currNumOfBlock++;

        for (int i = 0; i < grid.size(); i++) {
            if (i == 2) continue;
            for (int j = 0; j < grid.size(); j++) {
                if (!grid.get(i)[j].equals("*")) continue;
                String fillOrNot = randomBinaryChoice("yes", "no", fillInProb);
                if (fillOrNot.equals("no")) continue;
                String id = Character.toString((char) (97 + currNumOfBlock));
                boolean[] isHorizontal = {true, false};
                int[] size = {2, 3};
                int isHorizontalIdx = randomBinaryChoice(0, 1, 0.5);
                int sizeIdx = randomBinaryChoice(0, 1, 0.5);
                currNumOfBlock++;
                if (b.setBlock(id, i, j, size[sizeIdx], isHorizontal[isHorizontalIdx])) continue;
                if (b.setBlock(id, i, j, size[sizeIdx], isHorizontal[1 - isHorizontalIdx])) continue;
                if (b.setBlock(id, i, j, size[1 - sizeIdx], isHorizontal[isHorizontalIdx])) continue;
                if (b.setBlock(id, i, j, size[1 - sizeIdx], isHorizontal[1 - isHorizontalIdx])) continue;
                currNumOfBlock--;
            }
        }
        grid.get(2)[3] = "*";
        return (currNumOfBlock >= minBlocks && currNumOfBlock <= maxBlocks)
                ? b : newRandomWinBoard ();
    }
    private <E> E randomBinaryChoice(E item1, E item2, double probItem1) {
        return (Math.random() < probItem1) ? item1 : item2;
    }

    private GameBoard generateOneBoard () {
        /* BFS:
        * ) Queue is the open set
        * ) Adjacency is both the open set and closed set: the only thing that makes
        *   diff when a loop is just started/ended is that the open element always have empty arraylist
        */
        List <GameBoard> initWinBoardList = newRandomWinBoardList();

        int targetMoves = minMoves + (new Random ()).nextInt(maxMoves - minMoves);
        Queue <Node> queue = new LinkedList<>();
        List <Node> queueRecordList = new ArrayList <>();
        Map <Node, List <Node>> adjacency = new HashMap<>();
        Map <Node, List <Node>> adjacencyRefAB = new IdentityHashMap<>(30000);
        Map <List <Node>, Node> adjacencyRefBA = new IdentityHashMap<>(30000);
        Node initWinNode;
        List <Node> newNeighborList;

        for (GameBoard initWinBoard: initWinBoardList) {
            initWinNode = new Node (initWinBoard);
            initWinNode.dist = 0;
            newNeighborList = new ArrayList<>();
            adjacency.put(initWinNode, newNeighborList);
            adjacencyRefAB.put(initWinNode, newNeighborList);
            adjacencyRefBA.put(newNeighborList, initWinNode);
            queue.add(initWinNode);
        }
        while (!queue.isEmpty()) {
            Node curr = queue.poll();
            if (curr.dist > targetMoves) break;
            // The neighbor constructions, "ignoring reference and duplicates", are done here.
            for (Block b: curr.board.getBlocks()) {
                // Consider all possibility of its new position (diff than currently), then additional check if
                // they are really a neighbor to each other.
                Integer[] intv = curr.board.blockRange(b.getID());
                for (int i = intv[0]; i <= intv[1]; i++) {
                    GameBoard duplicate = curr.board.duplicate();
                    if (b.isHorizontal()) {
                        if (i == b.getCol()) continue;
                        duplicate.makeMove(b.getID(), new Integer[]{b.getRow(), i}, true);
                    } else {
                        if (i == b.getRow()) continue;
                        duplicate.makeMove(b.getID(), new Integer[]{i, b.getCol()}, true);
                    }
                    Node potentNeighNode = new Node(duplicate);
                    if (!curr.isNeighbor(potentNeighNode)) continue;
                    // For BFS to completely work, eventually the node's neighbors should be reference based
                    // Hence every neighbor should be referenced equivalently to a node in nodeList,
                    // the premises would be that the nodeList will contain all sufficient nodes
                    // to cover all actual neighbors, and all neighbors will never be inserted
                    // something not in nodeList.
                    List <Node> currNeighbors = adjacencyRefAB.get(curr);
                    List <Node> pnnNeighbors = adjacency.get(potentNeighNode);
                    if (pnnNeighbors == null) {
                        // this pnn is brand new. Add it to curr's neighbor list
                        currNeighbors.add(potentNeighNode);
                        // and put it on adjacencies and queue
                        newNeighborList = new ArrayList<>();
                        adjacency.put(potentNeighNode, newNeighborList);
                        adjacencyRefAB.put(potentNeighNode, newNeighborList);
                        adjacencyRefBA.put(newNeighborList, potentNeighNode);
                        queue.add(potentNeighNode);
                        // and define the dist
                        potentNeighNode.dist = curr.dist + 1;
                        potentNeighNode.pred = curr;
                    } else {
                        Node pnnOriginRef = adjacencyRefBA.get(pnnNeighbors);
                        // pnn is still on queue, w different ref
                        // or: potentNeighNode's board config is already on closed set before, w different ref
                        if (!(containsRef(currNeighbors, pnnOriginRef))) currNeighbors.add(pnnOriginRef);
                    }
                }
            }
            queueRecordList.add(curr);
        }
        //System.out.println("We have " + adjacency.size() + " nodes.");

        // Conclusion: the most difficult puzzle in this graph, along w numOfMoves
        Node maxNode = queueRecordList.get(queueRecordList.size()-1);
        while (maxNode.dist > maxMoves) maxNode = maxNode.pred;
        System.out.println("Claim max move: " + maxNode.dist);
/*
        // Backtracking
        System.out.println("Backward check . . .");
        for (Node x = maxNode; x != null; x = x.pred) {
            System.out.println("Claim max move: " + x.dist);
            x.board.printGrid();
        }
*/
        return (minMoves <= maxNode.dist) ? maxNode.board.duplicate() : null; // since prevLoc still exists
    }
    private boolean containsRef(List <Node> nl, Node n) {
        if (n == null) return (this == null);
        for (Node x : nl) if (x == n) return true;
        return false;
    }

    @Override
    public void run() {
        this.running = true;
        while (this.running) {
            Random random = new Random();
            int num = random.nextInt(19999);
            //System.out.println(" medium = " + medium.size() + " hard = " + hard.size());
            if (0 <= num && num <= 9999 && this.medium.size() <= 15) {
                addMediumPuzzle();
            } else if (this.hard.size() <= 15){
                addHardPuzzle();
            }
        }
    }

    public void addMediumPuzzle() {
        try {
            GameBoard med = generateAPuzzle(Difficulty.MEDIUM);
            this.lock.lock();
            this.medium.add(med);
        } finally {
            this.lock.unlock();
        }
    }

    public void addHardPuzzle() {
        try {
            GameBoard h = generateAPuzzle(Difficulty.HARD);
            this.lock.lock();
            this.hard.add(h);
        } finally {
            this.lock.unlock();
        }
    }

    public void stopThread() {
        this.running = false;
    }

}