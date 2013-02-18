package com.sid.cloudynote.client.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.axeiya.gwtckeditor.client.CKEditor;
import com.google.appengine.api.datastore.Key;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.sid.cloudynote.client.DataManager;
import com.sid.cloudynote.client.service.BlobService;
import com.sid.cloudynote.client.service.BlobServiceAsync;
import com.sid.cloudynote.client.service.InfoNoteService;
import com.sid.cloudynote.client.service.InfoNoteServiceAsync;
import com.sid.cloudynote.client.service.UploadService;
import com.sid.cloudynote.client.service.UploadServiceAsync;
import com.sid.cloudynote.shared.InfoNote;
import com.sid.cloudynote.shared.Notebook;

public class EditableNoteView extends ResizeComposite {

	private static EditPanelUiBinder uiBinder = GWT
			.create(EditPanelUiBinder.class);

	@UiField
	VerticalPanel topPanel;
	@UiField
	CKEditor ckeditor;
	@UiField
	TextBox title;
	@UiField
	ListBox notebook;
	@UiField
	Anchor attach;

	private Map<Key, Notebook> notebookMap;
	private List<Key> notebookList = new ArrayList<Key>();

	interface EditPanelUiBinder extends UiBinder<Widget, EditableNoteView> {
	}

	public EditableNoteView() {
		initWidget(uiBinder.createAndBindUi(this));
		this.loadNotebooks();
		// this.presentNote(DataManager.getCurrentNote());
	}

	public void newNote() {
		title.setText("Untitled");
		setSelectedNotebook(DataManager.getCurrentNotebookKey());
		ckeditor.setData("Content...");
	}

	public void presentNote(InfoNote note) {
		if (note != null) {
			title.setText(note.getTitle());
			setSelectedNotebook(DataManager.getCurrentNotebookKey());
			ckeditor.setData(note.getContent());
			if (note.getAttachments() != null) {
				presentAttachmentLinks(note.getAttachments());
			}
		}
	}

	public InfoNote getInfoNote() {
		return new InfoNote(getSelectedNotebook(), title.getText(),
				ckeditor.getData());
	}

	private String removeHTMLTags(String data) {
		return data.replaceAll("\\<.*?>", "");
	}

	public void setSelectedNotebook(Key key) {
		for (int i = 0; i < notebookList.size(); i++) {
			if (notebookList.get(i).equals(key)) {
				notebook.setSelectedIndex(i);
				break;
			}
		}
	}

	private void loadNotebooks() {
		notebookMap = DataManager.getNotebooks();
		for (Notebook nb : notebookMap.values()) {
			notebookList.add(nb.getKey());
			notebook.addItem(nb.getName());
		}
		setSelectedNotebook(DataManager.getCurrentNotebookKey());
	}

	public Notebook getSelectedNotebook() {
		return notebookMap.get(notebookList.get(notebook.getSelectedIndex()));
	}

	@UiHandler("attach")
	void onClick(ClickEvent e) {
		final DialogBox dialog = new DialogBox();
		UploadServiceAsync uploadService = (UploadServiceAsync) GWT
				.create(UploadService.class);
		uploadService.createUploadURL(new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				GWT.log("failed to create upload url");
			}

			@Override
			public void onSuccess(String url) {
				dialog.setTitle("Attach files");
				createUploadDialog(dialog, url);
				dialog.center();
			}
		});
	}

	private void createUploadDialog(final DialogBox dialog, String url) {
		final FormPanel form = new FormPanel();
		form.setEncoding(FormPanel.ENCODING_MULTIPART);
		form.setMethod(FormPanel.METHOD_POST);
		form.setAction(url);

		VerticalPanel panel = new VerticalPanel();
		panel.setSpacing(10);
		form.setWidget(panel);

		final FileUpload upload = new FileUpload();
		upload.setName("myFile");
		panel.add(upload);

		HorizontalPanel buttonPanel = new HorizontalPanel();
		panel.add(buttonPanel);

		buttonPanel.add(new Button("Cancel", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				dialog.hide();
			}
		}));

		buttonPanel.add(new Button("Submit", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				form.submit();
			}
		}));

		form.addSubmitCompleteHandler(new FormPanel.SubmitCompleteHandler() {
			@Override
			public void onSubmitComplete(SubmitCompleteEvent event) {
				addAttachment(upload.getFilename(), event.getResults());
				dialog.hide();
			}
		});
		dialog.add(form);
	}

	private void addAttachment(String fileName, String key) {
		addAttachmentToNote(fileName, key);
	}

	private void addAttachmentToNote(final String fileName, final String key) {
		InfoNote note = DataManager.getCurrentNote();
		note.getAttachments().add(key);

		InfoNoteServiceAsync service = (InfoNoteServiceAsync) GWT
				.create(InfoNoteService.class);
		service.modify(note, new AsyncCallback<Void>() {
			@Override
			public void onFailure(Throwable caught) {
				GWT.log("failed to add attachment");
			}

			@Override
			public void onSuccess(Void result) {
				GWT.log("attachment added successfully");
				presentAttachmentLink(fileName, key);
			}
		});
	}

	private void presentAttachmentLinks(final List<String> keys) {
		BlobServiceAsync service = GWT.create(BlobService.class);
		service.getBlobFileName(keys, new AsyncCallback<List<String>>() {
			@Override
			public void onFailure(Throwable caught) {
				GWT.log("get blob file name failed!");
			}

			@Override
			public void onSuccess(List<String> result) {
				for (int i=0;i<result.size();i++) {
					presentAttachmentLink(result.get(i),keys.get(i));
				}
			}
		});
	}

	private void presentAttachmentLink(String fileName, String key) {
		if (key == null)
			key = "null";
		Anchor file = new Anchor();
		file.setText(fileName);
		file.setHref("/cloudy_note/serve?blob-key=" + key);
		topPanel.add(file);
	}
}