package com.sid.cloudynote.client.view;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.appengine.api.datastore.Key;
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
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import com.sid.cloudynote.client.model.DataManager;
import com.sid.cloudynote.client.model.InfoNote;
import com.sid.cloudynote.client.model.Note;
import com.sid.cloudynote.client.service.InfoNoteService;
import com.sid.cloudynote.client.service.InfoNoteServiceAsync;

public class NoteListPanel extends ResizeComposite {
	private static NoteListPanelUiBinder uiBinder = GWT
			.create(NoteListPanelUiBinder.class);

	interface NoteListPanelUiBinder extends UiBinder<Widget, NoteListPanel> {
	}
	
	private EditPanel editPanel;

	public void setEditPanel(EditPanel editPanel) {
		this.editPanel = editPanel;
	}

	public static final ProvidesKey<InfoNote> KEY_PROVIDER = new ProvidesKey<InfoNote>() {
		@Override
		public Object getKey(InfoNote Note) {
			return Note == null ? null : Note.getKey();
		}
	};

	static interface Images extends ClientBundle {
		ImageResource home();
	}

	private ListDataProvider<InfoNote> dataProvider = new ListDataProvider<InfoNote>();

	static class NoteCell extends AbstractCell<InfoNote> {
		private final String imageHtml;

		public NoteCell(ImageResource image) {
			this.imageHtml = AbstractImagePrototype.create(image).getHTML();
		}

		@Override
		public void render(Context context, InfoNote Note, SafeHtmlBuilder sb) {
			// Value can be null, so do a null check..
			if (Note == null) {
				return;
			}

			sb.appendHtmlConstant("<table>");

			// Add the contact image.
			sb.appendHtmlConstant("<tr><td rowspan='3'>");
			sb.appendHtmlConstant(imageHtml);
			sb.appendHtmlConstant("</td>");

			// Add the name and address.
			sb.appendHtmlConstant("<td style='font-size:95%;'>");
			sb.appendEscaped(Note.getTitle());
			sb.appendHtmlConstant("</td></tr><tr><td>");
			sb.appendEscaped(Note.getContent());
			sb.appendHtmlConstant("</td></tr></table>");
		}
	}

	/**
	 * The pager used to change the range of data.
	 */
	@UiField
	ShowMorePagerPanel pagerPanel;

	/**
	 * The pager used to display the current range.
	 */
	// @UiField
	// RangeLabelPager rangeLabelPager;

	private CellList<InfoNote> cellList;

	public NoteListPanel() {
		initWidget(uiBinder.createAndBindUi(this));
		Images images = GWT.create(Images.class);

		NoteCell noteCell = new NoteCell(images.home());

		cellList = new CellList<InfoNote>(noteCell, KEY_PROVIDER);
		cellList.setPageSize(30);
		cellList.setKeyboardPagingPolicy(KeyboardPagingPolicy.INCREASE_RANGE);
		cellList.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.BOUND_TO_SELECTION);

		// Add a selection model so we can select cells.
		final SingleSelectionModel<InfoNote> selectionModel = new SingleSelectionModel<InfoNote>(
				KEY_PROVIDER);
		cellList.setSelectionModel(selectionModel);

		selectionModel
				.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
					public void onSelectionChange(SelectionChangeEvent event) {
						// TODO to present selected note in the edit panel
						Note note = selectionModel.getSelectedObject();
						DataManager.setCurrentNote(note.getKey());
						editPanel.presentNote(note);
//						Window.alert(selectionModel.getSelectedObject()
//								.getTitle());
						
					}
				});

		loadNotes();
		dataProvider.addDataDisplay(cellList);
		pagerPanel.setDisplay(cellList);
		// rangeLabelPager.setDisplay(cellList);
	}

	// private void loadNotes(){
	// List<InfoNote> notes = dataProvider.getList();
	// notes.addAll(DataManager.getNotes());
	// }

	public void loadNotes() {
		InfoNoteServiceAsync service = (InfoNoteServiceAsync) GWT
				.create(InfoNoteService.class);
		AsyncCallback<List<InfoNote>> callback = new AsyncCallback<List<InfoNote>>() {

			@Override
			public void onFailure(Throwable caught) {
				GWT.log("falied! getNotesList");
				caught.printStackTrace();
			}

			@Override
			public void onSuccess(List<InfoNote> result) {
				List<InfoNote> notes = dataProvider.getList();
				if (result != null && result.size() != 0) {
					Map<Key, InfoNote> noteMap = new HashMap<Key, InfoNote>();
					for (InfoNote note : result) {
						noteMap.put(note.getKey(), note);
					}
					DataManager.setNotes(noteMap);
					DataManager.setCurrentNote(result.get(0).getKey());
					notes.clear();
					notes.addAll(result);
				} else {
					DataManager.setNotes(null);
					GWT.log("No notes exist!");
				}
			}

		};
		service.getNotes(DataManager.getCurrentNotebook(),callback);
	}
}
