package dk.sfs.riskengine.consequence;

import dk.sfs.riskengine.persistence.domain.Vessel.ShipTypeIwrap;
import dk.sfs.riskengine.statistics.Exponential;
import dk.sfs.riskengine.statistics.Uniform;
import dk.sfs.riskengine.consequence.Ship.ShipType;


public class HullFailure {
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
	public HullFailure() {
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
		hullDamage=Exponential.random(3.5);
		cargoDamage=hullDamage;
		if (hullDamage>0.7) {
			sinks=true;
			timeToSink=Uniform.random(0.4, 2.0); //Need better estimate
		}
	}
	
	
	private void estimateSpill(Ship ship1) {
		if (hullDamage<0.1) {return;}
		
		fueltype1Spilled=ship1.bunkerTonnage*ship1.fuelType1Fraction*Uniform.random(0.0, 0.3);
		fueltype2Spilled=ship1.bunkerTonnage*ship1.fuelType2Fraction*Uniform.random(0.0, 0.3);
		
		if (hullDamage>=0.1 && hullDamage<0.5) cargoSpilled=ship1.cargoTonnage*Uniform.random(0.0, 0.3);
		if (hullDamage>=0.5) cargoSpilled=ship1.cargoTonnage*Uniform.random(0.3, 0.5);
		
		if (ship1.shiptype!=ShipTypeIwrap.CRUDE_OIL_TANKER && ship1.shiptype!=ShipTypeIwrap.OIL_PRODUCTS_TANKER)
			cargoSpilled=0.0;
	}
	
	
	//Hull failure only leads to loss of lives if the ship sinks. That scenario has been taken care of
	private double estimateLossOflife(Ship ship1) {
		livesLost=0.0;
		return livesLost;
	}
	
	
	private double estimateMaterialCost(Ship ship1) {
		materialCost=ship1.valueOfShip * hullDamage;
		materialCost+=ship1.valueOfCargo * cargoDamage;
		return materialCost;
	}
}
