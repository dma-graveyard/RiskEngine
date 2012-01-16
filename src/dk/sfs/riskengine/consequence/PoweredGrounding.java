package dk.sfs.riskengine.consequence;

import dk.sfs.riskengine.statistics.Exponential;
import dk.sfs.riskengine.statistics.Uniform;


public class PoweredGrounding {

	public double penetration;		//Fraction of the draught.
	public double damageWidth;		//
	public double damageLength;		//Fraction of ship length
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
	public PoweredGrounding() {
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
		estimateDamage(ship1, waveHeight, softBottom);
		estimateSpill(ship1, waveHeight);
		estimateLossOflife(ship1);
		estimateMaterialCost(ship1);
		
		ship1.numberOfPersons-=livesLost;
		ship1.bunkerTonnage-=(fueltype1Spilled+fueltype2Spilled);
		ship1.cargoTonnage-=cargoSpilled;
		if (ship1.numberOfPersons<0) ship1.numberOfPersons=0;
		if (ship1.bunkerTonnage<0.0) ship1.bunkerTonnage=0.0;
		if(ship1.cargoTonnage<0.0) ship1.cargoTonnage=0.0;
		
		materialCost+=500.0*ship1.loa*(1e-6);	//mill. $. A grounding allways cost something eventhough no damage occurs. Inspection+waiting time;
	}
	
	
	public void estimateDamage(Ship ship1, double waveHeight, boolean softBottom) {
		penetration=0.0;
		damageLength=0.0;
		sinks=false;
		if (ship1.speed<2.0) return;
			
		damageLength=0.2*Math.log(0.3*ship1.speed);	//fraction of the ship length. We will need a better estimate
		damageLength*=Uniform.random(0.5, 1.5);
		if (softBottom) damageLength*=Exponential.random(5.0);	//Reduce the damage length if soft bottom
		
		double lambda=-20/ship1.speed;
		penetration=Math.exp(lambda);	//Penetration as fraction of draught. Should be height
		penetration*=Uniform.random(0.5, 1.5);
		if (softBottom) {
			double f=Exponential.random(5.0);
			if (f>1.0) f=1.0;
			penetration*=f;
		}
		
		if (damageLength>0.35 && penetration>0.5 && Uniform.random(0.0, 1.0)>0.5) {
			sinks=true;
			timeToSink=Uniform.random(0.5, 5.0);	//Need better estimate
		}
		
		estimateSpill(ship1, waveHeight);
		return;
	}
	
	
	private void estimateSpill(Ship ship1, double waveHeight) {

		//We assume that the ship is at level trim. Therefore the bunker tanks are not penetrated.
		//Actually large ships also have bunker tanks mid ship. But we forget that here 
		if (damageLength<0.05 || penetration<0.15)	//The first 5% of the ship does not have cargo. Double bottom assumed at 0.15*Draught
			cargoSpilled=0.0;
		else
		{	
			cargoSpilled=(damageLength-0.05)*ship1.cargoTonnage;
			if (waveHeight<2 && Uniform.random(0.0, 1.0)>0.5) cargoSpilled*=Uniform.random(0.3, 0.7);	//>0.5 attempts to model the double bottom
			if (waveHeight>=2 && waveHeight<5 && Uniform.random(0.0, 1.0)>0.3) cargoSpilled*=Uniform.random(0.5, 0.9);	//>0.3 attempts to model the double bottom
			if (waveHeight>=5 && Uniform.random(0.0, 1.0)>0.05) cargoSpilled*=Uniform.random(0.8, 1.0);
		}
		
		//The bunker tanks might also be hit
		if (penetration>0.05 && Uniform.random(0.0, 1.0)>0.5) {
			fueltype1Spilled=Uniform.random(0.0, 0.3)*ship1.fuelType1Fraction*ship1.bunkerTonnage;
			fueltype2Spilled=Uniform.random(0.0, 0.3)*ship1.fuelType2Fraction*ship1.bunkerTonnage;
		}
		
		return;
	}
	
	
	//Unless the ship sinks following a hull failure no lives are lost
	private double estimateLossOflife(Ship ship1) {
		livesLost=0.0;
		return livesLost;
	}
	
	
	private double estimateMaterialCost(Ship ship1) {
		materialCost=0.2*damageLength*ship1.valueOfShip;	//Todo: get some infomation on this
		materialCost+=0.2*damageLength*ship1.valueOfCargo;
		return materialCost;
	}
	
}
