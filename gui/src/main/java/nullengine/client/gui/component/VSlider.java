package nullengine.client.gui.component;

import com.github.mouse0w0.observable.value.*;
import nullengine.client.gui.Region;
import nullengine.client.gui.input.MouseActionEvent;
import nullengine.client.gui.input.MouseEvent;
import nullengine.client.gui.shape.Rect;
import nullengine.util.Color;
import org.joml.Vector2f;

public class VSlider extends Region {

    private Rect slider = new Rect();

    private Rect back = new Rect();

    private MutableDoubleValue min = new SimpleMutableDoubleValue(0);
    private MutableDoubleValue max = new SimpleMutableDoubleValue(1);
    private MutableDoubleValue value = new SimpleMutableDoubleValue(0);

    private final MutableFloatValue sliderLength = new SimpleMutableFloatValue(150);
    private final MutableFloatValue sliderThickness = new SimpleMutableFloatValue(15);
    private final MutableFloatValue step = new SimpleMutableFloatValue(0.01f);
    /**
     * VSlider is designed to be up-to-down as default. this variable defines as if this slider uses down-to-up as min to max or not
     */
    private final MutableBooleanValue flip = new SimpleMutableBooleanValue();

    private boolean select = false;

    public VSlider() {
        value.addChangeListener((ob, o, n) -> rebuild());
        min.addChangeListener((observable, oldValue, newValue) -> {
            if (newValue > max.get()) {
                min.set(oldValue);
            } else {
                rebuild();
            }
        });
        max.addChangeListener((observable, oldValue, newValue) -> {
            if (newValue < min.get()) {
                max.set(oldValue);
            } else {
                rebuild();
            }
        });
        step.addChangeListener((observable, oldValue, newValue) -> {
            if (newValue == 0) {
                step.set(oldValue);
            } else {
                resizeSlider(sliderThickness.get(), sliderLength.get() * (float) (step.get() / (max.get() - min.get())));
            }
        });
        sliderLength.addChangeListener((observable, oldValue, newValue) -> rebuild());
        sliderThickness.addChangeListener((observable, oldValue, newValue) -> rebuild());
        this.getChildren().addAll(back, slider);
        backBg().setValue(Color.BLUE);
        sliderBg().setValue(Color.WHITE);
        addEventHandler(MouseActionEvent.MOUSE_PRESSED, this::onPressed);
        addEventHandler(MouseActionEvent.MOUSE_RELEASED, this::onReleased);
        addEventHandler(MouseEvent.MOUSE_MOVED, this::onMoved);
    }

    public MutableDoubleValue value() {
        return value;
    }

    public MutableDoubleValue max() {
        return max;
    }

    public MutableDoubleValue min() {
        return min;
    }

    public MutableFloatValue sliderThickness() {
        return sliderThickness;
    }

    public MutableFloatValue sliderLength() {
        return sliderLength;
    }

    public MutableFloatValue step() {
        return step;
    }

    public MutableBooleanValue flip() {
        return flip;
    }

    public void rebuild() {
        if (value.get() > max.get()) {
            value.set(max.get());
        } else if (value.get() < min.get()) {
            value.set(min.get());
        }
        resizeBack(sliderThickness.get(), sliderLength.get());
        resizeSlider(sliderThickness.get(), sliderLength.get() * (float) (step.get() / (max.get() - min.get())));
        slider.x().set(back.x().get());
        slider.y().set((float) (((back.height().get() - slider.height().get()) * (flip.get() ? 1 - ((value.get() - min.get()) / (max.get() - min.get())) : ((value.get() - min.get()) / (max.get() - min.get()))))));
    }

    private void onPressed(MouseActionEvent e) {
        if (e.getY() > slider.y().get() + slider.width().get()) {
            value.set(value.getValue() + step.get() * (flip.get() ? -1 : 1));
        } else if (e.getY() < slider.y().get()) {
            value.set(value.getValue() - step.get() * (flip.get() ? -1 : 1));
        }
        if (slider.contains(e.getX(), e.getY()))
            select = true;
    }

    private void onMoved(MouseEvent e) {
        if(!select) return;
        if ((e.getY() - slider.y().get() - slider.rectSize().get().y()) / height().get() > step.get() / (max.get() - min.get()) * 0.9) {
            value.set(value.getValue() + step.get() * (flip.get() ? -1 : 1));
        } else if ((slider.y().get() - e.getY()) / height().get() > step.get() / (max.get() - min.get()) * 0.9) {
            value.set(value.getValue() - step.get() * (flip.get() ? -1 : 1));
        }
    }

    private void onReleased(MouseActionEvent e) {
        select = false;
    }

    private void resizeBack(float width, float height) {
        back.rectSize().setValue(new Vector2f(width, height));
    }

    private void resizeSlider(float width, float height) {
        slider.rectSize().setValue(new Vector2f(width, height));
    }

    public MutableValue<Color> backBg() {
        return back.fillColor();
    }

    public MutableValue<Color> sliderBg() {
        return slider.fillColor();
    }

}