package dk.sfs.riskengine.test;

import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;

import dk.sfs.riskengine.index.IncidentType.ShipSize;
import dk.sfs.riskengine.persistence.mapper.AccidentFrequenceMapper;
import dk.sfs.riskengine.persistence.mapper.DBSessionFactory;

public class MapperTest {

	public static void main(String[] args) {

		SqlSession sess = DBSessionFactory.getSession();

		try {
			AccidentFrequenceMapper mapper = sess.getMapper(AccidentFrequenceMapper.class);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("accidentTypeName", "FIRE_EXPLOSION");
			map.put("shipTypename", "Fishing ship");
			ShipSize size = ShipSize.SMALL;

			map.put("shipSize", size.name());
			Double d =  mapper.selectByShipTypeAndAccidentType(map);
			System.out.println(d);
		} finally {
			sess.close();
		}

	}
}
