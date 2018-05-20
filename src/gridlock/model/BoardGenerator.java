package gridlock.model;

import sun.java2d.pipe.AAShapePipe;

import java.util.*;
import java.io.*;

public class BoardGenerator {

    private class Node {
        Board board;
        boolean isWin;
        boolean isVisited;
        int dist;
        Node pred;

        Node (Board board) {
            this.board = board;
            Block zBlock = board.getBlock("z");
            if (zBlock != null && Arrays.equals(zBlock.getPosition().get(0), new Integer[] {2, 4})) this.isWin = true;
                else this.isWin = false;
            this.isVisited = false;
            this.dist = 60; // so far there has not been puzzle with >= 49
            this.pred = null;
        }

        /**
         * Two nodes are equal, iff each block (with same id)'s range is exactly equal
         * AND for each block id A, and for every other blocks with diff orientation whose range
         * intersects A, A in both nodes are in same hemispheres
         * Execption holds when a node is a win node. In this case if the z block is located differently,
         * assume they are different.
         * @param obj
         * @return
         */
        @Override
        public boolean equals (Object obj) {
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

        private boolean isSameRange (Node n) {
            // After ensuring n is "proper", comparing range
            for (Block thisBlock : this.board.getBlocks()) {
                String id = thisBlock.getID();
                Block thatBlock = n.board.getBlock(id);
                int thisRow = thisBlock.getRow();
                int thisCol = thisBlock.getCol();
                int thatRow = thatBlock.getRow();
                int thatCol = thatBlock.getCol();
                Integer [] br1 = this.board.blockRange(id);
                Integer [] br2 = n.board.blockRange(id);
                //System.out.println("id " + id + " br1 " + Arrays.toString(br1) + " br2 " + Arrays.toString(br2));
                //System.out.println("loc1 " + thisRow + thisCol + " loc2 " + thatRow + thatCol);
                if (br1 [0] != br2 [0] || br1 [1] != br2 [1]) return false;
                //System.out.println("pass range");
            }
            return true;
        }

        private boolean isSameHemisphere (Node n) {
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
                    Integer [] thisBr = this.board.blockRange(id);
                    Integer [] otherBr = this.board.blockRange(otherOrientationBlock.getID());
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

    public Board generateAPuzzle (Difficulty d) {
        Board result = null;
        int retry = 0;
        while (result == null && retry < 100) {
            result = generateOneBoard(generateWinBoard(d), lowestNumOfMoves(d), highestNumOfMoves(d));
            retry++;
        }
        if (result == null) {
            String level = d.equals(d.valueOf("EASY")) ? "easy" : d.equals(d.valueOf("MEDIUM")) ? "medium" : "hard";
            result = process("src/gridlock/resources/" + level + "/20.txt");
            System.out.println("Too long");
        }
        return result;
    }

    public Board generateOneBoard (String file) {
        return generateOneBoard (process(file), 0 ,60);
    }
    private Board process (String file) {
        Board board = new Board ();
        Scanner sc = null;
        try {
            sc = new Scanner(new File(file));
            for (int row = 0; row < 6; row++) {
                for (int col = 0; col < 6; col++) {
                    String id = sc.next();
                    if (!id.equals("*")) {
                        int blockID = board.blockExist(id);
                        if (blockID != -1) board.incrementSize(blockID, row, col);
                        else board.setBlock(id, row, col);
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

    public Board generateOneBoard (Board winBoard, int minMoves, int maxMoves) {
        long startTime = System.nanoTime();

        /* BFS:
        * ) Queue is the open set
        * ) Adjacency is both the open set and closed set: the only thing that makes
        *   diff when a loop is just started/ended is that the open element always have empty arraylist
        */
        Node initWinNode = new Node (winBoard);
        Queue <Node> queue = new LinkedList<>();
        Map <Node, List <Node>> adjacency = new HashMap<>();
        Map <Node, List <Node>> adjacencyRefAB = new IdentityHashMap<>(30000);
        Map <List <Node>, Node> adjacencyRefBA = new IdentityHashMap<>(30000);

        List <Node> newNeighborList = new ArrayList<>();
        adjacency.put(initWinNode, newNeighborList);
        adjacencyRefAB.put(initWinNode, newNeighborList);
        adjacencyRefBA.put(newNeighborList, initWinNode);
        queue.add(initWinNode);
        while (!queue.isEmpty()) {
            Node curr = queue.poll();
            //System.out.println ("Node list index " + (int)(adjacency.size() - queue.size()) + " out of " + adjacency.size()+ "\n");
            // The neighbor constructions, "ignoring reference and duplicates", are done here. Otherwise it is gonna loop
            for (Block b: curr.board.getBlocks()) {
                // Consider all possibility of its new position (diff than currently), the new board is a neighbor
                Integer[] intv = curr.board.blockRange(b.getID());
                for (int i = intv[0]; i <= intv[1]; i++) {
                    Board duplicate = curr.board.duplicate();
                    if (b.isHorizontal()) {
                        if (i == b.getCol()) continue;
                        duplicate.makeMove(b.getID(), new Integer[]{b.getRow(), i}, true);
                    } else {
                        if (i == b.getRow()) continue;
                        duplicate.makeMove(b.getID(), new Integer[]{i, b.getCol()}, true);
                    }
                    Node potentNeighNode = new Node(duplicate);
                    if (!curr.isNeighbor(potentNeighNode)) continue;
                    // For Djikstra to work, eventually the node's neighbors should be reference based
                    // Hence every neighbor should be referenced equivalently to a node in nodeList,
                    // the premises would be that the nodeList will contain all sufficient nodes
                    // to cover all actual neighbors, and all neighbors will never be inserted
                    // something not in nodeList.
                    List <Node> currNeighbors = adjacencyRefAB.get(curr);
                    List <Node> pnnNeighbors = adjacency.get(potentNeighNode);
                    if (pnnNeighbors == null) {
                        // this pnn is brand new. Add it to curr's neighbor list
                        currNeighbors.add(potentNeighNode);
                        // and put it on queue
                        newNeighborList = new ArrayList<>();
                        adjacency.put(potentNeighNode, newNeighborList);
                        adjacencyRefAB.put(potentNeighNode, newNeighborList);
                        adjacencyRefBA.put(newNeighborList, potentNeighNode);
                        queue.add(potentNeighNode);
                    } else {
                        Node pnnOriginRef = adjacencyRefBA.get(pnnNeighbors);
                        // pnn is still on queue, w different ref (pnnNeighbors.size() == 0)
                        // or: potentNeighNode's board config is already on closed set before, w different ref
                        if (!(containsRef(currNeighbors, pnnOriginRef))) currNeighbors.add(pnnOriginRef);
                    }
                }
            }
        }
        long endTime = System.nanoTime();
        long duration = (endTime - startTime)/1000000;
        //System.out.println("Duration " + duration + "/1000 seconds.");
        //System.out.println("We have " + adjacency.size() + " nodes.");

        startTime = System.nanoTime();
        // Now, BFS/Djikstra using all win nodes first. Using node ref as it is, we will use adjacencyRefAB
        // This is still weak
        queue.clear();
        for (Node n: adjacencyRefAB.keySet()) {
            if (n.isWin) {
                n.dist = 0;
                n.isVisited = true;
                queue.add(n);
            }
        }
        while (!queue.isEmpty()) {
            Node curr = queue.poll();
            for (Node neighbor: adjacencyRefAB.get(curr)) {
                if (!neighbor.isVisited) {
                    neighbor.dist = curr.dist + 1;
                    neighbor.pred = curr;
                    neighbor.isVisited = true;
                    queue.add(neighbor);
                }
            }
        }

        // Conclusion: the most difficult puzzle in this graph, along w numOfMoves
        Node maxNode = initWinNode;
        for (Node n: adjacency.keySet()) if (n.dist > maxNode.dist) maxNode = n;
        //maxNode.board.printGrid();
        System.out.println("Claim Max move: " + maxNode.dist);
        // Backtracking
        /*System.out.println("Backward check . . .");
        for (Node x = maxNode; x != null; x = x.pred) {
            x.board.printGrid();
            System.out.println("Max move: " + x.dist);
        }*/
        endTime = System.nanoTime();
        duration = (endTime - startTime)/1000000;
        //System.out.println("Duration " + duration + "/1000 seconds.");
        return (minMoves <= maxNode.dist && maxNode.dist <= maxMoves) ? maxNode.board : null;
    }
    /* -prvt
     * Checking if a node list contains a node based on reference
     */
    private boolean containsRef (List <Node> nl, Node n) {
        if (n == null) return (this == null);
        for (Node x: nl) if (x == n) return true;
        return false;
    }

    /* -prvt
    * Bare: sometimes working sometimes not
    */
    public Board newRandomWinBoard(double p, int minBlockNum, int maxBlockNum) {
        int currNumOfBlock = 0;
        Board b = new Board ();
        List <String []> grid = b.getGrid();
        if (b.setBlock("z", 2, 4, 2, true)) currNumOfBlock++;

        Random random = new Random();
        int row = random.nextInt(5);
        int col = random.nextInt(1) + 4;
        boolean isVertical = randomBinaryChoice(true, false, 0.5);
        int s = randomBinaryChoice(2,3,0.5);

        if (b.setBlock("a", row,  col, s, isVertical)) currNumOfBlock++;
        // cheat
        grid.get(2)[3] = "-";

        for (int i = 0; i < grid.size(); i++) {
            if (i == 2) continue;
            for (int j = 0; j < grid.size(); j++) {
                if (!grid.get(i)[j].equals("*")) continue;
                String fillOrNot = randomBinaryChoice("yes", "no", p);
                if (fillOrNot.equals("no")) continue;
                String id = Character.toString((char)(97 + currNumOfBlock));
                boolean [] isHorizontal = {true, false};
                int [] size = {2, 3};
                int isHorizontalIdx = randomBinaryChoice(0, 1, 0.5);
                int sizeIdx = randomBinaryChoice(0, 1, 0.5);
                currNumOfBlock++;
                if (b.setBlock(id, i, j, size[sizeIdx], isHorizontal[isHorizontalIdx])) continue;
                if (b.setBlock(id, i, j, size[sizeIdx], isHorizontal[1-isHorizontalIdx])) continue;
                if (b.setBlock(id, i, j, size[1-sizeIdx], isHorizontal[isHorizontalIdx])) continue;
                if (b.setBlock(id, i, j, size[1-sizeIdx], isHorizontal[1-isHorizontalIdx])) continue;
                currNumOfBlock--;
            }
        }
        grid.get(2)[3] = "*";
        return (currNumOfBlock > minBlockNum && currNumOfBlock < maxBlockNum)
                ? b : newRandomWinBoard(p, minBlockNum, maxBlockNum);
    }
    private <E> E randomBinaryChoice (E item1, E item2, double probItem1) {
        return (Math.random() < probItem1) ? item1 : item2;
    }

    private Board generateWinBoard (Difficulty d) {
        double p;
        int minBlockNum;
        int maxBlockNum;
        if (d.equals(d.valueOf("EASY"))) {
            p = 0.3;
            minBlockNum = 4;
            maxBlockNum = 6;
        } else if (d.equals(d.valueOf("MEDIUM"))) {
            p = 0.5;
            minBlockNum = 7;
            maxBlockNum = 9;
        } else {
            p = 0.5;
            minBlockNum = 10;
            maxBlockNum = 13;
        }
        return newRandomWinBoard(p, minBlockNum, maxBlockNum);
    }

    private int lowestNumOfMoves (Difficulty d) {
        return d.equals(d.valueOf("EASY")) ? 3 : d.equals(d.valueOf("MEDIUM")) ? 8 : 14;
    }

    private int highestNumOfMoves (Difficulty d) {
        return d.equals(d.valueOf("EASY")) ? 7 : d.equals(d.valueOf("MEDIUM")) ? 13 : 20;
    }
}
