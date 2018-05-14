package gridlock.view;

import gridlock.model.Board;
import gridlock.model.SystemSettings;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.io.File;
import java.util.ArrayList;

public class MouseGestures {
    class DragContext {
        double x;
        double y;
    }

    private SystemSettings settings;

    private Board board;
    private String id;

    private ArrayList <Node> enObjects;
    private Node currObject;

    private Pane pane;
    private int gridX;
    private int gridY;
    private Boolean isHorizontal;

    private double initialMinX;
    private double initialMaxX;
    private double initialMinY;
    private double initialMaxY;

    private DragContext dragContext = new DragContext();

//    private MediaPlayer mediaPlayer;

    public MouseGestures(SystemSettings settings, String id, Board board, Pane pane,
                         int gridX, int gridY, Boolean isHorizontal,
                            Node recNode, ArrayList<Node> recNodeL) {
        this.settings = settings;
        this.id = id;
        this.board = board;
        this.pane = pane;
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

        node.setOnMousePressed( onMousePressedEventHandler);
        node.setOnMouseDragged( onMouseDraggedEventHandler);
        node.setOnMouseReleased(onMouseReleasedEventHandler);
    }

    private EventHandler<MouseEvent> onMousePressedEventHandler = event -> {

        Node node = ((Node) (event.getSource()));

        dragContext.x = node.getTranslateX() - event.getSceneX();
        dragContext.y = node.getTranslateY() - event.getSceneY();
    };

    private EventHandler<MouseEvent> onMouseDraggedEventHandler = event -> {

        Node node = ((Node) (event.getSource()));
        double deltaX = dragContext.x + event.getSceneX();
        double deltaY = dragContext.y + event.getSceneY();

        double distMinX = deltaX + this.initialMinX;
        double distMaxX = deltaX + this.initialMaxX;
        double distMinY = deltaY + this.initialMinY;
        double distMaxY = deltaY + this.initialMaxY;

        if (this.isHorizontal) {
            if ((distMinX < 0 || distMaxX > this.pane.getWidth()) || collisionCheck(distMinX, distMaxX, distMinY, distMaxY)) {
                return;
            }
            node.setTranslateX(deltaX);
        }

        else {
            if (distMinY < 0 || distMaxY > this.pane.getHeight() || collisionCheck(distMinX, distMaxX, distMinY, distMaxY)) {
                return;
            }
            node.setTranslateY(deltaY);
        }
    };

    private EventHandler<MouseEvent> onMouseReleasedEventHandler = event -> {
        Node node = ((Node) (event.getSource()));

        // Round X
        double xFactor = this.pane.getWidth() / this.gridX;
        double xRounded = xFactor*(Math.round(node.getTranslateX()/xFactor));
        node.setTranslateX(xRounded);

        // Round Y
        double yFactor = this.pane.getHeight() / this.gridY;
        double yRounded = yFactor*(Math.round(node.getTranslateY()/yFactor));
        node.setTranslateY(yRounded);

        // Make Move
        int newRow = (int)((yRounded + this.initialMinY) / yFactor);
        int newCol = (int)((xRounded +this.initialMinX) / xFactor);
        Integer[] newPosition = {newRow, newCol};
        this.board.makeMove(this.id, newPosition);
        this.board.updateNumMoves();
        this.board.checkGameOver();

        this.settings.playMoveBlockSound();
    };

    private boolean collisionCheck(double minX, double maxX, double minY, double maxY) {
        // Make Bounds Smaller
        Rectangle bounds;
        if (this.isHorizontal) {
            // For Horizontal Rectangles, make Y bounds small, with normal X bounds.
            bounds = new Rectangle(minX, (this.initialMinY + this.initialMaxY)/2, maxX - minX, 1);
        }
        else {
            bounds = new Rectangle((this.initialMinX + this.initialMaxX)/2, minY, 1, maxY - minY);
        }
        for (Node enObject: this.enObjects) {
            if (bounds.intersects(enObject.getBoundsInParent()) && (!this.currObject.equals(enObject))) {
                return true;
            }
        }
        return false;
    }


}
