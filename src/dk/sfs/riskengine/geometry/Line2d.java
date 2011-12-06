package dk.sfs.riskengine.geometry;

public class Line2d {
	
	public Point2d p1;
	public Point2d p2;
	public SphereProjection sphere;
	
	
	public Line2d() {
		p1=new Point2d();
		p2=new Point2d();
		sphere=new SphereProjection();
	}
	
	public Line2d(double x1,double y1,double x2,double y2) {
		p1=new Point2d();
		p2=new Point2d();
		p1.x=x1;p1.y=y1;
		p2.x=x2;p2.y=y2;
		sphere=new SphereProjection();
	}
	
	public Line2d(Point2d pt1, Point2d pt2) {
		p1=new Point2d();
		p2=new Point2d();
		p1=pt1;
		p2=pt2;
		sphere=new SphereProjection();
	}
	
	
	public void setProjectionPoint(double lon0, double lat0) {
		sphere.setCentralPoint(lon0,lat0);
		p1.sphere.setCentralPoint(lon0, lat0);
		p2.sphere.setCentralPoint(lon0, lat0);
		p1.calcxy();
		p2.calcxy();
	}

	
	public Point2d getIntersectionPoint(Line2d l2) {
		Point2d p=new Point2d();
		double uaT=(l2.p2.x-l2.p1.x)*(p1.y-l2.p1.y)-(l2.p2.y-l2.p1.y)*(p1.x-l2.p1.x);
		double ubT=(p2.x-p1.x)*(p1.y-l2.p1.y)-(p2.y-p1.y)*(p1.x-l2.p1.x);
		double uN=(l2.p2.y-l2.p1.y)*(p2.x-p1.x)-(l2.p2.x-l2.p1.x)*(p2.y-p1.y);
		
		if (uN!=0.0) {
			double ua=uaT/uN;
			double ub=ubT/uN;
			double x=p1.x+ua*(p2.x-p1.x);
			double y=p1.y+ua*(p2.y-p1.y);
			p.x=x;p.y=y;		
		}
		return p;
	}
	
	
	public boolean doLinesIntersects(Line2d l2,boolean onlySegments) {
		boolean b=false;
		
		double uaT=(l2.p2.x-l2.p1.x)*(p1.y-l2.p1.y)-(l2.p2.y-l2.p1.y)*(p1.x-l2.p1.x);
		double ubT=(p2.x-p1.x)*(p1.y-l2.p1.y)-(p2.y-p1.y)*(p1.x-l2.p1.x);
		double uN=(l2.p2.y-l2.p1.y)*(p2.x-p1.x)-(l2.p2.x-l2.p1.x)*(p2.y-p1.y);
				
		if (uN!=0.0) {
			double ua=uaT/uN;
			double ub=ubT/uN;
			double x=p1.x+ua*(p2.x-p1.x);
			double y=p1.y+ua*(p2.y-p1.y);
			if (!onlySegments) b=true;
			else
				if (ua>=0 && ua<=1.0 && ub>=0.0 && ub<=0.0) b=true;			
		}
		return b;		
	}
	
	/**Calculates the parameters lat and lon using x,y
	 * 
	 */
	public void calcLatLon() {
		p1.calcLatLon();
		p2.calcLatLon();
	}
	
	/**Calculates the parameters x,y using lat and lon
	 * 
	 */
	public void calcxy() {
		p1.calcxy();
		p2.calcxy();
	}
	
	
	public double length() {
		double d=Math.sqrt(Math.pow((p2.x-p1.x),2)+Math.pow((p2.y-p1.y),2));
		return d;
	}
	
	
	public void setLinePerpendicularToAngle(double lon0, double lat0, double lineLength, double angleDeg, boolean pointIsMidpoint) {
		
		p1.setLatLon(lon0, lat0);
		p2.setLatLon(lon0, lat0);
		
		
		Point2d u=Point2d.getUnitVector(angleDeg);
		u=u.getPerpVector();
		
		if (pointIsMidpoint) {
			p1.x+=u.x*lineLength/2.0;
			p1.y+=u.y*lineLength/2.0;
			p2.x+=-u.x*lineLength/2.0;
			p2.y+=-u.y*lineLength/2.0;
		}
		else
		{
			p2.x+=u.x*lineLength;
			p2.y+=u.y*lineLength;
		}
		
		p1.calcLatLon();
		p2.calcLatLon();
	}
	
	
public void setLineEqualToAngle(double lon0, double lat0, double lineLength, double angleDeg, boolean pointIsMidpoint) {
		
		p1.setLatLon(lon0, lat0);
		p2.setLatLon(lon0, lat0);
		
		
		Point2d u=Point2d.getUnitVector(angleDeg);
		
		if (pointIsMidpoint) {
			p1.x+=u.x*lineLength/2.0;
			p1.y+=u.y*lineLength/2.0;
			p2.x+=-u.x*lineLength/2.0;
			p2.y+=-u.y*lineLength/2.0;
		}
		else
		{
			p2.x+=u.x*lineLength;
			p2.y+=u.y*lineLength;
		}
		
		p1.calcLatLon();
		p2.calcLatLon();
	}


public Point2d getPointPerpToLine(double s, double d) {
	Point2d p=new Point2d();
	p.x=p1.x;p.y=p1.y;
	
	Point2d p_unit=new Point2d();
	p_unit=p.getUnitVector(p2);
	p.x=p1.x+s*p_unit.x;
	p.y=p1.y+s*p_unit.y;
		
	Point2d p_perp=new Point2d();
	p_perp=p_unit.getPerpVector();
	p.x=p.x+d*p_perp.x;
	p.y=p.y+d*p_perp.y;
	
	p.sphere=sphere;
	p.calcLatLon();
	
	return p;
}
	
}
