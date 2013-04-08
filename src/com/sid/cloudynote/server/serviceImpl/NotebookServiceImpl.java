package com.sid.cloudynote.server.serviceImpl;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.search.IndexSpec;
import com.google.appengine.api.search.SearchServiceFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.sid.cloudynote.client.service.NotebookService;
import com.sid.cloudynote.server.PMF;
import com.sid.cloudynote.shared.InfoNote;
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
	 * 
	 * @throws NotLoggedInException
	 */
	@Override
	public void delete(Notebook entity) throws NotLoggedInException {
		checkLoggedIn();
		PersistenceManager pm = PMF.getInstance().getPersistenceManager();
		try {
			pm.currentTransaction().begin();
			List<InfoNote> notes = this.getNotes(entity);
			if (notes == null || notes.size() == 0) {
			} else {
				for(InfoNote note : notes){
					pm.deletePersistent(note);
					this.deleteDocumentForNote(note);
				}
				com.sid.cloudynote.shared.User user = pm.getObjectById(com.sid.cloudynote.shared.User.class, entity.getUser().getEmail());
				user.setTotalNotes(user.getTotalNotes()-notes.size());
				pm.makePersistent(user);
			}
			pm.deletePersistent(entity);
			pm.currentTransaction().commit();
		} catch (Exception e) {
		} finally {
			pm.close();
		}
	}

	/**
	 * 
	 * @throws NotLoggedInException
	 */
	@Override
	public void modify(Notebook entity) throws NotLoggedInException {
		checkLoggedIn();
		PersistenceManager pm = PMF.getInstance().getPersistenceManager();
		try {
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
			e.printStackTrace();
		} finally {
			pm.close();
		}
		return result;
	}
	
	private void deleteDocumentForNote(InfoNote note) {
		try {
			SearchServiceFactory.getSearchService().getIndex(IndexSpec.newBuilder().setName("note-index").build()).delete(KeyFactory.keyToString(note.getKey()));
		} catch (RuntimeException e) {
			GWT.log("Failed to delete document");
		}
	}
}
