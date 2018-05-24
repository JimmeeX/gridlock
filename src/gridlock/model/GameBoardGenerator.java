package gridlock.model;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

import static java.lang.Thread.sleep;

/**
 * GameBoardGenerator Class is designed to implement different level puzzle generator
 * and uses multithreading to generate medium and hard levels while the main program is running
 */
public class GameBoardGenerator implements Runnable {

    private ArrayList<GameBoard> medium;
    private ArrayList<GameBoard> hard;
    private boolean threadRun;
    private boolean threadResume;
    private boolean threadAbortRequest; // The order to abort procedures in the thread. There are two abortion
                                        // checks in place: newEndGameBoardList and generateGameBoard
    private boolean isUsed;
    private final ReentrantLock lock = new ReentrantLock();

    private int minMoves;
    private int maxMoves;
    private int minBlocks;
    private int maxBlocks;
    private double fillInProb;
    private String keyToReferToCampaignMode;
    private int numOfFinalMoves;
    /** (Private)
     * Node Class contains a GameBoard object and other information for the level generator BFS graph
     */
    private class Node {
        GameBoard board;
        boolean isWin;
        int dist;
        Node pred;

        /**
         * Class constructor for Node Class
         * @param board the starting board
         */
        Node (GameBoard board) {
            this.board = board;
            Block zBlock = board.getBlock("z");
            if (zBlock != null && Arrays.equals(zBlock.getPosition().get(0), new Integer[] {2, 4})) this.isWin = true;
            else this.isWin = false;
            this.dist = 60; // so far there has not been puzzle with >= 49
            this.pred = null;
        }

        /** Two nodes are equal iff their board configuration are the same.
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
        /**
         * The hash code for Node is editted to obtain a faster equivalent-and-visited node search
         * for BFS in generateGameBoard.
         * This checks for eleven block ids (including "z" block) and compare its changeable row/column
         * position in thisNode and thatNode (Obviously two equal nodes will have a same game-board
         * configuration, and thus a same changeable row/column positions for every id).
         *
         * If we assume that each block's fixed column/row (corresponding to id)
         * is always fixed during the game play (since you only slide it), then this function ensures
         * that two nodes with the same hashCode also have the exact-same eleven blocks' arrangements.
         * This may increase the "chance" of finding maximally one block, given a hashCode number.
         * @return hashCode number
         */
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
         * Two nodes n1 and n2 are adjacent, iff:
         * 1. There is exactly a block to move, s.t. n1's board becomes n2's board
         * 2. We employ some more restriction to reduce the number of edges and visited nodes:
         *      * The blocks' ranges should not be exactly equal
         *      * Unless one node is a winNode and the other is node.
         *          In this case, it is obvious that "z" was moved, thus there should be a possibility
         *          to move this way in order to create shortest path
         *      * Another unless: if there is an other block B (clearly not moved and with diff orientation)
         *          whose range intersects the "line" formed by position unit grid of the currently
         *          changed block A in n1 and n2's boards,
         *          s.t. A in both nodes are in different "hemispheres" of B's axis.
         *          In that case, an option to move from n1 to n2 should be explored.
         *
         * This constructs a list of such neighbor nodes with new references and no duplications.
         * @param
         * @return
         */
        public List <Node> produceNeighborNodes () {
            List <Node> neighborNodeList = new ArrayList<>();
            for (Block b: this.board.getBlocks()) {
                // Consider all possibility of its new position (diff than currently)
                Integer[] intv = this.board.blockRange(b.getID());
                for (int i = intv[0]; i <= intv[1]; i++) {
                    GameBoard duplicate = this.board.duplicateGridandBlocks();
                    if (b.isHorizontal()) {
                        if (i == b.getCol()) continue;
                        duplicate.makeMove(b.getID(), new Integer[]{b.getRow(), i}, true);
                    } else {
                        if (i == b.getRow()) continue;
                        duplicate.makeMove(b.getID(), new Integer[]{i, b.getCol()}, true);
                    }
                    Node potentNeighNode = new Node (duplicate);
                    // Decide further restriction
                    if (isBothWinOrBothNotWinNodes(potentNeighNode) &&
                            isSameRange (potentNeighNode) &&
                            isSameHemisphere (potentNeighNode)) continue;
                    neighborNodeList.add(potentNeighNode);
                }
            }
            return neighborNodeList;
        }
        /** (Private)
         * Check if both nodes are win nodes or (both) not win nodes
         * @param n the node to be checked
         * @return false only if one is winning and the other is not
         */
        private boolean isBothWinOrBothNotWinNodes(Node n) {
            return ((this.isWin && n.isWin) || (!this.isWin && !n.isWin));
        }
        /** (Private)
         * Check if the node is in the same range
         * @param n the node to be checked
         * @return true if the two nodes are in the same range
         * @return false if the two nodes are not in the same range
         */
        private boolean isSameRange(Node n) {
            for (Block thisBlock : this.board.getBlocks()) {
                String id = thisBlock.getID();
                Block thatBlock = n.board.getBlock(id);
                Integer[] br1 = this.board.blockRange(id);
                Integer[] br2 = n.board.blockRange(id);
                if (br1[0] != br2[0] || br1[1] != br2[1]) return false;
            }
            return true;
        }
        /**
         * Check if the node is in the same hemisphere
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

    /**
     * Constructor for GameBoardGenerator class
     */
    public GameBoardGenerator() {
        this.medium = new ArrayList<>();
        this.hard = new ArrayList<>();
    }

    /**
     * Get an easy level puzzle
     * @return a GameBoard of level easy
     */
    public GameBoard getEasy() {
        pauseThread(); isUsed = true;
        System.out.println("Threading is off for users, level easy");
        GameBoard e = generateGameBoardASAP(Difficulty.EASY);
        e.setMinMoves();
        isUsed = false; resumeThread();
        System.out.println("Threading is on for background, level easy");
        return e;
    }
    /**
     * Get a medium level puzzle
     * @return a GameBoard of level medium
     * @post medium.size()--
     */
    public GameBoard getMedium() {
        pauseThread(); isUsed = true;
        System.out.println("Threading is off for users, level medium");
        GameBoard med;
        if (this.medium.size() > 0) {
            this.lock.lock();
            med = this.medium.remove(0);
            this.lock.unlock();
        } else {
            med = generateGameBoardASAP(Difficulty.MEDIUM);
        }
        med.setMinMoves();
        isUsed = false; resumeThread();
        System.out.println("Threading is on for background, level medium");
        return med;
    }
    /**
     * Get a hard level puzzle
     * @return a GameBoard of level hard
     * @post hard.size()--
     */
    public GameBoard getHard() {
        pauseThread(); isUsed = true;
        System.out.println("Threading is off for users, level hard");
        GameBoard h;
        if (this.hard.size() > 0) {
            this.lock.lock();
            h = this.hard.remove(0);
            this.lock.unlock();
        } else {
            h = generateGameBoardASAP(Difficulty.HARD);
        }
        h.setMinMoves();
        isUsed = false; resumeThread();
        System.out.println("Threading is on for background, level hard");
        return h;
    }
    /** (Private)
     * Generate a puzzle when demanded by users. Since it has to be fast, there is 10 retries
     * that when failed, will automatically refer to a campaign puzzle.
     * @param d the level difficulty
     * @return the puzzle game-board
     */
    private GameBoard generateGameBoardASAP(Difficulty d) {
        GameBoard result = null;
        int retry = 0;
        while (result == null && retry < 10) {
            System.out.println("=== Generating a game-board ASAP for difficulty " + d.toString() + ", retry #" + retry);
            result = generateGameBoard(d);
            retry++;
        }
        if (result == null) {
            Random random = new Random();
            int num = random.nextInt(19) + 1;
            result = new GameBoard();
            result.process("src/gridlock/resources/" + keyToReferToCampaignMode
                    + "/" + num + ".txt");
            System.out.println("Generating ASAP is too long. Creating template puzzle...");
        } else {
            System.out.println("An on-the spot game-board is found with number of moves: " + numOfFinalMoves);
        }
        result.printGrid();
        return result;
    }

    /** (Private)
     * Determine and save the initial settings according to difficulty level, mainly the number of blocks' range,
     * and the minimal and maximal number of moves permitted.
     *
     * Additional saved info includes (1) the probability corresponding to gameBoard filling
     * in newEndGameBoard function, and (2) keyword for reference to a template puzzle if after several
     * trials failing to find a puzzle.
     * @param d the difficulty level
     */
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

    /** (Private)
     * Generate a random end game-board list.
     *
     * This function initially find an example of an end game-board E based on newEndGameBoard function.
     * Then it wants to return a cover set of end game-boards corresponding to the graph of all possible
     * configurations that can be reached from E.
     *
     * It is henceforth possible to BFS from these game-boards/nodes; and if we stop the search at proper time,
     * we are guaranteed that all visited nodes/game-boards have correct distance from any possible end game-board
     * state that realistically can be reached in the gameplay.
     *
     * This function implements a possible way to create this list, i.e. to build all end game-boards which
     * (1) Has same blocks ids as E
     * (2) Each block corresponding to id has same column (if vertical) or row (if horizontal) as E's version
     * (3) If there are >= 2 blocks in the same row/column, their relative positions must be equal to E's
     *
     * @return a list of end game-boards
     */
    private List <GameBoard> newEndGameBoardList() {
        GameBoard referencedWinBoard = newEndGameBoard();

        List <GameBoard> result = new ArrayList<>();
        List <GameBoard> tempResult = new ArrayList<>();
        result.add(new GameBoard());
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 2; j++) {
                for (GameBoard gb : result) {
                    // Check for abort request
                    if (threadAbortRequest) return null;
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
                                tempGb = gb.duplicateGridandBlocks();
                                if (tempGb.setBlock("z", 2, 4, 2, true) &&
                                        tempGb.setBlock(primaryBlock.getID(), j == 0 ? primaryBlock.getRow() : pCount,
                                                j == 0 ? pCount : primaryBlock.getCol(), primaryBlock.getSize(),
                                                primaryBlock.isHorizontal()))
                                        tempResult.add(tempGb);
                            } else {
                                for (int sCount = pCount + primaryBlock.getSize();
                                     sCount + secondaryBlock.getSize() - 1 < 6; sCount++) {
                                    tempGb = gb.duplicateGridandBlocks();
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
                                tempGb = gb.duplicateGridandBlocks();
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
        // Randomize the order of endGameBoard list
        Collections.shuffle(result);
        return result;
    }
    /** (Private)
     * Generate a random end game-board state
     * @return an end game-board
     */
    private GameBoard newEndGameBoard() {
        int currNumOfBlock = 0;
        GameBoard gb = new GameBoard();
        List <String []> grid = gb.getGrid();
        if (gb.setBlock("z", 2, 4, 2, true)) currNumOfBlock++;
        grid.get(2)[3] = "-";

        Random random = new Random();
        int row = random.nextInt(5);
        int col = randomBinaryChoice(4, 5, 0.5);
        int s = randomBinaryChoice(2, 3, 0.5);
        if (gb.setBlock("a", row, col, s, false)) currNumOfBlock++;

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
                if (gb.setBlock(id, i, j, size[sizeIdx], isHorizontal[isHorizontalIdx])) continue;
                if (gb.setBlock(id, i, j, size[sizeIdx], isHorizontal[1 - isHorizontalIdx])) continue;
                if (gb.setBlock(id, i, j, size[1 - sizeIdx], isHorizontal[isHorizontalIdx])) continue;
                if (gb.setBlock(id, i, j, size[1 - sizeIdx], isHorizontal[1 - isHorizontalIdx])) continue;
                currNumOfBlock--;
            }
        }
        grid.get(2)[3] = "*";
        return (currNumOfBlock >= minBlocks && currNumOfBlock <= maxBlocks)
                ? gb : newEndGameBoard();
    }
    /** (Private)
     * Randomly choose between 2 items with the given probability of item 1
     * @param item1 the first item to be selected
     * @param item2 the second item to be selected
     * @param probItem1 the probability that the first item is selected
     * @param <E>
     * @return item1 or item2 randomly
     */
    private <E> E randomBinaryChoice(E item1, E item2, double probItem1) {
        return (Math.random() < probItem1) ? item1 : item2;
    }

    /** (Private)
     * Generate a single starting game board for a particular difficulty.
     * Initially it presets the information related to difficulty such as min and max number of moves.
     * Then this function performs BFS from the list given by newEndGameBoardList, which guarantees
     * that a BFS starting from this list will give a correct interpretation that a recorded
     * distance in a visited node X - IS - the minimal number of moves needed from X's game-board
     * configuration to an end game-board.
     * @return a starting game-board
     */
    private GameBoard generateGameBoard(Difficulty d) {
        generateInitialHeuristics (d);
        int targetMoves = minMoves + (new Random ()).nextInt(maxMoves - minMoves);
        List <GameBoard> initWinBoardList = newEndGameBoardList();
        if (initWinBoardList == null) return null;

        // For BFS to store pred and distance variables properly, every node's neighbors should
        // be stored reference-based. If a new-found node is equivalent to a node in nodeList
        // (in the form of both adjacency.keySet() or queueRecordList), use that reference instead.
        //
        // To avoid checking 20K+ nodes, we use hashMaps :). Due to code reuse, three hashMaps still
        // implements adjacency list structures: the classic adjacency
        // A more efficient structure would be using hashMap to store nodes as keys,
        // with unique value-reference.
        //
        // The premises would be that the nodeList will contain all sufficient nodes
        // to cover all actual neighbors, and all neighbors will never be inserted
        // with something not in nodeList.
        //
        // Theoretically, the data structures here represent something:
        // 1) Queue is the open set
        // 2) Adjacency is both the open set and closed set: the only thing that determines whether an element is in
        //      open or closed set when a loop is just started/just ended, is that an open-set element always has
        //      an empty list as its neighborList.
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
            // Ensuring we only want to explore up to a distance of targetMoves
            if (curr.dist > targetMoves) break;
            for (Node neighNode : curr.produceNeighborNodes()) {
                List <Node> currNeighbors = adjacencyRefAB.get(curr);
                List <Node> nnNeighbors = adjacency.get(neighNode);
                if (nnNeighbors == null) {
                    // This neighNode is brand new. Add it to curr's neighbor list
                    currNeighbors.add(neighNode);
                    // and put it on adjacencies and queue
                    newNeighborList = new ArrayList<>();
                    adjacency.put(neighNode, newNeighborList);
                    adjacencyRefAB.put(neighNode, newNeighborList);
                    adjacencyRefBA.put(newNeighborList, neighNode);
                    queue.add(neighNode);
                    // and define the nn's distance and predecessor
                    neighNode.dist = curr.dist + 1;
                    neighNode.pred = curr;
                } else {
                    Node nnOriginRef = adjacencyRefBA.get(nnNeighbors);
                    // neighNode is still on queue, w a different reference
                    // or: neighNode's board config is already on closed set before, w different ref
                    if (!(containsRef(currNeighbors, nnOriginRef))) currNeighbors.add(nnOriginRef);
                }
                // Check for abort request
                if (threadAbortRequest) return null;
            }
            // Add the curr to queue history. This will be the nodeList with sorted increasing distance
            queueRecordList.add(curr);
        }
        // Gives out the most difficult puzzle in this graph, along w numOfMoves.
        // If the numOfMoves exceeds the maxMoves limit (e.g. easy/medium level), we can drop the puzzle
        // difficulty by one step by referring to its predecessor (which has numOfMoves-1 steps).
        Node maxNode = queueRecordList.get(queueRecordList.size()-1);
        while (maxNode.dist > maxMoves) maxNode = maxNode.pred;
        // Backtracking
        /* System.out.println("Backward check . . .");
        for (Node x = maxNode; x != null; x = x.pred) {
            System.out.println("DEBUG: Claim max move: " + x.dist);
            x.board.printGrid();
        }*/

        // Since prevLoc traces may exist, we return the duplicate instead
        GameBoard result = maxNode.board.duplicateGridandBlocks();
        numOfFinalMoves = maxNode.dist;
        return (minMoves <= maxNode.dist) ? result : null;
    }
    /** (Private)
     * Check if a node list contains a node based on reference
     * @param nl the node list
     * @param n the node to check if contained
     * @return true if the node list contains the reference
     * @return false if the node list doesn't contain the reference
     */
    private boolean containsRef(List <Node> nl, Node n) {
        if (n == null) return (this == null);
        for (Node x : nl) if (x == n) return true;
        return false;
    }

    /**
     * The method called for multithreading
     */
    @Override
    public void run() {
        this.threadRun = true;
        this.threadResume = true;
        while (this.threadRun) {
            while (this.threadResume) {
                isUsed = true;
                Random random = new Random();
                int num = random.nextInt(19999);
                //System.out.println("Medium array size: " + medium.size() +
                // ", Hard array size: " + hard.size());
                if (0 <= num && num <= 9999 && this.medium.size() <= 15) {
                    tryAddMediumGameBoard();
                } else if (this.hard.size() <= 15) {
                    tryAddHardGameBoard();
                }
            }
            isUsed = false;
        }
    }
    /** (Private)
     * Try to generate a medium puzzle and if not null, add it in the ArrayList of medium puzzles
     */
    private void tryAddMediumGameBoard() {
        try {
            GameBoard med = generateGameBoard(Difficulty.MEDIUM);
            this.lock.lock();
            if (med != null) {
                this.medium.add(med);
                System.out.println("Adding a medium game-board with numOfMoves: " + numOfFinalMoves + ". Now medium array size " + medium.size());
                med.printGrid();
            } else {
                System.out.println("A medium game-board not found. Medium array size still " + medium.size());
            }
        } finally {
            if (this.lock.isHeldByCurrentThread()) this.lock.unlock();
        }
    }
    /** (Private)
     * Try to generate a hard puzzle and if not null, add it in the ArrayList of hard puzzles
     */
    private void tryAddHardGameBoard() {
        try {
            GameBoard h = generateGameBoard(Difficulty.HARD);
            this.lock.lock();
            if (h != null) {
                this.hard.add(h);
                System.out.println("Adding a hard game-board with numOfMoves: " + numOfFinalMoves + ". Now hard array size " + hard.size());
                h.printGrid();
            } else {
                System.out.println("A hard game-board not found. Hard array size still " + hard.size());
            }
        } finally {
            if (this.lock.isHeldByCurrentThread()) this.lock.unlock();
        }
    }

    /** (Private)
     * Pause the thread of GameBoardGen
     */
    private void pauseThread () {
        if (!this.threadResume) return;
        this.threadAbortRequest = true;
        this.threadResume = false;
        while (isUsed) sleepAndDoNotCare (100);
        this.threadAbortRequest = false;
    }
    /** (Private)
     * Resume the thread of GameBoardGen
     */
    private void resumeThread() {
        if (this.threadResume) return;
        while (isUsed) sleepAndDoNotCare(100);
        this.threadResume = true;
        while (!isUsed) sleepAndDoNotCare(100);
    }
    /**
     * Stop the thread of GameBoardGen
     */
    public void stopThread() { this.threadRun = false; }
    /** (Private)
     * Sleep while taking too-simple care of sleep (long millis) command.
     * @param millis the number of milliseconds
     */
    private void sleepAndDoNotCare (long millis) {
        try {
            sleep (millis);
        } catch (InterruptedException e) {
            System.out.println("InterruptedException issue: " + e.getMessage());
        }
    }

}