package gridlock.model;

public class BoardState {
	Board board;
	int density;

	public BoardState(int density) {
		this.board = new Board();
		this.density = density;
	}
}
