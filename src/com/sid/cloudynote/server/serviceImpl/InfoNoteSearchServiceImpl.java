package com.sid.cloudynote.server.serviceImpl;

import com.google.appengine.api.search.Cursor;
import com.google.appengine.api.search.Field;
import com.google.appengine.api.search.Query;
import com.google.appengine.api.search.QueryOptions;
import com.google.appengine.api.search.Results;
import com.google.appengine.api.search.ScoredDocument;
import com.google.appengine.api.search.SearchException;
import com.google.appengine.api.search.SortExpression;
import com.google.appengine.api.search.SortOptions;
import com.google.appengine.api.search.StatusCode;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.sid.cloudynote.client.service.InfoNoteSearchService;
import com.sid.cloudynote.server.DocumentManager;

public class InfoNoteSearchServiceImpl extends RemoteServiceServlet implements
		InfoNoteSearchService {

	@Override
	public void searchNotes(String text) {
		try {
			// Query the index.
			Results<ScoredDocument> results = this.findNoteDocuments("title:"
					+ text + " OR content:" + text, 5, Cursor.newBuilder()
					.build());
			for (ScoredDocument document : results) {
				System.out.println(document.getId());
				for (Field field : document.getFields()) {
					System.out.print(field.getName() + ":");
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
			Cursor nextCursor = results.getCursor();
		} catch (SearchException e) {
			if (StatusCode.TRANSIENT_ERROR.equals(e.getOperationResult()
					.getCode())) {
				// retry
			}
		}
	}

	public Results<ScoredDocument> findNoteDocuments(String queryString,
			int limit, Cursor cursor) {
		try {
			SortOptions sortOptions = SortOptions.newBuilder().addSortExpression(
							SortExpression.newBuilder()
									.setExpression("title")
									.setDirection(SortExpression.SortDirection.DESCENDING)
									.setDefaultValue("")).setLimit(1000).build();
			QueryOptions options = QueryOptions.newBuilder().setLimit(limit)
					.setFieldsToSnippet("content")
					.setFieldsToReturn("title", "content")
					.setSortOptions(sortOptions).setCursor(cursor).build();
			Query query = Query.newBuilder().setOptions(options)
					.build(queryString);
			return DocumentManager.getIndex().search(query);
		} catch (SearchException e) {
			return null;
		}
	}
}
