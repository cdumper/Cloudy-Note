package com.sid.cloudynote.server.serviceImpl;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.google.appengine.api.datastore.Key;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.sid.cloudynote.client.service.InfoNoteService;
import com.sid.cloudynote.server.GSQLUtil;
import com.sid.cloudynote.server.PMF;
import com.sid.cloudynote.shared.InfoNote;
import com.sid.cloudynote.shared.Notebook;

public class InfoNoteServiceImpl extends RemoteServiceServlet implements
		InfoNoteService {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1560433333437871362L;

	/**
	 * 添加实体
	 */
	@Override
	public void add(InfoNote note) {
		PersistenceManager pm = PMF.getInstance().getPersistenceManager();
		try {
			pm.currentTransaction().begin();
			// Notebook notebook = pm.getObjectById(Notebook.class,
			// note.getNotebook().getKey());
			// Notebook notebook = note.getNotebook();
			// System.out.println("trying to add note into notebook:"+notebook.getKey());
			// notebook.getNotes().add(note);
			// InfoNote entity = pm.getObjectById(InfoNote.class,
			// note.getKey());
			pm.makePersistent(note);
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
	 */
	@Override
	public void delete(InfoNote entity) {
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
	public void modify(InfoNote note) {
		PersistenceManager pm = PMF.getInstance().getPersistenceManager();
		String title = note.getTitle();
		Notebook notebook = note.getNotebook();
		String content = note.getContent();
		try {
			// 更新数据，直接调用makePersistent()方法的，要求实体类注解了如下
			// @PersistenceCapable(detachable="true")
			pm.currentTransaction().begin();
			InfoNote entity = pm.getObjectById(InfoNote.class, note.getKey());
			entity.setTitle(title);
			entity.setContent(content);
			entity.setNotebook(notebook);
			pm.makePersistent(note);
			pm.currentTransaction().commit();
		} catch (Exception e) {
		} finally {
			if (pm.currentTransaction().isActive())
				pm.currentTransaction().rollback();
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
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<InfoNote> getPaginationData(String filter, String ordering,
			long firstResult, long maxResult) {
		List<InfoNote> result = null;
		PersistenceManager pm = PMF.getInstance().getPersistenceManager();
		try {
			pm.currentTransaction().begin();
			Query query = GSQLUtil.getSelectSqlStr(pm, InfoNote.class, filter,
					ordering, firstResult, maxResult);
			Object obj = query.execute("root");
			if (obj != null) {
				result = (List<InfoNote>) obj;
				result = new ArrayList<InfoNote>(pm.detachCopyAll(result));
				result.size();
			} else {
				result = new ArrayList<InfoNote>();
			}
			// pm.currentTransaction().begin();
			// Query query = pm.newQuery(InfoNote.class);
			// result = (List<InfoNote>) query.execute("root");
			// result = new ArrayList<InfoNote>(pm.detachCopyAll(result));
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
	 */
	@Override
	public List<InfoNote> getPaginationData(String filter, String ordering) {
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
	 */
	@Override
	public List<InfoNote> getPaginationData(String filter, long firstResult,
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
	 */
	@Override
	public List<InfoNote> getPaginationData(long firstResult, long maxResult) {
		return getPaginationData(null, null, firstResult, maxResult);
	}

	/**
	 * @param filter
	 *            查询过滤条件
	 * @return 查询处理好的数据
	 */
	@Override
	public List<InfoNote> getPaginationData(String filter) {
		return getPaginationData(filter, null);
	}

	/**
	 * @return 查询处理好的数据
	 */
	@Override
	public List<InfoNote> getPaginationData() {
		return getPaginationData(null);
	}

	public List<InfoNote> getNotes(Notebook notebook) {
		List<InfoNote> result = null;
		PersistenceManager pm = PMF.getInstance().getPersistenceManager();
		try {
			// pm.currentTransaction().begin();
			Query q = pm.newQuery(InfoNote.class);
			q.setFilter("notebook == notebookParam");
			q.declareParameters(Key.class.getName() + " notebookParam");

			Object obj = q.execute(notebook.getKey());
			if (obj != null) {
				result = (List<InfoNote>) obj;
				result = new ArrayList<InfoNote>(pm.detachCopyAll(result));
				result.size();
			} else {
				result = new ArrayList<InfoNote>();
			}
		} catch (Exception e) {
		} finally {
			pm.close();
		}
		return result;
	}
}
