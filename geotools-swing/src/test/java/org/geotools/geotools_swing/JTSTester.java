/**
 * 
 */
package org.geotools.geotools_swing;

import org.geotools.geometry.jts.JTSFactoryFinder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Polygon;

/**
 * @author sam
 *
 */
public class JTSTester {

	/**
	 * 当前的地理信息对象创建工具
	 */
	public static GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();

	/**
	 * 多边形对象
	 */
	private Polygon p1;

	/**
	 * 线条对象
	 */
	private LineString l1;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		p1 = geometryFactory.createPolygon(new Coordinate[] { new Coordinate(310, 410), new Coordinate(260, 210),
				new Coordinate(610, 120), new Coordinate(820, 210), new Coordinate(840, 300), new Coordinate(830, 400),
				new Coordinate(630, 480), new Coordinate(480, 490), new Coordinate(310, 410) });
		l1 = geometryFactory.createLineString(new Coordinate[] { new Coordinate(210, 340), new Coordinate(970, 340) });
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		
	}

	@Test
	public void test() {
	}

}
