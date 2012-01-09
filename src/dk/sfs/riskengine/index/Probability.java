package dk.sfs.riskengine.index;
import dk.sfs.riskengine.geometry.Geofunctions;
import dk.sfs.riskengine.geometry.Point2d;
import dk.sfs.riskengine.statistics.Weibull;
import dk.sfs.riskengine.statistics.Weibull;

public class Probability {

	//Main function
	public double getProbability() {
	
		int incidentType=6;
		double winddirection=25.0;		// Blowing from north-north-east
		double windspeed=10.0;			// in m/s
		double currentdirection=225.0;	// Running to the south west
		double currentspeed=3.0;		// in knots
		double visibility=10000.0;		// in m

		int age=10; 			// Years. Look it up in Seaweb, then we also need the IMO number
		double lat=55.7;
		double lon=11.2;
		double cog=90.0;		// Sailing eastward
		double sog=15.0;		// Knots
		double draught=7.5;		// in m.
		double shipLength=250.0; // in m.
		String flag="";			// Get it from Seaweb or mmsi
		int shiptype=1;			// Need to be defined
		double cpa=0.5;			// in nautical miles;	I assume there is only one other ship. That might not be the case!
		double tcpa=2.0;		// in minutes

		
		//Calculate the drift parameters. Assumes the ship is dead in the water.
		Point2d resultant=new Point2d();
		resultant=estimateCombinedWindCurrentDrift(winddirection, windspeed, currentdirection, currentspeed);
		double driftSpeed=resultant.x;	//In knots
		double driftDirection=resultant.y;	//Compass direction

		//Multiply factors
		double ageFactor=getAgeFactor(incidentType, age);
		double flagFactor=getFlagFactor(incidentType, flag);	
		double windCurrentFactor=getWindCurrentFactor(incidentType, cog, winddirection, windspeed, currentdirection, currentspeed);
		double visibilityFactor=getVisibilityFactor(incidentType, visibility);
		
		double exposure=getExposure(incidentType, lat, lon, cog, sog, draught, driftSpeed, driftDirection, cpa, tcpa);
		double casrat=getCasualtyRate(incidentType, shiptype, shipLength);
		
		double p=ageFactor * flagFactor * windCurrentFactor * visibilityFactor * exposure * casrat;
		return p;
	}
	
	
	//Input age in years
	public double getAgeFactor(int incidentType, double age) {
		double lambda=0.0;
		
		if (incidentType==1) lambda=0.01;	//Collision
		if (incidentType==2) lambda=0.105;	//Foundering
		if (incidentType==3) lambda=0.04;	//Hull failure
		if (incidentType==4) lambda=0.04;	//Machinery failure
		if (incidentType==5) lambda=0.06;	//Fire/explosion
		if (incidentType==6) lambda=0.04;	//Wrecked/Stranded due to navigational error
		if (incidentType==7) lambda=0.04;	//Wrecked/Stranded due to machinery failure
		
		double f=Math.exp(lambda*age);
		return f;
	}
	
	//Todo
	public double getFlagFactor(int incidentType, String flag) {
		return 1.0;
	}
	
	
	//cog is course over ground
	//winddirection is the compass angle from where the wind is blowing
	//wind speed is in m/s
	//currentdirection is the compass direction in which the water is flowing
	//currentspeed is in knots
	public double getWindCurrentFactor(int incidentType, double cog, double winddirection, double windspeed, double currentdirection, double currentspeed) {
		double f=1.0;
		
		boolean crossWind=false;		//True if the resultant wind vector is perp to the cog
		boolean crossCurrent=false;
		boolean crossResultant=false;	//True if the resultant wind+current vector is perp to the cog
		
		double anglediff=Math.abs(Geofunctions.angleDiff(cog, winddirection));	//Make sure the angle types are the same 
		if ((anglediff>45 && anglediff<135) || (anglediff>225 && anglediff<315)) crossWind=true;
		
		anglediff=Math.abs(Geofunctions.angleDiff(cog, currentdirection));
		if ((anglediff>45 && anglediff<135) || (anglediff>225 && anglediff<315)) crossCurrent=true;

		Point2d resultant=new Point2d();
		resultant=estimateCombinedWindCurrentDrift(winddirection, windspeed, currentdirection, currentspeed);
		anglediff=Math.abs(Geofunctions.angleDiff(cog,resultant.y));
		if ((anglediff>45 && anglediff<135) || (anglediff>225 && anglediff<315)) crossResultant=true;

		
		if (incidentType==1) { //Collision
			if (resultant.x>3.0 && crossResultant) f=Math.exp(0.2*(resultant.x-3.0));
		}
		
		if (incidentType==2) { //Foundering
			if (windspeed*0.51444>7.0) f=Math.exp(0.2*(windspeed*0.51444-7.0));
		}
		
		if (incidentType==3) { //Hull failure
			if (windspeed*0.51444>7.0) f=Math.exp(0.2*(windspeed*0.51444-7.0));
		}
		
		if (incidentType==4) { //Machinery failure
			if (windspeed*0.51444>7.0) f=Math.exp(0.2*(windspeed*0.51444-7.0));
		}
		
		if (incidentType==5) { //Fire/explosion
			f=1.0;
		}
		
		if (incidentType==6) { //Wrecked/Stranded due to navigational error
			f=1.0;
		}
		
		if (incidentType==7) { //Wrecked/Stranded due to machinery failure
			f=1.0;
		}
		
		return f;
	}

	
	//Input visibility in [m]
	//Returns the visibility factor
	public double getVisibilityFactor(int incidentType, double visibility) {
		double lambda=0.0007;
		double scaleFactor=10.0;
		double minimum=0.9;
		
		double f=minimum+Math.exp(-visibility*lambda)*scaleFactor;
		
		if (incidentType==1) f=f;	//Collision
		if (incidentType==2) f=1.0;	//Foundering
		if (incidentType==3) f=1.0;	//Hull failure
		if (incidentType==4) f=1.0;	//Machinery failure
		if (incidentType==5) f=1.0;	//Fire/explosion
		if (incidentType==6) f=f;	//Wrecked/Stranded due to navigational error
		if (incidentType==7) f=1.0;	//Wrecked/Stranded due to machinery failure
		
		return f;
	}
	
	
	//I do not like this. NavStat is set manually by the ship. Should at least make a sanity check
	public double getNavStatFactor(int incidentType, int navStat) {
		return 1.0;
	}
	
	
	// Not really sure what Marnis thought. Here it is how exposed the ship is to the different incident types
	// lat,lon is the current position of the ship
	// draught in m
	// sog in knots
	// driftspeed in knots
	// driftDirection The compass direction in which the ship will drift
	// cpa in [nautical miles]
	// tcpa in [minutes]
	public double getExposure(int incidentType, double lat, double lon, double cog, double sog, double draught, double driftSpeed, double driftDirection, double cpa, double tcpa) {
		double e=1.0;
		
		double repairTime=Weibull.random(1.1, 5.35)*3600.0;	//Seconds
		
		if (incidentType==1) { //Collision
			double e1=Math.exp(-Math.abs(cpa)*1.0);
			double e2=Math.exp(-Math.abs(tcpa)*0.1);
			e=e1*e2;
		}
		
		if (incidentType==2) { //Foundering
			e=1.0; //The definition of foundering is that the ship sinks or capsizes
		}
		
		if (incidentType==3) { //Hull failure
			e=1.0;
		}
		
		if (incidentType==4) { //Machinery failure. I do not understand why this, when we have type 7
			e=1.0;
		}
		
		if (incidentType==5) { //Fire/explosion
			e=1.0;
		}
		
		if (incidentType==6) { //Wrecked/Stranded due to navigational error
			double dt=60.0; //The time step in seconds
			double abortAfter=3600;	//Abort the search after 3600 seconds
			double t=getTimeToGrounding(lon, lat, cog, sog, draught, dt, abortAfter);
			if (t>0)
				e=Math.exp(-Math.abs(t/60.0)*0.1);	//Using the same as tcpa
			else
				e=0.0;
		}
		
		if (incidentType==7) { //Wrecked/Stranded due to machinery failure
			double dt=60.0; //The time step in seconds
			double abortAfter=3600*12;	//12 hours in seconds
			double t=getTimeToGrounding(lon, lat, driftDirection, driftSpeed, draught, dt, abortAfter);
			e=0.0;
			if (t>0) {
				//ToDo Estimate the probability that the ship can drop its anchor
				if (t<repairTime)
					e=0.0;
				else
					e=Math.exp(-Math.abs(t/60.0)*0.05);
			}
		}
		
		return e;
	}
	

	//Dummy function
	//Returns: Average incidents per minute. Need to discuss the timeinterval
	//This routine should find the values in a database
	public double getCasualtyRate(int incidentType, int shiptype, double shipsize) {
		double r=0.0;
		
		int Nshiptypes=3; //Need to find this
		int Nshipsizes=10;	//Need to find this
		
		if (incidentType==1) { //Collision
			double collisionFreq=3.0;	//The total number of collisions for all ships
			r=collisionFreq/(Nshiptypes*Nshipsizes);	//Uniformly distributed on each type and size;
		}
		
		if (incidentType==2) { //Foundering
			double founderingFreq=3.0;	//The total number of foundering for all ships
			r=founderingFreq/(Nshiptypes*Nshipsizes);	//Uniformly distributed on each type and size;
		}
		
		if (incidentType==3) { //Hull failure
			double hullFailureFreq=3.0;	//The total number of hull failures for all ships
			r=hullFailureFreq/(Nshiptypes*Nshipsizes);	//Uniformly distributed on each type and size;
		}
		
		if (incidentType==4) { //Machinery failure
			double machineryFailureFreq=3.0;	//The total number of machinery failures for all ships
			r=machineryFailureFreq/(Nshiptypes*Nshipsizes);	//Uniformly distributed on each type and size;
		}
		
		if (incidentType==5) { //Fire/explosion
			double fireFreq=3.0;	//The total number of fires for all ships
			r=fireFreq/(Nshiptypes*Nshipsizes);	//Uniformly distributed on each type and size;
		}
		
		if (incidentType==6) { //Wrecked/Stranded due to navigational error
			double poweredGroundingFreq=3.0;	//The total number of powered groundings for all ships
			r=poweredGroundingFreq/(Nshiptypes*Nshipsizes);	//Uniformly distributed on each type and size;
		}
		
		if (incidentType==7) { //Wrecked/Stranded due to machinery failure
			double driftingGroundingFreq=3.0;	//The total number of drift grounding for all ships
			r=driftingGroundingFreq/(Nshiptypes*Nshipsizes);	//Uniformly distributed on each type and size;
		}

		r=r/365.25/24.0/60.0;	//Frequency per minute. Need to discuss the timespan
		return r;
	}	
	
	
	//Iterates along a course line until a grounding occurs.
	public double getTimeToGrounding(double lon, double lat, double cog, double sog, double draught, double dt, double abortAfter) {
		
		Point2d p0=new Point2d();
		p0.lon=lon;
		p0.lat=lat;
		p0.sphere.setCentralPoint(lon, lat);
		p0.calcxy();
		
		Point2d u=new Point2d();
		u=u.getUnitVector(Geofunctions.compass2cartesian(cog));
		
		boolean stop=false;
		double t=0;
		while (!stop && t<abortAfter) {
			p0.x+=u.x*sog*0.514444*t;
			p0.y+=u.y*sog*0.514444*t;
			p0.calcLatLon();
			double depth=10.0;
			//double depth=getDepth(p0.lat, p0.lon);	//TODO!!!!!!!!!!!!!!!
			if (draught>depth*1.1) stop=true;	//Assumes the depth must be at least somewhat smaller than the draught before it really grounds 
			t+=dt;
		}
		if (t>=abortAfter) t=-1;
		return t;
	}
	
	
	//Returns a vector where x is the speed in knots and y is the compass direction
	//winddirection is the compass angle from where the wind is blowing
	//windspeed in m/s
	//currentdirection is the compass direction in which the water is flowing
	//currentspeed in knots
	public Point2d estimateCombinedWindCurrentDrift(double winddirection, double windspeed, double currentdirection, double currentspeed) {
		Point2d rst=new Point2d();
		
		//Translate windspeed into a speed vector
		double angle=Geofunctions.compass2cartesian(winddirection)+180.0;	//Winddirection is opposite
		if (angle>=360.0) angle=360.0-angle;
		Point2d w=new Point2d();
		w=w.getUnitVector(angle);
		w=w.Multiply(windspeed*0.15);	//assume that the drifting ship will move with 15% of the wind speed. Should of course be a function of the ships superstructure
		
		
		angle=Geofunctions.compass2cartesian(currentdirection);
		Point2d c=new Point2d();
		c=c.getUnitVector(angle);
		c=c.Multiply(currentspeed*0.514444);
		
		//Add vectors
		Point2d p=new Point2d();
		p=w.Plus(c);

		rst.x=p.length()/0.514444;	//Speed in knots
		rst.y=p.getAngle();	//Direction
		rst.y=Geofunctions.cartesian2compass(rst.y);
		
		return rst;
	}
}
