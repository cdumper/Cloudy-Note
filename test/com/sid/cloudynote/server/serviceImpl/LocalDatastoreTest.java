package com.sid.cloudynote.server.serviceImpl;

import javax.jdo.PersistenceManager;

import junit.framework.TestCase;

import org.junit.Ignore;

@Ignore
public class LocalDatastoreTest extends TestCase {
	PersistenceManager pm;
//	private final LocalServiceTestHelper helper = new LocalServiceTestHelper(
//			new LocalDatastoreServiceTestConfig());
//
//	@Before
//	public void setUp() {
//		helper.setUp();
//	}
//
//	@After
//	public void tearDown() {
//		helper.tearDown();
//	}

//	public void testInsertNotebook() {
//		pm = TestPMF.getInstance().getPersistenceManager();
//		User user = new User("user1", "1");
//		Query q = pm.newQuery(Notebook.class);
//		q.setFilter("user == userParam");
//		q.declareParameters(User.class.getName() + " userParam");
//
//		Object obj = q.execute(user);
//		List<Notebook> result = (List<Notebook>) obj;
//		assertEquals(result.size(), 0);
//	}
}
