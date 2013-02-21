package com.sid.cloudynote.client;

import java.util.Map;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.core.client.GWT;
import com.sid.cloudynote.shared.InfoNote;
import com.sid.cloudynote.shared.Notebook;

public class DataManager {
	static Map<Key, Notebook> notebooks;
	static Map<Key, InfoNote> notes;
	static Key currentNoteKey;
	static Key currentNotebookKey;
	static Notebook currentNotebook;
	static InfoNote currentNote;

	public static Map<Key, Notebook> getNotebooks() {
		return notebooks;
	}

	public static void setNotebooks(Map<Key, Notebook> notebooks) {
		DataManager.notebooks = notebooks;
	}

	public static Map<Key, InfoNote> getNotes() {
		return notes;
	}

	public static void setNotes(Map<Key, InfoNote> notes) {
		DataManager.notes = notes;
	}

	public static Key getCurrentNoteKey() {
		return DataManager.currentNoteKey;
	}

	public static void setCurrentNote(Key key) {
		DataManager.currentNoteKey = key;
		DataManager.currentNote = DataManager.notes.get(key);
	}

	public static void setCurrentNote(InfoNote note) {
		DataManager.currentNote = note;
		DataManager.currentNoteKey = note.getKey();
	}

	public static Key getCurrentNotebookKey() {
		return DataManager.currentNotebookKey;
	}

	public static void setCurrentNotebook(Key key) {
		DataManager.currentNotebookKey = key;
		DataManager.currentNotebook = DataManager.notebooks.get(key);
	}

	public static void setCurrentNotebook(Notebook notebook) {
		DataManager.currentNotebook = notebook;
		DataManager.currentNotebookKey = notebook.getKey();
	}

	public static InfoNote getCurrentNote() {
		if (DataManager.currentNote == null) {
			if (DataManager.notes == null || DataManager.notes.size() == 0) {
				GWT.log("No note exists!");
			} else {
				DataManager.currentNote = DataManager.notes
						.get(DataManager.currentNoteKey);
			}
		}
		return DataManager.currentNote;

	}

	/**
	 * The function to get the current selected notebook if no notebook selected
	 * then set the default notebook as current one
	 * 
	 * @return Notebook currentNotebook
	 */
	public static Notebook getCurrentNotebook() {
		if (DataManager.currentNotebook == null) {
			if (DataManager.notebooks == null
					|| DataManager.notebooks.size() == 0) {
				GWT.log("No notebook exists!");
			} else {
				for (Notebook notebook : DataManager.notebooks.values()) {
					if ("Default".equals(notebook.getName())) {
						setCurrentNotebook(notebook);
					}
				}
			}
		}
		return DataManager.currentNotebook;
	}
}