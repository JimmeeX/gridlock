package gridlock.view;

import gridlock.model.Board;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

public class MouseGestures {
    class DragContext {
        double x;
        double y;
    }

    private Board board;
    private String id;

//    ArrayList <Node> environmentObjects;
    private Pane pane;
    private int gridX;
    private int gridY;
    private Boolean isHorizontal;

    private double initialMinX;
    private double initialMaxX;
    private double initialMinY;
    private double initialMaxY;

    DragContext dragContext = new DragContext();

    public MouseGestures(String id, Board board, Pane pane, int gridX, int gridY, Boolean isHorizontal) {
        this.id = id;
        this.board = board;
        this.pane = pane;
        this.gridX = gridX;
        this.gridY = gridY;
        this.isHorizontal = isHorizontal;
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

        // TODO: Get collision (need to know location of other object)
        /*
        if(collision):
           return;

        else {
            node.setTranslateX(deltaX)
        }
        * */

        if (this.isHorizontal) {
            double deltaX = dragContext.x + event.getSceneX();
            if (deltaX + this.initialMinX < 0 || deltaX + this.initialMaxX > this.pane.getWidth()) {
                return;
            }
            node.setTranslateX(deltaX);
        }

        else {
            double deltaY = dragContext.y + event.getSceneY();
            if (deltaY + this.initialMinY < 0 || deltaY + this.initialMaxY > this.pane.getHeight()) {
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
}
