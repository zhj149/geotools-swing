package org.geotools.geotools_swing.plugins;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;

import org.apache.commons.lang3.StringUtils;
import org.geotools.geotools_swing.MapPane;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * 标尺插件
 * 
 * @author sam
 *
 */
public class ScalebarPlugin extends PluginBase {

	/**
	 * 标尺插件
	 * 
	 * @param title
	 * @param group
	 * @param map
	 */
	public ScalebarPlugin(String title, String group, MapPane map) {
		super(title, group, map);
	}

	/**
	 * {@inheritDoc} 将标尺直接画到图片上
	 */
	@Override
	public void beforePaint(Graphics2D g2d) {
		MapPane mapPane = this.getMapPane();
		// 屏幕分辨率
		int dpi = Toolkit.getDefaultToolkit().getScreenResolution();
		// 获取放大比例
		double scale = mapPane.getMapContent().getViewport().getWorldToScreen().getScaleX();
		// 一个像素的大小
		double i = 1 / (double) dpi;
		// 一个像素的尺寸
		double pixelInMeters = i * 0.0254;
		double inMeters = scale * pixelInMeters;

		Rectangle location = mapPane.getVisibleRectangle();

//		if (inMeters == 0.0) {
//			this.paint(g2d, location, "NaN");
//		} else {
//			if (scalebarUnits == UnitPolicy.IMPERIAL) {
//				result2 = calculateUnitAndLength(inMeters, location.width / crs. , Unit.MILE,
//						Unit.FOOT, Unit.YARD, Unit.INCHES);
//			} else {
//				/*
//				 * METRIC and AUTO both treated as metric, since auto is
//				 * converted above in CRS search
//				 */
//				result2 = calculateUnitAndLength(inMeters, location.width / type.getNumintervals(), KILOMETER, METER,
//						CENTIMETER);
//			}
//
//			int trueBarLength2 = result2.getLeft() * type.getNumintervals();
//			Pair<Integer, Unit> unitMeasure2 = result2.getRight();
//			int nice2 = unitMeasure2.getLeft();
//			Unit measurement2 = unitMeasure2.getRight();
//			doDraw(measurement2, context, trueBarLength2, nice2);
//		}
	}
	
//	public static Pair<Integer, Pair<Integer, Unit>> calculateUnitAndLength( double meterPerPixel,
//            int idealBarLength ) {
//
//        if (units == null || units.length == 0) {
//            throw new IllegalArgumentException("units must have at least one unit"); //$NON-NLS-1$
//        }
//
//        final double mIdealBarDistance = (meterPerPixel * idealBarLength);
//        final NiceIntegers niceIntegers = new NiceIntegers();
//
//        Unit unit = units[0];
//        double displayDistance = -1;
//        int barLengthPixels = idealBarLength;
//
//        for( int i = 0; displayDistance < 1 && i < units.length; i++ ) {
//            unit = units[i];
//            displayDistance = closestInt((int) unit.meterToUnit(mIdealBarDistance), niceIntegers);
//
//            barLengthPixels = (int) (unit.unitToMeter(displayDistance) / meterPerPixel);
//        }
//
//        Pair<Integer, Unit> unitMeasure = new Pair<Integer, Unit>((int) displayDistance, unit);
//
//        return new Pair<Integer, Pair<Integer, Unit>>(barLengthPixels, unitMeasure);
//    }

	/**
	 * 绘制具体的文本操作
	 * 
	 * @param g
	 * @param text
	 */
	private void paint(Graphics2D g, Rectangle location, String text) {

		if (StringUtils.isEmpty(text))
			return;

		int stringWidth = g.getFontMetrics().stringWidth(text);

		int iWidth = 150;
		int iX = location.width - iWidth - 5;
		int iY = location.height - 20;

		g.setColor(Color.WHITE);
		g.drawString(text, iX + (iWidth - stringWidth) / 2, iY - 2);
		g.drawLine(iX, iY, location.width - 5, iY);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void afterPaint(Graphics2D g2d, int dx, int dy) {

	}

}
