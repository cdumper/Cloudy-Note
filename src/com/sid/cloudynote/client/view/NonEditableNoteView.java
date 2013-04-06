package com.sid.cloudynote.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DecoratedPopupPanel;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.sid.cloudynote.client.service.ShareNoteService;
import com.sid.cloudynote.client.service.ShareNoteServiceAsync;
import com.sid.cloudynote.client.view.interfaces.INonEditableNoteView;
import com.sid.cloudynote.shared.InfoNote;

public class NonEditableNoteView extends ResizeComposite implements
		INonEditableNoteView {

	@UiField
	Container container;
	@UiField
	DockLayoutPanel content;
	@UiField
	Label title;
	@UiField
	Label notebook;
	@UiField
	Label datetime;
	@UiField
	HTML noteContent;
	@UiField
	Button editButton;
	@UiField
	Button shareButton;
	@UiField
	Button deleteButton;
	@UiField
	Style style;
	Resources resources;

	@UiHandler("editButton")
	void onClickEdit(ClickEvent e) {
		presenter.onClickEdit(this.note);
	}

	@UiHandler("shareButton")
	void onClickShare(ClickEvent e) {
		final DecoratedPopupPanel popup = new DecoratedPopupPanel();
		popup.setStyleName(style.shareDropdown());
		HTMLPanel content = new HTMLPanel("");
		popup.setWidget(content);
		popup.setAutoHideEnabled(true);
		Element email = DOM.createDiv();
		DOM.sinkEvents(email, Event.ONCLICK);
		DOM.setEventListener(email, new EventListener() {
			public void onBrowserEvent(Event event) {
				popup.hide();
				final DialogBox dialog = new DialogBox();
				dialog.setText("Email Note \""+note.getTitle()+"\"");
				
				HTMLPanel content = new HTMLPanel("");
				dialog.setWidget(content);
				
				final TextBox email = new TextBox();
				email.setWidth("300px");
				final TextArea message = new TextArea();
				message.setSize("300px", "100px");
				
				HTMLPanel buttonPanel = new HTMLPanel("");
				Button cancel = new Button("Cancel", new ClickHandler(){

					@Override
					public void onClick(ClickEvent event) {
						dialog.hide();
					}
					
				});
				
				Button send = new Button("Send", new ClickHandler(){

					@Override
					public void onClick(ClickEvent event) {
						ShareNoteServiceAsync service = GWT.create(ShareNoteService.class);
						service.sendByEmail(email.getText(), message.getText(), note, new AsyncCallback<String>(){

							@Override
							public void onFailure(Throwable caught) {
								GWT.log("Failed to email note:"+note.getTitle()+ " to "+email.getText());
							}

							@Override
							public void onSuccess(String result) {
								GWT.log("Successfully emailed note:"+note.getTitle()+ " to "+email.getText());
								dialog.hide();
							}
						});
					}
				});
				buttonPanel.add(cancel);
				buttonPanel.add(send);
				
				content.add(new Label("Send to:"));
				content.add(email);
				content.add(new Label("Message:"));
				content.add(message);
				content.add(buttonPanel);
				dialog.center();
			}
		});
		email.setClassName(style.item());
		email.setInnerHTML(AbstractImagePrototype.create(resources.email())
				.getHTML() + "<span>Email</span>");
		content.getElement().appendChild(email);

		Element facebook = DOM.createDiv();
		DOM.sinkEvents(facebook, Event.ONCLICK);
		DOM.setEventListener(facebook, new EventListener() {
			public void onBrowserEvent(Event event) {
				popup.hide();
				// TODO post to facebook
				showNotImplementedMessage();
			}
		});
		facebook.setClassName(style.item());
		facebook.setInnerHTML(AbstractImagePrototype.create(
				resources.facebook()).getHTML()
				+ "<span>Facebook</span>");
		content.getElement().appendChild(facebook);

		Element twitter = DOM.createDiv();
		DOM.sinkEvents(twitter, Event.ONCLICK);
		DOM.setEventListener(twitter, new EventListener() {
			public void onBrowserEvent(Event event) {
				popup.hide();
				// TODO post to twitter
				showNotImplementedMessage();
			}
		});
		twitter.setClassName(style.item());
		twitter.setInnerHTML(AbstractImagePrototype.create(resources.twitter())
				.getHTML() + "<span>Twitter</span>");
		content.getElement().appendChild(twitter);

		popup.showRelativeTo(this.shareButton);
	}

	protected void showNotImplementedMessage() {
		final DecoratedPopupPanel popup = new DecoratedPopupPanel();
		Timer timer = new Timer() {
			public void run() {
				popup.hide();
			}
		};
		HTMLPanel content = new HTMLPanel("");
		popup.setWidget(content);
		popup.setGlassEnabled(true);
		popup.setAutoHideEnabled(true);
		Label message = new Label(
				"We're sorry. This feature is still under development...");
		content.add(message);
		popup.center();
		timer.schedule(1500);
	}

	@UiHandler("deleteButton")
	void onClickDelete(ClickEvent e) {
		presenter.deleteNote(this.note);
	}

	public interface Style extends CssResource {
		String item();
		String shareDropdown();
	}

	interface Resources extends ClientBundle {
		@Source("../resources/images/email.png")
		ImageResource email();

		@Source("../resources/images/share.png")
		ImageResource share();

		@Source("../resources/images/share.png")
		ImageResource facebook();

		@Source("../resources/images/share.png")
		ImageResource twitter();

	}

	private Presenter presenter;
	private InfoNote note;

	public InfoNote getNote() {
		return note;
	}

	@Override
	public void setNote(InfoNote note) {
		this.note = note;
	}

	private static NonEditPanelUiBinder uiBinder = GWT
			.create(NonEditPanelUiBinder.class);

	interface NonEditPanelUiBinder extends
			UiBinder<Widget, NonEditableNoteView> {
	}

	public NonEditableNoteView() {
		initWidget(uiBinder.createAndBindUi(this));
		resources = GWT.create(Resources.class);
	}

	public NonEditableNoteView(InfoNote note) {
		initWidget(uiBinder.createAndBindUi(this));
		resources = GWT.create(Resources.class);
		this.note = note;
		this.title.setText(note.getTitle());
		this.datetime.setText("Created Time: " + note.getCreatedTime()
				+ "	Last Modified Time: " + note.getLastModifiedTime());
		this.notebook.setText(note.getNotebook().getName());
		this.noteContent.setHTML(note.getContent().getValue());
	}

	public void presentNote() {
		if (note != null) {
			this.editButton.setVisible(true);
			this.shareButton.setVisible(true);
			this.deleteButton.setVisible(true);
			title.setText(note.getTitle());
			datetime.setText("Created Time: " + note.getCreatedTime()
					+ "	Last Modified Time: " + note.getLastModifiedTime());
			notebook.setText(note.getNotebook().getName());
			noteContent.setHTML(note.getContent().getValue());
			noteContent.setWidth("90%");
		} else {
			this.editButton.setVisible(false);
			this.shareButton.setVisible(false);
			this.deleteButton.setVisible(false);
			title.setText("");
			datetime.setText("");
			notebook.setText("");
			noteContent.setHTML("");
			noteContent.setWidth("90%");
		}
	}

	@Override
	public void presentAttachmentLink(String fileName, String key) {
		// TODO presentAttachmentLink

	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public Widget asWidget() {
		return this.content;
	}

	public Container getContainer() {
		return this.container;
	}
}
