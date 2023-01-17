package tech.fastj.input.keyboard;

import tech.fastj.input.InputBinding;
import tech.fastj.input.keyboard.events.KeyboardActionEvent;

public interface KeyboardBinding<T extends KeyboardActionEvent> extends InputBinding<T> {
}
