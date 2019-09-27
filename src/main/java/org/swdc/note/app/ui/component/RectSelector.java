package org.swdc.note.app.ui.component;

import javafx.application.Platform;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Dialog;
import javafx.scene.paint.Color;
import javafx.stage.StageStyle;

public class RectSelector extends Dialog<RectResult>{

	private StyleableCanvas canvas;
	private int colCounts = 4;
	private int rowCounts = 3;
	
	private int cellSize = 32;

	private int selRow;
	private int selCol;
	
	public RectSelector() {
		this.initStyle(StageStyle.UNDECORATED);
		this.initUI();
	}
	
	public void initUI() {
		
		int height = rowCounts * cellSize + 2;
		int width = colCounts * cellSize + 2;
		
		this.setWidth(width);
		this.setHeight(height);
		canvas = new StyleableCanvas();
		canvas.setWidth(width);
		canvas.setHeight(height);
		canvas.getStyleClass().add("rect-selector");
		this.getDialogPane().getChildren().clear();
		this.getDialogPane().setContent(canvas);

		this.setOnShowing(e -> {
			this.rowCounts = 3;
			this.colCounts = 4;

			this.setWidth(width);
			this.setHeight(height);
			canvas.setWidth(width);
			canvas.setHeight(height);
			this.getDialogPane().setPrefHeight(height);
			this.getDialogPane().setPrefWidth(width);

			drawBackground();
			drawGrid();
		});

		canvas.setOnMouseMoved(e -> {
			Color color = StyleableCanvas.FILL_META_DATA.getStyleableProperty(canvas).getValue();
			this.selCol = ((int)e.getSceneX() / cellSize) + 1;
			this.selRow = ((int)e.getSceneY() / cellSize) + 1;
			if(selCol > colCounts) {
				this.colCounts ++;
				this.setWidth(selCol * cellSize + 2);
				canvas.setWidth(selCol * cellSize + 2);
			}
			if(selRow > rowCounts) {
				this.rowCounts ++;
				this.setHeight(selRow * cellSize + 2);
				canvas.setHeight(selRow * cellSize + 2);
			}
			GraphicsContext context = canvas.getGraphicsContext2D();
			drawBackground();
			context.setFill(color);
			context.fillRect(0, 0, selCol * cellSize, selRow * cellSize);
			drawGrid();

		});
		
		canvas.setOnMouseClicked(e -> {
			RectResult rectResult = new RectResult();
			rectResult.setxCount(selRow);
			rectResult.setyCount(selCol);

			this.setResult(rectResult);
		});
		

		Platform.runLater(() -> {
			drawBackground();
			drawGrid();
		});
		
	}


	public void drawBackground() {
		Color color = StyleableCanvas.BACKGROUND_META_DATA.getStyleableProperty(canvas).getValue();
		int height = rowCounts * cellSize + 2;
		int width = colCounts * cellSize + 2;
		GraphicsContext context = canvas.getGraphicsContext2D();
		context.setFill(color);
		context.fillRect(0, 0, width, height);
	}
	
	public void drawGrid() {
		Color color = StyleableCanvas.BORDER_META_DATA.getStyleableProperty(canvas).getValue();
		int height = rowCounts * cellSize + 2;
		int width = colCounts * cellSize + 2;
		
		GraphicsContext context = canvas.getGraphicsContext2D();
		context.setStroke(color);
		
		for (int i = 0; i <= colCounts; i++) {
			context.strokeLine(i * cellSize + 1, 0, i * cellSize + 1, height);
		}
		for (int i = 0; i <= rowCounts; i++) {
			context.strokeLine(0, i * cellSize + 1, width, i * cellSize + 1);
		}
	}
	
}
