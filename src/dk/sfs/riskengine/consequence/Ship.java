package dk.sfs.riskengine.consequence;

import dk.sfs.riskengine.persistence.domain.Vessel.ShipTypeIwrap;
import dk.sfs.riskengine.statistics.Uniform;

public class Ship {
	public enum ShipType {
		CrudeOilTanker, OilProductsTanker, ChemTanker, GasTanker, Bulkcarrier, ContainerShip, GenCargoShip, RoRoCargo, PassengerShip, FastFerry, SupportShip, FishingShip, PleasureBoat, OtherShip
	}

	public enum PolutionType {
		MGO, MDO, IFO, HFO, Crude, Chemicals, Ore, Gas, Containers, Vehicles, None
	}

	public ShipTypeIwrap shiptype;
	public double loa; // length overall m
	public double breadth; // m
	public double designDraught; // m
	public Double draught; // m
	public double Cb; // block coefficient
	public double deadweight; // t
	public double lightweight; // t
	public double grossTonnage; // t

	public int numberOfPersons; // Total number of persons onboard

	public double designSpeed; // knots
	public double speed; // knots
	public double depth;                //The depth of the ship in meters
	public double age; // years
	public double valueOfShip; // million US dollar
	public double valueOfCargo; // million US dollar

	public double loaded; // 1.0=full, 0.0=empty could also be somewhere in
							// between
	PolutionType fuelType1; // The main fuel type when the ships sails away from
							// shore
	PolutionType fuelType2; // A better fuel type that the ship switches to
							// close to shore.
	PolutionType cargoType; // Here we categorise the cargo as to its polution.
							// Their might be better ways.

	double cargoTonnage; // tons. When fully loaded this is the deadweight
	double bunkerTonnage; // tons
	double fuelType1Fraction;
	double fuelType2Fraction;

	public Ship() {
		shiptype = ShipTypeIwrap.OTHER_SHIP;
		loa = 0.0;
		breadth =0.0;
		Cb = 0.0;
		designDraught = 0.0;	
		draught = null;	
		deadweight = 0.0;	
		loaded = 0.0;	
		valueOfShip = 0.0;	
		valueOfCargo = 0.0;	
		bunkerTonnage = 0.0;
		cargoTonnage = 0.0;
		fuelType1 = PolutionType.None;
		cargoType = PolutionType.None;
		numberOfPersons = 0;
		depth=0.0;
	}

	public void EstimateShipParameters(boolean overWriteCurrentValues, boolean includeStocastic) {
		if (loa == 0.0)
			return;

		if (overWriteCurrentValues) {
			if (breadth == 0.0)
				estimateBreadth();
			estimateBlockCoef();
			estimateDesignDraught();
			draught = designDraught;
			estimateDeadweight();
			estimateLoadFactor();
			estimateShipValue(includeStocastic);
			estimateCargoValue(includeStocastic);
			estimateBunkerSize(includeStocastic);
			estimateCargoSize();
			estimateFueltypes();
			estimateCargoType();
			estimateNumberOfPersons();
			estimateDepth();
			
		} else // Overwrite only if value is missing
		{
			if (breadth == 0.0)
				estimateBreadth();
			if (Cb == 0.0)
				estimateBlockCoef();
			if (designDraught == 0.0)
				estimateDesignDraught();
			if (draught == null)
				draught = designDraught;
			if (deadweight == 0.0)
				estimateDeadweight();
			if (loaded == 0.0)
				estimateLoadFactor();
			if (valueOfShip == 0.0)
				estimateShipValue(includeStocastic);
			if (valueOfCargo == 0.0)
				estimateCargoValue(includeStocastic);
			if (bunkerTonnage == 0.0)
				estimateBunkerSize(includeStocastic);
			if (cargoTonnage == 0.0)
				estimateCargoSize();
			if (fuelType1 == PolutionType.None)
				estimateFueltypes();
			if (cargoType == PolutionType.None)
				estimateCargoType();
			if (numberOfPersons == 0)
				estimateNumberOfPersons();
			if (depth==0.0) estimateDepth();
		}

		return;
	}

	public double estimateBreadth() {

		if (loa == 0.0) {
			breadth = 0.0;
			return breadth;
		}
		if (shiptype == ShipTypeIwrap.CRUDE_OIL_TANKER)
			breadth = loa / 5.9;
		if (shiptype == ShipTypeIwrap.OIL_PRODUCTS_TANKER)
			breadth = loa / 6.2;
		if (shiptype == ShipTypeIwrap.CHEMICAL_TANKER)
			breadth = loa / 6.7;
		if (shiptype == ShipTypeIwrap.GAS_TANKER)
			breadth = loa / 6.2;
		if (shiptype == ShipTypeIwrap.CONTAINER_SHIP)
			breadth = loa / 6.9;
		if (shiptype == ShipTypeIwrap.GENERAL_CARGO_SHIP)
			breadth = loa / 6.3;
		if (shiptype == ShipTypeIwrap.BULK_CARRIER)
			breadth = loa / 6.4;
		if (shiptype == ShipTypeIwrap.RO_RO_CARGO_SHIP)
			breadth = loa / 6.3;
		if (shiptype == ShipTypeIwrap.PASSENGER_SHIP)
			breadth = loa / 5.1;
		if (shiptype == ShipTypeIwrap.FAST_FERRY)
			breadth = loa / 4.5; // Difficult as they are often catamarans.
		if (shiptype == ShipTypeIwrap.SUPPORT_SHIP)
			breadth = loa / 5.0;
		if (shiptype == ShipTypeIwrap.FISHING_SHIP)
			breadth = loa / 4.5;
		if (shiptype == ShipTypeIwrap.PLEASURE_BOAT)
			breadth = loa / 5.1;
		if (shiptype == ShipTypeIwrap.OTHER_SHIP)
			breadth = loa / 3.8;
		return breadth;
	}

	public double estimateBlockCoef() {
		double block = 0.75;
		if (shiptype == ShipTypeIwrap.CRUDE_OIL_TANKER)
			block = 0.79;
		if (shiptype == ShipTypeIwrap.OIL_PRODUCTS_TANKER)
			block = 0.75;
		if (shiptype == ShipTypeIwrap.CHEMICAL_TANKER)
			block = 0.72;
		if (shiptype == ShipTypeIwrap.GAS_TANKER)
			block = 0.71;
		if (shiptype == ShipTypeIwrap.CONTAINER_SHIP)
			block = 0.62;
		if (shiptype == ShipTypeIwrap.GENERAL_CARGO_SHIP)
			block = 0.71;
		if (shiptype == ShipTypeIwrap.BULK_CARRIER)
			block = 0.78;
		if (shiptype == ShipTypeIwrap.RO_RO_CARGO_SHIP)
			block = 0.58;
		if (shiptype == ShipTypeIwrap.PASSENGER_SHIP)
			block = 0.48;
		if (shiptype == ShipTypeIwrap.FAST_FERRY)
			block = 0.25; // Difficult as they are often catamarans.
		if (shiptype == ShipTypeIwrap.SUPPORT_SHIP)
			block = 0.68;
		if (shiptype == ShipTypeIwrap.FISHING_SHIP)
			block = 0.51;
		if (shiptype == ShipTypeIwrap.PLEASURE_BOAT)
			block = 0.39;
		if (shiptype == ShipTypeIwrap.OTHER_SHIP)
			block = 0.72;
		Cb = block;
		return block;
	}

	public double estimateDesignDraught() {
		double a = 0.0, b = 0.0;
		if (shiptype == ShipTypeIwrap.CRUDE_OIL_TANKER) {
			a = 0.0617;
			b = 1.0353;
		}
		if (shiptype == ShipTypeIwrap.OIL_PRODUCTS_TANKER) {
			a = 0.0584;
			b = 1.5377;
		}
		if (shiptype == ShipTypeIwrap.CHEMICAL_TANKER) {
			a = 0.054;
			b = 1.3559;
		}
		if (shiptype == ShipTypeIwrap.GAS_TANKER) {
			a = 0.044;
			b = 2.3677;
		}
		if (shiptype == ShipTypeIwrap.CONTAINER_SHIP) {
			a = 0.0373;
			b = 3.1098;
		}
		if (shiptype == ShipTypeIwrap.GENERAL_CARGO_SHIP) {
			a = 0.0529;
			b = 1.4747;
		}
		if (shiptype == ShipTypeIwrap.BULK_CARRIER) {
			a = 0.0589;
			b = 1.4006;
		}
		if (shiptype == ShipTypeIwrap.RO_RO_CARGO_SHIP) {
			a = 0.0436;
			b = 0.8762;
		}
		if (shiptype == ShipTypeIwrap.PASSENGER_SHIP) {
			a = 0.0252;
			b = 1.6237;
		}
		if (shiptype == ShipTypeIwrap.FAST_FERRY) {
			a = 0.0276;
			b = 0.7533;
		} // Difficult as they are often catamarans.
		if (shiptype == ShipTypeIwrap.SUPPORT_SHIP) {
			a = 0.0644;
			b = 0.6313;
		}
		if (shiptype == ShipTypeIwrap.FISHING_SHIP) {
			a = 0.0351;
			b = 2.7625;
		}
		if (shiptype == ShipTypeIwrap.PLEASURE_BOAT) {
			a = 0.0434;
			b = 0.965;
		}
		if (shiptype == ShipTypeIwrap.OTHER_SHIP) {
			a = 0.0253;
			b = 1.8557;
		}

		designDraught = a * loa + b;
		return designDraught;
	}

	public double estimateDeadweight() {
		double a = 0.0, b = 0.0;
		if (shiptype == ShipTypeIwrap.CRUDE_OIL_TANKER) {
			a = 1621.4;
			b = 0.0165;
		}
		if (shiptype == ShipTypeIwrap.OIL_PRODUCTS_TANKER) {
			a = 1621.4;
			b = 0.0165;
		}
		if (shiptype == ShipTypeIwrap.CHEMICAL_TANKER) {
			a = 341.2;
			b = 0.0262;
		}
		if (shiptype == ShipTypeIwrap.GAS_TANKER) {
			a = 1481.2;
			b = 0.0144;
		}
		if (shiptype == ShipTypeIwrap.CONTAINER_SHIP) {
			a = 2087.2;
			b = 0.0123;
		}
		if (shiptype == ShipTypeIwrap.GENERAL_CARGO_SHIP) {
			a = 488.5;
			b = 0.023;
		}
		if (shiptype == ShipTypeIwrap.BULK_CARRIER) {
			a = 2040.7;
			b = 0.0156;
		}
		if (shiptype == ShipTypeIwrap.RO_RO_CARGO_SHIP) {
			a = 709.1;
			b = 0.0158;
		}
		if (shiptype == ShipTypeIwrap.PASSENGER_SHIP) {
			a = 40.6;
			b = -2386.7;
		} // Linear
		if (shiptype == ShipTypeIwrap.FAST_FERRY) {
			a = 12.567;
			b = 0.0349;
		}
		if (shiptype == ShipTypeIwrap.SUPPORT_SHIP) {
			a = 71.4;
			b = -2683.0;
		} // Linear
		if (shiptype == ShipTypeIwrap.FISHING_SHIP) {
			a = 211.5;
			b = 0.0247;
		}
		if (shiptype == ShipTypeIwrap.PLEASURE_BOAT) {
		}
		; // Not really applicable.
		if (shiptype == ShipTypeIwrap.OTHER_SHIP) {
			a = 71.4;
			b = -2683.0;
		} // Linear

		if (shiptype == ShipTypeIwrap.PASSENGER_SHIP || shiptype == ShipTypeIwrap.SUPPORT_SHIP
				|| shiptype == ShipTypeIwrap.OTHER_SHIP)
			deadweight = a * loa + b;
		else
			deadweight = a * Math.exp(b * loa);
		return deadweight;
	}

	// ToDo: Consider using the lightweight to estimate the loadFactor;
	public double estimateLoadFactor() {
		loaded = 1.0;
		if (designDraught > 0.0 && draught != null) {
			if (draught / designDraught < 0.8)
				loaded = 0.0; // Applies to bulk/tanker ships
		}
		
		if (shiptype == ShipTypeIwrap.CONTAINER_SHIP)
			loaded = Uniform.random(0.4, 1.2);
		if (shiptype == ShipTypeIwrap.GENERAL_CARGO_SHIP)
			loaded = Uniform.random(0.4, 1.2);
		if (shiptype == ShipTypeIwrap.RO_RO_CARGO_SHIP)
			loaded = Uniform.random(0.4, 1.2);
		if (shiptype == ShipTypeIwrap.PASSENGER_SHIP)
			loaded = Uniform.random(0.4, 1.2);
		if (shiptype == ShipTypeIwrap.FAST_FERRY)
			loaded = Uniform.random(0.4, 1.2);
		if (shiptype == ShipTypeIwrap.SUPPORT_SHIP)
			loaded = Uniform.random(0.4, 1.2);
		if (shiptype == ShipTypeIwrap.FISHING_SHIP)
			loaded = Uniform.random(0.4, 1.2);
		if (shiptype == ShipTypeIwrap.PLEASURE_BOAT)
			loaded = Uniform.random(0.4, 1.2);
		if (shiptype == ShipTypeIwrap.OTHER_SHIP)
			loaded = Uniform.random(0.4, 1.2);
		if (loaded > 1.0)
			loaded = 1.0;
		return loaded;
	}

	// Crude estimate of the newbuilding price in dollar. Ignores the time of
	// build
	public double estimateShipValue(boolean includeStocastic) {
		double newBuildingCost = 0.0; // million US dollar
		double l = loa;
		if (shiptype == ShipTypeIwrap.CRUDE_OIL_TANKER || shiptype == ShipTypeIwrap.OIL_PRODUCTS_TANKER) {
			if (loa > 400)
				l = 400;
			newBuildingCost = 6.77 * Math.exp(0.008 * l);
		}

		if (shiptype == ShipTypeIwrap.CHEMICAL_TANKER) {
			if (loa > 250)
				l = 250;
			newBuildingCost = 3.48 * Math.exp(0.013 * l);
		}

		if (shiptype == ShipTypeIwrap.GAS_TANKER) {
			if (loa > 350)
				l = 350;
			newBuildingCost = 5.89 * Math.exp(0.012 * l);
		}

		if (shiptype == ShipTypeIwrap.BULK_CARRIER) {
			if (loa > 350)
				l = 350;
			newBuildingCost = 3.33 * Math.exp(0.010 * l);
		}

		if (shiptype == ShipTypeIwrap.CONTAINER_SHIP) {
			if (loa > 375)
				l = 375;
			newBuildingCost = 4.59 * Math.exp(0.010 * l);
		}

		if (shiptype == ShipTypeIwrap.GENERAL_CARGO_SHIP) {
			if (loa > 225)
				l = 225;
			newBuildingCost = 1.09 * Math.exp(0.017 * l);
		}

		if (shiptype == ShipTypeIwrap.RO_RO_CARGO_SHIP) {
			if (loa > 275)
				l = 275;
			newBuildingCost = 1.5871 * Math.exp(0.016 * l);
		}

		if (shiptype == ShipTypeIwrap.PASSENGER_SHIP) {
			if (loa > 350)
				l = 350;
			newBuildingCost = 3.31 * Math.exp(0.017 * l);
		}

		if (shiptype == ShipTypeIwrap.FAST_FERRY) {
			if (loa > 160)
				l = 160;
			newBuildingCost = 2.99 * Math.exp(0.024 * l);
		}

		if (shiptype == ShipTypeIwrap.SUPPORT_SHIP) {
			newBuildingCost = 0.5 * loa; // Not documented. Just a quick
											// estimate
		}

		if (shiptype == ShipTypeIwrap.FISHING_SHIP) {
			newBuildingCost = 0.05 * loa; // Not documented. Just a quick
											// estimate
		}

		if (shiptype == ShipTypeIwrap.PLEASURE_BOAT) {
			newBuildingCost = 0.04 * loa; // Not documented. Just a quick
											// estimate
		}

		if (shiptype == ShipTypeIwrap.OTHER_SHIP) {
			newBuildingCost = 0.5 * loa; // Not documented. Just a quick
											// estimate
		}

		if (includeStocastic)
			newBuildingCost *= Uniform.random(0.8, 1.2);

		if (age < 20.0)
			// assume the ship depreciates linearly over 20 years
			valueOfShip = newBuildingCost * (20 - age) / 20.0;
		else
			valueOfShip = newBuildingCost / 20.0;

		return valueOfShip;

	}

	// A VERY crude estimate is that the value of the cargo is equal to the cost
	// of the ship multiplied by a factor.
	// loaded = the fraction of the ship that is loaded. 1=full, 0=empty
	// ToDo: Use the deadweight and the unit value of the cargo. Unit cost for
	// ore and oil can be found. But of course the vary a lot
	public double estimateCargoValue(boolean includeStocastic) {
		double value = 0.0;
		double c = 0.5;
		if (shiptype == ShipTypeIwrap.CRUDE_OIL_TANKER)
			c = 0.3;
		if (shiptype == ShipTypeIwrap.OIL_PRODUCTS_TANKER)
			c = 0.4;
		if (shiptype == ShipTypeIwrap.CHEMICAL_TANKER)
			c = 0.5;
		if (shiptype == ShipTypeIwrap.GAS_TANKER)
			c = 0.3;
		if (shiptype == ShipTypeIwrap.CONTAINER_SHIP)
			c = 0.6;
		if (shiptype == ShipTypeIwrap.GENERAL_CARGO_SHIP)
			c = 0.4;
		if (shiptype == ShipTypeIwrap.BULK_CARRIER)
			c = 0.3;
		if (shiptype == ShipTypeIwrap.RO_RO_CARGO_SHIP)
			c = 0.5;
		if (shiptype == ShipTypeIwrap.PASSENGER_SHIP)
			c = 0.2; // Vehicles+luggage
		if (shiptype == ShipTypeIwrap.FAST_FERRY)
			c = 0.2; // Vehicles+luggage
		if (shiptype == ShipTypeIwrap.SUPPORT_SHIP)
			c = 0.05;
		if (shiptype == ShipTypeIwrap.FISHING_SHIP)
			c = 0.1;
		if (shiptype == ShipTypeIwrap.PLEASURE_BOAT)
			c = 0.05;
		if (shiptype == ShipTypeIwrap.OTHER_SHIP)
			c = 0.1;

		value = estimateNewBuildingPrice(includeStocastic);
		value *= loaded;
		valueOfCargo = value * c;
		return value;
	}

	public double estimateCargoSize() {
		cargoTonnage = deadweight * loaded;
		return cargoTonnage;
	}

	public double estimateBunkerSize(boolean includeStocastic) {
		double size; // In tons [t]
		double a = 25.42, b = 0.025;

		if (shiptype == ShipTypeIwrap.CRUDE_OIL_TANKER) {
			a = 147.03;
			b = 0.012;
		}
		if (shiptype == ShipTypeIwrap.OIL_PRODUCTS_TANKER) {
			a = 31.95;
			b = 0.0208;
		}
		if (shiptype == ShipTypeIwrap.CHEMICAL_TANKER) {
			a = 11.53;
			b = 0.0309;
		}
		if (shiptype == ShipTypeIwrap.GAS_TANKER) {
			a = 97.29;
			b = 0.015;
		}
		if (shiptype == ShipTypeIwrap.CONTAINER_SHIP) {
			a = 149.8;
			b = 0.0137;
		}
		if (shiptype == ShipTypeIwrap.GENERAL_CARGO_SHIP) {
			a = 8.155;
			b = 0.0346;
		}
		if (shiptype == ShipTypeIwrap.BULK_CARRIER) {
			a = 107.9;
			b = 0.014;
		}
		if (shiptype == ShipTypeIwrap.RO_RO_CARGO_SHIP) {
			a = 52.485;
			b = 0.0196;
		}
		if (shiptype == ShipTypeIwrap.PASSENGER_SHIP && loa <= 250.0) {
			a = 9.55;
			b = 0.025;
		}
		if (shiptype == ShipTypeIwrap.PASSENGER_SHIP && loa > 250.0) {
			a = 431.3;
			b = 0.0068;
		}
		if (shiptype == ShipTypeIwrap.FAST_FERRY) {
			a = 2.8884;
			b = 0.0317;
		}
		if (shiptype == ShipTypeIwrap.SUPPORT_SHIP) {
			a = 18.72;
			b = 0.0465;
		}
		if (shiptype == ShipTypeIwrap.FISHING_SHIP) {
			a = 10.29;
			b = 0.0488;
		}
		if (shiptype == ShipTypeIwrap.PLEASURE_BOAT) {
			a = 49.7;
			b = 0.0253;
		}
		if (shiptype == ShipTypeIwrap.OTHER_SHIP) {
			a = 25.42;
			b = 0.025;
		}

		size = a * Math.exp(b * loa);
		if (includeStocastic)
			size *= Uniform.random(0.2, 1.0); // The tank might be almost empty
												// or they might be full
		bunkerTonnage = size;
		return size;
	}

	// returns the amount of fueltype 1
	public double estimateFuel1Fraction() {
		fuelType1Fraction = 0.9;
		fuelType2Fraction = 0.1;
		return fuelType1Fraction;
	}

	// returns the amount of fueltype 2
	public double estimateFuel2Fraction() {
		estimateFuel1Fraction();
		return fuelType2Fraction;
	}

	public void estimateFueltypes() {
		if (shiptype == ShipTypeIwrap.CRUDE_OIL_TANKER) {
			fuelType1 = PolutionType.HFO;
			fuelType2 = PolutionType.MDO;
			fuelType1Fraction = 0.8;
			fuelType2Fraction = 0.2;
		}
		if (shiptype == ShipTypeIwrap.OIL_PRODUCTS_TANKER) {
			fuelType1 = PolutionType.HFO;
			fuelType2 = PolutionType.MDO;
			fuelType1Fraction = 0.8;
			fuelType2Fraction = 0.2;
		}
		if (shiptype == ShipTypeIwrap.CHEMICAL_TANKER) {
			fuelType1 = PolutionType.HFO;
			fuelType2 = PolutionType.MDO;
			fuelType1Fraction = 0.8;
			fuelType2Fraction = 0.2;
		}
		if (shiptype == ShipTypeIwrap.GAS_TANKER) {
			fuelType1 = PolutionType.HFO;
			fuelType2 = PolutionType.MDO;
			fuelType1Fraction = 0.8;
			fuelType2Fraction = 0.2;
		}
		if (shiptype == ShipTypeIwrap.CONTAINER_SHIP) {
			fuelType1 = PolutionType.HFO;
			fuelType2 = PolutionType.MDO;
			fuelType1Fraction = 0.8;
			fuelType2Fraction = 0.2;
		}
		if (shiptype == ShipTypeIwrap.GENERAL_CARGO_SHIP) {
			fuelType1 = PolutionType.HFO;
			fuelType2 = PolutionType.MDO;
			fuelType1Fraction = 0.8;
			fuelType2Fraction = 0.2;
		}
		if (shiptype == ShipTypeIwrap.BULK_CARRIER) {
			fuelType1 = PolutionType.HFO;
			fuelType2 = PolutionType.MDO;
			fuelType1Fraction = 0.8;
			fuelType2Fraction = 0.2;
		}
		if (shiptype == ShipTypeIwrap.RO_RO_CARGO_SHIP) {
			fuelType1 = PolutionType.HFO;
			fuelType2 = PolutionType.MDO;
			fuelType1Fraction = 0.8;
			fuelType2Fraction = 0.2;
		}
		if (shiptype == ShipTypeIwrap.PASSENGER_SHIP) {
			fuelType1 = PolutionType.MDO;
			fuelType2 = PolutionType.None;
			fuelType1Fraction = 1.0;
			fuelType2Fraction = 0.0;
		}
		if (shiptype == ShipTypeIwrap.FAST_FERRY) {
			fuelType1 = PolutionType.MGO;
			fuelType2 = PolutionType.None;
			fuelType1Fraction = 1.0;
			fuelType2Fraction = 0.0;
		}
		if (shiptype == ShipTypeIwrap.SUPPORT_SHIP) {
			fuelType1 = PolutionType.MDO;
			fuelType2 = PolutionType.None;
			fuelType1Fraction = 1.0;
			fuelType2Fraction = 0.0;
		}
		if (shiptype == ShipTypeIwrap.FISHING_SHIP) {
			fuelType1 = PolutionType.MDO;
			fuelType2 = PolutionType.None;
			fuelType1Fraction = 1.0;
			fuelType2Fraction = 0.0;
		}
		if (shiptype == ShipTypeIwrap.PLEASURE_BOAT) {
			fuelType1 = PolutionType.MDO;
			fuelType2 = PolutionType.None;
			fuelType1Fraction = 1.0;
			fuelType2Fraction = 0.0;
		}
		if (shiptype == ShipTypeIwrap.OTHER_SHIP) {
			fuelType1 = PolutionType.MDO;
			fuelType2 = PolutionType.None;
			fuelType1Fraction = 1.0;
			fuelType2Fraction = 0.0;
		}
		return;
	}

	public void estimateCargoType() {

		if (shiptype == ShipTypeIwrap.CRUDE_OIL_TANKER) {
			cargoType = PolutionType.Crude;
		}
		if (shiptype == ShipTypeIwrap.OIL_PRODUCTS_TANKER) {
			cargoType = PolutionType.MDO;
		}
		if (shiptype == ShipTypeIwrap.CHEMICAL_TANKER) {
			cargoType = PolutionType.Chemicals;
		}
		if (shiptype == ShipTypeIwrap.GAS_TANKER) {
			cargoType = PolutionType.Gas;
		}
		if (shiptype == ShipTypeIwrap.CONTAINER_SHIP) {
			cargoType = PolutionType.Containers;
		}
		if (shiptype == ShipTypeIwrap.GENERAL_CARGO_SHIP) {
			cargoType = PolutionType.Containers;
		}
		if (shiptype == ShipTypeIwrap.BULK_CARRIER) {
			cargoType = PolutionType.Ore;
		}
		if (shiptype == ShipTypeIwrap.RO_RO_CARGO_SHIP) {
			cargoType = PolutionType.Vehicles;
		}
		if (shiptype == ShipTypeIwrap.PASSENGER_SHIP) {
			cargoType = PolutionType.Vehicles;
		}
		if (shiptype == ShipTypeIwrap.FAST_FERRY) {
			cargoType = PolutionType.Vehicles;
		}
		if (shiptype == ShipTypeIwrap.SUPPORT_SHIP) {
			cargoType = PolutionType.None;
		}
		if (shiptype == ShipTypeIwrap.FISHING_SHIP) {
			cargoType = PolutionType.None;
		}
		if (shiptype == ShipTypeIwrap.PLEASURE_BOAT) {
			cargoType = PolutionType.None;
		}
		if (shiptype == ShipTypeIwrap.OTHER_SHIP) {
			cargoType = PolutionType.None;
		}
		return;
	}

	public int estimateNumberOfPersons() {
		if (shiptype == ShipTypeIwrap.PASSENGER_SHIP || shiptype == ShipTypeIwrap.FAST_FERRY) {
			if (loa <= 100)
				numberOfPersons = (int) Uniform.random(50, 250);
			if (loa > 100 && loa <= 200)
				numberOfPersons = (int) Uniform.random(250, 1000);
			if (loa > 200)
				numberOfPersons = (int) Uniform.random(1000, 4000);
		} else {
			numberOfPersons = (int) Uniform.random(10, 30);
		}

		if (shiptype == ShipTypeIwrap.PLEASURE_BOAT)
			numberOfPersons = (int) Uniform.random(1, 6);
		if (shiptype == ShipTypeIwrap.FISHING_SHIP)
			numberOfPersons = (int) Uniform.random(1, 6);

		return numberOfPersons;
	}

	public double calcDisplacement(boolean useCurrentDraught) {
		double disp = 0.0;
		if (useCurrentDraught && draught != null)
			disp = loa * breadth * draught * Cb * 1.025; // tons 1.025t/m3 In
															// Denmark it varies
															// from 1.01 to 1.03
		else
			disp = loa * breadth * designDraught * Cb * 1.025; // tons
		return disp;
	}

	// Not used. Need a bit more work
	public double estimateDeadweight0() {
		double disp = calcDisplacement(false); // tons
		deadweight = disp - lightweight;
		return deadweight;
	}

	// Not used. Need a bit more work
	public double estimateLightWeight() {
		double disp = calcDisplacement(false); // tons
		double machineryWeight = 0.8 * Math.pow(designSpeed, 3) * Math.pow(disp, 0.67) * (45.8 - loa / 61 - 12 * Cb)
				/ (15000 - 181 * Math.sqrt(loa));
		double steelWeight = (loa * loa * (0.21 + 10.2 / (loa + 1000)) + 0.357 * loa) * (1 + 0.5 * (Cb - 0.5));
		double outfitWeight = 0.064 * loa * loa - 0.00013 * loa * loa * loa;
		lightweight = steelWeight + machineryWeight + outfitWeight;
		return lightweight;
	}
	
	double estimateNewBuildingPrice(boolean includeStocastic) {
        double age0=age;
        age=0.0;
        double newBuildingPrice=estimateShipValue(includeStocastic);
        age=age0;
        return newBuildingPrice;
}

	public double estimateDepth() {
        double a=0.09,b=0.8;
        if (shiptype==ShipTypeIwrap.CRUDE_OIL_TANKER) {a=0.0935;b=-1.491;}
        if (shiptype==ShipTypeIwrap.OIL_PRODUCTS_TANKER) {a=0.0958;b=-1.3162;}
        if (shiptype==ShipTypeIwrap.CHEMICAL_TANKER) {a=0.0775;b=-0.4337;}
        if (shiptype==ShipTypeIwrap.GAS_TANKER) {a=0.0949;b=-1.0147;}
        if (shiptype==ShipTypeIwrap.CONTAINER_SHIP) {a=0.0741;b=1.0261;}
        if (shiptype==ShipTypeIwrap.GENERAL_CARGO_SHIP) {a=0.0763;b=0.4633;}
        if (shiptype==ShipTypeIwrap.BULK_CARRIER) {a=0.085;b=-0.0111;}
        if (shiptype==ShipTypeIwrap.RO_RO_CARGO_SHIP) {a=0.1358;b=0.-4.2569;}
        if (shiptype==ShipTypeIwrap.PASSENGER_SHIP) {a=0.0628;b=0.9345;}
        if (shiptype==ShipTypeIwrap.FAST_FERRY) {a=0.0745;b=0.4937;} 
        if (shiptype==ShipTypeIwrap.SUPPORT_SHIP) {a=0.0645;b=1.8854;}
        if (shiptype==ShipTypeIwrap.FISHING_SHIP) {a=0.0712;b=1.5763;}
        if (shiptype==ShipTypeIwrap.PLEASURE_BOAT) {a=0.0569;b=1.913;}
        if (shiptype==ShipTypeIwrap.OTHER_SHIP) {a=0.0955;b=0.8276;}
        
        depth=a*loa+b;
        if (depth<2.0) depth=2.0;
        return depth;
}

}
