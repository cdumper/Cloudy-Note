package com.sid.cloudynote.client.sharing.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;
import com.sid.cloudynote.client.sharing.view.interfaces.ISharingNoteView;
import com.sid.cloudynote.client.view.Container;
import com.sid.cloudynote.shared.InfoNote;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.axeiya.gwtckeditor.client.CKEditor;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Button;

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
	@UiField VerticalPanel editNorthPanel;
	@UiField TextBox editTitle;
	@UiField HorizontalPanel editCenterPanel;
	@UiField CKEditor ckeditor;
	@UiField VerticalPanel viewNorthPanel;
	@UiField HorizontalPanel viewCenterPanel;
	@UiField HTML viewContent;
	@UiField Label titleLabel;
	@UiField Button editButton;

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
		this.deckPanel.setVisible(false);
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
		// TODO viewSharedNote
		this.deckPanel.setVisible(true);
		this.deckPanel.showWidget(0);
	}

	@Override
	public void editNote() {
		// TODO editSharedNote
		this.deckPanel.setVisible(true);
		deckPanel.showWidget(1);
	}
}
