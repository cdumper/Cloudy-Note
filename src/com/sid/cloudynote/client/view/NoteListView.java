package com.sid.cloudynote.client.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.appengine.api.datastore.Key;
import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ClickableTextCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.HasKeyboardPagingPolicy.KeyboardPagingPolicy;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import com.sid.cloudynote.client.AppController;
import com.sid.cloudynote.client.DataManager;
import com.sid.cloudynote.client.event.GroupsChangedEvent;
import com.sid.cloudynote.client.event.NoteChangedEvent;
import com.sid.cloudynote.client.event.PresentNotesEvent;
import com.sid.cloudynote.client.event.interfaces.IGroupsChangedHandler;
import com.sid.cloudynote.client.event.interfaces.INoteChangedHandler;
import com.sid.cloudynote.client.event.interfaces.IPresentNotesHandler;
import com.sid.cloudynote.client.view.interfaces.INoteListView;
import com.sid.cloudynote.shared.Group;
import com.sid.cloudynote.shared.InfoNote;
import com.sid.cloudynote.shared.User;

public class NoteListView extends ResizeComposite implements
		INoteChangedHandler, IPresentNotesHandler, INoteListView, IGroupsChangedHandler {
	@UiTemplate("NoteListView.ui.xml")
	interface NoteListPanelUiBinder extends UiBinder<Widget, NoteListView> {
	}

	private static NoteListPanelUiBinder uiBinder = GWT
			.create(NoteListPanelUiBinder.class);

	/**
	 * The pager used to change the range of data.
	 */
	@UiField
	ShowMorePagerPanel pagerPanel;
	@UiField
	Container container;
	@UiField
	Label label;

	public Container getContainer() {
		return container;
	}

	private Set<Group> groupsSet;
	private List<User> friendsList;
	private CellList<InfoNote> cellList;
	private NoteCell noteCell;
	private ListDataProvider<InfoNote> dataProvider = new ListDataProvider<InfoNote>();
	public static final ProvidesKey<InfoNote> KEY_PROVIDER = new ProvidesKey<InfoNote>() {
		@Override
		public Object getKey(InfoNote InfoNote) {
			return InfoNote == null ? null : InfoNote.getKey();
		}
	};

	static interface Images extends ClientBundle {
		ImageResource home();
	}

	private SingleSelectionModel<InfoNote> selectionModel;
	private Presenter presenter;

	class NoteCell extends AbstractCell<InfoNote> {
		List<GrantAccessItem> items = new ArrayList<GrantAccessItem>();
		private SingleSelectionModel<InfoNote> selectionModel;

		public SingleSelectionModel<InfoNote> getSelectionModel() {
			return selectionModel;
		}

		public void setSelectionModel(
				SingleSelectionModel<InfoNote> selectionModel) {
			this.selectionModel = selectionModel;
		}

		Presenter presenter;

		public Presenter getPresenter() {
			return presenter;
		}

		public void setPresenter(Presenter presenter) {
			this.presenter = presenter;
		}

		private final String imageHtml;

		public NoteCell(ImageResource image) {
			super("contextmenu");
			this.imageHtml = AbstractImagePrototype.create(image).getHTML();
		}

		@Override
		public void render(Context context, InfoNote note, SafeHtmlBuilder sb) {
			if (note == null) {
				return;
			}

			sb.appendHtmlConstant("<table height='30px'>");

			sb.appendHtmlConstant("<tr><td rowspan='3'>");
			sb.appendHtmlConstant(imageHtml);
			sb.appendHtmlConstant("</td>");

			sb.appendHtmlConstant("<td style='font-size:95%;'>");
			sb.appendEscaped(note.getTitle());
			sb.appendHtmlConstant("</td></tr><tr><td>");
			sb.appendEscaped(note.getContent().replaceAll("\\<.*?>", ""));
			sb.appendHtmlConstant("</td></tr></table>");
		}

		@Override
		public void onBrowserEvent(Context context, Element parent,
				InfoNote value, NativeEvent event,
				ValueUpdater<InfoNote> valueUpdater) {
			event.preventDefault();
			event.stopPropagation();

			if ("contextmenu".equals(event.getType())) {
				noteContextMenu = new NoteContextMenu(value);
				initialNoteContextMenu();
				noteContextMenu.setSelectedNote(value);
				noteContextMenu.setPopupPosition(event.getClientX(),
						event.getClientY());
				noteContextMenu.show();
			}
		}

		private class NoteContextMenu extends PopupPanel {
			private InfoNote selectedNote;

			public InfoNote getSelectedNote() {
				return selectedNote;
			}

			public void setSelectedNote(InfoNote selectedNote) {
				this.selectedNote = selectedNote;
			}

			NoteContextMenu(InfoNote note) {
				super();
				this.selectedNote = note;
				this.setAutoHideEnabled(true);
				this.setAnimationEnabled(true);
			}
		}

		List<String> OPERATION_LIST = new ArrayList<String>(Arrays.asList(
				"Edit", "Share", "Delete"));
		private NoteContextMenu noteContextMenu;

		private void initialNoteContextMenu() {
			VerticalPanel content = new VerticalPanel();
			noteContextMenu.setAnimationEnabled(true);
			noteContextMenu.setWidget(content);
			ClickableTextCell cell = new ClickableTextCell() {
				@Override
				public void onBrowserEvent(Context context, Element parent,
						String value, NativeEvent event,
						ValueUpdater<String> valueUpdater) {
					if ("click".equals(event.getType())) {
						noteContextMenu.hide();
						if ("Edit".equals(value)) {
							selectionModel.setSelected(
									noteContextMenu.getSelectedNote(), true);
							presenter.startEditing(noteContextMenu
									.getSelectedNote());
						} else if ("Delete".equals(value)) {
							showDeletePanel();
						} else if ("Share".equals(value)) {
							showSharePanel();
						}
					}
				}
			};
			CellList<String> operationList = new CellList<String>(cell);
			operationList.setRowData(OPERATION_LIST);
			content.add(operationList);
		}

		public void showSharePanel() {
			final DialogBox dialog = new DialogBox();
			dialog.setText(noteContextMenu.getSelectedNote().getTitle());

			showChooseWayToSharePanel(dialog);
			dialog.center();
		}

		private void showShareToIndividual(final DialogBox dialog) {
			InfoNote note = noteContextMenu.getSelectedNote();

			VerticalPanel content = new VerticalPanel();
			dialog.setWidget(content);
			ScrollPanel scrollPanel = new ScrollPanel();
			HTMLPanel scrollContent = new HTMLPanel("");

			Label name = new Label("Share with individuals or groups");
			if (groupsSet != null && groupsSet.size() != 0) {
				scrollContent.add(new Label("Groups:"));
				for (Group g : groupsSet) {
					GrantAccessItem item;
					boolean exist = false;
					int permission = 1;
					if (note.getGroupAccess() != null
							&& note.getGroupAccess().size() != 0) {
						for (Entry<Key, Integer> entry : note.getGroupAccess()
								.entrySet()) {
							if (entry.getKey().equals(g.getKey())) {
								exist = true;
								permission = entry.getValue();
								// break;
							}
						}
					}
					item = new GrantAccessItem(g, exist, permission);
					items.add(item);
					scrollContent.add(item.asWidget());
				}
			}

			if (friendsList != null && friendsList.size() != 0) {
				scrollContent.add(new Label("Friends:"));
				for (User user : friendsList) {
					GrantAccessItem item;
					boolean exist = false;
					int permission = 1;
					if (note.getUserAccess() != null
							&& note.getUserAccess().size() != 0) {
						for (Entry<String, Integer> entry : note
								.getUserAccess().entrySet()) {
							if (entry.getKey().equals(user.getEmail())) {
								exist = true;
								permission = entry.getValue();
								// break;
							}
						}
					}
					item = new GrantAccessItem(user, exist, permission);
					items.add(item);
					scrollContent.add(item.asWidget());
				}
			}

			HorizontalPanel buttonPanel = new HorizontalPanel();
			buttonPanel.add(new Button("Cancel", new ClickHandler() {
				public void onClick(ClickEvent event) {
					showChooseWayToSharePanel(dialog);
				}
			}));
			buttonPanel.add(new Button("OK", new ClickHandler() {
				public void onClick(ClickEvent event) {
					Map<String, Integer> users = new HashMap<String, Integer>();
					Map<Key, Integer> groups = new HashMap<Key, Integer>();
					// List<InfoNote> notes = new ArrayList<InfoNote>();
					// notes.add(noteContextMenu.getSelectedNote());
					for (GrantAccessItem item : items) {
						if (item.checkBox.getValue()) {
							String permission = item.access
									.getItemText(item.access.getSelectedIndex());
							int _permission = 1;
							if ("Read-Only".equals(permission)) {
								_permission = 1;
							} else if ("Write".equals(permission)) {
								_permission = 2;
							}

							if (item.item instanceof Group) {
								groups.put(((Group) item.item).getKey(),
										_permission);
							} else if (item.item instanceof User) {
								users.put(((User) item.item).getEmail(),
										_permission);
							}
						}
					}
					presenter.shareNoteToUsersAndGroups(noteContextMenu.getSelectedNote(), users, groups);
					dialog.hide();
				}
			}));
			content.add(name);
			scrollPanel.add(scrollContent);
			content.add(scrollPanel);
			content.add(buttonPanel);
		}

		private void showMakePublic(DialogBox dialog) {
			List<InfoNote> notes = new ArrayList<InfoNote>();
			notes.add(noteContextMenu.getSelectedNote());
			presenter.makeNotesPublic(notes);
			dialog.hide();
		}

		private void showChooseWayToSharePanel(final DialogBox dialog) {
			// the UI for choosing Share or Public
			VerticalPanel content = new VerticalPanel();
			dialog.setWidget(content);

			Label chooseLabel = new Label(
					" Share with individuals or Make it public");
			HorizontalPanel buttonPanel = new HorizontalPanel();
			Button shareButton = new Button("Shared to individuals");
			Button publicButton = new Button("Make public");
			buttonPanel.add(shareButton);
			buttonPanel.add(publicButton);

			shareButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					showShareToIndividual(dialog);
				}
			});

			publicButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					showMakePublic(dialog);
				}
			});
			content.add(chooseLabel);
			content.add(buttonPanel);
		}

		public void showDeletePanel() {
			final DialogBox dialog = new DialogBox();
			dialog.setText("Delete Note");
			VerticalPanel content = new VerticalPanel();
			dialog.setWidget(content);

			final Label name = new Label();
			content.add(name);

			HorizontalPanel buttonPanel = new HorizontalPanel();
			buttonPanel.add(new Button("Cancel", new ClickHandler() {
				public void onClick(ClickEvent event) {
					dialog.hide();
				}
			}));
			buttonPanel.add(new Button("OK", new ClickHandler() {
				public void onClick(ClickEvent event) {
					presenter.deleteNote(noteContextMenu.getSelectedNote());
					dialog.hide();
				}
			}));
			content.add(buttonPanel);

			dialog.show();
			dialog.center();
		}
	}

	private class GrantAccessItem {
		private HTMLPanel content = new HTMLPanel("");
		private Object item = null;
		private CheckBox checkBox = new CheckBox();
		private Label label = new Label();
		ListBox access = new ListBox();

		public GrantAccessItem(Object item, boolean checked, int permission) {
			this.item = item;
			checkBox.setValue(checked);
			access.addItem("Read-Only");
			access.addItem("Write");
			access.setSelectedIndex(permission - 1);
			if (item instanceof Group) {
				label.setText(((Group) item).getName());
			} else if (item instanceof User) {
				label.setText(((User) item).getEmail());
			}
			label.getElement().setId("item");

			content.add(checkBox);
			content.add(access);
			content.add(label);
		}

		public Widget asWidget() {
			return this.content;
		}
	}

	public NoteListView() {
		initWidget(uiBinder.createAndBindUi(this));
		Images images = GWT.create(Images.class);

		noteCell = new NoteCell(images.home());

		cellList = new CellList<InfoNote>(noteCell, KEY_PROVIDER);
		cellList.setPageSize(30);
		cellList.setKeyboardPagingPolicy(KeyboardPagingPolicy.INCREASE_RANGE);
		cellList.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.BOUND_TO_SELECTION);

		// Add a selection model so we can select cells.
		selectionModel = new SingleSelectionModel<InfoNote>(KEY_PROVIDER);
		cellList.setSelectionModel(selectionModel);
		noteCell.setSelectionModel(selectionModel);

		selectionModel
				.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
					public void onSelectionChange(SelectionChangeEvent event) {
						InfoNote note = selectionModel.getSelectedObject();
						presenter.onNoteItemSelected(note);
						// DataManager.setCurrentNote(note.getKey());
					}
				});
		dataProvider.addDataDisplay(cellList);
		pagerPanel.setDisplay(cellList);
		// rangeLabelPager.setDisplay(cellList);
	}

	@Override
	public void onNoteChanged(NoteChangedEvent event) {
		presenter.loadNoteList(event.getNotebook());
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
		noteCell.setPresenter(presenter);

		presenter.loadGroupList(AppController.get().getLoginInfo().getEmail());
		presenter
				.loadFriendsList(AppController.get().getLoginInfo().getEmail());
	}

	@Override
	public void setNoteList(List<InfoNote> result) {
		List<InfoNote> notes = dataProvider.getList();
		notes.clear();
		if(result!=null)	notes.addAll(result);
		if (DataManager.getCurrentNote() != null)
			selectionModel.setSelected(DataManager.getCurrentNote(), true);
	}

	public void setLabel(String text) {
		this.label.setText(text);
	}

	@Override
	public Widget asWidget() {
		return this.pagerPanel;
	}

	public void setFriendsList(List<User> result) {
		this.friendsList = result;
	}

	public void setGroupSet(Set<Group> result) {
		this.groupsSet = result;
	}

	@Override
	public void onGroupsChanged(GroupsChangedEvent event) {
		presenter.loadGroupList(AppController.get().getLoginInfo().getEmail());
	}

	@Override
	public void onPresentNotes(PresentNotesEvent event) {
		this.setNoteList(event.getNotes());
	}
}