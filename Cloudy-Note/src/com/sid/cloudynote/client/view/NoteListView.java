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
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import com.sid.cloudynote.client.DataManager;
import com.sid.cloudynote.client.event.INoteChangedHandler;
import com.sid.cloudynote.client.event.NoteChangedEvent;
import com.sid.cloudynote.client.service.InfoNoteService;
import com.sid.cloudynote.client.service.InfoNoteServiceAsync;
import com.sid.cloudynote.client.view.interfaces.INoteListView;
import com.sid.cloudynote.shared.InfoNote;

public class NoteListView extends ResizeComposite implements
		INoteChangedHandler, INoteListView {
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

	/**
	 * The pager used to display the current range.
	 */
	// @UiField
	// RangeLabelPager rangeLabelPager;

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

	static class NoteCell extends AbstractCell<InfoNote> {
		private SingleSelectionModel<InfoNote> selectionModel;

		public SingleSelectionModel<InfoNote> getSelectionModel() {
			return selectionModel;
		}

		public void setSelectionModel(SingleSelectionModel<InfoNote> selectionModel) {
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
		public void render(Context context, InfoNote note,
				SafeHtmlBuilder sb) {
			// Value can be null, so do a null check..
			if (note == null) {
				return;
			}

			sb.appendHtmlConstant("<table>");

			// Add the contact image.
			sb.appendHtmlConstant("<tr><td rowspan='3'>");
			sb.appendHtmlConstant(imageHtml);
			sb.appendHtmlConstant("</td>");

			// Add the name and address.
			sb.appendHtmlConstant("<td style='font-size:95%;'>");
			sb.appendEscaped(note.getTitle());
			sb.appendHtmlConstant("</td></tr><tr><td>");
			sb.appendEscaped(note.getContent().replaceAll("\\<.*?>",""));
			sb.appendHtmlConstant("</td></tr></table>");
		}

		@Override
		public void onBrowserEvent(Context context, Element parent,
				InfoNote value, NativeEvent event,
				ValueUpdater<InfoNote> valueUpdater) {
			event.preventDefault();
			event.stopPropagation();
			
			if ("contextmenu".equals(event.getType())){
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

			public void deleteNote() {
				InfoNoteServiceAsync service = GWT.create(InfoNoteService.class);
				service.delete(selectedNote, new AsyncCallback<Void>(){
					@Override
					public void onFailure(Throwable caught) {
						GWT.log("Delete notebook failed!");
					}

					@Override
					public void onSuccess(Void result) {
						presenter.loadNoteList(DataManager.getCurrentNotebook());
					}
				});
			}
		}
		
		List<String> OPERATION_LIST = new ArrayList<String>(Arrays.asList(
				"Edit", "Share", "Delete"));
		private NoteContextMenu noteContextMenu;
		
		private void initialNoteContextMenu(){
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
							selectionModel.setSelected(noteContextMenu.getSelectedNote(), true);
							presenter.startEditing(noteContextMenu.getSelectedNote());
						} else if ("Delete".equals(value)) {
							showDeletePanel();
						} else if ("Share".equals(value)) {
							showSharePanel(noteContextMenu.getSelectedNote());
						} 
					}
				}
			};
			CellList<String> operationList = new CellList<String>(cell);
			operationList.setRowData(OPERATION_LIST);
			content.add(operationList);
		}
		
		public void showSharePanel(final InfoNote note) {
			final DialogBox dialog = new DialogBox();
			dialog.setText(note.getTitle());
			VerticalPanel content = new VerticalPanel();
			dialog.setWidget(content);

			Label name = new Label(" Share with individuals</br>Invite others to view or edit this notebook. ");
			final TextBox users = new TextBox();
			final ListBox access = new ListBox();
			access.addItem("Read-Only");
			access.addItem("Write");

			HorizontalPanel buttonPanel = new HorizontalPanel();
			buttonPanel.add(new Button("Cancel", new ClickHandler() {
				public void onClick(ClickEvent event) {
					dialog.hide();
				}
			}));
			buttonPanel.add(new Button("OK", new ClickHandler() {
				public void onClick(ClickEvent event) {
					presenter.shareNoteToUser(users.getText(),note,access.getItemText(access.getSelectedIndex()));
					dialog.hide();
				}
			}));
			content.add(name);
			content.add(users);
			content.add(access);
			content.add(buttonPanel);

			dialog.show();
			dialog.center();
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
					noteContextMenu.deleteNote();
					dialog.hide();
				}
			}));
			content.add(buttonPanel);

			dialog.show();
			dialog.center();
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
//						DataManager.setCurrentNote(note.getKey());
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
	}

	@Override
	public void setNoteList(List<InfoNote> result) {
		List<InfoNote> notes = dataProvider.getList();
		notes.clear();
		notes.addAll(result);
		if(DataManager.getCurrentNote()!=null)
			selectionModel.setSelected(DataManager.getCurrentNote(), true);
	}
	
	public void setLabel(String text){
		this.label.setText(text);
	}

	@Override
	public Widget asWidget() {
		return this.pagerPanel;
	}
}