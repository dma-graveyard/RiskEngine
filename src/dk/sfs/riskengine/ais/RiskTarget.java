package dk.sfs.riskengine.ais;

import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;

import org.apache.log4j.Logger;

import dk.frv.ais.geo.GeoLocation;
import dk.frv.ais.message.AisMessage;
import dk.frv.ais.message.AisMessage5;
import dk.frv.ais.message.AisPosition;
import dk.frv.ais.message.AisPositionMessage;
import dk.frv.ais.message.ShipTypeCargo;
import dk.sfs.riskengine.consequence.Ship;
import dk.sfs.riskengine.geometry.CPA;
import dk.sfs.riskengine.geometry.Point2d;
import dk.sfs.riskengine.index.Collision;
import dk.sfs.riskengine.index.MachineryFailure;
import dk.sfs.riskengine.index.StrandedByMachineFailure;
import dk.sfs.riskengine.index.StrandedByNavigationError;
import dk.sfs.riskengine.metoc.Metoc;
import dk.sfs.riskengine.persistence.domain.AisVesselStatic;
import dk.sfs.riskengine.persistence.domain.Vessel;
import dk.sfs.riskengine.persistence.domain.Vessel.ShipTypeIwrap;

public class RiskTarget {

	private static final long CAL_PERIOD = 1 * 60l * 1000l; // 1 min

	private static final Logger log = Logger.getLogger(RiskTarget.class);

	private Long mmsi;
	private Long imo;
	private Vessel staticInfo;
	private Double actualDraught;
	private GeoLocation pos;

	public enum AisClass {
		A, B;

	}

	private AisClass aisClass;
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

	public RiskTarget(AisMessage msg) {
		super();
		if (msg instanceof AisPositionMessage) {
			aisClass = AisClass.A;
		} else {
			aisClass = AisClass.B;
		}
		mmsi = msg.getUserId();

		/*
		 * Get info from ais static
		 */
		AisVesselStatic aisStat = AisVesselStatic.findByMmsi(mmsi, aisClass);
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
				double length = (aisStat.getDimBow() + aisStat.getDimStern());

				staticInfo = new Vessel();
				staticInfo.setLength(Double.valueOf(length));
				staticInfo.setBreadth(Double.valueOf(aisStat.getDimPort() + aisStat.getDimStarboard()));
				staticInfo.setShipTypeIwrap(ShipTypeIwrap.getShipTypeFromAisType(
						new ShipTypeCargo(aisStat.getShipType()).getShipType(), length));
				staticInfo.setNameOfShip(aisStat.getName());
				if (aisStat.getDraught() != null) {
					staticInfo.setDraught(aisStat.getDraught() / 10.0);
				}
			}
			/*
			 * Update actual draught
			 */
			if (aisStat.getDraught() != null) {
				actualDraught = aisStat.getDraught() / 10.0;
			}
		}

	}

	public void setPos(GeoLocation pos) {
		this.pos = pos;
		positionVector = CPA.getPositionVector(pos);
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
		return pos.getLongitude();
	}

	public double getLatitude() {
		return pos.getLatitude();
	}

	public GeoLocation getGeoLocation() {
		return pos;
	}

	public Double getSog() {
		return sog;
	}

	public void setSog(Double sog) {
		this.sog = sog;
		speedVector = CPA.getSpeedVector(cog, sog);
	}

	public GeoLocation getPos() {
		return pos;
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
		Metoc metoc = Metoc.getMetocForPosition(pos);
		 System.out.println(mmsi);
		 if(mmsi == 219007589l){
		 int i= 0;
		 i++;
		 }
		/*
		 * update risk indexes and conseqence
		 */
		double maxConsequence = 0.0;
		double maxProbability = 0.0;
		double totalProbability = 0.0;
		double totalConsequence = 0.0;
		int n = 0; // Counter. Not really used.

		// FireExplosion fireExplosion = new FireExplosion(metoc, this);
		// fireExplosion.save();
		// maxConsequence = fireExplosion.getMaxConsequence();
		// if (fireExplosion.getProbability() > 0.0 &&
		// fireExplosion.getConsequence() > 0.0) {
		// maxProbability += fireExplosion.getMaxProbability();
		// totalProbability += fireExplosion.getProbability();
		// totalConsequence += fireExplosion.getConsequence();
		// n++;
		// }

		// MachineryFailure machineryFailure = new MachineryFailure(metoc,
		// this);
		// // machineryFailure.save();
		// if (machineryFailure.getProbability() > 0.0 &&
		// machineryFailure.getConsequence() > 0.0) {
		// maxProbability += machineryFailure.getMaxProbability();
		// totalProbability += machineryFailure.getProbability();
		// totalConsequence += machineryFailure.getConsequence();
		// n++;
		// }

		// requires static info
		StrandedByMachineFailure strandedByMachineFailure = new StrandedByMachineFailure(metoc, this);
		strandedByMachineFailure.save();
		maxConsequence = strandedByMachineFailure.getMaxConsequence();
		if (strandedByMachineFailure.getProbability() > 0.0 && strandedByMachineFailure.getConsequence() > 0.0) {
			maxProbability += strandedByMachineFailure.getMaxProbability();
			totalProbability += strandedByMachineFailure.getProbability();
			totalConsequence += strandedByMachineFailure.getConsequence();
			n++;
		}

		StrandedByNavigationError strandedByNavigationError = new StrandedByNavigationError(metoc, this);
		strandedByNavigationError.save();
		if (strandedByNavigationError.getProbability() > 0.0 && strandedByNavigationError.getConsequence() > 0.0) {
			maxProbability += strandedByNavigationError.getMaxProbability();
			totalProbability += strandedByNavigationError.getProbability();
			totalConsequence += strandedByNavigationError.getConsequence();
			n++;
		}

		// HullFailure hullFailure = new HullFailure(metoc, this);
		// hullFailure.save();
		// if (hullFailure.getProbability() > 0.0 &&
		// hullFailure.getConsequence() > 0.0) {
		// maxProbability += hullFailure.getMaxProbability();
		// totalProbability += hullFailure.getProbability();
		// totalConsequence += hullFailure.getConsequence();
		// n++;
		// }

		// Foundering foundering = new Foundering(metoc, this);
		// foundering.save();
		// if (foundering.getProbability() > 0.0 && foundering.getConsequence()
		// > 0.0) {
		// maxProbability += foundering.getMaxProbability();
		// totalProbability += foundering.getProbability();
		// totalConsequence += foundering.getConsequence();
		// n++;
		// }

		if (cpaTarget != null && cpaTarget.hasStaticInfo()) {
			Collision col = new Collision(metoc, this, cpa, cpaTime, cpaTarget);
			col.save();
			if (col.getProbability() > 0.0 && col.getConsequence() > 0.0) {
				maxProbability += col.getMaxProbability();
				totalProbability += col.getProbability();
				totalConsequence += col.getConsequence();
				n++;
			}
		}

		// Calculates the total risk index
		// TODO: Here we let the probabilities of the different incidents be
		// independent
		// of each other. We should consider how much this affects the result.
		if (maxProbability > 1.0) {
			maxProbability = 1.0;
		}
		if (totalConsequence > maxConsequence) {
			totalConsequence = maxConsequence;
		}
		double probabilityNormalized = 0.0;
		if (maxProbability > 0.0)
			probabilityNormalized = totalProbability / maxProbability;
		double consequenceNormalized = 0.0;
		if (maxConsequence > 0.0)
			consequenceNormalized = totalConsequence / maxConsequence;

		double riskIndex = totalProbability * totalConsequence;
		double riskIndexNormalized = 0.0;
		if (maxProbability * maxConsequence > 0.0)
			riskIndexNormalized = riskIndex / (maxProbability * maxConsequence);

		// This is just to get the total written to the db. MachineryFailure is
		// not used elsewhere
		MachineryFailure machineryFailure = new MachineryFailure(metoc, this);
		machineryFailure.setConsequence(totalConsequence);
		machineryFailure.setProbability(totalProbability);
		machineryFailure.setRiskIndex(riskIndex);
		machineryFailure.setConsequenceNormalized(consequenceNormalized);
		machineryFailure.setProbabilityNormalized(probabilityNormalized);
		machineryFailure.setRiskIndexNormalized(riskIndexNormalized);
		machineryFailure.save();

		lastUpdated = System.currentTimeMillis();
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
			/*
			 * Date from loyds table
			 */
			ship1.age = cal.get(Calendar.YEAR) - staticInfo.getYearOfBuild();
			ship1.deadweight = staticInfo.getDeadweight();
			ship1.grossTonnage = staticInfo.getGt();
			ship1.numberOfPersons = staticInfo.getPassengers();
			ship1.designSpeed = staticInfo.getSpeed();
			ship1.valueOfShip = staticInfo.getNewbuildingPrice();
		}
		ship1.loa = staticInfo.getLength(); // m
		ship1.breadth = staticInfo.getBreadth(); // m
		if (staticInfo.getDraught() != null) {
			ship1.designDraught = staticInfo.getDraught();
		}
		if (actualDraught != null) {
			ship1.draught = actualDraught;
		}

		ship1.speed = sog; // knots

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

	public double getLastUpdated() {
		return lastUpdated;
	}

}
