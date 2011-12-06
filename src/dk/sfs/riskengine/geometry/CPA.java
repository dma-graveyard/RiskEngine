package dk.sfs.riskengine.geometry;

public class CPA {

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
			return 0.0; // any time is ok. Use time 0.

		Point2d w0 = new Point2d();
		w0 = p1.Minus(p2); // w0=p1-p2
		double cpatime = -(w0.dotProduct(dv)) / dv2; // cpatime=(w0*dv)/dv2

		if (cpatime < 0.0) { // Need to check this!!!!!!!!!!!!!!
			w0 = p2.Minus(p1);
			cpatime = -(w0.dotProduct(dv)) / dv2;
		}

		return cpatime; // time of CPA
	}

	// cpa_distance(): compute the distance at CPA for two tracks
	// Input: two tracks defined by point p1, speed vector v1 and point p2 and
	// speed vector v2
	// Return: the distance for which the two tracks are closest
	public static double cpa_distance(Point2d p1, Point2d v1, Point2d p2, Point2d v2) {
		double ctime = cpa_time(p1, v1, p2, v2);

		Point2d cpt1 = new Point2d();
		Point2d cpt2 = new Point2d();
		cpt1 = p1.Plus(v1.Multiply(ctime)); // p1+(v1*ctime)
		cpt2 = p2.Plus(v2.Multiply(ctime)); // p2+(v2*ctime)

		double d = cpt1.distance(cpt2); // Distance between the points cpt1 and
										// cpt2
		return d; // distance at CPA
	}

	public static double MsToKnots(double speed) {
		return 0.514444 * speed;
	}

	public static double KnotsToMs(double speed) {
		return speed / 0.514444;
	}
}
