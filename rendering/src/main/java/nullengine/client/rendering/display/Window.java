package nullengine.client.rendering.display;

import nullengine.client.rendering.image.ReadOnlyImage;

public interface Window extends WindowCallbacks {

    int getX();

    int getY();

    void setPos(int x, int y);

    int getWidth();

    int getHeight();

    Monitor getMonitor();

    void setMonitor(Monitor monitor);

    float getContentScaleX();

    float getContentScaleY();

    void setSize(int width, int height);

    boolean isResized();

    DisplayMode getDisplayMode();

    default void setDisplayMode(DisplayMode mode) {
        setDisplayMode(mode, -1, -1, -1);
    }

    void setDisplayMode(DisplayMode mode, int newWidth, int newHeight, int frameRate);

    String getTitle();

    void setTitle(String title);

    void setIcon(ReadOnlyImage... icons);

    Cursor getCursor();

    void show();

    void hide();

    boolean isShowing();

    boolean isDoCloseImmediately();

    void setDoCloseImmediately(boolean doCloseImmediately);

    void swapBuffers();

    void dispose();

    // ================= Window Attributes Start =================
    boolean isFocused();

    void focus();

    boolean isDecorated();

    void setDecorated(boolean decorated);

    boolean isResizable();

    void setResizable(boolean resizable);

    boolean isFloating();

    void setFloating(boolean floating);

    boolean isTransparent();

    void setTransparent(boolean transparent);

    boolean isIconified();

    boolean isMaximized();

    void iconify();

    void maximize();

    void restore();
    // ================= Window Attributes End =================
}
