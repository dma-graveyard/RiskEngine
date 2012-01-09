package dk.sfs.riskengine.persistence.mapper;

import dk.sfs.riskengine.persistence.domain.Vessel;

/**
 * @author rch
 *
 */
public interface VesselMapper {

	/**
	 * @param mmsi
	 * @return
	 */
	Vessel selectByMmsi(Long mmsi); 
	Vessel selectByImo(Long imo);
	void updateMmsiforImo(Vessel vessel);
	void updateMmsiToNull(Long mmsi);
}