package com.sid.cloudynote.client.sharing.view;

import java.util.List;

import com.axeiya.gwtckeditor.client.CKConfig;
import com.axeiya.gwtckeditor.client.CKConfig.PRESET_TOOLBAR;
import com.axeiya.gwtckeditor.client.CKConfig.TOOLBAR_OPTIONS;
import com.axeiya.gwtckeditor.client.CKEditor;
import com.axeiya.gwtckeditor.client.Toolbar;
import com.axeiya.gwtckeditor.client.ToolbarLine;
import com.google.appengine.api.datastore.Text;
import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.dom.client.UListElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.HasKeyboardPagingPolicy.KeyboardPagingPolicy;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import com.sid.cloudynote.client.event.GroupsChangedEvent;
import com.sid.cloudynote.client.event.interfaces.IGroupsChangedHandler;
import com.sid.cloudynote.client.sharing.view.interfaces.ISharingView;
import com.sid.cloudynote.client.view.Container;
import com.sid.cloudynote.shared.Group;
import com.sid.cloudynote.shared.InfoNote;

public class SharingView extends Composite implements ISharingView,
		IGroupsChangedHandler {
	private Presenter presenter;
	private static SharingViewUiBinder uiBinder = GWT
			.create(SharingViewUiBinder.class);

	interface SharingViewUiBinder extends UiBinder<Widget, SharingView> {
	}

	@UiField
	Container container;
	@UiField
	DockLayoutPanel content;
	@UiField
	HTMLPanel searchPanel;
	@UiField
	HTMLPanel searchField;
	@UiField
	TextBox searchBox;
	@UiField
	Button searchButton;
	@UiField
	ListBox sortBy;
	@UiField
	ListBox view;
	@UiField
	ListBox settings;
	@UiField
	HTMLPanel groupPanel;
	@UiField
	Button publicButton;
	@UiField
	Button sharedButton;
	@UiField
	UListElement groupList;

	// note list panel
	@UiField
	HTMLPanel noteListPanel;

	// main note editing panel
	@UiField
	DeckPanel notePanel;
	@UiField
	DockLayoutPanel editPanel;
	@UiField
	HTMLPanel titlePanel;
	@UiField
	DockLayoutPanel viewPanel;
	@UiField
	Anchor userLink;
	// @UiField
	// Label createdTimeLabel;
	@UiField
	HTMLPanel editTitlePanel;
	@UiField
	TextBox editTitle;
	@UiField
	HTMLPanel editContent;
	@UiField
	ScrollPanel viewCenterPanel;
	@UiField
	HTML viewContent;
	@UiField
	Label titleLabel;
	@UiField
	Button editButton;
	@UiField
	Button doneButton;
	CKEditor ckeditor;
	@UiField
	Style style;

	@UiHandler("publicButton")
	void onPublicButtonClicked(ClickEvent e) {
		presenter.viewPublicNotes();
	}

	@UiHandler("sharedButton")
	void onSharedButtonClicked(ClickEvent e) {
		presenter.viewSharedWithMeNotes();
	}

	@UiHandler("editButton")
	void onClickEdit(ClickEvent e) {
		presenter.onClickEdit();
	}

	@UiHandler("doneButton")
	void onClickDone(ClickEvent e) {
		presenter.onClickDone();
	}

	interface Style extends CssResource {
		String groupItem();

		String groupLabel();
	}

	public interface Images extends ClientBundle, Tree.Resources {
		@Source("../../resources/images/group.png")
		ImageResource group();
	}

	private Images images;
	private CellList<InfoNote> notesList;
	private NoteCell noteCell;
	private ListDataProvider<InfoNote> dataProvider = new ListDataProvider<InfoNote>();
	public static final ProvidesKey<InfoNote> KEY_PROVIDER = new ProvidesKey<InfoNote>() {
		@Override
		public Object getKey(InfoNote InfoNote) {
			return InfoNote == null ? null : InfoNote.getKey();
		}
	};
	private SingleSelectionModel<InfoNote> selectionModel;

	class NoteCell extends AbstractCell<InfoNote> {
		@Override
		public void render(Context context, final InfoNote note,
				SafeHtmlBuilder sb) {
			if (note == null) {
				return;
			}

			sb.appendHtmlConstant("<table width=\"100%\" style='border-bottom:1px solid #E6E6E6;'>");

			sb.appendHtmlConstant("<tr><th colspan=\"2\" height=\"15px\">");
			sb.appendHtmlConstant(note.getTitle());
			sb.appendHtmlConstant("</div></th></tr>");

			sb.appendHtmlConstant("<tr><td colspan=\"2\" height=\"50px\"><p style=\"max-height: 50px;overflow: hidden;\">");
			sb.appendEscaped(note.getContent().getValue().replaceAll("\\<.*?>", ""));
			sb.appendHtmlConstant("</td></tr>");

			sb.appendHtmlConstant("<tr><td align=\"left\">Author:");
			sb.appendEscaped(note.getUser().getNickname());
			sb.appendHtmlConstant("</td><td align=\"right\">");
			sb.appendEscaped(DateTimeFormat.getShortDateFormat().format(
					note.getCreatedTime()));
			sb.appendHtmlConstant("</td></tr></table>");
		}
	}

	public SharingView() {
		initWidget(uiBinder.createAndBindUi(this));
		images = GWT.create(Images.class);
		bindSearchHandler();
		initialNoteListPanel();
		initialNotePanel();
		initializeCKEditor();
		bindPresentersAndViews();
	}
	
	private void bindSearchHandler() {
		this.searchBox.getElement().setAttribute("placeholder", "Search Notes");
		this.searchField.sinkEvents(Event.ONCLICK);
		this.searchField.addHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				searchBox.setFocus(true);
			}
		}, ClickEvent.getType());
		this.searchButton.addClickHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				presenter.searchNotes(searchBox.getText());
			}
		});
		
		this.sortBy.addItem("Sort By");
		this.view.addItem("View");
		this.settings.addItem("Settings");
	}

	private void initialNotePanel() {
		this.notePanel.setAnimationEnabled(true);
		this.notePanel.showWidget(0);
		viewPanel.setVisible(false);
	}

	private void initialNoteListPanel() {
		noteCell = new NoteCell();

		notesList = new CellList<InfoNote>(noteCell, KEY_PROVIDER);
		notesList.setPageSize(30);
		notesList.setKeyboardPagingPolicy(KeyboardPagingPolicy.INCREASE_RANGE);
		notesList
				.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.BOUND_TO_SELECTION);

		selectionModel = new SingleSelectionModel<InfoNote>(KEY_PROVIDER);
		notesList.setSelectionModel(selectionModel);

		selectionModel
				.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
					public void onSelectionChange(SelectionChangeEvent event) {
						if (selectionModel.getSelectedObject() != null) {
							presentNote(selectionModel.getSelectedObject());
						} else {
							viewPanel.setVisible(false);
						}
					}
				});
		dataProvider.addDataDisplay(notesList);
		this.noteListPanel.add(new ScrollPanel(notesList));
	}

	@Override
	public Widget asWidget() {
		return this.content;
	}

	private void bindPresentersAndViews() {
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	public Container getContainer() {
		return this.container;
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
				DOM.sinkEvents(div, Event.ONCLICK);
				DOM.setEventListener(div, new EventListener() {
					public void onBrowserEvent(Event event) {
						presenter.onGroupItemSelected(g);
					}
				});
				div.setAttribute("style", "padding-top: 5px;cursor: pointer;");
				div.setInnerHTML(AbstractImagePrototype.create(images.group())
						.getHTML() + g.getName());
				li.appendChild(div);
				groupList.appendChild(li);
			}
		}
	}

	@Override
	public void onGroupsChanged(GroupsChangedEvent event) {
		presenter.loadGroupList();
	}

	@Override
	public void setNoteList(List<InfoNote> result) {
		List<InfoNote> notes = dataProvider.getList();
		selectionModel.clear();
		notes.clear();
		if (result != null && result.size() != 0) {
			notes.addAll(result);
		}
		// notesList.redraw();
	}

	@Override
	public void presentNote(final InfoNote note) {
		viewPanel.setVisible(true);
		this.titleLabel.setText(note.getTitle());
		// this.createdTimeLabel.setText(note.getCreatedTime().toString());
		this.viewContent.setHTML(note.getContent().getValue());
		this.userLink.setText(note.getUser().getEmail());
		this.userLink.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				System.out.println(note.getUser().getEmail());
				presenter.viewUserProfile(note.getUser().getEmail());
			}
			
		});
		this.notePanel.showWidget(0);
	}

	@Override
	public void editNote(InfoNote note) {
		this.editTitle.setText(note.getTitle());
		this.ckeditor.setHTML(note.getContent().getValue());
		notePanel.showWidget(1);
	}

	@Override
	public void showAccessDeniedPanel() {
		final DialogBox dialog = new DialogBox();
		dialog.setText("Access Denied!");
		HTMLPanel content = new HTMLPanel("");
		content.setWidth("300px");
		dialog.setWidget(content);

		content.add(new Label("You do not have the access to edit this note."));
		content.add(new Button("OK", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				dialog.hide();
			}
		}));
		dialog.center();
	}

	@Override
	public InfoNote getSeletedNote() {
		return selectionModel.getSelectedObject();
	}

	@Override
	public void setSelectedNote(InfoNote note) {
		this.selectionModel.setSelected(note, true);
	}

	private void initializeCKEditor() {
		CKConfig ckf = new CKConfig(PRESET_TOOLBAR.FULL);
		ckf.setSkin("office2003");
		// TODO ckeditor height
		ckf.setHeight("396px");
		// ckf.setHeight("250px");

		ckf.setResizeEnabled(false);

		ToolbarLine line = new ToolbarLine();
		TOOLBAR_OPTIONS[] t = { TOOLBAR_OPTIONS.NewPage,
				TOOLBAR_OPTIONS.Preview, TOOLBAR_OPTIONS.Templates,
				TOOLBAR_OPTIONS._, TOOLBAR_OPTIONS.Bold,
				TOOLBAR_OPTIONS.Italic, TOOLBAR_OPTIONS.Underline,
				TOOLBAR_OPTIONS.Strike, TOOLBAR_OPTIONS.Font,
				TOOLBAR_OPTIONS.FontSize, TOOLBAR_OPTIONS.TextColor,
				TOOLBAR_OPTIONS.BGColor, TOOLBAR_OPTIONS.Subscript,
				TOOLBAR_OPTIONS.Superscript, TOOLBAR_OPTIONS.Outdent,
				TOOLBAR_OPTIONS.Indent, TOOLBAR_OPTIONS.HorizontalRule,
				TOOLBAR_OPTIONS.NumberedList, TOOLBAR_OPTIONS.BulletedList,
				TOOLBAR_OPTIONS.JustifyLeft, TOOLBAR_OPTIONS.JustifyCenter,
				TOOLBAR_OPTIONS.JustifyRight, TOOLBAR_OPTIONS._,
				TOOLBAR_OPTIONS.Image, TOOLBAR_OPTIONS.Table,
				TOOLBAR_OPTIONS.SpecialChar };
		line.addAll(t);

		Toolbar toolBar = new Toolbar();
		toolBar.add(line);

		ckf.setToolbar(toolBar);

		this.ckeditor = new CKEditor(true, ckf);
		this.editContent.add(this.ckeditor);
	}

	@Override
	public InfoNote getModifiedNoteData() {
		InfoNote note = selectionModel.getSelectedObject();
		note.setTitle(this.editTitle.getText());
		note.setContent(new Text(this.ckeditor.getHTML()));
		return note;
	}

	@Override
	public void updateNote(InfoNote result) {
		List<InfoNote> notes = dataProvider.getList();
		for(int i=0; i<notes.size();i++) {
			if (result.getKey().equals(notes.get(i).getKey())) {
				notes.remove(i);
				notes.add(i, result);
			}
		}
	}
}
