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

import java.awt.Rectangle;
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

	/** The default zoom increment */
	public static final double DEFAULT_ZOOM_FACTOR = 1.5;

	/** The working zoom increment */
	protected double zoom;

	/**
	 * Constructor
	 */
	public AbstractZoomTool() {
		setZoom(DEFAULT_ZOOM_FACTOR);
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
	 * Respond to a mouse wheel scroll event received from the map pane
	 *
	 * @param ev
	 *            the mouse event
	 */
	@Override
	public void onMouseWheelMoved(MapMouseEvent ev) {
		try {

			int clicks = iCount.addAndGet(ev.getWheelAmount());

			double actualZoom = 1;
			// positive clicks are down - zoom out
			if (clicks >= 3) {
				actualZoom = getZoom();
			} else if (clicks <= -3) {
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

				DirectPosition2D corner = new DirectPosition2D(mapPos.getX() - 0.5d * paneArea.width / newScale,
						mapPos.getY() + 0.5d * paneArea.height / newScale);

				Envelope2D newMapArea = new Envelope2D();
				newMapArea.setFrameFromCenter(mapPos, corner);
				mapPane.setDisplayArea(newMapArea);

			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {

		}
	}
}
