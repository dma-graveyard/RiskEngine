package dk.sfs.riskengine;

import dk.sfs.riskengine.ais.RiskTarget;
import dk.sfs.riskengine.geometry.Point2d;
import dk.sfs.riskengine.metoc.Metoc;

public class StrandedByNavigationError extends IncidentType {

	private static final int TIME_TO_REPAIR = 30*60; // 30 min.
	
	public StrandedByNavigationError(Metoc metoc, RiskTarget vessel) {
		super(metoc, vessel);
		
	}

	@Override
	public double getAgeFactorParam() {
		return 0.04;
	}

	@Override
	public double getNumberOfIncidentPerMinut(String shiptype, double shipsize) {
		
		return 0.005;
	}

	@Override
	public double getExposure() {
		
		double t=getTimeToGrounding( vessel.getCog(), vessel.getSog());
	
		if (t<3600) {
			return 	Math.exp(-Math.abs(t/60.0)*0.1);	//Using the same as tcpa
		}
		return 0;
	}

	@Override
	public double getVisibilityFactor() {
		return 0.9+Math.exp(-metoc.getVisibility()*0.0007)*10l;
	}

	
}
