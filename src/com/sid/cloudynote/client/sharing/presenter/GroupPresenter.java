package com.sid.cloudynote.client.sharing.presenter;

import java.util.Set;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;
import com.sid.cloudynote.client.AppController;
import com.sid.cloudynote.client.event.ViewGroupNotesEvent;
import com.sid.cloudynote.client.event.ViewPublicNotesEvent;
import com.sid.cloudynote.client.event.ViewSharedNotesEvent;
import com.sid.cloudynote.client.presenter.Presenter;
import com.sid.cloudynote.client.service.GroupService;
import com.sid.cloudynote.client.service.GroupServiceAsync;
import com.sid.cloudynote.client.sharing.view.interfaces.IGroupView;
import com.sid.cloudynote.shared.Group;

public class GroupPresenter implements Presenter, IGroupView.Presenter{
	private final HandlerManager eventBus;
	private IGroupView view;
	
	public GroupPresenter(IGroupView view, HandlerManager eventBus) {
		super();
		this.eventBus = eventBus;
		this.view = view;
		this.loadGroupList();
	}

	@Override
	public void viewPublic() {
		eventBus.fireEvent(new ViewPublicNotesEvent());
	}

	@Override
	public void viewShared() {
		eventBus.fireEvent(new ViewSharedNotesEvent(AppController.get().getLoginInfo()));
	}
	
	@Override
	public void loadGroupList() {
		final String email = AppController.get().getLoginInfo().getEmailAddress();
		GroupServiceAsync groupService = GWT.create(GroupService.class);
		groupService.getGroups(email, new AsyncCallback<Set<Group>>(){
			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Failed to load group list of user : " + email);
			}

			@Override
			public void onSuccess(Set<Group> result) {
				view.setGroupList(result);
			}
		});
	}

	public void onGroupItemSelected (Group group) {
		// TODO view notes in the group
		eventBus.fireEvent(new ViewGroupNotesEvent(group));
	}

	@Override
	public void go(HasWidgets container) {
		container.clear();
		container.add(view.asWidget());
	}

}
