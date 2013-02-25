package com.sid.cloudynote.server;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;


/**
 * 用来处理的GSQL语句的简单封装类
 * @since 2011/09/01 1.0
 * @version 1.0
 * @author kyle
 *
 */
public class GSQLUtil {

	/**
	 * 
	 * @param pm PersistenceManager对象，用来产生Query对象
	 * @param c 需要查询的类
	 * @param filter 查询过滤条件
	 * @param ordering 查询后排序条件
	 * @param firstResult 开始记录
	 * @param maxResult 检索结果的最大数量
	 * @return 处理好的Query对象
	 */
	public static Query getSelectSqlStr(PersistenceManager pm,Class<?> c,String filter, 
			String ordering,long firstResult ,long maxResult) {
		Query query = pm.newQuery(c);
		//设置过滤条件
		if(filter!=null) {
			query.setFilter(filter);
		}
		//设置排序条件
		if(ordering!=null) {
			query.setOrdering(ordering);
		}
		//设置开始和最大值
		if(firstResult!=-1 && maxResult!=-1) {
			query.setRange(firstResult, maxResult);
		}
		return query;
	}
	
	/**
	 * 
	 * @param pm PersistenceManager对象，用来产生Query对象
	 * @param c 需要查询的类
	 * @param filter 查询过滤条件
	 * @param ordering 查询后排序条件
	 * @return 处理好的Query对象
	 */
	public static Query getSelectSqlStr(PersistenceManager pm,Class<?> c,String filter, 
			String ordering) {
		long min=-1;
		return getSelectSqlStr(pm,c,filter,ordering,min,min);
	}
	
	/**
	 * 
	 * @param pm PersistenceManager对象，用来产生Query对象
	 * @param c 需要查询的类
	 * @param filter 查询过滤条件
	 * @return 处理好的Query对象
	 */
	public static Query getSelectSqlStr(PersistenceManager pm,Class<?> c,String filter) {
		return getSelectSqlStr(pm,c,filter,null);
	}
	
	/**
	 * 
	 * @param pm PersistenceManager对象，用来产生Query对象
	 * @param c 需要查询的类
	 * @return 处理好的Query对象
	 */
	public static Query getSelectSqlStr(PersistenceManager pm,Class<?> c) {
		return getSelectSqlStr(pm,c,null);
	}
	
}