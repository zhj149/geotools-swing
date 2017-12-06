package org.geotools.geotools_swing;

/**
 * 设置画布的操作
 * 符合接口隔离原则
 * @author sam
 *
 */
public interface MapListener {

	/**
	 * 获取当前的画布
	 * @return
	 */
	public MapPane getMapPane();
	
	/**
	 *  设置当前的画布
	 * @param mapPane
	 */
	public void setMapPane(MapPane mapPane);
}
