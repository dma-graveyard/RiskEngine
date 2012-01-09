package dk.sfs.riskengine.consequence;

import dk.sfs.riskengine.persistence.domain.Vessel.ShipTypeIwrap;
import dk.sfs.riskengine.statistics.Exponential;
import dk.sfs.riskengine.statistics.Uniform;
import dk.sfs.riskengine.statistics.Weibull;

import dk.sfs.riskengine.consequence.Ship.ShipType;


public class LossOfLives {
	public static double costOfLife=3.0;	//mill US. dollar

	//This estimate is only a dummy function. It captures the parameters
	//Have no idea as to the quality of the actual number
	//Consider a Bayesian approach
	//timeFromRescue in hours
	//airTemperatire in degrees Celcius
	//Waveheight in m
	public static double afterAbandonShip(Ship ship1, double timeFromRescue, double airTemperature, double waveHeight) {
		double nLives=0.0;	//Number of dead people
		
		double alfa=0.05*timeFromRescue;
		alfa*=Math.exp(323.15/(273.15+airTemperature)-1);
		alfa*=Math.exp(waveHeight/7.0);
		if (alfa<0.001) alfa=0.001;
		if (alfa>1.0) alfa=1.0;

		double beta=Math.exp(timeFromRescue/5.0);
		beta*=Math.exp(323.15/(273.15+airTemperature)-1);
		beta*=Math.exp(waveHeight/7.0);
		if (beta<1.0) beta=1.0;
		beta=10.0;
		
		nLives=Weibull.random(beta, alfa);	//Fraction of the total number on board
		if (nLives>1.0) nLives=1.0;
		nLives=ship1.numberOfPersons*nLives;
		if (nLives>ship1.numberOfPersons) nLives=ship1.numberOfPersons;
		
		return nLives;
	}

	
	//ToDo
	//If timeToSink is short people die before they reach the life rafts
	public static double inEvacuation(Ship ship1, double timeToSink) {
		double nLives=0.0;	//Number of dead people
		if (timeToSink<0.5) {
			nLives=Exponential.random(8.0)*ship1.numberOfPersons;
		}
		return nLives;
	}
	
	
	//Returns the number of people killed in the collision
	public static double inCollision(Ship ship1, Collision damage) {
		double nLives=0.0;
		if (damage.penetration<0.05) return nLives;
		
		if (ship1.shiptype==ShipTypeIwrap.PASSENGER_SHIP || ship1.shiptype==ShipTypeIwrap.FAST_FERRY || ship1.shiptype==ShipTypeIwrap.OTHER_SHIP) nLives=Exponential.random(0.5);
		
		if ((ship1.shiptype==ShipTypeIwrap.CRUDE_OIL_TANKER || ship1.shiptype==ShipTypeIwrap.OIL_PRODUCTS_TANKER 
			|| ship1.shiptype==ShipTypeIwrap.CHEMICAL_TANKER || ship1.shiptype==ShipTypeIwrap.GAS_TANKER 
			|| ship1.shiptype==ShipTypeIwrap.BULK_CARRIER || ship1.shiptype==ShipTypeIwrap.GENERAL_CARGO_SHIP
			|| ship1.shiptype==ShipTypeIwrap.CONTAINER_SHIP) && damage.xlocation<0.15) nLives=Exponential.random(0.5);

		if ((ship1.shiptype==ShipTypeIwrap.SUPPORT_SHIP || ship1.shiptype==ShipTypeIwrap.RO_RO_CARGO_SHIP) && damage.xlocation>0.85) nLives=Exponential.random(0.5);

		if ((ship1.shiptype==ShipTypeIwrap.PLEASURE_BOAT || ship1.shiptype==ShipTypeIwrap.FISHING_SHIP)) nLives=Exponential.random(0.3);
		
		if (nLives>ship1.numberOfPersons) nLives=ship1.numberOfPersons;
		return nLives;
	}
	
	
	public static double inFire(Ship ship1) {
		double nLives=0.0;
		
		if (ship1.shiptype==ShipTypeIwrap.CRUDE_OIL_TANKER || ship1.shiptype==ShipTypeIwrap.OIL_PRODUCTS_TANKER 
			|| ship1.shiptype==ShipTypeIwrap.CHEMICAL_TANKER || ship1.shiptype==ShipTypeIwrap.GAS_TANKER 
			|| ship1.shiptype==ShipTypeIwrap.BULK_CARRIER || ship1.shiptype==ShipTypeIwrap.GENERAL_CARGO_SHIP
			|| ship1.shiptype==ShipTypeIwrap.CONTAINER_SHIP || ship1.shiptype==ShipTypeIwrap.SUPPORT_SHIP 
			|| ship1.shiptype==ShipTypeIwrap.RO_RO_CARGO_SHIP || ship1.shiptype==ShipTypeIwrap.OTHER_SHIP) nLives=Exponential.random(8.0)*ship1.numberOfPersons;
		
		if ((ship1.shiptype==ShipTypeIwrap.PASSENGER_SHIP || ship1.shiptype==ShipTypeIwrap.FAST_FERRY) && Uniform.random()>0.7) nLives=Exponential.random(8.0)*ship1.numberOfPersons;
		
		if ((ship1.shiptype==ShipTypeIwrap.PLEASURE_BOAT || ship1.shiptype==ShipTypeIwrap.FISHING_SHIP)) nLives=Exponential.random(0.2);
		
		if (nLives>ship1.numberOfPersons) nLives=ship1.numberOfPersons;
		return nLives;
	}
	
}
