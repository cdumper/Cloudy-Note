package com.sid.cloudynote.client.model;

import java.util.List;

/**
 * 数据处理的基础接口
 * @since 2011/09/01 1.0
 * @version 1.0
 * @author kyle
 *
 * @param <T> 实体类 entity class
 */
public interface IDAO<T> {

	/**
	 * 增加实体
	 * @param entity
	 */
	public void add(T entity);
	
	/**
	 * 更新实体
	 * @param entity
	 */
	public void modify(T entity);
		
	/**
	 * 删除实体
	 * @param entityClass 实体类
	 * @param entityids 实体id数组
	 */	
	public void delete(T entity);
	
	/**
	 * @param filter 查询过滤条件
	 * @param ordering 查询后排序条件
	 * @param firstResult 开始记录
	 * @param maxResult 检索结果的最大数量
	 * @return 查询处理好的数据
	 * @author kyle
	 */
	public List<T> getPaginationData(String filter, String ordering,
			long firstResult ,long maxResult);
	
	/**
	 * @param filter 查询过滤条件
	 * @param odering 查询后排序条件
	 * @return  查询处理好的数据
	 * @author kyle
	 */
	public List<T> getPaginationData(String filter, String ordering);
	
	/**
	 * 
	 * @param filter 查询过滤条件
	 * @param firstResult 开始记录
	 * @param maxResult 检索结果的最大数量
	 * @return 查询处理好的数据
	 * @author kyle
	 */
	public List<T> getPaginationData(String filter,long firstResult ,
			long maxResult);
	
	/**
	 * 
	 * @param firstResult 开始记录
	 * @param maxResult 检索结果的最大数量
	 * @return 查询处理好的数据
	 * @author kyle
	 */
	public List<T> getPaginationData(long firstResult ,long maxResult);
	
	/**
	 * @param filter 查询过滤条件
	 * @return 查询处理好的数据
	 * @author kyle
	 */
	public List<T> getPaginationData(String filter);
	
	/**
	 * @return 查询处理好的数据
	 * @author kyle
	 */
	public List<T> getPaginationData();
}