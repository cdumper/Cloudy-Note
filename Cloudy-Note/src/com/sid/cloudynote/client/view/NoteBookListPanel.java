package com.sid.cloudynote.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.TreeListener;
import com.google.gwt.user.client.ui.Widget;

public class NoteBookListPanel extends Composite {

	private static NotebookListPanelUiBinder uiBinder = GWT
			.create(NotebookListPanelUiBinder.class);

	interface NotebookListPanelUiBinder extends
			UiBinder<Widget, NoteBookListPanel> {
	}

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

	public NoteBookListPanel() {
		initWidget(uiBinder.createAndBindUi(this));
		Images images = GWT.create(Images.class);

		TreeItem allNotes = new TreeItem(imageItemHTML(images.home(),
				"All Notes"));
		allNotes.addItem("1");
		addImageItem(allNotes, "Inbox", images.inbox());
		addImageItem(allNotes, "Drafts", images.drafts());
		addImageItem(allNotes, "Templates", images.templates());
		addImageItem(allNotes, "Sent", images.sent());
		addImageItem(allNotes, "Trash", images.trash());
		TreeItem tags = new TreeItem("Tags");
		allNotes.setState(true);
		tags.setState(false);

		noteBooksTree.addItem(allNotes);
		noteBooksTree.addItem(tags);
		noteBooksTree.addTreeListener(new TreeListener() {

			@Override
			public void onTreeItemSelected(TreeItem item) {
				if (item.getChildCount() == 0) {
					Window.alert("item selected" + item.getTitle());
				}
			}

			@Override
			public void onTreeItemStateChanged(TreeItem item) {
				// Window.alert("state changed" + item.getTitle());
			}

		});
	}

	@UiField
	Tree noteBooksTree;

	// @UiHandler("noteBooksTree")
	// void onClick(SelectionEvent<TreeItem> e) {
	// Window.alert(e.getSelectedItem().getClass().getName());
	// }

	private TreeItem addImageItem(TreeItem root, String title,
			ImageResource imageProto) {
		TreeItem item = new TreeItem(imageItemHTML(imageProto, title));
		root.addItem(item);
		return item;
	}

	private String imageItemHTML(ImageResource imageProto, String title) {
		return AbstractImagePrototype.create(imageProto).getHTML() + " "
				+ title;
	}
}
