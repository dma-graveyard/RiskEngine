package dk.sfs.riskengine.index;

import dk.sfs.riskengine.ais.RiskTarget;
import dk.sfs.riskengine.metoc.Metoc;

public class StrandedByNavigationError extends IncidentType {

	
	
	public StrandedByNavigationError(Metoc metoc,RiskTarget vessel) {
		super(metoc,vessel);
		
	}

	@Override
	public double getAgeFactorParam() {
		return 0.04;
	}

	

	@Override
	public double getExposure() {
		
		double t=getTimeToGrounding( vessel.getCog(), vessel.getSog());
	
		if (t<3600) {
			return 	Math.exp(-Math.abs(t/60.0)*0.1);	//Using the same as tcpa
		}
		return 0;
	}

	
	public double getVisibilityFactor_() {
		return 0.9+Math.exp(-metoc.getVisibility()*0.0007)*10l;
	}

	@Override
	public AccidentType getAccidentType() {
		return AccidentType.POWEREDGROUNDING;
	}

	@Override
	public double getWindcurrentFactor() {		
		return 1.0;
	}

}
