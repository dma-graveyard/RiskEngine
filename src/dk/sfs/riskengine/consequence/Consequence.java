package dk.sfs.riskengine.consequence;

import dk.sfs.riskengine.index.IncidentType.AccidentType;
import dk.sfs.riskengine.persistence.domain.Vessel.ShipTypeIwrap;

public class Consequence {

	

	/**
	 * @param incident the accident type
	 * @param ship1  the damaged ship. 
	 * @param ship2 striking ship in case of collision 
	 * @return
	 */
	public static double getConsequence(AccidentType incident, Ship ship1,  double waveHeight , boolean softBottom, double timeFromRescue, double airTemperature, Ship ship2 ) {

		// Store original values because they are manipulated in the following
		// functions
		int persons0 = ship1.numberOfPersons;
		double cargoTonnage0 = ship1.cargoTonnage;
		double bunkerTonnage0 = ship1.bunkerTonnage;

		// Setup the different consequences
		Foundering foundering = new Foundering();
		Collision collision = new Collision();
		DriftGrounding driftGrnd = new DriftGrounding();
		PoweredGrounding poweredGrnd = new PoweredGrounding();
		FireExplosion fireExplosion = new FireExplosion();
		HullFailure hullFailure = new HullFailure();
		MachineryFailure machineryFailure = new MachineryFailure();

		switch (incident) {
		case COLLISION:
			collision.calcConsequences(ship1, ship2, waveHeight);
			break;
		case POWEREDGROUNDING:
			poweredGrnd.calcConsequences(ship1, waveHeight, softBottom);
			break;
		case DRIFTGROUNDING:
			driftGrnd.calcConsequences(ship1, waveHeight, softBottom);
			break;
		case FIRE_EXPLOSION:
			fireExplosion.calcConsequences(ship1);
			break;
		case HULLFAILURE:
			hullFailure.calcConsequences(ship1);
			break;
		case MACHINERYFAILURE:
			machineryFailure.calcConsequences(ship1);
			break;
		default:
			break;
		}

		boolean sinks = false;
		if (collision.sinks || driftGrnd.sinks || fireExplosion.sinks || hullFailure.sinks || poweredGrnd.sinks
				|| machineryFailure.sinks)
			sinks = true; // If its a collision or grounding or explosion and
							// the ship sinks then we suddenly have a foundering.

		if (incident == AccidentType.FOUNDERING || sinks)
			foundering.calcConsequences(ship1, timeFromRescue, airTemperature, waveHeight);

		double polutionCost = estimatePolutionCost(ship1, incident, foundering, collision, poweredGrnd, driftGrnd,
				fireExplosion, hullFailure, machineryFailure);
		double livesCost = estimateLossOfLife(foundering, collision, poweredGrnd, driftGrnd, fireExplosion,
				hullFailure, machineryFailure);
		double materialCost = estimateMaterialCost(foundering, collision, poweredGrnd, driftGrnd, fireExplosion,
				hullFailure, machineryFailure);
		
		double totalCost = polutionCost + livesCost + materialCost;

		// Restore original values. Important if the ship is used in another
		// calculation
		ship1.numberOfPersons = persons0;
		ship1.bunkerTonnage = bunkerTonnage0;
		ship1.cargoTonnage = cargoTonnage0;

		return totalCost;
	}
	
	
	//Estimates the maximum possible consequence
	public static double getMaxConsequence(Ship ship1) {
		
		double materialCost=ship1.valueOfShip+ship1.valueOfCargo;
		double lossOfLives=ship1.numberOfPersons*LossOfLives.costOfLife;
		
		//Spill costs
		double fuel1=ship1.fuelType1Fraction*ship1.bunkerTonnage;
		double fuel2=ship1.fuelType2Fraction*ship1.bunkerTonnage;
		double cargoOil=0.0;
		//Only oil tankers can polute in this model.
		if (ship1.shiptype == ShipTypeIwrap.CRUDE_OIL_TANKER || ship1.shiptype == ShipTypeIwrap.OIL_PRODUCTS_TANKER) {
			cargoOil=ship1.cargoTonnage;
		}
		double spillsize = fuel1 + fuel2 + cargoOil;
		double polutionCost = PolutionCost.totalEstimate(spillsize);
		
		double maxCost=materialCost + lossOfLives + polutionCost;
		return maxCost;
	}

	
	
	// Todo: Include weather. Does the oil evaporate or does it drift away from
	// shore?
	// Include different cost for different oil types
	public static double estimatePolutionCost(Ship ship1, AccidentType incident, Foundering foundering, Collision collision,
			PoweredGrounding poweredGrnd, DriftGrounding driftGrnd, FireExplosion fireExplosion,
			HullFailure hullFailure, MachineryFailure machineryFailure) {

		double fuel1 = 0.0;
		fuel1 += foundering.fueltype1Spilled;
		fuel1 += collision.fueltype1Spilled;
		fuel1 += poweredGrnd.fueltype1Spilled;
		fuel1 += driftGrnd.fueltype1Spilled;
		fuel1 += fireExplosion.fueltype1Spilled;
		fuel1 += hullFailure.fueltype1Spilled;
		fuel1 += machineryFailure.fueltype1Spilled;

		double fuel2 = 0.0;
		fuel2 += foundering.fueltype2Spilled;
		fuel2 += collision.fueltype2Spilled;
		fuel2 += poweredGrnd.fueltype2Spilled;
		fuel2 += driftGrnd.fueltype2Spilled;
		fuel2 += fireExplosion.fueltype2Spilled;
		fuel2 += hullFailure.fueltype2Spilled;
		fuel2 += machineryFailure.fueltype2Spilled;

		double cargoOil = 0.0; // ToDo include other types of pollution than oil
		if (ship1.shiptype == ShipTypeIwrap.CRUDE_OIL_TANKER || ship1.shiptype == ShipTypeIwrap.OIL_PRODUCTS_TANKER) {
			cargoOil += foundering.cargoSpilled;
			cargoOil += collision.cargoSpilled;
			cargoOil += poweredGrnd.cargoSpilled;
			cargoOil += driftGrnd.cargoSpilled;
			cargoOil += fireExplosion.cargoSpilled;
			cargoOil += hullFailure.cargoSpilled;
			cargoOil += machineryFailure.cargoSpilled;
		}

		double spillsize = fuel1 + fuel2 + cargoOil;
		double polutionCost = PolutionCost.totalEstimate(spillsize);
		return polutionCost;
	}

	public static double estimateLossOfLife(Foundering foundering, Collision collision, PoweredGrounding poweredGrnd,
			DriftGrounding driftGrnd, FireExplosion fireExplosion, HullFailure hullFailure,
			MachineryFailure machineryFailure) {
		double cost = 0.0; // mill. US dollar
		double nLives = 0.0;

		nLives += foundering.livesLost;
		nLives += collision.livesLost;
		nLives += poweredGrnd.livesLost;
		nLives += driftGrnd.livesLost;
		nLives += fireExplosion.livesLost;
		nLives += hullFailure.livesLost;
		nLives += machineryFailure.livesLost;

		cost = nLives * LossOfLives.costOfLife;
		return cost;
	}

	// This is the damage to the ship and the damage to the cargo
	public static double estimateMaterialCost(Foundering foundering, Collision collision, PoweredGrounding poweredGrnd,
			DriftGrounding driftGrnd, FireExplosion fireExplosion, HullFailure hullFailure,
			MachineryFailure machineryFailure) {
		double cost = 0.0; // mill. US dollar

		cost += foundering.materialCost;
		cost += collision.materialCost;
		cost += poweredGrnd.materialCost;
		cost += driftGrnd.materialCost;
		cost += fireExplosion.materialCost;
		cost += hullFailure.materialCost;
		cost += machineryFailure.materialCost;
		return cost;
	}

}
