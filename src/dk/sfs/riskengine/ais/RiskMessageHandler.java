package dk.sfs.riskengine.ais;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import dk.frv.ais.geo.GeoLocation;
import dk.frv.ais.handler.IAisHandler;
import dk.frv.ais.message.AisMessage;
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
			if (target != null && !target.hasStaticInfo()) {
				AisMessage5 staticMsg = (AisMessage5) aisMessage;
				target.setStaticInfo(staticMsg);
			}
		} else if (aisMessage instanceof AisPositionMessage) {

			AisPositionMessage msg = (AisPositionMessage) aisMessage;

			// check the ais target is within the monitored area.
			GeoLocation pos = msg.getPos().getGeoLocation();

			if (pos.getLatitude() < latMin || latMax < pos.getLatitude() || pos.getLongitude() < lonMin
					|| lonMax < pos.getLongitude()) {

				// ship is out of zone - remove from zone map
				map.remove(msg.getUserId());
				return;
			}

			// Ship in the zone
			try {
				RiskTarget target = map.get(msg.getUserId());

				if (target == null) {
					target = new RiskTarget(msg);
					map.put(aisMessage.getUserId(), target);
				}

				target.setPos(msg.getPos());
				if (msg.getCog() != 3600) {
					target.setCog(msg.getCog() / 10d);
				}
				if (msg.getSog() != 1023) {
					target.setSog(msg.getSog() / 10d);
				}

				if (target.timeToUpdate()) {
					target.updateCollisionTarget(map.values());
					target.updateRiskIndexes();
				}

			} catch (Exception e) {
				log.error("Trouble !", e);
			}
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
