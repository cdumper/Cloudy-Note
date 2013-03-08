package com.sid.cloudynote.client.sharing.view;

import com.axeiya.gwtckeditor.client.CKEditor;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.sid.cloudynote.client.sharing.view.interfaces.ISharingNoteView;
import com.sid.cloudynote.client.view.Container;
import com.sid.cloudynote.shared.InfoNote;

public class SharingNoteView extends ResizeComposite implements
		ISharingNoteView {
	@UiField
	Container container;
	@UiField
	DeckPanel deckPanel;
	@UiField
	DockLayoutPanel editPanel;
	@UiField
	HorizontalPanel titlePanel;
	@UiField
	DockLayoutPanel viewPanel;
	@UiField
	HorizontalPanel propertyPanel;
	@UiField
	Label tagsLabel;
	@UiField
	Label createdTimeLabel;
	@UiField
	HorizontalPanel editPropertyPanel;
	@UiField
	HorizontalPanel editTitlePanel;
	@UiField
	VerticalPanel editNorthPanel;
	@UiField
	TextBox editTitle;
	@UiField
	HorizontalPanel editCenterPanel;
	@UiField
	CKEditor ckeditor;
	@UiField
	VerticalPanel viewNorthPanel;
	@UiField
	HorizontalPanel viewCenterPanel;
	@UiField
	HTML viewContent;
	@UiField
	Label titleLabel;
	@UiField
	Button editButton;
	@UiField
	TextBox editTags;

	private Presenter presenter;
	private InfoNote note;

	public InfoNote getNote() {
		return note;
	}

	public void setNote(InfoNote note) {
		this.note = note;
	}

	private static SharingNoteViewUiBinder uiBinder = GWT
			.create(SharingNoteViewUiBinder.class);

	interface SharingNoteViewUiBinder extends UiBinder<Widget, SharingNoteView> {
	}

	public SharingNoteView() {
		initWidget(uiBinder.createAndBindUi(this));
		editButton.addClickHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				presenter.startEditing(note);
			}
		});
		this.deckPanel.setAnimationEnabled(true);
		this.deckPanel.showWidget(0);
	}

	public void editNote(InfoNote note) {

	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	public Container getContainer() {
		return this.container;
	}

	@Override
	public Widget asWidget() {
		return this.deckPanel;
	}

	public void presentNote() {
		this.titleLabel.setText(this.note.getTitle());
		if (note.getTags() != null && note.getTags().size() != 0) {
			String tags = "Tags: ";
//			TODO show tags in sharingNoteView
//			for (Tag tag : note.getTags()) {
//				tags+=tag.getName();
//			}
			this.tagsLabel.setText(tags);
		} else {
			this.tagsLabel.setVisible(false);
		}
		this.createdTimeLabel.setText(this.note.getCreatedTime().toString());
		this.viewContent.setText(this.note.getContent());
		
		this.deckPanel.showWidget(0);
	}

	@Override
	public void editNote() {
		this.editTitle.setText(note.getTitle());
		if (note.getTags() != null && note.getTags().size() != 0) {
			String tags = "Tags: ";
			//TODO show tags in sharingNoteView
//			for (Tag tag : note.getTags()) {
//				tags+=tag.getName()+" ";
//			}
			this.editTags.setText(tags);
		} else {
			this.editTags.setText("Add tags");
		}
		this.ckeditor.setData(note.getContent());
		deckPanel.showWidget(1);
	}

	public void showAccessDeniedPanel() {
		final DialogBox dialog = new DialogBox();
		dialog.setText("Access Denied!");
		VerticalPanel content = new VerticalPanel();
		dialog.setWidget(content);
		
		content.add(new Label("You do not have the access to edit this note."));
		content.add(new Button("OK",new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				dialog.hide();
			}
		}));
		dialog.center();
	}
}
