package com.sid.cloudynote.server.serviceImpl;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.PersistenceManager;

import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.search.Cursor;
import com.google.appengine.api.search.Field;
import com.google.appengine.api.search.IndexSpec;
import com.google.appengine.api.search.Query;
import com.google.appengine.api.search.QueryOptions;
import com.google.appengine.api.search.Results;
import com.google.appengine.api.search.ScoredDocument;
import com.google.appengine.api.search.SearchException;
import com.google.appengine.api.search.SearchServiceFactory;
import com.google.appengine.api.search.SortExpression;
import com.google.appengine.api.search.SortOptions;
import com.google.appengine.api.search.StatusCode;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.sid.cloudynote.client.service.InfoNoteSearchService;
import com.sid.cloudynote.server.DocumentManager;
import com.sid.cloudynote.server.PMF;
import com.sid.cloudynote.shared.InfoNote;
import com.sid.cloudynote.shared.NotLoggedInException;

public class InfoNoteSearchServiceImpl extends RemoteServiceServlet implements
		InfoNoteSearchService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2290681731155436884L;

	@Override
	public List<InfoNote> searchNotes(String text) throws NotLoggedInException {
		checkLoggedIn();
		List<InfoNote> result = new ArrayList<InfoNote>();
		PersistenceManager pm = PMF.getInstance().getPersistenceManager();
		String query;
		if ("".equals(text.trim())) {
			query = "owner:" + getUser().getEmail();
		} else {
			query = "owner:" + getUser().getEmail() + " AND (title:" + text
					+ " OR content:" + text + ")";
		}

		try {
			// Query the index.
			Results<ScoredDocument> results = this.findNoteDocuments(query, 5,
					Cursor.newBuilder().build());
			InfoNote note;
			for (ScoredDocument document : results) {
				note = pm.detachCopy(pm.getObjectById(InfoNote.class,
						KeyFactory.stringToKey(document.getId())));
				result.add(note);
				for (Field field : document.getFields()) {
					switch (field.getType()) {
					case TEXT:
						System.out.println(field.getText());
						break;
					case HTML:
						System.out.println(field.getHTML());
						break;
					case ATOM:
						System.out.println(field.getAtom());
						break;
					case DATE:
						System.out.println(field.getDate());
						break;
					}
				}
			}
			// save the cursor for the next set of results
			// Cursor nextCursor = results.getCursor();
		} catch (SearchException e) {
			if (StatusCode.TRANSIENT_ERROR.equals(e.getOperationResult()
					.getCode())) {
				// retry
			}
		}
		return result;
	}

	public Results<ScoredDocument> findNoteDocuments(String queryString,
			int limit, Cursor cursor) {
		try {
			SortOptions sortOptions = SortOptions
					.newBuilder()
					.addSortExpression(
							SortExpression
									.newBuilder()
									.setExpression("title")
									.setDirection(
											SortExpression.SortDirection.DESCENDING)
									.setDefaultValue("")).setLimit(1000)
					.build();
			QueryOptions options = QueryOptions.newBuilder().setLimit(limit)
					.setFieldsToSnippet("content")
					.setFieldsToReturn("title", "content")
					.setSortOptions(sortOptions).setCursor(cursor).build();
			Query query = Query.newBuilder().setOptions(options)
					.build(queryString);
			return SearchServiceFactory.getSearchService().getIndex(IndexSpec.newBuilder().setName("note-index").build()).search(query);
		} catch (SearchException e) {
			return null;
		}
	}

	private void checkLoggedIn() throws NotLoggedInException {
		if (getUser() == null) {
			throw new NotLoggedInException("Not logged in.");
		}
	}

	private User getUser() {
		UserService userService = UserServiceFactory.getUserService();
		return userService.getCurrentUser();
	}
}
