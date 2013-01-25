package com.sid.cloudynote.client.view;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.appengine.api.datastore.Key;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DecoratedPopupPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.TreeListener;
import com.google.gwt.user.client.ui.Widget;
import com.sid.cloudynote.client.model.DataManager;
import com.sid.cloudynote.client.model.Notebook;
import com.sid.cloudynote.client.service.NotebookService;
import com.sid.cloudynote.client.service.NotebookServiceAsync;

public class NoteBookListPanel extends Composite {

	private static NotebookListPanelUiBinder uiBinder = GWT
			.create(NotebookListPanelUiBinder.class);

	interface NotebookListPanelUiBinder extends
			UiBinder<Widget, NoteBookListPanel> {
	}
	
	private NoteListPanel notePanel;
	
	public void setNotePanel(NoteListPanel notePanel) {
		this.notePanel = notePanel;
	}

	public interface Images extends ClientBundle, Tree.Resources {
		ImageResource drafts();

		ImageResource home();

		ImageResource inbox();

		ImageResource sent();

		ImageResource templates();

		ImageResource trash();
		// @Source("noimage.png")
		// ImageResource treeLeaf();
	}

	private Images images;
	private NotebookTreeItem allNotes;
	private NotebookTreeItem tags;

	public NoteBookListPanel() {
		sinkEvents(Event.ONMOUSEDOWN | Event.ONMOUSEUP | Event.ONDBLCLICK
				| Event.ONCONTEXTMENU);
		initWidget(uiBinder.createAndBindUi(this));
		images = GWT.create(Images.class);

		allNotes = new NotebookTreeItem(new TreeRootItem(images.home(),
				"All Notes"));
		loadNotebooks();
		allNotes.setState(true);
		noteBooksTree.addItem(allNotes);
		// addImageItem(allNotes, "Inbox", images.inbox());
		// addImageItem(allNotes, "Drafts", images.drafts());
		// addImageItem(allNotes, "Templates", images.templates());
		// addImageItem(allNotes, "Sent", images.sent());
		// addImageItem(allNotes, "Trash", images.trash());
		tags = new NotebookTreeItem("Tags");
		tagsTree.addItem(tags);
		
		noteBooksTree.addTreeListener(new TreeListener() {

			@Override
			public void onTreeItemSelected(TreeItem item) {
				if (item.getChildCount() == 0) {
					Notebook selectedNotebook = ((NotebookTreeItem) item).getNotebook();
//					System.out.println(DataManager.getNotebooks().size()+" "+selectedNotebook.getName());
					DataManager.setCurrentNotebook(selectedNotebook.getKey());
					notePanel.loadNotes();
//					System.out.println("notebook selected "+DataManager.getCurrentNotebookKey());
				}
			}

			@Override
			public void onTreeItemStateChanged(TreeItem item) {
				// Window.alert("state changed" + item.getTitle());
			}

		});

		noteBooksTree.addMouseDownHandler(new MouseDownHandler() {
			@Override
			public void onMouseDown(MouseDownEvent event) {
				// System.out.println("Mouse Down!");
				if (event.getNativeButton() == NativeEvent.BUTTON_RIGHT) {
					// System.out.println("Mouse Down Button Right!");
					event.preventDefault();
					event.stopPropagation();

					Tree tree = (Tree) event.getSource();
					NotebookTreeItem item = (NotebookTreeItem) getTreeItemAt(
							tree, event.getNativeEvent().getClientY());
					if (item != null) {
						DecoratedPopupPanel popup = new DecoratedPopupPanel(true);
						popup.add(new Button(item.getText()));
						popup.setPopupPosition(event.getClientX(),event.getClientY());
						event.preventDefault();
						event.stopPropagation();
						popup.show();
//						Window.alert("right click on notebook "
//								+ item.getNotebook().getName());
					}
				}
			}

			public TreeItem getTreeItemAt(Tree tree, int p_y) {
				TreeItem nearest = null;
				TreeItem exact = null;
				for (int i = 0; i < tree.getItemCount(); i++) {
					for (int j = 0; j < tree.getItem(i).getChildCount(); j++) {
						TreeItem item = tree.getItem(i).getChild(j);
						int top = item.getAbsoluteTop();
						int height = item.getOffsetHeight();
						if (top >= 0 && height != 0) {
							nearest = item;
							if (p_y >= top && p_y < top + height) {
								exact = item;
							}
						}
					}
				}
				return exact;
			}
		});
	}

	@UiField
	Tree noteBooksTree;
	@UiField
	Tree tagsTree;

	// @UiHandler("noteBooksTree")
	// void onClick(SelectionEvent<TreeItem> e) {
	// Window.alert(e.getSelectedItem().getClass().getName());
	// }

	// private NotebookTreeItem addImageItem(NotebookTreeItem root, String
	// title,
	// ImageResource imageProto) {
	// NotebookTreeItem item = new NotebookTreeItem(imageItemHTML(imageProto,
	// title));
	// root.addItem(item);
	// return item;
	// }

	private NotebookTreeItem addImageItem(NotebookTreeItem root,
			Notebook notebook, ImageResource imageProto) {
		NotebookTreeItem item = new NotebookTreeItem(imageItemHTML(imageProto,
				notebook.getName()));
		item.setNotebook(notebook);
		root.addItem(item);
		return item;
	}

	private String imageItemHTML(ImageResource imageProto, String title) {
		return AbstractImagePrototype.create(imageProto).getHTML() + " "
				+ title;
	}

	public void loadNotebooks() {
		NotebookServiceAsync service = (NotebookServiceAsync) GWT
				.create(NotebookService.class);
		AsyncCallback<List<Notebook>> callback = new AsyncCallback<List<Notebook>>() {

			@Override
			public void onFailure(Throwable caught) {
				System.out.println("falied! getNotebooksList");
				caught.printStackTrace();
			}

			@Override
			public void onSuccess(List<Notebook> result) {
				if (result.size() != 0) {
					Map<Key,Notebook> notebookMap = new HashMap<Key,Notebook>();
					for(Notebook notebook : result){
						notebookMap.put(notebook.getKey(), notebook);
					}
					DataManager.setNotebooks(notebookMap);
					DataManager.setCurrentNotebook(result.get(0).getKey());
					
					allNotes.removeItems();
					for (Notebook notebook : result) {
						addImageItem(allNotes, notebook, images.templates());
					}
//					System.out.println("notebook list loaded");
				} else {
					System.out.println("No notebooks exist!");
					createDefaultNotebook();
					loadNotebooks();
				}
			}
		};
		service.getPaginationData(callback);
	}
	
	private void createDefaultNotebook() {
		NotebookServiceAsync service = (NotebookServiceAsync) GWT
				.create(NotebookService.class);
		AsyncCallback<Void> callback = new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Add Notebook falied!");
				caught.printStackTrace();
			}

			@Override
			public void onSuccess(Void result) {
				GWT.log("Default notebook created...");
			}
		};
		service.add(new Notebook("Default"), callback);
	}

	private class NotebookTreeItem extends TreeItem implements
			ContextMenuHandler {
		private Notebook notebook;

		NotebookTreeItem() {
			super();
			addDomHandler(this, ContextMenuEvent.getType());
		}

		NotebookTreeItem(Widget widget) {
			super(widget);
			addDomHandler(this, ContextMenuEvent.getType());
		}

		NotebookTreeItem(String s) {
			super(s);
			addDomHandler(this, ContextMenuEvent.getType());
		}

		public void setNotebook(Notebook notebook) {
			this.notebook = notebook;
		}

		public Notebook getNotebook() {
			return this.notebook;
		}

		@Override
		public void onContextMenu(ContextMenuEvent event) {
			event.preventDefault();
			event.stopPropagation();

			// PopupPanel contextMenu = new PopupPanel(true);
			// contextMenu.add(new HTML(notebook.getName()));
			// contextMenu.setPopupPosition(event.getNativeEvent().getClientX(),
			// event.getNativeEvent().getClientY());
			// event.getNativeEvent().stopPropagation();
			// contextMenu.show();
		}
	}
}
