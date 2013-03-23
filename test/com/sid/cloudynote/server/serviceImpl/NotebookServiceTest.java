package com.sid.cloudynote.server.serviceImpl;

import com.google.gwt.core.client.GWT;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.sid.cloudynote.client.service.NotebookService;
import com.sid.cloudynote.client.service.NotebookServiceAsync;
import com.sid.cloudynote.shared.Notebook;

public class NotebookServiceTest extends GWTTestCase {
	NotebookServiceAsync service;

	@Override
	public String getModuleName() {
		return "com.sid.cloudynote.Cloudy_Note";
	}

	@Override
	protected void gwtSetUp() throws Exception {
		service = GWT.create(NotebookService.class);
		ServiceDefTarget target = (ServiceDefTarget) service;
		target.setServiceEntryPoint(GWT.getModuleBaseURL() + "cloudy_note/notebookService");
	}

	@Override
	protected void gwtTearDown() throws Exception {
		// TODO Auto-generated method stub
		super.gwtTearDown();
	}

//	public void testAddNotebook() {
//		delayTestFinish(15000);
//
//		service.add(new Notebook("New notebook"), new AsyncCallback<Notebook>() {
//			public void onFailure(Throwable caught) {
//				fail("Request failure: " + caught.getMessage());
//			}
//
//			public void onSuccess(Notebook result) {
//				assertNotNull(result.getKey());
//				finishTest();
//			}
//		});
//	}
}
