package dk.sfs.riskengine;

import dk.sfs.riskengine.ais.RiskTarget;
import dk.sfs.riskengine.geometry.Point2d;
import dk.sfs.riskengine.metoc.Metoc;
import dk.sfs.riskengine.statistics.weibull;

public class MachineryFailure extends IncidentType {

	
	public MachineryFailure(Metoc metoc, RiskTarget vessel) {
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

	public double getWindcurrentFactor() {
		if (metoc.getWindSpeed() * 0.51444 > 7.0) {
			return Math.exp(0.2 * (metoc.getWindSpeed() * 0.51444 - 7.0));
		}
		return 1.0;
	}
	
	
}
