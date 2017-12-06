package org.geotools.geotools_swing.action;

import org.geotools.geotools_swing.MapPane;
import org.geotools.geotools_swing.tools.ZoomInTool;

/**
 * 放大操作
 * 
 * @author sam
 *
 */
public class ZoomInAction extends AbstractMapAction {

	private static final long serialVersionUID = -5480011168098849229L;

	/**
	 * 放大操作
	 */
	public ZoomInAction() {

	}

	/**
	 * 放大操作
	 * 
	 * @param mapPane
	 */
	public ZoomInAction(MapPane mapPane) {
		this.setMapPane(mapPane);
	}

	@Override
	public void actionPerformed(java.awt.event.ActionEvent e) {
		if (this.getMapPane() != null) {
			this.getMapPane().setCursorTool(new ZoomInTool());
		}
	}

}
