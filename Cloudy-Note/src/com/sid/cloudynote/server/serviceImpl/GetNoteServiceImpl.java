package com.sid.cloudynote.server.serviceImpl;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.sid.cloudynote.client.model.InfoNote;
import com.sid.cloudynote.client.service.GetNoteService;
import com.sid.cloudynote.server.PMF;

public class GetNoteServiceImpl extends RemoteServiceServlet implements GetNoteService{
	private static final long serialVersionUID = 1L;

	@Override
	public InfoNote getNote(String title) {
		List<InfoNote> result = new ArrayList<InfoNote>();
//		System.out.println("look for "+title);
		PersistenceManager pm = PMF.getInstance().getPersistenceManager();
		InfoNote note = new InfoNote();
		try {
			pm.currentTransaction().begin();
			Query query = pm.newQuery(InfoNote.class);
			query.setFilter("title == titleParam");
			query.declareParameters("String titleParam");
			result = (List<InfoNote>) query.execute(title);
			result = new ArrayList<InfoNote>(pm.detachCopyAll(result));
			note = result.get(0);
//			System.out.println(result.size());
//			for(Note n : result){
//				System.out.println("result list:"+n.getTitle());
//			}
//			note = (Note) query.execute(title);
			note = result.get(0);
			System.out.println("item 0:"+note.getTitle());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (pm.currentTransaction().isActive()) {
				pm.currentTransaction().rollback();
			}
		}
		pm.close();
		return note;
	}
}
