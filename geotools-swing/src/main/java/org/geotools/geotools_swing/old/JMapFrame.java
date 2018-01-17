package org.geotools.geotools_swing.old;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuBar;

import org.geotools.geotools_swing.JMapCanvas;
import org.geotools.geotools_swing.action.OpenShpLayerAction;
import org.geotools.geotools_swing.action.PanAction;
import org.geotools.geotools_swing.action.RestAction;
import org.geotools.geotools_swing.action.ZoomInAction;
import org.geotools.geotools_swing.action.ZoomOutAction;
import org.geotools.map.MapContent;


/**
 * javafx实现的地图窗口
 * 
 * 
 * @author sam
 *
 */
public class JMapFrame extends JFrame {

	private static final long serialVersionUID = 3792114102061157548L;
	
	
	public JMapFrame(){
		initCompent();
	}

	/**
	 * 窗口的启动
	 */
	private void initCompent() {

		this.setLayout(new BorderLayout());
		
		MapContent mapContent = new MapContent();
		mapContent.setTitle("Quickstart");

		JMapCanvas map = new JMapCanvas(mapContent);
		map.setPreferredSize(new Dimension(1024, 768));
		JMenuBar toolBar = new JMenuBar();

		// 执行的动作
		RestAction restAction = new RestAction(map);
		ZoomInAction zoomInAction = new ZoomInAction(map);
		ZoomOutAction zoomOutAction = new ZoomOutAction(map);
		PanAction paneAction = new PanAction(map);
		OpenShpLayerAction shpAction = new OpenShpLayerAction(map);
		

		// reset按钮
		JButton btnRest = new JButton("", new ImageIcon( this.getClass().getResource("/mActionZoomFullExtent.png")));
		btnRest.setToolTipText("重置");
		btnRest.setAction(restAction);
		toolBar.add(btnRest);

		// 放大按钮
		JButton btnZoomIn = new JButton("", new ImageIcon( this.getClass().getResource("/mActionZoomIn.png")));
		btnZoomIn.setToolTipText("放大");
		btnZoomIn.setAction(zoomInAction);
		toolBar.add(btnZoomIn);

		// 缩小按钮
		JButton btnZoomOut = new JButton("", new ImageIcon( this.getClass().getResource("/mActionZoomOut.png")));
		btnZoomOut.setToolTipText("缩小");
		btnZoomOut.setAction(zoomOutAction);
		toolBar.add(btnZoomOut);

		// 拖动按钮
		JButton btnPane = new JButton("", new ImageIcon( this.getClass().getResource("/mActionPan.png")));
		btnPane.setToolTipText("拖动");
		btnPane.setAction(paneAction);
		toolBar.add(btnPane);

		// 拖动按钮
		JButton shpPane = new JButton("", new ImageIcon( this.getClass().getResource("/open.gif")));
		shpPane.setToolTipText("导入图层");
		shpPane.setAction(shpAction);
		toolBar.add(shpPane);
		
		this.setJMenuBar(toolBar);
		this.add(map, BorderLayout.CENTER);
		
		map.repaint(true);

	}

	/**
	 * 程序的入口点
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		JMapFrame frame = new JMapFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1280 , 800);
		frame.setVisible(true);
	}

}
