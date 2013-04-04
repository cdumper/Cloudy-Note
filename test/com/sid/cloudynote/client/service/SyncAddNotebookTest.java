package com.sid.cloudynote.client.service;

import junit.framework.TestCase;

import org.junit.Test;

import com.gdevelop.gwt.syncrpc.SyncProxy;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sid.cloudynote.shared.InfoNote;

public class SyncAddNotebookTest extends TestCase{
	@Test
	public void testInfoNoteService() throws Exception {
	  InfoNoteServiceAsync service = (InfoNoteServiceAsync) SyncProxy.newProxyInstance(
			  InfoNoteServiceAsync.class, 
	    "http://127.0.0.1:8888/cloudy_note/", "notebookService");
	  
	  service.add(new InfoNote(), new AsyncCallback<InfoNote>(){
		@Override
		public void onFailure(Throwable caught) {
			fail("rpc failed");
		}

		@Override
		public void onSuccess(InfoNote result) {
			assertNull(result.getKey());
		}
	  });
	}
}
