package nullengine.client.rendering.display.callback;

import nullengine.client.rendering.display.Window;

@FunctionalInterface
public interface WindowIconifyCallback {
    void invoke(Window window, boolean iconified);
}
