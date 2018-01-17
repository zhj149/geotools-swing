package org.geotools.geotools_swing;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JSplitPane;

import org.geotools.geotools_swing.controll.JMapLayerTable;
import org.geotools.geotools_swing.controll.JMapToolBar;
import org.geotools.map.MapContent;

/**
 * 新的地图窗口程序
 * @author sam
 *
 */
public class JMapFrame extends JFrame {

	private static final long serialVersionUID = 682799622168380767L;

	/**
	 * 新的地图窗口程序
	 */
	public JMapFrame(){
		super();
		this.setBounds(100, 100, 1024, 768);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setExtendedState(JFrame.MAXIMIZED_BOTH); //默认窗口最大化
		this.initCompents();
	}
	
	/**
	 * 地图工具条
	 */
	private JMapToolBar toolBar;
	
	/**
	 * 图层工具
	 */
	private JMapLayerTable layerTable;
	
	/**
	 * 地图对象
	 */
	private JMapCanvas canvas;
	
	/**
	 * 初始化控件显示
	 */
	protected void initCompents(){
		
		this.setLayout(new BorderLayout());
		canvas = new JMapCanvas(new MapContent());
		layerTable = new JMapLayerTable(canvas);
		toolBar = new JMapToolBar(canvas);
		
		this.add(toolBar , BorderLayout.NORTH);
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, layerTable, canvas);
		this.add(splitPane, BorderLayout.CENTER);
	}
}
