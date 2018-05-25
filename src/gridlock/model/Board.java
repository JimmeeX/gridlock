package gridlock.model;

import java.util.ArrayList;

/**
 * Board Class containing the grid and blocks in a simple game
 * Added by Alina
 */
public class Board {

	private ArrayList<String[]> grid;
	private ArrayList<Block> blocks;

	/**
	 * Class constructor for Board
	 */
	public Board() {
		initialiseGrid(6);
		this.blocks = new ArrayList<>();
	}
	/**
	 * initialise the grid (size x size)
	 * @param size the length of the grid (square)
	 * @post this.grid.size() >= 0
	 */
	public void initialiseGrid(int size) {
		this.grid = new ArrayList<>();
		for (int row = 0; row < size; row++) {
			String[] newRow = new String[size];
			for (int col = 0; col < size; col++) {
				newRow[col] = "*";
			}
			this.grid.add(newRow);
		}
	}

	/**
	 * get the list of blocks
	 * @return the list of blocks
	 */
	public ArrayList<Block> getBlocks() {
		return this.blocks;
	}

	/**
	 * get the grid
	 * @return the grid
	 */
	public ArrayList<String[]> getGrid() {
		return this.grid;
	}

	/**
	 * get the whole row of the grid
	 * @param row the row index
	 * @return the row of the grid
	 */
	public String[] getGridRow(int row) {
		return this.grid.get(row);
	}

	/**
	 * get one specific block
	 * @param blockID index of the block
	 * @return the block
	 */
	public Block getBlock(int blockID) {
		return this.blocks.get(blockID);
	}

	/**
	 * get the id(name) of the block
	 * @param blockID the index of the block
	 * @return the name of the block
	 */
	public String getBlockID(int blockID) {
		return this.blocks.get(blockID).getID();
	}

	/**
	 * set the list of blocks
	 * @param blocks the list of blocks
	 */
	public void setBlocks(ArrayList<Block> blocks) {
		this.blocks = blocks;
	}

	/**
	 * set the grid
	 */
	public void setGrid() {
		for (Block block : this.blocks) {
			for (Integer[] position : block.getPosition()) {
				this.grid.get(position[0])[position[1]] = block.getID();
			}
		}
	}

}
