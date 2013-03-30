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
	 * Create a new notebook
	 * 
	 * @throws NotLoggedInException
	 */
	@Override
	public void add(Notebook entity) throws NotLoggedInException {
		checkLoggedIn();
		PersistenceManager pm = PMF.getInstance().getPersistenceManager();
		try {
			pm.currentTransaction().begin();
			entity.setUser(getUser());
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

	@Override
	@SuppressWarnings("unchecked")
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
