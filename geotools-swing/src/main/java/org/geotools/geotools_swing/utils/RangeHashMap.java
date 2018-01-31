package org.geotools.geotools_swing.utils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * 范围hashmap 在区间范围上取值的hashmap
 * 
 * @author sam
 *
 * @param <K>
 * @param <V>
 */
public class RangeHashMap<K, V> extends HashMap<K, V> {

	private static final long serialVersionUID = -1615435488644017718L;

	/**
	 * 初始化表示的范围
	 */
	private List<K> range = new LinkedList<>();

	/**
	 * 获取当前的表示范围 不允许修改，所以转换为array给用户
	 * 
	 * @return
	 */
	public Object[] getRange() {
		return range.toArray();
	}

	/**
	 * 无参数的构造函数
	 */
	public RangeHashMap() {
		super();
	}

	/**
	 * 直接初始化的方法
	 * 
	 * @param ks
	 *            必须是排好序的key，而且不能重复
	 * @param vs
	 *            排好序对应的value
	 */
	public RangeHashMap(K[] ks, V[] vs) throws Exception {
		super();

		if (ks == null || vs == null)
			throw new IllegalAccessException("keys and values not can be null");

		if (vs.length != vs.length)
			throw new IllegalAccessException("keys and values size must be equals");

		for (int i = 0; i < ks.length; i++) {
			range.add(ks[i]);
			this.put(ks[i], vs[i]);
		}
	}

	/**
	 * 重写的获取数据的方法
	 */
	@SuppressWarnings("unchecked")
	@Override
	public V get(Object key) {
		if (this.containsKey(key))
			return super.get(key);

		for (int i = 0; i < this.range.size() - 1; i++) {
			K k = range.get(i);
			if (k instanceof Comparable) {
				K k1 = range.get(i + 1);
				int i1 = ((Comparable<Object>) k).compareTo(key);
				int i2 = ((Comparable<Object>) k1).compareTo(key);
				if ((i1 + i2) == 0)
					return super.get(k);
			}
		}

		return super.get(range.get(this.range.size() - 1));
	}

	/**
	 * 加入新数据
	 */
	@Override
	public V put(K key, V value) {
		this.range.add(key);
		return super.put(key, value);
	}
}
