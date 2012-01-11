package dk.sfs.riskengine.consequence;


import dk.sfs.riskengine.persistence.domain.Vessel.ShipTypeIwrap;
import dk.sfs.riskengine.statistics.Exponential;
import dk.sfs.riskengine.statistics.Uniform;
import dk.sfs.riskengine.consequence.Ship.ShipType;

public class FireExplosion {

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
	public FireExplosion() {
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
		hullDamage=Exponential.random(3.0);
		if (hullDamage>1.0) hullDamage=1.0;
		cargoDamage=hullDamage;
		if (hullDamage>0.6) {
			sinks=true;
			timeToSink=Uniform.random(1.0, 5.0);	//Uniformly between 1 and 5 hours
		}
	}
	
	
	private void estimateSpill(Ship ship1) {
		if (hullDamage>0.5) {
			double vol=0.33*ship1.bunkerTonnage*Uniform.random(0.0,1.0);	//Consider a lognormal or normal distribution
			fueltype1Spilled=vol*ship1.fuelType1Fraction;
			fueltype2Spilled=vol*ship1.fuelType2Fraction;
			if (ship1.shiptype==ShipTypeIwrap.CRUDE_OIL_TANKER || ship1.shiptype==ShipTypeIwrap.OIL_PRODUCTS_TANKER) {
				vol=0.33*Uniform.random(0.0,1.0);
				cargoSpilled=vol*ship1.cargoTonnage;
			}
		}
	}
	
	
	private double estimateLossOflife(Ship ship1) {
		livesLost=LossOfLives.inFire(ship1);
		return livesLost;
	}
	
	
	private double estimateMaterialCost(Ship ship1) {
		materialCost=ship1.valueOfShip * hullDamage;
		materialCost+=ship1.valueOfCargo * cargoDamage;
		return materialCost;
	}
}
