package org.geotools.geotools_swing.plugins;

import org.geotools.geotools_swing.MapListener;
import org.geotools.geotools_swing.event.MapPaintListener;

/**
 * 插件接口对象
 * @author sam
 *
 */
public interface Plugin extends MapPaintListener, MapListener {
	
	//begin constant

	/**
	 * 插件为图层插件
	 */
	public static final String GROUP_LAYER = "layergroup";
	
	/**
	 * 工具组插件
	 */
	public static final String GROUP_TOOLS = "toolsgroup";
	
	/**
	 * 没有分组，可能不需要显示在ui上的插件
	 */
	public static final String GROUP_EMPTY = "emptygroup";
	
	//end
	
	/**
	 * 插件名称
	 */
	public String getTitle();
	
	/**
	 * 获取插件所在分组
	 */
	public String getGroup();
	
}