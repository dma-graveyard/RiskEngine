package dk.sfs.riskengine.persistence.mapper;

import dk.sfs.riskengine.persistence.domain.AisVesselStatic;

public interface AisVesselStaticMapper {

	AisVesselStatic selectByPrimaryKey(Long mmsi);

}