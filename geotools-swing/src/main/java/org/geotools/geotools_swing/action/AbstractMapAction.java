package org.geotools.geotools_swing.action;

import javax.swing.AbstractAction;

import org.geotools.geotools_swing.MapPane;

/**
 * 所有的工具控件的基类
 * @author sam
 *
 */
public abstract class AbstractMapAction extends AbstractAction implements MapAction {
	
	private static final long serialVersionUID = -3214956077693326058L;
	
	/**
	 * 画布对象
	 */
	protected MapPane mapPane;
	
	/**
	 * 所有的工具控件的基类
	 */
	public AbstractMapAction(MapPane mapPane){
		this.setMapPane(mapPane);
	}
	
	/**
	 * 画布对象
	 */
	@Override
	public MapPane getMapPane() {
		return this.mapPane;
	}

	/**
	 * 设置画布对象
	 */
	@Override
	public void setMapPane(MapPane mapPane) {
		this.mapPane = mapPane;
	}

}
