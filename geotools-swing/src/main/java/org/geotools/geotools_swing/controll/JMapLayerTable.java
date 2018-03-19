package org.geotools.geotools_swing.controll;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collection;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableCellRenderer;

import org.geotools.geotools_swing.MapListener;
import org.geotools.geotools_swing.MapPane;
import org.geotools.geotools_swing.event.MapPaneEvent;
import org.geotools.geotools_swing.event.MapPaneListener;
import org.geotools.geotools_swing.old.JSimpleStyleDialog;
import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;
import org.geotools.map.event.MapLayerEvent;
import org.geotools.map.event.MapLayerListEvent;
import org.geotools.map.event.MapLayerListListener;
import org.geotools.map.event.MapLayerListener;
import org.geotools.styling.Style;
import org.jdesktop.swingx.JXTable;
import org.sam.swing.resource.ResourceLoader;
import org.sam.swing.table.JSTableBuilder;
import org.sam.swing.table.JSTableColumn;
import org.sam.swing.table.JSTableColumnModel;
import org.sam.swing.table.JSTableModel;
import org.sam.swing.table.JSTableModelEvent;
import org.sam.swing.table.defaultImpl.JSTableDefaultBuilderImpl;
import org.sam.swing.table.defaultImpl.JSTableModelDefaultAdapter;
import org.sam.swing.table.editor.JSTableCheckboxEditor;
import org.sam.swing.table.editor.JSTableImageButtonEditor;
import org.sam.swing.table.header.JSTableHeader;
import org.sam.swing.table.header.JSTableHeaderCheckboxRenderer;
import org.sam.swing.table.renderer.JSTableCheckboxRenderer;
import org.sam.swing.table.renderer.JSTableImageButtonRenderer;

/**
 * 图层树控件
 * 
 * @author sam
 *
 */
public class JMapLayerTable extends JPanel implements MapListener, MapPaneListener, MapLayerListListener {

	private static final long serialVersionUID = -2455449236847315868L;

	/**
	 * 当前的表格控件
	 */
	private JXTable table;

	/**
	 * 表格的model
	 */
	private JSTableModel<Collection<Layer>> tableModel;

	/**
	 * 表格列的model
	 */
	private JSTableColumnModel tableColumnModel;

	/**
	 * 当前的画布对象
	 */
	private MapPane mapPane;

	/**
	 * 图层操作
	 */
	private MapLayerListener mapLayerListener;

	/**
	 * 执行图层的操作事件接口
	 * 
	 * @return
	 */
	public MapLayerListener getMapLayerListener() {
		return mapLayerListener;
	}

	/**
	 * 执行图层的操作事件接口
	 * 
	 * @param mapLayerListener
	 */
	public void setMapLayerListener(MapLayerListener mapLayerListener) {
		this.mapLayerListener = mapLayerListener;
	}

	/**
	 * 初始化控件显示
	 * 
	 * @param mapPane
	 */
	public JMapLayerTable(MapPane mapPane) {
		this.setMapPane(mapPane);
		this.initCompenets();
	}

	/**
	 * 初始化控件显示
	 * 
	 * @throws Exception
	 */
	private void initCompenets() {
		this.setLayout(new BorderLayout());

		DefaultTableCellRenderer renderl = new DefaultTableCellRenderer();
		renderl.setHorizontalAlignment(DefaultTableCellRenderer.LEFT);

		JSTableColumn column0 = new JSTableColumn();
		column0.setModelIndex(0);
		column0.setHeaderValue("可见");
		column0.setTitle("可见");
		column0.setIdentifier("visible");
		column0.setMaxWidth(25);
		column0.setResizable(false);
		column0.setDefaultValue(true);
		JSTableCheckboxRenderer cbxRenderer = new JSTableCheckboxRenderer();
		column0.setCellRenderer(cbxRenderer);
		JCheckBox checkbox = new JCheckBox();
		checkbox.setOpaque(true);
		checkbox.setHorizontalAlignment(JCheckBox.CENTER);
		column0.setCellEditor(new JSTableCheckboxEditor(checkbox));
		// 表头改为checkbox风格
		JSTableHeaderCheckboxRenderer headerChx = new JSTableHeaderCheckboxRenderer();
		headerChx.setSelected(true);
		headerChx.addMouseListener(new MouseAdapter() {

			// 设置图层的可见性
			@Override
			public void mouseClicked(MouseEvent e) {
				int rowCount = tableModel.getRowCount();
				for (int i = 0; i < rowCount; i++) {
					tableModel.setValueAt(headerChx.isSelected(), i, column0.getModelIndex());
				}
			}
		});
		column0.setHeaderRenderer(headerChx);

		JSTableColumn column1 = new JSTableColumn();
		column1.setModelIndex(1);
		column1.setHeaderValue("名称");
		column1.setTitle("名称");
		column1.setIdentifier("title");
		column1.setWidth(80);
		column1.setEditable(false);
		column1.setCellRenderer(renderl);

		JSTableColumn column2 = new JSTableColumn();
		column2.setModelIndex(2);
		column2.setHeaderValue("编辑");
		column2.setTitle("编辑");
		column2.setIdentifier("selected");
		column2.setMaxWidth(50);
		column2.setResizable(false);
		column2.setDefaultValue(false);
		column2.setCellRenderer(cbxRenderer);
		JCheckBox editcheckbox = new JCheckBox();
		editcheckbox.setOpaque(true);
		editcheckbox.addActionListener(e -> {
			if (this.mapLayerListener != null) {
				try {
					Layer layer = (Layer) this.tableModel.getData(table.convertRowIndexToModel(table.getSelectedRow()));
					this.mapLayerListener.layerSelected(new MapLayerEvent(layer, MapLayerEvent.SELECTION_CHANGED));
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		editcheckbox.setHorizontalAlignment(JCheckBox.CENTER);
		column2.setCellEditor(new JSTableCheckboxEditor(editcheckbox));

		JSTableColumn column3 = new JSTableColumn();
		column3.setModelIndex(3);
		column3.setHeaderValue("样式");
		column3.setTitle("样式");
		column3.setIdentifier("style");
		column3.setMaxWidth(50);
		column3.setCellRenderer(new JSTableImageButtonRenderer(new ImageIcon(ResourceLoader.getResource("style.gif"))));
		JButton btnStyle = new JButton(new ImageIcon(ResourceLoader.getResource("style.gif")));
		JSTableImageButtonEditor imageButtonEditor = new JSTableImageButtonEditor(btnStyle);
		column3.setCellEditor(imageButtonEditor);
		// 给button添加按钮事件
		btnStyle.addActionListener(e -> {
			Object layer = null;
			try {
				layer = tableModel.getData(table.convertRowIndexToModel(table.getSelectedRow()));
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			FeatureLayer styleLayer = (FeatureLayer) layer;
			Style style = JSimpleStyleDialog.showDialog(this, styleLayer);
			if (style != null) {
				styleLayer.setStyle(style);
				tableModel.setValueAt(style, table.convertRowIndexToModel(table.getSelectedRow()), column3.getModelIndex());
			}
		});

		JSTableBuilder<Collection<Layer>> builder = new JSTableDefaultBuilderImpl<>(Layer.class, column0, column1,
				column2, column3);
		try {
			tableModel = builder.buildTableModel();
			tableColumnModel = builder.buildTableColumnModel();
			table = new JXTable(tableModel, tableColumnModel);
			tableModel.setTableModelLinster(new JSTableModelDefaultAdapter<Layer>() {

				
				@Override
				public void beforRetrieve(JSTableModelEvent event) throws Exception {
					tableModel.removeAll();
				}

				/**
				 * 检索数据
				 */
				@Override
				public Collection<Layer> onRetrieve() throws Exception {
					return getMapPane().getMapContent().layers();
				}

			});
			tableModel.setEditable(true);
			table.setTableHeader(new JSTableHeader(tableColumnModel));
			table.setSortable(false);
			table.setShowGrid(false);
			table.setRowHeight(25);
			tableModel.retrieve();

			this.add(new JScrollPane(table), BorderLayout.CENTER);

		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	// 以下是实现的接口

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MapPane getMapPane() {
		return this.mapPane;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setMapPane(MapPane mapPane) {
		this.mapPane = mapPane;
		if (this.mapPane != null) {
			this.mapPane.addMapPaneListener(this);
			this.mapPane.getMapContent().addMapLayerListListener(this);
		}
	}

	/**
	 * 清空图层
	 */
	public void clear() {
		try {
			this.tableModel.clear();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 更新一行数据
	 * 
	 * @param layer
	 *            图层
	 */
	public void updateEdit(Layer layer) {
		if (layer == null)
			return;

		try {
			int index = this.tableModel.findIndexOf(layer);
			if (index < 0)
				return;

			this.tableModel.setValueAt(layer.isSelected(), index, 2);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onNewContext(MapPaneEvent ev) {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onNewRenderer(MapPaneEvent ev) {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onResized(MapPaneEvent ev) {

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onDisplayAreaChanged(MapPaneEvent ev) {

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onRenderingStarted(MapPaneEvent ev) {

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onRenderingStopped(MapPaneEvent ev) {

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onRenderingProgress(MapPaneEvent ev) {

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void layerAdded(MapLayerListEvent event) {
		try {
			// 直接加到最后一行
			tableModel.insert(tableModel.getRowCount(), event.getElement());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void layerRemoved(MapLayerListEvent event) {
		try {
			tableModel.delete(event.getFromIndex());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void layerChanged(MapLayerListEvent event) {
		try{
			tableModel.retrieve();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void layerMoved(MapLayerListEvent event) {
		try{
			tableModel.moveRow(event.getFromIndex(), event.getToIndex());
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void layerPreDispose(MapLayerListEvent event) {
		try {
			tableModel.delete(event.getFromIndex());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
