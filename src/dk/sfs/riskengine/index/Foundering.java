package dk.sfs.riskengine.index;

import dk.sfs.riskengine.ais.RiskTarget;
import dk.sfs.riskengine.metoc.Metoc;

public class Foundering extends IncidentType {

	
	@Override
	public double getAgeFactorParam() {
		return 0.105;
	}

	public double getFlagFactor(String flag) {
		return 1.0;
	}
	
	public Foundering(Metoc metoc,RiskTarget vessel) {
		super( metoc,vessel);
	
	}

	public double getWindcurrentFactor() {
		if (metoc.getWindSpeed() > 7.0) {
			return Math.exp(0.2 * (metoc.getWindSpeed()  - 7.0));
		}
		return 1.0;
	}
	
	
	@Override
	public AccidentType getAccidentType() {
	
		return AccidentType.FOUNDERING;
	}

}
