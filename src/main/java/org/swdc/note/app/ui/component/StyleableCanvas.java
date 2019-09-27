package org.swdc.note.app.ui.component;

import java.util.List;

import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import javafx.css.StyleablePropertyFactory;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;

public class StyleableCanvas extends Canvas {
	
	private static final StyleablePropertyFactory<StyleableCanvas> factory = new StyleablePropertyFactory<>(Canvas.getClassCssMetaData());
	
	private StyleableProperty<Color> background = factory.createStyleableColorProperty(this, "background", "-c-background-color");
	
	protected static final CssMetaData<StyleableCanvas, Color> BACKGROUND_META_DATA = factory.createColorCssMetaData("-c-background-color", c -> c.background, Color.BLACK);
	
	private StyleableProperty<Color> border = factory.createStyleableColorProperty(this, "border", "-c-border-color");
	
	protected static final CssMetaData<StyleableCanvas, Color> BORDER_META_DATA = factory.createColorCssMetaData("-c-border-color", c -> c.border, Color.GRAY);
	
	private StyleableProperty<Color> fill = factory.createStyleableColorProperty(this, "fill", "-c-fill-color");
	
	protected static final CssMetaData<StyleableCanvas, Color> FILL_META_DATA = factory.createColorCssMetaData("-c-fill-color", c -> c.fill, Color.LIGHTGRAY);
	
	@Override
    public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
        return factory.getCssMetaData();
    }

	public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
		return factory.getCssMetaData();
	}
	
}
