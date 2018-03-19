package org.geotools.geotools_swing.action;

import javax.swing.ImageIcon;

import org.geotools.geotools_swing.MapPane;
import org.geotools.geotools_swing.tools.ZoomOutTool;

/**
 * 缩小操作
 * 
 * @author sam
 *
 */
public class ZoomOutAction extends AbstractMapAction {

	private static final long serialVersionUID = 2667458318140535983L;

	/**
	 * 缩小操作
	 * 
	 * @param mapPane
	 */
	public ZoomOutAction(MapPane mapPane) {
		super(mapPane);
		this.putValue(SMALL_ICON, new ImageIcon(this.getClass().getResource("/mActionZoomOut.png")));
		this.putValue(NAME, "");
		this.putValue(SHORT_DESCRIPTION, "缩小");
	}

	@Override
	public void actionPerformed(java.awt.event.ActionEvent e) {
		if (this.getMapPane() != null) {
			this.getMapPane().setCursorTool(new ZoomOutTool());
		}
	}


}
