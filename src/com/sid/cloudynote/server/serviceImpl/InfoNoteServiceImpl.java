package com.sid.cloudynote.server.serviceImpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Text;
import com.google.appengine.api.search.Document;
import com.google.appengine.api.search.Field;
import com.google.appengine.api.search.IndexSpec;
import com.google.appengine.api.search.PutException;
import com.google.appengine.api.search.SearchServiceFactory;
import com.google.appengine.api.search.StatusCode;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.sid.cloudynote.client.service.InfoNoteService;
import com.sid.cloudynote.server.DocumentManager;
import com.sid.cloudynote.server.PMF;
import com.sid.cloudynote.shared.Group;
import com.sid.cloudynote.shared.InfoNote;
import com.sid.cloudynote.shared.NotLoggedInException;
import com.sid.cloudynote.shared.Notebook;
import com.sid.cloudynote.shared.Tag;
import com.sid.cloudynote.shared.Visibility;

public class InfoNoteServiceImpl extends RemoteServiceServlet implements
		InfoNoteService {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1560433333437871362L;

	/**
	 * Create a note
	 * 
	 * @throws NotLoggedInException
	 */
	@Override
	public InfoNote add(InfoNote note) throws NotLoggedInException {
		checkLoggedIn();
		PersistenceManager pm = PMF.getInstance().getPersistenceManager();
		try {
			pm.currentTransaction().begin();
			note.setCreatedTime(new Date());
			note.setLastModifiedTime(new Date());
			note.setUser(getUser());
			
			//Add one to totalNotes of user & notebook
			com.sid.cloudynote.shared.User user = pm.getObjectById(com.sid.cloudynote.shared.User.class, getUser().getEmail());
			user.setTotalNotes(user.getTotalNotes()+1);
			Notebook notebook = pm.getObjectById(Notebook.class, note.getNotebook().getKey());
			notebook.setTotalNotes(notebook.getTotalNotes()+1);
			
			pm.makePersistent(note);
			pm.makePersistent(notebook);
			pm.makePersistent(user);
			pm.currentTransaction().commit();
			note = pm.detachCopy(note);
			//create a searchable document for the newly added note
			createDocumentForNote(note);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (pm.currentTransaction().isActive()) {
				pm.currentTransaction().rollback();
			}
			pm.close();
		}
		return note;
	}

	/**
	 * Delete a note
	 * 
	 * @throws NotLoggedInException
	 */
	@Override
	public void delete(InfoNote note) throws NotLoggedInException {
		checkLoggedIn();
		PersistenceManager pm = PMF.getInstance().getPersistenceManager();
		try {
			// check the owner of the note is the current user
			if (!note.getUser().equals(getUser())) {
				GWT.log("You don't have the access to delete since you're not the ower of the note");
			} else {
				//Delete one to totalNotes of user & notebook
				com.sid.cloudynote.shared.User user = pm.getObjectById(com.sid.cloudynote.shared.User.class, getUser().getEmail());
				user.setTotalNotes(user.getTotalNotes()-1);
				Notebook notebook = pm.getObjectById(Notebook.class, note.getNotebook().getKey());
				notebook.setTotalNotes(notebook.getTotalNotes()-1);
				
				pm.deletePersistent(note);
				pm.makePersistent(notebook);
				pm.makePersistent(user);
				deleteDocumentForNote(note);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pm.close();
		}
	}

	/**
	 * Update a note
	 * 
	 * @throws NotLoggedInException
	 */
	@Override
	public InfoNote modify(InfoNote note) throws NotLoggedInException {
		checkLoggedIn();
		PersistenceManager pm = PMF.getInstance().getPersistenceManager();
		try {
			pm.currentTransaction().begin();
			if (!verifyEditAccess(note)) {
				return null;
			} else {
				note.setLastModifiedTime(new Date());
				pm.makePersistent(note);
			}
			pm.currentTransaction().commit();
			note = pm.detachCopy(note);
			createDocumentForNote(note);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (pm.currentTransaction().isActive())
				pm.currentTransaction().rollback();
			pm.close();
		}
		return note;
	}

	/**
	 * Move a note to another notebook
	 */
	@Override
	public InfoNote moveNoteTo(InfoNote note, Notebook notebook)
			throws NotLoggedInException {
		checkLoggedIn();
		PersistenceManager pm = PMF.getInstance().getPersistenceManager();
		String title = note.getTitle();
		String content = note.getContent().getValue();
		List<String> attachments = note.getAttachments();
		InfoNote entity = new InfoNote(notebook, title, new Text(content),
				attachments);
		try {
			pm.currentTransaction().begin();
			if (!note.getUser().equals(getUser())) {
				GWT.log("You don't have the access to delete since you're not the ower of the note");
			} else {
				entity.setCreatedTime(note.getCreatedTime());
				entity.setLastModifiedTime(new Date());
				entity.setUser(getUser());
				note = pm.getObjectById(InfoNote.class, note.getKey());
				Notebook originNotebook = note.getNotebook();
				originNotebook.setTotalNotes(originNotebook.getTotalNotes()-1);
				pm.makePersistent(originNotebook);
				pm.deletePersistent(note);
				deleteDocumentForNote(note);
				pm.makePersistent(entity);
				notebook.setTotalNotes(notebook.getTotalNotes()+1);
				pm.makePersistent(notebook);
				createDocumentForNote(entity);
			}
			entity = pm.detachCopy(entity);
			pm.currentTransaction().commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (pm.currentTransaction().isActive())
				pm.currentTransaction().rollback();
			pm.close();
		}
		return entity;
	}

	/**
	 * Get notes in a notebook
	 */
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
			e.printStackTrace();
		} finally {
			pm.close();
		}
		return result;
	}

	/**
	 * Get all the public notes
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<InfoNote> getPublicNotes() throws NotLoggedInException {
		List<InfoNote> result = new ArrayList<InfoNote>();
		PersistenceManager pm = PMF.getInstance().getPersistenceManager();

		Query q = pm.newQuery(InfoNote.class);
		q.setFilter("visibility == vParam");
		q.declareParameters(Integer.class.getName() + " vParam");
		try {
			Object obj = q.execute(Visibility.PUBLIC);
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

	/**
	 * Get all the notes shared with current logged in user
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<InfoNote> getSharedNotes(String email)
			throws NotLoggedInException {
		List<InfoNote> result = new ArrayList<InfoNote>();
		PersistenceManager pm = PMF.getInstance().getPersistenceManager();

		com.sid.cloudynote.shared.User user;
		Query q = pm.newQuery(com.sid.cloudynote.shared.User.class);
		q.setFilter("email == emailParam");
		q.declareParameters(String.class.getName() + " emailParam");
		q.setRange(0, 1);
		try {
			List<com.sid.cloudynote.shared.User> users = (List<com.sid.cloudynote.shared.User>) q
					.execute(email);
			if (users != null) {
				users = new ArrayList<com.sid.cloudynote.shared.User>(
						pm.detachCopyAll(users));
				users.size();
				if (!users.isEmpty()) {
					user = users.get(0);
					Map<Key, Integer> access = user.getAccess();
					result = this.getNotes(access.keySet());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pm.close();
		}
		return result;
	}

	/**
	 * Get notes with a set of keys
	 * 
	 * @param keys
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<InfoNote> getNotes(Set<Key> keys) {
		List<InfoNote> result = new ArrayList<InfoNote>();
		PersistenceManager pm = PMF.getInstance().getPersistenceManager();
		try {
			Query q = pm.newQuery(InfoNote.class);
			q.setFilter("key == keyParam");
			q.declareParameters(Key.class.getName() + " keyParam");
			q.setRange(0, 1);

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
			e.printStackTrace();
		} finally {
			pm.close();
		}
		return result;
	}

	/**
	 * Change the visibility of a list of notes to make them public
	 */
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
			e.printStackTrace();
		} finally {
			pm.close();
		}
	}

	/**
	 * Verify the current logged in user whether or not has the edit access to a
	 * note
	 */
	@Override
	public boolean verifyEditAccess(InfoNote infoNote)
			throws NotLoggedInException {
		this.checkLoggedIn();
		// com.sid.cloudynote.shared.User user = AppController.get()
		// .getLoginInfo();
		PersistenceManager pm = PMF.getInstance().getPersistenceManager();

		// verify the user access
		String userEmail = this.getUser().getEmail();
		InfoNote note = pm.detachCopy(pm.getObjectById(InfoNote.class,
				infoNote.getKey()));
		//check if is the owner
		if (note.getUser().getEmail().equals(userEmail)) {
			return true;
		}
		
		if (note.getUserAccess().containsKey(userEmail)) {
			if (note.getUserAccess().get(userEmail) == 1) {
				// read-only access
			} else if (note.getUserAccess().get(userEmail) == 2) {
				// write access
				return true;
			}
		}

		// verify the group access
		for (Entry<Key, Integer> entry : note.getGroupAccess().entrySet()) {
			if (entry.getValue() == 2) {
				Group group = pm.detachCopy(pm.getObjectById(Group.class,
						entry.getKey()));
				if (group.getOwner().equals(userEmail)
						|| group.getMembers().contains(userEmail)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Grant access to the current logged in user to a list of notes
	 */
	@Override
	public void addUserAccessEntry(List<InfoNote> notes,
			Map<String, Integer> access) throws NotLoggedInException {
		this.checkLoggedIn();
		PersistenceManager pm = PMF.getInstance().getPersistenceManager();

		for (InfoNote note : notes) {
			for (Entry<String, Integer> entry : access.entrySet()) {
				note.getUserAccess().put(entry.getKey(), entry.getValue());
			}
		}
		try {
			pm.currentTransaction().begin();
			pm.makePersistentAll(notes);
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
	public void addGroupAccessEntry(List<InfoNote> notes,
			Map<Key, Integer> access) throws NotLoggedInException {
		this.checkLoggedIn();
		PersistenceManager pm = PMF.getInstance().getPersistenceManager();

		// if (!getUser().getEmail().equals(note.getUser().getEmail())) {
		// GWT.log("Permission denied to add group access entry. Not owner of note:"
		// + note.getKey() + " Request from " + getUser().getEmail());
		// return;
		// }
		for (InfoNote note : notes) {
			for (Entry<Key, Integer> entry : access.entrySet()) {
				note.getGroupAccess().put(entry.getKey(), entry.getValue());
			}
		}
		try {
			pm.currentTransaction().begin();
			pm.makePersistentAll(notes);
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
	public List<InfoNote> getNotesInGroup(Key groupKey)
			throws NotLoggedInException {
		this.checkLoggedIn();
		PersistenceManager pm = PMF.getInstance().getPersistenceManager();
		List<InfoNote> notes = new ArrayList<InfoNote>();
		Group group = pm.detachCopy(pm.getObjectById(Group.class, groupKey));
		if (group.getAccess() != null) {
			for (Key key : group.getAccess().keySet()) {
				notes.add(pm.detachCopy(pm.getObjectById(InfoNote.class, key)));
			}
		}
		return notes;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<InfoNote> getNotesByTag(Tag tag) {
		PersistenceManager pm = PMF.getInstance().getPersistenceManager();
		List<InfoNote> notes = new ArrayList<InfoNote>();
		Query q = pm.newQuery(InfoNote.class);
		q.setFilter("tags.contains(tagParam)");
		q.declareParameters(Key.class.getName() + " tagParam");
		Object o = q.execute(tag.getKey());
		if (o != null) {
			notes = (List<InfoNote>) o;
			notes = new ArrayList<InfoNote>(pm.detachCopyAll(notes));
			notes.size();
		}
		return notes;
	}

	/**
	 * Method to create a searchable document for note
	 * 
	 * @param note
	 */
	private void createDocumentForNote(InfoNote note) {
		Document document = Document
				.newBuilder()
				.setId(KeyFactory.keyToString(note.getKey()))
				.addField(
						Field.newBuilder().setName("title")
								.setText(note.getTitle()))
				.addField(
						Field.newBuilder().setName("content")
								.setHTML(note.getContent().getValue()))
				.addField(
						Field.newBuilder().setName("owner")
								.setText(note.getUser().getEmail())).build();
		try {
			IndexSpec indexSpec = IndexSpec.newBuilder().setName("note-index").build();
		    SearchServiceFactory.getSearchService().getIndex(indexSpec).put(document);
		} catch (PutException e) {
			if (StatusCode.TRANSIENT_ERROR.equals(e.getOperationResult()
					.getCode())) {
				DocumentManager.getIndex().put(document);
			}
		}
	}

	private void deleteDocumentForNote(InfoNote note) {
		try {
			SearchServiceFactory.getSearchService().getIndex(IndexSpec.newBuilder().setName("note-index").build()).delete(KeyFactory.keyToString(note.getKey()));
		} catch (RuntimeException e) {
			GWT.log("Failed to delete document");
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
	public InfoNote getNoteByKey(Key key) {
		PersistenceManager pm = PMF.getInstance().getPersistenceManager();
		InfoNote note = pm.detachCopy(pm.getObjectById(InfoNote.class, key));
		pm.close();
		return note;
	}
}
