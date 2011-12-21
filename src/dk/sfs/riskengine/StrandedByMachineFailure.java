package dk.sfs.riskengine;

import dk.sfs.riskengine.IncidentType.AccidentType;
import dk.sfs.riskengine.ais.RiskTarget;
import dk.sfs.riskengine.geometry.Point2d;
import dk.sfs.riskengine.metoc.Metoc;
import dk.sfs.riskengine.statistics.weibull;

public class StrandedByMachineFailure extends IncidentType {

	
	public StrandedByMachineFailure(Metoc metoc,RiskTarget vessel) {
		super( metoc,vessel);
		
	}

	@Override
	public double getAgeFactorParam() {
		return 0.04;
	}

	

	@Override
	public double getExposure() {
		Point2d drift= estimateCombinedWindCurrentDrift();
		double t=getTimeToGrounding( drift.x, drift.y);
	
		if (t>0) {
			//TODO Estimate the probability that the ship can drop its anchor
			double repairTime=weibull.random(1.1, 5.35, Math.random())*3600.0;	//Seconds
			if (t<repairTime)
				return 0.0;
			else
				return Math.exp(-Math.abs(t/60.0)*0.05);
		}
		return 0;
	}

	@Override
	public AccidentType getAccidentType() {
	
		return AccidentType.DRIFTGROUNDING;
	}

}
