package com.sid.cloudynote.client.model;

import java.util.List;

public class DataManager {
	static List<Notebook> notebooks;
	static List<InfoNote> notes;
	static int currentNote;
	static int currentNotebook;
	
	public static List<Notebook> getNotebooks(){
		return notebooks;
	}
	
	public static void setNotebooks(List<Notebook> notebooks){
		DataManager.notebooks = notebooks;
	}
	
	public static List<InfoNote> getNotes(){
		return notes;
	}
	
	public static void setNotes(List<InfoNote> notes){
		DataManager.notes = notes;
	}
	
	public static int getCurrentNote(){
		return DataManager.currentNote;
	}
	
	public static void setCurrentNote(int index){
		DataManager.currentNote = index;
	}
	
	public static int getCurrentNotebook(){
		return DataManager.currentNotebook;
	}
	
	public static void setCurrentNotebook(int index){
		DataManager.currentNotebook = index;
	}
}
