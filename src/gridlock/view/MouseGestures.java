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

    public void moveNodeX(double distMinX) {
        this.currObject.setTranslateX(distMinX - this.initialMinX);
    }

    public void moveNodeY(double distMinY) {
        this.currObject.setTranslateY(distMinY - this.initialMinY);
    }

    public TranslateTransition animateMoveNodeX(double distMinX) {
        Rectangle rec = (Rectangle)this.currObject;
        TranslateTransition tt = new TranslateTransition(Duration.millis(250), rec);
        tt.setToX(distMinX - this.initialMinX);
        tt.setCycleCount(1);
        tt.play();
        return tt;
    }

    public TranslateTransition animateMoveNodeY(double distMinY) {
        Rectangle rec = (Rectangle)this.currObject;
        TranslateTransition tt = new TranslateTransition(Duration.millis(250), rec);
        tt.setToY(distMinY - this.initialMinY);
        tt.setCycleCount(1);
        tt.setRate(1);
        tt.play();
        return tt;
    }

    private EventHandler<MouseEvent> onMouseEnteredEventHandler = event -> {
        Node node = ((Node) (event.getSource()));
        node.setCursor(Cursor.OPEN_HAND);
    };

    private EventHandler<MouseEvent> onMouseExitedEventHandler = event -> {
        Node node = ((Node) (event.getSource()));
        node.setCursor(Cursor.DEFAULT);
    };

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

    private double computeDeltaX(double deltaX) {
        if (deltaX < this.translateMinX) {
            deltaX = this.translateMinX;
        }
        else if (deltaX > this.translateMaxX) {
            deltaX = this.translateMaxX;
        }
        return deltaX;
    }

    private double computeDeltaY(double deltaY) {
        if (deltaY < this.translateMinY) {
            deltaY = this.translateMinY;
        }
        else if (deltaY > this.translateMaxY) {
            deltaY = this.translateMaxY;
        }
        return deltaY;
    }
}
