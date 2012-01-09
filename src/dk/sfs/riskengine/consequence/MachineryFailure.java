package dk.sfs.riskengine.consequence;


public class MachineryFailure {

	public double penetration;		//
	public double damageWidth;		//
	public double xlocation;		//fraction of ship1's length
	public boolean sinks;			//Yes or no
	public double timeToSink;		//hours
	public double cargoSpilled;		//tons
	public double fueltype1Spilled;	//tons
	public double fueltype2Spilled;	//tons
	public double hullDamage;		//Percentage 0=none 1=all
	public double cargoDamage;		//Percentage of cargo damages	0=none, 1=all
	public double polutionCost;		//Million US dollar
	public double materialCost;		//Million US dollar
	public double livesLost;		//Number of people
	
	
	//Constructor
	public MachineryFailure() {
		penetration=0.0;
		damageWidth=0.0;
		xlocation=0.0;
		timeToSink=9999.9;
		sinks=false;
		hullDamage=0.0;
		cargoDamage=0.0;
		cargoSpilled=0.0;
		fueltype1Spilled=0.0;
		fueltype2Spilled=0.0;
		polutionCost=0.0;
		materialCost=0.0;
		livesLost=0.0;
	}
	
	//This has no consequences here. Drifting is handled elsewhere
	//There might be an increased probability of collision.
	//Perhaps in severe weather the ship can capsize if it gets machinery faillure
	public void calcConsequences(Ship ship1) {
		estimateDamage(ship1);
		estimateSpill(ship1);
		estimateLossOflife(ship1);
		estimateMaterialCost(ship1);
		
		ship1.numberOfPersons-=livesLost;
		ship1.bunkerTonnage-=(fueltype1Spilled+fueltype2Spilled);
		ship1.cargoTonnage-=cargoSpilled;
		if (ship1.numberOfPersons<0) ship1.numberOfPersons=0;
		if (ship1.bunkerTonnage<0.0) ship1.bunkerTonnage=0.0;
		if(ship1.cargoTonnage<0.0) ship1.cargoTonnage=0.0;
	}
	
	
	private void estimateDamage(Ship ship1) {
		return;
	}
	
	
	private void estimateSpill(Ship ship1) {
		return;
	}
	
	
	private double estimateLossOflife(Ship ship1) {
		livesLost=0.0;
		return livesLost;
	}
	
	
	private double estimateMaterialCost(Ship ship1) {
		materialCost=0.0;
		return materialCost;
	}
}
