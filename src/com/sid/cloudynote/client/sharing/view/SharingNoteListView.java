package com.sid.cloudynote.client.sharing.view;

import java.util.List;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.HasKeyboardPagingPolicy.KeyboardPagingPolicy;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import com.sid.cloudynote.client.event.ViewGroupNotesEvent;
import com.sid.cloudynote.client.event.ViewPublicNotesEvent;
import com.sid.cloudynote.client.event.ViewSharedNotesEvent;
import com.sid.cloudynote.client.event.interfaces.IViewGroupNotesHandler;
import com.sid.cloudynote.client.event.interfaces.IViewPublicHandler;
import com.sid.cloudynote.client.event.interfaces.IViewSharedHandler;
import com.sid.cloudynote.client.sharing.view.interfaces.ISharingNoteListView;
import com.sid.cloudynote.client.view.Container;
import com.sid.cloudynote.shared.InfoNote;
import com.sid.cloudynote.shared.Tag;

public class SharingNoteListView extends ResizeComposite implements ISharingNoteListView, IViewPublicHandler, IViewSharedHandler, IViewGroupNotesHandler{
	@UiField
	Container container;
	@UiField
	HTMLPanel content;
	
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

		noteCell = new NoteCell();

		notesList = new CellList<InfoNote>(noteCell, KEY_PROVIDER);
		notesList.setPageSize(30);
		notesList.setKeyboardPagingPolicy(KeyboardPagingPolicy.INCREASE_RANGE);
		notesList.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.BOUND_TO_SELECTION);

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
		this.content.add(new ScrollPanel(notesList));
	}

	public Container getContainer() {
		return this.container;
	}

	@Override
	public Widget asWidget() {
		return this.content;
	}
	
	static class NoteCell extends AbstractCell<InfoNote> {
		private Presenter presenter;

		public NoteCell() {
			super("click");
		}

		@Override
		public void render(Context context, final InfoNote note,
				SafeHtmlBuilder sb) {
			if (note == null) {
				return;
			}

			sb.appendHtmlConstant("<table width=\"100%\" style='border-bottom:1px solid #cccccc;'>");

			sb.appendHtmlConstant("<tr><th colspan=\"2\" height=\"15px\">");
			sb.appendHtmlConstant(note.getTitle());
			sb.appendHtmlConstant("</div></th></tr>");

			if (note.getTags()==null || note.getTags().size()==0) {
				sb.appendHtmlConstant("<tr><td colspan=\"2\" height=\"50px\">");
				sb.appendEscaped(note.getContent().replaceAll("\\<.*?>",""));
				sb.appendHtmlConstant("</td></tr>");
			} else {
				sb.appendHtmlConstant("<tr><td colspan=\"2\" height=\"40px\">");
				sb.appendEscaped(note.getContent().replaceAll("\\<.*?>",""));
				sb.appendHtmlConstant("</td></tr>");
				sb.appendHtmlConstant("<tr><td colspan=\"2\">");
				sb.appendHtmlConstant("Tags:");
				//TODO present tags in the sharingNoteListView
//				for (Tag tag : note.getTags()) {
//					sb.appendEscaped(" "+tag.getName());
//				}
				sb.appendHtmlConstant("</td></tr>");
			}
			
			sb.appendHtmlConstant("<tr><td align=\"left\">Author:");
			sb.appendEscaped(note.getUser().getNickname().split("@")[0]);
			sb.appendHtmlConstant("</td><td align=\"right\">");
			sb.appendEscaped(note.getCreatedTime().getDay()+"/"+note.getCreatedTime().getMonth()+"/"+note.getCreatedTime().getYear());
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
		presenter.loadSharedNoteList(event.getUser().getEmail());
	}

	@Override
	public void onViewGroups(ViewGroupNotesEvent event) {
		presenter.loadNotesInGroup(event.getGroup());
	}
}
