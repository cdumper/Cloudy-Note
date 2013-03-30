package com.sid.cloudynote.server.serviceImpl;

import java.util.Map;
import java.util.Map.Entry;

import javax.jdo.PersistenceManager;

import com.google.appengine.api.datastore.Key;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.sid.cloudynote.client.service.AccessRightService;
import com.sid.cloudynote.server.PMF;
import com.sid.cloudynote.shared.Group;
import com.sid.cloudynote.shared.InfoNote;
import com.sid.cloudynote.shared.Notebook;
import com.sid.cloudynote.shared.User;

public class AccessRightServiceImpl extends RemoteServiceServlet implements
		AccessRightService {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7288183526875394912L;

	@Override
	public void saveGroupAndUserAccess(Group group,
			Map<Key, Integer> groupAccess, User user,
			Map<Key, Integer> userAccess) {
		if (group != null) {
			this.saveGroupAccess(group, groupAccess);
		}
		if (user != null) {
			this.saveUserAccess(user, userAccess);
		}
	}

	@Override
	public void saveNotebookAndNotePermission(Notebook notebook,
			Map<Key, Integer> notebookGroupPermission,
			Map<String, Integer> notebookUserPermission, InfoNote note,
			Map<Key, Integer> noteGroupPermission,
			Map<String, Integer> noteUserPermission) {
		if (notebook != null){
			this.saveNotebookPermission(notebook, notebookGroupPermission,
					notebookUserPermission);
		}
		if (note != null) {
			this.saveNotePermission(note, noteGroupPermission, noteUserPermission);
		}
	}

	@Override
	public void saveGroupAccess(Group group, Map<Key, Integer> groupAccess) {
		PersistenceManager pm = PMF.getInstance().getPersistenceManager();
		try {
			pm.currentTransaction().begin();
			if (groupAccess != null) {
				// removed old access entries
				for (Entry<Key, Integer> entry : group.getAccess().entrySet()) {
					if (!groupAccess.keySet().contains(entry.getKey())) {
						Key key = entry.getKey();
						if (key.getKind().equals("Notebook")) {
							// TODO group notebook access
						} else if (key.getKind().equals("InfoNote")) {
							InfoNote note = pm.getObjectById(InfoNote.class,
									key);
							note.getGroupAccess().remove(group.getKey());
							pm.makePersistent(note);
						}
					}
				}
				group.getAccess().clear();
				// add new or modified access entries
				for (Entry<Key, Integer> entry : groupAccess.entrySet()) {
					Key key = entry.getKey();
					if (key.getKind().equals("Notebook")) {
						// TODO group notebook access
					} else if (key.getKind().equals("InfoNote")) {
						group.getAccess().put(key, entry.getValue());
						InfoNote note = pm.getObjectById(InfoNote.class, key);
						note.getGroupAccess().put(group.getKey(),
								entry.getValue());
						pm.makePersistent(note);
					}
				}
			}
			pm.makePersistent(group);
			pm.currentTransaction().commit();
		} catch (Exception e) {

		} finally {
			if (pm.currentTransaction().isActive()) {
				pm.currentTransaction().rollback();
			}
			pm.close();
		}
	}

	@Override
	public void saveUserAccess(User user, Map<Key, Integer> userAccess) {
		PersistenceManager pm = PMF.getInstance().getPersistenceManager();

		try {
			pm.currentTransaction().begin();
			if (userAccess != null) {
				// removed old access entries
				for (Entry<Key, Integer> entry : user.getAccess().entrySet()) {
					if (!userAccess.keySet().contains(entry.getKey())) {
						Key key = entry.getKey();
						if (key.getKind().equals("Notebook")) {
							// TODO user notebook access
						} else if (key.getKind().equals("InfoNote")) {
							InfoNote note = pm.getObjectById(InfoNote.class,
									key);
							note.getUserAccess().remove(user.getEmail());
							pm.makePersistent(note);
						}
					}
				}
				user.getAccess().clear();
				// add new access entries
				for (Entry<Key, Integer> entry : userAccess.entrySet()) {
					Key key = entry.getKey();
					if (key.getKind().equals("Notebook")) {
						// TODO user notebook access
					} else if (key.getKind().equals("InfoNote")) {
						user.getAccess().put(key, entry.getValue());
						InfoNote note = pm.getObjectById(InfoNote.class, key);
						note.getUserAccess().put(user.getEmail(),
								entry.getValue());
						pm.makePersistent(note);
					}
				}
			}
			pm.makePersistent(user);
			pm.currentTransaction().commit();
		} catch (Exception e) {

		} finally {
			if (pm.currentTransaction().isActive()) {
				pm.currentTransaction().rollback();
			}
			pm.close();
		}
	}

	@Override
	public void saveNotebookPermission(Notebook notebook,
			Map<Key, Integer> notebookGroupPermission,
			Map<String, Integer> notebookUserPermission) {
		// TODO save notebook permission

	}

	@Override
	public void saveNotePermission(InfoNote note,
			Map<Key, Integer> noteGroupPermission,
			Map<String, Integer> noteUserPermission) {
		PersistenceManager pm = PMF.getInstance().getPersistenceManager();
		try {
			pm.currentTransaction().begin();

			if (noteGroupPermission != null) {
				// removed old access entries
				for (Entry<Key, Integer> entry : note.getGroupAccess()
						.entrySet()) {
					if (!noteGroupPermission.keySet().contains(entry.getKey())) {
						Group group = pm.getObjectById(Group.class,
								entry.getKey());
						group.getAccess().remove(note.getKey());
						pm.makePersistent(group);
					}
				}
				note.getGroupAccess().clear();
				// add new access entries
				for (Entry<Key, Integer> entry : noteGroupPermission.entrySet()) {
					note.getGroupAccess().put(entry.getKey(), entry.getValue());
					Group group = pm.getObjectById(Group.class, entry.getKey());
					group.getAccess().put(note.getKey(), entry.getValue());
					pm.makePersistent(group);
				}
			}
			
			if (noteUserPermission != null) {
				// removed old access entries
				for (Entry<String, Integer> entry : note.getUserAccess().entrySet()) {
					if (!noteUserPermission.keySet().contains(entry.getKey())) {
						User user = pm.getObjectById(User.class,
								entry.getKey());
						user.getAccess().remove(note.getKey());
						pm.makePersistent(user);
					}
				}
				note.getUserAccess().clear();
				// add new access entries
				for (Entry<String, Integer> entry : noteUserPermission.entrySet()) {
					note.getUserAccess().put(entry.getKey(), entry.getValue());
					User user = pm.getObjectById(User.class, entry.getKey());
					user.getAccess().put(note.getKey(), entry.getValue());
					pm.makePersistent(user);
				}
			}

			pm.makePersistent(note);
			pm.currentTransaction().commit();
		} catch (Exception e) {

		} finally {
			if (pm.currentTransaction().isActive()) {
				pm.currentTransaction().rollback();
			}
			pm.close();
		}
	}
}
