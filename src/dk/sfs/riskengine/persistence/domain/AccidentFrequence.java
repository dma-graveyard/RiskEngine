package dk.sfs.riskengine.persistence.domain;

import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;

import dk.sfs.riskengine.IncidentType.ShipSize;
import dk.sfs.riskengine.persistence.mapper.AccidentFrequenceMapper;
import dk.sfs.riskengine.persistence.mapper.DBSessionFactory;

public class AccidentFrequence {
	
	
	
	private Double freqSmallShip;
	private Double freqMediumShip;
	private Double freqLargeShip;
	
	
	public static Double getByShipAndAccidentType(String accidentName, String shipTypeIwrap, ShipSize size){
		SqlSession sess = DBSessionFactory.getSession();

		try {
			AccidentFrequenceMapper mapper = sess.getMapper(AccidentFrequenceMapper.class);			
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("accidentTypeName", accidentName);
			map.put("shipTypename", shipTypeIwrap);
			map.put("shipSize", size.name());
			return mapper.selectByShipTypeAndAccidentType(map);
		} finally {
			sess.close();
		}
		
	}
	
	public static Double selectByAvgByAccidentType(String accidentName){
		SqlSession sess = DBSessionFactory.getSession();

		try {
			AccidentFrequenceMapper mapper = sess.getMapper(AccidentFrequenceMapper.class);			
			return mapper.selectByAvgByAccidentType(accidentName);
		} finally {
			sess.close();
		}
	}


	public Double getFreqSmallShip() {
		return freqSmallShip;
	}

	public void setFreqSmallShip(Double freqSmallShip) {
		this.freqSmallShip = freqSmallShip;
	}

	public Double getFreqMediumShip() {
		return freqMediumShip;
	}

	public void setFreqMediumShip(Double freqMediumShip) {
		this.freqMediumShip = freqMediumShip;
	}

	public Double getFreqLargeShip() {
		return freqLargeShip;
	}

	public void setFreqLargeShip(Double freqLargeShip) {
		this.freqLargeShip = freqLargeShip;
	}

}