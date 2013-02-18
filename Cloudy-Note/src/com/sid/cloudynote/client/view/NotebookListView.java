package com.sid.cloudynote.client.view;

import java.util.List;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
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
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import com.sid.cloudynote.client.event.INotebookChangedHandler;
import com.sid.cloudynote.client.event.NotebookChangedEvent;
import com.sid.cloudynote.client.view.interfaces.INotebookListView;
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
	VerticalPanel content;

	@UiField
	ShowMorePagerPanel pagerPanel;
	@UiField
	Tree tagsTree;

	private Images images;
	private NotebookTreeItem tags;
	private Presenter presenter;
	private CellList<Notebook> cellList;
	private SingleSelectionModel<Notebook> selectionModel;
	private ListDataProvider<Notebook> dataProvider = new ListDataProvider<Notebook>();
	public static final ProvidesKey<Notebook> KEY_PROVIDER = new ProvidesKey<Notebook>() {
		@Override
		public Object getKey(Notebook notebook) {
			return notebook == null ? null : notebook.getKey();
		}
	};

	static class NotebookCell extends AbstractCell<Notebook> {
		private final String imageHtml;

		public NotebookCell(ImageResource image) {
			this.imageHtml = AbstractImagePrototype.create(image).getHTML();
		}

		@Override
		public void render(Context context, Notebook notebook,
				SafeHtmlBuilder sb) {
			if (notebook != null) {
				sb.appendHtmlConstant(imageHtml);
				sb.appendEscaped(notebook.getName());
//				sb.appendHtmlConstant("(");
//				sb.appendEscaped(notebook.getUser().getEmail());
//				sb.appendHtmlConstant(")");
			}
		}
	}

	public NotebookListView() {
		// sinkEvents(Event.ONMOUSEDOWN | Event.ONMOUSEUP | Event.ONDBLCLICK
		// | Event.ONCONTEXTMENU);

		initWidget(uiBinder.createAndBindUi(this));
		images = GWT.create(Images.class);

		tags = new NotebookTreeItem("Tags");
		tagsTree.addItem(tags);

		// allNotes = new NotebookTreeItem(new TreeRootItem(images.home(),
		// "Notebooks"));
		// allNotes.setState(true);
		// noteBooksTree.addItem(allNotes);
		//
		// noteBooksTree.addTreeListener(new TreeListener() {
		//
		// @Override
		// public void onTreeItemSelected(TreeItem item) {
		// if (item.getChildCount() == 0) {
		// Notebook selectedNotebook = ((NotebookTreeItem) item)
		// .getNotebook();
		// // if (!selectedNotebook.getKey().equals(
		// // DataManager.getCurrentNotebookKey()))
		// // noteListPanel.loadNotes();
		// if (presenter != null) {
		// presenter.onNotebookItemSelected(selectedNotebook);
		// }
		//
		// }
		// }
		//
		// @Override
		// public void onTreeItemStateChanged(TreeItem item) {
		// }
		// });

		NotebookCell notebookCell = new NotebookCell(images.drafts());

		cellList = new CellList<Notebook>(notebookCell, KEY_PROVIDER);
		cellList.setPageSize(30);
		cellList.setKeyboardPagingPolicy(KeyboardPagingPolicy.INCREASE_RANGE);
		cellList.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.BOUND_TO_SELECTION);

		selectionModel = new SingleSelectionModel<Notebook>(KEY_PROVIDER);
		cellList.setSelectionModel(selectionModel);

		selectionModel
				.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
					public void onSelectionChange(SelectionChangeEvent event) {
						Notebook notebook = selectionModel.getSelectedObject();
						presenter.onNotebookItemSelected(notebook);
					}
				});
		dataProvider.addDataDisplay(cellList);
		pagerPanel.setDisplay(cellList);

		// noteBooksTree.addMouseDownHandler(new MouseDownHandler() {
		// @Override
		// public void onMouseDown(MouseDownEvent event) {
		// // System.out.println("Mouse Down!");
		// if (event.getNativeButton() == NativeEvent.BUTTON_RIGHT) {
		// // System.out.println("Mouse Down Button Right!");
		// event.preventDefault();
		// event.stopPropagation();
		//
		// Tree tree = (Tree) event.getSource();
		// NotebookTreeItem item = (NotebookTreeItem) getTreeItemAt(
		// tree, event.getNativeEvent().getClientY());
		// if (item != null) {
		// DecoratedPopupPanel popup = new DecoratedPopupPanel(
		// true);
		// popup.add(new Button(item.getText()));
		// popup.setPopupPosition(event.getClientX(),
		// event.getClientY());
		// event.preventDefault();
		// event.stopPropagation();
		// popup.show();
		// // Window.alert("right click on notebook "
		// // + item.getNotebook().getName());
		// }
		// }
		// }
		//
		// public TreeItem getTreeItemAt(Tree tree, int p_y) {
		// TreeItem exact = null;
		// for (int i = 0; i < tree.getItemCount(); i++) {
		// for (int j = 0; j < tree.getItem(i).getChildCount(); j++) {
		// TreeItem item = tree.getItem(i).getChild(j);
		// int top = item.getAbsoluteTop();
		// int height = item.getOffsetHeight();
		// if (top >= 0 && height != 0) {
		// if (p_y >= top && p_y < top + height) {
		// exact = item;
		// }
		// }
		// }
		// }
		// return exact;
		// }
		// });
	}

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
	public void setNotebookList(List<Notebook> result) {
		List<Notebook> notebooks = dataProvider.getList();
		notebooks.clear();
		notebooks.addAll(result);
//		if (DataManager.getCurrentNotebook() != null)
//			selectionModel.setSelected(DataManager.getCurrentNotebook(), true);
	}

	@Override
	public Widget asWidget() {
		return this.content;
	}
}
