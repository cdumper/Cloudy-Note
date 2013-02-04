package com.sid.cloudynote.server.serviceImpl;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.sid.cloudynote.client.service.GetNotesListService;
import com.sid.cloudynote.server.PMF;
import com.sid.cloudynote.shared.InfoNote;

public class GetNotesListServiceImpl extends RemoteServiceServlet implements GetNotesListService{
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unchecked")
	@Override
	public List<InfoNote> getNoteList() {
		List<InfoNote> execute = null;
		PersistenceManager pm = PMF.getInstance().getPersistenceManager();
		try {
			pm.currentTransaction().begin();
			Query query = pm.newQuery(InfoNote.class);
			execute = (List<InfoNote>) query.execute("root");
			execute = new ArrayList<InfoNote>(pm.detachCopyAll(execute));
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
