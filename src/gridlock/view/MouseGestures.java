package gridlock.view;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

public class MouseGestures {
    class DragContext {
        double x;
        double y;
    }
    private Pane pane;
    private Boolean isHorizontal;

    private double initialMinX;
    private double initialMaxX;
    private double initialMinY;
    private double initialMaxY;

    DragContext dragContext = new DragContext();

    public MouseGestures(Pane pane, Boolean isHorizontal) {
        this.pane = pane;
        this.isHorizontal = isHorizontal;
    }

    public void makeDraggable(Node node) {

        this.initialMinX = node.getBoundsInParent().getMinX();
        this.initialMaxX = node.getBoundsInParent().getMaxX();
        this.initialMinY = node.getBoundsInParent().getMinY();
        this.initialMaxY = node.getBoundsInParent().getMaxY();

        node.setOnMousePressed( onMousePressedEventHandler);
        node.setOnMouseDragged( onMouseDraggedEventHandler);
    }

    EventHandler<MouseEvent> onMousePressedEventHandler = event -> {

        Node node = ((Node) (event.getSource()));

        dragContext.x = node.getTranslateX() - event.getSceneX();
        dragContext.y = node.getTranslateY() - event.getSceneY();
    };

    EventHandler<MouseEvent> onMouseDraggedEventHandler = event -> {

        Node node = ((Node) (event.getSource()));
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
}
