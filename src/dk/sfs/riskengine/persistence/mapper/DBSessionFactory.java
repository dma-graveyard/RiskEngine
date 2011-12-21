package dk.sfs.riskengine.persistence.mapper;

import java.io.IOException;
import java.io.Reader;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.log4j.Logger;

public class DBSessionFactory {

	private static final Logger log = Logger.getLogger(DBSessionFactory.class);
	private static SqlSessionFactory self;
	private static Object mutex = new Object();

	private static SqlSessionFactory getInstance() {
		if (self == null) {
			synchronized (mutex) {
				if (self == null) {
					String resource = "dk/sfs/riskengine/persistence/xml/Configuration.xml";
					Reader reader;
					try {
						reader = Resources.getResourceAsReader(resource);
						self = new SqlSessionFactoryBuilder().build(reader);
					} catch (IOException e) {
						log.fatal("Could not read db configuration file "
								+ "dk/sfs/riskengine/persistence/xml/Configuration.xml", e);
						throw new RuntimeException(e);
					}
				}
			}
		}
		return self;

	}

	public static SqlSession getSession() {
		return getInstance().openSession(ExecutorType.REUSE, true);
	}

}
