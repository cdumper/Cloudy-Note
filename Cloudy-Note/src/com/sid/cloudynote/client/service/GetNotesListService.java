package com.sid.cloudynote.client.service;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.sid.cloudynote.shared.InfoNote;
/**
 * NOTE: This service is not in use!! May be needed to extend in the future
 * @author sid
 *
 */
@RemoteServiceRelativePath("getNotesList")
public interface GetNotesListService extends RemoteService{
	List<InfoNote> getNoteList();
}
