package gridlock.model;

import java.util.ArrayList;

public class BoardState {
	private Board board;
	private BoardState pred;
	private int nMoves;
	private Block toMove;
	private ArrayList<Integer[]> positions;

	public BoardState(BoardState pred, int numOfMoves, Block block, ArrayList<Integer[]> positions) {
		this.board = new Board();
		this.pred = pred;
		this.nMoves = numOfMoves;
		this.toMove = block;
		copyArrayList(positions);
	}

	public void copyArrayList(ArrayList<Integer[]> positions) {
		this.positions = new ArrayList<>();
		for (Integer[] position: positions) {
			Integer[] newPosition = new Integer[2];
			newPosition[0] = position[0];
			newPosition[1] = position[1];
			this.positions.add(newPosition);
		}
	}

	public int getNMoves() {
		return this.nMoves;
	}

	public Board getBoard() {
		return this.board;
	}

	public Block getBlock() {
		return this.toMove;
	}
}
