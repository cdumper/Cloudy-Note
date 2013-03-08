package com.sid.cloudynote.client.view;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.axeiya.gwtckeditor.client.CKEditor;
import com.google.appengine.api.datastore.Key;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.sid.cloudynote.client.AppController;
import com.sid.cloudynote.client.DataManager;
import com.sid.cloudynote.client.event.TagChangedEvent;
import com.sid.cloudynote.client.event.interfaces.ITagChangedHandler;
import com.sid.cloudynote.client.view.interfaces.IEditableNoteView;
import com.sid.cloudynote.shared.InfoNote;
import com.sid.cloudynote.shared.Notebook;
import com.sid.cloudynote.shared.Tag;

public class EditableNoteView extends ResizeComposite implements
		IEditableNoteView, ITagChangedHandler {

	private static EditPanelUiBinder uiBinder = GWT
			.create(EditPanelUiBinder.class);

	@UiField
	Container container;
	@UiField
	DockLayoutPanel content;
	@UiField
	VerticalPanel topPanel;
	@UiField
	Button doneButton;
	@UiField
	HTMLPanel tagsEditPanel;
	@UiField
	SuggestBox tagInput;
	@UiField
	DivElement tagsEditLozengePanel;
	@UiField
	CKEditor ckeditor;
	@UiField
	TextBox title;
	@UiField
	ListBox notebook;
	@UiField
	Anchor attach;
	@UiField
	public Style style;

	public interface Style extends CssResource {
		String tagsEditLozenge();

		String tagsEditIcon();
	}

	private static final String TAGINPUT_DEFAULT = "Add tags seperated by comma";
	private boolean isNew;
	private Presenter presenter;
	private Map<Key, Notebook> notebookMap;
	private List<Key> notebookList = new ArrayList<Key>();
	private Map<Key,Tag> allTags = new HashMap<Key,Tag>();
	private Map<Key,Tag> tags = new HashMap<Key,Tag>();
	private MultiWordSuggestOracle oracle = new MultiWordSuggestOracle();

	interface EditPanelUiBinder extends UiBinder<Widget, EditableNoteView> {
	}

	public EditableNoteView() {
		initWidget(uiBinder.createAndBindUi(this));
		this.tagInput.setText(TAGINPUT_DEFAULT);

		this.tagInput.addSelectionHandler(new SelectionHandler<Suggestion>() {

			@Override
			public void onSelection(SelectionEvent<Suggestion> event) {
				addTag(event.getSelectedItem().getReplacementString());
			}

		});

		this.tagInput.addKeyPressHandler(new KeyPressHandler() {

			@Override
			public void onKeyPress(KeyPressEvent event) {
				if (',' == event.getCharCode()) {
					event.getNativeEvent().preventDefault();
					if (!"".equals(tagInput.getText().split(",")[0].trim()))
						addTag(tagInput.getText().split(",")[0]);
				}
			}

		});

		this.tagInput.getTextBox().addFocusHandler(new FocusHandler() {

			@Override
			public void onFocus(FocusEvent event) {
				if (tagInput.getText().equals(TAGINPUT_DEFAULT)) {
					tagInput.setText("");
				}
			}

		});

		this.tagInput.getTextBox().addBlurHandler(new BlurHandler() {

			@Override
			public void onBlur(BlurEvent event) {
				if (tagInput.getText().trim().equals("")) {
					tagInput.setText(TAGINPUT_DEFAULT);
				}
			}

		});
		oracle = (MultiWordSuggestOracle) this.tagInput.getSuggestOracle();
	}

	public boolean isNew() {
		return isNew;
	}

	public void setNew(boolean isNew) {
		this.isNew = isNew;
	}

	@UiHandler("doneButton")
	void onClickDone(ClickEvent e) {
		if (this.isNew) {
			presenter.createNewNote(this.getInfoNote(),this.getTags());
		} else {
			InfoNote note = DataManager.getCurrentNote();
			note.setTitle(this.getInfoNote().getTitle());
			note.setContent(this.getInfoNote().getContent());
			note.setTags(this.getInfoNote().getTags());
			if (!note.getNotebook().getKey()
					.equals(this.getInfoNote().getNotebook().getKey())) {
				presenter.moveNote(note, this.getInfoNote().getNotebook(),this.getTags());
			} else {
				presenter.updateNote(note,this.getTags());
			}
		}
	}

	@Override
	public void newNote() {
		title.setText("Untitled");
		setSelectedNotebook(DataManager.getCurrentNotebookKey());
		ckeditor.setData("Content...");
	}

	@Override
	public void presentNote(InfoNote note) {
		if (note != null) {
			title.setText(note.getTitle());
			setSelectedNotebook(DataManager.getCurrentNotebookKey());
			ckeditor.setData(note.getContent());
			if (note.getAttachments() != null) {
				presentAttachmentLinks(note.getAttachments());
			}
			if(note.getTags()!=null && note.getTags().size()!=0){
				for(Key key : note.getTags()) {
					this.tags.put(key, this.allTags.get(key));
				}
			}
			this.presentTags();
		}
	}

	public InfoNote getInfoNote() {
		InfoNote note = new InfoNote(getSelectedNotebook(), title.getText(),
				ckeditor.getData());
		return note;
	}
	
	public Map<Key,Tag> getTags() {
		return this.tags;
	}

	// private String removeHTMLTags(String data) {
	// return data.replaceAll("\\<.*?>", "");
	// }

	public void setSelectedNotebook(Key key) {
		for (int i = 0; i < notebookList.size(); i++) {
			if (notebookList.get(i).equals(key)) {
				notebook.setSelectedIndex(i);
				break;
			}
		}
	}

	private void loadNotebooks() {
		presenter.loadNotebooks();
	}

	private void loadAllTags() {
		presenter.loadAllTags();
	}

	@Override
	public void setAllTagsList(Map<Key,Tag> tags) {
		this.allTags = tags;
		if (allTags != null && allTags.size() != 0) {
			for (Tag tag : tags.values()) {
				this.oracle.add(tag.getName());
			}
		}
	}

	public Notebook getSelectedNotebook() {
		return notebookMap.get(notebookList.get(notebook.getSelectedIndex()));
	}

	@UiHandler("attach")
	void onClickAttach(ClickEvent e) {
		presenter.onClickAttach();
	}

	private void presentAttachmentLinks(final List<String> keys) {
		presenter.presentAttachmentLinks(keys);
	}

	@Override
	public void presentAttachmentLink(String fileName, String key) {
		if (key == null)
			key = "null";
		Anchor file = new Anchor();
		file.setText(fileName);
		file.setHref("/cloudy_note/serve?blob-key=" + key);
		topPanel.add(file);
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
		this.loadNotebooks();
		this.loadAllTags();
	}

	@Override
	public Widget asWidget() {
		return this.content;
	}

	public Container getContainer() {
		return this.container;
	}

	@Override
	public void setNotebookMap(Map<Key, Notebook> notebookMap) {
		this.notebookMap = notebookMap;
		for (Notebook nb : notebookMap.values()) {
			notebookList.add(nb.getKey());
			notebook.addItem(nb.getName());
		}
		setSelectedNotebook(DataManager.getCurrentNotebookKey());
	}

	private void addTag(String tagName) {
		boolean exist = false;
		Tag tag = new Tag(tagName);
		tag.setCreatedTime(new Date());
		tag.setUser(AppController.get().getLoginInfo().getEmail());

		for (Tag t : this.tags.values()) {
			if (tagName.equals(t.getName())) {
				exist = true;
			}
		}

		for (Tag t : this.allTags.values()) {
			if (tagName.equals(t.getName())) {
				tag = t;
			}
		}

		if (this.tags == null) {
			this.tags = new HashMap<Key,Tag>();
		}

		if (!exist) {
			this.tags.put(null,tag);
		}
		this.tagInput.setText("");
		this.presentTags();
	}

	public void presentTags() {
		if (this.tags != null && this.tags.size() != 0) {
			this.tagsEditLozengePanel.setInnerText("");
			for (final Tag tag : this.tags.values()) {
				final Element div = DOM.createDiv();
				div.addClassName(style.tagsEditLozenge());
				Element span = DOM.createSpan();
				span.setInnerText(tag.getName());
				Image image = new Image();
				image.addStyleName(style.tagsEditIcon());
				DOM.sinkEvents(image.getElement(), Event.ONCLICK);
				DOM.setEventListener(image.getElement(), new EventListener() {
					public void onBrowserEvent(Event event) {
						tags.remove(tag.getKey());
						div.removeFromParent();
					}
				});
				div.appendChild(span);
				div.appendChild(image.getElement());

				this.tagsEditLozengePanel.appendChild(div);
			}
			this.tagsEditLozengePanel.appendChild(this.tagInput.getElement());
			this.tagInput.setFocus(true);
		}
	}

	@Override
	public void onTagChanged(TagChangedEvent event) {
		this.loadAllTags();
	}
}