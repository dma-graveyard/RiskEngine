package dk.sfs.riskengine.ais;

import java.util.HashMap;
import java.util.Map;

import dk.frv.ais.geo.GeoLocation;
import dk.frv.ais.handler.IAisHandler;
import dk.frv.ais.message.AisMessage;
import dk.frv.ais.message.AisMessage18;
import dk.frv.ais.message.AisPosition;
import dk.sfs.riskengine.IncidentType;
import dk.sfs.riskengine.MachineryFailure;

public class RiskMessageHandler implements IAisHandler {

	private Map<Long, RiskTarget> map = new HashMap<Long, RiskTarget>();

	private double latMin;
	private double latMax;
	private double  lonMin;
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
		
		AisMessage18 msg = null;
		switch (aisMessage.getMsgId()) {
		case 18:
			msg = (AisMessage18) aisMessage;
			break;
		default:
			return;
		}
		// check the ais target is within the monitored area. 

		GeoLocation pos =  msg.getPos().getGeoLocation();
		
		if( pos.getLatitude()< latMin  || latMax <pos.getLatitude()|| pos.getLongitude()< lonMin || lonMax< pos.getLongitude()){
			// ship out of zone - remove from map
			map.remove(msg.getUserId());
			return;
		}
		
		
		RiskTarget target =  map.get(msg.getUserId());
		if(target==null){
			target = new RiskTarget(msg);
			map.put(msg.getUserId(), target);
		}
		target.setPos(msg.getPos());
		target.setCog(msg.getCog());
		
		
		if(!target.timeToUpdate()){
			// calculate every minute
			return;
		}
		
		//Update the risk for this target 
		
		new MachineryFailure(, target)
		
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
	
	
}
