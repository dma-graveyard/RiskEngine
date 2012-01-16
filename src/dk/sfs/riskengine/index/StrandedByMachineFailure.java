package dk.sfs.riskengine.index;

import dk.sfs.riskengine.ais.RiskTarget;
import dk.sfs.riskengine.geometry.Point2d;
import dk.sfs.riskengine.metoc.Metoc;
import dk.sfs.riskengine.statistics.Weibull;
import dk.sfs.riskengine.statistics.Weibull;

public class StrandedByMachineFailure extends IncidentType {

	public StrandedByMachineFailure(Metoc metoc, RiskTarget vessel) {
		super(metoc, vessel);

	}

	@Override
	public double getAgeFactorParam() {
		return 0.04;
	}

	@Override
	public double getWindcurrentFactor() {
		return 1.0;
	}

	public double getExposure() {
		Point2d drift = estimateCombinedWindCurrentDrift();
		double t = getTimeToGrounding(drift.x, drift.y);
		
		/*
		 * TODO check for possiblity to throw anchor.
		 */
		Boolean anchor = true;
		if(t>1800 && anchor){
			/*
			 * Only if possiblity to throw anchor !!
			 */
			return 0.0;
		}
		if (t >= 0) {
			// TODO Estimate the probability that the ship can drop its anchor
			double repairTime = Weibull.random(1.1, 5.35) * 3600.0; // Seconds
			if (t < repairTime)
				return 1.0;
			else
				return Math.exp(-Math.abs(t - repairTime) * 0.03);
		}
		return 0.0;
	}

	@Override
	public AccidentType getAccidentType() {

		return AccidentType.DRIFTGROUNDING;
	}

}
