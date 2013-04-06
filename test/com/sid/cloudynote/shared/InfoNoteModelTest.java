package com.sid.cloudynote.shared;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.google.appengine.api.datastore.Text;

public class InfoNoteModelTest{
	@Test
	public void testConstructor(){
		InfoNote note = new InfoNote(new Notebook("Notebook"),"Title",new Text("Content"));
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
}
