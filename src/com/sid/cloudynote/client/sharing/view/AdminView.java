package com.sid.cloudynote.client.sharing.view;

import java.util.List;
import java.util.Map;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellList;
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
import com.sid.cloudynote.client.sharing.view.interfaces.IAdminView;
import com.sid.cloudynote.client.view.Container;
import com.sid.cloudynote.shared.Group;
import com.sid.cloudynote.shared.InfoNote;
import com.sid.cloudynote.shared.Notebook;
import com.sid.cloudynote.shared.User;

public class AdminView extends Composite implements IAdminView {
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

	interface AdminViewUiBinder extends UiBinder<Widget, AdminView> {
	}

	interface Style extends CssResource {

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
	
	private void presentGroupAccess(Group group) {
		this.contentDeck.showWidget(0);
		this.groupAccessContentPanel.setVisible(true);
		this.userAccessContentPanel.setVisible(false);
		this.groupLabel.setText(group.getName()+" Access");
		this.groupAccessContent.clear();
		if(group.getAccess().size()!=0){
			final FlexTable table = new FlexTable();
			//generate the header
			CheckBox allRead = new CheckBox("Read");
			CheckBox allWrite = new CheckBox("Write");
			allRead.addValueChangeHandler(new ValueChangeHandler<Boolean>(){

				@Override
				public void onValueChange(ValueChangeEvent<Boolean> event) {
					for(int i=1; i<table.getRowCount();i++){
						((CheckBox)table.getWidget(i, 1)).setValue(event.getValue());
					}
				}
				
			});
			allWrite.addValueChangeHandler(new ValueChangeHandler<Boolean>(){

				@Override
				public void onValueChange(ValueChangeEvent<Boolean> event) {
					for(int i=1; i<table.getRowCount();i++){
						((CheckBox)table.getWidget(i, 2)).setValue(event.getValue());
					}
				}
				
			});
			table.setWidget(0, 1, allRead);
			table.setWidget(0, 2, allWrite);
			table.setWidget(0, 3, new Label("Notebook"));
			table.setWidget(0, 4, new Label("Note"));
			
			//generate table content
			for(Map.Entry<Key, Integer> entry : group.getAccess().entrySet()){
				int row = table.getRowCount();
				Label rowValue = new Label();
				rowValue.getElement().setInnerHTML(KeyFactory.keyToString(entry.getKey()));
				rowValue.setVisible(false);
				CheckBox read = new CheckBox("Read");
				CheckBox write = new CheckBox("Write");
				table.setWidget(row, 0, rowValue);
				table.setWidget(row, 1, read);
				table.setWidget(row, 2, write);
				table.setWidget(row, 3, new Label("-"));
				table.setWidget(row, 4, new Label("NoteKey:"+entry.getKey()));
				if(entry.getValue()==1){
					read.setValue(true);
					write.setValue(false);
				} else if (entry.getValue()==2) {
					read.setValue(true);
					write.setValue(true);
				}
			}
			this.groupAccessContent.add(table);
			this.groupAccessContent.add(new Button("Save",new ClickHandler(){

				@Override
				public void onClick(ClickEvent event) {
					for(int i=1;i<table.getRowCount();i++){
						String keyString = table.getWidget(i,0).getElement().getInnerHTML();
						Key key = KeyFactory.stringToKey(keyString);
						int permission = 0;
						if(((CheckBox)table.getWidget(i,2)).getValue()){
							permission = 2;
						} else if(((CheckBox)table.getWidget(i,1)).getValue()){
							permission = 1;
						}
						// TODO update the database
					}
				}
				
			}));
		}
	}
	
	private void presentNotebookPermission(Notebook notebook) {
		this.contentDeck.showWidget(1);
		this.notebookPermissionContentPanel.setVisible(true);
		this.notePermissionContentPanel.setVisible(false);
		this.notebookLabel.setText(notebook.getName()+" Permission");
		this.notePermissionContent.clear();
		if(notebook.getAccess().size()!=0){
			final FlexTable table = new FlexTable();
			//generate the header
			CheckBox allRead = new CheckBox("Read");
			CheckBox allWrite = new CheckBox("Write");
			allRead.addValueChangeHandler(new ValueChangeHandler<Boolean>(){

				@Override
				public void onValueChange(ValueChangeEvent<Boolean> event) {
					for(int i=1; i<table.getRowCount();i++){
						((CheckBox)table.getWidget(i, 1)).setValue(event.getValue());
					}
				}
				
			});
			allWrite.addValueChangeHandler(new ValueChangeHandler<Boolean>(){

				@Override
				public void onValueChange(ValueChangeEvent<Boolean> event) {
					for(int i=1; i<table.getRowCount();i++){
						((CheckBox)table.getWidget(i, 2)).setValue(event.getValue());
					}
				}
				
			});
			table.setWidget(0, 1, allRead);
			table.setWidget(0, 2, allWrite);
			table.setWidget(0, 3, new Label("Group"));
			table.setWidget(0, 4, new Label("User"));
			
			//generate table content
			for(Map.Entry<String, Integer> entry : notebook.getAccess().entrySet()){
				int row = table.getRowCount();
				Label rowValue = new Label();
				rowValue.getElement().setInnerHTML(entry.getKey());
				rowValue.setVisible(false);
				CheckBox read = new CheckBox("Read");
				CheckBox write = new CheckBox("Write");
				table.setWidget(row, 0, rowValue);
				table.setWidget(row, 1, read);
				table.setWidget(row, 2, write);
				table.setWidget(row, 3, new Label("-"));
				table.setWidget(row, 4, new Label(entry.getKey()));
				if(entry.getValue()==1){
					read.setValue(true);
					write.setValue(false);
				} else if (entry.getValue()==2) {
					read.setValue(true);
					write.setValue(true);
				}
			}
			this.notebookPermissionContent.add(table);
			this.notebookPermissionContent.add(new Button("Save",new ClickHandler(){

				@Override
				public void onClick(ClickEvent event) {
					for(int i=1;i<table.getRowCount();i++){
						String keyString = table.getWidget(i,0).getElement().getInnerHTML();
						Key key = KeyFactory.stringToKey(keyString);
						int permission = 0;
						if(((CheckBox)table.getWidget(i,2)).getValue()){
							permission = 2;
						} else if(((CheckBox)table.getWidget(i,1)).getValue()){
							permission = 1;
						}
						// TODO update the database
					}
				}
				
			}));
		}
	}

	private void presentUserAccess(User user) {
		this.contentDeck.showWidget(0);
		//check whether or not present the group access
		if (userAccessSelectionModel.getSelectedObject() != null) {
			Group group = userAccessSelectionModel.getSelectedObject();
			if(group.getKey()!=null){
				presentGroupAccess(group);
			} else {
				this.groupAccessContentPanel.setVisible(false);
			}
		}
		this.userAccessContentPanel.setVisible(true);
		this.userAccessContent.clear();
		
		this.userLabel.setText(user.getEmail()+" Access");
		if(user.getAccess().size()!=0){
			final FlexTable table = new FlexTable();
			//generate the header
			CheckBox allRead = new CheckBox("Read");
			CheckBox allWrite = new CheckBox("Write");
			allRead.addValueChangeHandler(new ValueChangeHandler<Boolean>(){

				@Override
				public void onValueChange(ValueChangeEvent<Boolean> event) {
					for(int i=1; i<table.getRowCount();i++){
						((CheckBox)table.getWidget(i, 1)).setValue(event.getValue());
					}
				}
				
			});
			allWrite.addValueChangeHandler(new ValueChangeHandler<Boolean>(){

				@Override
				public void onValueChange(ValueChangeEvent<Boolean> event) {
					for(int i=1; i<table.getRowCount();i++){
						((CheckBox)table.getWidget(i, 2)).setValue(event.getValue());
					}
				}
				
			});
			table.setWidget(0, 1, allRead);
			table.setWidget(0, 2, allWrite);
			table.setWidget(0, 3, new Label("Notebook"));
			table.setWidget(0, 4, new Label("Note"));
			
			//generate table content
			for(Map.Entry<Key, Integer> entry : user.getAccess().entrySet()){
				int row = table.getRowCount();
				Label rowValue = new Label();
				rowValue.getElement().setInnerHTML(KeyFactory.keyToString(entry.getKey()));
				rowValue.setVisible(false);
				CheckBox read = new CheckBox("Read");
				CheckBox write = new CheckBox("Write");
				table.setWidget(row, 0, rowValue);
				table.setWidget(row, 1, read);
				table.setWidget(row, 2, write);
				table.setWidget(row, 3, new Label("-"));
				table.setWidget(row, 4, new Label("NoteKey:"+entry.getKey()));
				if(entry.getValue()==1){
					read.setValue(true);
					write.setValue(false);
				} else if (entry.getValue()==2) {
					read.setValue(true);
					write.setValue(true);
				}
			}
			this.userAccessContent.add(table);
			this.userAccessContent.add(new Button("Save",new ClickHandler(){

				@Override
				public void onClick(ClickEvent event) {
					for(int i=1;i<table.getRowCount();i++){
						String keyString = table.getWidget(i,0).getElement().getInnerHTML();
						Key key = KeyFactory.stringToKey(keyString);
						int permission = 0;
						if(((CheckBox)table.getWidget(i,2)).getValue()){
							permission = 2;
						} else if(((CheckBox)table.getWidget(i,1)).getValue()){
							permission = 1;
						}
						// TODO update the database
					}
				}
			}));
		}
	}
	
	private void presentNotePermission(InfoNote note) {
		this.contentDeck.showWidget(1);
		//check whether or not present the notebook access
		if (notePermissionSelectionModel.getSelectedObject() != null) {
			Notebook notebook = notePermissionSelectionModel.getSelectedObject();
			if(notebook.getKey()!=null){
				presentNotebookPermission(notebook);
			} else {
				this.notebookPermissionContentPanel.setVisible(false);
			}
		}
		this.notePermissionContentPanel.setVisible(true);
		this.notePermissionContent.clear();
		
		this.noteLabel.setText(note.getTitle()+" Permission");
		if(note.getGroupAccess().size()!=0 || note.getUserAccess().size()!=0){
			final FlexTable table = new FlexTable();
			//generate the header
			CheckBox allRead = new CheckBox("Read");
			CheckBox allWrite = new CheckBox("Write");
			allRead.addValueChangeHandler(new ValueChangeHandler<Boolean>(){

				@Override
				public void onValueChange(ValueChangeEvent<Boolean> event) {
					for(int i=1; i<table.getRowCount();i++){
						((CheckBox)table.getWidget(i, 1)).setValue(event.getValue());
					}
				}
				
			});
			allWrite.addValueChangeHandler(new ValueChangeHandler<Boolean>(){

				@Override
				public void onValueChange(ValueChangeEvent<Boolean> event) {
					for(int i=1; i<table.getRowCount();i++){
						((CheckBox)table.getWidget(i, 2)).setValue(event.getValue());
					}
				}
				
			});
			table.setWidget(0, 1, allRead);
			table.setWidget(0, 2, allWrite);
			table.setWidget(0, 3, new Label("Group"));
			table.setWidget(0, 4, new Label("User"));
			
			//generate table content
			//first part to generate group access
			for(Map.Entry<Key, Integer> entry : note.getGroupAccess().entrySet()){
				int row = table.getRowCount();
				Label rowValue = new Label();
				rowValue.getElement().setInnerHTML(KeyFactory.keyToString(entry.getKey()));
				rowValue.setVisible(false);
				CheckBox read = new CheckBox("Read");
				CheckBox write = new CheckBox("Write");
				table.setWidget(row, 0, rowValue);
				table.setWidget(row, 1, read);
				table.setWidget(row, 2, write);
				table.setWidget(row, 3, new Label("GroupKey:"+entry.getKey()));
				table.setWidget(row, 4, new Label("-"));
				if(entry.getValue()==1){
					read.setValue(true);
					write.setValue(false);
				} else if (entry.getValue()==2) {
					read.setValue(true);
					write.setValue(true);
				}
			}
			//second part to generate user access
			for(Map.Entry<String, Integer> entry : note.getUserAccess().entrySet()){
				int row = table.getRowCount();
				Label rowValue = new Label();
				rowValue.getElement().setInnerHTML(entry.getKey());
				rowValue.setVisible(false);
				CheckBox read = new CheckBox("Read");
				CheckBox write = new CheckBox("Write");
				table.setWidget(row, 0, rowValue);
				table.setWidget(row, 1, read);
				table.setWidget(row, 2, write);
				table.setWidget(row, 3, new Label("-"));
				table.setWidget(row, 4, new Label("User:"+entry.getKey()));
				if(entry.getValue()==1){
					read.setValue(true);
					write.setValue(false);
				} else if (entry.getValue()==2) {
					read.setValue(true);
					write.setValue(true);
				}
			}
			
			this.notePermissionContent.add(table);
			this.notePermissionContent.add(new Button("Save",new ClickHandler(){

				@Override
				public void onClick(ClickEvent event) {
					for(int i=1;i<table.getRowCount();i++){
						String keyString = table.getWidget(i,0).getElement().getInnerHTML();
						int permission = 0;
						if(((CheckBox)table.getWidget(i,2)).getValue()){
							permission = 2;
						} else if(((CheckBox)table.getWidget(i,1)).getValue()){
							permission = 1;
						}
						// TODO update the database
					}
				}
			}));
		}
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
							presentNotePermission(note);
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
							presentUserAccess(user);
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
						if (group == null)
							return;
						presenter.onUserAccessItemSelected(group);
						presentGroupAccess(group);
						
						if (notePermissionSelectionModel.getSelectedObject() != null) {
							notePermissionSelectionModel.setSelected(
									notePermissionSelectionModel
											.getSelectedObject(), false);
						}
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
						if (notebook == null)
							return;
						presenter.onNotePermissionItemSelected(notebook);
						presentNotebookPermission(notebook);

						if (userAccessSelectionModel.getSelectedObject() != null) {
							userAccessSelectionModel.setSelected(
									userAccessSelectionModel
											.getSelectedObject(), false);
						}
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
	Label subListLabel;

	@Override
	public void setSubListLabel(String text) {
		this.subListLabel.setText(text);
	}

	@UiField
	DeckPanel subListDeck;
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
}
