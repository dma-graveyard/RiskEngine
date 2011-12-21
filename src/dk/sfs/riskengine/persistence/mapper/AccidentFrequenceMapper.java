package dk.sfs.riskengine.persistence.mapper;

import java.util.Map;

public interface AccidentFrequenceMapper {

	
	Double selectByShipTypeAndAccidentType(Map<String, Object> map);
	Double selectByAvgByAccidentType(String accidentTypeName);

}