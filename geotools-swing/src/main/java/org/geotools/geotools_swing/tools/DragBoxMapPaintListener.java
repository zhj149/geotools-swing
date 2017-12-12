package org.geotools.geotools_swing.tools;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;

import org.geotools.geotools_swing.event.MapPaintListener;

/**
 * 实现拖动框的操作
 * 
 * @author sam
 *
 */
public interface DragBoxMapPaintListener extends MapPaintListener {

	/**
	 * 当前是否是拖动状态
	 * 
	 * @return
	 */
	public boolean isDrag();

	/**
	 * 屏幕操作起始位置
	 * 
	 * @return
	 */
	public Point getStartDevicePos();

	/**
	 * 屏幕操作结束位置
	 * 
	 * @return
	 */
	public Point getEndDevicePos();

	/**
	 * 绘制拖动框的操作
	 * 
	 * @param g2d
	 */
	@Override
	public default void afterPaint(Graphics2D g2d, int dx, int dy) {
		if (g2d != null && this.isDrag()) {
			Color color = g2d.getColor();
			g2d.setColor(Color.GREEN);
			int x = Math.min(getStartDevicePos().x, getEndDevicePos().x);
			int y = Math.min(getStartDevicePos().y, getEndDevicePos().y);
			int w = Math.abs(getEndDevicePos().x - getStartDevicePos().x);
			int h = Math.abs(getEndDevicePos().y - getStartDevicePos().y);
			g2d.drawRect(x, y, w, h);
			g2d.setColor(color);
		}
	}
}
