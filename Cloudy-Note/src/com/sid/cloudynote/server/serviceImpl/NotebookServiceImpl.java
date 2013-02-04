package com.sid.cloudynote.server.serviceImpl;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.sid.cloudynote.client.service.NotebookService;
import com.sid.cloudynote.server.GSQLUtil;
import com.sid.cloudynote.server.PMF;
import com.sid.cloudynote.shared.NotLoggedInException;
import com.sid.cloudynote.shared.Notebook;

public class NotebookServiceImpl extends RemoteServiceServlet implements
		NotebookService {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3191322844970681081L;

	/**
	 * 添加实体
	 * 
	 * @throws NotLoggedInException
	 */
	@Override
	public void add(Notebook entity) throws NotLoggedInException {
		checkLoggedIn();
		entity.setUser(getUser());
		PersistenceManager pm = PMF.getInstance().getPersistenceManager();
		try {
			pm.currentTransaction().begin();
			pm.makePersistent(entity);
			pm.currentTransaction().commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (pm.currentTransaction().isActive()) {
				pm.currentTransaction().rollback();
			}
			pm.close();
		}
	}

	/**
	 * 删除实体
	 * 
	 * @throws NotLoggedInException
	 */
	@Override
	public void delete(Notebook entity) throws NotLoggedInException {
		checkLoggedIn();
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
	 * 
	 * @throws NotLoggedInException
	 */
	@Override
	public void modify(Notebook entity) throws NotLoggedInException {
		checkLoggedIn();
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
	 * @throws NotLoggedInException
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<Notebook> getPaginationData(String filter, String ordering,
			long firstResult, long maxResult) throws NotLoggedInException {
		checkLoggedIn();
		List<Notebook> result = new ArrayList<Notebook>();
		PersistenceManager pm = PMF.getInstance().getPersistenceManager();
		try {
			pm.currentTransaction().begin();
			Query query = GSQLUtil.getSelectSqlStr(pm, Notebook.class, filter,
					ordering, firstResult, maxResult);
			Object obj = query.execute();
			if (obj != null) {
				result = (List<Notebook>) obj;
				result = new ArrayList<Notebook>(pm.detachCopyAll(result));
				result.size();
			} else {
				result = new ArrayList<Notebook>();
			}

		} catch (Exception e) {
		} finally {
			if (pm.currentTransaction().isActive()) {
				pm.currentTransaction().rollback();
			}
			pm.close();
		}
		return result;
	}

	/**
	 * @param filter
	 *            查询过滤条件
	 * @param odering
	 *            查询后排序条件
	 * @return 查询处理好的数据
	 * @author kyle
	 * @throws NotLoggedInException
	 */
	@Override
	public List<Notebook> getPaginationData(String filter, String ordering)
			throws NotLoggedInException {
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
	 * @throws NotLoggedInException
	 */
	@Override
	public List<Notebook> getPaginationData(String filter, long firstResult,
			long maxResult) throws NotLoggedInException {
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
	 * @throws NotLoggedInException
	 */
	@Override
	public List<Notebook> getPaginationData(long firstResult, long maxResult)
			throws NotLoggedInException {
		return getPaginationData(null, null, firstResult, maxResult);
	}

	/**
	 * @param filter
	 *            查询过滤条件
	 * @return 查询处理好的数据
	 * @author kyle
	 * @throws NotLoggedInException
	 */
	@Override
	public List<Notebook> getPaginationData(String filter)
			throws NotLoggedInException {
		return getPaginationData(filter, null);
	}

	/**
	 * @return 查询处理好的数据
	 * @author kyle
	 * @throws NotLoggedInException
	 */
	@Override
	public List<Notebook> getPaginationData() throws NotLoggedInException {
		return getPaginationData(null);
	}

	@Override
	public List<Notebook> getNotebooks() throws NotLoggedInException {
		checkLoggedIn();
		List<Notebook> result = new ArrayList<Notebook>();
		PersistenceManager pm = PMF.getInstance().getPersistenceManager();
		try {
			Query q = pm.newQuery(Notebook.class);
			q.setFilter("user == userParam");
			q.declareParameters(User.class.getName() + " userParam");

			Object obj = q.execute(getUser());
			if (obj != null) {
				result = (List<Notebook>) obj;
				result = new ArrayList<Notebook>(pm.detachCopyAll(result));
				result.size();
			}
		} catch (Exception e) {
		} finally {
			pm.close();
		}
		return result;
	}

	private void checkLoggedIn() throws NotLoggedInException {
		if (getUser() == null) {
			throw new NotLoggedInException("Not logged in.");
		}
	}

	private User getUser() {
		UserService userService = UserServiceFactory.getUserService();
		return userService.getCurrentUser();
	}
}
