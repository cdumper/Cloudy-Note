package com.sid.cloudynote.client.sharing.view;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.dom.client.UListElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.Widget;
import com.sid.cloudynote.client.event.GroupsChangedEvent;
import com.sid.cloudynote.client.event.interfaces.IGroupsChangedHandler;
import com.sid.cloudynote.client.sharing.view.interfaces.IGroupView;
import com.sid.cloudynote.client.view.Container;
import com.sid.cloudynote.shared.Group;

public class GroupView extends Composite implements IGroupView,
		IGroupsChangedHandler {
	@UiField
	Container container;
	@UiField
	HTMLPanel content;
	@UiField
	Button publicButton;
	@UiField
	Button sharedButton;
	@UiField
	UListElement groupList;
	@UiField
	Style style;
	
	public interface Images extends ClientBundle, Tree.Resources {
		@Source("../../resources/images/group.png")
		ImageResource group();
	}
	private Images images;

	interface Style extends CssResource {
		String groupItem();

		String groupLabel();
	}

	public Container getContainer() {
		return container;
	}

	private Presenter presenter;

	private static GroupViewUiBinder uiBinder = GWT
			.create(GroupViewUiBinder.class);

	interface GroupViewUiBinder extends UiBinder<Widget, GroupView> {
	}

	@UiHandler("publicButton")
	void onPublicButtonClicked(ClickEvent e) {
		presenter.viewPublic();
	}

	@UiHandler("sharedButton")
	void onSharedButtonClicked(ClickEvent e) {
		presenter.viewShared();
	}

	public GroupView() {
		initWidget(uiBinder.createAndBindUi(this));
		images = GWT.create(Images.class);
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public Widget asWidget() {
		return this.content;
	}

	@Override
	public void setGroupList(List<Group> groups) {
		if (groups != null && groups.size() != 0) {
			while (groupList.hasChildNodes()) {
				groupList.removeChild(groupList.getFirstChild());
			}
			for (final Group g : groups) {
				LIElement li = Document.get().createLIElement();
				Element div = DOM.createDiv();
//				Element button = DOM.createButton();
//				button.setInnerText(g.getName());
//				button.addClassName(style.groupItem());
//				button.addClassName(".gwt-Button");
				DOM.sinkEvents(div, Event.ONCLICK);
				DOM.setEventListener(div, new EventListener() {
					public void onBrowserEvent(Event event) {
						presenter.onGroupItemSelected(g);
					}
				});
				div.setAttribute("style", "padding-top: 5px;cursor: pointer;");
//				div.setInnerHTML("<div style=\"display:inline;padding-left: 45px;\">"+AbstractImagePrototype.create(images.group()).getHTML()+button.getInnerHTML()+"</div>");
				div.setInnerHTML(AbstractImagePrototype.create(images.group()).getHTML()+g.getName());
//				div.appendChild(button);
//				div.insertFirst(new Image(images.group()).getElement());
				li.appendChild(div);
				groupList.appendChild(li);
			}
		}
	}

	@Override
	public void onGroupsChanged(GroupsChangedEvent event) {
		presenter.loadGroupList();
	}
}
