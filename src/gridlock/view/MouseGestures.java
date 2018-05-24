package gridlock.view;

import gridlock.model.GameBoard;
import gridlock.model.SystemSettings;
import javafx.animation.TranslateTransition;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.ArrayList;

/**
 * Helper Class to implement Drag and Drop Interface for Blocks/Rectangles.
 */
public class MouseGestures {
    class DragContext {
        double x;
        double y;
    }

    private SystemSettings settings;

    private GameBoard board;
    private String id;

    private ArrayList <Node> enObjects;
    private Node currObject;

    private Pane boardField;
    private int gridX;
    private int gridY;
    private Boolean isHorizontal;

    private double initialMinX;
    private double initialMaxX;
    private double initialMinY;
    private double initialMaxY;

    private double initialTranslateX;
    private double initialTranslateY;

    private double translateMinX;
    private double translateMaxX;
    private double translateMinY;
    private double translateMaxY;

    private DragContext dragContext = new DragContext();

    /**
     * Constructor
     * @param settings Settings for the App
     * @param id Block ID
     * @param board GameBoard (Back-end)
     * @param boardField Pane (Front-end)
     * @param gridX Number of Columns
     * @param gridY Number of Rows
     * @param isHorizontal Block is horizontal or vertical
     * @param recNode Target Node (what the mouse clicks)
     * @param recNodeL List of Other Nodes (for collision checking)
     */
    public MouseGestures(SystemSettings settings, String id, GameBoard board, Pane boardField,
                         int gridX, int gridY, Boolean isHorizontal,
                         Node recNode, ArrayList<Node> recNodeL) {
        this.settings = settings;
        this.id = id;
        this.board = board;
        this.boardField = boardField;
        this.gridX = gridX;
        this.gridY = gridY;
        this.isHorizontal = isHorizontal;
        this.currObject = recNode;
        this.enObjects = recNodeL;
    }

    /**
     * Function to actually add Drag + Drop Interface to the Node
     * @param node Target Node
     */
    public void makeDraggable(Node node) {

        this.initialMinX = node.getBoundsInParent().getMinX();
        this.initialMaxX = node.getBoundsInParent().getMaxX();
        this.initialMinY = node.getBoundsInParent().getMinY();
        this.initialMaxY = node.getBoundsInParent().getMaxY();

        node.setOnMouseEntered(onMouseEnteredEventHandler);
        node.setOnMouseExited(onMouseExitedEventHandler);
        node.setOnMousePressed(onMousePressedEventHandler);
        node.setOnMouseDragged(onMouseDraggedEventHandler);
        node.setOnMouseReleased(onMouseReleasedEventHandler);
    }

    /**
     * Simple Moving Animation for Horizontal Node to a new X Location
     * @param distMinX New X Location (relative to the Pane)
     * @return TranslateTransition Animation
     */
    public TranslateTransition animateMoveNodeX(double distMinX) {
        Rectangle rec = (Rectangle)this.currObject;
        TranslateTransition tt = new TranslateTransition(Duration.millis(250), rec);
        tt.setToX(distMinX - this.initialMinX);
        tt.setCycleCount(1);
        tt.play();
        return tt;
    }

    /**
     * Simple Moving Animation for Vertical Node to a new Y Location
     * @param distMinY New Y Location (relative to the Pane)
     * @return TranslateTransition Animation
     */
    public TranslateTransition animateMoveNodeY(double distMinY) {
        Rectangle rec = (Rectangle)this.currObject;
        TranslateTransition tt = new TranslateTransition(Duration.millis(250), rec);
        tt.setToY(distMinY - this.initialMinY);
        tt.setCycleCount(1);
        tt.setRate(1);
        tt.play();
        return tt;
    }

    /**
     * Changes the cursor to a open hand when it enters the block.
     */
    private EventHandler<MouseEvent> onMouseEnteredEventHandler = event -> {
        Node node = ((Node) (event.getSource()));
        node.setCursor(Cursor.OPEN_HAND);
    };

    /**
     * Changes the cursor to a default when it exits the block.
     */
    private EventHandler<MouseEvent> onMouseExitedEventHandler = event -> {
        Node node = ((Node) (event.getSource()));
        node.setCursor(Cursor.DEFAULT);
    };

    /**
     * Grabs location values when mouse clicks on the block. Initial state before dragging.
     */
    private EventHandler<MouseEvent> onMousePressedEventHandler = event -> {

        Node node = ((Node) (event.getSource()));

        this.dragContext.x = node.getTranslateX() - event.getSceneX();
        this.dragContext.y = node.getTranslateY() - event.getSceneY();

        this.initialTranslateX = node.getTranslateX() - this.boardField.getLayoutX();
        this.initialTranslateY = node.getTranslateY() - this.boardField.getLayoutY();

        this.translateMinX = -this.initialMinX;
        this.translateMaxX = this.boardField.getWidth() - this.initialMaxX;
        this.translateMinY = -this.initialMinY;
        this.translateMaxY = this.boardField.getHeight() - this.initialMaxY;
    };

    /**
     * Called when the block is being dragged around.
     * Checks for collisions with the border/with other blocks before moving it.
     * Sets cursor to closed hand
     */
    private EventHandler<MouseEvent> onMouseDraggedEventHandler = event -> {
        Node node = ((Node) (event.getSource()));
        double deltaX = this.dragContext.x + event.getSceneX();
        double deltaY = this.dragContext.y + event.getSceneY();

        double distMinX = deltaX + this.initialMinX;
        double distMaxX = deltaX + this.initialMaxX;
        double distMinY = deltaY + this.initialMinY;
        double distMaxY = deltaY + this.initialMaxY;

        if (this.isHorizontal) {
            this.handleBlockCollisionX(distMinX, distMaxX);
            deltaX = this.computeDeltaX(deltaX);
            node.setTranslateX(deltaX);
        }

        else {
            this.handleBlockCollisionY(distMinY, distMaxY);
            deltaY = this.computeDeltaY(deltaY);
            node.setTranslateY(deltaY);
        }

        node.setCursor(Cursor.CLOSED_HAND);
    };

    /**
     * Once Mouse releases the block, a move will be registered.
     * Code will: Round off to nearest grid
     * Play the Move
     * Update Board
     * Play Sound
     */
    private EventHandler<MouseEvent> onMouseReleasedEventHandler = event -> {
        Node node = ((Node) (event.getSource()));

        // Round X
        double xFactor = this.boardField.getWidth() / this.gridX;
        double xRounded = xFactor*(Math.round(node.getTranslateX()/xFactor));
        node.setTranslateX(xRounded);

        // Round Y
        double yFactor = this.boardField.getHeight() / this.gridY;
        double yRounded = yFactor*(Math.round(node.getTranslateY()/yFactor));
        node.setTranslateY(yRounded);

        // Make Move
        int newRow = (int)((yRounded + this.initialMinY) / yFactor);
        int newCol = (int)((xRounded + this.initialMinX) / xFactor);
        Integer[] newPosition = {newRow, newCol};
        this.board.makeMove(this.id, newPosition, true);
        this.board.updateNumMoves();
        this.board.checkGameOver();

        node.setCursor(Cursor.OPEN_HAND);

        this.settings.playMoveBlockSound();
    };

    /**
     * Checks for Collisions with other Blocks horizontally
     * @param minX min X Location of the Node being Dragged
     * @param maxX max X Location of the Node being Dragged
     */
    private void handleBlockCollisionX(double minX, double maxX) {
        double mouseX = -this.dragContext.x + this.initialTranslateX;

        Rectangle bounds = new Rectangle(minX, (this.initialMinY + this.initialMaxY)/2, maxX - minX, 1);

        for (Node enObject: this.enObjects) {
            Bounds enObjectBound = enObject.getBoundsInParent();
            if (bounds.intersects(enObject.getBoundsInParent()) && !this.currObject.equals(enObject)) {
                // Collision from the Left
                if (mouseX <= enObjectBound.getMinX() && this.translateMaxX == this.boardField.getWidth() - this.initialMaxX) {
                    this.translateMaxX = enObjectBound.getMinX() - this.initialMaxX;
                }
                // Collision from the Right
                else if (mouseX >= enObjectBound.getMinX() && this.translateMinX == -this.initialMinX) {
                    this.translateMinX = enObjectBound.getMaxX() - this.initialMinX;
                }
            }
        }
    }

    /**
     * Checks for Collisions with other Blocks vertically
     * @param minY min Y Location of the Node being Dragged
     * @param maxY max Y Location of the Node being Dragged
     */
    private void handleBlockCollisionY(double minY, double maxY) {
        double mouseY = -this.dragContext.y + this.initialTranslateY;

        Rectangle bounds = new Rectangle((this.initialMinX + this.initialMaxX)/2, minY, 1, maxY - minY);

        for (Node enObject: this.enObjects) {
            Bounds enObjectBound = enObject.getBoundsInParent();
            if (bounds.intersects(enObject.getBoundsInParent()) && !this.currObject.equals(enObject)) {
                // Collision from the Top
                if (mouseY <= enObjectBound.getMinY() && this.translateMaxY == this.boardField.getHeight() - this.initialMaxY) {
                    this.translateMaxY = enObjectBound.getMinY() - this.initialMaxY;

                }
                // Collision from the Bottom
                else if (mouseY >= enObjectBound.getMinY() && this.translateMinY == -this.initialMinY) {
                    this.translateMinY = enObjectBound.getMaxY() - this.initialMinY;
                }
            }
        }
    }

    /**
     * Helper Function to compute final deltaX (what distance the Node can be dragged) based on translateMin; translateMax
     * @param deltaX Dragging Value of the Node
     * @return the New deltaX
     */
    private double computeDeltaX(double deltaX) {
        if (deltaX < this.translateMinX) {
            deltaX = this.translateMinX;
        }
        else if (deltaX > this.translateMaxX) {
            deltaX = this.translateMaxX;
        }
        return deltaX;
    }

    /**
     * Helper Function to compute final deltaY (what distance the Node can be dragged) based on translateMin; translateMax
     * @param deltaY Dragging Value of the Node
     * @return the New deltaY
     */
    private double computeDeltaY(double deltaY) {
        if (deltaY < this.translateMinY) {
            deltaY = this.translateMinY;
        } else if (deltaY > this.translateMaxY) {
            deltaY = this.translateMaxY;
        }
        return deltaY;
    }
}
