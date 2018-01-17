package org.geotools.geotools_swing.tools;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Rectangle;

import org.geotools.geometry.DirectPosition2D;
import org.geotools.geometry.Envelope2D;
import org.geotools.geotools_swing.MapPane;
import org.geotools.geotools_swing.event.MapMouseEvent;

/**
 * A cursor tool to zoom out the map pane display.
 * <p>
 * For mouse clicks, the display will be zoomed-out such that the map centre is
 * the position of the mouse click and the map width and height are calculated
 * as:
 * 
 * <pre>
 *    {@code len = len.old * z}
 * </pre>
 * 
 * where {@code z} is the linear zoom increment (>= 1.0)
 * 
 * @author Michael Bedward
 * @since 2.6
 *
 * @source $URL$
 * @version $Id$
 */
public class ZoomOutTool extends AbstractZoomTool {

	/** Tool name */
	public static final String TOOL_NAME = "ZoomOut";

	/** Tool tip text */
	public static final String TOOL_TIP = "ZoomOut";

	/** Cursor hotspot coordinates */
	public static final Point CURSOR_HOTSPOT = new Point(14, 9);

	private Cursor cursor;

	/**
	 * Constructor
	 */
	public ZoomOutTool() {
	}

	/**
	 * 将自己注册到绘制事件里
	 */
	@Override
	public void setMapPane(MapPane pane) {
		super.setMapPane(pane);
	}

	/**
	 * 取消掉鼠标和绘制对象操作
	 */
	@Override
	public void unUsed() {
		super.unUsed();
	}

	/**
	 * Zoom out by the currently set increment, with the map centred at the
	 * location (in world coords) of the mouse click
	 *
	 * @param ev
	 *            the mouse event
	 */
	@Override
	public void onMouseClicked(MapMouseEvent ev) {
		Rectangle paneArea = getMapPane().getVisibleRectangle();
		DirectPosition2D mapPos = ev.getWorldPos();

		double scale = getMapPane().getWorldToScreenTransform().getScaleX();
		double newScale = scale / zoom;
		
		if (isNotZoomed(newScale))
			return;

		DirectPosition2D corner = new DirectPosition2D(mapPos.getX() - 0.5d * paneArea.getWidth() / newScale,
				mapPos.getY() + 0.5d * paneArea.getHeight() / newScale);

		Envelope2D newMapArea = new Envelope2D();
		newMapArea.setFrameFromCenter(mapPos, corner);
		getMapPane().setDisplayArea(newMapArea);
		//然后平移到鼠标操作点
		getMapPane().move((int) (ev.getX() - paneArea.getWidth() / 2),
				(int) (ev.getY() - paneArea.getHeight() / 2));
	}

	/**
	 * Get the mouse cursor for this tool
	 */
	@Override
	public Cursor getCursor() {
		return cursor;
	}

}
