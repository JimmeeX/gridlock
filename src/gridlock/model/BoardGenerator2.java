//package gridlock.model;
//
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.util.*;
//
//public class BoardGenerator2 {
//
//    private class Node {
//        Board board;
//        boolean isOneStepToWin;
//        List <Integer []> nextStepShouldCoverThisPositions;
//        int dist;
//        Node pred = null;
//        String predBlockID = null;
//
//        Node (Board board) {
//            this.board = board;
//            Block zBlock = board.getBlock("z");
//            if (zBlock != null && this.board.blockRange("z")[1] == 4 && zBlock.getCol() != 4)
//                this.isOneStepToWin = true;
//            else this.isOneStepToWin = false;
//            this.nextStepShouldCoverThisPositions = new ArrayList<>();
//            this.dist = 100; // so far there has not been puzzle with >= 100 moves
//        }
//
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
//    }
//
//    public Board generateOneBoard (String file) {
//        long startTime = System.nanoTime();
//
//        Node initWinNode = new Node (process(file));
//        Queue <Node> queue = new LinkedList<>();
//        List <Node> nodeList = new ArrayList<>(); // the visited nodes will soon be the node lists
//        initWinNode.dist = 0;
//        queue.add(initWinNode);
//        nodeList.add(initWinNode);
//        // BFS
//        while (!queue.isEmpty()) {
//            Node curr = queue.poll();
//            System.out.println ("Node list index " + (int)(nodeList.size() - queue.size()) + " out of " + nodeList.size());
//            List <Map.Entry <String, Integer []>> biaptcList = blockIDAndPlacesToConsider(curr);
//            for (Map.Entry <String, Integer []> e: biaptcList) {
//                String id = e.getKey();
//                Integer [] position = e.getValue();
//
//                System.out.println("XX " + id +  position[0] + position[1] + "\n");
//                Board duplicate = curr.board.duplicate();
//                duplicate.makeMove(id, position, true);
//                Node newNode = new Node(duplicate);
//                if (curr != initWinNode && newNode.isOneStepToWin) continue;
//                if (nodeList.indexOf(newNode) == -1) {
//                    // is not visited before
//                    nodeList.add(newNode);
//                    queue.add(newNode);
//                    newNode.dist = curr.dist + 1;
//                    newNode.pred = curr;
//                    newNode.predBlockID = id;
//                }
//                //if (j==1) System.out.println("Duplicates:"); duplicate.printGrid();
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
//        // Find maximal, print with max num of moves
//        Node maxNode = nodeList.get(nodeList.size()-1);
//        maxNode.board.printGrid();
//        System.out.println("Claim Max move: " + maxNode.dist);
///*
//        // Backtracking
//        System.out.println("Backward check . . .");
//        for (Node x = maxNode; x != null; x = x.pred) {
//            x.board.printGrid();
//            System.out.println("Max move: " + x.dist);
//        }
//*/
//        long endTime = System.nanoTime();
//        long duration = (endTime - startTime)/1000000;
//        System.out.println("Duration " + duration + "/1000 seconds.");
//        return maxNode.board;
//    }
//
//    private List <Map.Entry <String, Integer []>> blockIDAndPlacesToConsider (Node curr) {
//        List <Map.Entry <String, Integer []>> finalListofMaps = new ArrayList<>();
//
//        // If is win node, consider z's move. Consider any unit grid before z to be placed
//        // Otherwise, check pred node, which block id was moved, find exact places to be "obstacled".
//        // Any blocks that can pass / cover??? that space should be considered (except the last block moved)
//        if (curr.board.getBlock("z").getCol() == 4) {
//            Integer[] intv = curr.board.blockRange("z");
//            for (int i = intv[0]; i <= intv [1]-1; i++) {
//                AbstractMap.SimpleEntry x = new AbstractMap.SimpleEntry <> ("z", new Integer[]{2, i});
//                finalListofMaps.add(x);
//            }
//        } else {
//            String pbi = curr.predBlockID;
//            Block predBlock = curr.board.getBlock(pbi);
//
//            List <Integer []> vacantPositions = new ArrayList<>();
//            Integer [] posPred = curr.pred.board.getBlock(pbi).getPosition().get(0);
//            Integer [] posCurr = predBlock.getPosition().get(0);
//            if (predBlock.isHorizontal()) {
//                if (posPred[1] < posCurr[1]) {
//                    for (int i = posPred[1]; i < posCurr[1]; i++)
//                        vacantPositions.add(new Integer[]{posCurr[0], i});
//                } else {
//                    for (int i = posPred[1]; i > posCurr[1]; i--)
//                        vacantPositions.add(0, new Integer[]{posCurr[0], i+predBlock.getSize()-1});
//                }
//            } else {
//                if (posPred[0] < posCurr[0]) {
//                    for (int i = posPred[0]; i < posCurr[0]; i++)
//                        vacantPositions.add(new Integer[]{i, posCurr[1]});
//                } else {
//                    for (int i = posPred[0]; i > posCurr[0]; i--)
//                        vacantPositions.add(0, new Integer[]{i+predBlock.getSize()-1, posCurr[1]});
//                }
//            }
//            System.out.println("YA " + pbi +" "+ vacantPositions.get(0)[0] + vacantPositions.get(0)[1]);
//            curr.board.printGrid();
//
//            for (Block b: curr.board.getBlocks()) {
//                if (b == predBlock) continue;
//                System.out.println("POK" + b.getID());
//                Integer[] bintv = curr.board.blockRange(b.getID());
//                for (Integer [] vPos: vacantPositions) {
//                    if (b.isHorizontal() && vPos[0] == b.getRow()) {
//                        System.out.println("AA" + Arrays.toString(bintv));
//                        for (int i = bintv[0]; i <= bintv[1]; i++) {
//                            // We only consider blocks that bring additional obstacles
//                            if (i <= vPos[1] && vPos[1] <= i+b.getSize()-1){
//                                AbstractMap.SimpleEntry x =
//                                        new AbstractMap.SimpleEntry <> (b.getID(), new Integer[]{b.getRow(), i});
//                                finalListofMaps.add(x);
//                            }
//                        }
//                    } else if (!b.isHorizontal() && vPos[1] == b.getCol()) {
//                        System.out.println("AA" + Arrays.toString(bintv));
//                        for (int i = bintv[0]; i <= bintv[1]; i++) {
//                            if (i <= vPos[0] && vPos[0] <= i+b.getSize()-1){
//                                AbstractMap.SimpleEntry x =
//                                        new AbstractMap.SimpleEntry <> (b.getID(), new Integer[]{i, b.getCol()});
//                                finalListofMaps.add(x);
//                            }
//                        }
//                    }
//                }
//            }
//            System.out.println("YO " + finalListofMaps.size() + "\n");
//        }
//        return finalListofMaps;
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
//}
