package dk.sfs.riskengine.index;

import dk.sfs.riskengine.ais.RiskTarget;
import dk.sfs.riskengine.geometry.Geofunctions;
import dk.sfs.riskengine.geometry.Point2d;
import dk.sfs.riskengine.metoc.Metoc;

public class Collision extends IncidentType {

	
	private double tcpa;
	private double cpa;
	public Collision(Metoc metoc, RiskTarget own, double cpa, double tcpa, RiskTarget other) {
		super(metoc, own,other);
		this.cpa= cpa;
		this.tcpa = tcpa;
		
	}

	@Override
	public double getAgeFactorParam() {
		return 0.01;
	}

	
	

	/**
	 * Return a factor if current+wind is sideway and strong enough
	 * 
	 * @return
	 * 
	 */
	@Override
	public double getWindcurrentFactor() {

		Point2d resultant = estimateCombinedWindCurrentDrift();
		double anglediff = Math.abs(Geofunctions.angleDiff(vessel.getCog(), resultant.y));

		if (resultant.x > 3.0 && ((anglediff > 45 && anglediff < 135) || (anglediff > 225 && anglediff < 315))) {
			/*
			 * current+wind is sideway and strong enough
			 */
			return Math.exp(0.2 * (resultant.x - 3.0));
		}
		return 1.0;
	}

	
	public double getVisibilityFactor_() {
		return 0.9 + Math.exp(-metoc.getVisibility() * 0.0007) * 10l;
	}

	@Override
	public double getExposure() {
		double e1 = Math.exp(-Math.abs(cpa) * 1.0);
		double e2 = Math.exp(-Math.abs(tcpa) * 0.1);
		return e1 * e2;
	}

	@Override
	public AccidentType getAccidentType() {
	
		return AccidentType.COLLISION;
	}
	
	
}
