package org.geotools.geotools_swing.action;

import org.geotools.geotools_swing.MapPane;
import org.geotools.geotools_swing.tools.PanTool;

/**
 * 移动操作
 * 
 * @author sam
 *
 */
public class PanAction extends AbstractMapAction {

	private static final long serialVersionUID = -8580473973445330548L;

	/**
	 * 移动操作
	 * 
	 * @param mapPane
	 */
	public PanAction(MapPane mapPane) {
		super(mapPane);
	}

	@Override
	public void actionPerformed(java.awt.event.ActionEvent e) {
		if (this.getMapPane() != null) {
			this.getMapPane().setCursorTool(new PanTool());
		}
	}

}
