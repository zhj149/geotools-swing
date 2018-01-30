/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */

package org.geotools.geotools_swing.tools;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.concurrent.atomic.AtomicInteger;

import org.geotools.geometry.DirectPosition2D;
import org.geotools.geometry.Envelope2D;
import org.geotools.geotools_swing.MapPane;
import org.geotools.geotools_swing.event.MapMouseEvent;

/**
 * Abstract base class for the zoom-in and zoom-out tools. Provides getter /
 * setter methods for the zoom increment.
 * 
 * @author Michael Bedward
 * @since 2.6
 *
 *
 *
 * @source $URL$
 * @version $Id$
 */

public abstract class AbstractZoomTool extends CursorTool {

	private AtomicInteger iCount = new AtomicInteger(0);

	/**
	 * The default zoom increment
	 */
	public static final double DEFAULT_ZOOM_FACTOR = 1.25;

	/**
	 * 新增加的设置的最小放大比例
	 */
	public static final double MIN_ZOOM = 3.02;

	/**
	 * The working zoom increment
	 */
	protected double zoom;
	
	/**
	 * 鼠标按下的时候的拖动点
	 */
	private final Point beginPos;

	/**
	 * 鼠标松手的位置点
	 */
	private final Point endPos;

	/**
	 * 是否拖动状态
	 */
	private volatile boolean panning = false;

	/**
	 * Constructor
	 */
	public AbstractZoomTool() {
		setZoom(DEFAULT_ZOOM_FACTOR);
		beginPos = new Point(0, 0);
		endPos = new Point(0, 0);
	}

	/**
	 * Get the current areal zoom increment.
	 * 
	 * @return the current zoom increment as a double
	 */
	public double getZoom() {
		return zoom;
	}

	/**
	 * Set the zoom increment
	 * 
	 * @param newZoom
	 *            the new zoom increment; values &lt;= 1.0 will be ignored
	 * 
	 * @return the previous zoom increment
	 */
	public double setZoom(double newZoom) {
		double old = zoom;
		if (newZoom > 1.0d) {
			zoom = newZoom;
		}
		return old;
	}

	/**
	 * 判断是否继续放大
	 * 
	 * @param newZoom
	 * @return true 不放大了 false 继续放大
	 */
	public boolean isNotZoomed(double newZoom) {
		return Math.abs(newZoom) < MIN_ZOOM;
	}
	
	/**
	 * Respond to a mouse button press event from the map mapPane. This may
	 * signal the start of a mouse drag. Records the event's window position.
	 * 
	 * @param ev
	 *            the mouse event
	 */
	@Override
	public void onMousePressed(MapMouseEvent ev) {
		if (ev.getButton() == MouseEvent.BUTTON3) {
			beginPos.setLocation(ev.getPoint());
			endPos.setLocation(ev.getPoint());
			panning = true;
		}
	}
	
	/**
	 * Respond to a mouse dragged event. Calls
	 * {@link com.adcc.geotools.swing.MapPane#moveImage()}
	 * 
	 * @param ev
	 *            the mouse event
	 */
	@Override
	public void onMouseDragged(MapMouseEvent ev) {
		if (panning) {
			endPos.setLocation(ev.getPoint());
			if (!beginPos.equals(endPos)) {
				getMapPane().moveImage(endPos.x - beginPos.x, endPos.y - beginPos.y);
			}
		}
	}

	/**
	 * If this button release is the end of a mouse dragged event, requests the
	 * map mapPane to repaint the display
	 * 
	 * @param ev
	 *            the mouse event
	 */
	@Override
	public void onMouseReleased(MapMouseEvent ev) {
		if (panning && !beginPos.equals(endPos)) {
			getMapPane().move(endPos.x - beginPos.x, endPos.y - beginPos.y);
		}
		panning = false;
		beginPos.setLocation(0, 0);
		endPos.setLocation(0, 0);
	}

	/**
	 * Respond to a mouse wheel scroll event received from the map pane
	 *
	 * @param ev
	 *            the mouse event
	 */
	@Override
	public void onMouseWheelMoved(MapMouseEvent ev) {
		try {

			int clicks = iCount.addAndGet(-ev.getWheelAmount());

			double actualZoom = 1;
			// positive clicks are down - zoom out
			if (clicks >= 1) {
				actualZoom = getZoom();
			} else if (clicks <= -1) {
				actualZoom = -1.0 / getZoom();
			} else {
				return;
			}

			synchronized (AbstractZoomTool.class) {

				MapPane mapPane = this.getMapPane();
				iCount.set(0);
				Rectangle paneArea = mapPane.getVisibleRectangle();
				// 不知道这个是不是传说中的获取当前坐标点
				DirectPosition2D mapPos = ev.getWorldPos();
				double scale = mapPane.getWorldToScreenTransform().getScaleX();
				double newScale = scale * actualZoom;

				if (isNotZoomed(newScale))
					return;

				DirectPosition2D corner = new DirectPosition2D(mapPos.getX() - 0.5d * paneArea.width / newScale,
						mapPos.getY() + 0.5d * paneArea.height / newScale);

				Envelope2D newMapArea = new Envelope2D();
				newMapArea.setFrameFromCenter(mapPos, corner);
				mapPane.setDisplayArea(newMapArea);

				// 然后平移到鼠标操作点
				getMapPane().move((int) (ev.getX() - paneArea.getWidth() / 2),
						(int) (ev.getY() - paneArea.getHeight() / 2));

			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {

		}
	}
}
