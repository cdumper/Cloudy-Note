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
import com.google.gwt.safehtml.shared.SafeHtml;
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
import com.google.gwt.user.client.ui.HTMLPanel;
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
import com.sid.cloudynote.client.AppController;
import com.sid.cloudynote.client.DataManager;
import com.sid.cloudynote.client.event.NotebookChangedEvent;
import com.sid.cloudynote.client.event.TagChangedEvent;
import com.sid.cloudynote.client.event.UserInfoChangedEvent;
import com.sid.cloudynote.client.event.interfaces.INotebookChangedHandler;
import com.sid.cloudynote.client.event.interfaces.ITagChangedHandler;
import com.sid.cloudynote.client.event.interfaces.IUserInfoChangedHandler;
import com.sid.cloudynote.client.service.NotebookService;
import com.sid.cloudynote.client.service.NotebookServiceAsync;
import com.sid.cloudynote.client.view.interfaces.INotebookListView;
import com.sid.cloudynote.shared.Notebook;
import com.sid.cloudynote.shared.Tag;

public class NotebookListView extends ResizeComposite implements
		INotebookChangedHandler, ITagChangedHandler, INotebookListView, IUserInfoChangedHandler {

	@UiTemplate("NoteBookListView.ui.xml")
	interface NotebookListPanelUiBinder extends
			UiBinder<Widget, NotebookListView> {
	}

	private static NotebookListPanelUiBinder uiBinder = GWT
			.create(NotebookListPanelUiBinder.class);

	//TODO style celllist
	interface MyCellListResources extends CellList.Resources {
		@Source({"../resources/css/CellList.css"})
		@Override
		public CellListStyle cellListStyle();
		
		interface CellListStyle extends CellList.Style{
		}
	}

	public interface Images extends ClientBundle, Tree.Resources {
		@Source("../resources/images/allnotes.png")
		ImageResource allNotes();
		
		@Source("../resources/images/notebook.png")
		ImageResource notebook();
		
		@Source("../resources/images/notebook2.png")
		ImageResource sharedNotebook();

		@Source("../resources/images/trash.png")
		ImageResource trash();
		
		@Source("../resources/images/tag.png")
		ImageResource tag();
		
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
							showRenamePanel(notebookContextMenu.selectedNotebook);
						} else if ("Delete".equals(value)) {
							showDeletePanel(notebookContextMenu.selectedNotebook);
						} else if ("Properties".equals(value)) {
							// TODO show notebook properties
							// showpRropertiesPanel();
						}
					}
				}

				@Override
				protected void render(Context context, SafeHtml value,
						SafeHtmlBuilder sb) {
					sb.appendHtmlConstant("<div style=\"margin: 5px;\">");
					sb.append(value);
					sb.appendHtmlConstant("</div>");
				}
			};
			CellList<String> operationList = new CellList<String>(cell);
			operationList.setRowData(OPERATION_LIST);
			content.add(operationList);
		}

		public void showRenamePanel(Notebook notebook) {
			final DialogBox dialog = new DialogBox();
			dialog.setAnimationEnabled(true);
			dialog.setGlassEnabled(true);
			dialog.setText("Rename Notebook");
			HTMLPanel content = new HTMLPanel("");
			content.setWidth("250px");
			dialog.setWidget(content);

			Label label = new Label("Rename \""+notebook.getName()+"\" to:");
			final TextBox name = new TextBox();
			name.setWidth("200px");
			content.add(label);
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
			dialog.center();
		}

		public void showDeletePanel(Notebook notebook) {
			final DialogBox dialog = new DialogBox();
			dialog.setAnimationEnabled(true);
			dialog.setGlassEnabled(true);
			dialog.setText("Delete Notebook");
			HTMLPanel content = new HTMLPanel("");
			dialog.setWidget(content);

			final Label name = new Label("Sure to delete \""+notebook.getName()+"\"?");
			name.setWidth("250px");
			content.add(name);

			HorizontalPanel buttonPanel = new HorizontalPanel();
			buttonPanel.add(new Button("Cancel", new ClickHandler() {
				public void onClick(ClickEvent event) {
					dialog.hide();
				}
			}));
			buttonPanel.add(new Button("OK", new ClickHandler() {
				public void onClick(ClickEvent event) {
					presenter.deleteNotebook(notebookContextMenu.selectedNotebook);
					dialog.hide();
				}
			}));
			content.add(buttonPanel);
			dialog.center();
		}

		@Override
		public void render(Context context, Notebook notebook,
				SafeHtmlBuilder sb) {
			if (notebook == null) {
				return;
			}

			sb.appendHtmlConstant("<div style=\"padding:5px 5px 0px;\">");
			sb.appendHtmlConstant(imageHtml);
			sb.appendHtmlConstant("<div style=\"padding-left:10px;position: absolute;vertical-align:middle;display:inline;\">");
			sb.appendEscaped(notebook.getName()+" ("+notebook.getTotalNotes()+")");
			sb.appendHtmlConstant("</div>");
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
				sb.appendHtmlConstant("<div style=\"padding:5px 5px 0px;\">");
				sb.appendHtmlConstant(imageHtml);
				sb.appendHtmlConstant("<div style=\"margin-left:10px;vertical-align:middle;display:inline;\">");
				sb.appendEscaped(tag.getName());
				sb.appendHtmlConstant("</div>");
				sb.appendHtmlConstant("</div>");
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
							showRenamePanel(tagContextMenu.selectedTag);
						} else if ("Delete".equals(value)) {
							showDeletePanel(tagContextMenu.selectedTag);
						}
					}
				}
			};
			CellList<String> operationList = new CellList<String>(cell);
			operationList.setRowData(OPERATION_LIST);
			content.add(operationList);
		}

		public void showRenamePanel(Tag tag) {
			final DialogBox dialog = new DialogBox();
			dialog.setAnimationEnabled(true);
			dialog.setGlassEnabled(true);
			dialog.setText("Rename Tag");
			HTMLPanel content = new HTMLPanel("");
			content.setWidth("250px");
			dialog.setWidget(content);

			Label label = new Label("Rename \""+tag.getName()+"\" to:");
			final TextBox name = new TextBox();
			name.setWidth("200px");
			content.add(label);
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

		public void showDeletePanel(Tag tag) {
			final DialogBox dialog = new DialogBox();
			dialog.setAnimationEnabled(true);
			dialog.setGlassEnabled(true);
			dialog.setText("Delete Tag");
			HTMLPanel content = new HTMLPanel("");
			dialog.setWidget(content);

			final Label name = new Label("Sure to delete \""+tag.getName()+"\"?");
			name.setWidth("250px");
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
		}

		private HTML html;
		final DisclosurePanelImages images = (DisclosurePanelImages) GWT
				.create(DisclosurePanelImages.class);

		public DisclosurePanelHeader(boolean isOpen, String html) {
			this.addDomHandler(new ContextMenuHandler() {
				@Override
				public void onContextMenu(ContextMenuEvent event) {
					System.out.println("context menu event");
				}
			}, ContextMenuEvent.getType());

			this.image = (isOpen ? images.disclosurePanelOpen().createImage()
					: images.disclosurePanelClosed().createImage());
			this.html = new HTML(html);
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
							presenter.onNewNotebookButtonClicked();
							popup.hide();
						}
					}));
				} else if ("Tags".equals(this.html.getText())) {
					panel.add(new Button("Add Tag", new ClickHandler() {
						@Override
						public void onClick(ClickEvent event) {
							presenter.onNewTagButtonClicked();
							popup.hide();
						}
					}));
				}
				popup.setWidget(panel);
				popup.show();
			}
		}

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
		this.allNotes.setTotalNotes(AppController.get().getLoginInfo().getTotalNotes());
		notebookCell = new NotebookCell(images.notebook(), presenter);
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
						if (notebookSelectionModel.getSelectedObject() == null) {
							return;
						}
						
						Notebook notebook = notebookSelectionModel
								.getSelectedObject();
						
						presenter.onNotebookItemSelected(notebook);
						
						if (tagSelectionModel.getSelectedObject() != null) {
							tagSelectionModel.setSelected(tagSelectionModel.getSelectedObject(), false);
						}
					}
				});
		notebookDataProvider.addDataDisplay(notebooksCellList);
		pagerPanel.setDisplay(notebooksCellList);
		notebookPanel.setOpen(true);
	}

	private void initialTagsList() {
		tagCell = new TagCell(images.tag(), presenter);
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
						if (tagSelectionModel.getSelectedObject() == null) {
							return;
						}
						
						Tag tag = tagSelectionModel.getSelectedObject();
						if (tag != null) {
							presenter.loadNotesByTag(tag);
						}
						
						if (notebookSelectionModel.getSelectedObject() != null) {
							notebookSelectionModel.setSelected(notebookSelectionModel.getSelectedObject(), false);
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
		if (DataManager.getCurrentNotebook() != null) {
			this.notebookSelectionModel.setSelected(DataManager.getCurrentNotebook(), true);
		} else {
			this.notebookSelectionModel.setSelected(allNotes, true);
		}
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

	@Override
	public void onUserInfoChanged(UserInfoChangedEvent event) {
		this.allNotes.setTotalNotes(event.getUser().getTotalNotes());
		presenter.loadNotebookList();
	}
}
