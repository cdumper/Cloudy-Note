package com.sid.cloudynote.client.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import com.sid.cloudynote.client.DataManager;
import com.sid.cloudynote.client.event.INotebookChangedHandler;
import com.sid.cloudynote.client.event.NotebookChangedEvent;
import com.sid.cloudynote.client.service.NotebookService;
import com.sid.cloudynote.client.service.NotebookServiceAsync;
import com.sid.cloudynote.client.view.interfaces.INotebookListView;
import com.sid.cloudynote.shared.Notebook;
import com.sid.cloudynote.shared.Tag;

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
	DisclosurePanel notebookPanel;
	@UiField
	DisclosurePanel tagPanel;
	@UiField
	ShowMorePagerPanel pagerPanel;

	private Images images;
	private Presenter presenter;
	private NotebookCell notebookCell;
	private TagCell tagCell;
	private CellList<Tag> tagsCellList;
	private CellList<Notebook> notebooksCellList;
	private SingleSelectionModel<Notebook> notebookSelectionModel;
	private SingleSelectionModel<Tag> tagSelectionModel;
	private ListDataProvider<Notebook> notebookDataProvider = new ListDataProvider<Notebook>();
	private ListDataProvider<Tag> tagDataProvider = new ListDataProvider<Tag>();
	public static final ProvidesKey<Notebook> NOTEBOOK_KEY_PROVIDER = new ProvidesKey<Notebook>() {
		@Override
		public Object getKey(Notebook notebook) {
			return notebook == null ? null : notebook.getKey();
		}
	};

	public static final ProvidesKey<Tag> TAG_KEY_PROVIDER = new ProvidesKey<Tag>() {
		@Override
		public Object getKey(Tag tag) {
			return tag == null ? null : tag.getKey();
		}
	};

	static class NotebookCell extends AbstractCell<Notebook> {
		private Presenter presenter;
		public void setPresenter(Presenter presenter) {
			this.presenter = presenter;
		}

		private class NotebookContextMenu extends PopupPanel {
			private Notebook selectedNotebook;

			public void setSelectedNotebook(Notebook selectedNotebook) {
				this.selectedNotebook = selectedNotebook;
			}

			NotebookContextMenu(Notebook notebook) {
				super();
				this.selectedNotebook = notebook;
				this.setAutoHideEnabled(true);
				this.setAnimationEnabled(true);
			}

			public void renameNotebook(String name) {
				selectedNotebook.setName(name);
				NotebookServiceAsync service = GWT.create(NotebookService.class);
				service.modify(selectedNotebook, new AsyncCallback<Void>(){

					@Override
					public void onFailure(Throwable caught) {
						GWT.log("Rename notebook failed!");
					}

					@Override
					public void onSuccess(Void result) {
						presenter.loadNotebookList();
					}
				});
			}

			public void deleteNotebook() {
				//TODO need to take into account the CASSCADE DELETE (deleting all the notes in the notebook)
				Notebook notebook = DataManager.getCurrentNotebook();
				NotebookServiceAsync service = GWT.create(NotebookService.class);
				service.delete(notebook, new AsyncCallback<Void>(){

					@Override
					public void onFailure(Throwable caught) {
						GWT.log("Delete notebook failed!");
					}

					@Override
					public void onSuccess(Void result) {
						presenter.loadNotebookList();
					}
				});
			}
		}

		List<String> OPERATION_LIST = new ArrayList<String>(Arrays.asList(
				"Rename", "Delete", "Properties"));
		private final String imageHtml;
		private NotebookContextMenu notebookContextMenu;

		public NotebookCell(ImageResource image, Presenter presenter) {
			super("click", "contextmenu");
			this.presenter = presenter;
			this.imageHtml = AbstractImagePrototype.create(image).getHTML();
		}

		@Override
		public void onBrowserEvent(Context context, Element parent,
				Notebook value, NativeEvent event,
				ValueUpdater<Notebook> valueUpdater) {
			event.preventDefault();
			event.stopPropagation();
			if( "click".equals(event.getType())){
				notebookContextMenu.setSelectedNotebook(value);
			}
			else if ("contextmenu".equals(event.getType())) {
				if (notebookContextMenu == null) {
					notebookContextMenu = new NotebookContextMenu(value);
					initialNotebookContextMenu();
				}
				notebookContextMenu.setPopupPosition(event.getClientX(),
						event.getClientY());
				notebookContextMenu.show();
				// Ignore clicks that occur outside of the outermost element.
				// EventTarget eventTarget = event.getEventTarget();
				// if (parent.getFirstChildElement().isOrHasChild(
				// Element.as(eventTarget))) {
				// doAction(value, valueUpdater);
				// }
			}
		}

		private void initialNotebookContextMenu() {
			VerticalPanel content = new VerticalPanel();
			notebookContextMenu.setAnimationEnabled(true);
			notebookContextMenu.setWidget(content);
			ClickableTextCell cell = new ClickableTextCell() {
				@Override
				public void onBrowserEvent(Context context, Element parent,
						String value, NativeEvent event,
						ValueUpdater<String> valueUpdater) {
					if ("click".equals(event.getType())) {
						notebookContextMenu.hide();
						if ("Rename".equals(value)) {
							showRenamePanel();
						} else if ("Delete".equals(value)) {
							showDeletePanel();
						} else if ("Properties".equals(value)) {
							// TODO
							// showpRropertiesPanel();
						}
					}
				}
			};
			CellList<String> operationList = new CellList<String>(cell);
			operationList.setRowData(OPERATION_LIST);
			content.add(operationList);
		}

		public void showRenamePanel() {
			final DialogBox dialog = new DialogBox();
			dialog.setText("Rename Notebook");
			VerticalPanel content = new VerticalPanel();
			dialog.setWidget(content);

			final TextBox name = new TextBox();
			content.add(name);
			HorizontalPanel buttonPanel = new HorizontalPanel();
			buttonPanel.add(new Button("Cancel", new ClickHandler() {
				public void onClick(ClickEvent event) {
					dialog.hide();
				}
			}));
			buttonPanel.add(new Button("OK", new ClickHandler() {
				public void onClick(ClickEvent event) {
					notebookContextMenu.renameNotebook(name.getText());
					dialog.hide();
				}
			}));
			content.add(buttonPanel);
			dialog.show();
			dialog.center();
		}

		public void showDeletePanel() {
			final DialogBox dialog = new DialogBox();
			dialog.setText("Delete Notebook");
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
					notebookContextMenu.deleteNotebook();
					dialog.hide();
				}
			}));
			content.add(buttonPanel);

			dialog.show();
			dialog.center();
		}

		@Override
		public void render(Context context, Notebook notebook,
				SafeHtmlBuilder sb) {
			if (notebook != null) {
				sb.appendHtmlConstant(imageHtml);
				sb.appendEscaped(notebook.getName());
			}
		}
	}

	static class TagCell extends AbstractCell<Tag> {
		private Presenter presenter;
		public void setPresenter(Presenter presenter) {
			this.presenter = presenter;
		}

		private class TagContextMenu extends PopupPanel {
			private Tag selectedTag;

			public void setSelectedTag(Tag selectedTag) {
				this.selectedTag = selectedTag;
			}

			TagContextMenu(Tag selectedTag) {
				super();
				this.selectedTag = selectedTag;
				this.setAutoHideEnabled(true);
				this.setAnimationEnabled(true);
			}

			public void renameTag(String name) {
				// TODO rename the tag
				System.out.println("rename tag " + selectedTag.getName()
						+ " to " + name);
			}

			public void deleteTag() {
				// TODO delete tag
				System.out.println("delete tag " + selectedTag.getName());
			}
		}

		List<String> OPERATION_LIST = new ArrayList<String>(Arrays.asList(
				"Rename", "Delete"));
		private final String imageHtml;
		private TagContextMenu tagContextMenu;

		public TagCell(ImageResource image, Presenter presenter) {
			super("click", "contextmenu");
			this.presenter = presenter;
			this.imageHtml = AbstractImagePrototype.create(image).getHTML();
		}

		@Override
		public void render(Context context, Tag tag, SafeHtmlBuilder sb) {
			if (tag != null) {
				sb.appendHtmlConstant(imageHtml);
				sb.appendEscaped(tag.getName());
			}
		}

		@Override
		public void onBrowserEvent(Context context, Element parent, Tag tag,
				NativeEvent event, ValueUpdater<Tag> valueUpdater) {
			event.preventDefault();
			event.stopPropagation();
			if ("click".equals(event.getType())){
				tagContextMenu.setSelectedTag(tag);
			}
			else if ("contextmenu".equals(event.getType())) {
				if (tagContextMenu == null) {
					tagContextMenu = new TagContextMenu(tag);
					initialTagContextMenu();
				}
				tagContextMenu.setPopupPosition(event.getClientX(),
						event.getClientY());
				tagContextMenu.show();
			}
		}

		private void initialTagContextMenu() {
			VerticalPanel content = new VerticalPanel();
			tagContextMenu.setAnimationEnabled(true);
			tagContextMenu.setWidget(content);
			ClickableTextCell cell = new ClickableTextCell() {
				@Override
				public void onBrowserEvent(Context context, Element parent,
						String value, NativeEvent event,
						ValueUpdater<String> valueUpdater) {
					if ("click".equals(event.getType())) {
						tagContextMenu.hide();
						if ("Rename".equals(value)) {
							showRenamePanel();
						} else if ("Delete".equals(value)) {
							showDeletePanel();
						}
					}
				}
			};
			CellList<String> operationList = new CellList<String>(cell);
			operationList.setRowData(OPERATION_LIST);
			content.add(operationList);
		}

		public void showRenamePanel() {
			final DialogBox dialog = new DialogBox();
			dialog.setText("Rename Tag");
			VerticalPanel content = new VerticalPanel();
			dialog.setWidget(content);

			final TextBox name = new TextBox();
			content.add(name);
			HorizontalPanel buttonPanel = new HorizontalPanel();
			buttonPanel.add(new Button("Cancel", new ClickHandler() {
				public void onClick(ClickEvent event) {
					dialog.hide();
				}
			}));
			buttonPanel.add(new Button("OK", new ClickHandler() {
				public void onClick(ClickEvent event) {
					tagContextMenu.renameTag(name.getText());
					dialog.hide();
				}
			}));
			content.add(buttonPanel);
			dialog.show();
			dialog.center();
		}

		public void showDeletePanel() {
			final DialogBox dialog = new DialogBox();
			dialog.setText("Delete Tag");
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
					tagContextMenu.deleteTag();
					dialog.hide();
				}
			}));
			content.add(buttonPanel);

			dialog.show();
			dialog.center();
		}
	}

	public NotebookListView() {
		initWidget(uiBinder.createAndBindUi(this));
		images = GWT.create(Images.class);

		initialNotebooksList();
		initialTagsList();
	}

	private void initialNotebooksList() {
		notebookCell = new NotebookCell(images.drafts(),presenter);

		notebooksCellList = new CellList<Notebook>(notebookCell,
				NOTEBOOK_KEY_PROVIDER);
		notebooksCellList.setPageSize(30);
		notebooksCellList
				.setKeyboardPagingPolicy(KeyboardPagingPolicy.INCREASE_RANGE);
		notebooksCellList
				.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.BOUND_TO_SELECTION);

		notebookSelectionModel = new SingleSelectionModel<Notebook>(
				NOTEBOOK_KEY_PROVIDER);
		notebooksCellList.setSelectionModel(notebookSelectionModel);

		notebookSelectionModel
				.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
					public void onSelectionChange(SelectionChangeEvent event) {
						Notebook notebook = notebookSelectionModel
								.getSelectedObject();
						presenter.onNotebookItemSelected(notebook);
					}
				});
		notebookDataProvider.addDataDisplay(notebooksCellList);
		pagerPanel.setDisplay(notebooksCellList);
		notebookPanel.setOpen(true);
	}

	private void initialTagsList() {
		tagCell = new TagCell(images.templates(),presenter);

		tagsCellList = new CellList<Tag>(tagCell, TAG_KEY_PROVIDER);
		tagsCellList.setPageSize(30);
		tagsCellList
				.setKeyboardPagingPolicy(KeyboardPagingPolicy.INCREASE_RANGE);
		tagsCellList
				.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.BOUND_TO_SELECTION);

		tagSelectionModel = new SingleSelectionModel<Tag>(TAG_KEY_PROVIDER);
		tagsCellList.setSelectionModel(tagSelectionModel);

		tagSelectionModel
				.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
					public void onSelectionChange(SelectionChangeEvent event) {
						Tag tag = tagSelectionModel.getSelectedObject();
						if (tag != null) {
							Window.alert("You selected: " + tag.getName());
						}
					}
				});
		tagDataProvider.getList().add(new Tag("GWT"));
		tagDataProvider.addDataDisplay(tagsCellList);
		tagPanel.setContent(tagsCellList);
		tagPanel.setOpen(true);
		// pagerPanel.setDisplay(notebooksCellList);
	}

	@Override
	public void onNotebookChanged(NotebookChangedEvent event) {
		presenter.loadNotebookList();
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
		notebookCell.setPresenter(presenter);
		tagCell.setPresenter(presenter);
	}

	@Override
	public void setNotebookList(List<Notebook> result) {
		List<Notebook> notebooks = notebookDataProvider.getList();
		notebooks.clear();
		notebooks.addAll(result);
	}

	@Override
	public Widget asWidget() {
		return this.content;
	}
}
