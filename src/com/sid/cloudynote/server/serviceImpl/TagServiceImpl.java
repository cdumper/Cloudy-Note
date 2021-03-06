package com.sid.cloudynote.server.serviceImpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.sid.cloudynote.client.service.TagService;
import com.sid.cloudynote.server.PMF;
import com.sid.cloudynote.shared.NotLoggedInException;
import com.sid.cloudynote.shared.Tag;

public class TagServiceImpl extends RemoteServiceServlet implements TagService{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1696604858964797961L;

	@Override
	public void add(Tag entity) throws NotLoggedInException {
		checkLoggedIn();
		entity.setUser(getUser().getEmail());
		entity.setCreatedTime(new Date());
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

	@Override
	public void modify(Tag entity) throws NotLoggedInException {
		checkLoggedIn();
		PersistenceManager pm = PMF.getInstance().getPersistenceManager();
		try {
			pm.currentTransaction().begin();
			if (!entity.getUser().equals(getUser().getEmail())) {
				GWT.log("You don't have the access to delete since you're not the ower of the note");
			} else {
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

	@Override
	public void delete(Tag entity) throws NotLoggedInException {
		checkLoggedIn();
		PersistenceManager pm = PMF.getInstance().getPersistenceManager();
		try {
			// check the owner of the tag is the current user
			if (!entity.getUser().equals(getUser().getEmail())) {
				GWT.log("You don't have the access to delete since you're not the ower of the tag");
			} else {
				pm.deletePersistent(entity);
			}
		} catch (Exception e) {
		} finally {
			pm.close();
		}
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
	public List<Tag> getTags(String email) throws NotLoggedInException {
		checkLoggedIn();
		List<Tag> result = new ArrayList<Tag>();
		PersistenceManager pm = PMF.getInstance().getPersistenceManager();
		try {
			Query q = pm.newQuery(Tag.class);
			q.setFilter("user == userParam");
			q.declareParameters(User.class.getName() + " userParam");

			Object obj = q.execute(email);
			if (obj != null) {
				result = (List<Tag>) obj;
				result = new ArrayList<Tag>(pm.detachCopyAll(result));
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
	public List<Tag> getTags(List<Key> tagsKey) throws NotLoggedInException{
		List<Tag> result = new ArrayList<Tag>();
		PersistenceManager pm = PMF.getInstance().getPersistenceManager();
		try {
			result.addAll(pm.detachCopyAll(pm.getObjectsById(tagsKey)));
		} catch (Exception e) {
		} finally {
			pm.close();
		}
		return result;
	}

	@Override
	public List<Tag> createTags(List<Tag> tags) throws NotLoggedInException{
		List<Tag> result = new ArrayList<Tag>();
		PersistenceManager pm = PMF.getInstance().getPersistenceManager();
		try {
			pm.makePersistentAll(tags);
			result = new ArrayList<Tag>(pm.detachCopyAll(tags));
		} catch (Exception e) {
		} finally {
			pm.close();
		}
		return result;
	}
}
