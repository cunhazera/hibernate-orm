/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.orm.test.jpa.schemagen;

import java.net.URL;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.hibernate.cfg.AvailableSettings;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.jpa.boot.spi.Bootstrap;
import org.hibernate.jpa.boot.spi.EntityManagerFactoryBuilder;

import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit5.EntityManagerFactoryBasedFunctionalTest;
import org.hibernate.testing.orm.junit.RequiresDialect;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Vlad Mihalcea
 */
@RequiresDialect( H2Dialect.class )
public class JpaSchemaGeneratorTest extends EntityManagerFactoryBasedFunctionalTest {

	private final String LOAD_SQL = getScriptFolderPath() + "load-script-source.sql";
	private final String CREATE_SQL = getScriptFolderPath() + "create-script-source.sql";
	private final String DROP_SQL = getScriptFolderPath() + "drop-script-source.sql";

	private static int schemagenNumber = 0;

	public String getScriptFolderPath() {
		return "org/hibernate/orm/test/jpa/schemagen/";
	}

	@Override
	public Class[] getAnnotatedClasses() {
		return new Class[] { Item.class };
	}

	@Test
	@TestForIssue(jiraKey = "HHH-8271")
	public void testSqlLoadScriptSourceClasspath() {
		Map<Object, Object> settings = buildSettings();
		settings.put( AvailableSettings.HBM2DDL_DATABASE_ACTION, "create-drop" );
		settings.put( AvailableSettings.HBM2DDL_LOAD_SCRIPT_SOURCE, getLoadSqlScript() );
		doTest( settings );
	}


	@Test
	@TestForIssue(jiraKey = "HHH-8271")
	public void testSqlLoadScriptSourceUrl() {
		Map<Object, Object> settings = buildSettings();
		settings.put( AvailableSettings.HBM2DDL_DATABASE_ACTION, "create-drop" );
		settings.put( AvailableSettings.HBM2DDL_LOAD_SCRIPT_SOURCE, getResourceUrlString( getLoadSqlScript() ) );
		doTest( settings );
	}

	@Test
	@TestForIssue(jiraKey = "HHH-8271")
	public void testSqlCreateScriptSourceClasspath() {
		Map<Object, Object> settings = buildSettings();
		settings.put( AvailableSettings.HBM2DDL_DATABASE_ACTION, "create-drop" );
		settings.put( AvailableSettings.HBM2DDL_CREATE_SOURCE, "metadata-then-script" );
		settings.put( AvailableSettings.HBM2DDL_CREATE_SCRIPT_SOURCE, getCreateSqlScript() );
		doTest( settings );
	}

	@Test
	@TestForIssue(jiraKey = "HHH-8271")
	public void testSqlCreateScriptSourceUrl() {
		Map<Object, Object> settings = buildSettings();
		settings.put( AvailableSettings.HBM2DDL_DATABASE_ACTION, "create-drop" );
		settings.put( AvailableSettings.HBM2DDL_CREATE_SOURCE, "metadata-then-script" );
		settings.put( AvailableSettings.HBM2DDL_CREATE_SCRIPT_SOURCE, getResourceUrlString( getCreateSqlScript() ) );
		doTest( settings );
	}


	@Test
	@TestForIssue(jiraKey = "HHH-8271")
	public void testSqlDropScriptSourceClasspath() {
		Map<Object, Object> settings = buildSettings();
		settings.put( AvailableSettings.HBM2DDL_DROP_SOURCE, "metadata-then-script" );
		settings.put( AvailableSettings.HBM2DDL_DATABASE_ACTION, "drop" );
		settings.put( AvailableSettings.HBM2DDL_DROP_SCRIPT_SOURCE, getDropSqlScript() );
		doTest( settings );
	}

	@Test
	@TestForIssue(jiraKey = "HHH-8271")
	public void testSqlDropScriptSourceUrl() {
		Map<Object, Object> settings = buildSettings();
		settings.put( AvailableSettings.HBM2DDL_DROP_SOURCE, "metadata-then-script" );
		settings.put( AvailableSettings.HBM2DDL_DATABASE_ACTION, "drop" );
		settings.put( AvailableSettings.HBM2DDL_DROP_SCRIPT_SOURCE, getResourceUrlString( getDropSqlScript() ) );
		doTest( settings );
	}

	protected String getLoadSqlScript() {
		return LOAD_SQL;
	}

	protected String getCreateSqlScript() {
		return CREATE_SQL;
	}

	protected String getDropSqlScript() {
		return DROP_SQL;
	}

	protected String encodedName() {
		return "sch" + (char) 233 + "magen-test";
	}

	protected String getResourceUrlString(String resource) {
		final URL url = getClass().getClassLoader().getResource( resource );
		if ( url == null ) {
			throw new RuntimeException( "Unable to locate requested resource [" + resource + "]" );
		}
		return url.toString();
	}

	private void doTest(Map<Object, Object> settings) {
		// We want a fresh db after emf close
		// Unfortunately we have to use this dirty hack because the db seems not to be closed otherwise
		settings.put( "hibernate.connection.url", "jdbc:h2:mem:db-schemagen" + schemagenNumber++
				+ ";MVCC=TRUE;LOCK_TIMEOUT=10000" );
		EntityManagerFactoryBuilder emfb = Bootstrap.getEntityManagerFactoryBuilder(
				buildPersistenceUnitDescriptor(),
				settings
		);
		EntityManagerFactory emf = emfb.build();
		try {
			EntityManager em = emf.createEntityManager();
			try {
				Assertions.assertNotNull( em.find( Item.class, encodedName() ) );
			}
			finally {
				em.close();
			}
		}
		finally {
			emf.close();
			emfb.cancel();
		}
	}
}
