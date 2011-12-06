package dk.sfs.riskengine;

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
	
	public Foundering(Metoc metoc, RiskTarget vessel) {
		super(metoc, vessel);
	
	}

	public double getWindcurrentFactor() {
		if (metoc.getWindSpeed() * 0.51444 > 7.0) {
			return Math.exp(0.2 * (metoc.getWindSpeed() * 0.51444 - 7.0));
		}
		return 1.0;
	}
	/* (non-Javadoc)
	 * @see dk.sfs.riskengine.IncidentProbability#getNumberOfIncidentPerMinut(int, double)
	 * 
	 */
	public double getNumberOfIncidentPerMinut(String shiptype, double shipsize){
		//TODO find stats for foundering for ship type and size.
		// 3 on 1 year
		return 3.0/(365.25*24l*60l);
	}
}
