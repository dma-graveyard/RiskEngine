package dk.sfs.riskengine.index;

import dk.sfs.riskengine.ais.RiskTarget;
import dk.sfs.riskengine.metoc.Metoc;

public class MachineryFailure extends IncidentType {

	
	public MachineryFailure(Metoc metoc, RiskTarget target) {
		super( metoc,target);
		
	}

	@Override
	public double getAgeFactorParam() {
		return 0.04;
	}

	

	
	@Override
	public AccidentType getAccidentType() {
	
		return AccidentType.MACHINERYFAILURE;
	}

	
}
