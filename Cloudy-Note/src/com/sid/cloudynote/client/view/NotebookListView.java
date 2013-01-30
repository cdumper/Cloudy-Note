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
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DecoratedPopupPanel;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.StackLayoutPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.TreeListener;
import com.google.gwt.user.client.ui.Widget;
import com.sid.cloudynote.client.event.INotebookChangedHandler;
import com.sid.cloudynote.client.event.NotebookChangedEvent;
import com.sid.cloudynote.shared.Notebook;

public class NotebookListView extends ResizeComposite implements
		INotebookChangedHandler, INotebookListView {

	@UiTemplate("NoteBookListView.ui.xml")
	interface NotebookListPanelUiBinder extends
			UiBinder<Widget, NotebookListView> {
	}

	private static NotebookListPanelUiBinder uiBinder = GWT
			.create(NotebookListPanelUiBinder.class);

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
	
	@UiField
	Container container;
	public Container getContainer() {
		return container;
	}
	@UiField
	StackLayoutPanel stackContent;

	@UiField
	Tree noteBooksTree;
	@UiField
	Tree tagsTree;

	private Presenter presenter;

	private NoteListView noteListPanel;

	public void setNotePanel(NoteListView noteListPanel) {
		this.noteListPanel = noteListPanel;
	}

	private Images images;
	private NotebookTreeItem allNotes;
	private NotebookTreeItem tags;

	public NotebookListView() {
		// sinkEvents(Event.ONMOUSEDOWN | Event.ONMOUSEUP | Event.ONDBLCLICK
		// | Event.ONCONTEXTMENU);
		initWidget(uiBinder.createAndBindUi(this));
		images = GWT.create(Images.class);

		allNotes = new NotebookTreeItem(new TreeRootItem(images.home(),
				"All Notes"));
		allNotes.setState(true);
		noteBooksTree.addItem(allNotes);

		tags = new NotebookTreeItem("Tags");
		tagsTree.addItem(tags);

		noteBooksTree.addTreeListener(new TreeListener() {

			@Override
			public void onTreeItemSelected(TreeItem item) {
				if (item.getChildCount() == 0) {
					Notebook selectedNotebook = ((NotebookTreeItem) item)
							.getNotebook();
//					if (!selectedNotebook.getKey().equals(
//							DataManager.getCurrentNotebookKey())) 
						// noteListPanel.loadNotes();
						if (presenter != null) {
							presenter.onNotebookItemSelected(selectedNotebook);
						}
					
				}
			}

			@Override
			public void onTreeItemStateChanged(TreeItem item) {
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
						DecoratedPopupPanel popup = new DecoratedPopupPanel(
								true);
						popup.add(new Button(item.getText()));
						popup.setPopupPosition(event.getClientX(),
								event.getClientY());
						event.preventDefault();
						event.stopPropagation();
						popup.show();
						// Window.alert("right click on notebook "
						// + item.getNotebook().getName());
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
	@Override
	public void onNotebookChanged(NotebookChangedEvent event) {
		presenter.loadNotebookList();
	}
	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}
	@Override
	public void setNotebookList(List<Notebook> notebooks) {
		Map<Key, Notebook> notebookMap = new HashMap<Key, Notebook>();
		allNotes.removeItems();
		for (Notebook notebook : notebooks) {
			notebookMap.put(notebook.getKey(), notebook);
			addImageItem(allNotes, notebook, images.templates());
		}
		allNotes.setState(true);
		// noteListPanel.loadNotes();
	}
	@Override
	public Widget asWidget() {
		return this.stackContent;
//		return this.container;
	}
}
