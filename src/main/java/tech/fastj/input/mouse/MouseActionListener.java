package tech.fastj.input.mouse;

import tech.fastj.input.mouse.events.MouseActionEvent;
import tech.fastj.input.mouse.events.MouseButtonEvent;
import tech.fastj.input.mouse.events.MouseMotionEvent;
import tech.fastj.input.mouse.events.MouseScrollEvent;
import tech.fastj.input.mouse.events.MouseWindowEvent;

import tech.fastj.gameloop.event.GameEventObserver;

/**
 * A mouse action listener.
 *
 * <b>NOTE:</b> For use with a FastJ {@code Scene}, a mouse action listener must be added to a
 * {@code Scene}'s list of mouse action listeners.
 * <br>
 * If you are planning to implement this class into a separate usage, you may consider using the {@code InputManager}
 * class to store a list of mouse action listeners. Then, have events from a class extending {@code MouseListener} fired
 * to that {@code InputManager}.
 *
 * @author Andrew Dey
 * @since 1.0.0
 */
public interface MouseActionListener extends GameEventObserver<MouseActionEvent> {

    /**
     * Event called when a mouse button is pressed.
     *
     * @param mouseButtonEvent The mouse event generated by the recent mouse button press.
     */
    default void onMousePressed(MouseButtonEvent mouseButtonEvent) {
    }

    /**
     * Event called when a mouse button is released.
     *
     * @param mouseButtonEvent The mouse event generated by the recent mouse button released.
     */
    default void onMouseReleased(MouseButtonEvent mouseButtonEvent) {
    }

    /**
     * Event called when a mouse button is clicked.
     *
     * @param mouseButtonEvent The mouse event generated by the recent mouse button click.
     */
    default void onMouseClicked(MouseButtonEvent mouseButtonEvent) {
    }

    /**
     * Event called when the mouse is moved.
     *
     * @param mouseMotionEvent The mouse event generated by the recent mouse moved.
     */
    default void onMouseMoved(MouseMotionEvent mouseMotionEvent) {
    }

    /**
     * Event called when the mouse is dragged.
     *
     * @param mouseMotionEvent The mouse event generated by the recent mouse dragged.
     */
    default void onMouseDragged(MouseMotionEvent mouseMotionEvent) {
    }

    /**
     * Event called when the mouse wheel is scrolled.
     *
     * @param mouseScrollEvent The mouse event generated by the recent mouse wheel scroll.
     */
    default void onMouseWheelScrolled(MouseScrollEvent mouseScrollEvent) {
    }

    /**
     * Event called when the mouse enters the application screen.
     *
     * @param mouseWindowEvent The mouse event generated by the mouse entering the screen.
     */
    default void onMouseEntersScreen(MouseWindowEvent mouseWindowEvent) {
    }

    /**
     * Event called when the mouse exits the application screen.
     *
     * @param mouseWindowEvent The mouse event generated by the mouse exiting the screen.
     */
    default void onMouseExitsScreen(MouseWindowEvent mouseWindowEvent) {
    }

    @Override
    default void eventReceived(MouseActionEvent mouseActionEvent) {
        if (mouseActionEvent.isConsumed()) {
            return;
        }

        switch (mouseActionEvent.getEventType()) {
            case Press: {
                assert mouseActionEvent instanceof MouseButtonEvent;
                onMousePressed((MouseButtonEvent) mouseActionEvent);
                break;
            }
            case Release: {
                assert mouseActionEvent instanceof MouseButtonEvent;
                onMouseReleased((MouseButtonEvent) mouseActionEvent);
                break;
            }
            case Click: {
                assert mouseActionEvent instanceof MouseButtonEvent;
                onMouseClicked((MouseButtonEvent) mouseActionEvent);
                break;
            }
            case Move: {
                assert mouseActionEvent instanceof MouseMotionEvent;
                onMouseMoved((MouseMotionEvent) mouseActionEvent);
                break;
            }
            case Drag: {
                assert mouseActionEvent instanceof MouseMotionEvent;
                onMouseDragged((MouseMotionEvent) mouseActionEvent);
                break;
            }
            case WheelScroll: {
                assert mouseActionEvent instanceof MouseScrollEvent;
                onMouseWheelScrolled((MouseScrollEvent) mouseActionEvent);
                break;
            }
            case Enter: {
                onMouseEntersScreen((MouseWindowEvent) mouseActionEvent);
                break;
            }
            case Exit: {
                onMouseExitsScreen((MouseWindowEvent) mouseActionEvent);
                break;
            }
        }

        mouseActionEvent.consume();
    }
}
