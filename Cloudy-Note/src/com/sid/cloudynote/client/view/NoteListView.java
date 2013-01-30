package com.sid.cloudynote.client.view;

import java.util.List;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
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
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import com.sid.cloudynote.client.DataManager;
import com.sid.cloudynote.client.event.INoteChangedHandler;
import com.sid.cloudynote.client.event.NoteChangedEvent;
import com.sid.cloudynote.client.presenter.NotePresenter;
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

	public Container getContainer() {
		return container;
	}

	/**
	 * The pager used to display the current range.
	 */
	// @UiField
	// RangeLabelPager rangeLabelPager;

	private CellList<InfoNote> cellList;

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
		private final String imageHtml;

		public NoteCell(ImageResource image) {
			this.imageHtml = AbstractImagePrototype.create(image).getHTML();
		}

		@Override
		public void render(Context context, InfoNote InfoNote,
				SafeHtmlBuilder sb) {
			// Value can be null, so do a null check..
			if (InfoNote == null) {
				return;
			}

			sb.appendHtmlConstant("<table>");

			// Add the contact image.
			sb.appendHtmlConstant("<tr><td rowspan='3'>");
			sb.appendHtmlConstant(imageHtml);
			sb.appendHtmlConstant("</td>");

			// Add the name and address.
			sb.appendHtmlConstant("<td style='font-size:95%;'>");
			sb.appendEscaped(InfoNote.getTitle());
			sb.appendHtmlConstant("</td></tr><tr><td>");
			sb.appendEscaped(InfoNote.getContent());
			sb.appendHtmlConstant("</td></tr></table>");
		}
	}

	public NoteListView() {
		initWidget(uiBinder.createAndBindUi(this));
		Images images = GWT.create(Images.class);

		NoteCell noteCell = new NoteCell(images.home());

		cellList = new CellList<InfoNote>(noteCell, KEY_PROVIDER);
		cellList.setPageSize(30);
		cellList.setKeyboardPagingPolicy(KeyboardPagingPolicy.INCREASE_RANGE);
		cellList.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.BOUND_TO_SELECTION);

		// Add a selection model so we can select cells.
		selectionModel = new SingleSelectionModel<InfoNote>(KEY_PROVIDER);
		cellList.setSelectionModel(selectionModel);

		selectionModel
				.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
					public void onSelectionChange(SelectionChangeEvent event) {
						InfoNote note = selectionModel.getSelectedObject();
						DataManager.setCurrentNote(note.getKey());
						presenter.onNoteItemSelected(note);
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
	}

	@Override
	public void setNoteList(List<InfoNote> result) {
		List<InfoNote> notes = dataProvider.getList();
		notes.clear();
		notes.addAll(result);
		selectionModel.setSelected(DataManager.getCurrentNote(), true);
	}

	@Override
	public Widget asWidget() {
		return this.pagerPanel;
	}
}