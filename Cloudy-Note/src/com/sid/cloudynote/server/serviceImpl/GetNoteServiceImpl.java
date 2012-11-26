package com.sid.cloudynote.server.serviceImpl;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.sid.cloudynote.client.model.Note;
import com.sid.cloudynote.client.service.GetNoteService;
import com.sid.cloudynote.server.PMF;

public class GetNoteServiceImpl extends RemoteServiceServlet implements GetNoteService{
	private static final long serialVersionUID = 1L;

	@Override
	public Note getNote(String title) {
		List<Note> result = new ArrayList<Note>();
//		System.out.println("look for "+title);
		PersistenceManager pm = PMF.getInstance().getPersistenceManager();
		Note note = new Note();
		try {
			pm.currentTransaction().begin();
			Query query = pm.newQuery(Note.class);
			query.setFilter("title == titleParam");
			query.declareParameters("String titleParam");
			result = (List<Note>) query.execute(title);
			result = new ArrayList<Note>(pm.detachCopyAll(result));
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
