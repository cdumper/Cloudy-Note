package com.sid.cloudynote.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;
import com.sid.cloudynote.client.view.interfaces.INonEditableNoteView;
import com.sid.cloudynote.shared.InfoNote;

public class NonEditableNoteView extends ResizeComposite implements INonEditableNoteView{
	
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
	
	@UiHandler("editButton")
	void onClickEdit(ClickEvent e){
		presenter.onClickEdit(this.note);
	}
	
	@UiHandler("shareButton")
	void onClickShare(ClickEvent e){
		//TODO share note through other channel
	}
	
	@UiHandler("deleteButton")
	void onClickDelete(ClickEvent e){
		presenter.deleteNote(this.note);
	}

	public interface Style extends CssResource {
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
	}

	public NonEditableNoteView(InfoNote note) {
		initWidget(uiBinder.createAndBindUi(this));
		this.note = note;
		this.title.setText(note.getTitle());
		this.datetime.setText("Created Time: "+note.getCreatedTime()+"	Last Modified Time: "+note.getLastModifiedTime());
		this.notebook.setText(note.getNotebook().getName());
		this.noteContent.setHTML(note.getContent());
	}

	public NonEditableNoteView(String firstName) {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public void presentNote() {
		if (note != null) {
			this.editButton.setVisible(true);
			this.shareButton.setVisible(true);
			this.deleteButton.setVisible(true);
			title.setText(note.getTitle());
			datetime.setText("Created Time: "+note.getCreatedTime()+"	Last Modified Time: "+note.getLastModifiedTime());
			notebook.setText(note.getNotebook().getName());
			noteContent.setHTML(note.getContent());
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
	public Widget asWidget(){
		return this.content;
	}
	
	public Container getContainer(){
		return this.container;
	}
}
