package gridlock.model;

import java.util.*;
import java.io.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * GameBoardGenerator Class is designed to implement different level puzzle generator
 * and uses multithreading to generate medium and hard levels while the main program is running
 */
public class GameBoardGenerator implements Runnable {

    private Node maxNode = null;
    private ArrayList<GameBoard> medium;
    private ArrayList<GameBoard> hard;
    private boolean running;
    private final ReentrantLock lock = new ReentrantLock();

    /**
     * Constructor for GameBoardGenerator class
     */
    public GameBoardGenerator() {
        this.medium = new ArrayList<>();
        this.hard = new ArrayList<>();
    }

    /**
     * get an easy level puzzle
     * @return a GameBoard of level easy
     */
    public GameBoard getEasy() {
        return generatePuzzle(Difficulty.EASY);
    }

    /**
     * get a medium level puzzle
     * @return a GameBoard of level medium
     * @post medium.size()--
     */
    public GameBoard getMedium() {
        GameBoard med;
        if (this.medium.size() > 0) {
            this.lock.lock();
            med = this.medium.remove(0);
            this.lock.unlock();
        } else {
            med = generatePuzzle(Difficulty.MEDIUM);
        }
        return med;
    }

    /**
     * get a hard level puzzle
     * @return a GameBoard of level hard
     * @post hard.size()--
     */
    public GameBoard getHard() {
        GameBoard h;
        if (this.hard.size() > 0) {
            this.lock.lock();
            h = this.hard.remove(0);
            this.lock.unlock();
        } else {
            h = generatePuzzle(Difficulty.HARD);
        }
        return h;
    }

    /**
     * generate a puzzle of a certain level of difficulty
     * @param d the level difficulty
     * @return the puzzle board
     */
    public GameBoard generatePuzzle (Difficulty d) {
        GameBoard result = null;
        int retry = 0;
        while (result == null && retry < 50) {
            result = generateBoard(generateEndBoard(d), minMoves(d), maxMoves(d));
            retry++;
        }
        if (result == null) {
            String level = d.equals(d.valueOf("EASY")) ? "easy" : d.equals(d.valueOf("MEDIUM")) ? "medium" : "hard";
            Random random = new Random();
            int num = random.nextInt(19) + 1;
            result = process("src/gridlock/resources/" + level + "/" + num + ".txt");
        }
        result.setMinMoves();
        return result;
    }

    /**
     * open, read and close a file containing a board implementation
     * @param file the file name
     * @return a board
     */
    private GameBoard process (String file) {
        GameBoard board = new GameBoard();
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

    /**
     * Generate a starting board with the given min and max number of moves
     * and an end board state
     * @param winBoard the end board state
     * @param minMoves the min number of moves required
     * @param maxMoves the max number of moves required
     * @return the starting board
     */
    public GameBoard generateBoard (GameBoard winBoard, int minMoves, int maxMoves) {
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
            // The neighbor constructions, "ignoring reference and duplicates", are done here. Otherwise it is gonna loop
            for (Block b: curr.board.getBlocks()) {
                // Consider all possibility of its new position (diff than currently), the new board is a neighbor
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
        maxNode = initWinNode;
        for (Node n: adjacency.keySet()) if (n.dist > maxNode.dist) maxNode = n;

        // Backtracking
        return (minMoves <= maxNode.dist && maxNode.dist <= maxMoves) ? maxNode.board.duplicate() : null;
    }

    /**
     * private method to check if a node list contains a node based on reference
     * @param nl the node list
     * @param n the node to check if contained
     * @return true if the node list contains the reference
     * @return false if the node list doesn't contain the reference
     */
    private boolean containsRef(List<Node> nl, Node n) {
        if (n == null) return (this == null);
        for (Node x : nl) if (x == n) return true;
        return false;
    }

    /**
     * private method to generate random end board state
     * @param p the probability of an independent cell to be filled
     * @param minBlockNum the min number of blocks
     * @param maxBlockNum the max number of blocks
     * @return an end board state
     */
    private GameBoard setEndBoard(double p, int minBlockNum, int maxBlockNum) {
        //JAVA DOES NOT USE ASSERT SAYS WAYNE - ALINA
        assert (minBlockNum <= maxBlockNum);

        int currNumOfBlock = 0;
        GameBoard b = new GameBoard();
        List <String []> grid = b.getGrid();
        if (b.setBlock("z", 2, 4, 2, true)) currNumOfBlock++;
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
                String fillOrNot = randomBinaryChoice("yes", "no", p);
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
        return (currNumOfBlock >= minBlockNum && currNumOfBlock <= maxBlockNum)
                ? b : setEndBoard(p, minBlockNum, maxBlockNum);
    }

    /**
     * private method to randomly choose between 2 items with the given probability of item 1
     * @param item1 the first item to be selected
     * @param item2 the second item to be selected
     * @param probItem1 the probability that the first item is selected
     * @param <E>
     * @return item1 or item2 randomly
     */
    private <E> E randomBinaryChoice(E item1, E item2, double probItem1) {
        return (Math.random() < probItem1) ? item1 : item2;
    }

    /**
     * generate an end board state with certain level of difficulty
     * @param d the difficulty of the puzzle required
     * @return a game board
     */
    public GameBoard generateEndBoard (Difficulty d) {
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
            p = 0.6;
            minBlockNum = 10;
            maxBlockNum = 13;
        }
	    GameBoard board = setEndBoard(p, minBlockNum, maxBlockNum);
        return board;
    }

    /**
     * get the min number of moves based on the difficulty level
     * @param d the difficulty level
     * @return the min number of moves
     */
    private int minMoves (Difficulty d) {
        return d.equals(d.valueOf("EASY")) ? 3 : d.equals(d.valueOf("MEDIUM")) ? 8 : 14;
    }

    /**
     * get the max number of moves based on the difficulty level
     * @param d the difficulty level
     * @return the max number of moves
     */
    private int maxMoves (Difficulty d) {
        return d.equals(d.valueOf("EASY")) ? 7 : d.equals(d.valueOf("MEDIUM")) ? 13 : 20;
    }

    /**
     * method called for multithreading
     */
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

    /**
     * generate a medium puzzle and add it in the ArrayList of medium puzzles
     */
    public void addMediumPuzzle() {
        try {
            GameBoard med = generatePuzzle(Difficulty.MEDIUM);
            this.lock.lock();
            this.medium.add(med);
        } finally {
            if (this.lock.isHeldByCurrentThread()) this.lock.unlock();
        }
    }

    /**
     * generate a hard puzzle and add it in the ArrayList of hard puzzles
     */
    public void addHardPuzzle() {
        try {
            GameBoard h = generatePuzzle(Difficulty.HARD);
            this.lock.lock();
            this.hard.add(h);
        } finally {
            if (this.lock.isHeldByCurrentThread()) this.lock.unlock();
        }
    }

    /**
     * stop the background threads from running
     */
    public void stopThread() {
        this.running = false;
    }

    /**
     * Private Node Class for the level generator graph
     */
    private static class Node {
        private GameBoard board;
        private boolean isWin;
        private boolean isVisited;
        private int dist;
        private Node pred;

        /**
         * Class constructor for Node Class
         * @param board the starting board
         */
        private Node (GameBoard board) {
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
         * @param obj the object to be compared
         * @return true if the two objects are equal
         * @return false if the two objects are not equal
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

        /**
         * Decide if n is a neighbor (assuming only one move is performed), either:
         * * If one win and one not, accept directly
         * * If they are ranged differently, accept directly
         * * If same, check if thay are same hemisphere or not. Yes <=> reject
         * @param n the neighbor to be checked
         * @return true if n is neighbor
         * @return false if n is not neighbor
         */
        public boolean isNeighbor (Node n) {
            return (!(bothYesOrNo(n) && isSameRange(n) && isSameHemisphere(n)));
        }

        /**
         * check the state of the node
         * @param n the node to be checked
         * @return false only if one is winning and the other is not
         */
        private boolean bothYesOrNo (Node n) {
            return ((this.isWin && n.isWin) || (!this.isWin && !n.isWin));
        }

        /**
         * check if the node is in the same range
         * @param n the node to be checked
         * @return true if the two nodes are in the same range
         * @return false if the two nodes are not in the same range
         */
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
                if (br1[0] != br2[0] || br1[1] != br2[1]) return false;
            }
            return true;
        }

        /**
         * check if the node is in the same hemisphere
         * @param n the node to be checked
         * @return true if they are in the same hemisphere
         * @return false if they are not in the same hemisphere
         */
        private boolean isSameHemisphere(Node n) {
            for (Block thisBlock : this.board.getBlocks()) {
                String id = thisBlock.getID();
                Block thatBlock = n.board.getBlock(id);
                int thisRow = thisBlock.getRow();
                int thisCol = thisBlock.getCol();
                int thatRow = thatBlock.getRow();
                int thatCol = thatBlock.getCol();
                for (Block otherOrientationBlock : this.board.getBlocks()) {
                    boolean a = thisBlock.isHorizontal();
                    // To reduce nodes: otherOri should "intersect thisBlock's range"
                    if (a == otherOrientationBlock.isHorizontal()) continue;
                    int otherRow = otherOrientationBlock.getRow();
                    int otherCol = otherOrientationBlock.getCol();
                    Integer[] thisBr = this.board.blockRange(id);
                    Integer[] otherBr = this.board.blockRange(otherOrientationBlock.getID());
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
            }
            return true;
        }
    }
}
