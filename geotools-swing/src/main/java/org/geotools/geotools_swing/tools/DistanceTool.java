package org.geotools.geotools_swing.tools;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;

import org.geotools.geometry.DirectPosition2D;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.geotools_swing.MapPane;
import org.geotools.geotools_swing.event.MapMouseEvent;
import org.geotools.geotools_swing.event.MapPaintListener;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;

/**
 * 测量工具
 * 
 * @author sam
 *
 */
public class DistanceTool extends AbstractZoomTool implements MapPaintListener {

	/**
	 * tools name
	 */
	public static final String TOOL_NAME = "Distance";

	/**
	 * Tool tip text
	 */
	public static final String TOOL_TIP = "测量工具";

	/**
	 * 地理信息对象生成工具
	 */
	public static GeometryFactory gf = JTSFactoryFinder.getGeometryFactory();

	/**
	 * 速度的文本格式化工具
	 */
	private static final DecimalFormat doubleDF = new DecimalFormat("###,###,###,##0.00");

	/**
	 * 绘制的字体
	 */
	private static final Font font = new Font("宋体", Font.BOLD, 16);

	/**
	 * 图标
	 */
	private Cursor cursor;

	/**
	 * 开始操作点(地理信息)
	 */
	private volatile com.vividsolutions.jts.geom.Point beginPos;

	/**
	 * 结束操作点(地理信息)
	 */
	private volatile com.vividsolutions.jts.geom.Point endPos;

	/**
	 * 当前的移动点
	 */
	private volatile com.vividsolutions.jts.geom.Point movePos;

	/**
	 * 是否正在移动
	 */
	private volatile boolean moving = false;

	/**
	 * 测量工具
	 */
	public DistanceTool() {

	}

	/**
	 * 将自己注册到绘制事件里
	 */
	@Override
	public void setMapPane(MapPane pane) {
		super.setMapPane(pane);
		if (pane != null) {
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
	 * 
	 */
	@Override
	public void onMouseClicked(MapMouseEvent ev) {

		// 只接收鼠标左键操作,因为将来右键可能有其他用处
		if (!(ev.getButton() == MouseEvent.BUTTON1))
			return;

		DirectPosition2D mapPos = ev.getWorldPos();
		if (!this.moving) {
			this.moving = true;
			this.beginPos = gf.createPoint(new Coordinate(mapPos.getX(), mapPos.getY()));
			movePos = gf.createPoint(new Coordinate(mapPos.getX(), mapPos.getY()));
		} else {
			this.moving = false;
			endPos = gf.createPoint(new Coordinate(mapPos.getX(), mapPos.getY()));
			movePos = null;
		}
	}

	/**
	 * 移动情况
	 */
	@Override
	public void onMouseMoved(MapMouseEvent ev) {
		if (this.moving && this.movePos != null) {
			DirectPosition2D mapPos = ev.getWorldPos();
			Coordinate coordinate = this.movePos.getCoordinate();
			coordinate.setOrdinate(Coordinate.X, mapPos.getX());
			coordinate.setOrdinate(Coordinate.Y, mapPos.getY());
			this.getMapPane().refresh();
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
	 * 绘制线条和计算距离的操作
	 */
	@Override
	public void afterPaint(Graphics2D g2d, int dx, int dy) {
		if (this.beginPos == null)
			return;

		AffineTransform w2c = this.getMapPane().getWorldToScreenTransform();
		Color colOrignal = g2d.getColor();

		Point2D ptBeginSrc = new Point2D.Double();
		Point2D ptEndSrc = new Point2D.Double();
		Point2D ptBeginTgr = new Point2D.Double();
		Point2D ptEndTgr = new Point2D.Double();
		double distance = 0.0d;

		if (this.moving) {
			if (this.movePos == null)
				return;

			ptBeginSrc.setLocation(beginPos.getX(), beginPos.getY());
			ptEndSrc.setLocation(movePos.getX(), movePos.getY());
			distance = movePos.distance(beginPos) * 100;
			
		} else {

			if (this.endPos == null)
				return;

			ptBeginSrc.setLocation(beginPos.getX(), beginPos.getY());
			ptEndSrc.setLocation(endPos.getX(), endPos.getY());
			distance = endPos.distance(beginPos) * 100;
		}

		w2c.transform(ptBeginSrc, ptBeginTgr);
		w2c.transform(ptEndSrc, ptEndTgr);

		g2d.setColor(Color.CYAN);

		int x1 = (int) ptBeginTgr.getX() + dx;
		int y1 = (int) ptBeginTgr.getY() + dy;
		int x2 = (int) ptEndTgr.getX() + dx;
		int y2 = (int) ptEndTgr.getY() + dy;
		g2d.drawLine(x1, y1, x2, y2);

		// 直接画到中间点上
		g2d.setColor(Color.BLACK);
		g2d.setFont(font);
		String strDis = doubleDF.format(distance);
		FontMetrics fontMetrics = g2d.getFontMetrics();
		int height = fontMetrics.getHeight();
		int width = fontMetrics.stringWidth(strDis);
		g2d.drawString(strDis, (x1 + x2) / 2 - width / 2, (y1 + y2) / 2 - height / 2);

		g2d.setColor(colOrignal);
	}

}
