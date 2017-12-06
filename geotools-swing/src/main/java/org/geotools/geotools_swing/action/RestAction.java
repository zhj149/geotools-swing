package org.geotools.geotools_swing.action;

import org.geotools.geotools_swing.MapPane;

/**
 * 地图初始化的动作
 * 
 * @author sam
 *
 */
public class RestAction extends AbstractMapAction {

	private static final long serialVersionUID = -1310517891302678241L;

	/**
	 * 地图初始化的动作
	 * 
	 * @param mapPane
	 */
	public RestAction(MapPane mapPane) {
		super(mapPane);
	}
	
	/**
	 * 不带地图上下文的构造函数
	 */
	public RestAction() {
	}

	@Override
	public void actionPerformed(java.awt.event.ActionEvent e) {
		if (this.getMapPane() != null)
			this.getMapPane().reset();
	}

}
