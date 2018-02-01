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

import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.MouseEvent;

import org.geotools.geotools_swing.event.MapMouseEvent;

/**
 * A cursor tool to pan the map pane display.
 * 
 * @author Michael Bedward
 * @since 2.6
 *
 * @source $URL$
 * @version $Id$
 */
public class PanTool extends AbstractZoomTool {

	/** Tool name */
	public static final String TOOL_NAME = "PanTool";

	/** Tool tip text */
	public static final String TOOL_TIP = "PanTool";

	private Cursor cursor;

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
	public PanTool() {
		panning = false;
		beginPos = new Point(0, 0);
		endPos = new Point(0, 0);
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
		super.onMousePressed(ev);
		if (ev.getButton() == MouseEvent.BUTTON1) {
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
		super.onMouseDragged(ev);
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
		super.onMouseReleased(ev);
		if (panning && !beginPos.equals(endPos)) {
			getMapPane().move(endPos.x - beginPos.x, endPos.y - beginPos.y);
		}
		panning = false;
		beginPos.setLocation(0, 0);
		endPos.setLocation(0, 0);
	}

	/**
	 * Get the mouse cursor for this tool
	 */
	@Override
	public Cursor getCursor() {
		return cursor;
	}
}
