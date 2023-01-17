package tech.fastj.input.mouse;

import tech.fastj.input.InputBinding;
import tech.fastj.input.mouse.events.MouseActionEvent;

public interface MouseBinding<T extends MouseActionEvent> extends InputBinding<T> {
}
