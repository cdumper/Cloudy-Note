package com.sid.cloudynote.server.serviceImpl;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.sid.cloudynote.client.model.Notebook;
import com.sid.cloudynote.client.service.NotebookService;
import com.sid.cloudynote.server.GSQLUtil;
import com.sid.cloudynote.server.PMF;

public class NotebookServiceImpl extends RemoteServiceServlet implements
		NotebookService {
	/**
	 * 添加实体
	 */
	@Override
	public void add(Notebook entity) {
		PersistenceManager pm = PMF.getInstance().getPersistenceManager();
		try {
			pm.makePersistent(entity);
		} catch (Exception e) {
		} finally {
			pm.close();
		}
	}

	/**
	 * 删除实体
	 */
	@Override
	public void delete(Notebook entity) {
		PersistenceManager pm = PMF.getInstance().getPersistenceManager();
		try {
			pm.deletePersistent(entity);
		} catch (Exception e) {
		} finally {
			pm.close();
		}
	}

	/**
	 * 更新实体
	 */
	@Override
	public void modify(Notebook entity) {
		PersistenceManager pm = PMF.getInstance().getPersistenceManager();
		try {
			// 更新数据，直接调用makePersistent()方法的，要求实体类注解了如下
			// @PersistenceCapable(detachable="true")
			pm.makePersistent(entity);
		} catch (Exception e) {
		} finally {
			pm.close();
		}
	}

	/**
	 * @param filter
	 *            查询过滤条件
	 * @param ordering
	 *            查询后排序条件
	 * @param firstResult
	 *            开始记录
	 * @param maxResult
	 *            检索结果的最大数量
	 * @return 查询处理好的数据
	 * @author kyle
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<Notebook> getPaginationData(String filter, String ordering,
			long firstResult, long maxResult) {
		PersistenceManager pm = PMF.getInstance().getPersistenceManager();
		List<Notebook> list = new ArrayList<Notebook>();
		try {
			Query query = GSQLUtil.getSelectSqlStr(pm, Notebook.class, filter,
					ordering, firstResult, maxResult);
			Object obj = query.execute();
			if (obj != null) {
				list = (List<Notebook>) obj;
				// 不调用list.size()方法，那么调用pm.close()后，再次使用list会出现Object Manager
				// has been closed Exception
				list.size();
			} else {
				list = new ArrayList<Notebook>();
			}

		} catch (Exception e) {
		} finally {
			pm.close();
		}
		return list;
	}

	/**
	 * @param filter
	 *            查询过滤条件
	 * @param odering
	 *            查询后排序条件
	 * @return 查询处理好的数据
	 * @author kyle
	 */
	@Override
	public List<Notebook> getPaginationData(String filter, String ordering) {
		long min = -1;
		return getPaginationData(filter, ordering, min, min);
	}

	/**
	 * 
	 * @param filter
	 *            查询过滤条件
	 * @param firstResult
	 *            开始记录
	 * @param maxResult
	 *            检索结果的最大数量
	 * @return 查询处理好的数据
	 * @author kyle
	 */
	@Override
	public List<Notebook> getPaginationData(String filter, long firstResult,
			long maxResult) {
		return getPaginationData(filter, null, firstResult, maxResult);
	}

	/**
	 * 
	 * @param firstResult
	 *            开始记录
	 * @param maxResult
	 *            检索结果的最大数量
	 * @return 查询处理好的数据
	 * @author kyle
	 */
	@Override
	public List<Notebook> getPaginationData(long firstResult, long maxResult) {
		return getPaginationData(null, null, firstResult, maxResult);
	}

	/**
	 * @param filter
	 *            查询过滤条件
	 * @return 查询处理好的数据
	 * @author kyle
	 */
	@Override
	public List<Notebook> getPaginationData(String filter) {
		return getPaginationData(filter, null);
	}

	/**
	 * @return 查询处理好的数据
	 * @author kyle
	 */
	@Override
	public List<Notebook> getPaginationData() {
		return getPaginationData(null);
	}
}
