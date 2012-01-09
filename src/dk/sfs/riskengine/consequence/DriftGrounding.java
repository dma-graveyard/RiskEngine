package dk.sfs.riskengine.consequence;

import dk.sfs.riskengine.persistence.domain.Vessel.ShipTypeIwrap;
import dk.sfs.riskengine.statistics.Exponential;
import dk.sfs.riskengine.statistics.Uniform;
import dk.sfs.riskengine.consequence.Ship.ShipType;


public class DriftGrounding {
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
	public DriftGrounding() {
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
	
	
	public void calcConsequences(Ship ship1, double waveHeight, boolean softBottom) {
		estimateSpill(ship1, waveHeight, softBottom);
		estimateLossOflife(ship1);
		estimateMaterialCost(ship1);
		
		ship1.numberOfPersons-=livesLost;
		ship1.bunkerTonnage-=(fueltype1Spilled+fueltype2Spilled);
		ship1.cargoTonnage-=cargoSpilled;
		if (ship1.numberOfPersons<0) ship1.numberOfPersons=0;
		if (ship1.bunkerTonnage<0.0) ship1.bunkerTonnage=0.0;
		if(ship1.cargoTonnage<0.0) ship1.cargoTonnage=0.0;
	}
	
	
	
	private void estimateSpill(Ship ship1, double waveHeight, boolean softBottom) {
		
		//Totally undocumented, but hopefully not totally wrong.
		//ToDo: Estimate if it sinks and the damage to cargo
		if (waveHeight<2 && Uniform.random(0.0, 1.0)>0.8) {
			double f=Uniform.random(0.0, 0.1);
			cargoSpilled=ship1.cargoTonnage*f;
			fueltype1Spilled=ship1.bunkerTonnage*ship1.fuelType1Fraction*f;
			fueltype2Spilled=ship1.bunkerTonnage*ship1.fuelType2Fraction*f;
			hullDamage=f;
			cargoDamage=f;
		}
		if (waveHeight>=2 && waveHeight<5 && Uniform.random(0.0, 1.0)>0.5) {
			double f=Uniform.random(0.0, 0.3);
			cargoSpilled=ship1.cargoTonnage*f;
			fueltype1Spilled=ship1.bunkerTonnage*ship1.fuelType1Fraction*f;
			fueltype2Spilled=ship1.bunkerTonnage*ship1.fuelType2Fraction*f;
			hullDamage=f;
			cargoDamage=f;
		}
		if (waveHeight>=5 && Uniform.random(0.0, 1.0)>0.2) {
			double f=Uniform.random(0.2, 0.7);
			cargoSpilled=ship1.cargoTonnage*f;
			fueltype1Spilled=ship1.bunkerTonnage*ship1.fuelType1Fraction*f;
			fueltype2Spilled=ship1.bunkerTonnage*ship1.fuelType2Fraction*f;
			hullDamage=f;
			cargoDamage=f;
		}
		
		if (softBottom) {
			double f=Exponential.random(500.0);	//ToDo: Find a better relation
			cargoSpilled*=f;
			fueltype1Spilled*=f;
			fueltype2Spilled*=f;
			hullDamage*=f;
			cargoDamage*=f;
		}
		
		if (ship1.shiptype!=ShipTypeIwrap.CRUDE_OIL_TANKER && ship1.shiptype!=ShipTypeIwrap.OIL_PRODUCTS_TANKER) {
			cargoSpilled=0.0;
		}
		
		if (hullDamage>0.5 && Uniform.random()>0.5) {
			sinks=true;
			timeToSink=Uniform.random(1.0,5.0);
		}
	}
	
	
	
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
