package dk.sfs.riskengine.consequence;

import dk.sfs.riskengine.persistence.domain.Vessel.ShipTypeIwrap;
import dk.sfs.riskengine.statistics.Uniform;


public class Collision {
	public double penetration;		//fraction of ship breadth
	public double damageWidth;		//fraction of ship length
	public double xlocation;		//fraction of ship1's length
	public boolean sinks;			//Yes or no
	public double timeToSink;		//hours
	public double cargoSpilled;		//tons
	public double fueltype1Spilled;	//tons
	public double fueltype2Spilled;	//tons
	public double hullDamage;		//Percentage 0.0=none 1.0=all
	public double cargoDamage;		//Percentage of cargo damaged	0.0=none, 1.0=all
	public double polutionCost;		//Million US dollar
	public double materialCost;		//Million US dollar
	public double livesLost;		//Number of people dead people
	
	//Constructor
	public Collision() {
		penetration=0.0;
		damageWidth=0.0;
		xlocation=0.0;
		timeToSink=9999.9;	//Important to use this value when nothing else has been assigned
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
	
	
	//A detailed method has been implemented in VB. Consider rewriting it in java
	//this includes the hit angle, energy of the ships, compartmentisation of the ship.
	//The below method is just to a get a value. Penetration increase if size or speed increases and visa versa
	//We assume the struck ship is box shaped and divided into a number of compartments
	public void calcConsequences(Ship ship1, Ship ship2, double waveHeight) {
		estimateDamage(ship1, ship2);
		doesTheShipSink(ship1, ship2, waveHeight);
		estimateSpill(ship1, waveHeight);
		estimateLossOflife(ship1);
		estimateMaterialCost(ship1);
		
		ship1.numberOfPersons-=livesLost;
		ship1.bunkerTonnage-=(fueltype1Spilled+fueltype2Spilled);
		ship1.cargoTonnage-=cargoSpilled;
		if (ship1.numberOfPersons<0) ship1.numberOfPersons=0;
		if (ship1.bunkerTonnage<0.0) ship1.bunkerTonnage=0.0;
		if(ship1.cargoTonnage<0.0) ship1.cargoTonnage=0.0;
	}
	
	
	private void estimateDamage(Ship ship1, Ship ship2) {
		penetration=0.0;
		damageWidth=0.0;
		hullDamage=0.0;
		if (ship1.calcDisplacement(true)<=0.0) return;
		
		double Ekin2=0.5*ship2.calcDisplacement(true)*1000.0*Math.pow(ship2.speed*0.5144,2)*0.000001; //Kinetic energy in MJ
		if (Ekin2<=0.1) {return;}
		
		double Edamage=Ekin2*0.4;	//Assume 40% of the energy is absorbed in the struck ship. 60% then goes into moving the ship and also into the bow of the striking ship
		double lambda=0.0007;
		penetration=1.0-Math.exp(-lambda*Edamage);	//Penetration as a fraction of the ship width
		//penetration*=Uniform.random(0.3,1.0);	//The collision happens at an angle. This reduces the penetration
		if (penetration<0.005) penetration=0.0;	//If the penetration is very small then ignore it
		
		damageWidth=ship2.breadth*0.67/ship1.loa;		//0.67=assume only 2/3 of the breadth penetrates. ToDo: Depends also on the penetration and hitting angle

		double damageHeight=1.0;                  //Height of the damaged ship.
		if (ship1.depth!=0.0) damageHeight=ship2.depth/ship1.depth;
		if (damageHeight >1.0) damageHeight =1.0;  
		hullDamage=penetration*damageWidth*damageHeight;  //hull damage is normalized with the size of the ship
		
		xlocation=Uniform.random(0.0, 1.0);	//Assume uniform probability for the location of the hit
	}
	
	
	//We have a better method. It just need to be converted to java.
	//For now use this
	private boolean doesTheShipSink(Ship ship1, Ship ship2, double waveHeight) {
		sinks=false;
		
		if (ship1.loa<20.0 && ship2.loa>50 && ship2.speed>8.0) {	//Small ships sinks when hit by a large ship
			sinks=true;
			timeToSink=Uniform.random(0.0, 0.5);	//Between 0 to half an hour
			return sinks;
		}
		
		if (penetration>0.7 || (penetration>0.4 && waveHeight>3.0)) {
			sinks=true;
			timeToSink=Uniform.random(0.3333, 1.0);	//Between 20 minutes and 1 hour
			return sinks;
		}
		return sinks;
	}
	
	
	private void estimateSpill(Ship ship1, double waveHeight) {
		double x1=xlocation-damageWidth/2.0;	//Lower x limit
		double x2=xlocation+damageWidth/2.0;	//Upper x limit

		//Bunker tank spill
		if (ship1.shiptype==ShipTypeIwrap.PASSENGER_SHIP || ship1.shiptype==ShipTypeIwrap.FAST_FERRY || ship1.shiptype==ShipTypeIwrap.RO_RO_CARGO_SHIP) {	//The bunker oil is located anywhere in the bottom
			if (penetration>0.05) {
				double vol=0.33*ship1.bunkerTonnage*Uniform.random(0.0,1.0);
				fueltype1Spilled=vol*ship1.fuelType1Fraction;
				fueltype2Spilled=vol*ship1.fuelType2Fraction;
			}
		}
		else
		{
			if (x1<0.15 && penetration>0.05) {	//Assume the fuel tanks are located in the aft of the ship
				double vol=0.33*ship1.bunkerTonnage*Uniform.random(0.0,1.0);
				fueltype1Spilled=vol*ship1.fuelType1Fraction;
				fueltype2Spilled=vol*ship1.fuelType2Fraction;
			}
		}
		
		//Cargo spill and cargo damage
		double vol=0.0;
		if (ship1.shiptype==ShipTypeIwrap.CRUDE_OIL_TANKER || ship1.shiptype==ShipTypeIwrap.OIL_PRODUCTS_TANKER ||
			ship1.shiptype==ShipTypeIwrap.CHEMICAL_TANKER || ship1.shiptype==ShipTypeIwrap.GAS_TANKER ||
			ship1.shiptype==ShipTypeIwrap.CONTAINER_SHIP || ship1.shiptype==ShipTypeIwrap.GENERAL_CARGO_SHIP) {	//Should be split into each type. Here we just generalise
			
			if (x2>=0.15 && x1<0.95 && penetration>0.05) {	//Assume that the cargo is located between 0.15 and 0.95*loa and there is a double side of 0.05*breadth
				vol=0.1*Uniform.random(0.2,0.95);	 //0.1 in each of the 10 cargo tanks
				if ((x1<0.31 && x2>0.31) || (x1<0.47 && x2>0.47) || (x1<0.63 && x2>0.63) || (x1<0.79 && x2>0.79)) vol*=2.0; //Two adjacent compartments damaged
				if (penetration>0.5) vol*=1.5;	//Tanks on the other side also penetrated. But more will probably stay there. That is why 1.5 and not 2.0
			}
			cargoDamage=hullDamage;	//Need to complicate this
			if (ship1.shiptype==ShipTypeIwrap.CRUDE_OIL_TANKER || ship1.shiptype==ShipTypeIwrap.OIL_PRODUCTS_TANKER) {
				cargoSpilled=vol*ship1.cargoTonnage;
				cargoDamage=vol;
			}
		}
		//ToDo: Damage to the other ships
		
		return;
	}
	
	
	private double estimateLossOflife(Ship ship1) {
		livesLost=LossOfLives.inCollision(ship1, this);
		return livesLost;
	}
	
	
	private double estimateMaterialCost(Ship ship1) {
		materialCost=ship1.valueOfShip * hullDamage;
		materialCost+=ship1.valueOfCargo * cargoDamage;
		return materialCost;
	}
}
