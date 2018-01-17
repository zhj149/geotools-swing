package org.geotools.geotools_swing.controll;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToolBar;

import org.geotools.geotools_swing.MapListener;
import org.geotools.geotools_swing.MapPane;
import org.geotools.geotools_swing.action.OpenShpLayerAction;
import org.geotools.geotools_swing.action.PanAction;
import org.geotools.geotools_swing.action.RestAction;
import org.geotools.geotools_swing.action.ZoomInAction;
import org.geotools.geotools_swing.action.ZoomOutAction;

/**
 * 地图工具栏
 * 
 * @author sam
 *
 */
public class JMapToolBar extends JToolBar implements MapListener {

	private static final long serialVersionUID = 366621552595015884L;

	/**
	 * 地图工具栏
	 * 
	 * @param mapPane
	 */
	public JMapToolBar(MapPane mapPane) {
		super();
		this.setMapPane(mapPane);
		this.initCompents();
	}

	/**
	 * 地图画布对象
	 */
	private MapPane canvas;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MapPane getMapPane() {
		return this.canvas;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setMapPane(MapPane mapPane) {
		this.canvas = mapPane;
	}

	/**
	 * 拖动操作
	 */
	public JButton panAction;

	/**
	 * 恢复地图初始状态
	 */
	public JButton resetAction;

	/**
	 * 放大操作
	 */
	public JButton zoominAction;

	/**
	 * 缩小操作
	 */
	public JButton zoomoutAction;
	
	/**
	 * shape文件操作对象
	 */
	public JButton shpAction;

	/**
	 * 初始化控件
	 */
	private void initCompents() {
		RestAction restHandler = new RestAction(this.canvas);
		ZoomInAction zoomInHandler = new ZoomInAction(this.canvas);
		ZoomOutAction zoomOutHandler = new ZoomOutAction(this.canvas);
		PanAction paneHandler = new PanAction(this.canvas);
		OpenShpLayerAction shpHandler = new OpenShpLayerAction(this.canvas);

		// reset按钮
		resetAction = new JButton("", new ImageIcon(this.getClass().getResource("/mActionZoomFullExtent.png")));
		resetAction.addActionListener(restHandler);
		this.add(resetAction);

		// 放大按钮
		zoominAction = new JButton("", new ImageIcon(this.getClass().getResource("/mActionZoomIn.png")));
		zoominAction.addActionListener(zoomInHandler);
		this.add(zoominAction);

		// 放大按钮
		zoomoutAction = new JButton("", new ImageIcon(this.getClass().getResource("/mActionZoomOut.png")));
		zoomoutAction.addActionListener(zoomOutHandler);
		this.add(zoomoutAction);

		// 拖动按钮
		panAction = new JButton("", new ImageIcon(this.getClass().getResource("/pan_mode.gif")));
		panAction.addActionListener(paneHandler);
		this.add(panAction);

		// 加入华丽的分割线
		this.addSeparator();
	
		//以下是和数据操作相关的功能点
		shpAction = new JButton("" , new ImageIcon(this.getClass().getResource("/mOpenLayer.png")));
		shpAction.addActionListener(shpHandler);
		this.add(shpAction);
	}
}
