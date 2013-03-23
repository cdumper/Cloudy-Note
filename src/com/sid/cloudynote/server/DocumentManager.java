package com.sid.cloudynote.server;

import com.google.appengine.api.search.Index;
import com.google.appengine.api.search.IndexSpec;
import com.google.appengine.api.search.SearchServiceFactory;

public class DocumentManager {
	public static Index getIndex(){
		IndexSpec indexSpec = IndexSpec.newBuilder().setName("myindex").build();
	    return SearchServiceFactory.getSearchService().getIndex(indexSpec);
	}
}
