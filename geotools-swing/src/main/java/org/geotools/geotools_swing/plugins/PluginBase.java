package org.geotools.geotools_swing.plugins;

import org.geotools.geotools_swing.MapPane;

/**
 * 所有plugin的基类
 * @author sam
 *
 */
public abstract class PluginBase implements Plugin {
	
	/**
	 * 插件名称
	 */
	private String title;
	
	/**
	 * 所在分组
	 */
	private String group;
	
	/**
	 * 当前的画布对象
	 */
	private MapPane canvas;
	
	/**
	 * 所有plugin的基类
	 * @param title 插件名称
	 * @param group 插件分组
	 * @param image 图标
	 * @param map 画布
	 */
	public PluginBase(String title , String group, MapPane map){
		this.title = title;
		this.group = group;
		this.canvas = map;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MapPane getMapPane() {
		return canvas;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setMapPane(MapPane mapPane) {
		this.canvas = mapPane;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getTitle() {
		return this.title;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getGroup() {
		return this.group;
	}
}
