package com.sid.cloudynote.client.sharing.view;

import java.util.Arrays;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import com.sid.cloudynote.client.sharing.view.interfaces.IGroupView;
import com.sid.cloudynote.client.view.Container;

public class GroupView extends Composite implements IGroupView{
	@UiField
	Container container;
	public Container getContainer() {
		return container;
	}

	private Presenter presenter;
	private String PUBLIC = "Public";
	private String SHARED_WITH_ME = "Shared with me";
	private String GROUP = "Group";
	private CellList<String> cellList;
	private SingleSelectionModel<String> selectionModel;

	private static GroupViewUiBinder uiBinder = GWT
			.create(GroupViewUiBinder.class);

	interface GroupViewUiBinder extends UiBinder<Widget, GroupView> {
	}

	public GroupView() {
		initWidget(uiBinder.createAndBindUi(this));
		TextCell cell = new TextCell();
		cellList = new CellList<String>(cell);
		cellList.setRowData(Arrays.asList(PUBLIC,SHARED_WITH_ME,GROUP));
		this.container.add(cellList);
		selectionModel = new SingleSelectionModel<String>();
		selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler(){
			@Override
			public void onSelectionChange(SelectionChangeEvent event) {
				// TODO Auto-generated method stub
				if (PUBLIC.equals(selectionModel.getSelectedObject())){
					presenter.viewPublic();
				} else if (SHARED_WITH_ME.equals(selectionModel.getSelectedObject())){
					presenter.viewShared();
				} else if (GROUP.equals(selectionModel.getSelectedObject())){
					presenter.viewGroups();
				}
			}
		});
		cellList.setSelectionModel(selectionModel);
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}
	
	@Override
	public Widget asWidget(){
		return this.cellList;
	}
}
