package dk.sfs.riskengine;

import dk.sfs.riskengine.ais.RiskTarget;
import dk.sfs.riskengine.geometry.CPA;
import dk.sfs.riskengine.geometry.Geofunctions;
import dk.sfs.riskengine.geometry.Point2d;
import dk.sfs.riskengine.metoc.Metoc;

public class Collision extends IncidentType {

	public Collision(Metoc metoc, RiskTarget vessel) {
		super(metoc, vessel);
	}

	@Override
	public double getAgeFactorParam() {
		return 0.01;
	}

	@Override
	public double getNumberOfIncidentPerMinut(String shiptype, double shipsize) {
		return 3.0 / (365.25 * 24l * 60l);
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

	@Override
	public double getVisibilityFactor() {
		return 0.9 + Math.exp(-metoc.getVisibility() * 0.0007) * 10l;
	}

	@Override
	public double getExposure() {
		//TODO TODO
		double cpa = CPA.cpa_distance(null, null, null, null);
		double tcpa = CPA.cpa_time(null, null, null, null);
		double e1 = Math.exp(-Math.abs(cpa) * 1.0);
		double e2 = Math.exp(-Math.abs(tcpa) * 0.1);
		return e1 * e2;
	}
}
