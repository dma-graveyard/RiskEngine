package dk.sfs.riskengine.persistence.mapper;

import dk.sfs.riskengine.persistence.domain.Vessel;

public interface VesselMapper {

	
	Vessel selectByMmsi(Long mmsi);

	
}