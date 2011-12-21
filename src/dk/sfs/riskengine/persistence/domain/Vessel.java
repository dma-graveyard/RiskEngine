package dk.sfs.riskengine.persistence.domain;

import org.apache.ibatis.session.SqlSession;

import dk.frv.ais.message.ShipTypeCargo.ShipType;
import dk.sfs.riskengine.persistence.mapper.DBSessionFactory;
import dk.sfs.riskengine.persistence.mapper.VesselMapper;

public class Vessel {
	private enum SHIP_TYPE_IWRAP{
		PLEASURE_BOAT,
		OIL_PRODUCTS_TANKER,
		OTHER_SHIP,
		SUPPORT_SHIP,
		PASSENGER_SHIP,
		GENERAL_CARGO_SHIP,
		FISHING_SHIP,
		CONTAINER_SHIP,
		GAS_TANKER,
		BULK_CARRIER,
		CRUDE_OIL_TANKER,
		CHEMICAL_TANKER,
		RO_RO_CARGO_SHIP
	}
	
	private Integer id;
	private Integer imo;
	private Integer mmsi;
	private String nameOfShip;
	private String shipTypeLr;
	private String shipTypeIwrap;
	private Double length;
	private String lpp;
	private String bmld;
	private Double breadth;
	private Double depth;
	private Double draught;
	private Double displacement;
	private Double deadweight;
	private Integer yearOfBuild;
	private String callsign;
	private String flag;
	private String hullType;
	private Integer gt;
	private Double speed;
	private Integer totalKwMainEng;
	private Double fuelCapacity1;
	private Double fuelCapacity2;
	private Integer passengers;
	private Integer tanks;
	private Double gasCapacity;
	private Integer teu;
	private String fuelType1;
	private String fuelType2;
	private Integer holds;
	private String class_;
	private Integer enginesRpm;
	private String engineType;
	private String iceCapable;
	private Double keelToMastHeight;
	private Double newbuildingPrice;
	private Integer fuelConsumptionMainEngines;
	private Integer fuelConsumptionTotal;
	private Integer bollardPull;

	
	public static Vessel getByMmsi(Long mmsi) {
		SqlSession sess = DBSessionFactory.getSession();
		try {
			return sess.getMapper(VesselMapper.class).selectByMmsi(mmsi);
		} finally {
			sess.close();
		}
	}
	
	public static Vessel getByImo(Long imo) {
		SqlSession sess = DBSessionFactory.getSession();
		try {
			return sess.getMapper(VesselMapper.class).selectByImo(imo);
		} finally {
			sess.close();
		}
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getImo() {
		return imo;
	}

	public void setImo(Integer imo) {
		this.imo = imo;
	}

	public Integer getMmsi() {
		return mmsi;
	}

	public void setMmsi(Integer mmsi) {
		this.mmsi = mmsi;
	}

	public String getNameOfShip() {
		return nameOfShip;
	}

	public void setNameOfShip(String nameOfShip) {
		this.nameOfShip = nameOfShip;
	}

	public String getShipTypeLr() {
		return shipTypeLr;
	}

	public void setShipTypeLr(String shipTypeLr) {
		this.shipTypeLr = shipTypeLr;
	}

	public String getShipTypeIwrap() {
		return shipTypeIwrap;
	}

	public void setShipTypeIwrap(String shipTypeIwrap) {
		this.shipTypeIwrap = shipTypeIwrap;
	}
	
	

	public Double getLength() {
		return length;
	}

	public void setLength(Double length) {
		this.length = length;
	}

	public String getLpp() {
		return lpp;
	}

	public void setLpp(String lpp) {
		this.lpp = lpp;
	}

	public String getBmld() {
		return bmld;
	}

	public void setBmld(String bmld) {
		this.bmld = bmld;
	}

	public Double getBreadth() {
		return breadth;
	}

	public void setBreadth(Double breadth) {
		this.breadth = breadth;
	}

	public Double getDepth() {
		return depth;
	}

	public void setDepth(Double depth) {
		this.depth = depth;
	}

	public Double getDraught() {
		return draught;
	}

	public void setDraught(Double draught) {
		this.draught = draught;
	}

	public Double getDisplacement() {
		return displacement;
	}

	public void setDisplacement(Double displacement) {
		this.displacement = displacement;
	}

	public Double getDeadweight() {
		return deadweight;
	}

	public void setDeadweight(Double deadweight) {
		this.deadweight = deadweight;
	}

	public Integer getYearOfBuild() {
		return yearOfBuild;
	}

	public void setYearOfBuild(Integer yearOfBuild) {
		this.yearOfBuild = yearOfBuild;
	}

	public String getCallsign() {
		return callsign;
	}

	public void setCallsign(String callsign) {
		this.callsign = callsign;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public String getHullType() {
		return hullType;
	}

	public void setHullType(String hullType) {
		this.hullType = hullType;
	}

	public Integer getGt() {
		return gt;
	}

	public void setGt(Integer gt) {
		this.gt = gt;
	}

	public Double getSpeed() {
		return speed;
	}

	public void setSpeed(Double speed) {
		this.speed = speed;
	}

	public Integer getTotalKwMainEng() {
		return totalKwMainEng;
	}

	public void setTotalKwMainEng(Integer totalKwMainEng) {
		this.totalKwMainEng = totalKwMainEng;
	}

	public Double getFuelCapacity1() {
		return fuelCapacity1;
	}

	public void setFuelCapacity1(Double fuelCapacity1) {
		this.fuelCapacity1 = fuelCapacity1;
	}

	public Double getFuelCapacity2() {
		return fuelCapacity2;
	}

	public void setFuelCapacity2(Double fuelCapacity2) {
		this.fuelCapacity2 = fuelCapacity2;
	}

	public Integer getPassengers() {
		return passengers;
	}

	public void setPassengers(Integer passengers) {
		this.passengers = passengers;
	}

	public Integer getTanks() {
		return tanks;
	}

	public void setTanks(Integer tanks) {
		this.tanks = tanks;
	}

	public Double getGasCapacity() {
		return gasCapacity;
	}

	public void setGasCapacity(Double gasCapacity) {
		this.gasCapacity = gasCapacity;
	}

	public Integer getTeu() {
		return teu;
	}

	public void setTeu(Integer teu) {
		this.teu = teu;
	}

	public String getFuelType1() {
		return fuelType1;
	}

	public void setFuelType1(String fuelType1) {
		this.fuelType1 = fuelType1;
	}

	public String getFuelType2() {
		return fuelType2;
	}

	public void setFuelType2(String fuelType2) {
		this.fuelType2 = fuelType2;
	}

	public Integer getHolds() {
		return holds;
	}

	public void setHolds(Integer holds) {
		this.holds = holds;
	}

	public String getClass_() {
		return class_;
	}

	public void setClass_(String class_) {
		this.class_ = class_;
	}

	public Integer getEnginesRpm() {
		return enginesRpm;
	}

	public void setEnginesRpm(Integer enginesRpm) {
		this.enginesRpm = enginesRpm;
	}

	public String getEngineType() {
		return engineType;
	}

	public void setEngineType(String engineType) {
		this.engineType = engineType;
	}

	public String getIceCapable() {
		return iceCapable;
	}

	public void setIceCapable(String iceCapable) {
		this.iceCapable = iceCapable;
	}

	public Double getKeelToMastHeight() {
		return keelToMastHeight;
	}

	public void setKeelToMastHeight(Double keelToMastHeight) {
		this.keelToMastHeight = keelToMastHeight;
	}

	public Double getNewbuildingPrice() {
		return newbuildingPrice;
	}

	public void setNewbuildingPrice(Double newbuildingPrice) {
		this.newbuildingPrice = newbuildingPrice;
	}

	public Integer getFuelConsumptionMainEngines() {
		return fuelConsumptionMainEngines;
	}

	public void setFuelConsumptionMainEngines(Integer fuelConsumptionMainEngines) {
		this.fuelConsumptionMainEngines = fuelConsumptionMainEngines;
	}

	public Integer getFuelConsumptionTotal() {
		return fuelConsumptionTotal;
	}

	public void setFuelConsumptionTotal(Integer fuelConsumptionTotal) {
		this.fuelConsumptionTotal = fuelConsumptionTotal;
	}

	public Integer getBollardPull() {
		return bollardPull;
	}

	public void setBollardPull(Integer bollardPull) {
		this.bollardPull = bollardPull;
	}

}
