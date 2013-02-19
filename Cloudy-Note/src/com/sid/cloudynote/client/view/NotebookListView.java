package com.sid.cloudynote.client.view;

import java.util.List;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
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
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Tree;
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
		private final String imageHtml;

		public NotebookCell(ImageResource image) {
			super("click","contextmenu");
			this.imageHtml = AbstractImagePrototype.create(image).getHTML();
		}

		@Override
		public void onBrowserEvent(Context context, Element parent,
				Notebook value, NativeEvent event,
				ValueUpdater<Notebook> valueUpdater) {
			event.preventDefault();
			event.stopPropagation();
			if ("contextmenu".equals(event.getType())) {
				//TODO pop up menu
				// Ignore clicks that occur outside of the outermost element.
				// EventTarget eventTarget = event.getEventTarget();
				// if (parent.getFirstChildElement().isOrHasChild(
				// Element.as(eventTarget))) {
				// doAction(value, valueUpdater);
				// }
			}
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
		private final String imageHtml;

		public TagCell(ImageResource image) {
			super("click","contextmenu");
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
		public void onBrowserEvent(Context context, Element parent,
				Tag tag, NativeEvent event,
				ValueUpdater<Tag> valueUpdater) {
			event.preventDefault();
			event.stopPropagation();
			if ("contextmenu".equals(event.getType())) {
				//TODO pop up menu
			}
		}
	}

	public NotebookListView() {
		initWidget(uiBinder.createAndBindUi(this));
		images = GWT.create(Images.class);

		initialNotebooksList();
		initialTagsList();
	}

	private void initialNotebooksList() {
		NotebookCell notebookCell = new NotebookCell(images.drafts());

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
		TagCell tagCell = new TagCell(images.templates());

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
