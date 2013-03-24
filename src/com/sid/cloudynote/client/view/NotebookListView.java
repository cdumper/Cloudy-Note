package com.sid.cloudynote.client.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.appengine.api.datastore.Key;
import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ClickableTextCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.HasKeyboardPagingPolicy.KeyboardPagingPolicy;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DisclosureEvent;
import com.google.gwt.user.client.ui.DisclosureHandler;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.DisclosurePanelImages;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import com.sid.cloudynote.client.DataManager;
import com.sid.cloudynote.client.event.NotebookChangedEvent;
import com.sid.cloudynote.client.event.TagChangedEvent;
import com.sid.cloudynote.client.event.interfaces.INotebookChangedHandler;
import com.sid.cloudynote.client.event.interfaces.ITagChangedHandler;
import com.sid.cloudynote.client.service.NotebookService;
import com.sid.cloudynote.client.service.NotebookServiceAsync;
import com.sid.cloudynote.client.view.interfaces.INotebookListView;
import com.sid.cloudynote.shared.Notebook;
import com.sid.cloudynote.shared.Tag;

public class NotebookListView extends ResizeComposite implements
		INotebookChangedHandler, ITagChangedHandler, INotebookListView {

	@UiTemplate("NoteBookListView.ui.xml")
	interface NotebookListPanelUiBinder extends
			UiBinder<Widget, NotebookListView> {
	}

	private static NotebookListPanelUiBinder uiBinder = GWT
			.create(NotebookListPanelUiBinder.class);

	//TODO style celllist
	interface MyCellListResources extends CellList.Resources {
		@Source({"CellList.css"})
		@Override
		public CellListStyle cellListStyle();
		
		interface CellListStyle extends CellList.Style{
		}
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

	@UiField
	Container container;

	public Container getContainer() {
		return container;
	}

	@UiField
	DockLayoutPanel content;
	@UiField
	HorizontalPanel buttonPanel;
	@UiField
	Button newNotebookButton;
	@UiField
	Button newNoteButton;
	@UiField
	ScrollPanel scrollPanel;
	@UiField
	DisclosurePanel notebookPanel;
	// @UiField
	// DisclosurePanelHeader notebookHeader;
	@UiField
	DisclosurePanel tagPanel;
	// @UiField
	// Header tagHeader;
	@UiField
	ShowMorePagerPanel pagerPanel;

	public static final String ALL_NOTES = "All Notes";
	private Images images;
	private Presenter presenter;
	private final Notebook allNotes = new Notebook(ALL_NOTES);
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
			return notebook == null ? null : notebook.getName();
		}
	};

	public static final ProvidesKey<Tag> TAG_KEY_PROVIDER = new ProvidesKey<Tag>() {
		@Override
		public Object getKey(Tag tag) {
			return tag == null ? null : tag.getKey();
		}
	};

	@UiHandler("newNoteButton")
	void onClickNewNote(ClickEvent e) {
		presenter.onNewNoteButtonClicked();
	}

	@UiHandler("newNotebookButton")
	void onClickNewNotebook(ClickEvent e) {
		presenter.onNewNotebookButtonClicked();
	}

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
				NotebookServiceAsync service = GWT
						.create(NotebookService.class);
				service.modify(selectedNotebook, new AsyncCallback<Void>() {

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
				// TODO need to take into account the CASSCADE DELETE (deleting
				// all the notes in the notebook)
				NotebookServiceAsync service = GWT
						.create(NotebookService.class);
				service.delete(selectedNotebook, new AsyncCallback<Void>() {
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
		private NotebookContextMenu notebookContextMenu;
		private final String imageHtml;

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

			if ("contextmenu".equals(event.getType())) {
				notebookContextMenu = new NotebookContextMenu(value);
				initialNotebookContextMenu();
				notebookContextMenu.setSelectedNotebook(value);
				notebookContextMenu.setPopupPosition(event.getClientX(),
						event.getClientY());
				notebookContextMenu.show();
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
							// TODO show notebook properties
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
			if (notebook == null) {
				return;
			}

			sb.appendHtmlConstant("<div style=\"background-color:#EAEDEF;color:#555E64;display:inline-block;\">");
			sb.appendHtmlConstant(imageHtml);
			sb.appendEscaped(notebook.getName());
			sb.appendHtmlConstant("</div>");
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
			if ("contextmenu".equals(event.getType())) {
				tagContextMenu = new TagContextMenu(tag);
				initialTagContextMenu();
				tagContextMenu.setSelectedTag(tag);
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
					presenter.renameTag(tagContextMenu.selectedTag,
							name.getText());
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
					System.out.println("delete tag : "
							+ tagContextMenu.selectedTag.getName());
					presenter.deleteTag(tagContextMenu.selectedTag);
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

		initialNotebookHeader();
		initialTagHeader();
		initialNotebooksList();
		initialTagsList();
	}

	@SuppressWarnings("deprecation")
	private class DisclosurePanelHeader extends HorizontalPanel {
		private Image image;

		public void setOpen(boolean isOpen) {
			this.image = (isOpen ? images.disclosurePanelOpen().createImage()
					: images.disclosurePanelClosed().createImage());
			this.clear();
			present();
		}

		private void present() {
			add(this.image);
			add(this.html);
			add(this.button);
		}

		private HTML html;
		Button button = new Button("$");
		final DisclosurePanelImages images = (DisclosurePanelImages) GWT
				.create(DisclosurePanelImages.class);

		public DisclosurePanelHeader(boolean isOpen, String html) {
			// it has to add this piece of code otherwise the onBrowserEvent
			// won't work
			this.addDomHandler(new ContextMenuHandler() {
				@Override
				public void onContextMenu(ContextMenuEvent event) {
					System.out.println("context menu event");
				}
			}, ContextMenuEvent.getType());
			// this.addDomHandler(new MouseUpHandler(){
			// @Override
			// public void onMouseUp(MouseUpEvent event) {
			// if (event.getNativeButton()==NativeEvent.BUTTON_RIGHT){
			// System.out.println("right button event");
			// }
			// }
			// }, MouseUpEvent.getType());

			this.image = (isOpen ? images.disclosurePanelOpen().createImage()
					: images.disclosurePanelClosed().createImage());
			this.html = new HTML(html);
			button.setSize("5px", "5px");
			present();
		}

		@Override
		public void onBrowserEvent(Event event) {
			event.preventDefault();
			event.stopPropagation();
			if ("contextmenu".endsWith(event.getType())) {
				final PopupPanel popup = new PopupPanel(true);
				VerticalPanel panel = new VerticalPanel();
				popup.setPopupPosition(event.getClientX(), event.getClientY());
				if ("Notebooks".equals(this.html.getText())) {
					panel.add(new Button("Add Notebook", new ClickHandler() {
						@Override
						public void onClick(ClickEvent event) {
							createNewDialog(true, false, "Create New Notebook")
									.center();
							popup.hide();
						}
					}));
				} else if ("Tags".equals(this.html.getText())) {
					panel.add(new Button("Add Tag", new ClickHandler() {
						@Override
						public void onClick(ClickEvent event) {
							createNewDialog(false, true, "Create New Tag")
									.center();
							popup.hide();
						}
					}));
				}
				popup.setWidget(panel);
				popup.show();
			}
		}

	}

	private DialogBox createNewDialog(final boolean isNotebook,
			final boolean isTag, String title) {
		final DialogBox dialog = new DialogBox();
		final TextBox name = new TextBox();
		Button confirm = new Button("Confirm");
		Button cancel = new Button("Cancel");
		dialog.setTitle(title);
		VerticalPanel panel = new VerticalPanel();
		dialog.add(panel);
		panel.add(name);
		panel.add(cancel);
		panel.add(confirm);
		panel.setSize("200", "200");

		confirm.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (isNotebook) {
					presenter.createNewNotebook(name.getText());
					dialog.hide();
				} else if (isTag) {
					presenter.createNewTag(name.getText());
					dialog.hide();
				}
			}
		});

		cancel.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				dialog.hide();
			}
		});
		return dialog;
	}

	@SuppressWarnings("deprecation")
	private void initialNotebookHeader() {
		final DisclosurePanelHeader header = new DisclosurePanelHeader(true,
				"Notebooks");
		notebookPanel.setHeader(header);
		notebookPanel.addEventHandler(new DisclosureHandler() {
			public void onClose(DisclosureEvent event) {
				header.setOpen(false);
			}

			public void onOpen(DisclosureEvent event) {
				header.setOpen(true);
			}
		});
	}

	@SuppressWarnings("deprecation")
	private void initialTagHeader() {
		final DisclosurePanelHeader header = new DisclosurePanelHeader(true,
				"Tags");
		tagPanel.setHeader(header);
		tagPanel.addEventHandler(new DisclosureHandler() {
			public void onClose(DisclosureEvent event) {
				header.setOpen(false);
			}

			public void onOpen(DisclosureEvent event) {
				header.setOpen(true);
			}
		});
	}

	private void initialNotebooksList() {
		notebookCell = new NotebookCell(images.drafts(), presenter);
		// TODO style notebooks cell lsit
		notebooksCellList = new CellList<Notebook>(notebookCell,GWT.<MyCellListResources> create(MyCellListResources.class),
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
		tagCell = new TagCell(images.templates(), presenter);
		//TODO style tags cell list
		CellList.Resources cellListRes = GWT.create(MyCellListResources.class);
		tagsCellList = new CellList<Tag>(tagCell,cellListRes, TAG_KEY_PROVIDER);
		
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
							presenter.loadNotesByTag(tag);
						}
					}
				});
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
		notebooks.add(allNotes);
		notebooks.addAll(result);
	}

	@Override
	public void setTagList(List<Tag> result) {
		Map<Key, Tag> tags = new HashMap<Key, Tag>();
		for (Tag tag : result) {
			tags.put(tag.getKey(), tag);
		}
		DataManager.setAllTags(tags);
		tagDataProvider.getList().clear();
		tagDataProvider.getList().addAll(result);
		this.tagsCellList.redraw();
	}

	@Override
	public Widget asWidget() {
		return this.content;
	}

	@Override
	public void onTagChanged(TagChangedEvent event) {
		presenter.loadTagList();
	}
}
