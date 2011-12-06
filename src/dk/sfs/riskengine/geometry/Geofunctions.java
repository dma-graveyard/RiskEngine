package dk.sfs.riskengine.geometry;

public final class Geofunctions {
	
	private Geofunctions() {}
	
	/**Converts a compass heading to a cartesian angle
	 * ex: 88 converts to 2
	 * 
	 * @param a A compass angle
	 * @return A cartesian angle
	 */
	public static double compass2cartesian(double a)
	{
	    double cartesianAngle;

	    if ((a >= 0.0) && (a <= 90.0))
	    {
	        cartesianAngle = 90.0 - a;
	    }
	    else
	    {
	        cartesianAngle = 450.0 - a;
	    }

	    return cartesianAngle;
	}


	/**Converts a cartesian angle to a compass heading
	 * ex: 0 converts to 90 
	 * 
	 * @param a A cartesian angle
	 * @return A compass angle
	 */
	public static double cartesian2compass(double a)
	{
	    double angle;

	    if (a>360.0) a=a-360.0;
	    if (a<0.0) a=360.0-a;
	    if (a>=0.0 && a<=90.0)
	        angle=90.0-a;
	    else
	        angle=450.0-a;

	    return angle;
	}
	
	/**Calculates the difference between angle A and angle B
	 * 
	 * @param a Angle1 in degrees
	 * @param b Angle2 in degrees
	 * @return The difference in degrees
	 */
	public static double angleDiff(double a, double b)
	{
	    double c=Math.cos(d2r(a))*Math.cos(d2r(b))+Math.sin(d2r(a))*Math.sin(d2r(b));
	    c=90.0-r2d(Math.asin(c));
	    return c;
	}
	
	public static double angleDiffSign(double a, double b)
	{
		double difference = b - a;
	    while (difference < -180) difference += 360;
	    while (difference > 180) difference -= 360;
	    return difference;
	}

	/**Converts from degrees to radians
	 * 
	 * @param deg
	 * @return The radians
	 */
	public static double d2r(double deg)
	{
	    return deg/180.0*Math.PI;
	}

	/**Converts from radians to degrees
	 * 
	 * @param rad
	 * @return The degrees
	 */
	public static double r2d(double rad)
	{
	    return rad*180.0/Math.PI;
	}
	
	
}
