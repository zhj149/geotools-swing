
package org.geotools.geotools_swing.tools;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;

import org.geotools.geometry.DirectPosition2D;
import org.geotools.geometry.Envelope2D;
import org.geotools.geotools_swing.MapPane;
import org.geotools.geotools_swing.event.MapMouseEvent;

/**
 * A cursor tool to zoom in the map pane display.
 * <p>
 * For mouse clicks, the display will be zoomed-in such that the map centre is
 * the position of the mouse click and the map width and height are calculated
 * as:
 * 
 * <pre>
 *    {@code len = len.old / z}
 * </pre>
 * 
 * where {@code z} is the linear zoom increment (>= 1.0)
 * <p>
 * The tool also responds to the user drawing a box on the map mapPane with
 * mouse click-and-drag to define the zoomed-in area.
 * 
 * @author Michael Bedward
 * @since 2.6
 * @source $URL$
 * @version $Id$
 */
public class ZoomInTool extends AbstractZoomTool implements DragBoxMapPaintListener {

	/** Tool name */
	public static final String TOOL_NAME = "ZoomIn";

	/** Tool tip text */
	public static final String TOOL_TIP = "Zoom In";

	/** Cursor hotspot coordinates */
	public static final Point CURSOR_HOTSPOT = new Point(14, 9);

	private Cursor cursor;

	private final Point startPosDevice;
	private final Point endPosDevice;
	private final Point2D startPosWorld;
	private volatile boolean dragged;

	/**
	 * Constructor
	 */
	public ZoomInTool() {
		startPosDevice = new Point();
		endPosDevice = new Point();
		startPosWorld = new DirectPosition2D();
		dragged = false;
	}

	/**
	 * 将自己注册到绘制事件里
	 */
	@Override
	public void setMapPane(MapPane pane) {
		super.setMapPane(pane);
		if (pane != null){
			pane.addPaintListener(this);
		}
	}

	/**
	 * 取消掉鼠标和绘制对象操作
	 */
	@Override
	public void unUsed() {
		super.unUsed();
		this.getMapPane().removePaintListener(this);
	}
	
	/**
	 * Zoom in by the currently set increment, with the map centred at the
	 * location (in world coords) of the mouse click
	 * 
	 * @param e
	 *            map mapPane mouse event
	 */
	@Override
	public void onMouseClicked(MapMouseEvent e) {
		Rectangle paneArea = getMapPane().getVisibleRectangle();
		DirectPosition2D mapPos = e.getWorldPos();

		double scale = getMapPane().getWorldToScreenTransform().getScaleX();
		double newScale = scale * zoom;

		DirectPosition2D corner = new DirectPosition2D(mapPos.getX() - 0.5d * paneArea.getWidth() / newScale,
				mapPos.getY() + 0.5d * paneArea.getHeight() / newScale);

		Envelope2D newMapArea = new Envelope2D();
		newMapArea.setFrameFromCenter(mapPos, corner);
		getMapPane().setDisplayArea(newMapArea);
	}

	/**
	 * Records the map position of the mouse event in case this button press is
	 * the beginning of a mouse drag
	 *
	 * @param ev
	 *            the mouse event
	 */
	@Override
	public void onMousePressed(MapMouseEvent ev) {
		startPosDevice.setLocation(ev.getPoint());
		startPosWorld.setLocation(ev.getWorldPos());
	}

	/**
	 * Records that the mouse is being dragged
	 *
	 * @param ev
	 *            the mouse event
	 */
	@Override
	public void onMouseDragged(MapMouseEvent ev) {
		dragged = true;
		endPosDevice.setLocation(ev.getPoint());
		getMapPane().refresh();
	}

	/**
	 * If the mouse was dragged, determines the bounds of the box that the user
	 * defined and passes this to the mapPane's {@code setDisplayArea} method.
	 *
	 * @param ev
	 *            the mouse event
	 */
	@Override
	public void onMouseReleased(MapMouseEvent ev) {
		if (dragged && !ev.getPoint().equals(startPosDevice)) {
			Envelope2D env = new Envelope2D();
			env.setFrameFromDiagonal(startPosWorld, ev.getWorldPos());
			dragged = false;
			getMapPane().setDisplayArea(env);
		}
	}

	/**
	 * Get the mouse cursor for this tool
	 */
	@Override
	public Cursor getCursor() {
		return cursor;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isDrag() {
		return this.dragged;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Point getStartDevicePos() {
		return this.startPosDevice;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Point getEndDevicePos() {
		return this.endPosDevice;
	}
}
