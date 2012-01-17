package dk.sfs.riskengine.ais;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import dk.frv.ais.geo.GeoLocation;
import dk.frv.ais.handler.IAisHandler;
import dk.frv.ais.message.AisMessage;
import dk.frv.ais.message.AisMessage18;
import dk.frv.ais.message.AisMessage5;
import dk.frv.ais.message.AisPositionMessage;
import dk.sfs.riskengine.geometry.CPA;
import dk.sfs.riskengine.geometry.Geofunctions;
import dk.sfs.riskengine.geometry.Point2d;

/**
 * hanlde Ais messages. Not Thread Safe ! Only one at a time
 * 
 * @author rch
 * 
 */
public class RiskMessageHandler implements IAisHandler {

	private static final Logger log = Logger.getLogger(RiskMessageHandler.class);
	private static Map<Long, RiskTarget> map = new HashMap<Long, RiskTarget>();
	private double latMin;
	private double latMax;
	private double lonMin;
	private double lonMax;

	public RiskMessageHandler(double latMin, double latMax, double lonMin, double lonMax) {
		super();
		this.latMin = latMin;
		this.latMax = latMax;
		this.lonMin = lonMin;
		this.lonMax = lonMax;
	}

	@Override
	public void receive(AisMessage aisMessage) {

		
		if (aisMessage instanceof AisMessage5) {

			/*
			 * Check for some static info to refresh
			 */
			RiskTarget target = map.get(aisMessage.getUserId());
			if (target != null) {
				AisMessage5 staticMsg = (AisMessage5) aisMessage;
				target.setStaticInfo(staticMsg);
			}
			
		} else if (aisMessage instanceof AisPositionMessage) {

			AisPositionMessage msg = (AisPositionMessage) aisMessage;
			process(msg.getPos().getGeoLocation(), msg.getCog(), msg.getSog(), aisMessage);
		}
		else if (aisMessage instanceof AisMessage18) {

			AisMessage18 msg = (AisMessage18) aisMessage;
			process(msg.getPos().getGeoLocation(), msg.getCog(), msg.getSog(), aisMessage);
		}

	}
	private void process(GeoLocation pos, Integer cog ,Integer sog, AisMessage aisMessage){

		if (pos.getLatitude() < latMin || latMax < pos.getLatitude() || pos.getLongitude() < lonMin
				|| lonMax < pos.getLongitude()) {

			// ship is out of zone - remove from zone map
			map.remove(aisMessage.getUserId());
			return;
		}
		if(aisMessage instanceof AisPositionMessage){
			int i =0;
			i++;
		}
		// Ship in the zone
		try {
			RiskTarget target = map.get(aisMessage.getUserId());

			if (target == null) {
				target = new RiskTarget(aisMessage);
				map.put(aisMessage.getUserId(), target);
			}

			target.setPos(pos);
			if (cog != 3600) {
				target.setCog(cog / 10d);
			}
			if (sog != 1023) {
				target.setSog(sog / 10d);
			}

			if (target.timeToUpdate()) {
				target.updateCollisionTarget(map.values());
				if(aisMessage instanceof AisPositionMessage){
					target.updateRiskIndexes();
				}
			}

		} catch (Exception e) {
			log.error("Trouble !", e);
		}
	}

	public void setLatMin(long latMin) {
		this.latMin = latMin;
	}

	public void setLatMax(long latMax) {
		this.latMax = latMax;
	}

	public void setLonMin(long lonMin) {
		this.lonMin = lonMin;
	}

	public void setLonMax(long lonMax) {
		this.lonMax = lonMax;
	}

	public static RiskTarget getTarget(Long mmsi) {
		return map.get(mmsi);

	}
}
