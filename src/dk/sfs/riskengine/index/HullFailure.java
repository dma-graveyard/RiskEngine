package dk.sfs.riskengine.index;

import dk.sfs.riskengine.ais.RiskTarget;
import dk.sfs.riskengine.metoc.Metoc;

public class HullFailure extends IncidentType {

	
	public HullFailure(Metoc metoc,RiskTarget vessel) {
		super( metoc, vessel);
		
	}

	@Override
	public double getAgeFactorParam() {
		return 0.04;
	}



	
	@Override
	public AccidentType getAccidentType() {
	
		return AccidentType.HULLFAILURE;
	}

	
}
