package com.pitchplayer.test;

import org.hibernate.SessionFactory;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.test.AbstractTransactionalDataSourceSpringContextTests;

public abstract class AbstractSpringTest extends
		AbstractTransactionalDataSourceSpringContextTests {

	SessionFactory sessionFactory;
	SimpleJdbcTemplate jt;

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Override
	protected String[] getConfigLocations() {
		return new String[] { "testContext.xml"};
		//return new String[] { "deploy/pitch.war/WEB-INF/applicationContext.xml" };
	}

	@Override
	protected void onSetUpBeforeTransaction() throws Exception {
		jt = new SimpleJdbcTemplate(jdbcTemplate);
	}

	@Override
	protected void onSetUpInTransaction() throws Exception {
		super.onSetUpInTransaction();
		// Load a batch of test data in the transaction
		// executeSqlScript("classpath:test-data.sql", false);
	}

	// Utility method
	protected void flush() {
		sessionFactory.getCurrentSession().flush();
	}
	
}
