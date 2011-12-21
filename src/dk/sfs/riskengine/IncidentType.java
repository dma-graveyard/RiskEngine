package dk.sfs.riskengine;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;

import dk.sfs.riskengine.ais.RiskTarget;
import dk.sfs.riskengine.geometry.CPA;
import dk.sfs.riskengine.geometry.Geofunctions;
import dk.sfs.riskengine.geometry.Point2d;
import dk.sfs.riskengine.metoc.Metoc;
import dk.sfs.riskengine.persistence.domain.DepthPoint;
import dk.sfs.riskengine.persistence.mapper.AccidentFrequenceMapper;
import dk.sfs.riskengine.persistence.mapper.DBSessionFactory;

public abstract class IncidentType {

	protected Metoc metoc;
	protected RiskTarget vessel;

	public enum AccidentType{
			COLLISION,
			FOUNDERING,
			HULLFAILURE,        
			MACHINERYFAILURE,   
			FIRE_EXPLOSION,     
			POWEREDGROUNDING,   
			DRIFTGROUNDING
			}
	
	public enum ShipSize{
		SMALL, MEDIUM, LARGE;
	}
	
	/**
	 * @param cog
	 * 
	 */
	public IncidentType(Metoc metoc, RiskTarget target) {
		super();
		this.vessel = target;
		this.metoc = metoc;
	}

	public double getTotalRisk() {
		double c = 1.0;
		if (vessel.hasStaticInfo()) {
			// requires static info
			c *= getCasualtyRate(vessel.getShipTypeIwrap(), vessel.getLength());
			
			if (vessel.getYearOfBuild() != null) {

				c *= getAgeFactor(new GregorianCalendar().get(Calendar.YEAR) - vessel.getYearOfBuild());
			}
			if (vessel.getFlag() != null) {
				c *= getFlagFactor(vessel.getFlag());
			}
			
		}
		return c*getWindcurrentFactor() * getVisibilityFactor() * getExposure();

	}

	public abstract double getAgeFactorParam();

	public double getAgeFactor(double age) {
		return Math.exp(getAgeFactorParam() * age);
	}

	public double getFlagFactor(String flag) {
		return 1.0;
	}

	/**
	 * Default wind factor. Override for incident specific wind factor
	 * 
	 * @return
	 */
	public double getWindcurrentFactor() {
		return 1.0;
	}

	/**
	 *  Override for incident specific visiblity factor when visibility is availaible.
	 * @param visibility
	 *           
	 * @return
	 */
	public final double getVisibilityFactor() {
		return 1.0;
	}

	/**
	 * Factor based on navigational status. Nav status is not reliable !!
	 * 
	 * @param navStat
	 * @return
	 */
	public double getNavStatFactor(int navStat) {
		return 1.0;
	}

	public double getExposure() {
		return 1.0;
	}

	public double getCasualtyRate(String shipTypeIwrap, double shipsize) {
		
		SqlSession sess = DBSessionFactory.getSession();

		try {
			AccidentFrequenceMapper mapper = sess.getMapper(AccidentFrequenceMapper.class);			
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("accidentTypeName", getAccidentType().name());
			map.put("shipTypename", shipTypeIwrap);
			ShipSize size;
			if(shipsize>100){
				size = ShipSize.LARGE;
			}else if(shipsize>100){
				size = ShipSize.SMALL;
			} else{
				size = ShipSize.MEDIUM;
			}
			
			map.put("shipSize", size.name());
			return mapper.selectByShipTypeAndAccidentType(map);
		} finally {
			sess.close();
		}
		
	}

	
	
	
	public static Double selectByAvgByAccidentType(String accidentName){
		SqlSession sess = DBSessionFactory.getSession();

		try {
			AccidentFrequenceMapper mapper = sess.getMapper(AccidentFrequenceMapper.class);			
			return mapper.selectByAvgByAccidentType(accidentName);
		} finally {
			sess.close();
		}
	}

	/**
	 * @return a vector where x is the speed in knots and y is the compass
	 *         direction
	 */
	protected Point2d estimateCombinedWindCurrentDrift() {

		/*
		 * Build a wind vector
		 */
		// Translate windspeed into a speed vector. Winddirection is opposite.
		double angle = Geofunctions.compass2cartesian(metoc.getWindDirection()) + 180.0;

		if (angle >= 360.0) {
			angle = 360.0 - angle;
		}
		Point2d w = Point2d.getUnitVector(angle);

		// assume that the drifting ship will move with 15% of the wind speed.
		// TODO make it a function of the ships superstructure
		w = w.Multiply(metoc.getWindSpeed() * 0.15);

		/*
		 * Buils a current vector
		 */
		angle = Geofunctions.compass2cartesian(metoc.getCurrentDirection());
		Point2d c = Point2d.getUnitVector(angle);
		c = c.Multiply(metoc.getCurrentSpeed() * 0.514444);

		// Add vectors

		Point2d p = w.Plus(c);

		Point2d rst = new Point2d();
		rst.x = p.length() / 0.514444; // Speed in knots
		rst.y = p.getAngle(); // Direction
		rst.y = Geofunctions.cartesian2compass(rst.y);

		return rst;
	}

	/**
	 * Caluclate the time before the ship will strand if continuing in same
	 * direction, same speed.
	 * 
	 * @param speed
	 *            of ship in knots
	 * @param direction
	 *            of ship in degree (0=North, clockwise)
	 * 
	 * @return time in seconds
	 */

	protected double getTimeToGrounding(double speed, double direction) {

		DepthPoint ground = DepthPoint.findGroundingPoint(vessel.getPos(), direction, vessel.getDraught());
		/*
		 * get distance from ship to grounding point
		 */
		Point2d pos = new Point2d();
		pos.setLatLon(vessel.getGeoLocation().getLongitude(), vessel.getGeoLocation().getLatitude());
		double dist = pos.distanceLatLon(ground.getLon(), ground.getLat());
		/*
		 * return time to grounding
		 */
		return dist / CPA.KnotsToMs(speed);
	}

	public abstract AccidentType getAccidentType();
}
