package com.sid.cloudynote.client.view;

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
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import com.sid.cloudynote.client.model.INote;
import com.sid.cloudynote.client.model.InfoNote;
import com.sid.cloudynote.client.service.NoteService;
import com.sid.cloudynote.client.service.NoteServiceAsync;

public class NoteListPanel extends ResizeComposite {
	private static NoteListPanelUiBinder uiBinder = GWT
			.create(NoteListPanelUiBinder.class);

	interface NoteListPanelUiBinder extends UiBinder<Widget, NoteListPanel> {
	}

	public static final ProvidesKey<INote> KEY_PROVIDER = new ProvidesKey<INote>() {
		@Override
		public Object getKey(INote INote) {
			return INote == null ? null : INote.getTitle();
		}
	};

	static interface Images extends ClientBundle {
		ImageResource home();
	}

	private ListDataProvider<INote> dataProvider = new ListDataProvider<INote>();

	static class NoteCell extends AbstractCell<INote> {
		private final String imageHtml;

		public NoteCell(ImageResource image) {
			this.imageHtml = AbstractImagePrototype.create(image).getHTML();
		}

		@Override
		public void render(Context context, INote INote, SafeHtmlBuilder sb) {
			// Value can be null, so do a null check..
			if (INote == null) {
				return;
			}

			sb.appendHtmlConstant("<table>");

			// Add the contact image.
			sb.appendHtmlConstant("<tr><td rowspan='3'>");
			sb.appendHtmlConstant(imageHtml);
			sb.appendHtmlConstant("</td>");

			// Add the name and address.
			sb.appendHtmlConstant("<td style='font-size:95%;'>");
			sb.appendEscaped(INote.getTitle());
			sb.appendHtmlConstant("</td></tr><tr><td>");
			sb.appendEscaped(INote.getContent());
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

	private CellList<INote> cellList;

	public NoteListPanel() {
		initWidget(uiBinder.createAndBindUi(this));
		Images images = GWT.create(Images.class);

		NoteCell noteCell = new NoteCell(images.home());

		cellList = new CellList<INote>(noteCell, KEY_PROVIDER);
		cellList.setPageSize(30);
		cellList.setKeyboardPagingPolicy(KeyboardPagingPolicy.INCREASE_RANGE);
		cellList.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.BOUND_TO_SELECTION);
		// List<INote> notes = new ArrayList<INote>();
		// for (int i = 0; i < 50; i++) {
		// notes.add(new INote());
		// }
		//
		// cellList.setRowCount(notes.size(), true);
		// cellList.setRowData(notes);

		// Add a selection model so we can select cells.
		final SingleSelectionModel<INote> selectionModel = new SingleSelectionModel<INote>(
				KEY_PROVIDER);
		cellList.setSelectionModel(selectionModel);

		selectionModel
				.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
					public void onSelectionChange(SelectionChangeEvent event) {
						// contactForm.setContact(selectionModel.getSelectedObject());
						Window.alert(selectionModel.getSelectedObject()
								.getTitle());
					}
				});

		loadNotes();
		dataProvider.addDataDisplay(cellList);
		pagerPanel.setDisplay(cellList);
		// rangeLabelPager.setDisplay(cellList);
	}

	private void loadNotes() {
		// List<INote> notes = dataProvider.getList();
		NoteServiceAsync service = (NoteServiceAsync) GWT
				.create(NoteService.class);
		AsyncCallback<List<INote>> callback = new AsyncCallback<List<INote>>() {

			@Override
			public void onFailure(Throwable caught) {
				System.out.println("falied! getNotesList");
				caught.printStackTrace();
			}

			@Override
			public void onSuccess(List<INote> result) {
				List<INote> notes = dataProvider.getList();
				if (result.size() != 0) {
					for (INote INote : result) {
						System.out.println(INote.getTitle());
						notes.addAll(result);
					}
				} else {
					for (int i = 0; i < 50; i++) {
						notes.add(new InfoNote("INote" + i, "content " + i, null,
								null));
					}
					System.out.println("No notes exist!");
				}
			}

		};
		service.getPaginationData(callback);
//		List<INote> notes = dataProvider.getList();
//		NoteServiceImpl service = new NoteServiceImpl();
//		List<INote> results = service.getPaginationData().getResultList();
//		System.out.println(results.size());
//		notes.addAll(results);
		// for (int i = 0; i < 50; i++) {
		// notes.add(new INote("INote"+i,"content "+i,null,null));
		// }
	}
}
