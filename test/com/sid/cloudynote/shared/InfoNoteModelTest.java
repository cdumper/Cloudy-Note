package com.sid.cloudynote.shared;

import junit.framework.TestCase;

public class InfoNoteModelTest extends TestCase{

	public void testConstructor(){
		InfoNote note = new InfoNote(new Notebook("Notebook"),"Title","Content");
		assertEquals(note.getNotebook().getName(),"Notebook");
		assertEquals(note.getTitle(),"Title");
		assertEquals(note.getContent(),"Content");
		assertEquals(note.getVisibility(),Visibility.PRIVATE);
		assertEquals(note.getAttachments().size(),0);
		assertEquals(note.getGroupAccess().size(),0);
		assertEquals(note.getUserAccess().size(),0);
		assertEquals(note.getTags().size(),0);
		assertEquals(note.getUser(),null);
		assertEquals(note.getCreatedTime(),null);
		assertEquals(note.getLastModifiedTime(),null);
	}
	
	public void testSetUser(){
	}
}
