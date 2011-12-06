package dk.sfs.riskengine;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;

import com.mysql.jdbc.interceptors.SessionAssociationInterceptor;

import dk.sfs.riskengine.ais.RiskTarget;
import dk.sfs.riskengine.geometry.CPA;
import dk.sfs.riskengine.geometry.Geofunctions;
import dk.sfs.riskengine.geometry.Point2d;
import dk.sfs.riskengine.metoc.Metoc;
import dk.sfs.riskengine.persistence.domain.DepthPoint;
import dk.sfs.riskengine.persistence.mapper.DBSessionFactory;
import dk.sfs.riskengine.persistence.mapper.DepthPointMapper;

public abstract class IncidentType {

	protected Metoc metoc;
	protected RiskTarget vessel;

	/**
	 * @param cog
	 * @param winddirection
	 *            compass angle FROM where the wind is blowing
	 * @param windspeed
	 *            in m/s
	 * @param currentdirection
	 *            compass direction IN which the water is flowing
	 * @param currentspeed
	 *            in knots
	 */
	public IncidentType(Metoc metoc, RiskTarget vessel) {
		super();
		this.metoc = metoc;
		this.vessel = vessel;
	}

	public double getTotalRisk() {
		return getAgeFactor(new GregorianCalendar().get(Calendar.YEAR) - vessel.getYearOfBuild())
				* getFlagFactor(vessel.getFlag()) * getWindcurrentFactor() * getVisibilityFactor() * getExposure()
				* getCasualtyRate();

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
	 * @param visibility
	 *            in m Override for incident specific wind factor
	 * @return
	 */
	public double getVisibilityFactor() {
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

	public double getCasualtyRate() {
		String shiptype = vessel.getShipTypeIwrap();
		double shipsize = vessel.getLength();
		return getNumberOfIncidentPerMinut(shiptype, shipsize) / getNumberOfShipsByTypeAndSize(shiptype, shipsize);
	}

	public abstract double getNumberOfIncidentPerMinut(String shiptype, double shipsize);

	/**
	 * TODO implement
	 * 
	 * @return
	 */
	private int getNumberOfShipsByTypeAndSize(String shiptype, double shipsize) {
		return 30;
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
		SqlSession sess = DBSessionFactory.getSession();
		try {
			 
			DepthPointMapper mapper = sess.getMapper(DepthPointMapper.class);

			/*
			 * Find closest deep point.
			 */
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("lat", vessel.getGeoLocation().getLatitude());
			map.put("lon", vessel.getGeoLocation().getLongitude());
			DepthPoint p = mapper.findClosestDeepPoint(map);

			/*
			 * Find first grounding point in direction
			 */

			double angle = Geofunctions.d2r(Geofunctions.compass2cartesian(direction));

			Long ratioM = Math.round(Math.sin(angle) * 10l);
			Long ratioN = Math.round(Math.cos(angle) * 10l);

			map.put("m", p.getM());
			map.put("n", p.getN());
			map.put("ratioM", ratioM);
			map.put("ratioN", ratioN);
			map.put("depth", vessel.getDraught());

			DepthPoint ground = mapper.findGroundingPointIndices(map);
			ground = mapper.selectByIndices(ground);

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
		} finally {
			sess.close();
		}
	}

}
