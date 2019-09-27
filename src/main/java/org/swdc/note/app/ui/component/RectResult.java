package org.swdc.note.app.ui.component;

public class RectResult {

	private int xCount;
	
	private int yCount;
	
	public int getxCount() {
		return xCount;
	}
	
	public int getyCount() {
		return yCount;
	}
	
	public void setxCount(int xCount) {
		this.xCount = xCount;
	}
	
	public void setyCount(int yCount) {
		this.yCount = yCount;
	}
	
	@Override
	public String toString() {
		return "X: " + xCount + ",Y: " + yCount;
	}
	
}
