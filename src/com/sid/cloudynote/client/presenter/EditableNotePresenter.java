package com.sid.cloudynote.client.presenter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.appengine.api.datastore.Key;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.sid.cloudynote.client.AppController;
import com.sid.cloudynote.client.DataManager;
import com.sid.cloudynote.client.event.EditNoteDoneEvent;
import com.sid.cloudynote.client.event.NoteChangedEvent;
import com.sid.cloudynote.client.event.NotebookChangedEvent;
import com.sid.cloudynote.client.event.TagChangedEvent;
import com.sid.cloudynote.client.service.BlobService;
import com.sid.cloudynote.client.service.BlobServiceAsync;
import com.sid.cloudynote.client.service.InfoNoteService;
import com.sid.cloudynote.client.service.InfoNoteServiceAsync;
import com.sid.cloudynote.client.service.TagService;
import com.sid.cloudynote.client.service.TagServiceAsync;
import com.sid.cloudynote.client.service.UploadService;
import com.sid.cloudynote.client.service.UploadServiceAsync;
import com.sid.cloudynote.client.view.EditableNoteView;
import com.sid.cloudynote.client.view.interfaces.IEditableNoteView;
import com.sid.cloudynote.shared.InfoNote;
import com.sid.cloudynote.shared.Notebook;
import com.sid.cloudynote.shared.Tag;

public class EditableNotePresenter implements Presenter, IEditableNoteView.Presenter{
	private IEditableNoteView view;
	private HandlerManager eventBus;
	
	
	public EditableNotePresenter(IEditableNoteView view, HandlerManager eventBus) {
		super();
		this.view = view;
		this.eventBus = eventBus;
		eventBus.addHandler(TagChangedEvent.TYPE, (EditableNoteView)view);
	}

	@Override
	public void loadNotebooks() {
		Map<Key,Notebook> notebookMap = DataManager.getNotebooks();
		view.setNotebookMap(notebookMap);
	}

	@Override
	public void createNewNote(final InfoNote note,final Map<Tag,Key> tags) {
		final List<Tag> tagsToCreate = new ArrayList<Tag>();
		final List<Tag> tagsCreated = new ArrayList<Tag>();
		for(Entry<Tag,Key> entry : tags.entrySet()) {
			if(entry.getValue()==null){
				tagsToCreate.add(entry.getKey());
			} else {
				tagsCreated.add(entry.getKey());
			}
		}
		TagServiceAsync tagService = GWT.create(TagService.class);
		tagService.createTags(tagsToCreate, new AsyncCallback<List<Tag>>(){

			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Failed to create tags");
			}

			@Override
			public void onSuccess(List<Tag> result) {
				eventBus.fireEvent(new TagChangedEvent());
				tagsCreated.addAll(result);
				List<Key> keys = new ArrayList<Key>();
				for(Tag tag : tagsCreated){
					keys.add(tag.getKey());
				}
				note.setTags(keys);
				
				InfoNoteServiceAsync noteService = (InfoNoteServiceAsync) GWT
						.create(InfoNoteService.class);
				noteService.add(note, new AsyncCallback<Void>() {
					@Override
					public void onFailure(Throwable caught) {
						GWT.log("falied! getNotesList");
						caught.printStackTrace();
					}
					
					@Override
					public void onSuccess(Void result) {
						eventBus.fireEvent(new NoteChangedEvent(DataManager
								.getCurrentNotebook()));
						eventBus.fireEvent(new NotebookChangedEvent());
						eventBus.fireEvent(new EditNoteDoneEvent());
						GWT.log("New InfoNote added successfully!");
					}
				});
			}
		});
	}

	@Override
	public void moveNote(final InfoNote note, final Notebook notebook,final Map<Tag,Key> tags) {
		final List<Tag> tagsToCreate = new ArrayList<Tag>();
		final List<Tag> tagsCreated = new ArrayList<Tag>();
		for(Entry<Tag,Key> entry : tags.entrySet()) {
			if(entry.getValue()==null){
				tagsToCreate.add(entry.getKey());
			} else {
				tagsCreated.add(entry.getKey());
			}
		}
		TagServiceAsync tagService = GWT.create(TagService.class);
		tagService.createTags(tagsToCreate, new AsyncCallback<List<Tag>>(){

			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Failed to create tags");
			}

			@Override
			public void onSuccess(List<Tag> result) {
				eventBus.fireEvent(new TagChangedEvent());
				tagsCreated.addAll(result);
				List<Key> keys = new ArrayList<Key>();
				for(Tag tag : tagsCreated){
					keys.add(tag.getKey());
				}
				note.setTags(keys);
				InfoNoteServiceAsync service = (InfoNoteServiceAsync) GWT
						.create(InfoNoteService.class);
				service.moveNoteTo(note, notebook, new AsyncCallback<Void>() {
					@Override
					public void onFailure(Throwable caught) {
						GWT.log("update note failed");
						caught.printStackTrace();
					}
					
					@Override
					public void onSuccess(Void result) {
						eventBus.fireEvent(new NoteChangedEvent(DataManager
								.getCurrentNotebook()));
						eventBus.fireEvent(new EditNoteDoneEvent());
						GWT.log("Note updated successfully!");
					}
				});
			}
		});
	}

	@Override
	public void updateNote(final InfoNote note,final Map<Tag,Key> tags) {
		final List<Tag> tagsToCreate = new ArrayList<Tag>();
		final List<Tag> tagsCreated = new ArrayList<Tag>();
		for(Entry<Tag,Key> entry : tags.entrySet()) {
			if(entry.getValue()==null){
				tagsToCreate.add(entry.getKey());
			} else {
				tagsCreated.add(entry.getKey());
			}
		}
		TagServiceAsync tagService = GWT.create(TagService.class);
		tagService.createTags(tagsToCreate, new AsyncCallback<List<Tag>>(){

			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Failed to create tags");
			}

			@Override
			public void onSuccess(List<Tag> result) {
				eventBus.fireEvent(new TagChangedEvent());
				tagsCreated.addAll(result);
				List<Key> keys = new ArrayList<Key>();
				for(Tag tag : tagsCreated){
					keys.add(tag.getKey());
				}
				note.setTags(keys);
				InfoNoteServiceAsync service = (InfoNoteServiceAsync) GWT
						.create(InfoNoteService.class);
				service.modify(note, new AsyncCallback<Void>() {
					@Override
					public void onFailure(Throwable caught) {
						GWT.log("update note failed");
						caught.printStackTrace();
					}
					
					@Override
					public void onSuccess(Void result) {
						eventBus.fireEvent(new NoteChangedEvent(DataManager
								.getCurrentNotebook()));
						eventBus.fireEvent(new EditNoteDoneEvent());
						GWT.log("Note updated successfully!");
					}
				});
			}
		});
	}

	@Override
	public void addAttachment(final String fileName, final String key) {
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
				view.presentAttachmentLink(fileName, key);
			}
		});
	}

	@Override
	public void go(HasWidgets container) {
		container.clear();
		container.add(view.asWidget());
	}

	@Override
	public void presentAttachmentLinks(final List<String> keys) {
		BlobServiceAsync service = GWT.create(BlobService.class);
		service.getBlobFileName(keys, new AsyncCallback<List<String>>() {
			@Override
			public void onFailure(Throwable caught) {
				GWT.log("get blob file name failed!");
			}

			@Override
			public void onSuccess(List<String> result) {
				for (int i=0;i<result.size();i++) {
					view.presentAttachmentLink(result.get(i),keys.get(i));
				}
			}
		});
	}

	@Override
	public void onClickAttach() {
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

	@Override
	public void loadAllTags() {
		TagServiceAsync service = GWT.create(TagService.class);
		service.getTags(AppController.get().getLoginInfo().getEmail(),new AsyncCallback<List<Tag>>(){

			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Failed to load tag list");
			}

			@Override
			public void onSuccess(List<Tag> result) {
				Map<Key,Tag> tags = new HashMap<Key,Tag>();
				for(Tag tag : result) {
					tags.put(tag.getKey(), tag);
				}
				view.setAllTagsList(tags);
			}
			
		});
	}
}
