package com.sid.cloudynote.client.sharing.view;

import java.util.List;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
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
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import com.sid.cloudynote.client.AppController;
import com.sid.cloudynote.client.DataManager;
import com.sid.cloudynote.client.view.Container;
import com.sid.cloudynote.client.view.interfaces.ISharingNoteListView;
import com.sid.cloudynote.shared.InfoNote;

public class SharingNoteListView extends ResizeComposite implements ISharingNoteListView{
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
		noteCell.setSelectionModel(selectionModel);

		selectionModel
				.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
					public void onSelectionChange(SelectionChangeEvent event) {
						InfoNote note = selectionModel.getSelectedObject();
						presenter.onNoteItemSelected(note);
					}
				});
		dataProvider.addDataDisplay(notesList);
		this.tabNotes.add(notesList);
		
	}

	public Container getContainer() {
		return this.container;
	}

	@Override
	public Widget asWidget() {
		return this.tabPanel;
	}
	
	static class NoteCell extends AbstractCell<InfoNote> {
		private SingleSelectionModel<InfoNote> selectionModel;

		public SingleSelectionModel<InfoNote> getSelectionModel() {
			return selectionModel;
		}

		public void setSelectionModel(SingleSelectionModel<InfoNote> selectionModel) {
			this.selectionModel = selectionModel;
		}

		private final String imageHtml;

		public NoteCell(ImageResource image) {
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

			sb.appendHtmlConstant("<tr><td>");
			sb.appendHtmlConstant(imageHtml);
			sb.appendHtmlConstant("</td>");

			sb.appendHtmlConstant("<td style='font-size:95%;'>");
			sb.appendEscaped(note.getTitle());
			sb.appendHtmlConstant("</td></tr><tr><td>");
			sb.appendEscaped(AppController.get().getLoginInfo().getEmailAddress());
			sb.appendHtmlConstant("</td><td colspan='2'>");
			sb.appendEscaped(note.getContent().replaceAll("\\<.*?>",""));
			sb.appendHtmlConstant("</td></tr><tr><td>");
			sb.appendEscaped(note.getProperty().getCreatedTime().toString());
			sb.appendHtmlConstant("</td></tr></table>");
		}
	}
	
	public void setNoteList(List<InfoNote> result){
		List<InfoNote> notes = dataProvider.getList();
		notes.clear();
		notes.addAll(result);
		if(DataManager.getCurrentNote()!=null)
			selectionModel.setSelected(DataManager.getCurrentNote(), true);
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}
}
