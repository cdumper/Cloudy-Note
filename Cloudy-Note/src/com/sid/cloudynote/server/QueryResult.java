package com.sid.cloudynote.server;

import java.util.List;

/**
 *查询结果封装类 query result class
 * @since 2011/09/01 1.0
 * @version 1.0
 * @author kyle
 *
 * @param <T> 实体类 entity class
 */

public class QueryResult<T> {
	// 查询结果记录query result record
	private List<T> resultList;
	// 总记录数 total record  
	private long totalRecord;
	
	/**
	 * 获取所有满足条件的实体对象list
	 * @return list 所有满足条件的实体对象
	 * @since 2011/09/01 1.0
	 */
	public List<T> getResultList() {
		return resultList;
	}
	
	public void setResultList(List<T> resultList) {
		this.resultList = resultList;
	}
	
	/**
	 * 获取总共有好多条记录
	 * @return 满足条件的记录数
	 * @since 2011/09/01 1.0
	 */
	public long getTotalRecord() {
		return totalRecord;
	}
	
	public void setTotalRecord(long totalRecord) {
		this.totalRecord = totalRecord;
	}
}
