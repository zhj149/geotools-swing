
package org.geotools.geotools_swing.tools;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashSet;
import java.util.Set;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.geometry.DirectPosition2D;
import org.geotools.geometry.Envelope2D;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.geotools_swing.MapPane;
import org.geotools.geotools_swing.event.MapMouseEvent;
import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.Fill;
import org.geotools.styling.Graphic;
import org.geotools.styling.Mark;
import org.geotools.styling.Rule;
import org.geotools.styling.Stroke;
import org.geotools.styling.Style;
import org.geotools.styling.StyleFactory;
import org.geotools.styling.Symbolizer;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.identity.FeatureId;

/**
 * 对象选择功能
 * @author sam
 *
 */
public class SelectFeatureTool extends AbstractZoomTool implements DragBoxMapPaintListener {

	/** Tool name */
	public static final String TOOL_NAME = "SelectFeature";

	/** Tool tip text */
	public static final String TOOL_TIP = "Select Feature";

	/** Cursor hotspot coordinates */
	public static final Point CURSOR_HOTSPOT = new Point(14, 9);

	private Cursor cursor;

	private final Point startPosDevice;
	private final Point endPosDevice;
	private final Point2D startPosWorld;
	private volatile boolean dragged;
	
	/**
	 * 查询工具类
	 */
	private StyleFactory sf = CommonFactoryFinder.getStyleFactory();
    private FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2();
    
    private static final Color LINE_COLOUR = Color.BLUE;
    private static final Color FILL_COLOUR = Color.CYAN;
    private static final Color SELECTED_COLOUR = Color.YELLOW;
    private static final float OPACITY = 1.0f;
    private static final float LINE_WIDTH = 1.0f;
    private static final float POINT_SIZE = 10.0f;


	/**
	 * Constructor
	 */
	public SelectFeatureTool() {
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
	 * 单选
	 */
	@Override
	public void onMouseClicked(MapMouseEvent ev) {
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
	
	 /**
     * This method is called by our feature selection tool when
     * the user has clicked on the map.
     *
     * @param pos map (world) coordinates of the mouse cursor
     */
    private void selectFeatures(MapMouseEvent ev) {

        System.out.println("Mouse click at: " + ev.getMapPosition());

        /*
         * Construct a 5x5 pixel rectangle centred on the mouse click position
         */
        Point screenPos = ev.getPoint();
        Rectangle screenRect = new Rectangle(screenPos.x-2, screenPos.y-2, 5, 5);
        
        /*
         * Transform the screen rectangle into bounding box in the coordinate
         * reference system of our map context. Note: we are using a naive method
         * here but GeoTools also offers other, more accurate methods.
         */
        AffineTransform screenToWorld = getMapPane().getScreenToWorldTransform();
        Rectangle2D worldRect = screenToWorld.createTransformedShape(screenRect).getBounds2D();
        ReferencedEnvelope bbox = new ReferencedEnvelope(
                worldRect,
                getMapPane().getMapContent().getCoordinateReferenceSystem());
        
        /*
         * Create a Filter to select features that intersect with
         * the bounding box
         */
        Filter filter = ff.intersects(ff.property("the_geom"), ff.literal(bbox));

        /*
         * Use the filter to identify the selected features
         */
        try {
            SimpleFeatureCollection selectedFeatures =
                    (SimpleFeatureCollection)this.getMapPane().getMapContent().layers().get(0).getFeatureSource().getFeatures(filter);

            SimpleFeatureIterator iter = selectedFeatures.features();
            Set<FeatureId> IDs = new HashSet<FeatureId>();
            try {
                while (iter.hasNext()) {
                    SimpleFeature feature = iter.next();
                    IDs.add(feature.getIdentifier());

                    System.out.println("   " + feature.getIdentifier());
                }

            } finally {
                iter.close();
            }

            if (IDs.isEmpty()) {
                System.out.println("   no feature selected");
            }

            displaySelectedFeatures(IDs);

        } catch (Exception ex) {
            ex.printStackTrace();
            return;
        }
    }
    
    /**
     * Sets the display to paint selected features yellow and
     * unselected features in the default style.
     *
     * @param IDs identifiers of currently selected features
     */
    public void displaySelectedFeatures(Set<FeatureId> IDs) {
        Style style;

        if (IDs.isEmpty()) {
            style = createDefaultStyle();

        } else {
            style = createSelectedStyle(IDs);
        }

        Layer layer = getMapPane().getMapContent().layers().get(0);
        ((FeatureLayer) layer).setStyle(style);
        getMapPane().repaint(true);
    }
    
    /**
     * Create a default Style for feature display
     */
    private Style createDefaultStyle() {
        Rule rule = createRule(Color.GREEN, Color.GREEN);

        FeatureTypeStyle fts = sf.createFeatureTypeStyle();
        fts.rules().add(rule);

        Style style = sf.createStyle();
        style.featureTypeStyles().add(fts);
        return style;
    }
    
    /**
     * Create a Style where features with given IDs are painted
     * yellow, while others are painted with the default colors.
     */
    private Style createSelectedStyle(Set<FeatureId> IDs) {
        Rule selectedRule = createRule(SELECTED_COLOUR, SELECTED_COLOUR);
        selectedRule.setFilter(ff.id(IDs));

        Rule otherRule = createRule(LINE_COLOUR, FILL_COLOUR);
        otherRule.setElseFilter(true);

        FeatureTypeStyle fts = sf.createFeatureTypeStyle();
        fts.rules().add(selectedRule);
        fts.rules().add(otherRule);

        Style style = sf.createStyle();
        style.featureTypeStyles().add(fts);
        return style;
    }
    
    /**
     * Helper for createXXXStyle methods. Creates a new Rule containing
     * a Symbolizer tailored to the geometry type of the features that
     * we are displaying.
     */
    private Rule createRule(Color outlineColor, Color fillColor) {
        Symbolizer symbolizer = null;
        Fill fill = null;
        Stroke stroke = sf.createStroke(ff.literal(outlineColor), ff.literal(LINE_WIDTH));

//        switch (geometryType) {
//            case POLYGON:
//                fill = sf.createFill(ff.literal(fillColor), ff.literal(OPACITY));
//                symbolizer = sf.createPolygonSymbolizer(stroke, fill, geometryAttributeName);
//                break;
//
//            case LINE:
//                symbolizer = sf.createLineSymbolizer(stroke, geometryAttributeName);
//                break;
//
//            case POINT:
//                fill = sf.createFill(ff.literal(fillColor), ff.literal(OPACITY));
//
//                Mark mark = sf.getCircleMark();
//                mark.setFill(fill);
//                mark.setStroke(stroke);
//
//                Graphic graphic = sf.createDefaultGraphic();
//                graphic.graphicalSymbols().clear();
//                graphic.graphicalSymbols().add(mark);
//                graphic.setSize(ff.literal(POINT_SIZE));
//
//                symbolizer = sf.createPointSymbolizer(graphic, geometryAttributeName);
//        }

        Rule rule = sf.createRule();
        rule.symbolizers().add(symbolizer);
        return rule;
    }
}
