package dk.sfs.riskengine.geometry;

import java.util.List;

import dk.frv.ais.geo.GeoLocation;
import dk.sfs.riskengine.persistence.domain.RiskIndexes;

public class CPA {

	private static final SphereProjection SPHERE = new SphereProjection();

	// cpa_time(): compute the time of CPA for two tracks
	// Input: two tracks defined by point p1, speed vector v1 and point p2 and
	// speed vector v2
	// Return: the time at which the two tracks are closest
	// all units are SI
	// double angleDeg=Geofunctions.compass2cartesian(cog);
	// p=getUnitVector(angleDeg); p is the unit vector for the course vector
	// v1.x=p.x*speed*0.514444; //1 knot=0.514444m/s
	// v1.y=p.y*speed*0.514444;
	// p1.x=Initial x position of ship;p1.y=Initial y position of ship;

	public static double cpa_time(Point2d p1, Point2d v1, Point2d p2, Point2d v2) {
		Point2d dv = new Point2d();
		dv = v1.Minus(v2); // Subtract the two speed vectors, dv=v1-v2.

		double SMALL_NUM = 0.000001;
		double dv2 = dv.dotProduct(dv); // dot product between dv and dv
										// dv2=dv.x*dv.x+dv.y*dv.y
		if (dv2 < SMALL_NUM) // the speed vectors are almost parallel
			return Double.POSITIVE_INFINITY; // any time is ok. Use time 0.

		Point2d w0 = new Point2d();
		w0 = p1.Minus(p2); // w0=p1-p2
		double cpatime = -(w0.dotProduct(dv)) / dv2; // cpatime=(w0*dv)/dv2

		return cpatime; // time of CPA
	}

	// cpa_distance(): compute the distance at CPA for two tracks
	// Input: two tracks defined by point p1, speed vector v1 and point p2 and
	// speed vector v2
	// Return: the distance for which the two tracks are closest
	public static double cpa_distance(Point2d p1, Point2d v1, Point2d p2, Point2d v2, double ctime) {

		Point2d cpt1 = new Point2d();
		Point2d cpt2 = new Point2d();
		cpt1 = p1.Plus(v1.Multiply(ctime)); // p1+(v1*ctime)
		cpt2 = p2.Plus(v2.Multiply(ctime)); // p2+(v2*ctime)

		double d = cpt1.distance(cpt2); // Distance between the points cpt1 and
										// cpt2
		return d; // distance at CPA
	}

	public static Point2d getPositionVector(GeoLocation pos) {
		Point2d positionPoint = new Point2d();
		positionPoint.sphere = SPHERE;
		positionPoint.setLatLon(pos.getLongitude(), pos.getLatitude());
		return positionPoint;
	}

	public static Point2d getSpeedVector(double cog, double sog) {
		return Point2d.getUnitVector(Geofunctions.compass2cartesian(cog)).Multiply(KnotsToMs(sog));
	}

	public static double MsToKnots(double speed) {
		return 0.514444 / speed;
	}

	public static double KnotsToMs(double speed) {
		return speed * 0.514444;
	}

	public static void setSphereCentralPoint(double lon, double lat) {
		SPHERE.setCentralPoint(lon, lat);
	}

	public static void main(String[] args) {

		List<RiskIndexes> risk = RiskIndexes.selectLatest();

		Point2d p1 = new Point2d();
		p1.sphere.setCentralPoint(11.0, 55.5);
		Point2d v1 = new Point2d();
		Point2d p2 = new Point2d();
		p2.sphere.setCentralPoint(11.0, 55.5);
		Point2d v2 = new Point2d();
		for (RiskIndexes risk_: risk) {

			p1.lon = risk_.getLon();
			p1.lat = risk_.getLat();
			p1.calcxy();
			double cog1 = risk_.getCog();
			double sog1 = risk_.getSog();

			p2.lon = risk_.getCpaLon();
			p2.lat = risk_.getCpaLat();
			p2.calcxy();
			double cog2 = risk_.getCpaCog();
			double sog2 = risk_.getCpaSog();

			v1 = Point2d.getUnitVector(Geofunctions.compass2cartesian(cog1)).Multiply(CPA.KnotsToMs(sog1));
			v2 = Point2d.getUnitVector(Geofunctions.compass2cartesian(cog2)).Multiply(CPA.KnotsToMs(sog2));

//			v1 = getSpeedVector(cog1, sog1);
//			v2 = getSpeedVector(cog2, sog2);
			
			double t = CPA.cpa_time(p1, v1, p2, v2);
			double d = CPA.cpa_distance(p1, v1, p2, v2, t);
			System.out.println(" time " + t + " " + risk_.getCpaTime());
			System.out.println("distance " + d + " " + risk_.getCpaDist());
		}
		

	}

}
