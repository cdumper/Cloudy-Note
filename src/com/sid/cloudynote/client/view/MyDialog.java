package com.sid.cloudynote.client.view;

import com.google.gwt.user.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;

public class MyDialog extends DialogBox
  {

    HTML close = new HTML("[X]");
    HTML title =new HTML("");
    HorizontalPanel captionPanel = new HorizontalPanel();

    public MyDialog(boolean autoHide, boolean modal)
      {
        super(autoHide, modal);
        Element td = getCellElement(0, 1);
        DOM.removeChild(td, (Element) td.getFirstChildElement());
        DOM.appendChild(td, captionPanel.getElement());
        captionPanel.setStyleName("Caption");//width-100%
        captionPanel.add(title);
        close.addStyleName("CloseButton");//float:right
        captionPanel.add(close);
        super.setGlassEnabled(true);
        super.setAnimationEnabled(true);
      }
    public MyDialog(boolean autoHide)
      {
        this(autoHide, true);
      }
    public MyDialog()
      {
        this(false);
      }

     @Override
    public String getHTML()
      {
        return this.title.getHTML();
      }

    @Override
    public String getText()
      {
        return this.title.getText();
      }

    @Override
    public void setHTML(String html)
      {
        this.title.setHTML(html);
      }

    @Override
    public void setText(String text)
      {
        this.title.setText(text);
      }

    @Override
    protected void onPreviewNativeEvent(NativePreviewEvent event)
      {
        NativeEvent nativeEvent = event.getNativeEvent();

        if (!event.isCanceled()
          && (event.getTypeInt() == Event.ONCLICK)
          && isCloseEvent(nativeEvent))
          {
            this.hide();
          }
        super.onPreviewNativeEvent(event);
      }

    private boolean isCloseEvent(NativeEvent event)
      {
        return event.getEventTarget().equals(close.getElement());//compares equality of the underlying DOM elements
      }
  }


