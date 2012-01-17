package dk.sfs.riskengine.persistence.mapper;

import dk.sfs.riskengine.ais.RiskTarget.AisClass;
import dk.sfs.riskengine.persistence.domain.AisVesselStatic;

public interface AisVesselStaticMapper {

	AisVesselStatic selectStaticClassA(Long mmsi);
	AisVesselStatic selectStaticClassB(Long mmsi);
}