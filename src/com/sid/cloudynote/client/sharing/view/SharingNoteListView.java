package com.sid.cloudynote.client.sharing.view;

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
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.HasKeyboardPagingPolicy.KeyboardPagingPolicy;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import com.sid.cloudynote.client.event.ViewPublicNotesEvent;
import com.sid.cloudynote.client.event.ViewSharedNotesEvent;
import com.sid.cloudynote.client.event.interfaces.IViewPublicHandler;
import com.sid.cloudynote.client.event.interfaces.IViewSharedHandler;
import com.sid.cloudynote.client.sharing.view.interfaces.ISharingNoteListView;
import com.sid.cloudynote.client.view.Container;
import com.sid.cloudynote.shared.InfoNote;

public class SharingNoteListView extends ResizeComposite implements ISharingNoteListView, IViewPublicHandler, IViewSharedHandler{
	@UiField
	Container container;
	@UiField
	TabLayoutPanel tabPanel;
	@UiField
	HTMLPanel tabNotes;
	@UiField
	HTMLPanel tabTags;
	@UiField
	HTMLPanel tabGroups;
	static interface Images extends ClientBundle {
		ImageResource home();
	}
	private CellList<InfoNote> notesList;
	private NoteCell noteCell;
	private ListDataProvider<InfoNote> dataProvider = new ListDataProvider<InfoNote>();
	public static final ProvidesKey<InfoNote> KEY_PROVIDER = new ProvidesKey<InfoNote>() {
		@Override
		public Object getKey(InfoNote InfoNote) {
			return InfoNote == null ? null : InfoNote.getKey();
		}
	};
	private SingleSelectionModel<InfoNote> selectionModel;
	
	private Presenter presenter;
	private static SharingNoteListViewUiBinder uiBinder = GWT
			.create(SharingNoteListViewUiBinder.class);

	interface SharingNoteListViewUiBinder extends
			UiBinder<Widget, SharingNoteListView> {
	}

	public SharingNoteListView() {
		initWidget(uiBinder.createAndBindUi(this));
		Images images = GWT.create(Images.class);

		noteCell = new NoteCell(images.home());

		notesList = new CellList<InfoNote>(noteCell, KEY_PROVIDER);
		notesList.setPageSize(30);
		notesList.setKeyboardPagingPolicy(KeyboardPagingPolicy.INCREASE_RANGE);
		notesList.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.BOUND_TO_SELECTION);

		// Add a selection model so we can select cells.
		selectionModel = new SingleSelectionModel<InfoNote>(KEY_PROVIDER);
		notesList.setSelectionModel(selectionModel);

		selectionModel
				.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
					public void onSelectionChange(SelectionChangeEvent event) {
//						InfoNote note = selectionModel.getSelectedObject();
//						presenter.onNoteItemSelected(note);
//						presenter.viewNote(note);
					}
				});
		dataProvider.addDataDisplay(notesList);
		this.tabNotes.add(new ScrollPanel(notesList));
		
	}

	public Container getContainer() {
		return this.container;
	}

	@Override
	public Widget asWidget() {
		return this.tabPanel;
	}
	
	static class NoteCell extends AbstractCell<InfoNote> {
		private Presenter presenter;

		private final String imageHtml;

		public NoteCell(ImageResource image) {
			super("click");
			this.imageHtml = AbstractImagePrototype.create(image).getHTML();
		}

		@Override
		public void render(Context context, final InfoNote note,
				SafeHtmlBuilder sb) {
			// Value can be null, so do a null check..
			if (note == null) {
				return;
			}
//			Anchor title = new Anchor(note.getTitle());
//			title.addClickHandler(new ClickHandler(){
//				@Override
//				public void onClick(ClickEvent event) {
//					presenter.viewNote(note);
//				}
//			});

			sb.appendHtmlConstant("<table>");

			sb.appendHtmlConstant("<tr><td>");
			sb.appendHtmlConstant(imageHtml);
			sb.appendHtmlConstant("</td>");

			sb.appendHtmlConstant("<td style='font-size:95%;'>");
			sb.appendEscaped(note.getTitle());
			sb.appendHtmlConstant("</td></tr><tr><td>");
			sb.appendEscaped(note.getUser().getEmail());
			sb.appendHtmlConstant("</td><td colspan='2'>");
			sb.appendEscaped(note.getContent().replaceAll("\\<.*?>",""));
			sb.appendHtmlConstant("</td></tr><tr><td>");
			sb.appendEscaped(note.getCreatedTime().toString());
			sb.appendHtmlConstant("</td></tr></table>");
		}

		public void setPresenter(Presenter presenter) {
			this.presenter = presenter;
		}

		@Override
		public void onBrowserEvent(Context context, Element parent,
				InfoNote value, NativeEvent event,
				ValueUpdater<InfoNote> valueUpdater) {
			if ("click".equals(event.getType())) {
				presenter.viewNote(value);
			}
		}
	}
	
	public void setNoteList(List<InfoNote> result){
		List<InfoNote> notes = dataProvider.getList();
		selectionModel.clear();
		notes.clear();
		if (result!=null && result.size()!=0){
			notes.addAll(result);
//			selectionModel.setSelected(result.get(0), true);
		}
		notesList.redraw();
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
		noteCell.setPresenter(presenter);
	}

	@Override
	public void onViewPublicNotes(ViewPublicNotesEvent event) {
		presenter.loadPublicNoteList();
	}

	@Override
	public void onViewSharedNotes(ViewSharedNotesEvent event) {
		presenter.loadSharedNoteList(event.getUser().getId());
	}
}
