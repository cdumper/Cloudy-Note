package com.sid.cloudynote.client;

import java.util.LinkedHashMap;
import java.util.Map;

import com.google.appengine.api.datastore.Key;
import com.google.gwt.core.client.GWT;
import com.sid.cloudynote.shared.Group;
import com.sid.cloudynote.shared.InfoNote;
import com.sid.cloudynote.shared.Notebook;
import com.sid.cloudynote.shared.Tag;
import com.sid.cloudynote.shared.User;

public class DataManager {
	static Map<Key, Group> myGroups = new LinkedHashMap<Key,Group>();
	static Map<Key, Group> allGroups = new LinkedHashMap<Key,Group>();
	static Map<String, User> allFriends = new LinkedHashMap<String,User>();
	static Map<Key, Notebook> notebooks = new LinkedHashMap<Key,Notebook>();
	static Map<Key, InfoNote> notes = new LinkedHashMap<Key,InfoNote>();
	static Map<Key, Tag> allTags = new LinkedHashMap<Key,Tag>();
	static Key currentNoteKey;
	static Key currentNotebookKey;
	static Notebook currentNotebook;
	static InfoNote currentNote;

	public static Map<Key, Tag> getAllTags() {
		return allTags;
	}

	public static void setAllTags(Map<Key, Tag> allTags) {
		DataManager.allTags.clear();
		DataManager.allTags.putAll(allTags);
	}

	public static Map<Key, Notebook> getNotebooks() {
		return notebooks;
	}

	public static void setNotebooks(Map<Key, Notebook> notebooks) {
		DataManager.notebooks.clear();
		DataManager.notebooks.putAll(notebooks);
	}

	public static Map<Key, InfoNote> getNotes() {
		return notes;
	}

	public static void setNotes(Map<Key, InfoNote> notes) {
		DataManager.notes.clear();
		DataManager.notes.putAll(notes);
	}
	
	
	public static Map<Key, Group> getMyGroups() {
		return myGroups;
	}
	
	public static void setMyGroups(Map<Key, Group> myGroups) {
		DataManager.myGroups.clear();
		DataManager.myGroups.putAll(myGroups);
	}
	
	public static Map<Key, Group> getAllGroups() {
		return allGroups;
	}
	
	public static void setAllGroups(Map<Key, Group> allGroups) {
		DataManager.allGroups.clear();
		DataManager.allGroups.putAll(allGroups);
	}

	public static Map<String, User> getAllFriends() {
		return allFriends;
	}

	public static void setAllFriends(Map<String, User> allFriends) {
		DataManager.allFriends.clear();
		DataManager.allFriends.putAll(allFriends);
	}
	
	// getter and setter for current note and notebook
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