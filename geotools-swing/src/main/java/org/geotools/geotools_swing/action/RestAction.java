package org.geotools.geotools_swing.action;

import javax.swing.ImageIcon;

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
		this.putValue(SMALL_ICON, new ImageIcon(this.getClass().getResource("/mActionZoomFullExtent.png")));
		this.putValue(NAME, "");
		this.putValue(SHORT_DESCRIPTION, "重置");
	}
	
	@Override
	public void actionPerformed(java.awt.event.ActionEvent e) {
		if (this.getMapPane() != null)
			this.getMapPane().reset();
	}

}
