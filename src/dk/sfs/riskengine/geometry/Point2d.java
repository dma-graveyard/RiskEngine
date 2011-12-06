package dk.sfs.riskengine.geometry;

public class Point2d {

	public double x;
	public double y;
	public double lon;
	public double lat;
	public SphereProjection sphere;
	
	
	/**Constructor
	 *
	 */
	public Point2d() {
		sphere=new SphereProjection();
	}
	
	/**Constructor
	 * @param x0
	 * @param y0
	 */
	public Point2d(double x0,double y0) {
		x=x0;y=y0;
		sphere=new SphereProjection();
	}
	
	public Point2d Minus(Point2d p2) {
		Point2d p1=new Point2d();
		p1.x=x-p2.x;
		p1.y=y-p2.y;
		return p1;
	}
	
	public Point2d Plus(Point2d p2) {
		Point2d p1=new Point2d();
		p1.x=x+p2.x;
		p1.y=y+p2.y;
		return p1;
	}
	
	public Point2d Multiply(double s) {
		Point2d p1=new Point2d();
		p1.x=x*s;
		p1.y=y*s;
		return p1;
	}
	
	
	public void setProjectionPoint(double lon0, double lat0) {
		sphere.setCentralPoint(lon0,lat0);
	}
	
	public void setProjectionPoint(Point2d p) {
		sphere.setCentralPoint(p.lon,p.lat);
	}
	
	public void setLatLon(double lon1,double lat1) {
		lon=lon1;lat=lat1;
		calcxy();
	}
	
	
	public void setxy(double x1,double y1) {
		x=x1;y=y1;
		calcLatLon();
	}
	
	
	/**Calculates the parameters lat and lon using x,y
	 * 
	 */
	public void calcLatLon() {
		lon=sphere.x2Lon(x, y);
		lat=sphere.y2Lat(x, y);
	}
	
	
	/**Calculates the parameters x and y using lon and lat
	 * 
	 */
	public void calcxy() {
		x=sphere.lon2x(lon, lat);
		y=sphere.lat2y(lon, lat);
	}
	
	
	/**Calculates the distance between this point and point p
	 * @param p A point of type Point2d
	 * @return A double 
	 */
	public double distance(Point2d p) {
		double d=Math.sqrt(Math.pow((x-p.x),2)+Math.pow((y-p.y),2));
		return d;
	}
	
	public double length() {
		return Math.sqrt(Math.pow((x),2)+Math.pow((y),2));
	}
	
	/**Calculates the dot product between this point and point p
	 * @param p A point of type Point2d
	 * @return A double 
	 */
	public double dotProduct(Point2d p)
	{
	    double d=x*p.x+y*p.y;
	    return d;
	}
	
	public double crossProduct(Point2d p)
	{
	    double crossProduct=x*p.y-y*p.x;
	    return crossProduct;
	}
	
	
	/**Treats the point as a vector and normalize it.
	 * 
	 */
	public void normalise()
	{
	    double d=Math.sqrt(x*x+y*y);
	    if (d>0.0) {
	        x=x/d;
	        y=y/d;
	    }
	}
	
	
	/**Returns a unit vector for the angle angleDeg.
	* @param angleDeg is the angle in degrees
	*/
	public static Point2d getUnitVector(double angleDeg) {
		Point2d u=new Point2d();
		u.x=Math.cos(angleDeg/180.0*Math.PI);
		u.y=Math.sin(angleDeg/180.0*Math.PI);
		return u;
	}
	
	/**
	 * Returns a unit vector for this vector and p2;
	 * @param p2
	 * @return unit vector
	 */
	public Point2d getUnitVector (Point2d p2) {
		Point2d u=new Point2d();
		
		double d=distance(p2);
		if (d>0.0) {
			u.x=(p2.x-x)/d;
			u.y=(p2.y-y)/d;
		}
		return u;
	}
	
	public Point2d getUnitVector() {
		Point2d u=new Point2d();
		Point2d p0=new Point2d(0.0,0.0);
		double d=distance(p0);
		if (d>0.0) {
			u.x=(x-p0.x)/d;
			u.y=(y-p0.y)/d;
		}
		return u;
		
	}
	
	/**Returns the tv�rvector for this vector
	* 
	*/
	public Point2d getPerpVector() {
		Point2d u=new Point2d();
		u.x=y;
		u.y=-x;
		return u;
	}
	
	
	
	/**Threat this point as a vector starting in (0,0) and calculates the angle between this and the vector A
	 * @param A point2d treated as a vector beginning in (0,0)
	 * @return The angle.
	 */
	public double getAngle(Point2d A)
	{
	    //Get the dot product and cross product.
	    double dot_product = x*A.x+y*A.y;
	    double cross_product = crossProduct(A);

	    //Calculate the angle.
	    //double angle=acos(dot_product);
	    double angle = aTan2(cross_product, dot_product);
	    angle=angle*180.0/Math.PI;
	    if (angle<0.0) angle=360.0+angle;
	    return angle;
	}
	
	public double getAngle() {
		Point2d p1=new Point2d(1,0);
		Point2d p=getUnitVector();
		double angle=p1.getAngle(p);
		return angle;
	}
	
	/**Calculates the angle with tangent opp/hyp.
	 * @param opp A double.
	 * @param adj A double.
	 * @return A double between Pi and -Pi
	 */
	public double aTan2(double opp, double adj)
	{
	    double angle;

	    //Get the basic angle.
	    if (Math.abs(adj)<0.0001)
	        angle=Math.PI;
	    else
	        angle=Math.abs(Math.atan(opp/adj));

	    //See if we are in quadrant 2 or 3.
	    if (adj < 0.0)
	       angle = Math.PI - angle;

	    //See if we are in quadrant 3 or 4.
	    if (opp<0.0)
	        angle=-angle;

	    return angle;
	}
	
	
	/**Rotates this point around a point given as a Point2d.
	 * 
	 * @param aroundPnt The point around which to rotate.
	 * @param thetaDeg The number of degrees to rotate
	 */
	public void rotate(Point2d aroundPnt, double thetaDeg)
	{
	    double theta = thetaDeg / 180.0 * Math.PI;
	    double x0=aroundPnt.x;
	    double y0=aroundPnt.y;

	    double x2 = x0 + (x - x0) * Math.cos(theta) - (y - y0) * Math.sin(theta);
	    double y2 = y0 + (x - x0) * Math.sin(theta) + (y - y0) * Math.cos(theta);

	    x=x2; y=y2;
	    calcLatLon();
	}
	
	/**Rotates this point around a point given as x0,y0.
	 * 
	 * @param x0 The x-cord around which to rotate.
	 * @param y0 The y-cord around which to rotate.
	 * @param thetaDeg The number of degrees to rotate
	 */
	public void rotate(double x0, double y0, double thetaDeg)
	{
	    Point2d pt0 =  new Point2d(x0,y0);
	    rotate(pt0,thetaDeg);
	}
	

	//---------------------------------------------------
	/**Converts this point to a latitude longitude point. Use pt0 as projection point
	 * 
	 */
	public void xy2LatLon(Point2d pt0)    //pt0 is the central point.
	{
	    SphereProjection sphere=new SphereProjection(pt0.x,pt0.y);
	    double lon1=sphere.x2Lon(x,y);
	    double lat1=sphere.y2Lat(x,y);
	    x=lon1;y=lat1;
	}

	
	/**Converts this point to a x,y [m] point. Use pt0 as projection point
	 * 
	 */
	public void latLon2xy(Point2d pt0)    //pt0 is the central point.
	{
		SphereProjection sphere=new SphereProjection(pt0.x,pt0.y);
	    double x1=sphere.lon2x(x,y);
	    double y1=sphere.lat2y(x,y);
	    x=x1;y=y1;
	}
	
	/**Converts this point to a x,y [m] point. Use x0,y0 as projection point
	 * @param x0 longitude of projection point
	 * @param y0 latitude of projection point
	 */
	public void latLon2xy(double x0, double y0)    //pt0 is the central point.
	{
		SphereProjection sphere=new SphereProjection(x0,y0);
	    double x1=sphere.lon2x(x,y);
	    double y1=sphere.lat2y(x,y);
	    x=x1;y=y1;
	}
	
	
	public double distanceLatLon(Point2d A)
	{
		calcxy();
		Point2d B =  new Point2d();
		B=A;
		B.sphere=sphere;
	    B.calcxy();
	    double dist=distance(B);
	    return dist;
	}
	
	public double distanceLatLon(double lon1,double lat1)
	{
	    Point2d pt1 =  new Point2d();
	    pt1.setLatLon(lon1,lat1);
	    pt1.sphere=sphere;
	    double dist=distanceLatLon(pt1);
	    return dist;
	}
	
	
	public Object clone() {
		Point2d p;
		try
		{
			p=(Point2d) super.clone();
		}
		catch(CloneNotSupportedException e)
		{
			return null;
		}
		return p;
	}
}
