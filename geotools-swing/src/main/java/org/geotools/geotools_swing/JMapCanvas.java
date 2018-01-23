package org.geotools.geotools_swing;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.swing.JPanel;

import org.geotools.geometry.DirectPosition2D;
import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.geotools_swing.event.DefaultMapMouseEventDispatcher;
import org.geotools.geotools_swing.event.MapMouseEventDispatcher;
import org.geotools.geotools_swing.event.MapMouseListener;
import org.geotools.geotools_swing.event.MapPaintListener;
import org.geotools.geotools_swing.event.MapPaneEvent;
import org.geotools.geotools_swing.event.MapPaneListener;
import org.geotools.geotools_swing.tools.CursorTool;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.map.MapViewport;
import org.geotools.map.event.MapBoundsEvent;
import org.geotools.map.event.MapBoundsListener;
import org.geotools.map.event.MapLayerListEvent;
import org.geotools.map.event.MapLayerListListener;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.renderer.GTRenderer;
import org.geotools.renderer.label.LabelCacheImpl;
import org.geotools.renderer.lite.LabelCache;
import org.geotools.renderer.lite.StreamingRenderer;
import org.geotools.util.logging.Logging;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

/**
 * javafx实现的geotools画布对象
 * 
 * @author sam
 *
 */
public class JMapCanvas extends JPanel implements MapPane, MapLayerListListener, MapBoundsListener {

	private static final long serialVersionUID = 9110483825678702033L;

	/**
	 * 重绘间隔
	 */
	public static final int DEFAULT_RESIZING_PAINT_DELAY = 500; // delay in
																// milliseconds

	/**
	 * 当前的日志对象
	 */
	protected static final Logger LOGGER = Logging.getLogger("org.geotools.JavaMapPane");

	/**
	 * 当前的视界范围
	 */
	protected ReferencedEnvelope fullExtent;

	/**
	 * 当前地图的上下文
	 */
	protected MapContent mapContent;

	/**
	 * 绘制上下文对象
	 */
	protected GTRenderer renderer;

	/*
	 * If the user sets the display area before the pane is shown on screen we
	 * store the requested envelope with this field and refer to it when the
	 * pane is shown.
	 */
	protected ReferencedEnvelope pendingDisplayArea;

	/**
	 * 注册的地图事件
	 */
	protected List<MapPaneListener> paneListeners = new LinkedList<>();

	/**
	 * 绘制事件
	 */
	protected List<MapPaintListener> paintListeners = new LinkedList<>();

	/**
	 * 缓存(应该是标签)
	 */
	protected LabelCache labelCache;

	/**
	 * 重绘的时候是否清理标签缓存数据
	 */
	private volatile boolean clearLabelCache = true;

	/**
	 * 当前的内存图片
	 */
	private BufferedImage baseImage;

	/**
	 * 内存2D画布对象
	 */
	private Graphics2D memory2D;

	/**
	 * 已经绘制好的地图偏移量
	 */
	private volatile Point offsetImage = new Point(0, 0);

	/**
	 * 鼠标操作事件包装对象
	 */
	protected MapMouseEventDispatcher mapMouseEventDispatcher;

	/**
	 * 当前的鼠标操作工具
	 */
	private CursorTool cursorTool;

	/**
	 * 缓存起来的世界到屏幕转换的转换对象
	 */
	private AffineTransform worldToScreen;

	/**
	 * 缓存起来的屏幕到世界的转换对象
	 */
	private AffineTransform screenToWorld;

	/**
	 * 背景的填充色
	 */
	private Color backgroundColor = new Color(100, 106, 116);

	/**
	 * javafx实现的geotools画布对象
	 * 
	 * @param content
	 */
	public JMapCanvas(MapContent content) {

		// 实现画布
		doSetRenderer(new StreamingRenderer());
		this.setMapContent(content);

		mapMouseEventDispatcher = new DefaultMapMouseEventDispatcher(this);
		this.addMouseListener(mapMouseEventDispatcher);
		this.addMouseMotionListener(mapMouseEventDispatcher);
		this.addMouseWheelListener(mapMouseEventDispatcher);

		this.addComponentListener(new ComponentAdapter() {

			@Override
			public void componentResized(ComponentEvent e) {
				JMapCanvas.this.repaint(true);
			}
		});

	}

	// begin functions

	/**
	 * Gets the full extent of map context's layers. The only reason this method
	 * is defined is to avoid having try-catch blocks all through other methods.
	 */
	private void setFullExtent() {
		if (mapContent != null) {
			try {

				fullExtent = mapContent.getMaxBounds();

				/*
				 * Guard agains degenerate envelopes (e.g. empty map layer or
				 * single point feature)
				 */
				if (fullExtent == null) {
					// set arbitrary bounds centred on 0,0
					fullExtent = worldEnvelope();
				}

			} catch (Exception ex) {
				throw new IllegalStateException(ex);
			}
		} else {
			fullExtent = null;
		}
	}

	/**
	 * 视界地图边界
	 * 
	 * @return
	 */
	private ReferencedEnvelope worldEnvelope() {
		return new ReferencedEnvelope(-180, 180, -90, 90, DefaultGeographicCRS.WGS84);
	}

	/**
	 * Helper method for {@linkplain #setDisplayArea} which is also called by
	 * other methods that want to set the display area without provoking
	 * repainting of the display
	 *
	 * @param envelope
	 *            requested display area
	 */
	protected void doSetDisplayArea(Envelope envelope) {

		Rectangle curPaintArea = this.getVisibleRectangle();

		assert (mapContent != null && curPaintArea != null && !curPaintArea.isEmpty());

		if (equalsFullExtent(envelope)) {
			setTransforms(fullExtent, curPaintArea);
		} else {
			setTransforms(envelope, curPaintArea);
		}
		ReferencedEnvelope adjustedEnvelope = getDisplayArea();
		mapContent.getViewport().setBounds(adjustedEnvelope);

		// Publish the resulting display area with the event
		publishEvent(new MapPaneEvent(this, MapPaneEvent.Type.DISPLAY_AREA_CHANGED, getDisplayArea()));
	}

	/**
	 * Check if the envelope corresponds to full extent. It will probably not
	 * equal the full extent envelope because of slack space in the display
	 * area, so we check that at least one pair of opposite edges are equal to
	 * the full extent envelope, allowing for slack space on the other two
	 * sides.
	 * <p>
	 * Note: this method returns {@code false} if the full extent envelope is
	 * wholly within the requested envelope (e.g. user has zoomed out from full
	 * extent), only touches one edge, or touches two adjacent edges. In all
	 * these cases we assume that the user wants to maintain the slack space in
	 * the display.
	 * <p>
	 * This method is part of the work-around that the map pane needs because of
	 * the differences in how raster and vector layers are treated by the
	 * renderer classes.
	 *
	 * @param envelope
	 *            a pending display envelope to compare to the full extent
	 *            envelope
	 *
	 * @return true if the envelope is coincident with the full extent evenlope
	 *         on at least two edges; false otherwise
	 *
	 * @todo My logic here seems overly complex - I'm sure there must be a
	 *       simpler way for the map pane to handle this.
	 */
	private boolean equalsFullExtent(final Envelope envelope) {
		if (fullExtent == null || envelope == null) {
			return false;
		}

		final double TOL = 1.0e-6d * (fullExtent.getWidth() + fullExtent.getHeight());

		boolean touch = false;
		if (Math.abs(envelope.getMinimum(0) - fullExtent.getMinimum(0)) < TOL) {
			touch = true;
		}
		if (Math.abs(envelope.getMaximum(0) - fullExtent.getMaximum(0)) < TOL) {
			if (touch) {
				return true;
			}
		}
		if (Math.abs(envelope.getMinimum(1) - fullExtent.getMinimum(1)) < TOL) {
			touch = true;
		}
		if (Math.abs(envelope.getMaximum(1) - fullExtent.getMaximum(1)) < TOL) {
			if (touch) {
				return true;
			}
		}

		return false;
	}

	/**
	 * 发布map事件订阅
	 * 
	 * @param ev
	 */
	private void publishEvent(MapPaneEvent ev) {
		for (MapPaneListener listener : paneListeners) {
			switch (ev.getType()) {
			case NEW_CONTEXT:
				listener.onNewContext(ev);
				break;

			case NEW_RENDERER:
				listener.onNewRenderer(ev);
				break;

			case PANE_RESIZED:
				listener.onResized(ev);
				break;

			case DISPLAY_AREA_CHANGED:
				listener.onDisplayAreaChanged(ev);
				break;

			case RENDERING_STARTED:
				listener.onRenderingStarted(ev);
				break;

			case RENDERING_STOPPED:
				listener.onRenderingStopped(ev);
				break;

			case RENDERING_PROGRESS:
				listener.onRenderingProgress(ev);
				break;
			}
		}
	}

	/**
	 * 设置绘制对象
	 * 
	 * @param newRenderer
	 */
	private void doSetRenderer(GTRenderer newRenderer) {
		if (newRenderer != null) {
			Map<Object, Object> hints = newRenderer.getRendererHints();
			if (hints == null) {
				hints = new HashMap<Object, Object>();
			}

			if (newRenderer instanceof StreamingRenderer) {
				if (hints.containsKey(StreamingRenderer.LABEL_CACHE_KEY)) {
					labelCache = (LabelCache) hints.get(StreamingRenderer.LABEL_CACHE_KEY);
				} else {
					labelCache = new LabelCacheImpl();
					hints.put(StreamingRenderer.LABEL_CACHE_KEY, labelCache);
				}
			}

			newRenderer.setRendererHints(hints);

			if (mapContent != null) {
				newRenderer.setMapContent(mapContent);
			}
		}

		renderer = newRenderer;
	}

	/**
	 * 当获取worldtoscreen失败的时候，自己重新计算的worldtoscreen
	 * 
	 * @param envelope
	 * @param paintArea
	 */
	private void setTransforms(final Envelope envelope, final Rectangle paintArea) {

		ReferencedEnvelope refEnv = null;
		if (envelope != null) {
			refEnv = new ReferencedEnvelope(envelope);
		} else {
			refEnv = worldEnvelope();
		}

		double xscale = paintArea.getWidth() / refEnv.getWidth();
		double yscale = paintArea.getHeight() / refEnv.getHeight();

		double scale = Math.min(xscale, yscale);

		double xoff = refEnv.getMedian(0) * scale - paintArea.getCenterX();
		double yoff = refEnv.getMedian(1) * scale + paintArea.getCenterY();

		worldToScreen = new AffineTransform(scale, 0, 0, -scale, -xoff, yoff);
		try {
			screenToWorld = worldToScreen.createInverse();
		} catch (NoninvertibleTransformException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 重新设置坐标系
	 * 
	 * @param crs
	 */
	public void setCrs(CoordinateReferenceSystem crs) {
		try {

			ReferencedEnvelope rEnv = getDisplayArea();

			CoordinateReferenceSystem sourceCRS = rEnv.getCoordinateReferenceSystem();
			CoordinateReferenceSystem targetCRS = crs;

			MathTransform transform = CRS.findMathTransform(sourceCRS, targetCRS);
			com.vividsolutions.jts.geom.Envelope newJtsEnv = JTS.transform(rEnv, transform);

			ReferencedEnvelope newEnvelope = new ReferencedEnvelope(newJtsEnv, targetCRS);
			mapContent.getViewport().setBounds(newEnvelope);
			fullExtent = null;
			doSetDisplayArea(newEnvelope);

			// ReferencedEnvelope displayArea =
			getDisplayArea();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 绘制成功后清理缓存的操作
	 */
	public void onRenderingCompleted() {
		if (clearLabelCache) {
			labelCache.clear();
		}
		clearLabelCache = false;

		MapPaneEvent ev = new MapPaneEvent(this, MapPaneEvent.Type.RENDERING_STOPPED);
		publishEvent(ev);
	}

	// end

	// begin MapPane implements

	/**
	 * 当前地图的上下文
	 */
	public MapContent getMapContent() {
		return mapContent;
	}

	/**
	 * 重新设置地图上下文
	 */
	public void setMapContent(MapContent content) {

		if (this.mapContent != content) {

			if (this.mapContent != null) {
				this.mapContent.removeMapLayerListListener(this);
			}

			this.mapContent = content;

			if (content != null) {
				MapViewport viewport = mapContent.getViewport();
				viewport.setMatchingAspectRatio(true);
				Rectangle rect = this.getVisibleRectangle();
				if (!rect.isEmpty()) {
					viewport.setScreenArea(rect);
				}
				this.mapContent.addMapLayerListListener(this);
				this.mapContent.addMapBoundsListener(this);

				// set all layers as selected by default for the info tool
				for (Layer layer : content.layers()) {
					layer.setSelected(true);
				}

				setFullExtent();
				doSetDisplayArea(mapContent.getViewport().getBounds());
			}

			if (renderer != null) {
				renderer.setMapContent(this.mapContent);
			}

			MapPaneEvent ev = new MapPaneEvent(this, MapPaneEvent.Type.NEW_CONTEXT);
			publishEvent(ev);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public GTRenderer getRenderer() {
		if (renderer == null) {
			doSetRenderer(new StreamingRenderer());
		}
		return renderer;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setRenderer(GTRenderer renderer) {
		doSetRenderer(renderer);
	}

	/**
	 * {@inheritDoc}
	 */
	public Color getBackground() {
		return this.backgroundColor;
	}

	/**
	 * 设置画布的背景填充色
	 * 
	 * @param color
	 */
	public void setBackground(Color color) {
		if (color != null) {
			this.backgroundColor = color;
		}
		super.setBackground(color);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MapMouseEventDispatcher getMouseEventDispatcher() {
		return mapMouseEventDispatcher;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setMouseEventDispatcher(MapMouseEventDispatcher dispatcher) {
		if (mapMouseEventDispatcher != dispatcher) {
			if (this.mapMouseEventDispatcher != null && dispatcher != null) {
				List<MapMouseListener> listeners = mapMouseEventDispatcher.getAllListeners();
				for (MapMouseListener l : listeners) {
					dispatcher.addMouseListener(l);
				}
				mapMouseEventDispatcher.removeAllListeners();
			}
			this.mapMouseEventDispatcher = dispatcher;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ReferencedEnvelope getDisplayArea() {
		ReferencedEnvelope aoi = null;
		Rectangle curPaintArea = this.getVisibleRectangle();

		if (curPaintArea != null && screenToWorld != null) {
			Point2D p0 = new Point2D.Double(curPaintArea.getMinX(), curPaintArea.getMinY());
			Point2D p1 = new Point2D.Double(curPaintArea.getMaxX(), curPaintArea.getMaxY());
			screenToWorld.transform(p0, p0);
			screenToWorld.transform(p1, p1);

			aoi = new ReferencedEnvelope(Math.min(p0.getX(), p1.getX()), Math.max(p0.getX(), p1.getX()),
					Math.min(p0.getY(), p1.getY()), Math.max(p0.getY(), p1.getY()),
					mapContent.getCoordinateReferenceSystem());
		}

		return aoi;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @return
	 */
	public DirectPosition2D getMapCenter() {
		AffineTransform tr = this.getScreenToWorldTransform();
		DirectPosition2D pos = new DirectPosition2D(this.getWidth() / 2, this.getHeight() / 2);
		tr.transform(pos, pos);
		pos.setCoordinateReferenceSystem(this.getMapContent().getCoordinateReferenceSystem());
		return pos;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setDisplayArea(Envelope envelope) {
		if (envelope == null) {
			throw new IllegalArgumentException("envelope must not be null");
		}

		doSetDisplayArea(envelope);
		if (mapContent != null) {
			clearLabelCache = true;
			this.repaint(false);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void reset() {
		if (mapContent != null) {
			setDisplayArea(mapContent.getMaxBounds());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AffineTransform getScreenToWorldTransform() {

		if (mapContent != null) {

			if (screenToWorld == null) {
				this.setTransforms(this.getDisplayArea(), this.getVisibleRectangle());
				return new AffineTransform(screenToWorld);
			} else {
				return new AffineTransform(screenToWorld);
			}

		} else {
			return null;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AffineTransform getWorldToScreenTransform() {

		if (mapContent != null) {

			if (worldToScreen == null) {
				this.setTransforms(this.getDisplayArea(), this.getVisibleRectangle());
				return new AffineTransform(worldToScreen);
			} else {
				return new AffineTransform(worldToScreen);
			}

		} else {
			return null;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addMapPaneListener(MapPaneListener listener) {
		this.paneListeners.add(listener);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeMapPaneListener(MapPaneListener listener) {
		this.paneListeners.remove(listener);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addMouseListener(MapMouseListener listener) {
		if (this.mapMouseEventDispatcher != null)
			this.mapMouseEventDispatcher.addMouseListener(listener);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeMouseListener(MapMouseListener listener) {
		if (this.mapMouseEventDispatcher != null)
			this.mapMouseEventDispatcher.removeMouseListener(listener);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CursorTool getCursorTool() {
		return this.cursorTool;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setCursorTool(CursorTool tool) {
		if (this.cursorTool != null) {
			this.cursorTool.unUsed();
		}

		this.cursorTool = tool;

		if (this.cursorTool != null) {
			this.cursorTool.setMapPane(this);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void moveImage(int dx, int dy) {
		if (this.baseImage != null) {
			offsetImage.setLocation(dx, dy);
			this.repaint();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void move(int dx, int dy) {

		final ReferencedEnvelope env = getDisplayArea();
		if (env == null)
			return;
		DirectPosition2D newPos = new DirectPosition2D(dx, dy);
		AffineTransform screenToWorldTransform = this.getScreenToWorldTransform();
		screenToWorldTransform.transform(newPos, newPos);

		env.translate(env.getMinimum(0) - newPos.x, env.getMaximum(1) - newPos.y);
		doSetDisplayArea(env);
		this.repaint(false);
		offsetImage.setLocation(0, 0);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void repaint(boolean resize) {

		Rectangle r = getVisibleRectangle();
		if (baseImage == null || resize) {
			baseImage = new BufferedImage(r.width, r.height, BufferedImage.TYPE_INT_ARGB);
			clearLabelCache = true;
			memory2D = baseImage.createGraphics();
//			memory2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//			memory2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		}
		memory2D.setBackground(this.backgroundColor);
		memory2D.clearRect(0, 0, r.width, r.height);
		
		this.renderer.paint(memory2D, r, this.mapContent.getMaxBounds(), getWorldToScreenTransform());

		if (this.paintListeners != null && !this.paintListeners.isEmpty()) {
			for (MapPaintListener listener : this.paintListeners) {
				listener.beforePaint(memory2D);
			}
		}

		this.clearLabelCache = true;
		this.onRenderingCompleted();
		this.repaint();
	}

	/**
	 * {@inheritDoc}
	 */
	public void refresh() {

		if (memory2D != null) {
			if (this.paintListeners != null && !this.paintListeners.isEmpty()) {
				for (MapPaintListener listener : this.paintListeners) {
					listener.beforePaint(memory2D);
				}
			}
		}

		this.repaint();
	}

	/**
	 * 重绘操作
	 */
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		if (this.baseImage != null) {
			g.drawImage(baseImage, offsetImage.x, offsetImage.y, null);
		}

		if (this.paintListeners != null && !this.paintListeners.isEmpty()) {
			for (MapPaintListener listener : this.paintListeners) {
				listener.afterPaint((Graphics2D) g, offsetImage.x, offsetImage.y);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Rectangle getVisibleRectangle() {
		Rectangle rectangle = new Rectangle(0, 0, (int) this.getWidth(), (int) this.getHeight());
		// javafx画布在初始化的时候，是没有大小的，所以为了防止出错，给个默认大小
		if (rectangle.isEmpty()) {
			rectangle.width = 640;
			rectangle.height = 480;
		}
		return rectangle;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addPaintListener(MapPaintListener listener) {
		this.paintListeners.add(listener);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removePaintListener(MapPaintListener listener) {
		this.paintListeners.remove(listener);
	}

	// end

	// begin layer implements

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void layerAdded(MapLayerListEvent event) {
		this.reset();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void layerRemoved(MapLayerListEvent event) {
		this.repaint(false);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void layerChanged(MapLayerListEvent event) {
		this.repaint(false);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void layerMoved(MapLayerListEvent event) {
		this.repaint(false);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void layerPreDispose(MapLayerListEvent event) {

	}

	// end

	// begin mapbound implements

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void mapBoundsChanged(MapBoundsEvent event) {
		int type = event.getType();
		if ((type & MapBoundsEvent.COORDINATE_SYSTEM_MASK) != 0) {
			/*
			 * The coordinate reference system has changed. Set the map to
			 * display the full extent of layer bounds to avoid the effect of a
			 * shrinking map
			 */
			setFullExtent();
			reset();
		}
	}

	// end
}
