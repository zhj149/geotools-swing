/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2011, Open Source Geospatial Foundation (OSGeo)
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
package org.geotools.geotools_swing;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;

import org.geotools.geometry.DirectPosition2D;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.geotools_swing.event.MapMouseEventDispatcher;
import org.geotools.geotools_swing.event.MapMouseListener;
import org.geotools.geotools_swing.event.MapPaintListener;
import org.geotools.geotools_swing.event.MapPaneListener;
import org.geotools.geotools_swing.tools.CursorTool;
import org.geotools.map.MapContent;
import org.geotools.renderer.GTRenderer;
import org.opengis.geometry.Envelope;

/**
 * 地图画布对外公布的操作接口
 * 
 * @author sam
 * @version 1.0
 */
public interface MapPane {

	/**
	 * 获取当前的地图上下文对象
	 * 
	 * @return
	 */
	public MapContent getMapContent();

	/**
	 * 设置当前的地图上下文
	 * 
	 * @param content
	 */
	public void setMapContent(MapContent content);
	
	/**
	 * 获取画布的背景填充色
	 * @return
	 */
	public Color getBackground();
	
	/**
	 * 设置画布的背景填充色
	 * @param color
	 */
	public void setBackground(Color color);

	/**
	 * 获取鼠标操作包装对象
	 * 
	 * @return
	 */
	public MapMouseEventDispatcher getMouseEventDispatcher();

	/**
	 * 设置鼠标操作包装对象
	 * 
	 * @param dispatcher
	 */
	public void setMouseEventDispatcher(MapMouseEventDispatcher dispatcher);

	/**
	 * 获取当前的显示地理范围
	 * 
	 * @return
	 */
	public ReferencedEnvelope getDisplayArea();

	/**
	 * 设置当前的地理坐标系到
	 * 
	 * @param envelope
	 */
	public void setDisplayArea(Envelope envelope);

	/**
	 * 转换地图到全景区域并重绘
	 */
	public void reset();

	/**
	 * 获取屏幕坐标系到地理坐标系的转换
	 * 
	 * @return
	 */
	public AffineTransform getScreenToWorldTransform();

	/**
	 * 获取地理信息坐标系到屏幕的转换
	 * 
	 * @return
	 */
	public AffineTransform getWorldToScreenTransform();

	/**
	 * 添加地图操作监听
	 * 
	 * @param listener
	 */
	public void addMapPaneListener(MapPaneListener listener);

	/**
	 * 移除地图操作事件监听
	 * 
	 * @param listener
	 */
	public void removeMapPaneListener(MapPaneListener listener);

	/**
	 * 添加鼠标事件 {@linkplain StatusBar}
	 * 
	 * @param listener
	 */
	public void addMouseListener(MapMouseListener listener);

	/**
	 * 移除鼠标事件
	 * 
	 * @param listener
	 */
	public void removeMouseListener(MapMouseListener listener);
	
	/**
	 * 增加绘制listener
	 * 
	 * @param listener
	 */
	public void addPaintListener(MapPaintListener listener);

	/**
	 * 移除绘制listener
	 * 
	 * @param listener
	 */
	public void removePaintListener(MapPaintListener listener);

	/**
	 * 获取当前的操作工具对象
	 * 
	 * @return
	 */
	public CursorTool getCursorTool();

	/**
	 * 设置当前的操作工具对象
	 * 
	 * @param tool
	 */
	public void setCursorTool(CursorTool tool);

	/**
	 * 拖动图片位置的操作
	 * 
	 * @param dx
	 *            x轴偏移量
	 * @param dy
	 *            y轴偏移量
	 */
	public void moveImage(int dx, int dy);
	
	/**
	 * 将视界平移并重绘
	 * @param dx x轴平移量
	 * @param dy y轴平移量
	 */
	public void move(int dx,int dy);

	/**
	 * 重绘地图操作
	 * 
	 * @param resize
	 *            当size发生变化对手执行的操作
	 */
	public void repaint(boolean resize);
	
	/**
	 * 刷新，不重绘地图
	 */
	public void refresh();

	/**
	 * 获取世界的中心点，获取的是地理信息位置 返回的是world coordinates
	 * 
	 * @return
	 */
	public DirectPosition2D getMapCenter();
	
	/**
	 * 获取当前画布的可视范围大小(像素)
	 * @return
	 */
	public Rectangle getVisibleRectangle();

	/**
	 * 获取绘制对象
	 * 
	 * @return
	 */
	public GTRenderer getRenderer();

	/**
	 * 设置绘制对象
	 * 
	 * @param renderer
	 */
	public void setRenderer(GTRenderer renderer);
}
