package dk.sfs.riskengine.ais;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import dk.frv.ais.geo.GeoLocation;
import dk.frv.ais.message.AisMessage18;
import dk.frv.ais.message.AisMessage5;
import dk.frv.ais.message.AisPosition;
import dk.frv.ais.message.AisPositionMessage;
import dk.frv.ais.message.ShipTypeCargo;
import dk.sfs.riskengine.Collision;
import dk.sfs.riskengine.FireExplosion;
import dk.sfs.riskengine.Foundering;
import dk.sfs.riskengine.HullFailure;
import dk.sfs.riskengine.MachineryFailure;
import dk.sfs.riskengine.StrandedByMachineFailure;
import dk.sfs.riskengine.StrandedByNavigationError;
import dk.sfs.riskengine.geometry.CPA;
import dk.sfs.riskengine.geometry.Point2d;
import dk.sfs.riskengine.metoc.Metoc;
import dk.sfs.riskengine.persistence.domain.RiskIndexes;
import dk.sfs.riskengine.persistence.domain.Vessel;

public class RiskTarget {

	private static final long CAL_PERIOD = 5 * 60l * 1000l; // 5 min

	private Long mmsi;
	private Long imo;
	private Vessel staticInfo;
	private AisPosition pos;
	/*
	 * Compass
	 */
	private Double cog;
	/*
	 * Knots
	 */
	private Double sog;
	private long lastUpdated = 0;

	
	private Point2d positionVector;
	/*
	 * m/s - cartesian
	 */
	private Point2d speedVector;

	
	private RiskTarget cpaTarget;
	private double cpaTime;
	private double cpa; 

	public RiskTarget(AisPositionMessage msg) {
		super();
		mmsi = msg.getUserId();
	}

	public boolean hasStaticInfo() {
		return staticInfo != null;
	}

	public void setStaticInfo(AisMessage5 msg) {
		/*
		 * try to get from Loyds with imo number
		 */

		this.staticInfo = Vessel.getByImo(msg.getImo());
		imo = msg.getImo();

		if (staticInfo == null) {
			staticInfo = new Vessel();
			/*
			 * Take info from ais msg but no flag and no yearOfBuild
			 */
			staticInfo.setDraught(new Double(msg.getDraught()));
			// TODO find shipTypeIwarp from ais type
			staticInfo.setShipTypeIwrap(new ShipTypeCargo(msg.getShipType()).getShipType().name());
			staticInfo.setLength(Double.valueOf(msg.getDimBow() + msg.getDimStern()));
			staticInfo.setNameOfShip(msg.getName());
		}
	}

	public void setPos(AisPosition pos) {
		this.pos = pos;
		positionVector = CPA.getPositionVector(pos.getGeoLocation());
	}

	public String getShipTypeIwrap() {
		return staticInfo.getShipTypeIwrap();
	}

	public Double getLength() {
		return staticInfo.getLength();
	}

	public Double getDraught() {
		return staticInfo.getDraught();
	}

	public Integer getYearOfBuild() {
		return staticInfo.getYearOfBuild();
	}

	public String getFlag() {
		return staticInfo.getFlag();
	}

	public long getLongitude() {
		return pos.getLongitude();
	}

	public long getLatitude() {
		return pos.getLatitude();
	}

	public GeoLocation getGeoLocation() {
		return pos.getGeoLocation();
	}

	public Double getSog() {
		return sog;
	}

	public void setSog(Double sog) {
		this.sog = sog;
		speedVector = CPA.getSpeedVector(cog, sog);
	}

	public GeoLocation getPos() {
		return pos.getGeoLocation();
	}

	public Double getCog() {
		return cog;
	}

	public void setCog(Double cog) {
		this.cog = cog;
	}

	public boolean timeToUpdate() {
		return System.currentTimeMillis() - lastUpdated > CAL_PERIOD;
	}

	public void updateRiskIndexes() {

		Metoc metoc = Metoc.getMetocForPosition(pos.getGeoLocation());

		RiskIndexes risk = new RiskIndexes();
		risk.setMmsi(mmsi.intValue());

		// TODO do some to avoid creation of new object

		risk.setFireExplosion(new FireExplosion(metoc, this).getTotalRisk());
		risk.setMachineryFailure(new MachineryFailure(metoc, this).getTotalRisk());

		if (hasStaticInfo()) {
			// requires static info
			risk.setStrandedByMachineFailure(new StrandedByMachineFailure(metoc, this).getTotalRisk());
			risk.setStrandedByNavigationError(new StrandedByNavigationError(metoc, this).getTotalRisk());
		}

		risk.setMachineryFailure(new MachineryFailure(metoc, this).getTotalRisk());
		risk.setHullFailure(new HullFailure(metoc, this).getTotalRisk());
		risk.setFoundering(new Foundering(metoc, this).getTotalRisk());

		if (cpaTarget != null) {
			risk.setCollision(new Collision(metoc, this, cpa, cpaTime).getTotalRisk());

			risk.setCpaTargetMmsi(cpaTarget.getMmsi());
			risk.setCpaTime(cpaTime);
			risk.setCpaDist(cpa);
			
			risk.setCpaCog(cpaTarget.cog);
			risk.setCpaSog(cpaTarget.sog);
			risk.setCpaLat(cpaTarget.pos.getGeoLocation().getLatitude());
			risk.setCpaLon(cpaTarget.pos.getGeoLocation().getLongitude());
		}
		risk.setDateTimeCreated(new Date());
		if (staticInfo != null) {
			risk.setStaticInfo(staticInfo);
		}
		risk.setCog(cog);
		risk.setSog(sog);
		risk.setLat(pos.getGeoLocation().getLatitude());
		risk.setLon(pos.getGeoLocation().getLongitude());
		
		// save in db
		risk.save();

	}

	public void updateCollisionTarget(Collection<RiskTarget> targetCollection) {

		if (sog == null || cog == null || sog < 0.5) {
			return;
		}
		cpaTarget = null;
		cpaTime = Double.POSITIVE_INFINITY;
		for (RiskTarget collisionShip : targetCollection) {

			if (collisionShip.equals(this)) {
				continue;
			}
			if (collisionShip.sog != null && collisionShip.sog != 0.0 && collisionShip.cog != null) {
				double time = CPA.cpa_time(positionVector, speedVector, collisionShip.positionVector,
						collisionShip.speedVector);
				if (time > 0 && time < cpaTime) {
					cpaTarget = collisionShip;
					cpaTime = time;
				}
			}
		}
		if(cpaTarget!=null){
		cpa = CPA.cpa_distance(positionVector, speedVector, cpaTarget.getPositionVector(), cpaTarget.getSpeedVector(),
				cpaTime);
		}
		
	}
	
	public Long getMmsi() {
		return mmsi;
	}

	public Point2d getPositionVector() {
		return positionVector;
	}

	public Point2d getSpeedVector() {
		return speedVector;
	}
}
