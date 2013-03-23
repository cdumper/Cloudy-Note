package com.sid.cloudynote.server;

import java.util.HashMap;
import java.util.Map;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManagerFactory;

public class TestPMF {
	static PersistenceManagerFactory pmfTestInstance;
	
	static {
	    Map<String,String> props = new HashMap<String,String>();
	    props.put("javax.jdo.PersistenceManagerFactoryClass", "org.datanucleus.store.appengine.jdo.DatastoreJDOPersistenceManagerFactory");
	    props.put("javax.jdo.option.ConnectionURL", "appengine");
	    props.put("javax.jdo.option.NontransactionalRead", "true");
	    props.put("javax.jdo.option.NontransactionalWrite", "true");
	    props.put("javax.jdo.option.RetainValues", "true");
	    props.put("datanucleus.appengine.autoCreateDatastoreTxns", "true");
	    pmfTestInstance = JDOHelper.getPersistenceManagerFactory(props);
	}

	private TestPMF() {
	}

	public static PersistenceManagerFactory getInstance() {
		return pmfTestInstance;
	}
}
