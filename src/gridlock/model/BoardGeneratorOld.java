//package gridlock.model;
//
//import sun.java2d.pipe.AAShapePipe;
//
//import java.util.*;
//import java.io.*;
//
//public class BoardGeneratorOld {
//
//    private class Node {
//        Board board;
//        boolean isWin;
//        List <Node> neighbors;
//        int dist;
//        Node pred;
//
//        Node (Board board) {
//            this.board = board;
//            Block zBlock = board.getBlock("z");
//            if (zBlock != null && Arrays.equals(zBlock.getPosition().get(0), new Integer[] {2, 4})) this.isWin = true;
//            else this.isWin = false;
//            this.neighbors = new ArrayList<>();
//            this.dist = 100; // so far there has not been puzzle with >= 100 moves
//            this.pred = null;
//        }
//
//        /**
//         * Two nodes are equal, iff each block (with same id)'s range is exactly equal
//         * AND for each block id A, and for every other blocks with diff orientation whose range
//         * intersects A, A in both nodes are in same hemispheres
//         * Execption holds when a node is a win node. In this case if the z block is located differently,
//         * assume they are different.
//         * @param obj
//         * @return
//         */
//        @Override
//        public boolean equals (Object obj) {
//            if (this == obj) return true;
//            // Assume for now there is no inheritance of Node
//            if (!obj.getClass().equals(this.getClass())) return false;
//            Node n = (Node) obj;
//            for (int i = 0; i < 6; i++) {
//                for (int j = 0; j < 6; j++) {
//                    if (!(this.board.getGrid().get(i)[j].equals(n.board.getGrid().get(i)[j]))) return false;
//                }
//            }
//            return true;
//        }
//
//        public boolean isNeighbor (Node n) {
//            // Decide if n is a neighbor, either:
//            // * Must be ranged differently
//            // * Must be not both win/not yet
//            // * Must be in diff hemisphere
//            return (!(isBothYesorNoWinCriteria(n) && isSameRange(n) && isSameHemisphere(n)));
//        }
//
//        private boolean isBothYesorNoWinCriteria (Node n) {
//            // false only if one is winning and the other is not
//            return ((this.isWin && n.isWin) || (!this.isWin && !n.isWin));
//        }
//
//        private boolean isSameRange (Node n) {
//            // After ensuring n is "proper", comparing range
//            for (Block thisBlock : this.board.getBlocks()) {
//                String id = thisBlock.getID();
//                Block thatBlock = n.board.getBlock(id);
//                int thisRow = thisBlock.getRow();
//                int thisCol = thisBlock.getCol();
//                int thatRow = thatBlock.getRow();
//                int thatCol = thatBlock.getCol();
//                Integer [] br1 = this.board.blockRange(id);
//                Integer [] br2 = n.board.blockRange(id);
//                //System.out.println("id " + id + " br1 " + Arrays.toString(br1) + " br2 " + Arrays.toString(br2));
//                //System.out.println("loc1 " + thisRow + thisCol + " loc2 " + thatRow + thatCol);
//                if (br1 [0] != br2 [0] || br1 [1] != br2 [1]) return false;
//                //System.out.println("pass range");
//            }
//            return true;
//        }
//
//        private boolean isSameHemisphere (Node n) {
//            // After ensuring n is same ranges, comparing hemisphere
//            for (Block thisBlock : this.board.getBlocks()) {
//                String id = thisBlock.getID();
//                Block thatBlock = n.board.getBlock(id);
//                int thisRow = thisBlock.getRow();
//                int thisCol = thisBlock.getCol();
//                int thatRow = thatBlock.getRow();
//                int thatCol = thatBlock.getCol();
//                //System.out.println("id " + id + " this pos " + thisRow + thisCol + " that pos " + thatRow + thatCol);
//                for (Block otherOrientationBlock : this.board.getBlocks()) {
//                    boolean a = thisBlock.isHorizontal();
//                    // To reduce nodes: otherOri should "intersect thisBlock's range"
//                    if (a == otherOrientationBlock.isHorizontal()) continue;
//                    int otherRow = otherOrientationBlock.getRow();
//                    int otherCol = otherOrientationBlock.getCol();
//                    Integer [] thisBr = this.board.blockRange(id);
//                    Integer [] otherBr = this.board.blockRange(otherOrientationBlock.getID());
//                    //System.out.println("id-other " + otherOrientationBlock.getID() + " other position " + otherRow + otherCol);
//                    //System.out.println("thisbr " + Arrays.toString(thisBr) + " otherbr " + Arrays.toString(otherBr));
//                    //System.out.println("thisblock itself is horizontal: " + String.valueOf(a));
//                    if (a) {
//                        if (thisBr[0] <= otherCol && thisBr[1] >= otherCol
//                                && otherBr[0] <= thisRow && otherBr[1] >= thisRow
//                                && (thisCol-otherCol)*(thatCol-otherCol) <= 0) return false;
//                    } else {
//                        if (thisBr[0] <= otherRow && thisBr[1] >= otherRow
//                                && otherBr[0] <= thisCol && otherBr[1] >= thisCol
//                                && (thisRow-otherRow)*(thatRow-otherRow) <= 0) return false;
//                    }
//                }
//                //System.out.println("pass hemi");
//            }
//            return true;
//        }
//    }
//
//    public Board generateOneBoard () {
//        return generateOneBoard ("src/gridlock/endGameState.txt");
//    }
//
//    public Board generateOneBoard (String file) {
//        long startTime = System.nanoTime();
//        Board winBoard = process(file);
//
//        // BFS: use lots of Node's equals function
//        Node initWinNode = new Node (winBoard);
//        Queue <Node> queue = new LinkedList<>();
//        List <Node> nodeList = new ArrayList<>(); // the visited nodes will soon be the node lists
//        queue.add(initWinNode);
//        nodeList.add(initWinNode);
//        while (!queue.isEmpty()) {
//            Node curr = queue.poll();
//            //System.out.println ("Node list index " + (int)(nodeList.size() - queue.size()) + " out of " + nodeList.size()+ "\n");
//            int j =0; if (nodeList.size() >= 1 && curr == nodeList.get(0)) j=1; //debug
//            // The neighbor "ignoring reference and duplicates" constructions is done here. Otherwise it is gonna loop
//            for (Block b: curr.board.getBlocks()) {
//                // Consider all possibility of its new position (diff than currently), the new board is a neighbor
//                Integer[] intv = curr.board.blockRange(b.getID());
//                for (int i = intv[0]; i <= intv[1]; i++) {
//                    Board duplicate = curr.board.duplicate();
//                    if (b.isHorizontal()) {
//                        if (i == b.getCol()) continue;
//                        duplicate.makeMove(b.getID(), new Integer[]{b.getRow(), i}, true);
//                    } else {
//                        if (i == b.getRow()) continue;
//                        duplicate.makeMove(b.getID(), new Integer[]{i, b.getCol()}, true);
//                    }
//                    Node newNode = new Node(duplicate);
//                    if (!curr.isNeighbor(newNode)) continue;
//                    // For Djikstra to work, eventually the node's neighbors should be reference based
//                    // Hence every neighbor should be referenced equivalently to a node in nodeList,
//                    // the premises would be that the nodeList will contain all sufficient nodes
//                    // to cover all actual neighbors, and all neighbors will never be inserted
//                    // something not in nodeList.
//                    // Thus we want to change every neigbhor that can be substituted with one n \in nodelist
//                    // then substitute it with n instead.
//                    int nIndex = nodeList.indexOf(newNode);
//                    if (nIndex != -1) {
//                        Node existingNode = nodeList.get(nIndex);
//                        if (!(containsRef(curr.neighbors, existingNode))) curr.neighbors.add(existingNode);
//                    } else {
//                        nodeList.add(newNode);
//                        queue.add(newNode);
//                        curr.neighbors.add(newNode);
//                    }
//                    //if (j==1) System.out.println("Duplicates:"); duplicate.printGrid();
//                }
//            }
//        }
//        System.out.println("We have " + nodeList.size() + " nodes.");
//
//        // Graph content
//        /*for (int i = 0; i < nodeList.size(); i++) {
//            System.out.println("NOde ke - " + i);
//            nodeList.get(i).board.printGrid();
//        }
//        for (int i = 0; i < nodeList.size(); i++) {
//            for (int j = 0; j < nodeList.size(); j++) {
//                if (nodeList.get(i).neighbors.contains(nodeList.get(j))) System.out.print("1 ");
//                else System.out.print("0 ");
//            }
//            System.out.println();
//        }*/
//
//        // Currently BFS. If weight != 1, SOON: Djikstra (using PQ)
//        // Only consider node as it is
//        List <Node> visited = new ArrayList<>();
//        for (Node n: nodeList) {
//            if (n.isWin) {
//                n.dist = 0;
//                visited.add(n);
//                queue.add(n);
//            }
//        }
//        while (!queue.isEmpty()) {
//            Node curr = queue.poll();
//            //System.out.println("Curr index " + (nodeList.indexOf(curr)) + ", now length" + curr.djikDist);
//            for (Node neighbor: curr.neighbors) {
//                //System.out.println("Neigh index " + (nodeList.indexOf(neighbor)) + ", now length" + neighbor.djikDist);
//                if (!containsRef(visited, neighbor)) {
//                    //System.out.println("new neighbor");
//                    neighbor.dist = curr.dist + 1;
//                    neighbor.pred = curr;
//                    visited.add(neighbor);
//                    queue.add(neighbor);
//                }
//            }
//        }
//
//        // Find maximal, print with max num of moves
//        Node maxNode = initWinNode;
//        for (Node n: nodeList) if (n.dist > maxNode.dist) maxNode = n;
//        maxNode.board.printGrid();
//        System.out.println("Claim Max move: " + maxNode.dist);
///*
//        // Backtracking
//        System.out.println("Backward check . . .");
//        for (Node x = maxNode; x != null; x = x.djikPred) {
//            x.board.printGrid();
//            System.out.println("Max move: " + x.djikDist);
//        }
//*/
//        long endTime = System.nanoTime();
//        long duration = (endTime - startTime)/1000000;
//        System.out.println("Duration " + duration + "/1000 seconds.");
//        return maxNode.board;
//    }
//
//    /* -prvt
//     *
//     */
//    private boolean containsRef (List <Node> nl, Node n) {
//        if (n == null) return (this == null);
//        for (Node x: nl) if (x == n) return true;
//        return false;
//    }
//
//    /* -prvt
//	 * process input txt file
//	 */
//    private Board process (String file) {
//        Board board = new Board ();
//        Scanner sc = null;
//        try {
//            sc = new Scanner(new File(file));
//            for (int row = 0; row < 6; row++) {
//                for (int col = 0; col < 6; col++) {
//                    String id = sc.next();
//                    if (!id.equals("*")) {
//                        int blockID = board.blockExist(id);
//                        if (blockID != -1) board.incrementSize(blockID, row, col);
//                        else board.setBlock(id, row, col);
//                    }
//                }
//            }
//        } catch (FileNotFoundException e) {
//            System.out.println(e.getMessage());
//        } finally {
//            if (sc != null) sc.close();
//        }
//        return board;
//    }
//
//    /* -prvt
//    * Bare: sometimes working sometimes not
//    */
//    public Board newRandomWinBoard() {
//        int currNumOfBlock = 0;
//        Board b = new Board ();
//        List <String []> grid = b.getGrid();
//        if (b.setBlock("z", 2, 4, 2, true)) currNumOfBlock++;
//        // cheat
//        grid.get(2)[3] = "-";
//
//        for (int i = 0; i < grid.size(); i++) {
//            if (i == 2) continue;
//            for (int j = 0; j < grid.size(); j++) {
//                if (!grid.get(i)[j].equals("*")) continue;
//                String fillOrNot = randomBinaryChoice("yes", "no", 0.3);
//                if (fillOrNot.equals("no")) continue;
//                String id = Character.toString((char)(96 + currNumOfBlock));
//                boolean [] isHorizontal = {true, false};
//                int [] size = {2, 3};
//                int isHorizontalIdx = randomBinaryChoice(0, 1, 0.5);
//                int sizeIdx = randomBinaryChoice(0, 1, 0.5);
//                currNumOfBlock++;
//                if (b.setBlock(id, i, j, size[sizeIdx], isHorizontal[isHorizontalIdx])) continue;
//                if (b.setBlock(id, i, j, size[sizeIdx], isHorizontal[1-isHorizontalIdx])) continue;
//                if (b.setBlock(id, i, j, size[1-sizeIdx], isHorizontal[isHorizontalIdx])) continue;
//                if (b.setBlock(id, i, j, size[1-sizeIdx], isHorizontal[1-isHorizontalIdx])) continue;
//                currNumOfBlock--;
//            }
//        }
//        grid.get(2)[3] = "*";
//        return (currNumOfBlock > 7 && currNumOfBlock < 12) ? b : null;
//    }
//    private <E> E randomBinaryChoice (E item1, E item2, double probItem1) {
//        return (Math.random() < probItem1) ? item1 : item2;
//    }
//
//}
