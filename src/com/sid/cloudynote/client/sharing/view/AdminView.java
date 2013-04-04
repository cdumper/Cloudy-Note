package com.sid.cloudynote.client.sharing.view;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import com.sid.cloudynote.client.DataManager;
import com.sid.cloudynote.client.event.GroupsChangedEvent;
import com.sid.cloudynote.client.event.NotebookChangedEvent;
import com.sid.cloudynote.client.event.interfaces.IGroupsChangedHandler;
import com.sid.cloudynote.client.event.interfaces.INotebookChangedHandler;
import com.sid.cloudynote.client.service.InfoNoteService;
import com.sid.cloudynote.client.service.InfoNoteServiceAsync;
import com.sid.cloudynote.client.sharing.view.interfaces.IAdminView;
import com.sid.cloudynote.client.view.Container;
import com.sid.cloudynote.shared.Group;
import com.sid.cloudynote.shared.InfoNote;
import com.sid.cloudynote.shared.Notebook;
import com.sid.cloudynote.shared.User;

public class AdminView extends Composite implements IAdminView,
		IGroupsChangedHandler, INotebookChangedHandler {
	private Presenter presenter;
	private SingleSelectionModel<Group> userAccessSelectionModel;
	private SingleSelectionModel<Notebook> notePermissionSelectionModel;
	private SingleSelectionModel<User> userSelectionModel;
	private SingleSelectionModel<InfoNote> noteSelectionModel;
	private ListDataProvider<Notebook> notebookDataProvider = new ListDataProvider<Notebook>();
	private ListDataProvider<Group> groupDataProvider = new ListDataProvider<Group>();
	private ListDataProvider<User> userDataProvider = new ListDataProvider<User>();
	private ListDataProvider<InfoNote> noteDataProvider = new ListDataProvider<InfoNote>();
	private static AdminViewUiBinder uiBinder = GWT
			.create(AdminViewUiBinder.class);
	FlexTable groupAccessTable = new FlexTable();
	FlexTable notebookPermissionTable = new FlexTable();
	FlexTable userAccessTable = new FlexTable();
	FlexTable notePermissionTable = new FlexTable();

	interface AdminViewUiBinder extends UiBinder<Widget, AdminView> {
	}

	interface Style extends CssResource {
		String header();
	}

	public static final ProvidesKey<Notebook> NOTEBOOK_KEY_PROVIDER = new ProvidesKey<Notebook>() {
		@Override
		public Object getKey(Notebook notebook) {
			return notebook == null ? null : notebook.getName();
		}
	};

	public static final ProvidesKey<InfoNote> NOTE_KEY_PROVIDER = new ProvidesKey<InfoNote>() {
		@Override
		public Object getKey(InfoNote note) {
			return note == null ? null : note.getKey();
		}
	};

	public static final ProvidesKey<Group> GROUP_KEY_PROVIDER = new ProvidesKey<Group>() {
		@Override
		public Object getKey(Group group) {
			return group == null ? null : group.getName();
		}
	};

	public static final ProvidesKey<User> USER_KEY_PROVIDER = new ProvidesKey<User>() {
		@Override
		public Object getKey(User user) {
			return user == null ? null : user.getEmail();
		}
	};

	public AdminView() {
		initWidget(uiBinder.createAndBindUi(this));

		initialUserAccessList();
		initialNotePermissionList();
		initialUserList();
		initialNoteList();
		subListDeck.showWidget(0);
	}

	@Override
	public void presentGroupAccess(Group group) {
		this.contentDeck.showWidget(0);
		// this.userAccessSaveButton.setEnabled(false);
		this.userAccessButtonPanel.setVisible(true);
		this.groupAccessContentPanel.setVisible(true);
		this.userAccessContentPanel.setVisible(false);
		if (group == null || group.getKey() == null) {
			this.groupLabel.setText("");
			this.groupAccessTable.removeAllRows();
			this.groupAccessContentPanel.setVisible(false);
			this.userAccessButtonPanel.setVisible(false);
			return;
		}
		this.groupLabel.setText(group.getName() + " Access");
		this.groupAccessTable.removeAllRows();
		// generate the header
		CheckBox allRead = new CheckBox("Read");
		CheckBox allWrite = new CheckBox("Write");
		allRead.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				for (int i = 1; i < groupAccessTable.getRowCount(); i++) {
					((CheckBox) groupAccessTable.getWidget(i, 1))
							.setValue(event.getValue());
				}
			}

		});
		allWrite.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				for (int i = 1; i < groupAccessTable.getRowCount(); i++) {
					((CheckBox) groupAccessTable.getWidget(i, 2))
							.setValue(event.getValue());
				}
			}

		});
		groupAccessTable.setWidget(0, 1, allRead);
		groupAccessTable.setWidget(0, 2, allWrite);
		groupAccessTable.setWidget(0, 3, new Label("Notebook"));
		groupAccessTable.setWidget(0, 4, new Label("Note"));

		if (group.getAccess().size() != 0) {
			// generate table content
			for (Map.Entry<Key, Integer> entry : group.getAccess().entrySet()) {
				int row = groupAccessTable.getRowCount();
				Label rowValue = new Label();
				rowValue.getElement().setInnerHTML(
						KeyFactory.keyToString(entry.getKey()));
				rowValue.setVisible(false);
				CheckBox read = new CheckBox("Read");
				CheckBox write = new CheckBox("Write");
				groupAccessTable.setWidget(row, 0, rowValue);
				groupAccessTable.setWidget(row, 1, read);
				groupAccessTable.setWidget(row, 2, write);
				groupAccessTable.setWidget(row, 3, new Label("-"));
				Label label = new Label();
				fillNoteTitle(label, entry.getKey());
				groupAccessTable.setWidget(row, 4, label);
				if (entry.getValue() == 1) {
					read.setValue(true);
					write.setValue(false);
				} else if (entry.getValue() == 2) {
					read.setValue(true);
					write.setValue(true);
				}
			}
		} else {
			this.userAccessButtonPanel.setVisible(false);
		}
		this.groupAccessTable.getRowFormatter().addStyleName(0, style.header());
		this.groupAccessTable.getColumnFormatter().setWidth(1, "10%");
		this.groupAccessTable.getColumnFormatter().setWidth(2, "10%");
		this.groupAccessTable.getColumnFormatter().setWidth(2, "35%");
		this.groupAccessTable.getColumnFormatter().setWidth(3, "35%");
		this.groupAccessContent.add(groupAccessTable);
	}

	@Override
	public void presentNotebookPermission(Notebook notebook) {
		this.contentDeck.showWidget(1);
		// this.notePermissionSaveButton.setEnabled(false);
		this.notePermissionButtonPanel.setVisible(true);
		this.notebookPermissionContentPanel.setVisible(true);
		this.notePermissionContentPanel.setVisible(false);

		if (notebook == null || notebook.getKey() == null) {
			this.groupLabel.setText("");
			this.notebookPermissionTable.removeAllRows();
			this.notebookPermissionContentPanel.setVisible(false);
			this.notePermissionButtonPanel.setVisible(true);
			return;
		}

		this.notebookLabel.setText(notebook.getName() + " Permission");
		this.notebookPermissionTable.removeAllRows();
		// generate the header
		CheckBox allRead = new CheckBox("Read");
		CheckBox allWrite = new CheckBox("Write");
		allRead.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				for (int i = 1; i < notebookPermissionTable.getRowCount(); i++) {
					((CheckBox) notebookPermissionTable.getWidget(i, 1))
							.setValue(event.getValue());
				}
			}

		});
		allWrite.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				for (int i = 1; i < notebookPermissionTable.getRowCount(); i++) {
					((CheckBox) notebookPermissionTable.getWidget(i, 2))
							.setValue(event.getValue());
				}
			}

		});
		notebookPermissionTable.setWidget(0, 1, allRead);
		notebookPermissionTable.setWidget(0, 2, allWrite);
		notebookPermissionTable.setWidget(0, 3, new Label("Group"));
		notebookPermissionTable.setWidget(0, 4, new Label("User"));

		if (notebook.getAccess().size() != 0) {
			// generate table content
			for (Map.Entry<String, Integer> entry : notebook.getAccess()
					.entrySet()) {
				int row = notebookPermissionTable.getRowCount();
				Label rowValue = new Label();
				rowValue.getElement().setInnerHTML(entry.getKey());
				rowValue.setVisible(false);
				CheckBox read = new CheckBox("Read");
				CheckBox write = new CheckBox("Write");
				notebookPermissionTable.setWidget(row, 0, rowValue);
				notebookPermissionTable.setWidget(row, 1, read);
				notebookPermissionTable.setWidget(row, 2, write);
				notebookPermissionTable.setWidget(row, 3, new Label("-"));
				notebookPermissionTable.setWidget(row, 4,
						new Label(getUserNickname(entry.getKey())));
				if (entry.getValue() == 1) {
					read.setValue(true);
					write.setValue(false);
				} else if (entry.getValue() == 2) {
					read.setValue(true);
					write.setValue(true);
				}
			}
		} else {
			this.notePermissionButtonPanel.setVisible(false);
		}
		this.notebookPermissionTable.getRowFormatter().addStyleName(0,
				style.header());
		this.notebookPermissionTable.getColumnFormatter().setWidth(1, "10%");
		this.notebookPermissionTable.getColumnFormatter().setWidth(2, "10%");
		this.notebookPermissionTable.getColumnFormatter().setWidth(2, "35%");
		this.notebookPermissionTable.getColumnFormatter().setWidth(3, "35%");
		this.notebookPermissionContent.add(notebookPermissionTable);
	}

	@Override
	public void presentUserAccess(User user) {
		this.contentDeck.showWidget(0);
		// this.userAccessSaveButton.setEnabled(false);
		// check whether or not present the group access
		if (userAccessSelectionModel.getSelectedObject() != null) {
			Group group = userAccessSelectionModel.getSelectedObject();
			if (group.getKey() != null) {
				presentGroupAccess(group);
			} else {
				this.groupAccessContentPanel.setVisible(false);
			}
		}
		this.userAccessContentPanel.setVisible(true);
		this.userAccessTable.removeAllRows();

		this.userLabel.setText(user.getEmail() + " Access");
		// generate the header
		CheckBox allRead = new CheckBox("Read");
		CheckBox allWrite = new CheckBox("Write");
		allRead.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				for (int i = 1; i < userAccessTable.getRowCount(); i++) {
					((CheckBox) userAccessTable.getWidget(i, 1)).setValue(event
							.getValue());
				}
			}

		});
		allWrite.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				for (int i = 1; i < userAccessTable.getRowCount(); i++) {
					((CheckBox) userAccessTable.getWidget(i, 2)).setValue(event
							.getValue());
				}
			}

		});
		userAccessTable.setWidget(0, 1, allRead);
		userAccessTable.setWidget(0, 2, allWrite);
		userAccessTable.setWidget(0, 3, new Label("Notebook"));
		userAccessTable.setWidget(0, 4, new Label("Note"));
		if (user.getAccess().size() != 0) {
			this.userAccessButtonPanel.setVisible(true);
			// generate table content
			for (Map.Entry<Key, Integer> entry : user.getAccess().entrySet()) {
				int row = userAccessTable.getRowCount();
				Label rowValue = new Label();
				rowValue.getElement().setInnerHTML(
						KeyFactory.keyToString(entry.getKey()));
				rowValue.setVisible(false);
				CheckBox read = new CheckBox("Read");
				CheckBox write = new CheckBox("Write");
				userAccessTable.setWidget(row, 0, rowValue);
				userAccessTable.setWidget(row, 1, read);
				userAccessTable.setWidget(row, 2, write);
				userAccessTable.setWidget(row, 3, new Label("-"));
				Label label = new Label();
				fillNoteTitle(label, entry.getKey());
				userAccessTable.setWidget(row, 4, label);
				if (entry.getValue() == 1) {
					read.setValue(true);
					write.setValue(false);
				} else if (entry.getValue() == 2) {
					read.setValue(true);
					write.setValue(true);
				}
			}
		}
		this.userAccessTable.getRowFormatter().addStyleName(0, style.header());
		this.userAccessTable.getColumnFormatter().setWidth(1, "10%");
		this.userAccessTable.getColumnFormatter().setWidth(2, "10%");
		this.userAccessTable.getColumnFormatter().setWidth(2, "35%");
		this.userAccessTable.getColumnFormatter().setWidth(3, "35%");
		this.userAccessContent.add(userAccessTable);
	}

	@Override
	public void presentNotePermission(InfoNote note) {
		this.contentDeck.showWidget(1);
		// this.notePermissionSaveButton.setEnabled(false);
		// check whether or not present the notebook access
		if (notePermissionSelectionModel.getSelectedObject() != null) {
			Notebook notebook = notePermissionSelectionModel
					.getSelectedObject();
			if (notebook.getKey() != null) {
				presentNotebookPermission(notebook);
			} else {
				this.notebookPermissionContentPanel.setVisible(false);
			}
		}
		this.notePermissionContentPanel.setVisible(true);
		this.notePermissionTable.removeAllRows();

		this.noteLabel.setText(note.getTitle() + " Permission");
		// generate the header
		CheckBox allRead = new CheckBox("Read");
		CheckBox allWrite = new CheckBox("Write");
		allRead.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				for (int i = 1; i < notePermissionTable.getRowCount(); i++) {
					((CheckBox) notePermissionTable.getWidget(i, 1))
							.setValue(event.getValue());
				}
			}

		});
		allWrite.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				for (int i = 1; i < notePermissionTable.getRowCount(); i++) {
					((CheckBox) notePermissionTable.getWidget(i, 2))
							.setValue(event.getValue());
				}
			}

		});
		notePermissionTable.setWidget(0, 1, allRead);
		notePermissionTable.setWidget(0, 2, allWrite);
		notePermissionTable.setWidget(0, 3, new Label("Group"));
		notePermissionTable.setWidget(0, 4, new Label("User"));

		if (note.getGroupAccess().size() != 0
				|| note.getUserAccess().size() != 0) {
			this.notePermissionButtonPanel.setVisible(true);
			// generate table content
			// first part to generate group access
			for (Map.Entry<Key, Integer> entry : note.getGroupAccess()
					.entrySet()) {
				int row = notePermissionTable.getRowCount();
				Label rowValue = new Label();
				rowValue.getElement().setInnerHTML(
						KeyFactory.keyToString(entry.getKey()));
				rowValue.setVisible(false);
				CheckBox read = new CheckBox("Read");
				CheckBox write = new CheckBox("Write");
				notePermissionTable.setWidget(row, 0, rowValue);
				notePermissionTable.setWidget(row, 1, read);
				notePermissionTable.setWidget(row, 2, write);
				notePermissionTable.setWidget(row, 3, new Label(getGroupName(entry.getKey())));
				notePermissionTable.setWidget(row, 4, new Label("-"));
				if (entry.getValue() == 1) {
					read.setValue(true);
					write.setValue(false);
				} else if (entry.getValue() == 2) {
					read.setValue(true);
					write.setValue(true);
				}
			}
			// second part to generate user access
			for (Map.Entry<String, Integer> entry : note.getUserAccess()
					.entrySet()) {
				int row = notePermissionTable.getRowCount();
				Label rowValue = new Label();
				rowValue.getElement().setInnerHTML(entry.getKey());
				rowValue.setVisible(false);
				CheckBox read = new CheckBox("Read");
				CheckBox write = new CheckBox("Write");
				notePermissionTable.setWidget(row, 0, rowValue);
				notePermissionTable.setWidget(row, 1, read);
				notePermissionTable.setWidget(row, 2, write);
				notePermissionTable.setWidget(row, 3, new Label("-"));
				notePermissionTable.setWidget(row, 4, 
						new Label(getUserNickname(entry.getKey())));
				if (entry.getValue() == 1) {
					read.setValue(true);
					write.setValue(false);
				} else if (entry.getValue() == 2) {
					read.setValue(true);
					write.setValue(true);
				}
			}
		}
		this.notePermissionTable.getRowFormatter().addStyleName(0,
				style.header());
		this.notePermissionTable.getColumnFormatter().setWidth(1, "10%");
		this.notePermissionTable.getColumnFormatter().setWidth(2, "10%");
		this.notePermissionTable.getColumnFormatter().setWidth(2, "35%");
		this.notePermissionTable.getColumnFormatter().setWidth(3, "35%");
		this.notePermissionContent.add(notePermissionTable);
	}

	private void initialNoteList() {
		noteSelectionModel = new SingleSelectionModel<InfoNote>(
				NOTE_KEY_PROVIDER);
		noteList.setSelectionModel(noteSelectionModel);

		noteSelectionModel
				.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
					public void onSelectionChange(SelectionChangeEvent event) {
						InfoNote note = noteSelectionModel.getSelectedObject();
						if (note != null && note.getKey() != null) {
							presenter.onNoteItemSelected(note);
						}
					}
				});

		this.noteDataProvider.addDataDisplay(this.noteList);
	}

	private void initialUserList() {
		userSelectionModel = new SingleSelectionModel<User>(USER_KEY_PROVIDER);
		userList.setSelectionModel(userSelectionModel);

		userSelectionModel
				.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
					public void onSelectionChange(SelectionChangeEvent event) {
						User user = userSelectionModel.getSelectedObject();
						if (user != null && user.getEmail() != null) {
							presenter.onUserItemSelected(user);
						}
					}
				});

		this.userDataProvider.addDataDisplay(this.userList);
	}

	private void initialUserAccessList() {
		userAccessSelectionModel = new SingleSelectionModel<Group>(
				GROUP_KEY_PROVIDER);
		userAccessList.setSelectionModel(userAccessSelectionModel);

		userAccessSelectionModel
				.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
					public void onSelectionChange(SelectionChangeEvent event) {
						Group group = userAccessSelectionModel
								.getSelectedObject();
						if (group == null) {
							if (userSelectionModel.getSelectedObject() != null) {
								userSelectionModel.setSelected(
										userSelectionModel.getSelectedObject(),
										false);
							}
							return;
						}

						if (notePermissionSelectionModel.getSelectedObject() != null) {
							notePermissionSelectionModel.setSelected(
									notePermissionSelectionModel
											.getSelectedObject(), false);
						}

						if (userSelectionModel.getSelectedObject() != null) {
							userSelectionModel.setSelected(
									userSelectionModel.getSelectedObject(),
									false);
						}

						presenter.onUserAccessItemSelected(group);
					}
				});
		this.groupDataProvider.getList().add(new Group("All Users"));
		this.groupDataProvider.getList().add(new Group("UnGrouped"));
		this.groupDataProvider.addDataDisplay(this.userAccessList);
		this.userAccessPanel.setOpen(true);
	}

	private void initialNotePermissionList() {
		notePermissionSelectionModel = new SingleSelectionModel<Notebook>(
				NOTEBOOK_KEY_PROVIDER);
		notePermissionList.setSelectionModel(notePermissionSelectionModel);

		notePermissionSelectionModel
				.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
					public void onSelectionChange(SelectionChangeEvent event) {
						Notebook notebook = notePermissionSelectionModel
								.getSelectedObject();
						if (notebook == null) {
							if (noteSelectionModel.getSelectedObject() != null) {
								noteSelectionModel.setSelected(
										noteSelectionModel.getSelectedObject(),
										false);
							}
							return;
						}

						if (userAccessSelectionModel.getSelectedObject() != null) {
							userAccessSelectionModel.setSelected(
									userAccessSelectionModel
											.getSelectedObject(), false);
						}

						if (noteSelectionModel.getSelectedObject() != null) {
							noteSelectionModel.setSelected(
									noteSelectionModel.getSelectedObject(),
									false);
						}

						presenter.onNotePermissionItemSelected(notebook);
					}
				});

		this.notebookDataProvider.getList().add(new Notebook("All Notes"));
		this.notebookDataProvider.addDataDisplay(this.notePermissionList);
		this.notePermissionPanel.setOpen(true);
	}

	@UiField
	Style style;
	@UiField
	Container container;
	@UiField
	DockLayoutPanel content;
	@UiField
	DisclosurePanel userAccessPanel;
	@UiField
	DisclosurePanel notePermissionPanel;
	@UiField
	HTMLPanel userAccessButtonPanel;
	@UiField
	HTMLPanel notePermissionButtonPanel;
	@UiField
	Label subListLabel;

	@Override
	public void setSubListLabel(String text) {
		this.subListLabel.setText(text);
	}

	@UiField
	DeckPanel subListDeck;
	@UiField
	Button userAccessSaveButton;
	@UiField
	Button userAccessRevertButton;
	@UiField
	Button notePermissionSaveButton;
	@UiField
	Button notePermissionRevertButton;

	@UiHandler("userAccessSaveButton")
	void onClickUserAccessSave(ClickEvent e) {
		Group group = null;
		User user = null;
		Map<Key, Integer> groupAccess = null;
		Map<Key, Integer> userAccess = null;
		if (this.groupAccessContentPanel.isVisible()) {
			group = this.userAccessSelectionModel.getSelectedObject();
			groupAccess = new LinkedHashMap<Key, Integer>();
			for (int i = 1; i < groupAccessTable.getRowCount(); i++) {
				String keyString = groupAccessTable.getWidget(i, 0)
						.getElement().getInnerHTML();
				Key key = KeyFactory.stringToKey(keyString);
				int permission = 0;
				if (((CheckBox) groupAccessTable.getWidget(i, 2)).getValue()) {
					permission = 2;
				} else if (((CheckBox) groupAccessTable.getWidget(i, 1))
						.getValue()) {
					permission = 1;
				}
				if (permission != 0)
					groupAccess.put(key, permission);
			}
		}
		if (this.userAccessContentPanel.isVisible()) {
			user = this.userSelectionModel.getSelectedObject();
			userAccess = new LinkedHashMap<Key, Integer>();
			for (int i = 1; i < userAccessTable.getRowCount(); i++) {
				String keyString = userAccessTable.getWidget(i, 0).getElement()
						.getInnerHTML();
				Key key = KeyFactory.stringToKey(keyString);
				int permission = 0;
				if (((CheckBox) userAccessTable.getWidget(i, 2)).getValue()) {
					permission = 2;
				} else if (((CheckBox) userAccessTable.getWidget(i, 1))
						.getValue()) {
					permission = 1;
				}
				if (permission != 0)
					userAccess.put(key, permission);
			}
		}
		presenter.saveUserAccessChanges(group, groupAccess, user, userAccess);
		// this.userAccessSaveButton.setEnabled(false);
	}

	@UiHandler("userAccessRevertButton")
	void onClickUserAccessRevert(ClickEvent e) {
		if (userSelectionModel.getSelectedObject() != null) {
			this.presentUserAccess(userSelectionModel.getSelectedObject());
		} else {
			this.presentGroupAccess(userAccessSelectionModel
					.getSelectedObject());
		}
		// this.userAccessSaveButton.setEnabled(false);
	}

	@UiHandler("notePermissionSaveButton")
	void onClickNotePermissionSave(ClickEvent e) {
		Notebook notebook = null;
		InfoNote note = null;
		Map<Key, Integer> notebookGroupPermission = null;
		Map<String, Integer> notebookUserPermission = null;
		Map<Key, Integer> noteGroupPermission = null;
		Map<String, Integer> noteUserPermission = null;
		if (this.notebookPermissionContentPanel.isVisible()) {
			notebook = this.notePermissionSelectionModel.getSelectedObject();
			notebookGroupPermission = new LinkedHashMap<Key, Integer>();
			notebookUserPermission = new LinkedHashMap<String, Integer>();
			// TODO save all the notebook permission
			for (int i = 1; i < notebookPermissionTable.getRowCount(); i++) {
				String keyString = notebookPermissionTable.getWidget(i, 0)
						.getElement().getInnerHTML();
				Key key = KeyFactory.stringToKey(keyString);
				int permission = 0;
				if (((CheckBox) notebookPermissionTable.getWidget(i, 2))
						.getValue()) {
					permission = 2;
				} else if (((CheckBox) notebookPermissionTable.getWidget(i, 1))
						.getValue()) {
					permission = 1;
				}
				if (permission != 0)
					notebookGroupPermission.put(key, permission);
			}
		}

		if (this.notePermissionContentPanel.isVisible()) {
			note = this.noteSelectionModel.getSelectedObject();
			noteGroupPermission = new LinkedHashMap<Key, Integer>();
			noteUserPermission = new LinkedHashMap<String, Integer>();
			for (int i = 1; i < notePermissionTable.getRowCount(); i++) {
				String keyString = notePermissionTable.getWidget(i, 0)
						.getElement().getInnerHTML();
				int permission = 0;
				if (((CheckBox) notePermissionTable.getWidget(i, 2)).getValue()) {
					permission = 2;
				} else if (((CheckBox) notePermissionTable.getWidget(i, 1))
						.getValue()) {
					permission = 1;
				}
				// check if it is a email. If yes, then this row is a user,
				// otherwise is a group
				if (permission != 0) {
					if (keyString
							.matches("^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$")) {
						noteUserPermission.put(keyString, permission);
					} else {
						noteGroupPermission.put(
								KeyFactory.stringToKey(keyString), permission);
					}
				}
			}
		}
		this.presenter.saveNotePermissionChanges(notebook,
				notebookGroupPermission, notebookUserPermission, note,
				noteGroupPermission, noteUserPermission);
		// this.notePermissionSaveButton.setEnabled(false);
	}

	@UiHandler("notePermissionRevertButton")
	void onClickNotePermissionRevert(ClickEvent e) {
		if (noteSelectionModel.getSelectedObject() != null) {
			this.presentNotePermission(noteSelectionModel.getSelectedObject());
		} else {
			this.presentNotebookPermission(notePermissionSelectionModel
					.getSelectedObject());
		}
		// this.notePermissionSaveButton.setEnabled(false);
	}

	@UiField(provided = true)
	CellList<User> userList = new CellList<User>(new AbstractCell<User>() {
		@Override
		public void render(Context context, User user, SafeHtmlBuilder sb) {
			if (user == null) {
				return;
			}

			sb.appendHtmlConstant("<div style=\"padding:5px 20px;\">");
			sb.appendEscaped(user.getEmail());
			sb.appendHtmlConstant("</div>");
		}
	});
	@UiField(provided = true)
	CellList<InfoNote> noteList = new CellList<InfoNote>(
			new AbstractCell<InfoNote>() {
				@Override
				public void render(Context context, InfoNote note,
						SafeHtmlBuilder sb) {
					if (note == null) {
						return;
					}

					sb.appendHtmlConstant("<div style=\"padding:5px 20px;\">");
					sb.appendEscaped(note.getTitle());
					sb.appendHtmlConstant("</div>");
				}
			});

	@UiField(provided = true)
	CellList<Group> userAccessList = new CellList<Group>(
			new AbstractCell<Group>() {
				@Override
				public void render(Context context, Group group,
						SafeHtmlBuilder sb) {
					if (group == null) {
						return;
					}

					sb.appendHtmlConstant("<div style=\"padding:5px 19px;\">");
					sb.appendEscaped(group.getName());
					sb.appendHtmlConstant("</div>");
				}
			});
	@UiField(provided = true)
	CellList<Notebook> notePermissionList = new CellList<Notebook>(
			new AbstractCell<Notebook>() {
				@Override
				public void render(Context context, Notebook notebook,
						SafeHtmlBuilder sb) {
					if (notebook == null) {
						return;
					}

					sb.appendHtmlConstant("<div style=\"padding:5px 19px;\">");
					sb.appendEscaped(notebook.getName());
					sb.appendHtmlConstant("</div>");
				}
			});
	@UiField
	DeckPanel contentDeck;
	@UiField
	HTMLPanel groupAccessContentPanel;
	@UiField
	Label groupLabel;
	@UiField
	HTMLPanel groupAccessContent;
	@UiField
	HTMLPanel userAccessContentPanel;
	@UiField
	Label userLabel;
	@UiField
	HTMLPanel userAccessContent;
	@UiField
	HTMLPanel notebookPermissionContentPanel;
	@UiField
	Label notebookLabel;
	@UiField
	HTMLPanel notebookPermissionContent;
	@UiField
	HTMLPanel notePermissionContentPanel;
	@UiField
	Label noteLabel;
	@UiField
	HTMLPanel notePermissionContent;

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

	@Override
	public void setGroupList(List<Group> groups) {
		this.groupDataProvider.getList().clear();
		this.groupDataProvider.getList().add(new Group("All Users"));
		this.groupDataProvider.getList().addAll(groups);
		this.groupDataProvider.getList().add(new Group("UnGrouped"));
	}

	@Override
	public void setNotebookList(List<Notebook> notebooks) {
		this.notebookDataProvider.getList().clear();
		this.notebookDataProvider.getList().add(new Notebook("All Notes"));
		this.notebookDataProvider.getList().addAll(notebooks);
	}

	@Override
	public void setUserList(List<User> users) {
		this.userDataProvider.getList().clear();
		this.userDataProvider.getList().addAll(users);
		this.subListDeck.showWidget(0);
	}

	@Override
	public void setNoteList(List<InfoNote> notes) {
		this.noteDataProvider.getList().clear();
		this.noteDataProvider.getList().addAll(notes);
		this.subListDeck.showWidget(1);
	}

	@Override
	public void setSelectedGroup(Group group) {
		this.userAccessSelectionModel.setSelected(group, true);
	}

	@Override
	public void setSelectedUser(User user) {
		this.userSelectionModel.setSelected(user, true);
	}

	@Override
	public void setSelectedNotebook(Notebook notebook) {
		this.notePermissionSelectionModel.setSelected(notebook, true);
	}

	@Override
	public void setSelectedNote(InfoNote note) {
		this.noteSelectionModel.setSelected(note, true);
	}

	@Override
	public void onGroupsChanged(GroupsChangedEvent event) {
		this.presenter.loadGroupList();
		if (this.userAccessSelectionModel.getSelectedObject() != null) {
			this.presenter.loadUserList(this.userAccessSelectionModel
					.getSelectedObject());
		}
	}

	@Override
	public void onNotebookChanged(NotebookChangedEvent event) {
		this.presenter.loadNotebookList();
		if (this.notePermissionSelectionModel.getSelectedObject() != null) {
			this.presenter.loadNoteList(this.notePermissionSelectionModel
					.getSelectedObject());
		}
	}
	
	private String getGroupName(Key key) {
		return DataManager.getAllGroups().get(key).getName();
	}
	
	private String getNotebookName(Key key) {
		return DataManager.getNotebooks().get(key).getName();
	}
	
	private String getUserNickname(String email) {
		return DataManager.getAllFriends().get(email).getNickname();
	}
	
	private void fillNoteTitle(final Label label, Key key) {
		InfoNoteServiceAsync service = GWT.create(InfoNoteService.class);
		service.getNoteByKey(key, new AsyncCallback<InfoNote>(){

			@Override
			public void onFailure(Throwable caught) {
				GWT.log(caught.getMessage());
			}

			@Override
			public void onSuccess(InfoNote result) {
				label.setText(result.getTitle());
			}
			
		});
	}
}
