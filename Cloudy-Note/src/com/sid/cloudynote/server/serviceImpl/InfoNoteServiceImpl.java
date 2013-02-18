package com.sid.cloudynote.server.serviceImpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.sid.cloudynote.client.service.InfoNoteService;
import com.sid.cloudynote.server.GSQLUtil;
import com.sid.cloudynote.server.PMF;
import com.sid.cloudynote.shared.InfoNote;
import com.sid.cloudynote.shared.NotLoggedInException;
import com.sid.cloudynote.shared.NoteProperty;
import com.sid.cloudynote.shared.Notebook;

public class InfoNoteServiceImpl extends RemoteServiceServlet implements
		InfoNoteService {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1560433333437871362L;

	/**
	 * 添加实体
	 * 
	 * @throws NotLoggedInException
	 */
	@Override
	public void add(InfoNote note) throws NotLoggedInException {
		checkLoggedIn();
		PersistenceManager pm = PMF.getInstance().getPersistenceManager();
		try {
			pm.currentTransaction().begin();
			NoteProperty property = new NoteProperty(new Date(), new Date());
			note.setProperty(property);
			note.setUser(getUser());
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
	 * 
	 * @throws NotLoggedInException
	 */
	@Override
	public void delete(InfoNote entity) throws NotLoggedInException {
		checkLoggedIn();
		PersistenceManager pm = PMF.getInstance().getPersistenceManager();
		try {
			// check the owner of the note is the current user
			if (!entity.getUser().equals(getUser())) {
				GWT.log("You don't have the access to delete since you're not the ower of the note");
			} else {
				pm.deletePersistent(entity);
			}
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
	public void modify(InfoNote note) throws NotLoggedInException {
		checkLoggedIn();
		PersistenceManager pm = PMF.getInstance().getPersistenceManager();
		try {
			pm.currentTransaction().begin();
			if (!note.getUser().equals(getUser())) {
				GWT.log("You don't have the access to delete since you're not the ower of the note");
			} else {
				pm.makePersistent(note);
			}
			pm.currentTransaction().commit();
		} catch (Exception e) {
		} finally {
			if (pm.currentTransaction().isActive())
				pm.currentTransaction().rollback();
			pm.close();
		}
	}

	@Override
	public void moveNoteTo(InfoNote note, Notebook notebook)
			throws NotLoggedInException {
		checkLoggedIn();
		PersistenceManager pm = PMF.getInstance().getPersistenceManager();
		String title = note.getTitle();
		String content = note.getContent();
		NoteProperty property = new NoteProperty(note.getProperty()
				.getCreatedTime(), new Date());
		try {
			pm.currentTransaction().begin();
			if (!note.getUser().equals(getUser())) {
				GWT.log("You don't have the access to delete since you're not the ower of the note");
			} else {
				InfoNote entity = new InfoNote(notebook, title, content);
				entity.setProperty(property);
				entity.setUser(getUser());
				pm.deletePersistent(note);
				pm.makePersistent(entity);
			}
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
	 * @throws NotLoggedInException
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<InfoNote> getPaginationData(String filter, String ordering,
			long firstResult, long maxResult) throws NotLoggedInException {
		checkLoggedIn();
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
	 * @throws NotLoggedInException
	 */
	@Override
	public List<InfoNote> getPaginationData(String filter, String ordering)
			throws NotLoggedInException {
		checkLoggedIn();
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
	 * @throws NotLoggedInException
	 */
	@Override
	public List<InfoNote> getPaginationData(String filter, long firstResult,
			long maxResult) throws NotLoggedInException {
		checkLoggedIn();
		return getPaginationData(filter, null, firstResult, maxResult);
	}

	/**
	 * 
	 * @param firstResult
	 *            开始记录
	 * @param maxResult
	 *            检索结果的最大数量
	 * @return 查询处理好的数据
	 * @throws NotLoggedInException
	 */
	@Override
	public List<InfoNote> getPaginationData(long firstResult, long maxResult)
			throws NotLoggedInException {
		checkLoggedIn();
		return getPaginationData(null, null, firstResult, maxResult);
	}

	/**
	 * @param filter
	 *            查询过滤条件
	 * @return 查询处理好的数据
	 * @throws NotLoggedInException
	 */
	@Override
	public List<InfoNote> getPaginationData(String filter)
			throws NotLoggedInException {
		checkLoggedIn();
		return getPaginationData(filter, null);
	}

	/**
	 * @return 查询处理好的数据
	 * @throws NotLoggedInException
	 */
	@Override
	public List<InfoNote> getPaginationData() throws NotLoggedInException {
		checkLoggedIn();
		return getPaginationData(null);
	}

	@SuppressWarnings("unchecked")
	public List<InfoNote> getNotes(Notebook notebook)
			throws NotLoggedInException {
		checkLoggedIn();
		List<InfoNote> result = new ArrayList<InfoNote>();
		PersistenceManager pm = PMF.getInstance().getPersistenceManager();
		try {
			Query q = pm.newQuery(InfoNote.class);
			q.setFilter("user == userParam && notebook == notebookParam");
			q.declareParameters(User.class.getName() + " userParam,"
					+ Key.class.getName() + " notebookParam");

			Object obj = q.execute(getUser(), notebook.getKey());
			if (obj != null) {
				result = (List<InfoNote>) obj;
				result = new ArrayList<InfoNote>(pm.detachCopyAll(result));
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
