package com.sid.cloudynote.server.serviceImpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.sid.cloudynote.shared.Visibility;

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
			note.setCreatedTime(new Date());
			note.setLastModifiedTime(new Date());
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
				note.setLastModifiedTime(new Date());
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
		List<String> attachments = note.getAttachments();
		try {
			pm.currentTransaction().begin();
			if (!note.getUser().equals(getUser())) {
				GWT.log("You don't have the access to delete since you're not the ower of the note");
			} else {
				InfoNote entity = new InfoNote(notebook, title, content,
						attachments);
				entity.setCreatedTime(note.getCreatedTime());
				entity.setLastModifiedTime(new Date());
				entity.setUser(getUser());
				// entity.setProperty(property);
				// entity.setAttachments(attachments);
				pm.deletePersistent(note);
				// pm.deletePersistent(note.getProperty());
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
		return getPaginationData(filter, null);
	}

	/**
	 * @return 查询处理好的数据
	 * @throws NotLoggedInException
	 */
	@Override
	public List<InfoNote> getPaginationData() throws NotLoggedInException {
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

	@Override
	@SuppressWarnings("unchecked")
	public List<InfoNote> getPublicNotes() throws NotLoggedInException {
		List<InfoNote> result = new ArrayList<InfoNote>();
		PersistenceManager pm = PMF.getInstance().getPersistenceManager();

		Query q = pm.newQuery(InfoNote.class);
		q.setFilter("visibility == vParam");
		q.declareParameters(Integer.class.getName()+" vParam");
		try {
			Object obj = q.execute(Visibility.PUBLIC);
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

	@Override
	@SuppressWarnings("unchecked")
	public List<InfoNote> getSharedNotes(String id) throws NotLoggedInException {
		// TODO Auto-generated method stub
		List<InfoNote> result = new ArrayList<InfoNote>();
		PersistenceManager pm = PMF.getInstance().getPersistenceManager();

		com.sid.cloudynote.shared.User user;
		Query q = pm.newQuery(com.sid.cloudynote.shared.User.class);
		q.setFilter("id == idParam");
		q.declareParameters(String.class.getName()+" idParam");
		q.setRange(0,1);
		try {
			List<com.sid.cloudynote.shared.User> users = (List<com.sid.cloudynote.shared.User>) q.execute(id);
			if (users != null) {
				users = new ArrayList<com.sid.cloudynote.shared.User>(pm.detachCopyAll(users));
				users.size();
				if (!users.isEmpty()) {
					user = users.get(0);
					Map<Key,Integer> access = user.getAccess();
					result = this.getNotes(access.keySet());
				}
			}
		} catch (Exception e) {
		} finally {
			pm.close();
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public List<InfoNote> getNotes(Set<Key> keys){
		List<InfoNote> result = new ArrayList<InfoNote>();
		PersistenceManager pm = PMF.getInstance().getPersistenceManager();
		try {
			Query q = pm.newQuery(InfoNote.class);
			q.setFilter("key == keyParam");
			q.declareParameters(Key.class.getName() + " keyParam");
			q.setRange(0,1);
			
			for (Key key : keys) {
				Object obj = q.execute(key);
				if (obj != null) {
					List<InfoNote> notes = (List<InfoNote>) obj;
					notes = new ArrayList<InfoNote>(pm.detachCopyAll(notes));
					notes.size();
					result.add(notes.get(0));
				}
			}
		} catch (Exception e) {
		} finally {
			pm.close();
		}
		return result;
	}

	@Override
	public void makeNotesPublic(List<InfoNote> notes)
			throws NotLoggedInException {
		this.checkLoggedIn();
		PersistenceManager pm = PMF.getInstance().getPersistenceManager();
		for (InfoNote note : notes) {
			note.setVisibility(Visibility.PUBLIC);
		}
		try {
			pm.currentTransaction().begin();
			pm.makePersistentAll(notes);
			pm.currentTransaction().commit();
		} catch (Exception e) {
		} finally {
			pm.close();
		}
	}
}