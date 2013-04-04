package com.sid.cloudynote.client.presenter;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import org.junit.Before;
import org.junit.Test;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;

public class FirstPresenterTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testFirstEasyMockTest() {
		// Create mock object based on HasClickHandlers interface
		final HasClickHandlers clickHandlerMock = createMock(HasClickHandlers.class);

		// Set expectation - one call to addClickHandler with an argument
		// matching the type "ClickHandler" and returning null.
		expect(clickHandlerMock.addClickHandler(isA(ClickHandler.class)))
				.andReturn(null);

		// Put mock into replay mode
		replay(clickHandlerMock);

		// Run test code
		clickHandlerMock.addClickHandler(new ClickHandler() {
		    @Override
		        public void onClick(final ClickEvent event) { }
		    });

		// Verify all expected were met.
		verify(clickHandlerMock);
	}
}
