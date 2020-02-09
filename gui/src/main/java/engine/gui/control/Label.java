package engine.gui.control;

import com.github.mouse0w0.observable.value.MutableDoubleValue;
import com.github.mouse0w0.observable.value.MutableValue;
import engine.gui.misc.Pos;
import engine.gui.text.Text;
import engine.graphics.font.Font;
import engine.util.Color;

public class Label extends Control {

    private Text text = new Text();

    public Label() {
        this.getChildren().addAll(text);
    }

    public Label(String text) {
        this();
        text().setValue(text);
    }

    public final MutableValue<Font> font() {
        return text.font();
    }

    public final MutableValue<String> text() {
        return text.text();
    }

    public final MutableValue<Color> textColor() {
        return text.color();
    }

    public MutableValue<Pos> textAlignment() {
        return text.textAlignment();
    }

    public MutableDoubleValue leading() {
        return text.leading();
    }

    @Override
    public float computeWidth() {
        return text.prefWidth() + padding().getValue().getLeft() + padding().getValue().getRight();
    }

    @Override
    public float computeHeight() {
        return text.prefHeight() + padding().getValue().getTop() + padding().getValue().getBottom();
    }

}