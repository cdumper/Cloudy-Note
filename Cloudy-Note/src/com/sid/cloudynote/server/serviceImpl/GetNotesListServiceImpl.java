package com.sid.cloudynote.server.serviceImpl;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.sid.cloudynote.client.model.INote;
import com.sid.cloudynote.client.service.GetNotesListService;
import com.sid.cloudynote.server.PMF;

public class GetNotesListServiceImpl extends RemoteServiceServlet implements GetNotesListService{
	private static final long serialVersionUID = 1L;

	@Override
	public List<INote> getNoteList() {
		List<INote> execute = null;
		PersistenceManager pm = PMF.getInstance().getPersistenceManager();
		try {
			pm.currentTransaction().begin();
			Query query = pm.newQuery(INote.class);
			execute = (List<INote>) query.execute("root");
			execute = new ArrayList< INote >(pm.detachCopyAll(execute));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (pm.currentTransaction().isActive()) {
				pm.currentTransaction().rollback();
			}
		}
		pm.close();
		return execute;
	}

}
