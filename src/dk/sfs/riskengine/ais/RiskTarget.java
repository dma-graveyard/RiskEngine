package dk.sfs.riskengine.ais;

import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;

import org.apache.log4j.Logger;

import dk.frv.ais.geo.GeoLocation;
import dk.frv.ais.message.AisMessage5;
import dk.frv.ais.message.AisPosition;
import dk.frv.ais.message.AisPositionMessage;
import dk.frv.ais.message.ShipTypeCargo;
import dk.frv.ais.reader.AisTcpReader;
import dk.sfs.riskengine.consequence.Ship;
import dk.sfs.riskengine.geometry.CPA;
import dk.sfs.riskengine.geometry.Point2d;
import dk.sfs.riskengine.index.Collision;
import dk.sfs.riskengine.index.FireExplosion;
import dk.sfs.riskengine.index.Foundering;
import dk.sfs.riskengine.index.HullFailure;
import dk.sfs.riskengine.index.MachineryFailure;
import dk.sfs.riskengine.index.StrandedByMachineFailure;
import dk.sfs.riskengine.index.StrandedByNavigationError;
import dk.sfs.riskengine.metoc.Metoc;
import dk.sfs.riskengine.persistence.domain.AisVesselStatic;
import dk.sfs.riskengine.persistence.domain.Vessel;
import dk.sfs.riskengine.persistence.domain.Vessel.ShipTypeIwrap;

public class RiskTarget {

	public static final long CAL_PERIOD = 10 * 60l * 1000l; // 10 min

	private static final Logger log = Logger.getLogger(RiskTarget.class);

	private Long mmsi;
	private Long imo;
	private Vessel staticInfo;
	private Double actualDraught;
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
	private Double cpaTime;
	private Double cpa;

	public RiskTarget(AisPositionMessage msg) {
		super();
		mmsi = msg.getUserId();

		/*
		 * Get info from ais static
		 */
		AisVesselStatic aisStat = AisVesselStatic.findByMmsi(mmsi);
		updateStaticInfo(aisStat);

	}

	public boolean hasStaticInfo() {
		return staticInfo != null;
	}

	/**
	 * @param msg
	 */
	public void setStaticInfo(AisMessage5 msg) {

		AisVesselStatic aisStat = new AisVesselStatic();
		aisStat.setDraught(msg.getDraught());
		aisStat.setDimBow(msg.getDimBow());
		aisStat.setDimStern(msg.getDimStern());
		aisStat.setImo(msg.getImo());
		aisStat.setShipType(msg.getShipType());
		aisStat.setName(msg.getName());

	}

	private void updateStaticInfo(AisVesselStatic aisStat) {

		if (aisStat != null && (staticInfo == null || staticInfo.getImo() == null)) {
			/*
			 * Get info from loyds with imo
			 */
			this.staticInfo = Vessel.getByImo(aisStat.getImo());
			if (staticInfo != null && staticInfo.getMmsi() != mmsi) {
				/*
				 * mmsi from loyds dont match mmsi i AIS. Update staticInfo.
				 */
				staticInfo.updateMmsiforImo();
			}

		}

		if (staticInfo == null) {
			/*
			 * Get info from loyds with mmsi
			 */
			this.staticInfo = Vessel.getByMmsi(mmsi);

		}

		if (aisStat != null) {

			if (staticInfo == null) {
				/*
				 * No info from loyds, get info from ais
				 */
				double length = (aisStat.getDimBow() + aisStat.getDimStern()) / 10.0;
				if (length < 1.0) {

					staticInfo = new Vessel();
					staticInfo.setLength(Double.valueOf(length));
					staticInfo.setBreadth(Double.valueOf(aisStat.getDimPort() + aisStat.getDimStarboard()) / 10.0);
					staticInfo.setShipTypeIwrap(ShipTypeIwrap.getShipTypeFromAisType(
							new ShipTypeCargo(aisStat.getShipType()).getShipType(), length));
					staticInfo.setNameOfShip(aisStat.getName());
				} else {
					/*
					 * no loyds, no length ...
					 */
					return;
				}
			}
			/*
			 * Update actual draught
			 */
			actualDraught = aisStat.getDraught() / 10.0;
		}

	}

	public void setPos(AisPosition pos) {
		this.pos = pos;
		positionVector = CPA.getPositionVector(pos.getGeoLocation());
	}

	public ShipTypeIwrap getShipTypeIwrap() {
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

	public double getLongitude() {
		return pos.getGeoLocation().getLongitude();
	}

	public double getLatitude() {
		return pos.getGeoLocation().getLatitude();
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

		if (!hasStaticInfo()) {
			/*
			 * Dont caclculate with a minimum of static data on the ship
			 */
			return;
		}
		Metoc metoc = Metoc.getMetocForPosition(pos.getGeoLocation());

		/*
		 * update risk indexes and conseqence
		 */
		new FireExplosion(metoc, this).save();
		new MachineryFailure(metoc, this).save();

		// requires static info
		new StrandedByMachineFailure(metoc, this).save();
		new StrandedByNavigationError(metoc, this).save();

		new MachineryFailure(metoc, this).save();
		new HullFailure(metoc, this).save();
		new Foundering(metoc, this).save();

		if (cpaTarget != null && cpaTarget.hasStaticInfo()) {
			Collision col = new Collision(metoc, this, cpa, cpaTime, cpaTarget);
			col.save();
		}
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

		if (cpaTime < 900) {
			/*
			 * cpatime < 900s calculate cpa.
			 */
			cpa = CPA.cpa_distance(positionVector, speedVector, cpaTarget.getPositionVector(),
					cpaTarget.getSpeedVector(), cpaTime);
		}

		if (cpa == null || cpa > 500) {
			/*
			 * cpa > 500 m, not a problem, reset cpaTarget.
			 */
			cpaTarget = null;
			cpaTime = Double.POSITIVE_INFINITY;
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

	
	
	
	public Ship getConsequenceShip() {
		Ship ship1 = new Ship();
		ship1.shiptype = staticInfo.getShipTypeIwrap();
		Calendar cal = new GregorianCalendar();
		if (staticInfo.getYearOfBuild() != null) {
			ship1.age = cal.get(Calendar.YEAR) - staticInfo.getYearOfBuild();
		}
		ship1.loa = staticInfo.getLength(); // m
		ship1.breadth = staticInfo.getBreadth(); // m
		ship1.designDraught = staticInfo.getDraught();
		ship1.draught = actualDraught;

		ship1.deadweight = staticInfo.getDeadweight();
		ship1.grossTonnage = staticInfo.getGt();
		ship1.numberOfPersons = staticInfo.getPassengers();
		ship1.designSpeed = staticInfo.getSpeed();
		ship1.speed = sog; // knots
		ship1.valueOfShip = staticInfo.getNewbuildingPrice();

		return ship1;
	}

	public Double getActualDraught() {
		return actualDraught;
	}

	public void setActualDraught(double actualDraught) {
		this.actualDraught = actualDraught;
	}

	public String getNameOfShip() {
		return staticInfo.getNameOfShip();
	}

}
