package gridlock.view;

import gridlock.model.Board;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;

public class MouseGestures {
    class DragContext {
        double x;
        double y;
    }

    private Board board;
    private String id;

    ArrayList <Node> enObjects;
    Node currObject;

    private Pane pane;
    private int gridX;
    private int gridY;
    private Boolean isHorizontal;

    private double initialMinX;
    private double initialMaxX;
    private double initialMinY;
    private double initialMaxY;

    DragContext dragContext = new DragContext();

    public MouseGestures(String id, Board board, Pane pane,
                         int gridX, int gridY, Boolean isHorizontal,
                            Node recNode, ArrayList<Node> recNodeL) {
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
        // TODO: Get collision (need to know location of other object)

////        collisionCheck();
//        if(!collisionCheck())
//            node.setTranslateX(deltaX);
//
//        else {
//            return;
//        }


        if (this.isHorizontal) {
            if ((deltaX + this.initialMinX < 0 || deltaX + this.initialMaxX > this.pane.getWidth()) || collisionCheck()) {
                return;
            }
            node.setTranslateX(deltaX);
        }

        else {
            if (deltaY + this.initialMinY < 0 || deltaY + this.initialMaxY > this.pane.getHeight() || collisionCheck()) {
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
        // x refers to column; y refers to row
        int newRow = (int)((yRounded + this.initialMinY) / yFactor);
        int newCol = (int)((xRounded +this.initialMinX) / xFactor);
        Integer[] newPosition = {newRow, newCol};
        this.board.makeMove(this.id, newPosition);
        this.board.checkGameOver();
        this.board.printGrid();
    };

    public boolean collisionCheck() {
//        System.out.println();

        Rectangle bounds;
        if (this.isHorizontal) {
            bounds = new Rectangle(
                    this.currObject.getBoundsInParent().getMinX() + 4,
                    this.currObject.getBoundsInParent().getMinY() + 4,
                    this.currObject.getBoundsInParent().getWidth() - 10,
                    this.currObject.getBoundsInParent().getHeight() - 10
            );
        }
        else {
            bounds = new Rectangle(
                    this.currObject.getBoundsInParent().getMinX() + 4,
                    this.currObject.getBoundsInParent().getMinY() + 4,
                    this.currObject.getBoundsInParent().getWidth() - 10,
                    this.currObject.getBoundsInParent().getHeight() - 10
            );
        }
        System.out.println("Current Object: " + bounds);

        Integer i = 0;
        for (Node enObject: this.enObjects) {
            System.out.println(i.toString() + ": " + enObject.getBoundsInParent());
            // Make Bounds slightly smaller

            // Make it vertically smaller (by 1 pixel)
            if (bounds.intersects(enObject.getBoundsInParent()) && (!this.currObject.equals(enObject))) {
                System.out.println("YES");
                return true;
            }
            i++;
        }
        return false;
    }


}
