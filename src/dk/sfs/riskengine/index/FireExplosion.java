package dk.sfs.riskengine.index;

import dk.sfs.riskengine.ais.RiskTarget;
import dk.sfs.riskengine.consequence.Consequence;
import dk.sfs.riskengine.index.IncidentType.AccidentType;
import dk.sfs.riskengine.metoc.Metoc;

public class FireExplosion extends IncidentType {

	
	public FireExplosion(Metoc metoc,RiskTarget vessel) {
		super(metoc,vessel);
		
	}

	@Override
	public double getAgeFactorParam() {
		return 0.06;
	}



	
	
	@Override
	public AccidentType getAccidentType() {
	
		return AccidentType.FIRE_EXPLOSION;
	}

}
