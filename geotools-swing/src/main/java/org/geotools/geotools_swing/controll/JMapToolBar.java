package org.geotools.geotools_swing.controll;

import javax.swing.JButton;
import javax.swing.JToolBar;

import org.geotools.geotools_swing.MapListener;
import org.geotools.geotools_swing.MapPane;
import org.geotools.geotools_swing.action.DistanceAction;
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
	private JButton btnPan;

	/**
	 * 恢复地图初始状态
	 */
	private JButton btnReset;

	/**
	 * 放大操作
	 */
	private JButton btnZoomIn;

	/**
	 * 缩小操作
	 */
	private JButton btnZoomOut;
	
	/**
	 * shape文件操作对象
	 */
	private JButton btnShp;
	
	/**
	 * 测距工具
	 */
	private JButton btnDistance;

	/**
	 * 初始化控件
	 */
	private void initCompents() {

		// reset按钮
		btnReset = new JButton(new RestAction(this.canvas));
		add(btnReset);

		// 放大按钮
		btnZoomIn = new JButton(new ZoomInAction(this.canvas));
		add(btnZoomIn);

		// 放大按钮
		btnZoomOut = new JButton(new ZoomOutAction(this.canvas));
		this.add(btnZoomOut);

		// 拖动按钮
		btnPan = new JButton(new PanAction(this.canvas));
		this.add(btnPan);

		// 加入华丽的分割线
		this.addSeparator();
		
		//测距工具
		btnDistance = new JButton(new DistanceAction(canvas));
		this.add(btnDistance);
		
		// 加入华丽的分割线
		this.addSeparator();
	
		//以下是和数据操作相关的功能点
		btnShp = new JButton(new OpenShpLayerAction(this.canvas));
		this.add(btnShp);
	}
}
